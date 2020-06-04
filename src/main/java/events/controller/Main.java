package events.controller;

import events.Arten;
import events.Start;
import events.VeraJdbcRepository;
import events.Event;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
class Main {

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
    public String showAll(HttpServletRequest request, Model model) throws ParseException {
        List<Arten> arten = new ArrayList<>(Start.getArten());
        Arten all = new Arten("Alle (auch Vergangenheit)");
        arten.add(all);
        model.addAttribute("arten", arten);

        repository.setWeatherTask();
        //Liste alle in Zukunft
        List<Event> ver = new ArrayList<>(repository.findAll());
        List<Event> future = new ArrayList<>();
        for (Event Event : ver) {
            if (checkFuture(Event.getDatum())) {
                future.add(Event);
            }
        }

        //Damit werden nur die letzt 20 Einträge wiedergegeben
        while (future.size() > 20) {
            future.remove(0);
        }

        Cookie[] cookies = request.getCookies();

            ArrayList<Long> upVote = new ArrayList<>();
            ArrayList<Long> downVote = new ArrayList<>();
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

        model.addAttribute("upVote", upVote);
        model.addAttribute("downVote", downVote);
        model.addAttribute("veranstaltungen", future);
        model.addAttribute("top3", getTop3());
        return "verlist";
    }

    /**
     * Der Listener fängt "http://localhost:8080/add" ab und leitet weiter an resources -> templates -> add.html (Tymeleaf)
     * d.h es wird die add.html Seite angezeigt
     */
    @GetMapping("add")
    public String form(Model model) {
        model.addAttribute("arten", Start.getArten());
        return "add";
    }

    /**
     * holt die selbstgeschrieben  VeraJdbcRepository Klasse, wodurch die dortigen Methoden für Datenbanken benutzt
     * benutzt werden können (z.B. repository.methode())
     */
    @Autowired
    VeraJdbcRepository repository;
    public static void main(String[] args) {
        SpringApplication.run(Start.class, args);
    }

    /**
     * die Startseite (verlist.html) hat post request hier hin gesendet (http://localhost:8080/sort). Dieser beinhalten die gewünschte Art
     * nach der die Veranstaltung sotiert werden soll. Die wird dann hier genommen, eine entsprechende Liste erstellt
     * und dann wieder an die Startseite gesendet --> Jetzt wird dort nur noch Veranstaltungen von einer Art gezeigt
     */
    @RequestMapping("sort")
    public String ShowAllWithCategoryFilter(Model model, @RequestParam String sort) {
        List<Arten> arten = new ArrayList<>(Start.getArten());
        Arten all = new Arten("Alle (auch Vergangenheit)");
        arten.add(all);
        model.addAttribute("arten", arten);
        repository.setWeatherTask();

        List<Event> ver = new ArrayList<>();
        if (sort.equals("Alle (auch Vergangenheit)")) {
            ver.addAll(repository.findAll());
            //Damit werden nur die letzt 20 Einträge wiedergegeben
        } else {
            ver.addAll(repository.findType(sort));
        }
        while (ver.size() > 20) {
            ver.remove(0);
        }
        model.addAttribute("veranstaltungen", ver);
        model.addAttribute("top3", getTop3());
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
    public String showAllWithSearch( Model model, @RequestParam String entry) {
        List<Arten> arten = new ArrayList<>(Start.getArten());
        Arten all = new Arten("Alle (auch Vergangenheit)");
        arten.add(all);
        model.addAttribute("arten", arten);

        repository.setWeatherTask();
        List<Event> verg = new ArrayList<>(repository.findAll());
        List<Event> su = new ArrayList<>();
        for (Event Event : verg) {
            if (Event.getVer_name().toLowerCase().contains(entry.toLowerCase()) ||
                    Event.getOrt().toLowerCase().contains(entry.toLowerCase())) {
                su.add(Event);
            }
        }
        model.addAttribute("top3", getTop3());
        model.addAttribute("veranstaltungen", su);
        return "verlist";
    }

    @RequestMapping("vote")
    public String vote (HttpServletRequest request, HttpServletResponse response, @RequestParam String id, @RequestParam String ranking) {
        int value = Integer.parseInt(ranking);
        value += votingChanged(request, id);

        long tmp = Long.parseLong(id);
        repository.vote(tmp, value);
        response.addCookie(new Cookie(id, ranking));
        return "voted";
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

    /**
     * Checks, if the date is in the future.
     */
    private boolean checkFuture(String pDateString) throws ParseException {
        Date date = new SimpleDateFormat("yyyy-MM-dd").parse(pDateString);
        return new Date().before(date);
    }

    /**
     * Find the top 3 upvoted events.
     */
    private List<Event> getTop3() {
        return new ArrayList<>(repository.findAll()).subList(0, 3);
    }
}
