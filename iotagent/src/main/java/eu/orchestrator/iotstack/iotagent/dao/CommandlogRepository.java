package eu.orchestrator.iotstack.iotagent.dao;

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
public class CommandlogRepository {

    @Autowired
    JdbcTemplate jdbcTemplate;

    class CommandlogRowMapper implements RowMapper<Commandlog> {

        @Override
        public Commandlog mapRow(ResultSet rs, int rowNum) throws SQLException {
            Commandlog clog = new Commandlog();
            clog.setCid(rs.getString("cid"));
            clog.setCdate(rs.getDate("cdate"));

            return clog;
        }//mapRaw         
    }//EoC 

    public List<Commandlog> findAll() {
        return jdbcTemplate.query("select * from commandlog", new CommandlogRowMapper());
    }//EoM

    public Commandlog findById(String id) {
        return jdbcTemplate.queryForObject("select * from commandlog where id=?", new Object[]{id},
                new BeanPropertyRowMapper<Commandlog>(Commandlog.class));
    }//EoM

    public int deleteById(String id) {
        return jdbcTemplate.update("delete from commandlog where id=?", new Object[]{id});
    }//EoM

    public int insert(Commandlog clog) {
        return jdbcTemplate.update("insert into commandlog (cid,cdate) " + "values(?,?)",
                new Object[]{clog.getCid(), clog.getCdate()});
    }//EoM

}//EoC
