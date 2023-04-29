package tech.ada.game.moviesbattle;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import tech.ada.game.moviesbattle.repository.MovieRepository;
import tech.ada.game.moviesbattle.scraper.OmdbRunner;
import tech.ada.game.moviesbattle.scraper.OmdbScraper;

@SpringBootTest
class MoviesbattleApplicationTests {

    @Autowired
    private OmdbScraper omdbScraper;

    @Autowired
    private OmdbRunner omdbRunner;

    @Autowired
    private MovieRepository movieRepository;

    @Test
    @DisplayName("ensure that bean omdbScraper was instantiated")
    void ensure_that_bean_omdbscraper_was_instantiated() {
        assertNotNull(omdbScraper, "Bean omdbScraper wasn't instantiated");
    }

    @Test
    @DisplayName("ensure that bean omdbRunner was instantiated")
    void ensure_that_bean_omdbRunner_was_instantiated() {
        assertNotNull(omdbScraper, "Bean omdbRunner wasn't instantiated");
    }

    @Test
    @DisplayName("ensure that bean movieRepository was instantiated")
    void ensure_that_bean_movieRepository_was_instantiated() {
        assertNotNull(movieRepository, "Bean omdbRunner wasn't instantiated");
    }
}
