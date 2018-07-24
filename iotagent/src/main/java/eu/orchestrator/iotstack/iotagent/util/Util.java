package eu.orchestrator.iotstack.iotagent.util;

import eu.orchestrator.iotstack.iotagent.IoTAgent;
import eu.orchestrator.iotstack.transfer.Node;
import eu.orchestrator.iotstack.transfer.Peer;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.web.client.RestTemplate;

/**
 *
 * @author Panagiotis Gouvas
 */
public class Util {

    private static final Logger logger = Logger.getLogger(Util.class.getName());

    private static final int COMMAND_GET_STATUS = 0;
    private static final int COMMAND_GET_VCPUS = 1;
    private static final int COMMAND_GET_CPUSPEED = 2;
    private static final int COMMAND_GET_MEMORYSIZE = 3;
    private static final int COMMAND_GET_DISKSIZE = 4;

    //  lscpu | grep -E '^Thread|^Core|^Socket|^CPU\('
    //  nproc --all
    //  lscpu | grep "max MHz" | awk  '{print $4}' | cut -d'.' -f1   
    //free -m | grep "Mem" | awk  '{print $2}'
    public static String[] GetCommandStatus(int command) {
        String cmdappend = "";
        String[] cmd = {
            "/bin/sh",
            "-c",
            "" //will be filled by cmdappend
        };
        switch (command) {
            case COMMAND_GET_STATUS:
                cmdappend = "status";
                if (IoTAgent.osname.equalsIgnoreCase("Linux") && IoTAgent.osarch.equalsIgnoreCase("amd64")) {
                    cmdappend = "cat status";
                }
                break;
            case COMMAND_GET_VCPUS:
                cmdappend = "nproc --all";
                break;

            case COMMAND_GET_CPUSPEED:
                cmdappend = "lscpu | grep 'max MHz' | awk  '{print $4}' | cut -d'.' -f1 ";
                break;

            case COMMAND_GET_MEMORYSIZE:
                cmdappend = "free -m | grep 'Mem' | awk  '{print $2}' ";
                break;

            default:
                break;
        }//switch
        cmd[2] = cmdappend;
        return cmd;
    }

    public static String executeCommandSingleLineOutput(String[] command) {
        StringBuffer output = new StringBuffer();
        Process p;
        try {
            p = Runtime.getRuntime().exec(command);
            p.waitFor();
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            output.append(reader.readLine());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return output.toString();
    }//EoM        

    public static String executeCommandMultiLineOutput(String[] command) {
        StringBuffer output = new StringBuffer();
        Process p;
        try {
            p = Runtime.getRuntime().exec(command);
            p.waitFor();
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line = "";
            while ((line = reader.readLine()) != null) {
                output.append(line + "\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return output.toString();
    }//EoM    

    public static int getVCPUs() {
        int ret = 0;
        String output = executeCommandSingleLineOutput(GetCommandStatus(COMMAND_GET_VCPUS));
//        logger.info("|" + output + "|");
        ret = Integer.parseInt(output.trim());
        return ret;
    }//EoM

    public static int getCPUSpeed() {
        int ret = 0;
        String output = executeCommandSingleLineOutput(GetCommandStatus(COMMAND_GET_CPUSPEED));
//        logger.info("|" + output + "|");
        ret = Integer.parseInt(output.trim());
        return ret;
    }//EoM    

    public static int getMemorysize() {
        int ret = 0;
        String output = executeCommandSingleLineOutput(GetCommandStatus(COMMAND_GET_MEMORYSIZE));
//        logger.info("|" + output + "|");
        ret = Integer.parseInt(output.trim());
        return ret;
    }//EoM

    public static List<Peer> getNeighbors() {
        String output = executeCommandMultiLineOutput(GetCommandStatus(COMMAND_GET_STATUS));
        String nodeid = "";
        List<Peer> peers = new ArrayList<>();
        //get node metadata
        int pivot = output.indexOf("NODE") + 5;
        nodeid = output.substring(pivot, output.indexOf("\n", pivot));
//        logger.info("NodeId: " + nodeid);
        String remaining = output.substring(pivot + nodeid.length(), output.length());
        //logger.info("remaining: "+remaining);
        Pattern pattern = Pattern.compile("[a-fA-F0-9]{1,4}:[a-fA-F0-9]{1,4}:[a-fA-F0-9]{1,4}:[a-fA-F0-9]{1,4}:[a-fA-F0-9]{1,4}:[a-fA-F0-9]{1,4}:[a-fA-F0-9]{1,4}:[a-fA-F0-9]{1,4}");
        Matcher matcher = pattern.matcher(remaining);
        Date now = new Date();
        while (matcher.find()) {
            String peerstr = matcher.group();
            Peer peer = new Peer(nodeid, peerstr, now, IoTAgent.nodeid);
            if (!peers.contains(peer)) {
                peers.add(peer);
            } else {
                //Preventive policy for double peers
//                logger.info("Ignored peer: "+peer +" as double");
            }
        }//while
        return peers;
    }//EoM      

    public static Node getNodeInfo() {
        String output = executeCommandMultiLineOutput(GetCommandStatus(COMMAND_GET_STATUS));
        String nodeid = "";
        List<Peer> peers = new ArrayList<>();
        //get node metadata
        int pivot = output.indexOf("NODE") + 5;
        nodeid = output.substring(pivot, output.indexOf("\n", pivot));
        Node node = new Node(nodeid);
        if (IoTAgent.isGateway()) {
            node.setGateway(nodeid);
        }
        node.setOsarch(IoTAgent.osarch);
        node.setOsname(IoTAgent.osname);
        node.setBootdate(new Date());
        return node;
    }//EoM

    public static String invokeRest(String ipv6) {
        RestTemplate restTemplate = new RestTemplate();
        String url = "http://[" + ipv6 + "]:8080/api/v1/echo";
        String response = "";
        logger.info("Invoking service echo at " + ipv6);
        try {
            response = restTemplate.getForObject(url, String.class);
            logger.info("Response at " + ipv6);
        } catch (Exception ex) {
            logger.severe(url + " is unreachable");
        }
        return response;
    }//EoM

}//EoC
