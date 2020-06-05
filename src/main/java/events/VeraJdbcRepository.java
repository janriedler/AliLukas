package events;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Repository
public class VeraJdbcRepository {

    @Autowired
    JdbcTemplate jdbcTemplate;

    private boolean weatherTaskIsSet;

    /**
     * selbst geschrieben Repository die die Methoden hat, die benötigt werden,
     * um mit der Datenbank zu arbeitet d.h. hier wird immer die Datenbank
     * geupdated
     */
    class VeranstaltungRowMapper implements RowMapper<Event> {

        @Override
        public Event mapRow(ResultSet rs, int rowNum) throws SQLException {
            Event vera = new Event();
            vera.setId(rs.getLong("id"));
            vera.setArt(rs.getString("art"));
            vera.setBeschreibung(rs.getString("beschreibung"));
            vera.setVer_name(rs.getString("ver_name"));
            vera.setOrt(rs.getString("ort"));
            vera.setDatum(rs.getString("datum"));
            vera.setRanking(rs.getString("rank"));
            vera.setWetter(rs.getString("wetter"));
            return vera;
        }

    }

    public List <Event> findAll() {
        return findAllSort();
    }

    private List <Event> findAllSort() {
        List<Event> old = new ArrayList<>(jdbcTemplate.query(
                "select * from Veranstaltung", new VeranstaltungRowMapper()));
        old.sort(Comparator.comparing(Event::getRankingInt).reversed());
        return old;
    }

    public List <Event> findType(String type) {
        List<Event> old = jdbcTemplate.query("SELECT * FROM VERANSTALTUNG WHERE ART =? ",
                new Object[] { type }, new VeranstaltungRowMapper());
        old.sort(Comparator.comparing(Event::getRankingInt).reversed());
        return old;
    }

    public List <Event> findName(String type) {
        List<Event> old = jdbcTemplate.query("SELECT * FROM VERANSTALTUNG WHERE VER_NAME =? ",
                new Object[] { type }, new VeranstaltungRowMapper());
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

    public int deleteALL() {
        return jdbcTemplate.update( "delete from VERANSTALTUNG");
    }



    public void setWeatherTask() {
        if (!weatherTaskIsSet) {
            Date date = new Date();
            Timer timer = new Timer();

            timer.schedule(new TimerTask() {
                public void run() {
                    System.out.println("UpdateWeatherTask is running at " + date);
                    updateWeather();
                }
            }, date, 60 * 60 * 1000);//60*60*1000 add 1 hour delay between jobs.
            weatherTaskIsSet = true;
        }
    }

    private void updateWeather() {
        List <Event> vera = new ArrayList<>(findAll());
        for (Event Event : vera) {
            if (checkTime(Event.getDatum())) {
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

    private boolean checkTime(String datum) {
        Date currentDate = new Date();
        DateFormat format = new SimpleDateFormat("yyyy-mm-dd", Locale.ENGLISH);
        Date datumDate;
        try {
            datumDate = format.parse(datum);
        } catch (ParseException e) {
            throw new IllegalArgumentException("Datum string is illegal!");
        }
        long difference = datumDate.getTime() - currentDate.getTime();
        return (difference <= 1000 * 60 * 60 * 24 * 7);
    }
}
