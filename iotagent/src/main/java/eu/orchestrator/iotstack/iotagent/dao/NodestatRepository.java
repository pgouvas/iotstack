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
            
            return nodestat;
        }//mapRaw         
    }//EoC 

    public List<Nodestat> findAll() {
        return jdbcTemplate.query("select * from nodestat", new NodestatRowMapper());
    }//EoM

    public List<Nodestat> findById(String nodeid) {
        return jdbcTemplate.query("select * from nodestat where nodeid=?", new Object[]{nodeid}, new NodestatRowMapper());
    }//EoM

    public int deleteById(String nodeid) {
        return jdbcTemplate.update("delete from nodestat where nodeid=?", new Object[]{nodeid});
    }//EoM

    public int insert(Nodestat nodestat) {
        return jdbcTemplate.update("insert into nodestat (nodeid,gateway,osarch,osname,bootdate,vcpus,cpuspeed,totalmemory, checkdate,bandwidth,rttdelay,packetloss) " + "values(?,?,?,?,?,?,?,?,?,?,?,?)",
                new Object[]{nodestat.getNodeid(), nodestat.getGateway(), nodestat.getOsarch(), nodestat.getOsname(), nodestat.getBootdate(), nodestat.getVcpus(), nodestat.getCpuspeed(), nodestat.getTotalmemory(), nodestat.getCheckdate() , nodestat.getBandwith(), nodestat.getRttdelay(), nodestat.getPacketloss() });
    }//EoM

    public int update(Nodestat nodestat) {
        return jdbcTemplate.update("update nodestat " + " set nodeid = ?, gateway = ?, osarch = ?, osname = ?, bootdate = ?, checkdate = ? , bandwidth = ? , rttdelay = ? , packetloss = ?   where nodeid = ?",
                new Object[]{nodestat.getNodeid(), nodestat.getGateway(), nodestat.getOsarch(), nodestat.getOsname(), nodestat.getBootdate(), nodestat.getCheckdate(), nodestat.getBandwith(), nodestat.getRttdelay(), nodestat.getPacketloss(), nodestat.getNodeid()});
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

}//EoC
