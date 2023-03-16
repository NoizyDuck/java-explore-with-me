package ru.practicum.controllers;

import org.springframework.web.bind.annotation.*;

@RestController
public class StatController {

    @GetMapping("/{message}")
    public void getter(@PathVariable String message){
        System.out.println(message);
    }

    @PostMapping("/hit")
    public void hit(@RequestBody HitRequestDto hitRequestDto){

    }
}
