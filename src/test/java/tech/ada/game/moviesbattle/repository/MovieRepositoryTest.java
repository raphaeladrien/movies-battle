package tech.ada.game.moviesbattle.repository;

import jakarta.transaction.Transactional;
import org.apache.commons.lang3.RandomStringUtils;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import tech.ada.game.moviesbattle.entity.Movie;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@Transactional
class MovieRepositoryTest {

    @Autowired
    private MovieRepository subject;

    @BeforeEach
    void setup() {
        final List<Movie> movies = new ArrayList<>(10);
        for(int i=0; i<10; i++) {
            movies.add(buildMovie());
        }

        subject.saveAll(movies);
    }

    @Test
    @DisplayName("ensure that two random movies will be returned")
    void ensure_two_random_movies_will_returned() {
        final List<Movie> movies = subject.findTwoRandomMovies();
        assertEquals(2, movies.size(), "Number of movies returned must be 2");
    }

    @Test
    @DisplayName("ensure that two random movies are different")
    void ensure_two_random_movies_different() {
        final List<Movie> movies = subject.findTwoRandomMovies();
        assertThat(movies.get(0))
            .usingRecursiveComparison()
            .ignoringFields("year", "imdbRating", "imdbVotes")
            .isNotEqualTo(movies.get(1));
    }

    private Movie buildMovie() {
        return new Movie(
            RandomStringUtils.random(9),
            1994,
            RandomStringUtils.random(9),
            RandomStringUtils.random(9),
            9.7f,
            2000L,
            RandomStringUtils.random(9)
        );
    }

}
