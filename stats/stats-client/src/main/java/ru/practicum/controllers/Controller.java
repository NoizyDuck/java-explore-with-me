package ru.practicum.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import ru.practicum.services.ClientService;

@RestController
public class Controller {
    @Autowired
    private ClientService clientService;

    @GetMapping("/{message}")
    public void get(@PathVariable String message) {
        clientService.get(message);
    }

}
