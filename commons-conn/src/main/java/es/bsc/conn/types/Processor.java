package es.bsc.conn.types;

/**
 * Copy from integratedtoolkit.types.resources.components
 */
public class Processor {

    private String name = BasicTypes.UNASSIGNED_STR;
    private int computingUnits = BasicTypes.ZERO_INT;
    private float speed = BasicTypes.UNASSIGNED_FLOAT;
    private String architecture = BasicTypes.UNASSIGNED_STR;
    private String propName = BasicTypes.UNASSIGNED_STR;
    private String propValue = BasicTypes.UNASSIGNED_STR;


    /**
     * Instantiates a default processor
     */
    public Processor() {
        // All default values are already set
    }

    /**
     * Instantiates a processor with a given name (the rest of the parameters are set by default)
     * 
     * @param name
     */
    public Processor(String name) {
        this.setName(name);
    }

    /**
     * Instantiates a processor with a given name and computing units (the rest of the parameters are set by default)
     * 
     * @param name
     * @param cu
     */
    public Processor(String name, int cu) {
        this.setName(name);
        this.setComputingUnits(cu);
    }

    /**
     * Instantiates a processor with a given name, computing units and speed (the rest of the parameters are set by
     * default)
     * 
     * @param name
     * @param cu
     * @param speed
     */
    public Processor(String name, int cu, float speed) {
        this.setName(name);
        this.setComputingUnits(cu);
        this.setSpeed(speed);
    }

    /**
     * Instantiates a processor with a given name, computing units, speed and architecture (the rest of the parameters
     * are set by default)
     * 
     * @param name
     * @param cu
     * @param speed
     * @param arch
     */
    public Processor(String name, int cu, float speed, String arch) {
        this.setName(name);
        this.setComputingUnits(cu);
        this.setSpeed(speed);
        this.setArchitecture(arch);
    }

    /**
     * Instantiates a processor with a given name, computing units, speed, architecture and processor property key-value
     * (the rest of the parameters are set by default)
     * 
     * @param name
     * @param cu
     * @param speed
     * @param arch
     * @param propName
     * @param propValue
     */
    public Processor(String name, int cu, float speed, String arch, String propName, String propValue) {
        this.setName(name);
        this.setComputingUnits(cu);
        this.setSpeed(speed);
        this.setArchitecture(arch);
        this.setPropName(propName);
        this.setPropValue(propValue);
    }

    /**
     * Instantiates a processor with a given name, computing units and processor property key-value (the rest of the
     * parameters are set by default)
     * 
     * @param name
     * @param cu
     * @param propName
     * @param propValue
     */
    public Processor(String name, int cu, String propName, String propValue) {
        this.setName(name);
        this.setComputingUnits(cu);
        this.setPropName(propName);
        this.setPropValue(propValue);
    }

    /**
     * Instantiates a new processor copying the properties of the given processor @p
     * 
     * @param p
     */
    public Processor(Processor p) {
        this.setName(p.getName());
        this.setComputingUnits(p.getComputingUnits());
        this.setSpeed(p.getSpeed());
        this.setArchitecture(p.getArchitecture());
        this.setPropName(p.getPropName());
        this.setPropValue(p.getPropValue());
    }

    /**
     * Returns the name of the processor
     * 
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the processor
     * 
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the number of computing units of the processor
     * 
     * @return
     */
    public int getComputingUnits() {
        return computingUnits;
    }

    /**
     * Sets the number of computing units of the processor
     * 
     * @param computingUnits
     */
    public void setComputingUnits(int computingUnits) {
        this.computingUnits = computingUnits;
    }

    /**
     * Adds a given amount @cu of computing units to the current processor
     * 
     * @param cu
     */
    public void addComputingUnits(int cu) {
        this.computingUnits = this.computingUnits + cu;
    }

    /**
     * Removes a given amount @cu of computing units to the current processor
     * 
     * @param cu
     */
    public void removeComputingUnits(int cu) {
        this.computingUnits = this.computingUnits - cu;
    }

    /**
     * Scales the current computing units by a given factor @amout
     * 
     * @param amount
     */
    public void multiply(int amount) {
        this.computingUnits = this.computingUnits * amount;
    }

    /**
     * Returns the processor speed
     * 
     * @return
     */
    public float getSpeed() {
        return speed;
    }

    /**
     * Sets the processor speed
     * 
     * @param speed
     */
    public void setSpeed(float speed) {
        this.speed = speed;
    }

    /**
     * Returns the processor architecture
     * 
     * @return
     */
    public String getArchitecture() {
        return architecture;
    }

    /**
     * Sets the processor architecture
     * 
     * @param architecture
     */
    public void setArchitecture(String architecture) {
        this.architecture = architecture;
    }

    /**
     * Returns the processor property name
     * 
     * @return
     */
    public String getPropName() {
        return propName;
    }

    /**
     * Sets the processor property name
     * 
     * @param propName
     */
    public void setPropName(String propName) {
        this.propName = propName;
    }

    /**
     * Returns the processor property value
     * 
     * @return
     */
    public String getPropValue() {
        return propValue;
    }

    /**
     * Sets the processor property value
     * 
     * @param propValue
     */
    public void setPropValue(String propValue) {
        this.propValue = propValue;
    }

}
