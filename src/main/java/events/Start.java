package events;

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
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        if ((newStart = checkNewInitialisation(bufferedReader))) {
            setEventTypes(bufferedReader);
            setInitialisingEvents(bufferedReader);
        }
        SpringApplication.run(Start.class, args);
    }

    Start() { }

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
        if ((answer = input.readLine()).equals("y")) {
            return true;
        } else if (answer.equals("n")) {
            return false;
        }
        return checkNewInitialisation(input);
    }

    private static void setEventTypes(BufferedReader input) throws IOException {
        String eventType;

        System.out.println("Geben Sie die gewünschten EventTypen für die Veranstaltungen an.");
        System.out.println("Bestätigen Sie jeweils mit Enter und geben Sie \"Quit\" ein um zu vollenden");
        while (!(eventType = input.readLine()).equals("quit")){
            EventType tmp = new EventType(eventType);
            eventTypes.add(tmp);
        }
    }

    private static void setInitialisingEvents(BufferedReader input) throws IOException {
        String event;

        System.out.println("Geben Sie die gewünschten vorinitalisierten Events ein");
        System.out.println("Geben sie das Event wie folgt ein: <Name> <Ort> <YYYY-DD-MM> <Beschreibung> <EventTyp>");
        System.out.println("Bestätigen Sie jeweils mit Enter und geben Sie \"Quit\" ein um zu vollenden");
        while (!(event = input.readLine()).equals("quit")){
            String[] attributes = event.split("\\s+");
            if (checkAttributes(attributes)) {
                Event tmp = new Event(attributes[0], attributes[1],
                        attributes[2], attributes[3], attributes[4]);
                events.add(tmp);
            } else {
                System.out.println("Die Parameter des Events passen nicht!");
            }
        }
    }

    private static boolean checkAttributes(String[] attributes) {
        return (attributes.length == 5 && checkNameIsUnique(attributes[0])
                && EventRepository.checkDateIsInFuture(attributes[2])
            && checkEventTypeExists(attributes[4]));
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
