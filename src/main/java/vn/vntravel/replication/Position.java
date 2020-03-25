package vn.vntravel.replication;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;

public class Position implements Serializable {
    // LastHeartbeat is the most recent heartbeat seen prior to this position.
    // For a HeartbeatRow, it is the exact (new) heartbeat value for this position.
    private final long lastHeartbeatRead;
    private final PartionPosition partionPosition;

    public Position(PartionPosition partionPosition, long lastHeartbeatRead) {
        this.partionPosition = partionPosition;
        this.lastHeartbeatRead = lastHeartbeatRead;
    }

    public static Position valueOf(PartionPosition binlogPosition, Long lastHeartbeatRead) {
        return new Position(binlogPosition, lastHeartbeatRead);
    }

    public Position withHeartbeat(long lastHeartbeatRead) {
        return new Position(getPartionPosition(), lastHeartbeatRead);
    }

    public static Position capture(Connection c, boolean gtidMode) throws SQLException {
//        return new Position(PartionPosition.capture(c, gtidMode), 0L);
        return null;
    }

    public long getLastHeartbeatRead() {
        return lastHeartbeatRead;
    }

    public PartionPosition getPartionPosition() {
        return partionPosition;
    }

    @Override
    public String toString() {
        return "Position[" + partionPosition + ", lastHeartbeat=" + lastHeartbeatRead + "]";
    }

    public String toCommandline() {
        String gtid = partionPosition.getGtidSetStr();
        if ( gtid != null )
            return gtid;
        else
            return partionPosition.getPartionId() + ":" + partionPosition.getOffset();
    }

    @Override
    public boolean equals(Object o) {
        if ( !(o instanceof Position) ) {
            return false;
        }
        Position other = (Position) o;

//        return lastHeartbeatRead == other.lastHeartbeatRead
//                && binlogPosition.equals(other.binlogPosition);
        return false;
    }

    @Override
    public int hashCode() {
        return partionPosition.hashCode();
    }

    public boolean newerThan(Position other) {
        if ( other == null )
            return true;
//        return this.getBinlogPosition().newerThan(other.getBinlogPosition());

        return false;
    }
}