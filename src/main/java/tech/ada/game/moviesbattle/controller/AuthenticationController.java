package tech.ada.game.moviesbattle.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tech.ada.game.moviesbattle.interactor.AuthenticateUser;
import static tech.ada.game.moviesbattle.interactor.AuthenticateUser.AuthenticationRequest;
import static tech.ada.game.moviesbattle.interactor.AuthenticateUser.AuthenticationResponse;
import static tech.ada.game.moviesbattle.interactor.RegisterUser.RegisterUserRequest;
import tech.ada.game.moviesbattle.interactor.RegisterUser;

@RestController
@RequestMapping("/movies-battle")
public class AuthenticationController {

    @Autowired
    private AuthenticateUser authenticateUser;
    @Autowired
    private RegisterUser registerUser;

    @PostMapping("/signin")
    public ResponseEntity<AuthenticationResponse> signin(
        @RequestBody final AuthenticationRequest authenticationRequest
    ) {
        return ResponseEntity.ok(authenticateUser.call(authenticationRequest));
    }

    @PostMapping("/register")
    public ResponseEntity<Boolean> register(
        @RequestBody final RegisterUserRequest registerUserRequest
    ) {
        return ResponseEntity.ok(registerUser.call(registerUserRequest));
    }
}
