package tech.ada.game.moviesbattle.repository;

import jakarta.transaction.Transactional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import tech.ada.game.moviesbattle.entity.Game;
import tech.ada.game.moviesbattle.entity.User;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@Transactional
class GameRepositoryTest {

    private User user;

    @Autowired
    private GameRepository subject;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setup() {
        String password = "a-super-password";
        String username = "a-super-username";
        user = userRepository.save(new User(username, password));
    }

    @Test
    @DisplayName("when exist in db a game with user id and inProgress provided, returns Optional<Game>")
    void when_exist_db_game_user_id_inProgress_returns_game() {
        final Game game = subject.save(buildGame());

        final Game result = subject.findByUserIdAndInProgress(user.getId(), game.getInProgress())
            .orElseThrow(() -> new RuntimeException("Error to retrieve game"));

        assertThat(game).usingRecursiveComparison().isEqualTo(result);
    }

    @Test
    @DisplayName("ensure that one field(inProgress) and object will be updated by id")
    void ensure_one_field_inProgress_will_be_updated_by_id() {
        final Game game = subject.save(buildGame());
        subject.save(buildGame());
        subject.save(buildGame());

        final int linesUpdated = subject.updateInProgressById(game.getId(), false);

        assertEquals(1, linesUpdated, "Just one line is expected be updated");
    }

    private Game buildGame() {
        return new Game(user, 0, true, null);
    }
}
