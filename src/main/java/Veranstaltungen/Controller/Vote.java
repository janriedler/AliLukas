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

@Controller
public class Vote {


    /**
     * die Startseite (verlist.html) hat post request hier hin gesendet. Dieser beinhalten das ein die Vera. ID und
     * das das ranking um eins eröht werden soll. Dies geschieht hier.
     */
    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST}, value = "voteUp")
    @ResponseBody
    public String up (@RequestParam String id, @RequestParam String ranking) {



        long tmp = Long.parseLong(id);
        repository.voteUp(tmp, ranking);

        return "<div>\n" +
                "    <a href=\"http://localhost:8080\">Startseite</a> <br><br><br>\n" +
                "</div>" +
                "Stimme abgeben";
    }

    /**
     * das gleiche wie ob bloß down vote
     */
    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST}, value = "voteDown")
    @ResponseBody
    public String down (@RequestParam String id, @RequestParam String ranking) {



        long tmp = Long.parseLong(id);
        System.out.println("Die Id sollte sein: " + tmp);
        repository.voteDown(tmp, ranking);

        return "<div>\n" +
                "    <a href=\"http://localhost:8080\">Startseite</a> <br><br><br>\n" +
                "</div>" +
                "Stimme abgeben";
    }



    @Autowired
    VeraJdbcRepository repository;
    public static void main(String[] args) {
        SpringApplication.run(Start.class, args);
    }





}
