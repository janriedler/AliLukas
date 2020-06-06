package events.repository;

import events.Start;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

@Repository
public class EventRepository {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public EventRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        if (Start.isNewStart()) {
            deleteAll();
            fetchEvents();
        }
        setWeatherTask();
    }

    class VeranstaltungRowMapper implements RowMapper<Event> {

        @Override
        public Event mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new Event(rs.getLong("id"), rs.getString("ver_name"),
                    rs.getString("place"), rs.getString("datum"),
                    rs.getString("description"), rs.getString("eventType"),
                    rs.getString("weather"), rs.getString("rank"));
        }
    }

    public List <Event> findAllSort() {
        List<Event> old = new ArrayList<>(jdbcTemplate.query(
                "select * from Event", new VeranstaltungRowMapper()));
        old.sort(Comparator.comparing(Event::getRankingInt).reversed());
        return old;
    }

    public List <Event> findByEventType(String eventType) {
        List<Event> old = jdbcTemplate.query("SELECT * FROM Event WHERE eventType =? ",
                new Object[] { eventType }, new VeranstaltungRowMapper());
        old.sort(Comparator.comparing(Event::getRankingInt).reversed());
        return old;
    }

    public List<Event> findByName(String name) {
        List<Event> old = jdbcTemplate.query("SELECT * FROM Event WHERE VER_NAME =? ",
                new Object[] { name }, new VeranstaltungRowMapper());
        old.sort(Comparator.comparing(Event::getRankingInt).reversed());
        return old;
    }

    public Event findById(long id) {
        return jdbcTemplate.queryForObject("select * from Event where id=?",
                new Object[] { id }, new VeranstaltungRowMapper());
    }

    public int insert(Event vera) {
        return jdbcTemplate.update("insert into Event " +
                        "(id, ver_name, place, datum, description, eventType, rank, weather) "
                        + "values(?,  ?, ?, ?, ?,?, ?, ?)",
                vera.getId(), vera.getVer_name(), vera.getPlace(), vera.getDatum(), vera.getDescription(),
                vera.getEventType(), vera.getRank(), vera.getWeather());
    }

    private int deleteAll() {
        return jdbcTemplate.update( "delete from Event");
    }

    public int vote(long id, int vote) {
        Event event = findById(id);
        int rank = event.getRankingInt() + vote;

        return jdbcTemplate.update("UPDATE Event\n" +
                "        SET rank = ?" +
                "        WHERE id = ?", rank, id);
    }

    private int updateWetterValue(Long id , String data) {
        return jdbcTemplate.update("UPDATE Event\n" +
                "        SET weather = ?\n" +
                "        WHERE id = ?", data, id);
    }

    private void fetchEvents() {
        List<Event> events = Start.getEvents();
        for (Event event: events) {
            insert(event);
        }
    }

    private void setWeatherTask() {
        Date date = new Date();
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                System.out.println("UpdateWeatherTask is running at " + date
                + " with intervall of 1 hour");
                updateWeather();
            }
        }, date, 60 * 60 * 1000);
    }

    private void updateWeather() {
        List <Event> events = findAllSort();
        for (Event Event : events) {
            if (datumIsInOneWeek(Event.getDatum())) {
                String id = JsonWeatherAPI.getWoeid(Event.getPlace());
                String weather;
                if (id != null && (weather = JsonWeatherAPI.getWeather(id, Event.getDatum())) != null) {
                    Event.setWeather(weather);
                    updateWetterValue(Event.getId(), weather);
                } else {
                    Event.setWeather("Ort nicht vorhanden");
                    updateWetterValue(Event.getId(), "Ort nicht vorhanden");
                }
            } else {
                Event.setWeather("Zu weit entfernt");
                updateWetterValue(Event.getId(), "Zu weit entfernt");
            }
        }
    }

    private boolean datumIsInOneWeek(String datum) {
        Date dateInOneWeek = new Date(new Date().getTime()
                + 1000 * 60 * 60 * 24 * 7);
        try {
            Date date = new SimpleDateFormat("yyyy-dd-MM").parse(datum);
            return (dateInOneWeek.getTime() - date.getTime() > 0);
        } catch (ParseException e) {
            throw new IllegalArgumentException("Datum ist illegal!");
        }
    }

    public static boolean checkDateIsInFuture(String datum) {
        Date date;
        try {
            date = new SimpleDateFormat("yyyy-dd-MM").parse(datum);
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            Date todayWithZeroTime = formatter.parse(formatter.format(new Date()));
            return (todayWithZeroTime.before(date) || todayWithZeroTime.equals(date));
        } catch (ParseException e) {
            throw new IllegalArgumentException("Datum ist illegal!");
        }
    }
}
