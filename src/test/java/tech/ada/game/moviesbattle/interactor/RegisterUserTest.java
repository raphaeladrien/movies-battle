package tech.ada.game.moviesbattle.interactor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.security.crypto.password.PasswordEncoder;
import static tech.ada.game.moviesbattle.entity.Role.USER;
import tech.ada.game.moviesbattle.entity.User;
import tech.ada.game.moviesbattle.interactor.exception.UserExistsException;
import tech.ada.game.moviesbattle.repository.UserRepository;
import tech.ada.game.moviesbattle.interactor.RegisterUser.RegisterUserRequest;
import tech.ada.game.moviesbattle.interactor.RegisterUser.RegisterUserResponse;

import java.util.Optional;

class RegisterUserTest {

    private final UserRepository userRepository = mock(UserRepository.class);
    private final PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
    private final RegisterUserRequest request = new RegisterUserRequest("username", "password");

    private final RegisterUser subject = new RegisterUser(userRepository, passwordEncoder);

    @Test
    @DisplayName("when user already exists in db, throws UserExistsException")
    void when_user_already_exists_throws_UserExistsException() {
        when(userRepository.findByUsername(request.getUsername())).thenReturn(
            Optional.of(new User(request.getUsername(), request.getPassword()))
        );

        assertThrows(UserExistsException.class, () -> subject.call(request));
    }

    @Test
    @DisplayName("when user does not exists in db, should create a new user and return true")
    void when_user_does_not_exists_create_user() {
        when(userRepository.findByUsername(request.getUsername())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(request.getPassword())).thenReturn("a-encoded-password");
        final RegisterUserResponse expectedResult = new RegisterUserResponse("User was created");

        final RegisterUserResponse result = subject.call(request);

        verify(userRepository, times(1)).findByUsername(request.getUsername());
        verify(passwordEncoder, times(1)).encode(request.getPassword());
        verify(userRepository, times(1)).save(new User(
            request.getUsername(), "a-encoded-password", USER
        ));
        assertEquals(expectedResult, result,"Result must be true");
    }
}
