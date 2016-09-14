package es.bsc.conn.types;
import java.util.HashMap;
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

    // Image
    protected String imageName;
    protected String imageType;
    protected HashMap<String, String> imageProperties = new HashMap<>();

    public HardwareDescription(){

    }

    public HardwareDescription(List<Processor> processors, int totalComputingUnits, float memorySize, String memoryType,
                               float storageSize, String storageType, int priceTimeUnit, float pricePerUnit,
                               String imageName, String imageType, HashMap<String, String> imageProperties) {
        this.processors = processors;
        this.totalComputingUnits = totalComputingUnits;
        this.memorySize = memorySize;
        this.memoryType = memoryType;
        this.storageSize = storageSize;
        this.storageType = storageType;
        this.priceTimeUnit = priceTimeUnit;
        this.pricePerUnit = pricePerUnit;
        this.imageName = imageName;
        this.imageType = imageType;
        this.imageProperties = imageProperties;
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

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) { this.imageName = imageName; }

    public String getImageType() { return imageType; }

    public void setImageType(String imageType) { this.imageType = imageType; }

    public HashMap<String, String> getImageProperties() { return imageProperties; }

    public void setImageProperties(HashMap<String, String> imageProperties) { this.imageProperties = imageProperties; }
}
