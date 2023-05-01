package tech.ada.game.moviesbattle.interactor;

import org.springframework.stereotype.Service;
import tech.ada.game.moviesbattle.context.UserContextHolder;
import tech.ada.game.moviesbattle.entity.Game;
import tech.ada.game.moviesbattle.interactor.exception.GameNotFoundException;
import tech.ada.game.moviesbattle.repository.GameRepository;

import java.util.UUID;

@Service
public class FinishGame {

    private final GameRepository gameRepository;
    private final UserContextHolder userContextHolder;

    public FinishGame(GameRepository gameRepository, UserContextHolder userContextHolder) {
        this.gameRepository = gameRepository;
        this.userContextHolder = userContextHolder;
    }

    public FinishResponse call() {
        final UUID userId = userContextHolder.getUserContextInfo().user().getId();

        final Game game = gameRepository.findByUserIdAndInProgress(userId, true).orElseThrow(() ->
            new GameNotFoundException("Game was found to user " + userId)
        );

        game.setInProgress(false);
        gameRepository.save(game);

        return new FinishResponse(game.getId());
    }

    public static class FinishResponse {

        private UUID gameId;

        public FinishResponse(UUID gameId) {
            this.gameId = gameId;
        }

        public UUID getGameId() {
            return gameId;
        }
    }
}
