package eu.orchestrator.iotstack.iotagent;

import eu.orchestrator.iotstack.iotagent.dao.NodeRepository;
import eu.orchestrator.iotstack.iotagent.util.Util;
import eu.orchestrator.iotstack.transfer.Node;
import java.util.concurrent.Executor;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
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
        //check that NodeID is already configured
//        Node node = Util.getNodeInfo();
//        noderepo.insert(node);            
    }//EoM

    @PostConstruct
    public void init() {
        Node node = Util.getNodeInfo();
        nodeid = node.getId();
        noderepo.insert(node);         
        logger.info("Initialization Finished!");
    }//EoM

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
