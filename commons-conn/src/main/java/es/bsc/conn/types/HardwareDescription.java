package es.bsc.conn.types;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Implementation of a Virtual Resource Hardware Description
 *
 */
public class HardwareDescription {

    // Processor
    protected List<Processor> processors = new LinkedList<>();
    // Processor CPU computing units
    protected int totalCPUComputingUnits = BasicTypes.ZERO_INT;
    protected int totalGPUComputingUnits = BasicTypes.ZERO_INT;
    protected int totalFPGAComputingUnits = BasicTypes.ZERO_INT;
    
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
    protected Map<String, String> imageProperties = new HashMap<>();


    /**
     * Instantiates a default hardware description
     */
    public HardwareDescription() {
        // All default values are already set
    }

    /**
     * Instantiates a new hardware description with the given parameters
     * 
     * @param processors
     * @param totalComputingUnits
     * @param memorySize
     * @param memoryType
     * @param storageSize
     * @param storageType
     * @param priceTimeUnit
     * @param pricePerUnit
     * @param imageName
     * @param imageType
     * @param imageProperties
     */
    public HardwareDescription(List<Processor> processors, int totalCPUComputingUnits, int totalGPUComputingUnits, int totalFPGAComputingUnits, float memorySize, String memoryType, float storageSize,
            String storageType, int priceTimeUnit, float pricePerUnit, String imageName, String imageType,
            Map<String, String> imageProperties) {

        this.processors = processors;
        this.totalCPUComputingUnits = totalCPUComputingUnits;
        this.totalGPUComputingUnits = totalGPUComputingUnits;
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

    /**
     * Returns the processors' description
     * 
     * @return
     */
    public List<Processor> getProcessors() {
        return processors;
    }

    /**
     * Sets the processors' description
     * 
     * @param proc
     */
    public void setProcessors(List<Processor> proc) {
        this.processors = proc;
    }

    /**
     * Returns the total number of computing units
     * 
     * @return
     */
    public int getTotalCPUComputingUnits() {
        return totalCPUComputingUnits;
    }

    /**
     * Sets the total number of computing units
     * 
     * @param tCU
     */
    public void setTotalComputingUnits(int tCU) {
        this.totalCPUComputingUnits = tCU;
    }
    
    /**
     * Returns the total number of computing units
     * 
     * @return
     */
    public int getTotalGPUComputingUnits() {
        return totalGPUComputingUnits;
    }

    /**
     * Sets the total number of computing units
     * 
     * @param tCU
     */
    public void setTotalGPUComputingUnits(int tCU) {
        this.totalGPUComputingUnits = tCU;
    }
    
    /**
     * Returns the total number of computing units
     * 
     * @return
     */
    public int getTotalFPGAComputingUnits() {
        return totalFPGAComputingUnits;
    }

    /**
     * Sets the total number of computing units
     * 
     * @param tCU
     */
    public void setTotalFPGAComputingUnits(int tCU) {
        this.totalFPGAComputingUnits = tCU;
    }

    /**
     * Returns the memory size
     * 
     * @return
     */
    public float getMemorySize() {
        return memorySize;
    }

    /**
     * Sets the memory size
     * 
     * @param memS
     */
    public void setMemorySize(float memS) {
        this.memorySize = memS;
    }

    /**
     * Returns the memory type
     * 
     * @return
     */
    public String getMemoryType() {
        return memoryType;
    }

    /**
     * Sets the memory type
     * 
     * @param memT
     */
    public void setMemoryType(String memT) {
        this.memoryType = memT;
    }

    /**
     * Returns the storage size
     * 
     * @return
     */
    public float getStorageSize() {
        return storageSize;
    }

    /**
     * Sets the storage size
     * 
     * @param strS
     */
    public void setStorageSize(float strS) {
        this.storageSize = strS;
    }

    /**
     * Returns the stoarge type
     * 
     * @return
     */
    public String getStorageType() {
        return storageType;
    }

    /**
     * Sets the storage type
     * 
     * @param strT
     */
    public void setStorageType(String strT) {
        this.storageType = strT;
    }

    /**
     * Returns the price per time unit
     * 
     * @return
     */
    public int getPriceTimeUnit() {
        return priceTimeUnit;
    }

    /**
     * Sets the price per time unit
     * 
     * @param pTU
     */
    public void setPriceTimeUnit(int pTU) {
        this.priceTimeUnit = pTU;
    }

    /**
     * Returns the price per unit
     * 
     * @return
     */
    public float getPricePerUnit() {
        return pricePerUnit;
    }

    /**
     * Sets the price per unit
     * 
     * @param pPU
     */
    public void setPricePerUnit(float pPU) {
        this.pricePerUnit = pPU;
    }

    /**
     * Returns the image name
     * 
     * @return
     */
    public String getImageName() {
        return imageName;
    }

    /**
     * Sets the image name
     * 
     * @param imageName
     */
    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    /**
     * Returns the image type
     * 
     * @return
     */
    public String getImageType() {
        return imageType;
    }

    /**
     * Sets the image type
     * 
     * @param imageType
     */
    public void setImageType(String imageType) {
        this.imageType = imageType;
    }

    /**
     * Returns the image properties
     * 
     * @return
     */
    public Map<String, String> getImageProperties() {
        return imageProperties;
    }

    /**
     * Sets the image properties
     * 
     * @param imageProperties
     */
    public void setImageProperties(Map<String, String> imageProperties) {
        this.imageProperties = imageProperties;
    }

}
