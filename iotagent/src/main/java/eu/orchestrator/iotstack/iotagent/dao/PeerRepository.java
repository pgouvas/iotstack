package eu.orchestrator.iotstack.iotagent.dao;

import eu.orchestrator.transfer.entities.iotstack.Node;
import eu.orchestrator.transfer.entities.iotstack.Peer;
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
            peer.setReportingnode(rs.getString("reportingnode"));
            peer.setIsactive(rs.getBoolean("isactive"));
            return peer;
        }//mapRaw         
    }//EoC 

    public List<Peer> findAll() {
        return jdbcTemplate.query("select * from peer", new PeerRowMapper());
    }//EoM

    public List<Peer> findAllActive() {
        return jdbcTemplate.query("select * from peer where isactive=true", new PeerRowMapper());
    }//EoM    
    
    public List<Peer> findById(String fromnode, String tonode) {
        return jdbcTemplate.query("select * from peer where (fromnode=? and tonode=?) or (fromnode=? and tonode=?)", new Object[]{fromnode, tonode, tonode, fromnode},
                new PeerRowMapper());
    }

    public int deleteById(String fromnode, String tonode) {
        return jdbcTemplate.update("delete from peer where fromnode=? and tonode=?", new Object[]{fromnode, tonode});
    }

    public void updateStatus(String fromnode, String tonode, boolean isactive) {
        jdbcTemplate.update("update peer " + " set  isactive = ?  where fromnode=? and tonode=? ", new Object[]{ isactive,  fromnode, tonode });
        jdbcTemplate.update("update peer " + " set  isactive = ?  where fromnode=? and tonode=? ", new Object[]{ isactive,  tonode, fromnode });        
    }//EoM    
    
    public int insert(Peer peer) {
        return jdbcTemplate.update("insert into peer (fromnode,tonode,registrationdate,reportingnode,isactive) " + "values(?,?,?,?,?)",
                new Object[]{peer.getFromnode(), peer.getTonode(), peer.getRegistrationdate(), peer.getReportingnode(), peer.isIsactive()});
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
    
    public List<Node> getAdjacentActiveNodes(String fromnode) {
        return jdbcTemplate.query("select distinct tonode from peer where fromnode = ? and isactive=true", new Object[]{fromnode}, new PeerNodeRowMapper());
    }

//    public List<Node> getAllPublishedNodes() {
//        return jdbcTemplate.query("select distinct * from (select tonode from peer union select fromnode from peer) ", new PeerNodeRowMapper());
//    }    
    public List<Node> getAllActiveAnnouncedPeers() {
        return jdbcTemplate.query("select distinct * from (select tonode from peer where isactive=true union select fromnode from peer where isactive=true) ", new PeerNodeRowMapper());
    }

}//EoC
