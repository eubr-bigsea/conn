package es.bsc.conn.types;
import java.util.LinkedList;
import java.util.List;

public class HardwareDescription {


    // Processor
    protected List<Processor> processors = new LinkedList<Processor>();
    protected int totalComputingUnits = BasicTypes.ZERO_INT;

    // Memory
    protected float memorySize = BasicTypes.UNASSIGNED_FLOAT;
    protected String memoryType = BasicTypes.UNASSIGNED_STR;
    // Storage
    protected float storageSize = BasicTypes.UNASSIGNED_FLOAT;
    protected String storageType = BasicTypes.UNASSIGNED_STR;

    // Price
    protected int priceTimeUnit = BasicTypes.UNASSIGNED_INT;
    protected float pricePerUnit = BasicTypes.UNASSIGNED_FLOAT;

    public HardwareDescription(){

    }

    public HardwareDescription(List<Processor> proc, int tCU, float memS, String memT, float strS, String strT, int pTU, float pPU) {
        this.processors = proc;
        this.totalComputingUnits = tCU;
        this.memorySize = memS;
        this.memoryType = memT;
        this.storageSize = strS;
        this.storageType = strT;
        this.priceTimeUnit = pTU;
        this.pricePerUnit = pPU;
    }

    public List<Processor> getProcessors() { return processors; }

    public void setProcessors(List<Processor> proc) { this.processors = proc; }

    public int getTotalComputingUnits() { return totalComputingUnits; }

    public void setTotalComputingUnits(int tCU) { this.totalComputingUnits = tCU; }

    public float getMemorySize() { return memorySize; }

    public void setMemorySize(float memS) { this.memorySize = memS; }

    public String getMemoryType() { return memoryType; }

    public void setMemoryType(String memT) { this.memoryType = memT; }

    public float getStorageSize() { return storageSize; }

    public void setStorageSize(float strS) { this.storageSize = strS; }

    public String getStorageType() { return storageType; }

    public void setStorageType(String strT) { this.storageType = strT; }

    public int getPriceTimeUnit() { return priceTimeUnit; }

    public void setPriceTimeUnit(int pTU) { this.priceTimeUnit = pTU; }

    public float getPricePerUnit() { return pricePerUnit; }

    public void setPricePerUnit(float pPU) { this.pricePerUnit = pPU; }
}
