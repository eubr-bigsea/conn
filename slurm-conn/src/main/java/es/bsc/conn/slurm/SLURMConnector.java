package es.bsc.conn.slurm;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import es.bsc.conn.Connector;
import es.bsc.conn.clients.exceptions.ConnClientException;
import es.bsc.conn.clients.slurm.JobDescription;
import es.bsc.conn.clients.slurm.SlurmClient;
import es.bsc.conn.exceptions.ConnException;
import es.bsc.conn.loggers.Loggers;
import es.bsc.conn.types.HardwareDescription;
import es.bsc.conn.types.InstallationDescription;
import es.bsc.conn.types.Processor;
import es.bsc.conn.types.SoftwareDescription;
import es.bsc.conn.types.VirtualResource;


/**
 * Implementation of SLURM Connector
 *
 */
public class SLURMConnector extends Connector {

    // Constants
    private static final String RUNNING = "RUNNING";
    private static final String FAILED = "FAILED";

    private static final long POLLING_INTERVAL = 5;
    private static final int TIMEOUT = 1_800;

    // Properties values
    private static final String JOB_STATE = "JobState";

    // Logger
    private static final Logger LOGGER = LogManager.getLogger(Loggers.SLURM);

    // VMM Client
    private SlurmClient client;
    private String network;

    // Information about requests
    private final Map<String, HardwareDescription> vmidToHardwareRequest = new HashMap<>();
    private final Map<String, SoftwareDescription> vmidToSoftwareRequest = new HashMap<>();
    private final Map<String, String> vmidToHostName = new HashMap<>();
    private int currentNodes;
    private String logDir;


    /**
     * Initializes the Slurm connector with the given properties
     * 
     * @param props
     * @throws ConnException
     */
    public SLURMConnector(Map<String, String> props) throws ConnException {
        super(props);
        String appLogdir = System.getProperty("compss.appLogDir");
        if (appLogdir == null) {
            throw new ConnException("Unable to get app log dir");
        }
        File f = new File(appLogdir + File.separator + "slurm-conn-log");
        if (f.mkdirs()) {
            logDir = f.getAbsolutePath();
        } else {
            throw new ConnException("Unable to create SLURM connector log dir");
        }
        
        String masterName = props.get("master_name");
        if (masterName == null || masterName.isEmpty()) {
            throw new ConnException("Unable to get master_name. Property is empty");
        }
        boolean ssh = false;
        String sshStr = props.get("slurm_over_ssh");
        if (sshStr != null && !sshStr.isEmpty()) {
            ssh = Boolean.parseBoolean(sshStr);
        }
        LOGGER.debug("Starting Slurm client for master in "+masterName+" and ssh "+ssh);
        this.client = new SlurmClient(masterName, ssh);
        
        this.network = props.get("network");
        if (this.network == null) {
            this.network = "";
        }
        currentNodes = 0;

    }

    @Override
    public Object create(HardwareDescription hd, SoftwareDescription sd, Map<String, String> prop) throws ConnException {
        try {
            String jobName = appName + '-' + UUID.randomUUID().toString();
            currentNodes++;
            String jobId = client.createCompute(generateJobDescription(hd, sd), generateExecScript(jobName, hd, sd, prop));
            vmidToHardwareRequest.put(jobId, hd);
            vmidToSoftwareRequest.put(jobId, sd);

            VirtualResource vr = new VirtualResource(jobId, hd, sd, prop);
            return vr.getId();
        } catch (ConnClientException ce) {
            LOGGER.error("Exception submitting vm creation", ce);
            currentNodes--;
            throw new ConnException(ce);
        } catch (Exception e) {
            LOGGER.error("Exception submitting vm creation", e);
            throw new ConnException(e);
        }
    }

