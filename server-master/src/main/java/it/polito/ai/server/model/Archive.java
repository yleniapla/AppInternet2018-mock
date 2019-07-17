package it.polito.ai.server.model;

import org.bson.types.ObjectId;
import org.hibernate.annotations.Index;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;
import java.util.LinkedList;

@Document
public class Archive {

    @Id
    private ObjectId id;
    private String user;
    private boolean removed = false;
    private int bought;
    @Indexed
    private long start;
    @Indexed
    private long end;
    private LinkedList<Position> archive;

    public Archive() {}

    public Archive(String user, long start, long end, LinkedList<Position> archive) {
        this.user = user;
        this.bought = 0;
        this.start = start;
        this.end = end;
        this.archive = archive;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public boolean isRemoved() {
        return removed;
    }

    public void setRemoved(boolean removed) {
        this.removed = removed;
    }

    public int getBought() {
        return bought;
    }

    public void setBought(int bought) {
        this.bought = bought;
    }

    public void increaseBought() {
        this.bought++;
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

    public LinkedList<Position> getArchive() {
        return archive;
    }

    public void setArchive(LinkedList<Position> archive) {
        this.archive = archive;
    }

    @Override
    public String toString() {
        return "Archive{" +
                "id=" + id +
                ", user=" + user +
                ", bought=" + bought +
                ", start=" + start +
                ", end=" + end +
                ", archive=" + archive +
                '}';
    }
}
