package tech.ada.game.moviesbattle.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import tech.ada.game.moviesbattle.interactor.exception.GameNotFoundException;
import tech.ada.game.moviesbattle.interactor.exception.MaxNumberAttemptsException;
import tech.ada.game.moviesbattle.interactor.exception.NoRankingAvailableException;
import tech.ada.game.moviesbattle.interactor.exception.OptionNotAvailableException;
import tech.ada.game.moviesbattle.interactor.exception.UserExistsException;

class DefaultExceptionHandlerTest {

    private final DefaultExceptionHandler defaultExceptionHandler = new DefaultExceptionHandler();

    @Test
    void testHandleRuntimeExceptionException() {
        Exception ex = new RuntimeException("Test exception");

        final ResponseEntity<DefaultExceptionHandler.RestError> responseEntity = defaultExceptionHandler
            .handleRuntimeExceptionException(ex);

        assertEquals(INTERNAL_SERVER_ERROR, responseEntity.getStatusCode(), "Status code must be the same");
    }

    @Test
    void testHandleGameNotFoundException() {
        Exception ex = new GameNotFoundException("Test exception");

        final ResponseEntity<DefaultExceptionHandler.RestError> responseEntity = defaultExceptionHandler
            .handleGameNotFoundException(ex);

        assertEquals(NOT_FOUND, responseEntity.getStatusCode(), "Status code must be the same");
    }

    @Test
    void testHandleMaxNumberAttemptsException() {
        Exception ex = new MaxNumberAttemptsException("Test exception");

        final ResponseEntity<DefaultExceptionHandler.RestError> responseEntity = defaultExceptionHandler
            .handleMaxNumberAttemptsException(ex);

        assertEquals(UNPROCESSABLE_ENTITY, responseEntity.getStatusCode(), "Status code must be the same");
    }

    @Test
    void testHandleOptionNotAvailableException() {
        Exception ex = new OptionNotAvailableException("Test exception");

        final ResponseEntity<DefaultExceptionHandler.RestError> responseEntity = defaultExceptionHandler
            .handleOptionNotAvailableException(ex);

        assertEquals(UNPROCESSABLE_ENTITY, responseEntity.getStatusCode(), "Status code must be the same");
    }

    @Test
    void testHandleNoRankingAvailableException() {
        Exception ex = new NoRankingAvailableException("Test exception");

        final ResponseEntity<DefaultExceptionHandler.RestError> responseEntity = defaultExceptionHandler
            .handleNoRankingAvailableException(ex);

        assertEquals(NOT_FOUND, responseEntity.getStatusCode(), "Status code must be the same");
    }

    @Test
    void testHandleBadCredentialsException() {
        Exception ex = new BadCredentialsException("Test exception");

        final ResponseEntity<DefaultExceptionHandler.RestError> responseEntity = defaultExceptionHandler
            .handleBadCredentialsException(ex);

        assertEquals(UNAUTHORIZED, responseEntity.getStatusCode(), "Status code must be the same");
    }

    @Test
    void testHandleUserExistsException() {
        Exception ex = new UserExistsException("Test exception");

        final ResponseEntity<DefaultExceptionHandler.RestError> responseEntity = defaultExceptionHandler
            .handleUserExistsException(ex);

        assertEquals(UNPROCESSABLE_ENTITY, responseEntity.getStatusCode(), "Status code must be the same");
    }
}

