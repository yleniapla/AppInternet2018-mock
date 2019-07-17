package it.polito.ai.server.model;

import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;

import java.util.Objects;

public class Position {

    private long time;

    @GeoSpatialIndexed(type = GeoSpatialIndexType.GEO_2DSPHERE)
    private GeoJsonPoint point;

    public Position(long time, GeoJsonPoint point) {
        this.time = time;
        this.point = point;
    }

    public Position() {}

    public long getTime() {
        return time;
    }
    public GeoJsonPoint getPoint() {
        return point;
    }

    public void setTime(long time) {
        this.time = time;
    }
    public void setPoint(GeoJsonPoint point) {
        this.point = point;
    }

    @Override
    public String toString() {
        return "Position{" +
                "time=" + time +
                ", point=" + this.point +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Position position = (Position) o;
        return getTime() == position.getTime() &&
                Objects.equals(getPoint(), position.getPoint());
    }

    @Override
    public int hashCode() {

        return Objects.hash(getTime(), getPoint());
    }
}