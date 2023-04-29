package tech.ada.game.moviesbattle.scraper;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static tech.ada.game.moviesbattle.scraper.OmdbRunner.InvalidMovieNumberException;
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
    private final String imdbMovieOne = "tt0111161";
    private final String imdbMovieTwo = "tt0068646";
    private final String imdbMovieThree = "tt0012345";
    private final String imdbMovieFour = "tt00207034";
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

        final OmdbRunner subject = new OmdbRunner(omdbScraper, movieRepository, 3, imdbIds);

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
        final OmdbRunner subject = new OmdbRunner(omdbScraper, movieRepository, 0, imdbIds);

        assertThrows(InvalidMovieNumberException.class, subject::run);

        verifyNoInteractions(omdbScraper, movieRepository);
    }

    @Test
    @DisplayName("when number of movies is less than zero, throws InvalidMovieNumberException")
    void when_number_of_movies_less_than_zero_throws_exception() throws Exception {
        final OmdbRunner subject = new OmdbRunner(omdbScraper, movieRepository, -1, imdbIds);

        assertThrows(InvalidMovieNumberException.class, subject::run);

        verifyNoInteractions(omdbScraper, movieRepository);
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
