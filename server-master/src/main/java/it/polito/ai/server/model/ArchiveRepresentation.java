package it.polito.ai.server.model;

import org.springframework.data.mongodb.core.geo.GeoJsonPoint;

import java.util.LinkedList;

public class ArchiveRepresentation {

    LinkedList<ArchivePositionRepresentation> positions;
    LinkedList<ArchiveTimestampRepresentation> timestamps;

    public ArchiveRepresentation(){}
    public ArchiveRepresentation(LinkedList<ArchivePositionRepresentation> pos,
                                              LinkedList<ArchiveTimestampRepresentation> times){
        this.positions = pos;
        this.timestamps = times;
    }

    public LinkedList<ArchivePositionRepresentation> getPositions() { return positions; }
    public void setPositions(LinkedList<ArchivePositionRepresentation> positions) {
        this.positions = positions;
    }

    public LinkedList<ArchiveTimestampRepresentation> getTimestamps() {
        return timestamps;
    }

    public void setTimestamps(LinkedList<ArchiveTimestampRepresentation> timestamps) {
        this.timestamps = timestamps;
    }


}
