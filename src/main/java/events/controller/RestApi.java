package events.controller;

import com.google.gson.Gson;

import events.data_access_layer.data_access_objects.Event;
import events.data_access_layer.repository.EventRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class RestApi {

    private EventRepository repository;

    @Autowired
    public RestApi(EventRepository repository) {
        this.repository = repository;
    }

    @GetMapping("events")
    @ResponseBody
    public String json(@RequestParam String sizeString) {
        List<Event> events = repository.findAllSort();
        int size = Integer.parseInt(sizeString);
        while (events.size() > size) {
            events.remove(0);
        }
        return new Gson().toJson(events);
    }
}
