package events.controller;

import com.google.gson.Gson;
import events.Event;
import events.Start;
import events.VeraJdbcRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

@Controller
public class RESTapi {
    @GetMapping("events")
    @ResponseBody
    public String json(@RequestParam String n) {
        List<Event> events = new ArrayList<>(repository.findAll());
        while (events.size() > Integer.parseInt(n)) {
            events.remove(0);
        }

        String json = new Gson().toJson(events);
        return json;
    }

    @Autowired
    VeraJdbcRepository repository;
    public static void main(String[] args) {
        SpringApplication.run(Start.class, args);
    }


}
