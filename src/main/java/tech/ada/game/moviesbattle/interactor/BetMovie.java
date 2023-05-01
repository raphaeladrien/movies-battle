package tech.ada.game.moviesbattle.interactor;

import org.springframework.stereotype.Service;
import tech.ada.game.moviesbattle.context.UserContextHolder;
import tech.ada.game.moviesbattle.entity.Game;
import tech.ada.game.moviesbattle.entity.Movie;
import tech.ada.game.moviesbattle.entity.Round;
import tech.ada.game.moviesbattle.interactor.exception.GameNotFoundException;
import tech.ada.game.moviesbattle.interactor.exception.MaxNumberAttemptsException;
import tech.ada.game.moviesbattle.interactor.exception.OptionNotAvailableException;
import tech.ada.game.moviesbattle.repository.GameRepository;

import java.util.Comparator;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Service
public class BetMovie {

    private final GameRepository gameRepository;
    private final UserContextHolder userContextHolder;

    public BetMovie(GameRepository gameRepository, UserContextHolder userContextHolder) {
        this.gameRepository = gameRepository;
        this.userContextHolder = userContextHolder;
    }

    public BetResponse call(UUID gameId, UUID movieId ) {
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

        final Round round = optionalRound.orElseThrow(() ->
            new OptionNotAvailableException("Game" + gameId + " without active round")
        );

        final boolean isOption = round.getFirstMovie().getId().equals(movieId) ||
            round.getSecondMovie().getId().equals(movieId);
        if(!isOption) {
            throw new OptionNotAvailableException("The option " + movieId + " is not present in active round");
        }

        final Movie movieWithBiggestScore = Stream.of(round.getFirstMovie(), round.getSecondMovie())
            .max(Comparator.comparing(Movie::getScore))
            .orElse(null);

        int index = IntStream.range(0, game.getRounds().size())
            .filter(i -> game.getRounds().get(i).equals(round))
            .findFirst()
            .orElse(-1);

        round.setAnswered(true);
        game.getRounds().set(index, round);

        final BetResponse response = getBetResponse(movieId, game, movieWithBiggestScore);
        gameRepository.save(game);

        return response;
    }

    private BetResponse getBetResponse(UUID movieId, Game game, Movie movieWithBiggestScore) {
        if (movieWithBiggestScore.getId().equals(movieId)) {
            return build(true, game.getErrors());
        } else {
            game.incrementErrorCount();
            return build(false, game.getErrors());
        }
    }

    private BetResponse build(boolean nailedIt, int errors) {
        return new BetResponse(nailedIt, errors);
    }

    public static class BetResponse {

        private final boolean nailedIt;
        private final int errors;

        public BetResponse(boolean nailedIt, int errors) {
            this.nailedIt = nailedIt;
            this.errors = errors;
        }

        public boolean isNailedIt() {
            return nailedIt;
        }

        public int getErrors() {
            return errors;
        }
    }
}
