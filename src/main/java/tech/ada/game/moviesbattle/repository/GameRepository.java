package tech.ada.game.moviesbattle.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import tech.ada.game.moviesbattle.entity.Game;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface GameRepository extends JpaRepository<Game, UUID> {
    Optional<Game> findByUserIdAndInProgress(UUID userId, boolean inProgress);

    @Query("SELECT g FROM Game g LEFT JOIN FETCH g.rounds WHERE g.id = :gameId and g.user.id = :userId and " +
        "g.inProgress = :inProgress")
    Optional<Game> findByIdAndUserIdAndInProgress(UUID gameId, UUID userId, boolean inProgress);

    @Modifying
    @Query("UPDATE Game g SET g.inProgress = :inProgress WHERE g.id = :id")
    int updateInProgressById(UUID id, boolean inProgress);
}
