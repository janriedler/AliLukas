package events.controller;

import events.repository.EventRepository;
import events.repository.Event;
import events.repository.EventType;
import events.repository.EventTypeRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
class Add {

    private EventRepository eventRepository;
    private EventTypeRepository eventTypeRepository;

    @Autowired
    public Add(EventRepository eventRepository, EventTypeRepository eventTypeRepository) {
        this.eventRepository = eventRepository;
        this.eventTypeRepository = eventTypeRepository;
    }

    /**
     * die add.html hat post request hier hin gesendet. Dieser beinhalten die Darten einer Vernastaltung
     * Diese Vera. wird hier dann erstellt (falls der Name nicht schon vorhanden ist) und dann an die DB gesendet.
     * Anschließend wird angezeigt das angezeigt gelaufen ist
     */
    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST}, value = "addVer")
    @ResponseBody
    public String getQuery (@RequestParam String name, @RequestParam String place, @RequestParam String description,
                            @RequestParam String eventType, @RequestParam String datum) {
        String checkOfDatum = checkDatum(datum);
        String checkOfName = checkName(name);
        String checkOfEventType = checkEventType(eventType);
        if (checkOfDatum != null) {
            return checkOfDatum;
        } else if (checkOfName != null) {
            return checkOfName;
        } else if (checkOfEventType != null){
            return checkOfEventType;
        } else {
            return insert(name, place, datum, description, eventType);
        }
    }

    private String checkDatum(String datum) {
        try {
            if (!EventRepository.checkDateIsInFuture(datum)) {
                return  "\"<script LANGUAGE='JavaScript'>\n" +
                        "    window.alert('Das Datum liegt in der Vergangenheit');\n" +
                        "    window.location.href='/add';\n" +
                        "    </script>\"" +
                        "Das Datum liegt in der Vergangenheit";
            }
        } catch (IllegalArgumentException e) {
            return  "\"<script LANGUAGE='JavaScript'>\n" +
                    "    window.alert('Das Datum ist illegal!');\n" +
                    "    window.location.href='/add';\n" +
                    "    </script>\"" +
                    "Das Datum ist illegal!";
        }
        return null;
    }

    private String checkName(String name) {
        if (eventRepository.findByName(name).size() != 0){
            return  "\"<script LANGUAGE='JavaScript'>\n" +
                    "    window.alert('Name schon vorhanden');\n" +
                    "    window.location.href='/add';\n" +
                    "    </script>\"" +
                    "Der Name ist leider schon vergeben";
        }
        return null;
    }

    private String checkEventType(String eventTypeName) {
        for (EventType eventType: eventTypeRepository.findAll()) {
            if (eventType.getName().equals(eventTypeName)) {
                return null;
            }
        }
        return "\"<script LANGUAGE='JavaScript'>\n" +
                "    window.alert('Illegaler Eventtyp!');\n" +
                "    window.location.href='/add';\n" +
                "    </script>\"" +
                "Illegaler Eventtyp!";
    }

    private String insert(String name, String place, String datum, String description, String eventType) {
        eventRepository.insert(new Event(name, place, datum, description, eventType));
        return "<script>\n" +
                " window.setTimeout(\"location.href='/';\", 0);\n" +
                "</script>" +
                "<div>\n" +
                "    <a href=\"http://localhost:8080\">Startseite</a> <br><br><br>\n" +
                "</div>" +
                "Die Veranstaltung wurde erfolgreich hinzugefügt";
    }
}
