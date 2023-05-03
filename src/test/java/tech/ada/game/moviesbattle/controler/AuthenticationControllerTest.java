package tech.ada.game.moviesbattle.controler;

import com.fasterxml.jackson.databind.ObjectMapper;
import static org.hamcrest.CoreMatchers.equalTo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import tech.ada.game.moviesbattle.interactor.AuthenticateUser;
import tech.ada.game.moviesbattle.interactor.AuthenticateUser.AuthenticationRequest;
import static tech.ada.game.moviesbattle.interactor.AuthenticateUser.AuthenticationResponse;
import tech.ada.game.moviesbattle.interactor.RegisterUser;

@SpringBootTest
@ExtendWith(SpringExtension.class)
class AuthenticationControllerTest {

    private static final String BASE_URL = "/movies-battle/id";

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private AuthenticateUser authenticateUser;

    @MockBean
    private RegisterUser registerUser;

    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Nested
    @DisplayName("when endpoint /signing is called")
    class WhenEndpointStartIsCalled {

        @Test
        @DisplayName("and the user and password provided in payload is invalid, returns unauthorized")
        void and_user_password_provided_is_invalid_returns_unauthorized() throws Exception {
            final AuthenticationRequest payload = new AuthenticationRequest(
                "user", "password"
            );

            when(authenticateUser.call(payload)).thenThrow(new BadCredentialsException("Not found"));

            final MockHttpServletRequestBuilder request = post(BASE_URL + "/signing")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsBytes(payload));

            mockMvc.perform(request)
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code", equalTo("MB0401")))
                .andExpect(jsonPath("$.message", equalTo("Unauthorized access. Please contact ada.tech " +
                    "support")));

            verify(authenticateUser, times(1)).call(payload);
        }

        @Test
        @DisplayName("and the user and password provided in payload is valid, returns access token")
        void and_user_password_provided_is_invalid_returns_access_token() throws Exception {
            final AuthenticationRequest payload = new AuthenticationRequest(
                "user", "password"
            );
            final String accessToken = "eyJhbGciOiJIUzI1NiJ9" +
                ".eyJzdWIiOiJiYWNhbmEiLCJpYXQiOjE2ODMwOTcwNTMsImV4cCI6MTY4MzE4MzQ1M30" +
                ".t3dF-7defGFljPvY8SnKFdDOuzDYSEg5LZqbnZTZNDI";

            when(authenticateUser.call(payload)).thenReturn(new AuthenticationResponse(
                accessToken
            ));

            final MockHttpServletRequestBuilder request = post(BASE_URL + "/signing")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsBytes(payload));

            mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.access_token", equalTo(accessToken)));

            verify(authenticateUser, times(1)).call(payload);
        }
    }
}
