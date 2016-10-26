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


public class Mesos extends Connector {

    private static final Logger logger = LogManager.getLogger(Loggers.MESOS);

    private static final long DEFAULT_TIME_SLOT = 300_000L;
    private static final double GIGAS_TO_MEGAS = 1024.0;
    private static final String UNDEFINED_IP = "-1.-1.-1.-1";

    private long timeSlot = DEFAULT_TIME_SLOT;

    private final MesosFramework framework;

    private final Map<String, VirtualResource> resources;


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

    private Value.Scalar buildScalar(double value) {
        return Value.Scalar.newBuilder().setValue(value).build();
    }

    private Resource buildResource(String name, double value) {
        return Resource.newBuilder().setName(name).setType(Value.Type.SCALAR).setScalar(buildScalar(value)).build();
    }

    @Override
    public Object create(HardwareDescription hd, SoftwareDescription sd, Map<String, String> prop) throws ConnException {
        List<Resource> res = new LinkedList<>();
        res.add(buildResource("cpus", hd.getTotalComputingUnits()));
        res.add(buildResource("mem", GIGAS_TO_MEGAS * hd.getMemorySize()));
        res.add(buildResource("disk", GIGAS_TO_MEGAS * hd.getStorageSize()));

        String newId = framework.requestWorker(res);
        resources.put(newId, new VirtualResource((String) newId, hd, sd, prop));

        return newId;
    }

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

    @Override
    public float getPriceSlot(VirtualResource vr) {
        if (vr.getProperties().containsKey("price")) {
            return Float.parseFloat(vr.getProperties().get("price"));
        }
        return 0.0f;
    }

    @Override
    public long getTimeSlot() {
        return timeSlot;
    }

    @Override
    public void destroy(Object id) {
        String identifier = (String) id;
        resources.remove(identifier);
        framework.removeWorker(identifier);
    }

    @Override
    public void close() {
        framework.stop();
    }

}
