package events;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Locale;
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
            Event event = new Event(rs.getLong("id"), rs.getString("ver_name"),
                    rs.getString("ort"), rs.getString("datum"),
                    rs.getString("beschreibung"), rs.getString("art"));
            event.setRanking(rs.getString("rank"));
            event.setWetter(rs.getString("wetter"));
            return event;
        }
    }

    public List <Event> findAllSort() {
        List<Event> old = new ArrayList<>(jdbcTemplate.query(
                "select * from Veranstaltung", new VeranstaltungRowMapper()));
        old.sort(Comparator.comparing(Event::getRankingInt).reversed());
        return old;
    }

    public List <Event> findByEventType(String eventType) {
        List<Event> old = jdbcTemplate.query("SELECT * FROM VERANSTALTUNG WHERE ART =? ",
                new Object[] { eventType }, new VeranstaltungRowMapper());
        old.sort(Comparator.comparing(Event::getRankingInt).reversed());
        return old;
    }

    public List<Event> findByName(String name) {
        List<Event> old = jdbcTemplate.query("SELECT * FROM VERANSTALTUNG WHERE VER_NAME =? ",
                new Object[] { name }, new VeranstaltungRowMapper());
        old.sort(Comparator.comparing(Event::getRankingInt).reversed());
        return old;
    }

    public Event findById(long id) {
        return jdbcTemplate.queryForObject("select * from Veranstaltung where id=?",
                new Object[] { id }, new VeranstaltungRowMapper());
    }

    public int deleteById(long id) {
        return jdbcTemplate.update("delete from Veranstaltung where id=?", id);
    }

    public int insert(Event vera) {
        return jdbcTemplate.update("insert into Veranstaltung " +
                        "(id, ver_name, ort, datum, beschreibung, art, rank, wetter) "
                        + "values(?,  ?, ?, ?, ?,?, ?, ?)",
                vera.getId(), vera.getVer_name(), vera.getOrt(), vera.getDatum(), vera.getBeschreibung(),
                vera.getArt(), vera.getRanking(), vera.getWetter());
    }

    public int vote(long id, int vote) {
        Event event = findById(id);
        int ranking = event.getRankingInt() + vote;

        return jdbcTemplate.update("UPDATE Veranstaltung\n" +
                "        SET RANK = ?" +
                "        WHERE ID = ?", ranking, id);
    }

    private int updateWetterValue(Long id , String data) {
        return jdbcTemplate.update("UPDATE Veranstaltung\n" +
                "        SET WETTER = ?\n" +
                "        WHERE ID = ?", data, id);
    }

    private int deleteAll() {
        return jdbcTemplate.update( "delete from VERANSTALTUNG");
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
                System.out.println("UpdateWeatherTask is running at " + date);
                updateWeather();
            }
        }, date, 60 * 60 * 1000);
    }

    private void updateWeather() {
        List <Event> events = findAllSort();
        for (Event Event : events) {
            if (datumIsInOneWeek(Event.getDatum())) {
                String id = JsonWeatherAPI.getWoeid(Event.getOrt());
                if (id != null) {
                    String weather = JsonWeatherAPI.getWeather(id, Event.getDatum());
                    if (weather != null) {
                        Event.setWetter(weather);
                        System.out.println("Id: " + Event.getId() + " der String: " + weather);
                        updateWetterValue(Event.getId(), weather);
                    } else {
                        Event.setWetter("Ort nicht vorhanden");
                        updateWetterValue(Event.getId(), "Ort nicht vorhanden");
                    }
                } else {
                    Event.setWetter("Ort nicht vorhanden");
                    updateWetterValue(Event.getId(), "Ort nicht vorhanden");
                }
            } else {
                Event.setWetter("Zu weit entfernt");
                updateWetterValue(Event.getId(), "Zu weit entfernt");
            }
        }
    }

    private boolean datumIsInOneWeek(String datum) {
        Date currentDate = new Date();
        DateFormat format = new SimpleDateFormat("yyyy-dd-MM", Locale.ENGLISH);
        Date datumDate;
        try {
            datumDate = format.parse(datum);
        } catch (ParseException e) {
            throw new IllegalArgumentException("Datum string is illegal!");
        }
        long difference = datumDate.getTime() - currentDate.getTime();
        return (difference <= 1000 * 60 * 60 * 24 * 7);
    }

    public static boolean checkDateIsInFuture(String datum) {
        Date date;
        try {
            date = new SimpleDateFormat("yyyy-MM-dd").parse(datum);
            return new Date().before(date);
        } catch (ParseException e) {
            throw new IllegalArgumentException("datum is illegal!");
        }
    }
}
