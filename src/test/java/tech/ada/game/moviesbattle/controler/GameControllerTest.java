package tech.ada.game.moviesbattle.controler;

import com.fasterxml.jackson.databind.ObjectMapper;
import static org.hamcrest.CoreMatchers.equalTo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import tech.ada.game.moviesbattle.controller.GameController.BetRequest;
import tech.ada.game.moviesbattle.interactor.BetMovie;
import tech.ada.game.moviesbattle.interactor.FinishGame;
import tech.ada.game.moviesbattle.interactor.RetrieveGameOptions;
import tech.ada.game.moviesbattle.interactor.StartGame;
import tech.ada.game.moviesbattle.interactor.exception.GameNotFoundException;
import tech.ada.game.moviesbattle.interactor.exception.MaxNumberAttemptsException;
import tech.ada.game.moviesbattle.interactor.exception.OptionNotAvailableException;

import java.util.List;
import java.util.UUID;

@SpringBootTest
@ExtendWith(SpringExtension.class)
class GameControllerTest {

    private static final String BASE_URL = "/movies-battle/game";

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private StartGame startGame;

    @MockBean
    private RetrieveGameOptions retrieveGameOptions;

    @MockBean
    private BetMovie betMovie;

    @MockBean
    private FinishGame finishGame;

    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Nested
    @DisplayName("when endpoint /start is called")
    class WhenEndpointStartIsCalled {

        @Test
        @DisplayName("when game is created with successful, returns HTTP 201")
        void when_game_is_create_with_successful_returns_created() throws Exception {
            final UUID gameId = UUID.randomUUID();
            when(startGame.call()).thenReturn(gameId);

            final MockHttpServletRequestBuilder request = post(BASE_URL + "/start");

            mockMvc.perform(request)
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id", equalTo(gameId.toString())))
                .andExpect(jsonPath("$._links.self.href",
                    equalTo("http://localhost/movies-battle/game/start"))
                )
                .andExpect(jsonPath("$._links.round.href",
                    equalTo("http://localhost/movies-battle/game/" + gameId + "/round"))
                )
                .andExpect(jsonPath("$._links.finish.href",
                    equalTo("http://localhost/movies-battle/game/finish"))
                )
                .andExpect(jsonPath("$._links.ranking.href",
                    equalTo("http://localhost/movies-battle/game/ranking"))
                );

            verify(startGame, times(1)).call();
        }

        @Test
        @DisplayName("when game an unexpected exception occurs, returns HTTP 500")
        void when_game_an_unexpected_exception_returns_internal_error() throws Exception {
            when(startGame.call()).thenThrow(new RuntimeException("an super error"));

            final MockHttpServletRequestBuilder request = post(BASE_URL + "/start");

            mockMvc.perform(request)
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code", equalTo("MB0500")));

            verify(startGame, times(1)).call();
        }
    }

    @Nested
    @DisplayName("when endpoint /:id/round is called")
    class WhenEndpointRoundIsCalled {

        @Test
        @DisplayName("and game exists, returns HTTP 200")
        void when_game_is_create_with_successful_returns_created() throws Exception {
            final UUID gameId = UUID.randomUUID();
            final UUID firstMovieId = UUID.randomUUID();
            final UUID secondMovieId = UUID.randomUUID();
            when(retrieveGameOptions.call(gameId)).thenReturn(build(firstMovieId, secondMovieId));

            final MockHttpServletRequestBuilder request = get(BASE_URL + "/" + gameId + "/round");

            mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.movies[0].id", equalTo(firstMovieId.toString())))
                .andExpect(jsonPath("$.movies[0].title", equalTo("a-title")))
                .andExpect(jsonPath("$.movies[0].year", equalTo(1994)))
                .andExpect(jsonPath("$.movies[0].directors", equalTo("a-director")))
                .andExpect(jsonPath("$.movies[1].id", equalTo(secondMovieId.toString())))
                .andExpect(jsonPath("$.movies[1].title", equalTo("a-title-2")))
                .andExpect(jsonPath("$.movies[1].year", equalTo(1984)))
                .andExpect(jsonPath("$.movies[1].directors", equalTo("a-director-2")))
                .andExpect(jsonPath("$._links.self.href",
                    equalTo("http://localhost/movies-battle/game/" + gameId + "/round"))
                )
                .andExpect(jsonPath("$._links.bet.href",
                    equalTo("http://localhost/movies-battle/game/"  + gameId + "/bet"))
                )
                .andExpect(jsonPath("$._links.finish.href",
                    equalTo("http://localhost/movies-battle/game/finish"))
                )
                .andExpect(jsonPath("$._links.ranking.href",
                    equalTo("http://localhost/movies-battle/game/ranking"))
                );

            verify(retrieveGameOptions, times(1)).call(gameId);
        }

