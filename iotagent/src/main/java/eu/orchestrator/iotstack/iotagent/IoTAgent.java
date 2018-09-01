package eu.orchestrator.iotstack.iotagent;

import eu.orchestrator.iotstack.iotagent.dao.NodeRepository;
import eu.orchestrator.iotstack.iotagent.util.Util;
import eu.orchestrator.transfer.entities.iotstack.Node;
import java.util.concurrent.Executor;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 *
 * @author Panagiotis Gouvas
 */
@EnableAsync
@EnableScheduling
@SpringBootApplication
public class IoTAgent {

    //TODO implement credentials management
    //TODO implement get resources
    //TODO implement cleaner of commandlog-cleaner
    //TODO implement reporterid to peer
    //TODO implement gateway keepalive listener
    private static final Logger logger = Logger.getLogger(IoTAgent.class.getName());

    //  The profile should be provided during the boot of the microservice
    //  mvn spring-boot:run -Dspring-boot.run.profiles=gateway/node
    //  java -jar iotagent-1.0-SNAPSHOT-spring-boot.jar --spring.profiles.active=gateway/node
    public static final String osname = System.getProperty("os.name");
    public static final String osarch = System.getProperty("os.arch");

    public static String activeProfile;
    //The node identifier of the Agent
    public static String nodeid;
    //The configured Gateway
    public String gateway;

    @Autowired
    NodeRepository noderepo;

    public static void main(String[] args) {
        SpringApplication.run(IoTAgent.class, args);
        logger.info("nodeid: " + nodeid + " profile:  " + activeProfile + " osname: " + osname + " osarch: " + osarch);

        //check profile
        if (activeProfile == null || !(activeProfile.equalsIgnoreCase("gateway") || activeProfile.equalsIgnoreCase("node"))) {
            logger.log(Level.SEVERE, "Agent requires a specific profile e.g. mvn spring-boot:run -Dspring-boot.run.profiles=gateway/node");
            System.exit(0);
        }
    }//EoM

    @PostConstruct
    public void init() {
        Node node = Util.getNodeInfo();
        nodeid = node.getId();
        int vcpus = Util.getVCPUs();
        int cpuspeed = Util.getCPUSpeed();
        int totalmemory = Util.getMemorysize();
        node.setVcpus(vcpus);
        node.setCpuspeed(cpuspeed);
        node.setTotalmemory(totalmemory);
        //persist to database
        noderepo.insert(node);
        //shutdown consul if running
        Util.shutdownConsul();
        //initialize ip3rf3 Daemon
        Util.killPerf3D();
        Util.initiateIPerf3D();
        logger.info("Initialization finished for " + nodeid + " VCPUs: " + vcpus + " cpuspeed: " + cpuspeed + " totalmemory: " + totalmemory);
    }//EoM

    @PreDestroy
    private void ondestroy(){
        //TODO implement destruction of iperf3daemon
    }
    
    @Bean
    public Executor asyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(500);
        executor.setThreadNamePrefix("GithubLookup-");
        executor.initialize();
        return executor;
    }

    @Value("${spring.profiles.active}")
    public void setActiveProfile(String activeprofile) {
        activeProfile = activeprofile;
    }

    public static boolean isGateway() {
        return IoTAgent.activeProfile.equalsIgnoreCase("gateway");
    }

}//EoC
