package tech.ada.game.moviesbattle.interactor;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import tech.ada.game.moviesbattle.entity.User;
import tech.ada.game.moviesbattle.repository.UserRepository;
import tech.ada.game.moviesbattle.service.JwtService;

@Service
public class AuthenticateUser {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthenticateUser(UserRepository userRepository, AuthenticationManager authenticationManager, JwtService jwtService) {
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    public AuthenticationResponse call(final AuthenticationRequest request) {
        final String username = request.username;
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                username, request.password
            )
        );

        final User user = userRepository.findByUsername(username).orElseThrow(RuntimeException::new);
        return new AuthenticationResponse(jwtService.generateToken(user));
    }

    public static class AuthenticationRequest {

        private String username;
        private String password;

        public AuthenticationRequest(String username, String password) {
            this.username = username;
            this.password = password;
        }

        public AuthenticationRequest() {
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

            AuthenticationRequest that = (AuthenticationRequest) o;

            return new EqualsBuilder().append(username, that.username).append(password, that.password).isEquals();
        }

        @Override
        public int hashCode() {
            return new HashCodeBuilder(17, 37).append(username).append(password).toHashCode();
        }
    }

    public static class AuthenticationResponse {

        @JsonProperty("access_token")
        private String accessToken;

        public AuthenticationResponse(String accessToken) {
            this.accessToken = accessToken;
        }

        public String getAccessToken() {
            return accessToken;
        }
    }
}
