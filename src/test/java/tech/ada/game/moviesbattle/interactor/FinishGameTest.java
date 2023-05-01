package tech.ada.game.moviesbattle.interactor;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import tech.ada.game.moviesbattle.context.UserContextHolder;
import tech.ada.game.moviesbattle.context.UserContextInfo;
import tech.ada.game.moviesbattle.entity.Game;
import tech.ada.game.moviesbattle.entity.User;
import tech.ada.game.moviesbattle.interactor.exception.GameNotFoundException;
import tech.ada.game.moviesbattle.repository.GameRepository;
import tech.ada.game.moviesbattle.interactor.FinishGame.FinishResponse;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

class FinishGameTest {

    private final GameRepository gameRepository = mock(GameRepository.class);
    private final UserContextHolder userContextHolder = mock(UserContextHolder.class);

    private final UUID userId = UUID.randomUUID();
    private final String username = "a-super-username";
    private final User user = new User(userId, username, "a-password");
    private final UserContextInfo userContextInfo = new UserContextInfo(user);

    private final FinishGame subject = new FinishGame(gameRepository, userContextHolder);

    @BeforeEach
    void setup() {
        when(userContextHolder.getUserContextInfo()).thenReturn(userContextInfo);
    }

    @Test
    @DisplayName("when a game is found, should finish the game and return the game id")
    void when_game_is_found_finish_game_and_return_game_id() {
        UUID gameId = UUID.randomUUID();

        when(userContextHolder.getUserContextInfo()).thenReturn(userContextInfo);
        when(gameRepository.findByUserIdAndInProgress(userId, true)).thenReturn(Optional.of(new Game(
            gameId, user, 2, true, List.of())));

        final FinishResponse response = subject.call();

        assertEquals(gameId, response.getGameId(), "Game id must be the same");
        verify(gameRepository).save(any(Game.class));
    }

    @Test
    @DisplayName("when the game isn't found, throws GameNotFoundException")
    void when_game_is_not_found_throws_GameNotFoundException() {
        when(gameRepository.findByUserIdAndInProgress(userId, true)).thenReturn(Optional.empty());
        assertThrows(GameNotFoundException.class, subject::call);

        verify(gameRepository, times(1)).findByUserIdAndInProgress(userId, true);
        verifyNoMoreInteractions(gameRepository);
    }
}

