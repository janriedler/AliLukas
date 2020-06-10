package events;

import events.data_access_layer.data_access_objects.Event;
import events.data_access_layer.repository.EventRepository;
import events.data_access_layer.data_access_objects.EventType;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.SpringApplication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class Start {

    private static List<EventType> eventTypes = new ArrayList<>();
    private static List<Event> events = new ArrayList<>();
    private static boolean newStart;

    public static void main(String[] args) throws IOException {
        BufferedReader bufferedReader
                = new BufferedReader(new InputStreamReader(System.in));
        if ((newStart = checkNewInitialisation(bufferedReader))) {
            setEventTypes(bufferedReader);
            setInitialisingEvents(bufferedReader);
        }
        SpringApplication.run(Start.class, args);
    }

    public Start() { }

    public static List<EventType> getEventTypes() {
        return eventTypes;
    }

    public static List<Event> getEvents() {
        return events;
    }

    public static boolean isNewStart() {
        return newStart;
    }

    private static boolean checkNewInitialisation(BufferedReader input) throws IOException {
        String answer;
        System.out.println("Soll die Datenbank neu intitalisiert werden (y/n)?");
        if ((answer = input.readLine()) != null) {
            if (answer.equals("y")) {
                return true;
            } else if (answer.equals("n")) {
                return false;
            }
        }
        return checkNewInitialisation(input);
    }

    private static void setEventTypes(BufferedReader input) throws IOException {
        String eventType;
        System.out.println("Geben Sie mind. einen gewünschten EventTypen" +
                " für die Veranstaltungen an.");
        System.out.println("Bestätigen Sie jeweils mit Enter und geben Sie" +
                " \"Quit\" ein um zu vollenden");
        while ((eventType = input.readLine()) != null
                && !eventType.equals("quit")) {
            if (!eventType.equals("")) {
                EventType tmp = new EventType(eventType);
                eventTypes.add(tmp);
            } else {
                System.out.println("EventType hat keinen Namen!");
            }
        }

        if (eventTypes.size() == 0) {
            System.out.println("Es wird mind. ein EventTyp benötigt!");
            setEventTypes(input);
        }
    }

    private static void setInitialisingEvents(BufferedReader input) throws IOException {
        String event;
        System.out.println("Geben Sie die gewünschten vorinitalisierten Events ein");
        System.out.println("Geben sie das Event wie folgt ein: <Name> <Ort>" +
                " <YYYY-DD-MM> <Beschreibung> <EventTyp>");
        System.out.println("Bestätigen Sie jeweils mit Enter und geben Sie" +
                " \"Quit\" ein um zu vollenden");
        while ((event = input.readLine()) != null
                && !event.equals("quit")){
            String[] attributes = event.split("\\s+");
            if (checkAttributes(attributes)) {
                Event tmp = new Event(attributes[0], attributes[1],
                        attributes[2], attributes[3], attributes[4]);
                events.add(tmp);
            } else {
                System.out.println("Die Parameter des Events passen nicht," +
                        " evtl liegt das Datum nicht in der Zukunft!");
            }
        }
    }

    private static boolean checkAttributes(String[] attributes) {
        if (attributes.length == 5) {
            boolean isDateLegalAndinFuture;
            try {
                isDateLegalAndinFuture = EventRepository.checkDateIsInFuture(attributes[2]);
            } catch (IllegalArgumentException e) {
                isDateLegalAndinFuture = false;
            }

            return (checkNameIsUnique(attributes[0]) && isDateLegalAndinFuture
                    && checkEventTypeExists(attributes[4]));
        }
        return false;
    }

    private static boolean checkNameIsUnique(String name) {
        for (Event event: events) {
            if (event.getVer_name().equals(name)) {
                return false;
            }
        }
        return true;
    }

    private static boolean checkEventTypeExists(String eventType) {
        for (EventType tmp: eventTypes) {
            if (tmp.getName().equals(eventType)) {
                return true;
            }
        }
        return false;
    }
}
