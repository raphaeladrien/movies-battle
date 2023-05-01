package tech.ada.game.moviesbattle.interactor;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import tech.ada.game.moviesbattle.context.UserContextHolder;
import tech.ada.game.moviesbattle.context.UserContextInfo;
import tech.ada.game.moviesbattle.entity.Game;
import tech.ada.game.moviesbattle.entity.Movie;
import tech.ada.game.moviesbattle.entity.Round;
import tech.ada.game.moviesbattle.entity.User;
import tech.ada.game.moviesbattle.repository.GameRepository;
import tech.ada.game.moviesbattle.repository.MovieRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class StartGame {

    private final UserContextHolder userContextHolder;
    private final GameRepository gameRepository;
    private final MovieRepository movieRepository;

    public StartGame(UserContextHolder userContextHolder, GameRepository gameRepository, MovieRepository movieRepository) {
        this.userContextHolder = userContextHolder;
        this.gameRepository = gameRepository;
        this.movieRepository = movieRepository;
    }

    @Transactional
    public UUID call() {
        final UserContextInfo userContextInfo = userContextHolder.getUserContextInfo();
        final User user = userContextInfo.user();
        final Optional<Game> otpGame = gameRepository.findByUserIdAndInProgress(user.getId(), true);

        if(otpGame.isPresent()) {
            final Game oldGame = otpGame.get();
            gameRepository.updateInProgressById(oldGame.getId(), false);
        }

        final List<Movie> movies = movieRepository.findTwoRandomMovies();
        final Game game = buildGame(user, movies);

        final Game persistedGame = gameRepository.save(game);

        return persistedGame.getId();
    }

    private Game buildGame(User user, List<Movie> movies) {
        final Game game = new Game(user, 0, true, null);
        final Round round =  new Round(
            game,
            movies.get(0),
            movies.get(1)
        );
        game.addRound(round);
        return game;
    }
}
