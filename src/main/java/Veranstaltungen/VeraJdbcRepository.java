package Veranstaltungen;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Repository
public class VeraJdbcRepository {
    @Autowired
    JdbcTemplate jdbcTemplate;


    /**
     * selbst geschrieben Repository die die Methoden hat, die benötigt werden um mit der Datenbank zu arbeitet
     * d.h. hier wird immer die Datenbank geupdated
     */

    class VeranstaltungRowMapper implements RowMapper < Veranstaltung > {

        @Override
        public Veranstaltung mapRow(ResultSet rs, int rowNum) throws SQLException {
            Veranstaltung vera = new Veranstaltung();
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
    //gibt liste aller Einträge soriert nach ranking zurück
    public List < Veranstaltung > findAll() {
        return findAllSort();

    }

    //sotiert Liste nach Ranking
    public List < Veranstaltung > findAllSort() {
        List<Veranstaltung> old = new ArrayList<>(
                jdbcTemplate.query("select * from Veranstaltung", new VeranstaltungRowMapper()));
        old.sort(Comparator.comparing(Veranstaltung::getRankingInt).reversed());
        return old;
    }

    //gibt alle Einträge zurück die den String "type" in der Spalte "art" enthalten sind
    public List < Veranstaltung > findType(String type) {
        List<Veranstaltung> old = jdbcTemplate.query("SELECT * FROM VERANSTALTUNG WHERE ART =? ", new Object[] {
                type
        },
                new VeranstaltungRowMapper());

        old.sort(Comparator.comparing(Veranstaltung::getRankingInt).reversed());
        return old;

    }
    //das selbe mit der Spalte "ver_name"
    public List < Veranstaltung > findName(String type) {
        List<Veranstaltung> old = jdbcTemplate.query("SELECT * FROM VERANSTALTUNG WHERE VER_NAME =? ", new Object[] {
                type
        },
                new VeranstaltungRowMapper());

        old.sort(Comparator.comparing(Veranstaltung::getRankingInt).reversed());
        return old;

    }

    //rest sollte logisch seint
    public Veranstaltung findById(long id) {
        return jdbcTemplate.queryForObject("select * from Veranstaltung where id=?", new Object[] {
                    id
                },
                new BeanPropertyRowMapper <> (Veranstaltung.class));
    }


    public int deleteById(long id) {
        return jdbcTemplate.update("delete from Veranstaltung where id=?", id);
    }

    public int insert(Veranstaltung vera) {
        return jdbcTemplate.update("insert into Veranstaltung (id, ver_name, ort, datum, beschreibung, art, rank, wetter) "
                        + "values(?,  ?, ?, ?, ?,?, ?, ?)",

                vera.getId(), vera.getVer_name(), vera.getOrt(), vera.getDatum(), vera.getBeschreibung(),
                vera.getArt(), vera.getRanking(), vera.getWetter());
    }



    public int voteUp(long id, String ranking) {

        int tmp = Integer.parseInt(ranking);
        tmp++;
        String erg = String.valueOf(tmp);
        return jdbcTemplate.update("UPDATE Veranstaltung\n" +
                "        SET RANK = ?\n" +
                "        WHERE ID = ?", erg, id);

    }


    public int voteDown(long id, String ranking) {

        int tmp = Integer.parseInt(ranking);
        tmp--;
        String erg = String.valueOf(tmp);
        return jdbcTemplate.update("UPDATE Veranstaltung\n" +
                "        SET RANK = ?\n" +
                "        WHERE ID = ?", erg, id);

    }

    /**
     * hierüber müssten wir noch reden
     */
    public void updateWetter() {
        List <Veranstaltung> vera = new ArrayList<>(findAll());
        for (Veranstaltung veranstaltung : vera) {
            if (veranstaltung.getWetter().equals("unbekannt")) {
                if (JsonFormat.checkTime(veranstaltung.getDatum())) { //wenn Zeit inner nächster Woche
                    String id = JsonFormat.getWoeid(veranstaltung.getOrt()); //mit Klasse JsonFormat wird die ID von einem Ort bestimmt
                    if (!id.equals("noloc")) { //loloc wird von JsonFormat zurückgegen wenn der Ort nicht gefunden wurde
                        String wet = JsonFormat.getWetter(id, veranstaltung.getDatum());
                        veranstaltung.setWetter(wet); //mit Klasse JsonFormat wird mittels ID und Datum das Wetter bestimmt
                        System.out.println("Id: " + veranstaltung.getId() + " der String: " + wet);
                        updateWetterValue(veranstaltung.getId(), wet);
                    } else {
                        veranstaltung.setWetter("Ort nicht vorhanden");
                        updateWetterValue(veranstaltung.getId(), "Ort nicht vorhanden");
                    }
                } else {
                    veranstaltung.setWetter("zu weit entfernt");
                    updateWetterValue(veranstaltung.getId(), "zu weit entfernt");
                }
            } else if (veranstaltung.getWetter().equals("zu weit entfernt")) {
                if (JsonFormat.checkTime(veranstaltung.getDatum())) { //wenn Zeit inner nächster Woche
                    String id = JsonFormat.getWoeid(veranstaltung.getOrt()); //mit Klasse JsonFormat wird die ID von einem Ort bestimmt
                    if (!id.equals("noloc")) { //loloc wird von JsonFormat zurückgegen wenn der Ort nicht gefunden wurde
                        String wet = JsonFormat.getWetter(id, veranstaltung.getDatum());
                        veranstaltung.setWetter(wet); //mit Klasse JsonFormat wird mittels ID und Datum das Wetter bestimmt
                        updateWetterValue(veranstaltung.getId(), wet);
                    } else {
                        veranstaltung.setWetter("Ort nicht vorhanden");
                        updateWetterValue(veranstaltung.getId(), "Ort nicht vorhanden");
                    }

                }
            }
        }

    }

    public int updateWetterValue(Long id , String data) {
        return jdbcTemplate.update("UPDATE Veranstaltung\n" +
                "        SET WETTER = ?\n" +
                "        WHERE ID = ?", data, id);

    }








}