        @Test
        @DisplayName("and game isn't found, returns HTTP 404")
        void when_game_is_not_found_returns_not_found() throws Exception {
            final UUID gameId = UUID.randomUUID();

            when(retrieveGameOptions.call(gameId)).thenThrow(new GameNotFoundException("Not found"));

            final MockHttpServletRequestBuilder request = get(BASE_URL + "/" + gameId + "/round");

            mockMvc.perform(request)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code", equalTo("MB0001")))
                .andExpect(jsonPath("$.message", equalTo("We couldn't find the game on our server. " +
                    "please start a new game")));

            verify(retrieveGameOptions, times(1)).call(gameId);
        }

        @Test
        @DisplayName("and max number of attempts was achieved, returns HTTP 422")
        void when_max_number_attempts_was_achieved_unprocessable_entity() throws Exception {
            final UUID gameId = UUID.randomUUID();

            when(retrieveGameOptions.call(gameId)).thenThrow(new MaxNumberAttemptsException("max-attempt"));

            final MockHttpServletRequestBuilder request = get(BASE_URL + "/" + gameId + "/round");

            mockMvc.perform(request)
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.code", equalTo("MB0002")))
                .andExpect(jsonPath("$.message", equalTo("Max number of attempts was achieved. " +
                    "please start a new game")));

            verify(retrieveGameOptions, times(1)).call(gameId);
        }

        @Test
        @DisplayName("and unexpected exception occurs, returns HTTP 500")
        void when_unexpected_exception_occurs_internal_error() throws Exception {
            final UUID gameId = UUID.randomUUID();

            when(retrieveGameOptions.call(gameId)).thenThrow(new RuntimeException("an-error"));

            final MockHttpServletRequestBuilder request = get(BASE_URL + "/" + gameId + "/round");

            mockMvc.perform(request)
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code", equalTo("MB0500")))
                .andExpect(jsonPath("$.message", equalTo("An internal server error occurred. " +
                    "Please contact ada.tech support.")));

            verify(retrieveGameOptions, times(1)).call(gameId);
        }

        private List<RetrieveGameOptions.MovieResponse> build(UUID firstId, UUID secondId) {
            return List.of(
                new RetrieveGameOptions.MovieResponse(
                    firstId,
                    "a-title",
                    1994,
                    "a-director"
                ),
                new RetrieveGameOptions.MovieResponse(
                    secondId,
                    "a-title-2",
                    1984,
                    "a-director-2"
                )
            );
        }
    }

    @Nested
    @DisplayName("when endpoint /:id/bet is called")
    class WhenEndpointBetIsCalled {

        @Test
        @DisplayName("and the option provided was accepted, returns HTTP 200")
        void and_option_provided_was_accepted_returns_ok() throws Exception {
            final UUID gameId = UUID.randomUUID();
            final UUID movieId = UUID.randomUUID();
            final BetRequest payload = new BetRequest(movieId);

            when(betMovie.call(gameId, movieId)).thenReturn(new BetMovie.BetResponse(
                true, 2
            ));

            final MockHttpServletRequestBuilder request = post(BASE_URL + "/" + gameId + "/bet")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsBytes(payload));

            mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.betResponse.nailedIt", equalTo(true)))
                .andExpect(jsonPath("$.betResponse.errors", equalTo(2)))
                .andExpect(jsonPath("$._links.self.href",
                    equalTo("http://localhost/movies-battle/game/" + gameId + "/bet"))
                )
                .andExpect(jsonPath("$._links.round.href",
                    equalTo("http://localhost/movies-battle/game/"  + gameId + "/round"))
                )
                .andExpect(jsonPath("$._links.finish.href",
                    equalTo("http://localhost/movies-battle/game/finish"))
                )
                .andExpect(jsonPath("$._links.ranking.href",
                    equalTo("http://localhost/movies-battle/game/ranking"))
                );

            verify(betMovie, times(1)).call(gameId, movieId);
        }

        @Test
        @DisplayName("and game wasn't found, returns HTTP 404")
        void and_game_was_not_found_returns_not_found() throws Exception {
            final UUID gameId = UUID.randomUUID();
            final UUID movieId = UUID.randomUUID();
            final BetRequest payload = new BetRequest(movieId);

            when(betMovie.call(gameId, movieId)).thenThrow(new GameNotFoundException("not found"));

            final MockHttpServletRequestBuilder request = post(BASE_URL + "/" + gameId + "/bet")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsBytes(payload));

            mockMvc.perform(request)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code", equalTo("MB0001")))
                .andExpect(jsonPath("$.message", equalTo("We couldn't find the game on our server. " +
                    "please start a new game")));

