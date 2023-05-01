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
import tech.ada.game.moviesbattle.interactor.RetrieveGameOptions;
import tech.ada.game.moviesbattle.interactor.StartGame;
import tech.ada.game.moviesbattle.interactor.exception.GameNotFoundException;
import tech.ada.game.moviesbattle.interactor.exception.MaxNumberAttemptsException;

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

    @AfterEach
    void teardown() {
        reset(startGame);
    }
}
