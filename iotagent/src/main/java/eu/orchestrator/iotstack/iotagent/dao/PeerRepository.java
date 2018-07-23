package eu.orchestrator.iotstack.iotagent.dao;

import eu.orchestrator.iotstack.transfer.Node;
import eu.orchestrator.iotstack.transfer.Peer;
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
public class PeerRepository {

    @Autowired
    JdbcTemplate jdbcTemplate;

    class PeerRowMapper implements RowMapper<Peer> {

        @Override
        public Peer mapRow(ResultSet rs, int rowNum) throws SQLException {
            Peer peer = new Peer();
            peer.setFromnode(rs.getString("fromnode"));
            peer.setTonode(rs.getString("tonode"));
            peer.setRegistrationdate(rs.getDate("registrationdate"));
            return peer;
        }//mapRaw         
    }//EoC 

    public List<Peer> findAll() {
        return jdbcTemplate.query("select * from peer", new PeerRowMapper());
    }//EoM

    public Peer findById(String fromnode, String tonode) {
        return jdbcTemplate.queryForObject("select * from peer where fromnode=? and tonode=?", new Object[]{fromnode, tonode},
                new BeanPropertyRowMapper<Peer>(Peer.class));
    }

    public int deleteById(String fromnode, String tonode) {
        return jdbcTemplate.update("delete from peer where fromnode=? and tonode=?", new Object[]{fromnode, tonode});
    }

    public int insert(Peer peer) {
        return jdbcTemplate.update("insert into peer (fromnode,tonode,registrationdate) " + "values(?,?,?)",
                new Object[]{peer.getFromnode(), peer.getTonode(), peer.getRegistrationdate()});
    }

    class PeerNodeRowMapper implements RowMapper<Node> {

        @Override
        public Node mapRow(ResultSet rs, int rowNum) throws SQLException {
            Node node = new Node();
            node.setId(rs.getString("tonode"));
            return node;
        }//mapRaw         
    }//EoC     

    public List<Node> getAdjacentNodes(String fromnode) {
        return jdbcTemplate.query("select distinct tonode from peer where fromnode = ?", new Object[]{fromnode}, new PeerNodeRowMapper());
    }

}//EoC
