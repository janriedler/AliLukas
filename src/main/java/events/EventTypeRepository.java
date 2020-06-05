package events;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;


@Repository
public class EventTypeRepository {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public EventTypeRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        if (Start.isNewStart()) {
            deleteAll();
            fetchEventTypes();
        }
    }

    class VeranstaltungRowMapper implements RowMapper<EventType> {

        @Override
        public EventType mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new EventType(rs.getString("ver_name"));
        }
    }

    public List <EventType> findAll() {
        return new ArrayList<>(jdbcTemplate.query(
                "select * from EventType", new VeranstaltungRowMapper()));
    }

    public List<EventType> findByName(String name) {
        return jdbcTemplate.query("SELECT * FROM EventType WHERE VER_NAME =? ",
                new Object[] { name }, new VeranstaltungRowMapper());
    }

    private int insert(EventType eventType) {
        return jdbcTemplate.update("insert into EventType " +
                        "(ver_name) " + "values(?)", eventType.getName());
    }

    private int deleteAll() {
        return jdbcTemplate.update( "delete from EventType");
    }

    private void fetchEventTypes() {
        List<EventType> eventTypes = Start.getEventTypes();
        for (EventType eventType: eventTypes) {
            insert(eventType);
        }
    }
}

