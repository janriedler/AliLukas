package events.controller;

import events.Event;
import events.EventRepository;
import events.EventType;
import events.EventTypeRepository;
import events.Start;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.ArrayList;
import java.util.List;


@Controller
class Main {

    private EventRepository eventRepository;
    private EventTypeRepository eventTypeRepository;

    @Autowired
    public Main(EventRepository eventRepository, EventTypeRepository eventTypeRepository) {
        this.eventRepository = eventRepository;
        this.eventTypeRepository = eventTypeRepository;
    }

    public static void main(String[] args) {
        SpringApplication.run(Start.class, args);
    }

    /**
     * Der Listener fängt die Startseite "http://localhost:8080" ab und leitet weiter an resources -> templates -> verlist.html (Tymeleaf)
     * d.h es wird die Startseite angezeigt
     * Dazu wird eine Liste mit allen Veranstaltungen in der Zukunft erstellt, auf 20 Einträge reduziert und die dann der
     * Startseite mitübergeben
     *
     * Zudem wird auch eine Liste der top 3 Events (auch mit vergangenen) erstellt und mitübergeben
     *
     * Dazu wird Wetter wird hier auch geupdated (1 Zeile)
     */
    @GetMapping()
    public String showAll(HttpServletRequest request, Model model) {
        List<Event> events = eventRepository.findAllSort();
        doPreparations(request, events, model);
        return "verlist";
    }

    /**
     * Der Listener fängt "http://localhost:8080/add" ab und leitet weiter an resources -> templates -> add.html (Tymeleaf)
     * d.h es wird die add.html Seite angezeigt
     */
    @GetMapping("add")
    public String addEvent(Model model) {
        model.addAttribute("arten", eventTypeRepository.findAll());
        return "add";
    }

    /**
     * die Startseite (verlist.html) hat post request hier hin gesendet (http://localhost:8080/sort). Dieser beinhalten die gewünschte Art
     * nach der die Veranstaltung sotiert werden soll. Die wird dann hier genommen, eine entsprechende Liste erstellt
     * und dann wieder an die Startseite gesendet --> Jetzt wird dort nur noch Veranstaltungen von einer Art gezeigt
     */
    @RequestMapping("sort")
    public String ShowAllWithEventType(HttpServletRequest request, Model model, @RequestParam String sort) {
        List<Event> events;
        if (sort.equals("Alle (auch Vergangenheit)")) {
            events = eventRepository.findAllSort();
        } else {
            events = eventRepository.findByEventType(sort);
        }
        doPreparations(request, events, model);
        return "verlist";
    }

    /**
     * die Startseite (verlist.html) hat post request hier hin gesendet (http://localhost:8080/suche).
     * Dieser beinhalten einen geuschten String.
     * Dieser wird dann hier genommen, alle Einträge in DB (Name,Ort) überprüft ob diese die Suche entahlten ist und
     * dann eine entsprechende Liste erstellt
     * Dies wird dann wieder an die Startseite gesendet --> Jetzt wird dort nur noch Veranstaltungen gezeigt die die
     * Suche entahlten
     */
    @RequestMapping("suche")
    public String showAllWithSearch(HttpServletRequest request, Model model, @RequestParam String entry) {
        List<Event> searchedEvents = new ArrayList<>();
        for (Event Event : eventRepository.findAllSort()) {
            if (Event.getVer_name().toLowerCase().contains(entry.toLowerCase()) ||
                    Event.getOrt().toLowerCase().contains(entry.toLowerCase())) {
                searchedEvents.add(Event);
            }
        }
        doPreparations(request, searchedEvents, model);
        return "verlist";
    }

    @RequestMapping("vote")
    public String vote (HttpServletRequest request, HttpServletResponse response, @RequestParam String id, @RequestParam String ranking) {
        int value = Integer.parseInt(ranking);
        if (Math.abs(value) == 1) {
            value += votingChanged(request, id);

            long tmp = Long.parseLong(id);
            eventRepository.vote(tmp, value);
            response.addCookie(new Cookie(id, ranking));
        }
        return "voted";
    }

    @RequestMapping("mobile")
    public String showMobile(HttpServletRequest request, Model model) {
        List<Event> events = eventRepository.findAllSort();
        doPreparations(request, events, model);
        return "mobile";
    }


    @RequestMapping("event")
    public String showEvent(HttpServletRequest request, Model model, @RequestParam String id) {
        List<Event> events = new ArrayList<>();
        events.add(eventRepository.findById(Integer.parseInt(id)));
        doPreparations(request, events, model);
        return "event";
    }

    private void doPreparations(HttpServletRequest request, List<Event> events, Model model) {
        last20(events);
        setEventTypes(model);
        ArrayList<Long> upVote = new ArrayList<>();
        ArrayList<Long> downVote = new ArrayList<>();
        setUpDownVoteLists(request, upVote, downVote);

        model.addAttribute("upVote", upVote);
        model.addAttribute("downVote", downVote);
        model.addAttribute("top3", getTop3());
        model.addAttribute("veranstaltungen", events);
    }

    private void setUpDownVoteLists(HttpServletRequest request, ArrayList<Long> upVote, ArrayList<Long> downVote) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (int i = 0; i < cookies.length; i++) {
                Cookie cookie = cookies[i];
                if (cookie.getValue().equals("1")) {
                    upVote.add(Long.parseLong(cookie.getName()));
                } else {
                    downVote.add(Long.parseLong(cookie.getName()));
                }
            }
        }
    }

    private void setEventTypes(Model model) {
        List<EventType> eventTypes = eventTypeRepository.findAll();
        eventTypes.add(0, new EventType("Alle (auch Vergangenheit)"));
        model.addAttribute("arten", eventTypes);
    }

    /**
     * Fetches the old Voting and returns what needs to be undone.
     */
    private int votingChanged(HttpServletRequest request, String id) {
        Cookie[] cookies = request.getCookies();
        Cookie voted = null;
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(id)) {
                    voted = cookie;
                }
            }
        }
        if (voted != null) {
            int votedValue = Integer.parseInt(voted.getValue());
            if (Math.abs(votedValue) == 1) {
                return -1 * votedValue;
            }
        }
        return 0;
    }

    private List<Event> getTop3() {
        if (eventRepository.findAllSort().size() <= 3) {
            return eventRepository.findAllSort();
        }
        return eventRepository.findAllSort().subList(0, 3);
    }

    private void last20(List<Event> events) {
        while (events.size() > 20) {
            events.remove(0);
        }
    }
}
