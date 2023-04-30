package tech.ada.game.moviesbattle.repository;

import jakarta.transaction.Transactional;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertFalse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import tech.ada.game.moviesbattle.entity.User;

import java.util.Optional;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@Transactional
class UserRepositoryTest {

    @Autowired
    private UserRepository subject;
    private final String username = "a-super-username";
    private final String password = "a-super-password";
    private final User user = new User(username, password);

    @BeforeEach
    void setup() {
        subject.save(user);
    }

    @Test
    @DisplayName("when a username is provided and exist in db, returns user")
    void when_username_provided_exist_db_returns_user() {
        final Optional<User> result = subject.findByUsername(username);
        final User userRetrieved = result.get();

        assertThat(user).usingRecursiveComparison().ignoringFields("id").isEqualTo(userRetrieved);
    }

    @Test
    @DisplayName("when a username is provided and don't exist in db, returns empty")
    void when_username_provided_do_not_exists_db_returns_user() {
        final Optional<User> result = subject.findByUsername("a-very-crazy-username");
        assertFalse(result.isPresent(), "User with username " + username + " should not be found");
    }

    @AfterEach
    void tearDown() {
        subject.deleteAll();
    }
}
