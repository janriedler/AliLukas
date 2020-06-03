/**
 * Die Klasse startet das Projekt (schaltet sozusagen die Listener an)
 * Der "Listener" der die Startseite (http://localhost:8080) abfängt ist in Controller -> Main
 */

package Veranstaltungen;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;


@SpringBootApplication
public class Start {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    VeraJdbcRepository repository;
    public static void main(String[] args) {
        SpringApplication.run(Start.class, args);
    }

}
