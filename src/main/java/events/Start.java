package events;

import events.controller.RESTapi;
import org.eclipse.jdt.internal.compiler.batch.Main;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLDeleteAll;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;

import java.io.*;
import java.lang.management.ManagementFactory;
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
        if (restart()) {
            init();
        }
        setArten();
        SpringApplication.run(Start.class, args);
    }

    private static boolean restart() throws IOException {
        BufferedReader brTest = new BufferedReader(new FileReader("src/main/resources/restart.txt"));
        String text = brTest .readLine();
        if (text != null) {
            File mFile3 = new File("src/main/resources/restart.txt");
            FileInputStream fis3 = new FileInputStream(mFile3);
            BufferedReader br3 = new BufferedReader(new InputStreamReader(fis3));
            String result3 = "";
            mFile3.delete();

            FileOutputStream fos3 = new FileOutputStream(mFile3);
            fos3.write(result3.getBytes());
            fos3.flush();
            return false;
        }else return true;
    }

    public static void init() throws IOException {
        System.out.println("Wollen sie die DB mit 2 Dummy Einräge NEU initialisieren ? (y/N)");
        BufferedReader inp = new BufferedReader(new InputStreamReader(System.in));
        File mFile = new File("src/main/resources/schema.sql");
        FileInputStream fis = new FileInputStream(mFile);
        BufferedReader br = new BufferedReader(new InputStreamReader(fis));
        String result = "";
        String eing = inp.readLine();
        if (eing.equals("y")) {

            result = "drop table veranstaltung; CREATE TABLE IF NOT EXISTS Veranstaltung (" +
                    "  id INT AUTO_INCREMENT  PRIMARY KEY,  ver_name VARCHAR(250) NOT NULL,  ort VARCHAR(250) NOT NULL,  " +
                    "datum VARCHAR(250) NOT NULL,  art VARCHAR(250) NOT NULL,  rank VARCHAR(250) NOT NULL,  " +
                    "wetter VARCHAR(250) NOT NULL,  beschreibung VARCHAR(250) DEFAULT NULL);";
        } else {
            result = "CREATE TABLE IF NOT EXISTS Veranstaltung (" +
                    "  id INT AUTO_INCREMENT  PRIMARY KEY,  ver_name VARCHAR(250) NOT NULL,  ort VARCHAR(250) NOT NULL,  " +
                    "datum VARCHAR(250) NOT NULL,  art VARCHAR(250) NOT NULL,  rank VARCHAR(250) NOT NULL,  " +
                    "wetter VARCHAR(250) NOT NULL,  beschreibung VARCHAR(250) DEFAULT NULL);";
        }
            mFile.delete();
            FileOutputStream fos = new FileOutputStream(mFile);
            fos.write(result.getBytes());
            fos.flush();

            File f = new File("src/main/resources/data.sql");

            f.getParentFile().mkdirs();
            f.createNewFile();
            File mFile2 = new File("src/main/resources/data.sql");
            FileInputStream fis2 = new FileInputStream(mFile2);
            BufferedReader br2 = new BufferedReader(new InputStreamReader(fis2));
            String result2 = "";
            result2 = "INSERT INTO VERANSTALTUNG (VER_NAME,ORT,DATUM,ART,RANK, WETTER, BESCHREIBUNG) VALUES ('Dummy','Dummy','2020-09-10','Dummy','1','Dummy','Dummy');" +
                    "INSERT INTO VERANSTALTUNG (VER_NAME,ORT,DATUM,ART,RANK, WETTER, BESCHREIBUNG) VALUES ('Dummy2','Dummy2','2020-09-10','Dummy2','2','Dummy2','Dummy2');";
            mFile2.delete();
            if (eing.equals("y")) {
                FileOutputStream fos2 = new FileOutputStream(mFile2);
                fos2.write(result2.getBytes());
                fos2.flush();
            }
            File mFile3 = new File("src/main/resources/restart.txt");
            FileInputStream fis3 = new FileInputStream(mFile3);
            BufferedReader br3 = new BufferedReader(new InputStreamReader(fis3));
            String result3 = "restart";
            mFile3.delete();

            FileOutputStream fos3 = new FileOutputStream(mFile3);
            fos3.write(result3.getBytes());
            fos3.flush();

            System.out.println("Datenbank wurde geladen. Start Sie das Programm nochmal");
            System.exit(0);

    }






    private static List<Arten> setArten() throws IOException {
        System.out.println("Geben Sie die gewünschten Arten für die Veranstaltungen an.");
        System.out.println("Bestätigen Sie jeweils mit Enter und geben Sie \"Quit\" ein um zu vollenden");
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
