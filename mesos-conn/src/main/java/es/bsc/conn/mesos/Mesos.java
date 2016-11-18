package es.bsc.conn.mesos;

import es.bsc.conn.Connector;
import es.bsc.conn.clients.mesos.framework.MesosFramework;
import es.bsc.conn.clients.mesos.framework.exceptions.FrameworkException;
import es.bsc.conn.exceptions.ConnException;
import es.bsc.conn.loggers.Loggers;
import es.bsc.conn.types.HardwareDescription;
import es.bsc.conn.types.SoftwareDescription;
import es.bsc.conn.types.VirtualResource;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.mesos.Protos.Resource;
import org.apache.mesos.Protos.Value;

/**
 * Implementation of Mesos connector
 *
 */
public class Mesos extends Connector {

    // Properties' names
    private static final String PROP_CPUS = "cpus";
    private static final String PROP_MEM = "mem";
    private static final String PROP_DISK = "disk";
    private static final String PROP_PORTS = "ports";
    private static final String PROP_PRICE = "price";

    // Conversion constants
    private static final double GIGAS_TO_MEGAS = 1024.0;
    private static final String UNDEFINED_IP = "-1.-1.-1.-1";
    private static final long SSH_PORT = 22L;

    // Logger
    private static final Logger logger = LogManager.getLogger(Loggers.MESOS);

    // Mesos Framework Client
    private final MesosFramework framework;

    // Information about resources
    private final Map<String, VirtualResource> resources;


    /**
     * Initializes the Mesos connector with the given properties.
     * A Mesos Framework is started, it will do the communication with Mesos.
     *
     * @param props
     * @throws ConnException
     */
    public Mesos(Map<String, String> props) throws ConnException {
        super(props);

        logger.info("Initializing MESOS Connector");
        resources = new HashMap<>();
        try {
            framework = new MesosFramework(props);
        } catch (FrameworkException fe) {
            throw new ConnException(fe);
        }
    }

    /**
     * Creates a mesos container. It is a docker image with the resources specified in hd, sd, prop.
     *
     * @param   hd Information about cpus, mem, disk, price and image.
     * @param   sd Information about operating system.
     * @param   prop Properties inherited from Resources.
     * @return  Object Mesos container identifier generated.
     * @throws ConnException
     */
    @Override
    public Object create(HardwareDescription hd, SoftwareDescription sd, Map<String, String> prop) throws ConnException {
        long adaptorMinPort = Long.parseLong(getProperty(hd.getImageProperties(),
                PROP_ADAPTOR_MIN_PORT, Integer.toString(DEFAULT_MIN_PORT)));
        long adaptorMaxPort = Long.parseLong(getProperty(hd.getImageProperties(),
                PROP_ADAPTOR_MAX_PORT, Integer.toString(DEFAULT_MAX_PORT)));

        List<Resource> res = new LinkedList<>();
        Value.Ranges ports = buildRanges(adaptorMinPort, adaptorMaxPort);
        res.add(buildResource(PROP_CPUS, hd.getTotalComputingUnits()));
        res.add(buildResource(PROP_MEM, GIGAS_TO_MEGAS * hd.getMemorySize()));
        res.add(buildResource(PROP_DISK, GIGAS_TO_MEGAS * hd.getStorageSize()));
        res.add(buildResource(PROP_PORTS, ports));

        String newId = framework.requestWorker(appName, hd.getImageName(), res);
        resources.put(newId, new VirtualResource((String) newId, hd, sd, prop));

        return newId;
    }

    /**
     * Waits Mesos container with identifier id to be ready.
     * @param  id Mesos container identifier.
     * @return VirtualResource assigned to that container.
     */
    @Override
    public VirtualResource waitUntilCreation(Object id) throws ConnException {
        String identifier = (String) id;
        if (!resources.containsKey(identifier)) {
            throw new ConnException("This identifier does not exist " + identifier);
        }
        VirtualResource vr = resources.get(identifier);
        String ip = framework.waitWorkerUntilRunning(identifier);
        if (UNDEFINED_IP.equals(ip)) {
            throw new ConnException("Could not wait until creation of worker " + id);
        }
        vr.setIp(ip);
        return vr;
    }

    /**
     * @param  vr Corresponding machine to get the price from.
     * @return Price
     */
    @Override
    public float getPriceSlot(VirtualResource vr) {
        if (vr.getProperties().containsKey(PROP_PRICE)) {
            return Float.parseFloat(vr.getProperties().get(PROP_PRICE));
        }
        return 0.0f;
    }

    /**
     * Kills the Mesos container with identifier id.
     * @param id
     */
    @Override
    public void destroy(Object id) {
        String identifier = (String) id;
        resources.remove(identifier);
        framework.removeWorker(identifier);
    }

    /**
     * Shutdowns the Mesos connector.
     */
    @Override
    public void close() {
        framework.stop();
    }

    private String getProperty(Map<String, String> props, String property, String defaultValue) {
        String value = props.get(property);
        return (value != null && !value.isEmpty())? value: defaultValue;
    }

    private Value.Range buildRange(long begin, long end) {
        return Value.Range.newBuilder().setBegin(begin).setEnd(end).build();
    }

    private Value.Scalar buildScalar(double value) {
        return Value.Scalar.newBuilder().setValue(value).build();
    }

    private Value.Ranges buildRanges(long begin, long end) {
        Value.Ranges.Builder ranges = Value.Ranges.newBuilder().addRange(buildRange(SSH_PORT, SSH_PORT));
        if (begin > 0L && end > 0L) {
            ranges.addRange(buildRange(begin, end));
        }
        return ranges.build();
    }

    private Resource buildResource(String name, Value.Ranges ranges) {
        return Resource.newBuilder().setName(name).setType(Value.Type.RANGES).setRanges(ranges).build();
    }

    private Resource buildResource(String name, double value) {
        return Resource.newBuilder().setName(name).setType(Value.Type.SCALAR).setScalar(buildScalar(value)).build();
    }


}
