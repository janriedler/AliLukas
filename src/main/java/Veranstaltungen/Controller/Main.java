package Veranstaltungen.Controller;

import Veranstaltungen.Start;
import Veranstaltungen.VeraJdbcRepository;
import Veranstaltungen.Veranstaltung;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class Main {
    /**
     * Der Listener fängt die Startseite "http://localhost:8080" ab und leitet weiter an resources -> templates -> verlist.html (Tymeleaf)
     * d.h es wird die Startseite angezeigt
     * Dazu wird eine Liste mit allen Veranstaltungen in der Zukunft erstellt, auf 20 Einträge reduziert und die dann der
     * Startseite mitübergeben
     *
     * Zudem wird auch eine Liste der top 3 Events (auch mit vergangen) erstellt und mitübergeben
     *
     * Dazu wird Wetter wird hier auch geupdatet (1 Zeile)
     */
    @GetMapping()
    public String showAll(Model model) throws IOException, ParseException {
        repository.updateWetter();

        //Liste alle in Zukunft
        List<Veranstaltung> ver = new ArrayList<>(repository.findAll());
        List<Veranstaltung> future = new ArrayList<>();
        for (Veranstaltung veranstaltung : ver) {
            if (checkFuture(veranstaltung.getDatum())) {
                future.add(veranstaltung);
            }
        }
        while (ver.size() > 20) {
            ver.remove(0);
        }
        model.addAttribute("veranstaltungen", future);


        //liste Top3
        List<Veranstaltung> tmp= new ArrayList<>(repository.findAll());
        List<Veranstaltung> top= new ArrayList<>();
        if (tmp.size() > 0) top.add(tmp.get(0));
        if (tmp.size() > 1) top.add(tmp.get(1));
        if (tmp.size() > 2) top.add(tmp.get(2));
        model.addAttribute("top3", top);
        return "verlist";
    }


    /**
     * Der Listener fängt "http://localhost:8080/add" ab und leitet weiter an resources -> templates -> add.html (Tymeleaf)
     * d.h es wird die add.html Seite angezeigt
     */
    @GetMapping("add")
    public String form() {
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
     * die Startseite (verlist.html) hat post request hier hin gesendet. Dieser beinhalten die gewünschte Art
     * nach der die Veranstaltung sotiert werden soll. Die wird dann hier genommen, eine entsprechende Liste erstellt
     * und dann wieder an die Startseite gesendet --> Jetzt wird dort nur noch Veranstaltungen von einer Art gezeigt
     */
    @RequestMapping("sort")
    public String showAll2(Model model, @RequestParam String sort) throws IOException {
        repository.updateWetter();
        List<Veranstaltung> ver = new ArrayList<>();
        if (sort.equals("Alle (auch Vergangenheit)")) {
            ver.addAll(repository.findAll());
            while (ver.size() > 20) { //noch nicht getestest
                ver.remove(0);
            }
            model.addAttribute("veranstaltungen", ver);
            return "verlist";
        }
        ver.addAll(repository.findType(sort));
        while (ver.size() > 20) {
            ver.remove(0);
        }
        model.addAttribute("veranstaltungen", ver);
        return "verlist";
    }


    /**
     * die Startseite (verlist.html) hat post request hier hin gesendet. Dieser beinhalten eine Suche
     * Die wird dann hier genommen, alle Einträge in DB (Name,Ort) überprüft ob diese die Suche entahlten ist und
     * dann eine entsprechende Liste erstellt
     * Dies wird dann wieder an die Startseite gesendet --> Jetzt wird dort nur noch Veranstaltungen gezeigt die die
     * Suche entahlten
     */
    @RequestMapping("suche")
    public String showAll3(Model model, @RequestParam String entry) throws IOException {
        repository.updateWetter();
        List<Veranstaltung> verg= new ArrayList<>(repository.findAll());
        List<Veranstaltung> su = new ArrayList<>();
        for (int i = 0; i < verg.size(); i++) {
            if (verg.get(i).getVer_name().toLowerCase().contains(entry.toLowerCase()) ||
                    verg.get(i).getOrt().toLowerCase().contains(entry.toLowerCase())) {
                su.add(verg.get(i));
            }
        }

        model.addAttribute("veranstaltungen", su);
        return "verlist";
    }


    //prüft ob Datum in Zukunft liegt
    public boolean checkFuture(String pDateString) throws ParseException {
        Date date = new SimpleDateFormat("yyyy-MM-dd").parse(pDateString);
        return new Date().before(date);
    }


    /**
     * die Startseite (verlist.html) hat post request hier hin gesendet. Dieser beinhalten das ein die Vera. ID und
     * das das ranking um eins eröht werden soll. Dies geschieht hier.
     */
    @RequestMapping("voteUp")
    public String up (Model model, @RequestParam String id, @RequestParam String ranking) throws ParseException, IOException {

        long tmp = Long.parseLong(id);
        repository.voteUp(tmp, ranking);
        return showAll(model);
    }

    /**
     * prinzip wie oben
     */
    @RequestMapping("voteDown")
    public String down (Model model, @RequestParam String id, @RequestParam String ranking) throws ParseException, IOException {

        long tmp = Long.parseLong(id);
        repository.voteDown(tmp, ranking);
        return showAll(model);
    }






}
