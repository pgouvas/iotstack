package eu.orchestrator.iotstack.iotagent.dao;

import eu.orchestrator.iotstack.transfer.Node;
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
public class NodeRepository {

    @Autowired
    JdbcTemplate jdbcTemplate;

    class NodeRowMapper implements RowMapper<Node> {

        @Override
        public Node mapRow(ResultSet rs, int rowNum) throws SQLException {
            Node node = new Node();
            node.setId(rs.getString("id"));
            node.setGateway(rs.getString("gateway"));
            node.setOsarch(rs.getString("osarch"));
            node.setOsname(rs.getString("osname"));
            node.setBootdate(rs.getDate("bootdate"));
            node.setVcpus(rs.getInt("vcpus"));
            node.setCpuspeed(rs.getInt("cpuspeed"));
            node.setTotalmemory(rs.getInt("totalmemory"));       
            
            return node;
        }//mapRaw         
    }//EoC 

    public List<Node> findAll() {
        return jdbcTemplate.query("select * from node", new NodeRowMapper());
    }//EoM
    
    public Node findById(String id) {
        return jdbcTemplate.queryForObject("select * from node where id=?", new Object[]{id},
                new BeanPropertyRowMapper<Node>(Node.class));
    }//EoM

    public int deleteById(String id) {
        return jdbcTemplate.update("delete from node where id=?", new Object[]{id});
    }//EoM

    public int insert(Node node) {
        return jdbcTemplate.update("insert into node (id,gateway,osarch,osname,bootdate,vcpus,cpuspeed,totalmemory ) " + "values(?,?,?,?,?,?,?,?)",
                new Object[]{node.getId(), node.getGateway(), node.getOsarch(), node.getOsname(), node.getBootdate(), node.getVcpus(), node.getCpuspeed(), node.getTotalmemory() });
    }//EoM

    public int update(Node node) {
        return jdbcTemplate.update("update node " + " set id = ?, gateway = ?, osarch = ?, osname = ?, bootdate = ? where id = ?",
                new Object[]{node.getId() , node.getGateway() , node.getOsarch() , node.getOsname() , node.getBootdate() , node.getId() });
    }//EoM

}//EoC
