package eu.orchestrator.iotstack.iotagent.dao;

import eu.orchestrator.transfer.entities.iotstack.Nodestat;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Panagiotis Gouvas
 */
@Repository
public class NodestatRepository {

    @Autowired
    JdbcTemplate jdbcTemplate;

    class NodestatRowMapper implements RowMapper<Nodestat> {

        @Override
        public Nodestat mapRow(ResultSet rs, int rowNum) throws SQLException {
            Nodestat nodestat = new Nodestat();
            nodestat.setNodeid(rs.getString("nodeid"));
            nodestat.setGateway(rs.getString("gateway"));
            nodestat.setOsarch(rs.getString("osarch"));
            nodestat.setOsname(rs.getString("osname"));
            nodestat.setBootdate(rs.getDate("bootdate"));
            nodestat.setVcpus(rs.getInt("vcpus"));
            nodestat.setCpuspeed(rs.getInt("cpuspeed"));
            nodestat.setTotalmemory(rs.getInt("totalmemory"));
            nodestat.setCheckdate(rs.getDate("bootdate"));
            nodestat.setBandwith(rs.getString("bandwidth"));
            nodestat.setRttdelay(rs.getString("rttdelay"));
            nodestat.setPacketloss(rs.getString("packetloss")); 
            nodestat.setContainer(rs.getString("container")); 
            
            return nodestat;
        }//mapRaw         
    }//EoC 

    public List<Nodestat> findAll() {
        return jdbcTemplate.query("select * from nodestat", new NodestatRowMapper());
    }//EoM
    
    public List<Nodestat> findAllAvailable() {
        return jdbcTemplate.query("select * from nodestat where container is null", new NodestatRowMapper());
    }//EoM    
    
    public List<Nodestat> findById(String nodeid) {
        return jdbcTemplate.query("select * from nodestat where nodeid=?", new Object[]{nodeid}, new NodestatRowMapper());
    }//EoM
    
    public List<Nodestat> findByContainer(String container) {
        return jdbcTemplate.query("select * from nodestat where container=?", new Object[]{container}, new NodestatRowMapper());
    }//EoM    

    public int deleteById(String nodeid) {
        return jdbcTemplate.update("delete from nodestat where nodeid=?", new Object[]{nodeid});
    }//EoM

    public int insert(Nodestat nodestat) {
        return jdbcTemplate.update("insert into nodestat (nodeid,gateway,osarch,osname,bootdate,vcpus,cpuspeed,totalmemory, checkdate,container) " + "values(?,?,?,?,?,?,?,?,?,?)",
                new Object[]{nodestat.getNodeid(), nodestat.getGateway(), nodestat.getOsarch(), nodestat.getOsname(), nodestat.getBootdate(), nodestat.getVcpus(), nodestat.getCpuspeed(), nodestat.getTotalmemory(), nodestat.getCheckdate() , nodestat.getContainer() });
    }//EoM

    public int updateRemote(Nodestat nodestat) {
        return jdbcTemplate.update("update nodestat " + " set nodeid = ?, gateway = ?, osarch = ?, osname = ?, bootdate = ?, checkdate = ?  where nodeid = ?",
                new Object[]{nodestat.getNodeid(), nodestat.getGateway(), nodestat.getOsarch(), nodestat.getOsname(), nodestat.getBootdate(), nodestat.getCheckdate(), nodestat.getNodeid()});
    }//EoM

    public int updateLocalRttdelay(String value, String nodeid) {
        return jdbcTemplate.update("update nodestat " + " set  rttdelay = ?  where nodeid = ?",
                new Object[]{ value,  nodeid});
    }//EoM
    
    public int updateLocalBandwidth(String value, String nodeid) {
        return jdbcTemplate.update("update nodestat " + " set  bandwidth = ?  where nodeid = ?",
                new Object[]{ value,  nodeid });
    }//EoM    
    
    public int updateLocalPacketloss(String value, String nodeid) {
        return jdbcTemplate.update("update nodestat " + " set  packetloss = ?  where nodeid = ?",
                new Object[]{  value,  nodeid });
    }//EoM    
       
    public int updateLocalContainer(String value, String nodeid) {
        return jdbcTemplate.update("update nodestat " + " set  container = ?  where nodeid = ?",
                new Object[]{  value,  nodeid });
    }//EoM        
       
    public int clearContainer(String containerid) {
        return jdbcTemplate.update("update nodestat " + " set  container = null  where container = ?",
                new Object[]{  containerid });
    }//EoM            
    
    class IntRowMapper implements RowMapper<Integer> {

        @Override
        public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
            return rs.getInt(1);

        }//mapRaw         
    }//EoC     

    public Integer getMaxVCPUs() { //select sum(vcpus) from nodestat
        return jdbcTemplate.query("select sum(vcpus) from nodestat", new IntRowMapper()).get(0);
    }//EoM

    public Integer getMaxRam() {
        return jdbcTemplate.query("select sum(totalmemory) from nodestat", new IntRowMapper()).get(0);
    }//EoM

    public Integer getMaxInstances() {
        return jdbcTemplate.query("select count(*) from nodestat", new IntRowMapper()).get(0);
    }//EoM    
    
    
    public Integer getRunningInstances() {
        return jdbcTemplate.query("SELECT count(*) FROM NODESTAT where container is not null", new IntRowMapper()).get(0);
    }//EoM      
    
    
    public Integer getUsedRam() {
        return jdbcTemplate.query("SELECT sum(totalmemory) FROM NODESTAT where container is not null", new IntRowMapper()).get(0);
    }

    public Integer getUsedVCpus() {
        return jdbcTemplate.query("SELECT sum(vcpus) FROM NODESTAT where container is not null", new IntRowMapper()).get(0);
    }    
    
}//EoC
