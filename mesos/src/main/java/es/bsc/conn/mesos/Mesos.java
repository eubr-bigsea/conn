package es.bsc.conn.mesos;

import com.google.protobuf.ByteString;

import es.bsc.conn.Connector;
import es.bsc.conn.exceptions.ConnectorException;
import es.bsc.conn.types.HardwareDescription;
import es.bsc.conn.types.SoftwareDescription;
import es.bsc.conn.types.VirtualResource;

import es.bsc.mesos.framework.MesosFramework;


import java.util.HashMap;
import java.util.LinkedList;

import org.apache.mesos.Protos.Resource;
import org.apache.mesos.Protos.Value;


public class Mesos extends Connector {

    private static final long DEFAULT_TIME_SLOT = 300_000L;

    private long timeSlot = DEFAULT_TIME_SLOT;

    private static MesosFramework framework;

    private HashMap<String, String> properties;


    public Mesos(HashMap<String, String> props) throws Exception {
        super(props);
        properties = props;
        framework = new MesosFramework(props);
    }

    private Value.Scalar buildScalar(double value) {
        return Value.Scalar.newBuilder().setValue(value).build();
    }

    private Resource buildResource(String name, double value) {
        return Resource.newBuilder().setName(name).setType(Value.Type.SCALAR)
                    .setScalar(buildScalar(value)).build();
    }

    @Override
    public Object create(HardwareDescription hd, SoftwareDescription sd, HashMap<String, String> prop) throws ConnectorException {
        LinkedList<Resource> res = new LinkedList<Resource>();
        res.add(buildResource("cpus", hd.getTotalComputingUnits()));
        res.add(buildResource("mem", hd.getMemorySize()));
        res.add(buildResource("disk", hd.getStorageSize()));

        return framework.requestWorker(res);
    }

    @Override
    public VirtualResource waitUntilCreation(Object id) throws ConnectorException {
        VirtualResource vr = (VirtualResource)id;
        String ip = framework.waitWorkerUntilRunning((String) vr.getId());
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
        framework.removeWorker((String) id);
    }

    @Override
    public void close() {
        framework.stop();
    }
}
