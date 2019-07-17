package it.polito.ai.server.service;

import it.polito.ai.server.exceptions.ItemNotFoundException;
import it.polito.ai.server.exceptions.UnauthorizedException;
import it.polito.ai.server.model.*;
import it.polito.ai.server.repositories.ArchiveRepository;
import it.polito.ai.server.repositories.UserRepository;
import javassist.NotFoundException;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
public class ArchiveService {

    private static final int EARTH_RADIUS = 6371;
    private static final int MAX_SPEED = 100;

    @Autowired
    ArchiveRepository archRepo;

    @Autowired
    UserRepository userRepo;

    @Autowired
    MongoTemplate mongoTemplate;

    public void addNewArchive(String username, LinkedList<Position> listPos) throws Exception {

        checkSpeed(listPos);
        long start = listPos.getFirst().getTime();
        long end = listPos.getLast().getTime();

        User user = userRepo.findByUsername(username);

        Archive a = new Archive(username, start, end, listPos);
        user.appendArchive(a);
        archRepo.insert(a);
        userRepo.save(user);
    }

    public Archive getArchive(String id) throws NotFoundException {
        Optional<Archive> a = archRepo.findById(new ObjectId(id));

        return a.orElseThrow( () -> new NotFoundException(id));
    }

    public LinkedList<ArchiveTimestampRepresentation> getTimestampRepresentation(List<Archive> archiveList){

        LinkedList<ArchiveTimestampRepresentation> timestampRepr = new LinkedList<>();

        for (Archive a : archiveList){

            String user = a.getUser();

            List approx = a.getArchive()
                    .stream()
                    .map( archive -> {

                        Long timestamp = (archive.getTime() / 60)*60; // approx to minute
                        return new ArchiveTimestampRepresentation(user,timestamp);

                    })
                    .filter(distinctByKey(p->p.getTimestamp())) // remove duplicates
                    .sorted( (a1,a2) -> (int)(a1.getTimestamp() - a2.getTimestamp()) )
                    .collect(Collectors.toCollection(LinkedList<ArchiveTimestampRepresentation>::new));
            timestampRepr.addAll(approx);

        }


        return timestampRepr;

    }

    public LinkedList<ArchivePositionRepresentation> getPositionRepresentation(List<Archive> archiveList){

        LinkedList<ArchivePositionRepresentation> positionRepr = new LinkedList<>();

        for (Archive a : archiveList){

            String user = a.getUser();

            List approx = a.getArchive()
                    .stream()
                    .map( archive ->  {

                        List<Double> coords = archive.getPoint().getCoordinates();
                        // Approx to 2nd decimal
                        double x = Math.floor(coords.get(0) * 100) / 100;
                        double y = Math.floor(coords.get(1) * 100) / 100;

                        GeoJsonPoint point = new GeoJsonPoint(x,y);
                        return new ArchivePositionRepresentation(user, point);

                    })
                    .filter(distinctByKey(p->p.getPoint()))
                    .sorted( (a1,a2) -> (int)(a1.getPoint().getX() - a2.getPoint().getX()) )
                    .collect(Collectors.toCollection(LinkedList<ArchivePositionRepresentation>::new));
            positionRepr.addAll(approx);

        }

        return positionRepr;

    }


    public void deleteArchive(String username, String id) throws RuntimeException {
        ObjectId objId = new ObjectId(id);
        Archive a = archRepo.findById(objId).orElseThrow( () -> new ItemNotFoundException());
        if(a.getUser().equals(username)) {
            // Set archive as removed
            a.setRemoved(true);
            archRepo.save(a);
            // Remove reference inside User
            User u = userRepo.findByUsername(username);
            u.removeArchive(id);
            userRepo.save(u);
        }
        else
            throw new UnauthorizedException();

    }

