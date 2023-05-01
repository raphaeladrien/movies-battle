package tech.ada.game.moviesbattle.controler;

import com.fasterxml.jackson.databind.ObjectMapper;
import static org.hamcrest.CoreMatchers.equalTo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
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
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
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
import tech.ada.game.moviesbattle.interactor.StartGame;

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
            final UUID gameId = UUID.randomUUID();
            when(startGame.call()).thenThrow(new RuntimeException("an super error"));

            final MockHttpServletRequestBuilder request = post(BASE_URL + "/start");

            mockMvc.perform(request)
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code", equalTo("MB0500")));

            verify(startGame, times(1)).call();
        }
    }

    @AfterEach
    void teardown() {
        reset(startGame);
    }
}
