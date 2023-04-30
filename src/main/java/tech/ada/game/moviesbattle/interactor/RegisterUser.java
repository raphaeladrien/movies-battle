package tech.ada.game.moviesbattle.interactor;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import tech.ada.game.moviesbattle.entity.User;
import tech.ada.game.moviesbattle.repository.UserRepository;
import static tech.ada.game.moviesbattle.entity.Role.USER;

@Service
public class RegisterUser {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public RegisterUser(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public boolean call(final RegisterUserRequest registerUserRequest) {
        final String encodedPassword = passwordEncoder.encode(registerUserRequest.password);
        final User user = new User(
            registerUserRequest.username, encodedPassword, USER
        );
        userRepository.save(user);
        return true;
    }

    public static class RegisterUserRequest {
        private String username;
        private String password;

        public RegisterUserRequest(String username, String password) {
            this.username = username;
            this.password = password;
        }

        public RegisterUserRequest() {
            super();
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
}
