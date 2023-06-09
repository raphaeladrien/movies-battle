package tech.ada.game.moviesbattle.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import tech.ada.game.moviesbattle.interactor.exception.GameNotFoundException;
import tech.ada.game.moviesbattle.interactor.exception.MaxNumberAttemptsException;
import tech.ada.game.moviesbattle.interactor.exception.NoRankingAvailableException;
import tech.ada.game.moviesbattle.interactor.exception.OptionNotAvailableException;
import tech.ada.game.moviesbattle.interactor.exception.UserExistsException;

@ControllerAdvice
public class DefaultExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(DefaultExceptionHandler.class);

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<RestError> handleRuntimeExceptionException(Exception ex) {
        if (logger.isErrorEnabled())
            logger.error(ex.getMessage(), ex);

        return  ResponseEntity.status(INTERNAL_SERVER_ERROR).body(
           new RestError("MB0500", "An internal server error occurred. Please contact ada.tech support.")
        );
    }

    @ExceptionHandler(GameNotFoundException.class)
    public ResponseEntity<RestError> handleGameNotFoundException(Exception ex) {
        if (logger.isErrorEnabled())
            logger.error(ex.getMessage(), ex);

        return  ResponseEntity.status(NOT_FOUND).body(
            new RestError("MB0001", "We couldn't find the game on our server. please start a new game")
        );
    }

    @ExceptionHandler(MaxNumberAttemptsException.class)
    public ResponseEntity<RestError> handleMaxNumberAttemptsException(Exception ex) {
        if (logger.isErrorEnabled())
            logger.error(ex.getMessage(), ex);

        return  ResponseEntity.status(UNPROCESSABLE_ENTITY).body(
            new RestError("MB0002", "Max number of attempts was achieved. please start a new game")
        );
    }

    @ExceptionHandler(OptionNotAvailableException.class)
    public ResponseEntity<RestError> handleOptionNotAvailableException(Exception ex) {
        if (logger.isErrorEnabled())
            logger.error(ex.getMessage(), ex);

        return  ResponseEntity.status(UNPROCESSABLE_ENTITY).body(
            new RestError("MB0003", "This movie isn't available as option in this round. please update " +
                "your information and try again")
        );
    }

    @ExceptionHandler(NoRankingAvailableException.class)
    public ResponseEntity<RestError> handleNoRankingAvailableException(Exception ex) {
        if (logger.isErrorEnabled())
            logger.error(ex.getMessage(), ex);

        return  ResponseEntity.status(NOT_FOUND).body(
            new RestError("MB0004", "The ranking isn't available. please try again later")
        );
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<RestError> handleBadCredentialsException(Exception ex) {
        if (logger.isErrorEnabled())
            logger.error(ex.getMessage(), ex);

        return  ResponseEntity.status(UNAUTHORIZED).body(
            new RestError("MB0401", "Unauthorized access. Please contact ada.tech support")
        );
    }

    @ExceptionHandler(UserExistsException.class)
    public ResponseEntity<RestError> handleUserExistsException(Exception ex) {
        if (logger.isErrorEnabled())
            logger.error(ex.getMessage(), ex);

        return  ResponseEntity.status(UNPROCESSABLE_ENTITY).body(
            new RestError("MB0005", "User already exists.")
        );
    }

    protected record RestError(String code, String message) { }
}
