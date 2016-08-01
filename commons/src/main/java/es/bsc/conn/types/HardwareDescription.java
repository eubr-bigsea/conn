package es.bsc.conn.types;
import java.util.LinkedList;
import java.util.List;

public class HardwareDescription {
    // Types
    public static final int UNASSIGNED_INT 		= -1;
    public static final String UNASSIGNED_STR 	= "[unassigned]";
    public static final float UNASSIGNED_FLOAT 	= (float) -1.0;
    public static final int ZERO_INT			= 0;
    public static final int ONE_INT				= 1;

    // Processor
    protected List<Processor> processors = new LinkedList<Processor>();
    protected int totalComputingUnits = ZERO_INT;

    // Memory
    protected float memorySize = UNASSIGNED_FLOAT;
    protected String memoryType = UNASSIGNED_STR;
    // Storage
    protected float storageSize = UNASSIGNED_FLOAT;
    protected String storageType = UNASSIGNED_STR;

    // Price
    protected int priceTimeUnit = UNASSIGNED_INT;
    protected float pricePerUnit = UNASSIGNED_FLOAT;

    public HardwareDescription(){

    }


}
