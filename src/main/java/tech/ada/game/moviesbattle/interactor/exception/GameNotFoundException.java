package tech.ada.game.moviesbattle.interactor.exception;

public class GameNotFoundException extends RuntimeException {
    public GameNotFoundException(String message) {
        super(message);
    }
}
