package Veranstaltungen.Controller;

import Veranstaltungen.Start;
import Veranstaltungen.VeraJdbcRepository;
import Veranstaltungen.Veranstaltung;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

@Controller
public class AddDatabase {

    private String name;
    private String ort;
    private String beschreibung;
    private String datum;
    private String art;

    /**
     * die add.html hat post request hier hin gesendet. Dieser beinhalten die Darten einer Vernastaltung
     * Diese Vera. wird hier dann erstellt (falls der Name nicht schon vorhanden ist) und dann an die DB gesendet.
     * Anschließend wird angezeigt das angezeigt gelaufen ist
     */
    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST}, value = "addVer")
    @ResponseBody
    public String getQuery (@RequestParam String name, @RequestParam String ort, @RequestParam String beschreibung,
                            @RequestParam String art, @RequestParam String datum) {
        this.name = name;
        this.ort = ort;
        this.beschreibung = beschreibung;
        this.art = art;
        this.datum = datum;

        List<Veranstaltung> validate = new ArrayList<>(repository.findName(name));
        if (validate.size() != 0) {
            return "<div>\n" +
                    "    <a href=\"http://localhost:8080\">Startseite</a> <br><br><br>\n" +
                    "</div>" +
                    "Die Name ist leider schon vergeben";
        }
        insert();
        return "<div>\n" +
                "    <a href=\"http://localhost:8080\">Startseite</a> <br><br><br>\n" +
                "</div>" +
                "Die Veranstaltung wurde erfolgreich hinzugefügt";
    }

    /**
     * holt die selbstgeschrieben  VeraJdbcRepository Klasse, wodurch die dortigen Methoden für Datenbanken abfragen
     * verwendet werden können
     */
    @Autowired
    VeraJdbcRepository repository;
    public static void main(String[] args) {
        SpringApplication.run(Start.class, args);
    }


    //logisch
    private void insert() {
        Veranstaltung neu = new Veranstaltung(name, ort, datum, beschreibung, art);
        repository.insert(neu);
    }




}
