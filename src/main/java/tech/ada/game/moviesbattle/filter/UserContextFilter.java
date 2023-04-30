package tech.ada.game.moviesbattle.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import static org.springframework.core.Ordered.LOWEST_PRECEDENCE;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import tech.ada.game.moviesbattle.context.UserContextHolder;
import tech.ada.game.moviesbattle.context.UserContextInfo;
import tech.ada.game.moviesbattle.entity.User;
import tech.ada.game.moviesbattle.repository.UserRepository;
import tech.ada.game.moviesbattle.service.JwtService;

import java.io.IOException;

@Component
@Order(LOWEST_PRECEDENCE)
public class UserContextFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final UserContextHolder userContextHolder;

    public UserContextFilter(JwtService jwtService, UserRepository userRepository, UserContextHolder userContextHolder) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.userContextHolder = userContextHolder;
    }

    @Override
    protected void doFilterInternal(
        HttpServletRequest request, HttpServletResponse response, FilterChain filterChain
    ) throws ServletException, IOException {

        final String requestedUrl = request.getRequestURI();
        if (requestedUrl.contains("/movies-battle/id")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            final String jwt = request.getHeader("Authorization");

            final String username = jwtService.extractUsername(jwt.substring(7));
            final User user = userRepository.findByUsername(username).orElseThrow(() ->
                new RuntimeException("User " + username +" was not found in DB")
            );

            final UserContextInfo userContextInfo = new UserContextInfo(
                user.getId(), user.getUsername()
            );

            userContextHolder.setUserContextInfo(userContextInfo);

            filterChain.doFilter(request, response);
        } finally {
            userContextHolder.reset();
        }
    }
}