            verify(betMovie, times(1)).call(gameId, movieId);
        }

        @Test
        @DisplayName("and max number of attempts was achieved, returns HTTP 422")
        void max_number_attempts_was_achieved_returns_unprocessable_entity() throws Exception {
            final UUID gameId = UUID.randomUUID();
            final UUID movieId = UUID.randomUUID();
            final BetRequest payload = new BetRequest(movieId);

            when(betMovie.call(gameId, movieId)).thenThrow(new MaxNumberAttemptsException("max number attempts"));

            final MockHttpServletRequestBuilder request = post(BASE_URL + "/" + gameId + "/bet")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsBytes(payload));

            mockMvc.perform(request)
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.code", equalTo("MB0002")))
                .andExpect(jsonPath("$.message", equalTo("Max number of attempts was achieved. " +
                    "please start a new game")));

            verify(betMovie, times(1)).call(gameId, movieId);
        }

        @Test
        @DisplayName("and movie isn't available at this round, returns HTTP 422")
        void movie_not_available_returns_unprocessable_entity() throws Exception {
            final UUID gameId = UUID.randomUUID();
            final UUID movieId = UUID.randomUUID();
            final BetRequest payload = new BetRequest(movieId);

            when(betMovie.call(gameId, movieId)).thenThrow(new OptionNotAvailableException("option not available"));

            final MockHttpServletRequestBuilder request = post(BASE_URL + "/" + gameId + "/bet")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsBytes(payload));

            mockMvc.perform(request)
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.code", equalTo("MB0004")))
                .andExpect(jsonPath("$.message", equalTo("This movie isn't available as option in " +
                    "this round. please update your information and try again")));

            verify(betMovie, times(1)).call(gameId, movieId);
        }

        @Test
        @DisplayName("and unexpected error occurred, returns HTTP 500")
        void unexpected_error_occurred_returns_internal_error() throws Exception {
            final UUID gameId = UUID.randomUUID();
            final UUID movieId = UUID.randomUUID();
            final BetRequest payload = new BetRequest(movieId);

            when(betMovie.call(gameId, movieId)).thenThrow(new RuntimeException("option not available"));

            final MockHttpServletRequestBuilder request = post(BASE_URL + "/" + gameId + "/bet")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsBytes(payload));

            mockMvc.perform(request)
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code", equalTo("MB0500")))
                .andExpect(jsonPath("$.message", equalTo("An internal server error occurred. " +
                    "Please contact ada.tech support.")));

            verify(betMovie, times(1)).call(gameId, movieId);
        }
    }

    @Nested
    @DisplayName("when endpoint /finish is called")
    class WhenEndpointFinishIsCalled {

        @Test
        @DisplayName("the game is finished, returns HTTP 200")
        void the_game_finished_returns_ok() throws Exception {
            final UUID gameId = UUID.randomUUID();

            when(finishGame.call()).thenReturn(new FinishGame.FinishResponse(gameId));

            final MockHttpServletRequestBuilder request = post(BASE_URL + "/finish");

            mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.gameId", equalTo(gameId.toString())))
                .andExpect(jsonPath("$._links.self.href",
                    equalTo("http://localhost/movies-battle/game/finish"))
                )
                .andExpect(jsonPath("$._links.start.href",
                    equalTo("http://localhost/movies-battle/game/start"))
                )
                .andExpect(jsonPath("$._links.ranking.href",
                    equalTo("http://localhost/movies-battle/game/ranking"))
                );

            verify(finishGame, times(1)).call();
        }

        @Test
        @DisplayName("the game is not found, returns HTTP 404")
        void the_game_not_found_returns_not_found() throws Exception {
            when(finishGame.call()).thenThrow(new GameNotFoundException("game not found"));

            final MockHttpServletRequestBuilder request = post(BASE_URL + "/finish");

            mockMvc.perform(request)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code", equalTo("MB0001")))
                .andExpect(jsonPath("$.message", equalTo("We couldn't find the game on our server. " +
                    "please start a new game")));

            verify(finishGame, times(1)).call();
        }

        @Test
        @DisplayName("and an unexpected error occurred, returns HTTP 500")
        void and_unexpected_error_occurred_returns_internal_error() throws Exception {
            when(finishGame.call()).thenThrow(new RuntimeException("an error"));

            final MockHttpServletRequestBuilder request = post(BASE_URL + "/finish");

            mockMvc.perform(request)
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code", equalTo("MB0500")))
                .andExpect(jsonPath("$.message", equalTo("An internal server error occurred. " +
                    "Please contact ada.tech support.")));

            verify(finishGame, times(1)).call();
        }
    }

    @AfterEach
    void teardown() {
        reset(startGame);
    }
}
