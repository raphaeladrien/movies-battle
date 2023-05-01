package tech.ada.game.moviesbattle.interactor;

import org.apache.commons.lang3.RandomStringUtils;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import tech.ada.game.moviesbattle.context.UserContextHolder;
import tech.ada.game.moviesbattle.context.UserContextInfo;
import tech.ada.game.moviesbattle.entity.Game;
import tech.ada.game.moviesbattle.entity.Movie;
import tech.ada.game.moviesbattle.entity.Round;
import tech.ada.game.moviesbattle.entity.User;
import tech.ada.game.moviesbattle.interactor.RetrieveGameOptions.GameNotFoundException;
import tech.ada.game.moviesbattle.interactor.RetrieveGameOptions.MaxNumberAttemptsException;
import tech.ada.game.moviesbattle.interactor.RetrieveGameOptions.MovieResponse;
import tech.ada.game.moviesbattle.repository.GameRepository;
import tech.ada.game.moviesbattle.repository.MovieRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

class RetrieveGameOptionsTest {

    private final MovieRepository movieRepository = mock(MovieRepository.class);
    private final GameRepository gameRepository = mock(GameRepository.class);

    private final UUID userId = UUID.randomUUID();
    private final String username = "a-super-username";
    private final User user = new User(userId, username, "a-password");
    private final UserContextInfo userContextInfo = new UserContextInfo(user);

    private final UserContextHolder userContextHolder = mock(UserContextHolder.class);
    private final RetrieveGameOptions subject = new RetrieveGameOptions(
        movieRepository, gameRepository, userContextHolder
    );

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
            subject.call(gameId);
        });

        verify(gameRepository, times(1)).findByIdAndUserIdAndInProgress(gameId, userId, true);
        verifyNoMoreInteractions(gameRepository);
        verifyNoInteractions(movieRepository);
    }

    @Test
    @DisplayName("when the game doesn't have no more tries, throws MaxNumberAttemptsException")
    void when_game_do_not_have_more_tries_throws_MaxNumberAttemptsException() {
        final UUID gameId = UUID.randomUUID();
        final Movie firstMovie = buildMovie();
        final Movie secondMovie = buildMovie();
        final List<Movie> movies =  List.of(firstMovie, secondMovie);
        final Game game = buildGame(UUID.randomUUID(), user, 3, movies, false);

        when(gameRepository.findByIdAndUserIdAndInProgress(gameId, userId, true)).thenReturn(Optional.of(game));

        assertThrows(MaxNumberAttemptsException.class, () -> {
            subject.call(gameId);
        });

        verify(gameRepository, times(1)).findByIdAndUserIdAndInProgress(gameId, userId, true);
        verifyNoMoreInteractions(gameRepository);
        verifyNoInteractions(movieRepository);
    }

    @Test
    @DisplayName("when exist game and have a round available, returns movies list")
    void when_exist_game_have_round_available() {
        final UUID gameId = UUID.randomUUID();
        final Movie firstMovie = buildMovie();
        final Movie secondMovie = buildMovie();
        final List<Movie> movies =  List.of(firstMovie, secondMovie);
        final Game game = buildGame(UUID.randomUUID(), user, 0, movies, false);

        when(gameRepository.findByIdAndUserIdAndInProgress(gameId, userId, true)).thenReturn(Optional.of(game));

        final List<MovieResponse> result = subject.call(gameId);

        assertEquals(build(movies), result, "List of movies must be equals");
        verify(gameRepository, times(1)).findByIdAndUserIdAndInProgress(gameId, userId, true);
        verifyNoMoreInteractions(gameRepository);
        verifyNoInteractions(movieRepository);
    }

    @Test
    @DisplayName("when exist a game without round available, creates a new round and return movies list")
    void when_do_not_exist_round_available_create_new_one_returns_movies_list() {
        final UUID gameId = UUID.randomUUID();
        final Movie firstMovie = buildMovie();
        final Movie secondMovie = buildMovie();
        final List<Movie> movies = List.of(firstMovie, secondMovie);
        final Game game = buildGame(UUID.randomUUID(), user, 0, movies, true);
        final Movie firstRandomMovie = buildMovie();
        final Movie secondRadonMovie = buildMovie();
        final List<Movie> randomMovies = List.of(firstRandomMovie, secondRadonMovie);

        when(gameRepository.findByIdAndUserIdAndInProgress(gameId, userId, true)).thenReturn(Optional.of(game));
        when(movieRepository.findTwoRandomMovies()).thenReturn(randomMovies);

        final List<MovieResponse> result = subject.call(gameId);

        assertEquals(build(randomMovies), result, "List of movies must be equals");
        verify(gameRepository, times(1)).findByIdAndUserIdAndInProgress(gameId, userId, true);
        verify(gameRepository, times(1)).save(any());
        verify(movieRepository, times(1)).findTwoRandomMovies();

        verifyNoMoreInteractions(gameRepository, movieRepository);
    }

    @Test
    @DisplayName("when exist a game without round available but a movie list was already used, creates a new round and return movies list")
    void when_do_not_exist_round_available_but_movie_list_already_used_create_new_one_returns_movies_list() {
        final UUID gameId = UUID.randomUUID();
        final Movie firstMovie = buildMovie();
        final Movie secondMovie = buildMovie();
        final List<Movie> movies = List.of(firstMovie, secondMovie);
        final Game game = buildGame(UUID.randomUUID(), user, 0, movies, true);
        final Movie firstRandomMovie = buildMovie();
        final Movie secondRadonMovie = buildMovie();
        final List<Movie> firstRandomMovie1 = List.of(secondMovie, firstMovie);
        final List<Movie> secondRandomMovies = List.of(firstRandomMovie, secondRadonMovie);

        when(gameRepository.findByIdAndUserIdAndInProgress(gameId, userId, true)).thenReturn(Optional.of(game));
        when(movieRepository.findTwoRandomMovies()).thenReturn(firstRandomMovie1).thenReturn(secondRandomMovies);

        final List<MovieResponse> result = subject.call(gameId);

        assertEquals(build(secondRandomMovies), result, "List of movies must be equals");
        verify(gameRepository, times(1)).findByIdAndUserIdAndInProgress(gameId, userId, true);
        verify(gameRepository, times(1)).save(any());
        verify(movieRepository, times(2)).findTwoRandomMovies();

        verifyNoMoreInteractions(gameRepository, movieRepository);
    }

    private Game buildGame(final UUID id, final User user, final int errors, List<Movie> movies, boolean answered) {
        final Game game = new Game(id, user, errors, true, null);
        if (!movies.isEmpty()) {
            final Round round =  new Round(
                game,
                movies.get(0),
                movies.get(1)
            );
            round.setAnswered(answered);
            game.addRound(round);
        }

        return game;
    }

    private Movie buildMovie() {
        return new Movie(
            UUID.randomUUID(),
            RandomStringUtils.random(9),
            1994,
            RandomStringUtils.random(9),
            RandomStringUtils.random(9),
            9.7f,
            2000L
        );
    }

    private List<RetrieveGameOptions.MovieResponse> build(List<Movie> movies) {
        return movies.stream().map(this::build).collect(Collectors.toList());
    }

    private RetrieveGameOptions.MovieResponse build(Movie movie) {
        return new RetrieveGameOptions.MovieResponse(
            movie.getId(),
            movie.getTitle(),
            movie.getYear(),
            movie.getDirector()
        );
    }
}
