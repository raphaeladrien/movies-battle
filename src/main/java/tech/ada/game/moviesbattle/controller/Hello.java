package tech.ada.game.moviesbattle.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/movies-battle")
public class Hello {

    @GetMapping("/hello")
    public String hello() {
        return "say hello";
    }
}
