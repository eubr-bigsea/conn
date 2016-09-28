package es.bsc.conn.rocci;

import es.bsc.conn.exceptions.ConnectorException;
import es.bsc.conn.rocci.types.json.JSONResources;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

// TODO: add logger
//import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


public class RocciClient {
    //private static Logger LOGGER = null;

    private String cmd_line = "";
    private String attributes = "";

    public RocciClient(List<String> cmd_string, String attr){
        //LOGGER = logger;
        for (String s : cmd_string){
            cmd_line += s + " ";
        }
        attributes = attr;
    }
    public String describe_resource(String resource_id) throws ConnectorException {
        String res_desc = "";
        String cmd = cmd_line + "--action describe" + " --resource " + resource_id;

        try {
            res_desc = execute_cmd(cmd);
        } catch (InterruptedException e) {
            //LOGGER.error(e);
            System.out.println(e);
        }
        return res_desc;
    }

    public String get_resource_status(String resource_id) throws ConnectorException {
        String res_status = null;
        String jsonOutput = describe_resource(resource_id);

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        jsonOutput = "{\"resources\":" + jsonOutput + "}";

        // convert the json string back to object
        JSONResources obj = gson.fromJson(jsonOutput, JSONResources.class);
        res_status = obj.getResources().get(0).getAttributes().getOcci().getCompute().getState();

        return res_status;
    }
    public String get_resource_address(String resource_id) throws ConnectorException {
        String res_ip = null;
        String jsonOutput = describe_resource(resource_id);

        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        jsonOutput = "{\"resources\":"+jsonOutput+"}";

        //convert the json string back to object
        JSONResources obj = gson.fromJson(jsonOutput, JSONResources.class);

        for(int i = 0; i< obj.getResources().get(0).getLinks().size(); i++){
            if(obj.getResources().get(0).getLinks().get(i).getAttributes().getOcci().getNetworkinterface() != null){
                res_ip = obj.getResources().get(0).getLinks().get(i).getAttributes().getOcci().getNetworkinterface().getAddress();
                break;
            }
        }
        return res_ip;
    }
    public void delete_compute(String resource_id) {
        String cmd = cmd_line + "--action delete" + " --resource " + resource_id;
        try {
            execute_cmd(cmd);
        } catch (ConnectorException | InterruptedException e) {
            //LOGGER.error(e);
            System.out.println(e);
        } catch (Exception e) {
            //LOGGER.error(e);
            System.out.println(e);
        }
    }
    public String create_compute(String os_tpl, String resource_tpl) {
        String s = "";

        String cmd = cmd_line + " --action create" + " --resource compute -M os_tpl#" +
                os_tpl + " -M resource_tpl#" + resource_tpl + " --attribute occi.core.title=\""+attributes+"\"";

        try {
            s = execute_cmd(cmd);
            System.out.println(s);
        } catch (ConnectorException | InterruptedException e) {
            //LOGGER.error(e);
            System.out.println(e);
        }

        return s;
    }

    private String execute_cmd(String cmd_args) throws ConnectorException, InterruptedException {
        String return_string = "";
        String buff = null;

        String [] cmd_line = {"/bin/bash", "-c", "occi " + cmd_args};

        try {
            System.out.println(Arrays.toString(cmd_line));
            Process p = Runtime.getRuntime().exec(cmd_line);

            BufferedReader stdInput1 = new BufferedReader(new
                    InputStreamReader(p.getInputStream()));

            BufferedReader stdError1 = new BufferedReader(new
                    InputStreamReader(p.getErrorStream()));

            // read the output from the command
            System.out.println("Here is the standard output of the command:\n");
            String s1 = null;
            while ((s1 = stdInput1.readLine()) != null) {
                System.out.println(s1);
                return_string += s1;
            }

            // read any errors from the attempted command
            System.out.println("Here is the standard error of the command (if any):\n");
            while ((s1 = stdError1.readLine()) != null) {
                System.out.println(s1);
            }

            p.waitFor();
            System.out.println("Exit value: " + p.exitValue());
            System.out.println("__________________________________________");
            return return_string;
        } catch (IOException e) {
            throw new ConnectorException(e);
        }
    }

}
