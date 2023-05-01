package tech.ada.game.moviesbattle.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import tech.ada.game.moviesbattle.entity.Movie;

import java.util.List;
import java.util.UUID;

@Repository
public interface MovieRepository extends JpaRepository<Movie, UUID> {

    default List<Movie> findTwoRandomMovies() {
        return findTwoRandomRecords(PageRequest.of(0, 2, Sort.by("id").ascending()));
    }

    @Query(value = "SELECT * FROM Movies ORDER BY RAND()", nativeQuery = true)
    List<Movie> findTwoRandomRecords(Pageable pageable);
}
