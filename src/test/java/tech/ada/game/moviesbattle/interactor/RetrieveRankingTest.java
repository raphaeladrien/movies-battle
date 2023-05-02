package tech.ada.game.moviesbattle.interactor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import tech.ada.game.moviesbattle.entity.Game;
import tech.ada.game.moviesbattle.entity.Movie;
import tech.ada.game.moviesbattle.entity.Round;
import tech.ada.game.moviesbattle.entity.User;
import tech.ada.game.moviesbattle.interactor.exception.NoRankingAvailableException;
import tech.ada.game.moviesbattle.repository.GameRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

class RetrieveRankingTest {

    private final GameRepository gameRepository = mock(GameRepository.class);
    private final Movie firstMovie = new Movie(
        "a-super-movie",
        1994,
        "a-director",
        "a-actor",
        9.7f,
        2000L,
        "tt0012345"
    );
    private final Movie secondMovie = new Movie(
        "a-super-movie-2",
        1995,
        "a-director-2",
        "a-actor-2",
        9.3f,
        5000L,
        "tt0067891"
    );
    private final List<Movie> movies = List.of(firstMovie, secondMovie);

    private final RetrieveRanking subject = new RetrieveRanking(gameRepository);

    @Test
    @DisplayName("when we don't have any ranking available, throws NoRankingAvailableException")
    void when_we_do_not_have_ranking_available_throws_NoRankingAvailableException() {
        when(gameRepository.findAllWithRoundsAndUser()).thenReturn(Optional.empty());

        assertThrows(NoRankingAvailableException.class, subject::call);

        verify(gameRepository, times(1)).findAllWithRoundsAndUser();
        verifyNoMoreInteractions(gameRepository);
    }

    @Test
    @DisplayName("when we have a ranking available, returns ranking")
    void when_we_have_ranking_available_returns_ranking() {
        final UUID idUser = UUID.randomUUID();
        final UUID idUserTwo = UUID.randomUUID();
        final UUID idUserThree = UUID.randomUUID();

        final User user = new User(idUser, "user-1", "a-password");
        final User userTwo = new User(idUserTwo, "user-2", "a-password");
        final User userThree = new User(idUserThree, "user-3", "a-password");

        final List<String> expectedRanking = List.of("user-2", "user-3", "user-1");
        final Game gameUser = buildGame(UUID.randomUUID(), user, 3, movies);
        final Game gameUserTwo = buildGame(UUID.randomUUID(), userTwo, 0, movies);
        final Game gameUserThree = buildGame(UUID.randomUUID(), userThree, 1, movies);

        when(gameRepository.findAllWithRoundsAndUser()).thenReturn(
            Optional.of(new ArrayList<>(List.of(gameUser, gameUserTwo, gameUserThree)))
        );

        final List<RetrieveRanking.Ranking> result = subject.call();

        final List<String> resultOrder = result.stream().map(ranking -> ranking.getUsername())
            .collect(Collectors.toList());

        assertEquals(expectedRanking, resultOrder, "Ranking must be equals to expected ranking");
        verify(gameRepository, times(1)).findAllWithRoundsAndUser();
        verifyNoMoreInteractions(gameRepository);
    }

    private Game buildGame(final UUID id, final User user, final int errors, List<Movie> movies) {
        final Game game = new Game(id, user, errors, true, null);
        if (!movies.isEmpty()) {
            final Round round =  new Round(
                game,
                movies.get(0),
                movies.get(1)
            );
            round.setAnswered(true);
            game.addRound(round);
        }

        return game;
    }
}
