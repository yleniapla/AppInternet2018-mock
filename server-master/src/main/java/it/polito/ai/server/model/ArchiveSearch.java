package it.polito.ai.server.model;

import org.bson.types.ObjectId;

public class ArchiveSearch {

    private String id;
    private String user;
    private int bought;
    private long start;
    private long end;

    public ArchiveSearch(ObjectId id, String user, int bought, long start, long end) {
        this.id = id.toHexString();
        this.user = user;
        this.bought = bought;
        this.start = start;
        this.end = end;
    }

    public ArchiveSearch(Archive a) {
        this.id = a.getId().toHexString();
        this.user = a.getUser();
        this.bought = a.getBought();
        this.start = a.getStart();
        this.end = a.getEnd();
    }

    public ArchiveSearch(Archive a, int bought) {
        this.id = a.getId().toHexString();
        this.user = a.getUser();
        this.bought = bought;
        this.start = a.getStart();
        this.end = a.getEnd();
    }


    public ArchiveSearch() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public int getBought() {
        return bought;
    }

    public void setBought(int bought) {
        this.bought = bought;
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
}
