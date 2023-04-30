package tech.ada.game.moviesbattle;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.client.RestTemplate;
import tech.ada.game.moviesbattle.repository.MovieRepository;
import tech.ada.game.moviesbattle.repository.UserRepository;
import tech.ada.game.moviesbattle.scraper.OmdbRunner;
import tech.ada.game.moviesbattle.scraper.OmdbScraper;

import java.util.concurrent.Executor;

@SpringBootTest
class MoviesbattleApplicationTests {

    @Autowired
    private OmdbScraper omdbScraper;

    @Autowired
    private OmdbRunner omdbRunner;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private AuthenticationProvider authenticationProvider;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private SecurityContext securityContext;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private SecurityFilterChain securityFilterChain;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private Executor taskExecutor;

    @Test
    @DisplayName("ensure that bean omdbScraper was instantiated")
    void ensure_that_bean_omdbscraper_was_instantiated() {
        assertNotNull(omdbScraper, "Bean omdbScraper wasn't instantiated");
    }

    @Test
    @DisplayName("ensure that bean omdbRunner was instantiated")
    void ensure_that_bean_omdbRunner_was_instantiated() {
        assertNotNull(omdbScraper, "Bean omdbRunner wasn't instantiated");
    }

    @Test
    @DisplayName("ensure that bean movieRepository was instantiated")
    void ensure_that_bean_movieRepository_was_instantiated() {
        assertNotNull(movieRepository, "Bean omdbRunner wasn't instantiated");
    }

    @Test
    @DisplayName("ensure that bean userRepository was instantiated")
    void ensure_that_bean_userRepository_was_instantiated() {
        assertNotNull(userRepository, "Bean userRepository wasn't instantiated");
    }

    @Test
    @DisplayName("ensure that bean userDetailsService was instantiated")
    void ensure_that_bean_userDetailsService_was_instantiated() {
        assertNotNull(userDetailsService, "Bean userDetailsService wasn't instantiated");
    }

    @Test
    @DisplayName("ensure that bean authenticationProvider was instantiated")
    void ensure_that_bean_authenticationProvider_was_instantiated() {
        assertNotNull(authenticationProvider, "Bean authenticationProvider wasn't instantiated");
    }

    @Test
    @DisplayName("ensure that bean authenticationManager was instantiated")
    void ensure_that_bean_authenticationManager_was_instantiated() {
        assertNotNull(authenticationManager, "Bean authenticationManager wasn't instantiated");
    }

    @Test
    @DisplayName("ensure that bean securityContext was instantiated")
    void ensure_that_bean_securityContext_was_instantiated() {
        assertNotNull(securityContext, "Bean securityContext wasn't instantiated");
    }

    @Test
    @DisplayName("ensure that bean passwordEncoder was instantiated")
    void ensure_that_bean_passwordEncoder_was_instantiated() {
        assertNotNull(passwordEncoder, "Bean passwordEncoder wasn't instantiated");
    }

    @Test
    @DisplayName("ensure that bean securityFilterChain was instantiated")
    void ensure_that_bean_securityFilterChain_was_instantiated() {
        assertNotNull(securityFilterChain, "Bean securityFilterChain wasn't instantiated");
    }

    @Test
    @DisplayName("ensure that bean restTemplate was instantiated")
    void ensure_that_bean_restTemplate_was_instantiated() {
        assertNotNull(restTemplate, "Bean restTemplate wasn't instantiated");
    }

    @Test
    @DisplayName("ensure that bean taskExecutor was instantiated")
    void ensure_that_bean_taskExecutor_was_instantiated() {
        assertNotNull(taskExecutor, "Bean taskExecutor wasn't instantiated");
    }
}
