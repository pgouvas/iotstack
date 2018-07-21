package eu.orchestrator.iotstack.iotagent;

import eu.orchestrator.iotstack.iotagent.dao.NodeRepository;
import eu.orchestrator.iotstack.iotagent.util.Util;
import eu.orchestrator.iotstack.transfer.Node;
import java.util.concurrent.Executor;
import java.util.logging.Level;
import java.util.logging.Logger;
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
    
    //It represents the current profile. it can be clusterhead or node  i.e. 
    //    mvn spring-boot:run -Dspring-boot.run.profiles=clusterhead
    //    java -jar iotagent-1.0-SNAPSHOT-spring-boot.jar --spring.profiles.active=clusterhead
    
    public static String activeProfile;
    //The node identifier of the Agent
    public static String nodeid;
    //The configured Clusterhead
    public String clusterhead;
    
    @Autowired
    NodeRepository noderepo;
    
    public static void main(String[] args) {
        SpringApplication.run(IoTAgent.class, args);
        logger.info("PROFILE:  "+ activeProfile);
        
        //check profile
        if (activeProfile==null || !(activeProfile.equalsIgnoreCase("clusterhead") || activeProfile.equalsIgnoreCase("node")) ) {
            logger.log(Level.SEVERE,"Agent requires a specific profile e.g. mvn spring-boot:run -Dspring-boot.run.profiles=clusterhead/node");
            System.exit(0);
        }        
        //check that NodeID is already configured
        Node node = Util.getNodeStatus();
        //noderepo.insert(node);
        logger.info(node.getId());        
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
    public void setActiveProfile(String activeprofile){
        activeProfile = activeprofile;
    }
    
    public static boolean isClusterHead(){
        return IoTAgent.activeProfile.equalsIgnoreCase("clusterhead");
    }
    
}//EoC
