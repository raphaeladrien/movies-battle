package tech.ada.game.moviesbattle.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.security.Key;
import java.util.ArrayList;
import java.util.HashMap;

class JwtServiceTest {

    private final String secret = "25442A472D4B6150645367566B59703273357638792F423F4528482B4D625165";
    private final long expirationTime = 86400000;

    private final JwtService subject = new JwtService(secret, expirationTime);

    @Test
    @DisplayName("when a valid token is provided, should return the username")
    void when_valid_token_provided_should_return_username() {
        final String validToken = "eyJhbGciOiJIUzI1NiJ9" +
            ".eyJzdWIiOiJhLXN1cGVyLXVzZXIiLCJpYXQiOjE2ODI4NTQ5MjAsImV4cCI6MTY4Mjk0MTMyMH0" +
            ".GWuXc2RgaTtYmFIa-Cfoom2pxY8YkpANdq6damqTO7E";
        final String username = "a-super-user";

        final String extractedUsername = subject.extractUsername(validToken);

        assertEquals(username, extractedUsername, "Username and token extracted username must be equals");
    }

    @Test
    @DisplayName("when an UserDetails with extra-claims is provided, should generate JWT token")
    void when_userdetails_extra_claims_provided_should_return_username() {
        final UserDetails userDetails = new User("username", "password", new ArrayList<>());

        final String token = subject.generateToken(new HashMap<>(), userDetails);
        final Claims claims = Jwts.parserBuilder().setSigningKey(getSignInKey()).build().parseClaimsJws(token).getBody();
        assertEquals("username", claims.getSubject(), "Username and token extracted username must be equals");
    }

    @Test
    @DisplayName("when an UserDetails without extra-claims is provided, should generate JWT token")
    void when_userdetails_provided_should_return_username() {
        final UserDetails userDetails = new User("username", "password", new ArrayList<>());

        final String token = subject.generateToken(userDetails);
        final Claims claims = Jwts.parserBuilder().setSigningKey(getSignInKey()).build().parseClaimsJws(token).getBody();
        assertEquals("username", claims.getSubject(), "Username and token extracted username must be equals");
    }

    @Test
    @DisplayName("when an expired token is provided, throws ExpiredJwtException")
    void when_expired_token_is_provided_throws_ExpiredJwtException() {
        final JwtService jwtService = new JwtService(secret, 0);

        final UserDetails userDetails = new User("username", "password", new ArrayList<>());
        final String token = jwtService.generateToken(userDetails);

        assertThrows(ExpiredJwtException.class, () -> {
            jwtService.isTokenValid(token, userDetails);
        });
    }

    @Test
    @DisplayName("when a valid token is provided, should return true")
    void when_valid_token_is_provided_return_true() {
        final JwtService jwtService = new JwtService(secret, 10000);

        final UserDetails userDetails = new User("username", "password", new ArrayList<>());
        final String token = jwtService.generateToken(userDetails);

        assertTrue(jwtService.isTokenValid(token, userDetails), "Token must be valid in this case");
    }

    private Key getSignInKey() {
        final byte[] keyBites = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBites);
    }
}
