package tech.ada.game.moviesbattle.interactor;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;
import tech.ada.game.moviesbattle.context.UserContextHolder;
import tech.ada.game.moviesbattle.entity.Game;
import tech.ada.game.moviesbattle.entity.Movie;
import tech.ada.game.moviesbattle.entity.Round;
import tech.ada.game.moviesbattle.repository.GameRepository;
import tech.ada.game.moviesbattle.repository.MovieRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class RetrieveGameOptions {

    private final MovieRepository movieRepository;
    private final GameRepository gameRepository;
    private final UserContextHolder userContextHolder;

    public RetrieveGameOptions(
        MovieRepository movieRepository,
        GameRepository gameRepository,
        UserContextHolder userContextHolder
    ) {
        this.movieRepository = movieRepository;
        this.gameRepository = gameRepository;
        this.userContextHolder = userContextHolder;
    }

    public List<MovieResponse> call(final UUID gameId) {
        final UUID userId = userContextHolder.getUserContextInfo().user().getId();

        final Optional<Game> optionalGame = gameRepository.findByIdAndUserIdAndInProgress(gameId, userId, true);
        final Game game = optionalGame.orElseThrow(() ->
            new GameNotFoundException("Was not found a game in-progress with game: " + gameId + " and user: " + userId)
        );

        if (game.getErrors() == 3) {
            throw new MaxNumberAttemptsException("Max number of attempts was achieved for the game" + gameId);
        }

        Optional<Round> optionalRound = game.getRounds().stream()
            .filter(round -> !round.isAnswered())
            .findFirst();

        if (optionalRound.isPresent()) {
            final Round round = optionalRound.get();
            return List.of(build(round.getFirstMovie()), build(round.getSecondMovie()));
        } else {
            final List<Movie> movies = retrieveNewOptions(game);
            final Round round = new Round(
                game,
                movies.get(0),
                movies.get(1)
            );

            game.addRound(round);
            gameRepository.save(game);

            return build(movies);
        }
    }

    private List<MovieResponse> build(List<Movie> movies) {
        return movies.stream().map(this::build).collect(Collectors.toList());
    }

    private MovieResponse build(Movie movie) {
        return new MovieResponse(
            movie.getId(),
            movie.getTitle(),
            movie.getYear(),
            movie.getDirector()
        );
    }

    private List<Movie> retrieveNewOptions(final Game game) {
        final List<Movie> movies = movieRepository.findTwoRandomMovies();
        final Pair<UUID, UUID> ids = Pair.of(movies.get(0).getId(), movies.get(1).getId());

        boolean pairExists = game.getRounds().stream().anyMatch(
            round -> (round.getFirstMovie().getId().equals(ids.getLeft()) && round.getSecondMovie().getId().equals(ids.getRight())) ||
                (round.getFirstMovie().getId().equals(ids.getRight()) && round.getSecondMovie().getId().equals(ids.getLeft()))
        );

        if (pairExists) {
            return retrieveNewOptions(game);
        } else {
            return movies;
        }
    }

    public static class MovieResponse {
        final UUID id;
        final String title;
        final int year;
        final String directors;

        public MovieResponse(UUID id, String title, int year, String directors) {
            this.id = id;
            this.title = title;
            this.year = year;
            this.directors = directors;
        }

        public UUID getId() {
            return id;
        }

        public String getTitle() {
            return title;
        }

        public int getYear() {
            return year;
        }

        public String getDirectors() {
            return directors;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;

            if (o == null || getClass() != o.getClass()) return false;

            MovieResponse that = (MovieResponse) o;

            return new EqualsBuilder().append(id, that.id).isEquals();
        }

        @Override
        public int hashCode() {
            return new HashCodeBuilder(17, 37).append(id).toHashCode();
        }
    }

    public static class GameNotFoundException extends RuntimeException {
        public GameNotFoundException(String message) {
            super(message);
        }
    }

    public static class MaxNumberAttemptsException extends RuntimeException {
        public MaxNumberAttemptsException(String message) {
            super(message);
        }
    }
}
