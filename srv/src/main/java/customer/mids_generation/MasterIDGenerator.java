package customer.mids_generation;

import java.time.Instant;
import java.time.OffsetDateTime;
import org.springframework.stereotype.Component;

@Component
public class MasterIDGenerator {
    private static final short TOTAL_BITS = 64;
    private static final short EPOCH_BITS = 42;
    private static final int NODE_ID_BITS = 11;
    private static final int SEQUENCE_BITS = 12;
    public static final int maxSequence = (int) (Math.pow(2, SEQUENCE_BITS) - 1); // 0 - 4095
    private static final int minNodeId = 192;
    private static final int maxNodeId = 255;

    // Custom Epoch (2020-01-01 12:00:00)
    private static final CharSequence EPOCH = "2020-01-01T12:00:00.000+00:00";
    private static final long CUSTOM_EPOCH = OffsetDateTime.parse(EPOCH).toInstant().toEpochMilli();
    // Create a Logger instance
    private static final int currentNodeId = minNodeId;
    // Declare a static variable for the single instance
    private static MasterIDGenerator instance = null;
    private int nodeId;
    private volatile long lastTimestamp = -1L;
    private volatile int sequence = 0;

    // Create SequenceGenerator with a nodeId
    private MasterIDGenerator() {
        this.nodeId = currentNodeId;
        checkNodeId(nodeId);
    }

    private static void checkNodeId(int nodeId) {
        if (nodeId < minNodeId || nodeId > maxNodeId) {
            throw new IllegalArgumentException(String.format("NodeId must be between %d and %d", minNodeId, maxNodeId));
        }
    }

    public static MasterIDGenerator getInstance() {
        if (instance == null) {
            instance = new MasterIDGenerator();
        }
        return instance;
    }

    private static int nodeIdIncrement(int currentNodeId) {
        currentNodeId++;
        if (currentNodeId > maxNodeId) {
            currentNodeId = minNodeId;
        }
        checkNodeId(currentNodeId);
        return currentNodeId;
    }

    // Get current timestamp in milliseconds, adjust for the custom epoch.
    private static long timestamp() {
        return Instant.now().toEpochMilli() - CUSTOM_EPOCH;
    }

    private void setNodeId(int nodeId) {
        this.nodeId = nodeId;
    }

    public synchronized long generateNextId() {
        long currentTimestamp = timestamp();

        if (currentTimestamp < lastTimestamp) {
            throw new IllegalStateException("Invalid System Clock!");
        }

        if (currentTimestamp == lastTimestamp) {
            sequence = (sequence + 1) & maxSequence;
            if (sequence == 0) {
                // Sequence Exhausted, wait till next millisecond.
                currentTimestamp = waitNextMillis(currentTimestamp);
            }
        } else {
            // reset sequence to start with zero for the next millisecond
            sequence = 0;
        }
        System.out.println("\nLast TimeStamp: " + lastTimestamp);
        lastTimestamp = currentTimestamp;

        // Build ID
        return buildID(currentTimestamp, nodeId, sequence);
    }

    private long buildID(long timestamp, int nodeId, int sequence) {
        long id = timestamp << (TOTAL_BITS - EPOCH_BITS);
        id |= ((long) nodeId << (TOTAL_BITS - EPOCH_BITS - NODE_ID_BITS));
        id |= sequence;
        // logging
        System.out.println("Master Id: [ " + id + " ]");
        System.out.println("Timestamp: " + timestamp + " | NodeId: " + nodeId + " | SequenceID: " + sequence);
        // Incrementing the node id
        setNodeId(nodeIdIncrement(nodeId));
        return id;
    }

    // Block and wait till next millisecond
    private long waitNextMillis(long currentTimestamp) {
        while (currentTimestamp == lastTimestamp) {
            currentTimestamp = timestamp();
        }
        return currentTimestamp;
    }
}