    public void buyArchives(String username, ArrayList<String> ids) throws RuntimeException {

        ArrayList<Archive> archives = new ArrayList<>();
        ArrayList<User> users = new ArrayList<>();

        for(String i : ids) {
            ObjectId id = new ObjectId(i);
            Archive a = archRepo.findById(id).orElseThrow( () -> new ItemNotFoundException());
            if(!a.getUser().equals(username) && !a.isRemoved()) {

                // Increase bought counter
                a.increaseBought();

                // Insert reference into User
                User u = userRepo.findByUsername(username);
                u.appendBoughtArchive(a);

                archives.add(a);
                users.add(u);
            }
            else
                throw new UnauthorizedException();

            for ( Archive a1 : archives ){
                archRepo.save(a1);
            }
            for ( User u1 : users ){
                userRepo.save(u1);
            }
        }
    }

    // Query for archives to buy
    public List<Archive> searchArchives(String username, UserRequest req) {

        // Check for archives of specified usernames
        MatchOperation matchStage1 = Aggregation.match(
                new Criteria("user").in(req.getUsernames())
        );
        // Remove archives from same user
        MatchOperation matchStage2 = Aggregation.match(
                new Criteria("user").ne(username)
        );
        // Check for start and end of archives
        MatchOperation matchStage3 = Aggregation.match(
                new Criteria("start").lte(req.getEnd())
        );
        MatchOperation matchStage4 = Aggregation.match(
                new Criteria("end").gte(req.getStart())
        );
        MatchOperation matchStage5 = Aggregation.match(
                new Criteria("removed").ne(true)
        );
        // Search for requirements on points
        UnwindOperation unwindStage = Aggregation.unwind("archive");

        MatchOperation matchStage6 = Aggregation.match(
                new Criteria("archive.point").within(req.getPolygon())
        );
        MatchOperation matchStage7 = Aggregation.match(
                new Criteria("archive.time").gte(req.getStart()).lte(req.getEnd())
        );

        GroupOperation groupStage = Aggregation.group("id")
                .first("user").as("user")
                .first("removed").as("removed")
                .first("bought").as("bought")
                .first("start").as("start")
                .first("end").as("end")
                .push("archive").as("archive");


        Aggregation aggregation = Aggregation.newAggregation(matchStage1, matchStage2, matchStage3, matchStage4, matchStage5, unwindStage, matchStage6, matchStage7, groupStage);

        AggregationResults<Archive> output
                = mongoTemplate.aggregate(aggregation, Archive.class, Archive.class);

        return output.getMappedResults();
    }


    // Check for valid positions
    private boolean checkSpeed(LinkedList<Position> positions) throws Exception {

        System.out.println("Size: " + positions.size());
        if (positions.size() < 2)
            return true;

        for (int i = 0; i < positions.size() - 1; i++) {

            long time = (positions.get(i+1).getTime() - positions.get(i).getTime())/1000;
            double distance = distance(
                    positions.get(i).getPoint().getY(),
                    positions.get(i).getPoint().getX(),
                    positions.get(i+1).getPoint().getY(),
                    positions.get(i+1).getPoint().getX()
            );

            double speed = distance/time;

            System.out.println("Time (s): " + time);
            System.out.println("Distance (m): " + distance);
            System.out.println("Speed (m/s): " + speed);

            if (time <= 0 || speed > MAX_SPEED)
                throw new Exception("One or more positions are not acceptable (times are decreasing or speed is above 100 m/s)");
        }

        return true;

    }

    // Distance between two points
    private double distance(double lat1, double long1, double lat2, double long2){

        double dLat  = Math.toRadians((lat2 - lat1));
        double dLong = Math.toRadians((long2 - long1));

        lat1 = Math.toRadians(lat1);
        lat2   = Math.toRadians(lat2);

        double a = haversin(dLat) + Math.cos(lat1) * Math.cos(lat2) * haversin(dLong);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS * 1000 * c;
    }

    private double haversin(double val){
        return Math.pow(Math.sin(val / 2), 2);
    }

    public static <T> Predicate<T> distinctByKey(Function<? super T, Object> keyExtractor)
    {
        Map<Object, Boolean> map = new ConcurrentHashMap<>();
        return t -> map.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }

}