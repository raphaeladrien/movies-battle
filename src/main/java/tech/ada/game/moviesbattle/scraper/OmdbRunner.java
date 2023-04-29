package tech.ada.game.moviesbattle.scraper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class OmdbRunner implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(OmdbRunner.class);

    private final OmdbScraper omdbScraper;
    private final Integer numberOfMovies;
    private final List<String> imdbIds = new ArrayList<>();

    public OmdbRunner(
        OmdbScraper omdbScraper,
        @Value("${omdb.qtd.movies}") Integer numberOfMovies,
        @Value("${omdb.imdb.top.movies}") List<String> imdbIds
    ) {
        this.omdbScraper = omdbScraper;
        this.numberOfMovies = numberOfMovies;
        this.imdbIds.addAll(imdbIds);
    }

    @Override
    public void run(String... args) throws Exception {
        final boolean isValidValue = numberOfMovies < 1;

        if (isValidValue)
            return;

        final AtomicInteger moviesCreated = new AtomicInteger(0);
        for (final String imdbId : imdbIds) {
            final boolean result = omdbScraper.run(imdbId).get();

            if (result) moviesCreated.incrementAndGet();

            if (moviesCreated.get() == numberOfMovies) {
                if (logger.isDebugEnabled())
                    logger.debug("{} were retrieved from OMDB API", moviesCreated.get());
                break;
            }
        }
    }
}
