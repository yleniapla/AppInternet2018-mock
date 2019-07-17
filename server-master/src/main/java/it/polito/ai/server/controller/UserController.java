package it.polito.ai.server.controller;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.polito.ai.server.exceptions.UnauthorizedException;
import it.polito.ai.server.model.*;
import it.polito.ai.server.repositories.ArchiveRepository;
import it.polito.ai.server.repositories.UserRepository;
import it.polito.ai.server.service.ArchiveService;
import javassist.NotFoundException;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/users")
public class UserController {

    @Autowired
    ArchiveService archService;

    @Autowired
    UserRepository userRepo;

    @Autowired
    ArchiveRepository archRepo;

    @Autowired
    private ObjectMapper mapper;


    // list of archives uploaded by the logged user
    @RequestMapping(value = "/archives", method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public LinkedList<ArchiveSearch> getArchives() {
        String username=(String)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User u = userRepo.findByUsername(username);

        LinkedList<ArchiveSearch> returnList = new LinkedList<>();
        for(Archive a : u.getArchives())
            returnList.add(new ArchiveSearch(a));

        return returnList;
    }

    // list of users
    @RequestMapping(method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public List<String> getUsers() {
        List<String> users = userRepo.findAll()
                .stream()
                .map( user -> user.getUsername() )
                .collect(Collectors.toList());
        return users;
    }

    // list of archives bought by the logged user
    @RequestMapping(value = "/bought", method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public LinkedList<ArchiveSearch> getBoughtArchives() {
        String username=(String)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User u = userRepo.findByUsername(username);

        LinkedList<ArchiveSearch> returnList = new LinkedList<>();
        for(Archive a : u.getBoughtArchives())
            returnList.add(new ArchiveSearch(a));

        return returnList;
    }

    // archive upload
    @RequestMapping(value ="/archives", method = RequestMethod.POST, consumes = "multipart/form-data")
    @ResponseStatus(value = HttpStatus.OK)
    public @ResponseBody
    void postUsers(@RequestParam("file") MultipartFile file) throws Exception {
        String username=(String)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        LinkedList<Position> rcvdPos = mapper.readValue(file.getInputStream(), new TypeReference<LinkedList<Position>>() {});
        archService.addNewArchive(username, rcvdPos);
    }


    // buy of a list of archives
    @RequestMapping(value = "/archives/buy", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public void buyArchives(@RequestBody ArchiveRequest req) throws RuntimeException {
        String username=(String)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        archService.buyArchives(username, req.getIds());
    }

    // archives search
    @RequestMapping(value = "/archives/search", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public ArchiveRepresentation searchArchives(@RequestBody UserRequest req) throws RuntimeException {
        String username=(String)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        List<Archive> foundArchives = archService.searchArchives(username, req);
        LinkedList<ArchivePositionRepresentation> listPoints = archService.getPositionRepresentation(foundArchives);
        LinkedList<ArchiveTimestampRepresentation> listTimes = archService.getTimestampRepresentation(foundArchives);
        return new ArchiveRepresentation(listPoints, listTimes);
    }

    // list of archives requested for buying
    @RequestMapping(value = "/archives/searchbuy", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public LinkedList<ArchiveSearch> searchBuyArchives(@RequestBody UserRequest req) throws RuntimeException {
        String username=(String)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        List<Archive> foundArchives = archService.searchArchives(username, req);
        LinkedList<ArchiveSearch> returnList = new LinkedList<>();

        User u = userRepo.findByUsername(username);
        List<String> boughtArchives = u.getBoughtArchives()
                .stream()
                .map( arch -> arch.getId().toHexString() )
                .collect(Collectors.toList());

        int bought;
        for(Archive a : foundArchives) {
            // Set bought to 1 if is already bought
            bought = (boughtArchives.contains(a.getId().toHexString())) ? 1 : 0;
            returnList.add(new ArchiveSearch(a, bought));
        }

        return returnList;
    }

    // archive download
    @RequestMapping(value = "/archives/{id}", method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public LinkedList<Position> getArchive(@PathVariable("id") String id) throws RuntimeException, NotFoundException {
        String username=(String)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Archive a = archRepo.findById(new ObjectId(id)).orElseThrow( () -> new NotFoundException(id));
        User u = userRepo.findByUsername(username);
        List<String> boughtArchives = u.getBoughtArchives()
                .stream()
                .map( arch -> arch.getId().toHexString() )
                .collect(Collectors.toList());

        if(username.equals(a.getUser()) || boughtArchives.contains(id))
            return a.getArchive();
        else
            throw new UnauthorizedException();
    }

    // archive deletion
    @RequestMapping(value = "/archives/{id}", method = RequestMethod.DELETE)
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public void deleteArchives(@PathVariable("id") String id) throws RuntimeException {
        String username=(String)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        archService.deleteArchive(username, id);
    }

}
