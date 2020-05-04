package vn.vntravel.replication;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PartionPosition {
    static final Logger LOGGER = LoggerFactory.getLogger(PartionPosition.class);

    private static final String FILE_COLUMN = "File";
    private static final String POSITION_COLUMN = "Position";
    private static final String GTID_COLUMN = "Executed_Gtid_Set";

    private final String gtid;
    private final long offset;
    private final String partionIdStr;

    public PartionPosition(String gtid, long l, String partionIdStr) {
        this.gtid = gtid;
        this.offset = l;
        this.partionIdStr = partionIdStr;
    }

    public long getOffset() {
        return offset;
    }

    public String getPartionId() {
        return partionIdStr;
    }

    public String getGtid() {
        return gtid;
    }
}
