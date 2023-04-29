package tech.ada.game.moviesbattle.scraper;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

class OmdbRunnerTest {

    private final OmdbScraper omdbScraper = mock(OmdbScraper.class);
    private final String imdbMovieOne = "tt0111161";
    private final String imdbMovieTwo = "tt0068646";
    private final List<String> imdbIds = Arrays.asList(imdbMovieOne, imdbMovieTwo);

    @Test
    @DisplayName("when number of movies is bigger than zero, ensure that omdbScraper is called")
    void when_number_of_movies_is_bigger_than_zero_omdbScrapper_must_be_called() throws Exception {
        final OmdbRunner subject = new OmdbRunner(omdbScraper, 1, imdbIds);
        when(omdbScraper.run("tt0111161")).thenReturn(CompletableFuture.completedFuture(false));
        when(omdbScraper.run("tt0068646")).thenReturn(CompletableFuture.completedFuture(true));

        subject.run();

        verify(omdbScraper, times(1)).run("tt0111161");
        verify(omdbScraper, times(1)).run("tt0068646");
    }

    @Test
    @DisplayName("when number of movies is zero, does nothing")
    void when_number_of_movies_is_zero_does_nothing() throws Exception {
        final OmdbRunner subject = new OmdbRunner(omdbScraper, 0, imdbIds);
        when(omdbScraper.run(any())).thenReturn(CompletableFuture.completedFuture(true));

        subject.run();
        verifyNoInteractions(omdbScraper);
    }

    @Test
    @DisplayName("when number of movies is less than zero, does nothing")
    void when_number_of_movies_less_than_zero_does_nothing() throws Exception {
        final OmdbRunner subject = new OmdbRunner(omdbScraper, -1, imdbIds);
        when(omdbScraper.run(any())).thenReturn(CompletableFuture.completedFuture(true));

        subject.run();
        verifyNoInteractions(omdbScraper);
    }
}
