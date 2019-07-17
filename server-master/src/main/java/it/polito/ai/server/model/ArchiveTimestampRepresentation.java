package it.polito.ai.server.model;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.util.LinkedList;

public class ArchiveTimestampRepresentation {

    private String username;
    private Long timestamp;

    public ArchiveTimestampRepresentation(){}
    public ArchiveTimestampRepresentation(String username, Long timestamp){
        this.timestamp = timestamp;
        this.username = username;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamps) {
        this.timestamp = timestamp;
    }


    public String getUsername() { return this.username; }

    public void setUsername(String username) {
        this.username = username;
    }


}
