package it.polito.ai.server.model;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.util.LinkedList;

public class ArchivePositionRepresentation {

    private String username;
    private GeoJsonPoint point;



    public ArchivePositionRepresentation(){}
    public ArchivePositionRepresentation(String username, GeoJsonPoint point){
        this.point = point;
        this.username = username;
    }

    public GeoJsonPoint getPoint() {
        return point;
    }

    public void setPoint(GeoJsonPoint points) {
        this.point = points;
    }


    public String getUsername() { return this.username; }

    public void setUsername(String username) {
        this.username = username;
    }

}
