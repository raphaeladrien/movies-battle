package tech.ada.game.moviesbattle.interactor;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import tech.ada.game.moviesbattle.entity.User;
import tech.ada.game.moviesbattle.interactor.exception.UserExistsException;
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

    public RegisterUserResponse call(final RegisterUserRequest registerUserRequest) {
        final String username = registerUserRequest.username;
        if (userRepository.findByUsername(username).isPresent()) {
            throw new UserExistsException("User " + username + " already exists in DB");
        }

        final String encodedPassword = passwordEncoder.encode(registerUserRequest.password);
        final User user = new User(
            username, encodedPassword, USER
        );
        userRepository.save(user);
        return new RegisterUserResponse("User was created");
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

        public String getUsername() {
            return username;
        }

        public String getPassword() {
            return password;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;

            if (o == null || getClass() != o.getClass()) return false;

            RegisterUserRequest that = (RegisterUserRequest) o;

            return new EqualsBuilder().append(username, that.username).append(password, that.password).isEquals();
        }

        @Override
        public int hashCode() {
            return new HashCodeBuilder(17, 37).append(username).append(password).toHashCode();
        }
    }

    public static class RegisterUserResponse {
        private final String message;

        public RegisterUserResponse(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;

            if (o == null || getClass() != o.getClass()) return false;

            RegisterUserResponse that = (RegisterUserResponse) o;

            return new EqualsBuilder().append(message, that.message).isEquals();
        }

        @Override
        public int hashCode() {
            return new HashCodeBuilder(17, 37).append(message).toHashCode();
        }
    }
}
