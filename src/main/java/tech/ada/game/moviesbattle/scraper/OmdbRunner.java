package tech.ada.game.moviesbattle.scraper;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import tech.ada.game.moviesbattle.entity.Movie;
import tech.ada.game.moviesbattle.interactor.RegisterUser;
import tech.ada.game.moviesbattle.repository.MovieRepository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

@Component
public class OmdbRunner implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(OmdbRunner.class);

    private final OmdbScraper omdbScraper;
    private final Integer numberOfMovies;
    private final List<String> imdbIds = new ArrayList<>();
    private final String users;
    private final MovieRepository movieRepository;
    private final RegisterUser registerUser;

    public OmdbRunner(
        OmdbScraper omdbScraper,
        MovieRepository movieRepository,
        RegisterUser registerUser,
        @Value("${omdb.qtd.movies}") Integer numberOfMovies,
        @Value("${omdb.imdb.top.movies}") List<String> imdbIds,
        @Value("${users}") String users
    ) {
        this.omdbScraper = omdbScraper;
        this.movieRepository = movieRepository;
        this.registerUser = registerUser;
        this.numberOfMovies = numberOfMovies;
        this.imdbIds.addAll(imdbIds);
        this.users = users;
    }

    @Override
    public void run(String... args) throws Exception {
        loadMovies();
        loadUsers();
    }

    private void loadUsers() {
        if (users.isEmpty()){
            return;
        }

        final String[] lstUsers = users.split(";");
        for(final String user : lstUsers) {
            String[] parts = user.split("#");
            if (parts.length != 2)
                throw new InvalidUserConfigurationException("Users env var must following this standard user#password;");

            registerUser.call(new RegisterUser.RegisterUserRequest(parts[0], parts[1]));
        }
    }

    private void loadMovies() throws InterruptedException, ExecutionException, JsonProcessingException {
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

    public static class InvalidUserConfigurationException extends RuntimeException {
        public InvalidUserConfigurationException(String message) {
            super(message);
        }
    }
}
