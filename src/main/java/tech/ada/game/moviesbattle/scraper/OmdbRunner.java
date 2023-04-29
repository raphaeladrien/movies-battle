package tech.ada.game.moviesbattle.scraper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import tech.ada.game.moviesbattle.entity.Movie;
import tech.ada.game.moviesbattle.repository.MovieRepository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class OmdbRunner implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(OmdbRunner.class);

    private final OmdbScraper omdbScraper;
    private final Integer numberOfMovies;
    private final List<String> imdbIds = new ArrayList<>();
    private final MovieRepository movieRepository;

    public OmdbRunner(
        OmdbScraper omdbScraper,
        MovieRepository movieRepository,
        @Value("${omdb.qtd.movies}") Integer numberOfMovies,
        @Value("${omdb.imdb.top.movies}") List<String> imdbIds
    ) {
        this.omdbScraper = omdbScraper;
        this.movieRepository = movieRepository;
        this.numberOfMovies = numberOfMovies;
        this.imdbIds.addAll(imdbIds);
    }

    @Override
    public void run(String... args) throws Exception {
        final boolean invalidMovieNumber = numberOfMovies < 3;

        if (invalidMovieNumber) {
            throw new InvalidMovieNumberException("Invalid configuration exception, number of movies must be greater than 2");
        }

        final Set<Movie> movies = new HashSet<>();
        for (final String imdbId : imdbIds) {
            final Movie movie = omdbScraper.run(imdbId).get();

            if (movie != null) movies.add(movie);

            if (movies.size() == numberOfMovies) {
                if (logger.isDebugEnabled())
                    logger.debug("{} were retrieved from OMDB API", movies.size());
                break;
            }
        }

        if (!movies.isEmpty())
            movieRepository.saveAll(movies);
    }

    public static class InvalidMovieNumberException extends RuntimeException {
        public InvalidMovieNumberException(String message) {
            super(message);
        }
    }
}
