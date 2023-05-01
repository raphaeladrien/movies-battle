package tech.ada.game.moviesbattle.interactor;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
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
import tech.ada.game.moviesbattle.repository.GameRepository;
import tech.ada.game.moviesbattle.repository.MovieRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

class StartGameTest {

    private final UserContextHolder userContextHolder = mock(UserContextHolder.class);
    private final GameRepository gameRepository = mock(GameRepository.class);
    private final MovieRepository movieRepository = mock(MovieRepository.class);

    private final UUID userId = UUID.randomUUID();
    private final String username = "a-super-username";
    private final User user = new User(userId, username, "a-password");
    private final UserContextInfo userContextInfo = new UserContextInfo(user);

    private final StartGame subject = new StartGame(userContextHolder, gameRepository, movieRepository);

    @Test
    @DisplayName("when user tries to start a new game but has in-progress, ends the former game and start a new one")
    void when_tries_start_new_game_has_in_progress_ends_end_game_start_new_one() {
        final User user = new User(userId, username, "a-password");
        final Game oldGame = buildGame(UUID.randomUUID(), user, 2, List.of());
        final Movie firstMovie = new Movie(
            "a-super-movie",
            1994,
            "a-director",
            "a-actor",
            9.7f,
            2000L,
            "tt0012345"
        );
        final Movie secondMovie = new Movie(
            "a-super-movie-2",
            1995,
            "a-director-2",
            "a-actor-2",
            9.3f,
            5000L,
            "tt0067891"
        );
        final List<Movie> movies = Arrays.asList(firstMovie, secondMovie);
        final ArgumentCaptor<Game> gameArgumentCaptor = ArgumentCaptor.forClass(Game.class);
        final Game game = buildGame(null, user, 0, movies);

        when(userContextHolder.getUserContextInfo()).thenReturn(userContextInfo);
        when(gameRepository.findByUserIdAndInProgress(user.getId(), true)).thenReturn(Optional.of(oldGame));
        when(movieRepository.findTwoRandomMovies()).thenReturn(movies);

        subject.call();

        verify(userContextHolder, times(1)).getUserContextInfo();
        verify(gameRepository, times(1)).findByUserIdAndInProgress(user.getId(), true);
        verify(gameRepository, times(1)).updateInProgressById(oldGame.getId(), false);
        verify(movieRepository, times(1)).findTwoRandomMovies();
        verify(gameRepository, times(1)).save(gameArgumentCaptor.capture());

        assertThat(game).usingRecursiveComparison().isEqualTo(gameArgumentCaptor.getValue());
        verifyNoMoreInteractions(userContextHolder, gameRepository, movieRepository);
    }

    @Test
    @DisplayName("when user tries to start a new game, starts a new one")
    void when_tries_start_new_game_starts_new_one() {
        final User user = new User(userId, username, "a-password");
        final Movie firstMovie = new Movie(
            "a-super-movie",
            1994,
            "a-director",
            "a-actor",
            9.7f,
            2000L,
            "tt0012345"
        );
        final Movie secondMovie = new Movie(
            "a-super-movie-2",
            1995,
            "a-director-2",
            "a-actor-2",
            9.3f,
            5000L,
            "tt0067891"
        );
        final List<Movie> movies = Arrays.asList(firstMovie, secondMovie);
        final ArgumentCaptor<Game> gameArgumentCaptor = ArgumentCaptor.forClass(Game.class);
        final Game game = buildGame(null, user, 0, movies);

        when(userContextHolder.getUserContextInfo()).thenReturn(userContextInfo);
        when(gameRepository.findByUserIdAndInProgress(user.getId(), true)).thenReturn(Optional.empty());
        when(movieRepository.findTwoRandomMovies()).thenReturn(movies);

        subject.call();

        verify(userContextHolder, times(1)).getUserContextInfo();
        verify(gameRepository, times(1)).findByUserIdAndInProgress(user.getId(), true);
        verify(gameRepository, times(0)).updateInProgressById(any(), eq(false));
        verify(movieRepository, times(1)).findTwoRandomMovies();
        verify(gameRepository, times(1)).save(gameArgumentCaptor.capture());

        assertThat(game).usingRecursiveComparison().isEqualTo(gameArgumentCaptor.getValue());
        verifyNoMoreInteractions(userContextHolder, gameRepository, movieRepository);
    }

    @Test
    @DisplayName("ensure that movies list is returned")
    void ensure_movies_list_returned() {
        final User user = new User(userId, username, "a-password");
        final Movie firstMovie = new Movie(
            "a-super-movie",
            1994,
            "a-director",
            "a-actor",
            9.7f,
            2000L,
            "tt0012345"
        );
        final Movie secondMovie = new Movie(
            "a-super-movie-2",
            1995,
            "a-director-2",
            "a-actor-2",
            9.3f,
            5000L,
            "tt0067891"
        );
        final List<Movie> movies = Arrays.asList(firstMovie, secondMovie);
        final ArgumentCaptor<Game> gameArgumentCaptor = ArgumentCaptor.forClass(Game.class);
        final UUID gameId = UUID.randomUUID();
        final Game game = buildGame(gameId, user, 0, movies);

        when(userContextHolder.getUserContextInfo()).thenReturn(userContextInfo);
        when(gameRepository.findByUserIdAndInProgress(user.getId(), true)).thenReturn(Optional.empty());
        when(movieRepository.findTwoRandomMovies()).thenReturn(movies);
        when(gameRepository.save(any())).thenReturn(game);

        final UUID result = subject.call();

        verify(userContextHolder, times(1)).getUserContextInfo();
        verify(gameRepository, times(1)).findByUserIdAndInProgress(user.getId(), true);
        verify(gameRepository, times(0)).updateInProgressById(any(), eq(false));
        verify(movieRepository, times(1)).findTwoRandomMovies();
        verify(gameRepository, times(1)).save(gameArgumentCaptor.capture());

        assertEquals(gameId, result, "Game ID must be the same");
        verifyNoMoreInteractions(userContextHolder, gameRepository, movieRepository);
    }

    @AfterEach
    void teardown() {
        reset(userContextHolder, gameRepository, movieRepository);
    }

    private Game buildGame(final UUID id, final User user, final int errors, List<Movie> movies) {
        final Game game = new Game(id, user, errors, true, null);
        if (!movies.isEmpty()) {
            final Round round =  new Round(
                game,
                movies.get(0),
                movies.get(1)
            );
            game.addRound(round);
        }

        return game;
    }
}
