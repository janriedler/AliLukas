package events;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Die Klasse startet das Projekt (schaltet sozusagen die Listener an)
 * Der "Listener" der die Startseite (http://localhost:8080) abfängt ist in Controller -> Main
 */
@SpringBootApplication
public class Start {
    static List<Arten> arten = new ArrayList<>();
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    VeraJdbcRepository repository;
    public static void main(String[] args) throws IOException {
        setArten();
        SpringApplication.run(Start.class, args);
    }

    public static List<Arten> setArten() throws IOException {
        System.out.println("Geben Sie die gewünschten Arten für die Veranstaltungen an.");
        System.out.println("Wenn Sie fertig sind geben Sie bitte \"quit\" ein");
        BufferedReader inp = new BufferedReader(new InputStreamReader(System.in));
        String art = "";
        while (!art.equals("quit")){
            art = inp.readLine();
            Arten tmp = new Arten(art);
            if (!art.equals("quit")) arten.add(tmp);
        }
        return arten;
    }

    public static List<Arten> getArten() {
        return arten;
    }

}
