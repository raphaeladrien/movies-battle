package tech.ada.game.moviesbattle.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.RepresentationModel;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.ada.game.moviesbattle.interactor.StartGame;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/movies-battle/game")
public class GameController {

    @Autowired
    private StartGame startGame;

    @PostMapping("/start")
    public ResponseEntity<StartResponse> start() {
        final UUID id = startGame.call();
        final URI location = ServletUriComponentsBuilder.fromCurrentContextPath()
            .path("/movies-battle/game/{id}/round")
            .buildAndExpand(id)
            .toUri();

        return ResponseEntity.created(location).body(StartResponse.build(id));
    }

    @GetMapping("/{id}/round")
    public ResponseEntity round(@PathVariable("id") final UUID gameId) {
        return ResponseEntity.ok("empty");
    }

    @PostMapping("/finish")
    public ResponseEntity finish() {
        return ResponseEntity.ok("empty");
    }

    @GetMapping("/ranking")
    public ResponseEntity ranking() {
        return ResponseEntity.ok("empty");
    }

    private static class StartResponse extends RepresentationModel<StartResponse> {
        private final UUID id;

        private StartResponse(UUID id) {
            this.id = id;
        }

        public UUID getId() {
            return id;
        }

        public static StartResponse build(UUID id) {
            final StartResponse response = new StartResponse(id);

            response.add(linkTo(methodOn(GameController.class).start()).withSelfRel());
            response.add(linkTo(methodOn(GameController.class).round(id)).withRel("round"));
            response.add(linkTo(methodOn(GameController.class).finish()).withRel("finish"));
            response.add(linkTo(methodOn(GameController.class).ranking()).withRel("ranking"));

            return response;
        }
    }
}
