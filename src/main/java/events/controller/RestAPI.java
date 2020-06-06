package events.controller;

import com.google.gson.Gson;

import events.Event;
import events.Start;
import events.EventRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class RestAPI {

    private EventRepository repository;

    @Autowired
    public RestAPI(EventRepository repository) {
        this.repository = repository;
    }

    @GetMapping("events")
    @ResponseBody
    public String json(@RequestParam String n) {
        List<Event> events = repository.findAllSort();
        int size = Integer.parseInt(n);
        while (events.size() > size) {
            events.remove(0);
        }
        return new Gson().toJson(events);
    }

    public static void main(String[] args) {
        SpringApplication.run(Start.class, args);
    }
}
