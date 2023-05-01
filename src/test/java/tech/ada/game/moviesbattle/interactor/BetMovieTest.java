package tech.ada.game.moviesbattle.interactor;

import org.apache.commons.lang3.RandomStringUtils;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import tech.ada.game.moviesbattle.context.UserContextHolder;
import tech.ada.game.moviesbattle.context.UserContextInfo;
import tech.ada.game.moviesbattle.entity.Game;
import tech.ada.game.moviesbattle.entity.Movie;
import tech.ada.game.moviesbattle.entity.Round;
import tech.ada.game.moviesbattle.entity.User;
import tech.ada.game.moviesbattle.interactor.exception.GameNotFoundException;
import tech.ada.game.moviesbattle.interactor.exception.MaxNumberAttemptsException;
import tech.ada.game.moviesbattle.interactor.exception.OptionNotAvailableException;
import tech.ada.game.moviesbattle.repository.GameRepository;
import tech.ada.game.moviesbattle.interactor.BetMovie.BetResponse;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

class BetMovieTest {

    private final GameRepository gameRepository = mock(GameRepository.class);
    private final UserContextHolder userContextHolder = mock(UserContextHolder.class);

    private final UUID userId = UUID.randomUUID();
    private final String username = "a-super-username";
    private final User user = new User(userId, username, "a-password");
    private final UserContextInfo userContextInfo = new UserContextInfo(user);

    private final BetMovie subject = new BetMovie(gameRepository, userContextHolder);

    @BeforeEach
    void setup() {
        when(userContextHolder.getUserContextInfo()).thenReturn(userContextInfo);
    }

    @Test
    @DisplayName("when an unknown game id is provided, throws GameNotFoundException")
    void when_unknown_game_id_is_provided_throws_GameNotFoundException() {
        final UUID gameId = UUID.randomUUID();

        when(gameRepository.findByIdAndUserIdAndInProgress(gameId, userId, true)).thenReturn(Optional.empty());

        assertThrows(GameNotFoundException.class, () -> {
            subject.call(gameId, UUID.randomUUID());
        });

        verify(gameRepository, times(1)).findByIdAndUserIdAndInProgress(gameId, userId, true);
        verifyNoMoreInteractions(gameRepository);
    }

    @Test
    @DisplayName("when the game doesn't have no more tries, throws MaxNumberAttemptsException")
    void when_game_do_not_have_more_tries_throws_MaxNumberAttemptsException() {
        final UUID gameId = UUID.randomUUID();
        final Movie firstMovie = buildMovie(9.5f, 2000);
        final Movie secondMovie = buildMovie(9.5f, 2000);
        final List<Movie> movies = List.of(firstMovie, secondMovie);
        final Game game = buildGame(UUID.randomUUID(), user, 3, movies, false);

        when(gameRepository.findByIdAndUserIdAndInProgress(gameId, userId, true)).thenReturn(Optional.of(game));

        assertThrows(MaxNumberAttemptsException.class, () -> {
            subject.call(gameId, UUID.randomUUID());
        });

        verify(gameRepository, times(1)).findByIdAndUserIdAndInProgress(gameId, userId, true);
        verifyNoMoreInteractions(gameRepository);
    }

    @Test
    @DisplayName("when an unknown movie is provided, throws OptionNotAvailableException")
    void when_unknwon_movie_provided_throws_OptionNotAvailableException() {
        final UUID gameId = UUID.randomUUID();
        final Movie firstMovie = buildMovie(9.5f, 2000);
        final Movie secondMovie = buildMovie(9.5f, 2000);
        final List<Movie> movies = List.of(firstMovie, secondMovie);
        final Game game = buildGame(UUID.randomUUID(), user, 0, movies, false);

        when(gameRepository.findByIdAndUserIdAndInProgress(gameId, userId, true)).thenReturn(Optional.of(game));

        assertThrows(OptionNotAvailableException.class, () -> {
            subject.call(gameId, UUID.randomUUID());
        });

        verify(gameRepository, times(1)).findByIdAndUserIdAndInProgress(gameId, userId, true);
        verifyNoMoreInteractions(gameRepository);
    }

    @Test
    @DisplayName("when user chooses the correct option, returns BetResponse")
    void when_user_chooses_correct_option_returns_BetResponse() {
        final UUID gameId = UUID.randomUUID();
        final Movie firstMovie = buildMovie(2.0f, 2000);
        final Movie secondMovie = buildMovie(9.5f, 2000);
        final List<Movie> movies = List.of(firstMovie, secondMovie);
        final Game game = buildGame(UUID.randomUUID(), user, 0, movies, false);

        when(gameRepository.findByIdAndUserIdAndInProgress(gameId, userId, true)).thenReturn(Optional.of(game));

        final BetResponse expectedResponse = new BetResponse(true, 0);
        final BetResponse response = subject.call(gameId, secondMovie.getId());

        assertThat(response).usingRecursiveComparison().isEqualTo(expectedResponse);
    }

    @Test
    @DisplayName("when user chooses the wrong option, returns BetResponse")
    void when_user_chooses_wrong_option_returns_BetResponse() {
        final UUID gameId = UUID.randomUUID();
        final Movie firstMovie = buildMovie(2.0f, 2000);
        final Movie secondMovie = buildMovie(9.5f, 2000);
        final List<Movie> movies = List.of(firstMovie, secondMovie);
        final Game game = buildGame(UUID.randomUUID(), user, 0, movies, false);

        when(gameRepository.findByIdAndUserIdAndInProgress(gameId, userId, true)).thenReturn(Optional.of(game));

        final BetResponse expectedResponse = new BetResponse(false, 1);
        final BetResponse response = subject.call(gameId, firstMovie.getId());

        assertThat(response).usingRecursiveComparison().isEqualTo(expectedResponse);
    }

    private Game buildGame(final UUID id, final User user, final int errors, List<Movie> movies, boolean answered) {
        final Game game = new Game(id, user, errors, true, null);
        if (!movies.isEmpty()) {
            final Round round = new Round(
                game,
                movies.get(0),
                movies.get(1)
            );
            round.setAnswered(answered);
            game.addRound(round);
        }

        return game;
    }

    private Movie buildMovie(float imdbRating, long votes) {
        return new Movie(
            UUID.randomUUID(),
            RandomStringUtils.random(9),
            1994,
            RandomStringUtils.random(9),
            RandomStringUtils.random(9),
            imdbRating,
            votes
        );
    }
}
