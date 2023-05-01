package tech.ada.game.moviesbattle.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class DefaultExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(DefaultExceptionHandler.class);

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<RestError> handleBadCredentialsException(Exception ex) {
        if (logger.isErrorEnabled())
            logger.error(ex.getMessage(), ex);

        return  ResponseEntity.status(UNAUTHORIZED).body(
            buildError(UNAUTHORIZED, "Authentication failed. Please check your credentials and try again.")
        );
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<RestError> handleUsernameNotFoundException(Exception ex) {
        if (logger.isErrorEnabled())
            logger.error(ex.getMessage(), ex);

        return  ResponseEntity.status(UNAUTHORIZED).body(
            buildError(UNAUTHORIZED, "Authentication failed. Please check your credentials and try again.")
        );
    }

    private RestError buildError(HttpStatus status, String message) {
        return new RestError(status.toString(), message);
    }

    private record RestError(String status, String message) { }
}
