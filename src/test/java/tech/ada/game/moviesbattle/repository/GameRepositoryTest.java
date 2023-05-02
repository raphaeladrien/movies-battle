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
import tech.ada.game.moviesbattle.entity.Movie;
import tech.ada.game.moviesbattle.entity.Round;
import tech.ada.game.moviesbattle.entity.User;

import java.util.List;
import java.util.Optional;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@Transactional
class GameRepositoryTest {

    private User user;

    @Autowired
    private GameRepository subject;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MovieRepository movieRepository;

    private Movie firstMovie;
    private Movie secondMovie;

    @BeforeEach
    void setup() {
        String password = "a-super-password";
        String username = "a-super-username";
        user = userRepository.save(new User(username, password));
        firstMovie = movieRepository.save(new Movie(
            "a-super-movie",
            1994,
            "a-director",
            "a-actor",
            9.7f,
            2000L,
            "tt0012345"
        ));
        secondMovie = movieRepository.save(new Movie(
            "a-super-movie-2",
            1995,
            "a-director-2",
            "a-actor-2",
            9.3f,
            5000L,
            "tt0067891"
        ));
    }

    @Test
    @DisplayName("when exist in db a game with user id and inProgress provided, returns Optional<Game>")
    void when_exist_db_game_user_id_inProgress_returns_game() {
        final Game game = subject.save(buildGame(false));

        final Game result = subject.findByUserIdAndInProgress(user.getId(), game.getInProgress())
            .orElseThrow(() -> new RuntimeException("Error to retrieve game"));

        assertThat(game).usingRecursiveComparison().isEqualTo(result);
    }

    @Test
    @DisplayName("ensure that one field(inProgress) and object will be updated by id")
    void ensure_one_field_inProgress_will_be_updated_by_id() {
        final Game game = subject.save(buildGame(false));
        subject.save(buildGame(false));
        subject.save(buildGame(false));

        final int linesUpdated = subject.updateInProgressById(game.getId(), false);

        assertEquals(1, linesUpdated, "Just one line is expected be updated");
    }

    @Test
    @DisplayName("ensure that game will be returned with rounds and user")
    void ensure_game_will_returned_rounds_user() {
        subject.save(buildGame(true));

        Optional<List<Game>> optGames = subject.findAllWithRoundsAndUser();

        final List<Game> games = optGames.orElseThrow(() -> new RuntimeException("FAIL"));
        assertEquals(1,games.size(), "In this case only one element must be returned");
    }

    private Game buildGame(boolean addRound) {
        final Game game = new Game(user, 0, true, null);
        if (addRound) {
            final Round round = new Round(
                game,
                firstMovie,
                secondMovie
            );
            game.addRound(round);
        }
        return game;
    }
}
