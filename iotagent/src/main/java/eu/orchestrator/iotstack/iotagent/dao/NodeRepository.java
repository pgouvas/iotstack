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
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

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
            node.setLastupdate(rs.getDate("lastupdate"));
            return node;
        }//mapRaw         
    }//EoC 

    public List<Node> findAll() {
        return jdbcTemplate.query("select * from node", new NodeRowMapper());
    }//EoM

    public Node findById(long id) {
        return jdbcTemplate.queryForObject("select * from node where id=?", new Object[]{id},
                new BeanPropertyRowMapper<Node>(Node.class));
    }

    public int deleteById(long id) {
        return jdbcTemplate.update("delete from node where id=?", new Object[]{id});
    }

//    @Transactional
    public int insert(Node node) {
        return jdbcTemplate.update("insert into node (id,lastupdate) " + "values(?,?)",
                new Object[]{node.getId(), node.getLastupdate()});
    }

    public int update(Node node) {
        return jdbcTemplate.update("update node " + " set id = ? where id = ?",
                new Object[]{node.getId()});
    }

}//EoC
