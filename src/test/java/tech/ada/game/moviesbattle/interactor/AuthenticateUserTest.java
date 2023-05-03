package tech.ada.game.moviesbattle.interactor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import tech.ada.game.moviesbattle.entity.User;
import tech.ada.game.moviesbattle.interactor.AuthenticateUser.AuthenticationRequest;
import tech.ada.game.moviesbattle.interactor.AuthenticateUser.AuthenticationResponse;
import tech.ada.game.moviesbattle.repository.UserRepository;
import tech.ada.game.moviesbattle.service.JwtService;

import java.util.Optional;
import java.util.UUID;

class AuthenticateUserTest {

    private final UserRepository userRepository = mock(UserRepository.class);
    private final AuthenticationManager authenticationManager = mock(AuthenticationManager.class);
    private final Authentication authentication = mock(Authentication.class);
    private final JwtService jwtService = mock(JwtService.class);


    private final UUID userId = UUID.randomUUID();
    private final String username = "a-super-username";
    private final User user = new User(userId, username, "a-password");

    private final String jwt = "eyJhbGciOiJIUzI1NiJ9" +
        ".eyJzdWIiOiJhLXN1cGVyLXVzZXIiLCJpYXQiOjE2ODI4NTQ5MjAsImV4cCI6MTY4Mjk0MTMyMH0" +
        ".GWuXc2RgaTtYmFIa-Cfoom2pxY8YkpANdq6damqTO7E";

    private final AuthenticateUser subject = new AuthenticateUser(userRepository, authenticationManager, jwtService);

    @Test
    @DisplayName("when a valid user and password is provided, returns JWT token")
    void when_valid_user_password_provided_returns_jwt_token() {
        final AuthenticationRequest request = new AuthenticationRequest(username, user.getPassword());
        final AuthenticationResponse expectedResponse = new AuthenticationResponse(jwt);

        when(authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(username, user.getPassword()))
        ).thenReturn(authentication);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(jwtService.generateToken(user)).thenReturn(jwt);

        final AuthenticationResponse response = subject.call(request);

        verify(userRepository, times(1)).findByUsername(username);
        verify(jwtService, times(1)).generateToken(user);

        assertEquals(expectedResponse.getAccessToken(), response.getAccessToken(), "Access token must be the same");
    }

    @Test
    @DisplayName("when an invalid user and password is provided, throws RuntimeException")
    void when_invalid_user_password_provided_returns_throws_RuntimeException() {
        final AuthenticationRequest request = new AuthenticationRequest(username, user.getPassword());

        when(authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(username, user.getPassword()))
        ).thenThrow(new RuntimeException("an exception"));

        assertThrows(RuntimeException.class, () -> subject.call(request));
    }
}
