package es.bsc.conn.types;

/**
 * Copy from integratedtoolkit.types.resources.components
 */
public class Processor {
    // Types TODO: where is the place for that?
    public static final int UNASSIGNED_INT 		= -1;
    public static final String UNASSIGNED_STR 	= "[unassigned]";
    public static final float UNASSIGNED_FLOAT 	= (float) -1.0;
    public static final int ZERO_INT			= 0;
    public static final int ONE_INT				= 1;

    private String name = UNASSIGNED_STR;
    private int computingUnits = ZERO_INT;
    private float speed = UNASSIGNED_FLOAT;
    private String architecture = UNASSIGNED_STR;
    private String propName = UNASSIGNED_STR;
    private String propValue = UNASSIGNED_STR;

    public Processor() {
    }

    public Processor(String name) {
        this.setName(name);
    }

    public Processor(String name, int cu) {
        this.setName(name);
        this.setComputingUnits(cu);
    }

    public Processor(String name, int cu, float speed) {
        this.setName(name);
        this.setComputingUnits(cu);
        this.setSpeed(speed);
    }

    public Processor(String name, int cu, float speed, String arch) {
        this.setName(name);
        this.setComputingUnits(cu);
        this.setSpeed(speed);
        this.setArchitecture(arch);
    }

    public Processor(String name, int cu, float speed, String arch, String propName, String propValue) {
        this.setName(name);
        this.setComputingUnits(cu);
        this.setSpeed(speed);
        this.setArchitecture(arch);
        this.setPropName(propName);
        this.setPropValue(propValue);
    }

    public Processor(String name, int cu, String propName, String propValue) {
        this.setName(name);
        this.setComputingUnits(cu);
        this.setPropName(propName);
        this.setPropValue(propValue);
    }

    public Processor(Processor p) {
        this.setName(p.getName());
        this.setComputingUnits(p.getComputingUnits());
        this.setSpeed(p.getSpeed());
        this.setArchitecture(p.getArchitecture());
        this.setPropName(p.getPropName());
        this.setPropValue(p.getPropValue());
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getComputingUnits() {
        return computingUnits;
    }

    public void setComputingUnits(int computingUnits) {
        this.computingUnits = computingUnits;
    }

    public void addComputingUnits(int cu) {
        this.computingUnits = this.computingUnits + cu;
    }

    public void removeComputingUnits(int cu) {
        this.computingUnits = this.computingUnits - cu;
    }

    public void multiply(int amount) {
        this.computingUnits = this.computingUnits*amount;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public String getArchitecture() {
        return architecture;
    }

    public void setArchitecture(String architecture) {
        this.architecture = architecture;
    }

    public String getPropName() {
        return propName;
    }

    public void setPropName(String propName) {
        this.propName = propName;
    }

    public String getPropValue() {
        return propValue;
    }

    public void setPropValue(String propValue) {
        this.propValue = propValue;
    }

}
