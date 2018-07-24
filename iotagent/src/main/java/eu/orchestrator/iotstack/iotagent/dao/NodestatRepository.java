package eu.orchestrator.iotstack.iotagent.dao;

import eu.orchestrator.iotstack.transfer.Nodestat;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
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
            return nodestat;
        }//mapRaw         
    }//EoC 

    public List<Nodestat> findAll() {
        return jdbcTemplate.query("select * from nodestat", new NodestatRowMapper());
    }//EoM
    
    public List<Nodestat> findById(String nodeid) {
        return jdbcTemplate.query("select * from nodestat where nodeid=?", new Object[]{nodeid},new NodestatRowMapper());
    }//EoM

    public int deleteById(String nodeid) {
        return jdbcTemplate.update("delete from nodestat where nodeid=?", new Object[]{nodeid});
    }//EoM

    public int insert(Nodestat nodestat) {
        return jdbcTemplate.update("insert into nodestat (nodeid,gateway,osarch,osname,bootdate,vcpus,cpuspeed,totalmemory, checkdate) " + "values(?,?,?,?,?,?,?,?,?)",
                new Object[]{nodestat.getNodeid(), nodestat.getGateway(), nodestat.getOsarch(), nodestat.getOsname(), nodestat.getBootdate(), nodestat.getVcpus(), nodestat.getCpuspeed(), nodestat.getTotalmemory(), nodestat.getCheckdate() });
    }//EoM

    public int update(Nodestat nodestat) {
        return jdbcTemplate.update("update nodestat " + " set nodeid = ?, gateway = ?, osarch = ?, osname = ?, bootdate = ?, checkdate = ? where nodeid = ?",
                new Object[]{nodestat.getNodeid() , nodestat.getGateway() , nodestat.getOsarch() , nodestat.getOsname() , nodestat.getBootdate() , nodestat.getCheckdate() , nodestat.getNodeid()});
    }//EoM

}//EoC