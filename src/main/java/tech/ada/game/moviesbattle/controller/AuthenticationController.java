package tech.ada.game.moviesbattle.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tech.ada.game.moviesbattle.interactor.AuthenticateUser;
import static tech.ada.game.moviesbattle.interactor.AuthenticateUser.AuthenticationRequest;
import static tech.ada.game.moviesbattle.interactor.AuthenticateUser.AuthenticationResponse;
import tech.ada.game.moviesbattle.interactor.RegisterUser;
import static tech.ada.game.moviesbattle.interactor.RegisterUser.RegisterUserRequest;
import static tech.ada.game.moviesbattle.interactor.RegisterUser.RegisterUserResponse;

@RestController
@RequestMapping("/movies-battle/id")
public class AuthenticationController {

    private final AuthenticateUser authenticateUser;
    private final RegisterUser registerUser;

    public AuthenticationController(AuthenticateUser authenticateUser, RegisterUser registerUser) {
        this.authenticateUser = authenticateUser;
        this.registerUser = registerUser;
    }

    @PostMapping("/signing")
    public ResponseEntity<AuthenticationResponse> signing(
        @RequestBody final AuthenticationRequest authenticationRequest
    ) {
        return ResponseEntity.ok(authenticateUser.call(authenticationRequest));
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterUserResponse> register(
        @RequestBody final RegisterUserRequest registerUserRequest
    ) {
        return ResponseEntity.ok(registerUser.call(registerUserRequest));
    }
}
