package eu.orchestrator.iotstack.iotagent.util;

import eu.orchestrator.iotstack.transfer.Node;
import eu.orchestrator.iotstack.transfer.Peer;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
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

    public static Node getNodeStatus() {
        String output = executeCommand("status");           //cat status
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
        while (matcher.find()) {
            String peerstr = matcher.group();
            Peer peer = new Peer(nodeid, peerstr, null);
            peers.add(peer);
        }
        Node nd = new Node(nodeid, peers);
        return nd;
    }//EoM

    public static String executeCommand(String command) {
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

    public static String invokeRest(String ipv6) {
        RestTemplate restTemplate = new RestTemplate();
        String url = "http://[" + ipv6 + "]:8080/api/v1/echo";
        logger.info("Invoking service echo at " + ipv6);
        String response = restTemplate.getForObject(url, String.class);
        logger.info("Response at " + ipv6);
        return response;
    }//EoM

}//EoC
