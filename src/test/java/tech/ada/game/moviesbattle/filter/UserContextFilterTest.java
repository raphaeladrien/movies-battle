package tech.ada.game.moviesbattle.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import tech.ada.game.moviesbattle.context.UserContextHolder;
import tech.ada.game.moviesbattle.context.UserContextInfo;
import tech.ada.game.moviesbattle.entity.User;
import tech.ada.game.moviesbattle.repository.UserRepository;
import tech.ada.game.moviesbattle.service.JwtService;

import java.util.Optional;
import java.util.UUID;

class UserContextFilterTest {

    private final JwtService jwtService = mock(JwtService.class);
    private final UserContextHolder userContextHolder = mock(UserContextHolder.class);
    private final UserRepository userRepository = mock(UserRepository.class);

    private final HttpServletRequest request = mock(HttpServletRequest.class);
    private final HttpServletResponse response = mock(HttpServletResponse.class);
    private final FilterChain filterChain = mock(FilterChain.class);

    private final User user = mock(User.class);

    private final UserContextFilter subject = new UserContextFilter(jwtService, userRepository, userContextHolder);

    @Test
    @DisplayName("when request URI contains /movies-battle/id, delegates to filter chain")
    void when_request_uri_contains_movies_battle_id_delegates_filter_chain() throws Exception {
        when(request.getRequestURI()).thenReturn("/movies-battle/id/register");

        subject.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
        verifyNoInteractions(jwtService, userContextHolder, userRepository);
    }


    @Nested
    @DisplayName("when request URI does not contains /movies-battle/id")
    class UrlDoesNotContainsMoviesBattleId {

        private final String jwt = "eyJhbGciOiJIUzI1NiJ9." +
            "eyJzdWIiOiJhLXN1cGVyLXVzZXIiLCJpYXQiOjE2ODI4NTQ5MjAsImV4cCI6MTY4Mjk0MTMyMH0." +
            "GWuXc2RgaTtYmFIa-Cfoom2pxY8YkpANdq6damqTO7E";
        private final String username = "a-super-user";

        @BeforeEach
        void configureRequest() {
            when(request.getRequestURI()).thenReturn("/movies-battle/start");
            when(request.getHeader("Authorization")).thenReturn("Bearer " + jwt);
        }

        @Test
        @DisplayName("and user is found in the database, fills UserContextHolder")
        void and_user_found_database_fills_user_context_holder() throws Exception {
            final ArgumentCaptor<UserContextInfo> userContextInfoCaptor = ArgumentCaptor.forClass(UserContextInfo.class);
            final UUID uuid = UUID.randomUUID();

            when(user.getId()).thenReturn(uuid);
            when(user.getUsername()).thenReturn(username);
            when(jwtService.extractUsername(jwt)).thenReturn("a-super-user");
            when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

            subject.doFilterInternal(request, response, filterChain);

            verify(userContextHolder, times(1)).setUserContextInfo(userContextInfoCaptor.capture());
            verify(filterChain, times(1)).doFilter(request, response);
            assertEquals(uuid, userContextInfoCaptor.getValue().user().getId(), "Context user id must be equals to user id");
        }

        @Test
        @DisplayName("and user is found in the database, throws RuntimeException")
        void and_user_is_not_found_database_delegates_filter_chain() throws Exception {
            when(jwtService.extractUsername(jwt)).thenReturn("a-super-user");
            when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

            assertThrows(RuntimeException.class, () -> {
                subject.doFilterInternal(request, response, filterChain);
            });
        }
    }
}
