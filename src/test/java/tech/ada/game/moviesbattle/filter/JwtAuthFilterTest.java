package tech.ada.game.moviesbattle.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.userdetails.UserDetailsService;
import tech.ada.game.moviesbattle.entity.User;
import tech.ada.game.moviesbattle.service.JwtService;

import java.io.IOException;

class JwtAuthFilterTest {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String JWT_TOKEN_PREFIX = "Bearer ";

    private final JwtService jwtService = mock(JwtService.class);
    private final UserDetailsService userDetailsService = mock(UserDetailsService.class);
    private final SecurityContext securityContext = mock(SecurityContext.class);
    private final Authentication authentication = mock(Authentication.class);

    private final HttpServletRequest request = mock(HttpServletRequest.class);
    private final HttpServletResponse response = mock(HttpServletResponse.class);
    private final FilterChain filterChain = mock(FilterChain.class);

    private final String jwt = "eyJhbGciOiJIUzI1NiJ9" +
        ".eyJzdWIiOiJhLXN1cGVyLXVzZXIiLCJpYXQiOjE2ODI4NTQ5MjAsImV4cCI6MTY4Mjk0MTMyMH0" +
        ".GWuXc2RgaTtYmFIa-Cfoom2pxY8YkpANdq6damqTO7E";


    private JwtAuthFilter jwtAuthFilter = new JwtAuthFilter(jwtService, userDetailsService, securityContext);

    @Test
    @DisplayName("when Authorization header is null, delegates to filter chain")
    void when_auth_header_is_null_delegates_filter_chain() throws ServletException, IOException {

        when(request.getHeader(AUTHORIZATION_HEADER)).thenReturn(null);

        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
        verifyNoInteractions(jwtService, userDetailsService);
    }

    @Test
    @DisplayName("when Authorization header has an invalid format, delegates to filter chain")
    void when_auth_header_has_invalid_format_delegates_filter_chain() throws ServletException, IOException {

        when(request.getHeader(AUTHORIZATION_HEADER)).thenReturn("a-super-toke");

        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
        verifyNoInteractions(jwtService, userDetailsService);
    }

    @Test
    @DisplayName("when a valid Authorization header is provided and SecurityContext is outdated, should update " +
        "SecurityContext and delegate to filter chain")
    void when_valid_auth_header_provided_update_security_context_delegate_filter_chain() throws ServletException, IOException {
        final String username = "a-super-user";
        final User user = new User(username, "a-password");

        when(request.getHeader(AUTHORIZATION_HEADER)).thenReturn(JWT_TOKEN_PREFIX + jwt);
        when(jwtService.extractUsername(jwt)).thenReturn(username);
        when(securityContext.getAuthentication()).thenReturn(null);
        when(userDetailsService.loadUserByUsername(username)).thenReturn(user);
        when(jwtService.isTokenValid(jwt, user)).thenReturn(true);

        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
        verify(securityContext, times(1)).setAuthentication(any());
    }

    @Test
    @DisplayName("when a valid Authorization header is provided and SecurityContext is updated, delegates to filter chain")
    void when_valid_auth_header_provided_delegate_filter_chain() throws ServletException, IOException {
        final String username = "a-super-user";

        when(request.getHeader(AUTHORIZATION_HEADER)).thenReturn(JWT_TOKEN_PREFIX + jwt);
        when(jwtService.extractUsername(jwt)).thenReturn(username);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
        verify(securityContext, times(0)).setAuthentication(any());
    }
}
