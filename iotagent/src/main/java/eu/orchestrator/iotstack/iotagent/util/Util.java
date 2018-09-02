package eu.orchestrator.iotstack.iotagent.util;

import eu.orchestrator.iotstack.iotagent.IoTAgent;
import eu.orchestrator.transfer.entities.iotstack.CommandBroadcastUpdateGateway;
import eu.orchestrator.transfer.entities.iotstack.Node;
import eu.orchestrator.transfer.entities.iotstack.Peer;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
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
    private static final int INITIATE_IPERF3 = 5;
    private static final int KILL_IPERF3 = 6;

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

            //5201 port    
            case INITIATE_IPERF3:
                cmdappend = "iperf3 -s -D";
                break;

            case KILL_IPERF3:
                cmdappend = "ps -ef | grep iperf3 | awk '{print $2}' | xargs kill -9";
                break;

            default:
                break;
        }//switch
        cmd[2] = cmdappend;
        return cmd;
    }//GetCommandStatus

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

    public static void shutdownConsul() {
        String[] cmd = {
            "/bin/sh",
            "-c",
            "" //will be filled by cmdappend
        };
        //1 - Stop Consul
        String cmdappend = "sudo service consul stop";
        cmd[2] = cmdappend;
        String output = executeCommandMultiLineOutput(cmd);
    } //EoM    

    public static void setupHosts(String nexusIPv6, String masterIPv6) {
        String[] cmd = {
            "/bin/sh",
            "-c",
            "" //will be filled by cmdappend
        };
        //1 - Stop Consul
        String cmdappend = "sudo sed -i '/nexus/d' /etc/hosts";
        cmd[2] = cmdappend;
        String output = executeCommandMultiLineOutput(cmd);

        cmdappend = "sudo sed -i '/master/d' /etc/hosts";
        cmd[2] = cmdappend;
        output = executeCommandMultiLineOutput(cmd);

        cmdappend = "sudo echo \"" + nexusIPv6 + " nexus \" >> /etc/hosts";
        cmd[2] = cmdappend;
        output = executeCommandMultiLineOutput(cmd);

        cmdappend = "sudo echo \"" + masterIPv6 + " master \" >> /etc/hosts";
        cmd[2] = cmdappend;
        output = executeCommandMultiLineOutput(cmd);

        logger.info("SetupHosts completed ");
    }//EoM      

    public static void setupDocker() {
        String[] cmd = {
            "/bin/sh",
            "-c",
            "" //will be filled by cmdappend
        };
        //1 - Stop Consul
        String cmdappend = "sudo service docker stop";
        cmd[2] = cmdappend;
        String output = executeCommandMultiLineOutput(cmd);

        String template = "{\n"
                + " \"insecure-registries\" : [\n"
                + "       \"nexus:39580\",\n"
                + "       \"nexus:34522\"\n"
                + "       ],\n"
                + "       \"ipv6\": true,\n"
                + "         \"fixed-cidr-v6\": \"2001:db8::/122\"\n"
                + "}";

        try {
            FileWriter fileWriter = new FileWriter("/etc/docker/daemon.json");
            fileWriter.write(template);
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException ex) {
            Logger.getLogger(Util.class.getName()).log(Level.SEVERE, null, ex);
        }

        //Start consul
        cmdappend = "sudo service docker start";
        cmd[2] = cmdappend;
        output = executeCommandMultiLineOutput(cmd);
        logger.info("Docker Configured!");
    }//EoM    

    public static void setupConsul(String masterip, String privateip, String graphidlower, String graphinstanceidlower, String componentnodeidlower, String componentnodeinstanceidlower) {
        String[] cmd = {
            "/bin/sh",
            "-c",
            "" //will be filled by cmdappend
        };
        //1 - Stop Consul
        String cmdappend = "sudo service consul stop";
        cmd[2] = cmdappend;
        String output = executeCommandMultiLineOutput(cmd);

        String nodename = graphidlower + "-" + graphinstanceidlower + "-" + componentnodeidlower + "-" + componentnodeinstanceidlower;
        String template = "#!/bin/bash\n"
                + "mkdir -p /tmp/consul\n"
                + "privateIP=" + privateip + "\n"
                + "masterIP=" + masterip + "\n"
                + "sudo /opt/consul agent -join=$masterIP -data-dir=/tmp/consul -bind=$privateIP -node=" + nodename + " -advertise=$privateIP -enable-script-checks=true -client=0.0.0.0 -config-dir=/etc/consul.d";

        try {
            FileWriter fileWriter = new FileWriter("/opt/scripts/consul-start.sh");
            fileWriter.write(template);
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException ex) {
            Logger.getLogger(Util.class.getName()).log(Level.SEVERE, null, ex);
        }

        //Start consul
        cmdappend = "sudo service consul start";
        cmd[2] = cmdappend;
        output = executeCommandMultiLineOutput(cmd);
        logger.info("Consul Start output: " + output);
        logger.info("Consul Configured!");
    }//EoM

    public static void setupNetdata(String graphidlower, String graphinstanceidlower, String componentnodeidlower) {
        String[] cmd = {
            "/bin/sh",
            "-c",
            "" //will be filled by cmdappend
        };
        //1 - Stop Consul
        String cmdappend = "sudo service netdata stop";
        cmd[2] = cmdappend;
        String output = executeCommandMultiLineOutput(cmd);
        logger.info("Netdata Stop output: " + output);

        //2 - change config
        cmdappend = "sudo sed -i -e \"s/\\[backend\\]/\\[backend\\]\\nprefix=netdata:" + graphidlower + ":" + graphinstanceidlower + ":" + componentnodeidlower + "/g\" /opt/netdata/etc/netdata/netdata.conf";
        cmd[2] = cmdappend;
        output = executeCommandMultiLineOutput(cmd);

        //3 - Start consul
        cmdappend = "sudo service netdata start";
        cmd[2] = cmdappend;
        output = executeCommandMultiLineOutput(cmd);
        logger.info("Netdata Start output: " + output);
    }//EoM    

    public static String initiateIPerf3D() {
        String output = executeCommandSingleLineOutput(GetCommandStatus(INITIATE_IPERF3));
//        logger.info("|" + output + "|");
        return output;
    }

    //TODO fix restart
    public static String killPerf3D() {
        String output = executeCommandSingleLineOutput(GetCommandStatus(KILL_IPERF3));
//        logger.info("|" + output + "|");
        return output;
    }

    public static String measureBandwith(String nodeid) {
        String cmdappend = "iperf3 -c " + nodeid + " -t 5  -i 0 -f m  -P 2   | grep SUM | grep sender | awk '{print $6}'";
        String[] cmd = {
            "/bin/sh",
            "-c",
            "" //will be filled by cmdappend
        };
        cmd[2] = cmdappend;
        String output = executeCommandMultiLineOutput(cmd);
        return output.trim();
    }//measureBandwith    

    public static String measureRTTDelay(String nodeid) {
        String cmdappend = "ping6 " + nodeid + " -c 5 | grep rtt | cut -d/ -f5";
        String[] cmd = {
            "/bin/sh",
            "-c",
            "" //will be filled by cmdappend
        };
        cmd[2] = cmdappend;
        String output = executeCommandMultiLineOutput(cmd);
        return output.trim();
    }//measureRTTDelay

    public static String measurePacketLoss(String nodeid) {
        String cmdappend = "ping6 " + nodeid + " -c 5 | grep loss | cut -d, -f3 | cut -d% -f1";
        String[] cmd = {
            "/bin/sh",
            "-c",
            "" //will be filled by cmdappend
        };
        cmd[2] = cmdappend;
        String output = executeCommandMultiLineOutput(cmd);
        return output.trim();
    }//measurePacketLoss    

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

    public static boolean invokeRestGetNodeId(String ipv6) {
        boolean ret = false;
        RestTemplate restTemplate = new RestTemplate();
        String url = "http://[" + ipv6 + "]:8080/api/v1/nodeid";
        String response = "";
        logger.info("Invoking service getNodeId at " + ipv6);
        try {
            response = restTemplate.getForObject(url, String.class);
            if (response.equalsIgnoreCase(response)) {
                ret = true;
            }
            logger.info("Service " + ipv6 + " active");
        } catch (Exception ex) {
            logger.info("Peer to "+ipv6 +" was considered active but invokeRestGetNodeId failed."); //TODO
            ret = false;
        }
        return ret;
    }//EoM    

    public static void notifyAdjacentNodesForGateway(CommandBroadcastUpdateGateway cug, String targetid) {
        logger.info("Notifying notifyAdjacentNodesForGateway " + targetid + "  for gateway: " + cug.getGatewayid());
        RestTemplate restTemplate = new RestTemplate();
        String url = "http://[" + targetid + "]:8080/api/v1/gateway";
        try {
            restTemplate.postForObject(url, cug, String.class);
        } catch (Exception ex) {
            logger.severe(ex.getMessage());
        }
    }//EoM    

}//EoC