    private String generateExecScript(String jobName, HardwareDescription hd, SoftwareDescription sd, Map<String, String> prop)
            throws ConnException {

        // stderr stdout flags
        String stdFlags = "-e " + logDir + File.separator + jobName + ".err -o " + logDir + File.separator + jobName + ".out";
        InstallationDescription instDesc = sd.getInstallation();
        StringBuilder script = new StringBuilder("#!/bin/sh\n");

        // COMMAND
        String installDir = instDesc.getInstallDir();
        if (installDir == null) {
            installDir = System.getenv("COMPSS_HOME");
            if (installDir == null) {
                throw new ConnException("Unable to get COMPSs installation directory");
            }
        }
        if (hd.getImageName() != null && !hd.getImageName().isEmpty() && !"None".equals(hd.getImageName())) {
            script.append("singularity exec " + hd.getImageName() + " " + installDir
                    + "/Runtime/scripts/system/adaptors/nio/persistent_worker_starter.sh");
        } else {
            script.append(installDir + "/Runtime/scripts/system/adaptors/nio/persistent_worker_starter.sh");
        }
        // libpath ($1)
        String libPath = instDesc.getLibraryPath();
        if (libPath == null || libPath.isEmpty()) {
            libPath = getPWD();
        }
        script.append(" " + libPath);
        // appDir ($2)
        String appDir = instDesc.getAppDir();
        if (appDir == null || appDir.isEmpty()) {
            appDir = getPWD();
        }
        script.append(" " + appDir);
        // classpath ($3)
        String cp = instDesc.getClasspath();
        if (cp == null || cp.isEmpty()) {
            cp = getPWD();
        }
        script.append(" " + cp);
        // jvm_opts_size ($4)
        String jvmOptsSize = prop.get("jvm_opts_size");
        if (jvmOptsSize == null || jvmOptsSize.isEmpty()) {
            jvmOptsSize = "0";
        }
        script.append(" " + jvmOptsSize);
        // jvm_opts_str ($5)
        String jvmOptsStr = prop.get("jvm_opts_str");
        if (jvmOptsStr == null) {
            jvmOptsStr = "";
        }
        script.append(" " + jvmOptsStr);

        // Configure worker debug level ($6)
        String workerDebug = prop.get("worker_debug");
        if (workerDebug == null || workerDebug.isEmpty() || "null".equals(workerDebug)) {
            workerDebug = "false";
        }
        script.append(" " + workerDebug);

        // MaxSend ($7)
        script.append(" " + 5);

        // MaxReceived ($7)
        script.append(" " + 5);

        // Node Name
        script.append(" $SLURM_JOB_NODELIST" + network);

        // worker Port
        script.append(" 43001");

        // master Port
        String masterPort = prop.get("master_port");
        if (masterPort == null || masterPort.isEmpty() || "null".equals(masterPort)) {
            masterPort = "43000";
        }
        script.append(" " + masterPort);

        // CPU CUs
        script.append(" " + hd.getTotalComputingUnits());

        // GPU CUs
        script.append(" " + hd.getTotalGPUComputingUnits());

        // CPU Affinity
        String cpuAff = prop.get("cpu_affinity");
        if (cpuAff == null || cpuAff.isEmpty() || "null".equals(cpuAff)) {
            cpuAff = "automatic";
        }
        script.append(" " + cpuAff);

        // GPU Affinity
        String gpuAff = prop.get("gpu_affinity");
        if (gpuAff == null || gpuAff.isEmpty() || "null".equals(gpuAff)) {
            gpuAff = "automatic";
        }
        script.append(" " + gpuAff);

        // Limit Of Tasks
        int limitOfTasks = instDesc.getLimitOfTasks();
        if (limitOfTasks < 0) {
            limitOfTasks = hd.getTotalComputingUnits();
        }
        script.append(" " + limitOfTasks);

        // uuid
        String uuid = System.getProperty("compss.uuid");
        if (uuid == null || uuid.isEmpty() || "null".equals(uuid)) {
            throw new ConnException("Unable to get uuid");
        }
        script.append(" " + uuid);

        // lang
        String lang = System.getProperty("compss.lang");
        if (lang == null || lang.isEmpty() || "null".equals(lang)) {
            throw new ConnException("Unable to get lang");
        }
        script.append(" " + lang);

        // sandboxeddir
        script.append(" " + instDesc.getWorkingDir() + File.separator + uuid + File.separator + "$SLURM_JOB_NODELIST" + network);

        // install_dir
        script.append(" " + installDir);

        // appdir
        script.append(" " + appDir);

        // library_path
        script.append(" " + libPath);

        // classpath
        script.append(" " + cp);

        // pythonpath
        String pythonPath = instDesc.getPythonPath();
        if (pythonPath == null || pythonPath.isEmpty()) {
            pythonPath = getPWD();
        }
        script.append(" " + pythonPath);

        // tracing
        String tracing = System.getProperty("compss.tracing");
        if (tracing == null || tracing.isEmpty() || "null".equals(tracing)) {
            tracing = "0";
        }
        script.append(" " + tracing);

        // extrae file
        String extraeFile = System.getProperty("compss.extrae.file");
        if (extraeFile == null || extraeFile.isEmpty() || "null".equals(extraeFile)) {
            extraeFile = "null";
        }
        script.append(" " + extraeFile);

        // NodeId
        script.append(" " + (client.getInitialNodes() + currentNodes));

        // Configure storage
        String storageConf = System.getProperty("compss.storage.conf");
        if (storageConf == null || storageConf.isEmpty() || "null".equals(storageConf)) {
            storageConf = "null";
        }
        script.append(" " + storageConf);

        // Task execution
        String executionType = System.getProperty("compss.task.execution");
        if (executionType == null || executionType.isEmpty() || "null".equals(executionType)) {
            executionType = "compss";
        }
        script.append(" " + executionType);

        // Persistent c flag
        String persistentC = System.getProperty("compss.worker.persistent.c");
        if (persistentC == null || persistentC.isEmpty() || "null".equals(persistentC)) {
            persistentC = "false";
        }
        script.append(" " + persistentC);

        File runScript = new File(logDir + File.separator + "run_" + jobName);
        try {
            if (!runScript.createNewFile()) {
                throw new IOException("ERROR: File already exists");
            }
            if (!runScript.setExecutable(true)) {
                throw new IOException("ERROR: Cannot make the file executable");
            }
        } catch (IOException ioe) {
            throw new ConnException("Exception creating script", ioe);
        }

        try (FileOutputStream fos = new FileOutputStream(runScript)) {
            fos.write(script.toString().getBytes());
            return stdFlags + " " + runScript.getAbsolutePath();
        } catch (IOException e) {
            throw new ConnException("Exception writting script", e);
        }
    }

