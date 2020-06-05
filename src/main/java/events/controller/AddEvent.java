package events.controller;

import events.Start;
import events.EventRepository;
import events.Event;

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
class AddEvent {

    private EventRepository repository;

    @Autowired
    public AddEvent(EventRepository repository) {
        this.repository = repository;
    }

    public static void main(String[] args) {
        SpringApplication.run(Start.class, args);
    }

    /**
     * die add.html hat post request hier hin gesendet. Dieser beinhalten die Darten einer Vernastaltung
     * Diese Vera. wird hier dann erstellt (falls der Name nicht schon vorhanden ist) und dann an die DB gesendet.
     * Anschließend wird angezeigt das angezeigt gelaufen ist
     */
    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST}, value = "addVer")
    @ResponseBody
    public String getQuery (@RequestParam String name, @RequestParam String ort, @RequestParam String beschreibung,
                            @RequestParam String art, @RequestParam String datum) {
        List<Event> validate = new ArrayList<>(repository.findByName(name));
        if (validate.size() != 0) {
            return  "\"<script LANGUAGE='JavaScript'>\n" +
                    "    window.alert('Name schon vorhanden');\n" +
                    "    window.location.href='/add';\n" +
                    "    </script>\"" +
                    "Die Name ist leider schon vergeben";
        }
        Event neu = new Event(name, ort, datum, beschreibung, art);
        repository.insert(neu);
        return "<script>\n" +
                " window.setTimeout(\"location.href='/';\", 0);\n" +
                "</script>" +
                "<div>\n" +
                "    <a href=\"http://localhost:8080\">Startseite</a> <br><br><br>\n" +
                "</div>" +
                "Die Veranstaltung wurde erfolgreich hinzugefügt";

    }
}
