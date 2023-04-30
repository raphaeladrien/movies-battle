package tech.ada.game.moviesbattle.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "Rounds")
public class Round extends Auditable {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "game_id")
    private Game game;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "first_movie")
    private Movie firstMovie;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "second_movie")
    private Movie secondMovie;

    public Round() {
        super();
    }

    public Round(Game game, Movie firstMovie, Movie secondMovie) {
        this.game = game;
        this.firstMovie = firstMovie;
        this.secondMovie = secondMovie;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public Movie getFirstMovie() {
        return firstMovie;
    }

    public void setFirstMovie(Movie firstMovie) {
        this.firstMovie = firstMovie;
    }

    public Movie getSecondMovie() {
        return secondMovie;
    }

    public void setSecondMovie(Movie secondMovie) {
        this.secondMovie = secondMovie;
    }
}