    private String getPWD() throws ConnException {
        String pwd = System.getenv("PWD");
        if (pwd == null) {
            throw new ConnException("Unable to get PWD directory");
        }
        return pwd;
    }

    private JobDescription generateJobDescription(HardwareDescription hd, SoftwareDescription sd) {
        Map<String, String> req = new HashMap<>();
        req.put("NumNodes", "1");

        req.put("NumCPUs", Integer.toString(hd.getTotalComputingUnits()));

        if (hd.getMemorySize() > 0) {
            req.put("mem", Integer.toString((int) hd.getMemorySize() * 1024));
        }
        // check gpus and set as gres
        int gpuUnits = 0;
        for (Processor p : hd.getProcessors()) {
            if ("GPU".equals(p.getArchitecture())) {
                gpuUnits = gpuUnits + p.getComputingUnits();
            }
        }
        if (gpuUnits > 0) {
            req.put("Gres", Integer.toString(gpuUnits));
        }
        return new JobDescription(req);
    }

    @Override
    public VirtualResource waitUntilCreation(Object id) throws ConnException {
        LOGGER.debug("Waiting for resource creation " + id);
        String jobId = (String) id;
        LOGGER.debug("Waiting until node of job " + jobId + " is created");

        try {
            JobDescription jd = client.getJobDescription(jobId);
            LOGGER.debug("Job State is " + jd.getProperty(JOB_STATE));
            int tries = 0;
            while (jd.getProperty(JOB_STATE) == null || !jd.getProperty(JOB_STATE).equals(RUNNING)) {
                if (jd.getProperty(JOB_STATE).equals(FAILED)) {
                    LOGGER.error("Error waiting for VM Creation. Middleware has return an error state");
                    throw new ConnException("Error waiting for VM Creation. Middleware has return an error state");
                }
                if (tries * POLLING_INTERVAL > TIMEOUT) {
                    client.cancelJob(jobId);
                    vmidToHardwareRequest.remove(jobId);
                    vmidToSoftwareRequest.remove(jobId);
                    throw new ConnException("Maximum Job creation time reached.");
                }

                tries++;

                Thread.sleep(POLLING_INTERVAL * 1_000);

                jd = client.getJobDescription(jobId);
                LOGGER.debug("Job State is " + jd.getProperty(JOB_STATE));
            }

            client.addNodesToMain(jobId, jd);

            // Create Virtual Resource
            VirtualResource vr = new VirtualResource();
            vr.setId(jobId);
            String resourceName = jd.getNodeList().get(0) + network;
            LOGGER.debug("Setting resource ip: " + resourceName);
            vmidToHostName.put(jobId, resourceName);
            vr.setIp(resourceName);
            vr.setProperties(null);

            HardwareDescription hd = vmidToHardwareRequest.get(jobId);
            if (hd == null) {
                throw new ConnException("Unregistered hardware description for job " + jobId);
            }
            /*
             * hd.setTotalComputingUnits(vmd.getCpus()); hd.setMemorySize(vmd.getRamMb()/1024);
             * hd.setStorageSize(vmd.getDiskGb()); hd.setImageName(sd.getImage());
             */
            vr.setHd(hd);

            SoftwareDescription sd = vmidToSoftwareRequest.get(jobId);
            if (sd == null) {
                throw new ConnException("Unregistered software description for job " + jobId);
            }
            sd.setOperatingSystemType("Linux");
            vr.setSd(sd);
            return vr;
        } catch (ConnClientException | InterruptedException e) {
            LOGGER.error("Exception waiting for VM Creation");
            throw new ConnException(e);
        }
    }

    @Override
    public void destroy(Object id) {
        String jobId = (String) id;
        LOGGER.debug("Destroying node for job " + jobId);
        try {
            String resourceName = vmidToHostName.get(jobId);
            if (resourceName != null && !resourceName.isEmpty()) {
                if (!network.isEmpty()) {
                    // erase network for resourceName
                    resourceName = resourceName.substring(0, resourceName.indexOf(network));
                }
                LOGGER.debug("Deleting node " + resourceName);
                client.deleteCompute(resourceName);
            } else {
                LOGGER.debug("Node not found cancelling job " + jobId);
            }
            vmidToHardwareRequest.remove(jobId);
            vmidToSoftwareRequest.remove(jobId);
            vmidToHostName.remove(jobId);

        } catch (ConnClientException cce) {
            LOGGER.error("Exception waiting for Node Destruction", cce);
        }
        LOGGER.debug("Node for job " + jobId + " destroyed.");
        currentNodes--;
    }

    @Override
    public float getPriceSlot(VirtualResource virtualResource) {
        return virtualResource.getHd().getPricePerUnit();
    }

    @Override
    public void close() {
        // Nothing to do
    }

}
