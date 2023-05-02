package tech.ada.game.moviesbattle.scraper;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import tech.ada.game.moviesbattle.interactor.RegisterUser;
import tech.ada.game.moviesbattle.interactor.RegisterUser.RegisterUserRequest;
import static tech.ada.game.moviesbattle.scraper.OmdbRunner.InvalidMovieNumberException;
import static tech.ada.game.moviesbattle.scraper.OmdbRunner.InvalidUserConfigurationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import tech.ada.game.moviesbattle.entity.Movie;
import tech.ada.game.moviesbattle.repository.MovieRepository;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

class OmdbRunnerTest {

    private final OmdbScraper omdbScraper = mock(OmdbScraper.class);
    private final MovieRepository movieRepository = mock(MovieRepository.class);
    private final RegisterUser registerUser = mock(RegisterUser.class);
    private final String imdbMovieOne = "tt0111161";
    private final String imdbMovieTwo = "tt0068646";
    private final String imdbMovieThree = "tt0012345";
    private final String imdbMovieFour = "tt00207034";
    private final String users = "ned.stark#123456;jon.snow#123456;sansa.stark#123456";
    private final List<String> imdbIds = Arrays.asList(imdbMovieOne, imdbMovieTwo, imdbMovieThree, imdbMovieFour);

    @Test
    @DisplayName("when number of movies is equals to number of movies retrieved, persists movies in db")
    void when_number_of_movies_is_equals_movies_retrieved_persists_db() throws Exception {
        final Movie movieOne = buildMovie("tt0068646");
        final Movie movieTwo = buildMovie("tt0012345");
        final Movie movieThree = buildMovie("tt00207034");
        final Set<Movie> movies = new HashSet<>();
        movies.add(movieOne);
        movies.add(movieTwo);
        movies.add(movieThree);

        final OmdbRunner subject = new OmdbRunner(omdbScraper, movieRepository, registerUser, 3, imdbIds, users);

        when(omdbScraper.run("tt0111161")).thenReturn(CompletableFuture.completedFuture(null));
        when(omdbScraper.run("tt0068646")).thenReturn(
            CompletableFuture.completedFuture(movieOne)
        );
        when(omdbScraper.run("tt0012345")).thenReturn(
            CompletableFuture.completedFuture(movieTwo)
        );
        when(omdbScraper.run("tt00207034")).thenReturn(
            CompletableFuture.completedFuture(movieThree)
        );

        subject.run();

        verify(omdbScraper, times(1)).run("tt0111161");
        verify(omdbScraper, times(1)).run("tt0068646");
        verify(omdbScraper, times(1)).run("tt0012345");
        verify(omdbScraper, times(1)).run("tt00207034");

        verify(movieRepository, times(1)).saveAll(movies);

        verifyNoMoreInteractions(omdbScraper, movieRepository);
    }

    @Test
    @DisplayName("when number of movies is zero, throws InvalidMovieNumberException")
    void when_number_of_movies_is_zero_throws_exception() throws Exception {
        final OmdbRunner subject = new OmdbRunner(omdbScraper, movieRepository, registerUser, 0, imdbIds, users);

        assertThrows(InvalidMovieNumberException.class, subject::run);

        verifyNoInteractions(omdbScraper, movieRepository);
    }

    @Test
    @DisplayName("when number of movies is less than zero, throws InvalidMovieNumberException")
    void when_number_of_movies_less_than_zero_throws_exception() throws Exception {
        final OmdbRunner subject = new OmdbRunner(omdbScraper, movieRepository, registerUser, -1, imdbIds, users);

        assertThrows(InvalidMovieNumberException.class, subject::run);

        verifyNoInteractions(omdbScraper, movieRepository);
    }

    @Test
    @DisplayName("when list of users are provided, generates users")
    void when_users_are_provided_generate_user() throws Exception {
        final OmdbRunner subject = new OmdbRunner(omdbScraper, movieRepository, registerUser, 3, imdbIds, users);

        when(omdbScraper.run(any(String.class))).thenReturn(CompletableFuture.completedFuture(null));

        subject.run();

        verify(registerUser, times(3)).call(any(RegisterUserRequest.class));
    }

    @Test
    @DisplayName("when list of users are provided is empty, does nothing")
    void when_users_are_provided_is_empty_does_nothing() throws Exception {
        final OmdbRunner subject = new OmdbRunner(omdbScraper, movieRepository, registerUser, 3, imdbIds, "");

        when(omdbScraper.run(any(String.class))).thenReturn(CompletableFuture.completedFuture(null));

        subject.run();

        verify(registerUser, times(0)).call(any(RegisterUserRequest.class));
    }

    @Test
    @DisplayName("when only user is provided, generates user")
    void when_only_user_is_provided_generates_user() throws Exception {
        final OmdbRunner subject = new OmdbRunner(omdbScraper, movieRepository, registerUser, 3, imdbIds, "jon.snow#12345");

        when(omdbScraper.run(any(String.class))).thenReturn(CompletableFuture.completedFuture(null));

        subject.run();

        verify(registerUser, times(1)).call(any(RegisterUserRequest.class));
    }

    @Test
    @DisplayName("when user is provided in wrong standard, throws InvalidUserConfigurationException")
    void when_user_is_provided_wrong_standard_throws_InvalidUserConfigurationException() throws Exception {
        final OmdbRunner subject = new OmdbRunner(omdbScraper, movieRepository, registerUser, 3, imdbIds, "jon.snow-12345");

        when(omdbScraper.run(any(String.class))).thenReturn(CompletableFuture.completedFuture(null));

        assertThrows(InvalidUserConfigurationException.class, subject::run);

        verify(registerUser, times(0)).call(any(RegisterUserRequest.class));
    }

    private Movie buildMovie(final String imdbId) {
        return new Movie(
            "a-super-title",
            2020,
            "a-super-director",
            "a-great-actor",
            9.3f,
            220000L,
            imdbId
        );
    }
}
