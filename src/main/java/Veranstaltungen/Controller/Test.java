package Veranstaltungen.Controller;

import Veranstaltungen.Start;
import Veranstaltungen.VeraJdbcRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;

public class Test {
    @Autowired
    static VeraJdbcRepository repository;
    public static void main(String[] args) {
        SpringApplication.run(Start.class, args);
    }




}
