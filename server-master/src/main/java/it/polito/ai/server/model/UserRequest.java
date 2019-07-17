package it.polito.ai.server.model;

import org.springframework.data.mongodb.core.geo.GeoJsonPolygon;

import java.util.LinkedList;

public class UserRequest {

    private long start;
    private long end;
    private GeoJsonPolygon polygon;
    private LinkedList<String> usernames;

    public UserRequest() {}

    public UserRequest(long start, long end, GeoJsonPolygon polygon) {
        this.start = start;
        this.end = end;
        this.polygon = polygon;
    }

    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public long getEnd() {
        return end;
    }

    public void setEnd(long end) {
        this.end = end;
    }

    public GeoJsonPolygon getPolygon() {
        return polygon;
    }

    public void setPolygon(GeoJsonPolygon polygon) {
        this.polygon = polygon;
    }

    public LinkedList<String> getUsernames() {
        return usernames;
    }

    public void setUsername(LinkedList<String> usernames) {
        this.usernames = usernames;
    }
}
