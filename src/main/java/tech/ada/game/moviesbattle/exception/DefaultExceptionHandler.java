package tech.ada.game.moviesbattle.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
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



    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<RestError> handleBadCredentialsException(Exception ex) {
        if (logger.isErrorEnabled())
            logger.error(ex.getMessage(), ex);

        return  ResponseEntity.status(UNAUTHORIZED).body(
            buildError(UNAUTHORIZED, "Authentication failed. Please check your credentials and try again.")
        );
    }

    private RestError buildError(HttpStatus status, String message) {
        return new RestError(status.toString(), message);
    }

    private record RestError(String code, String message) { }
}
