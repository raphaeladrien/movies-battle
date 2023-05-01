package tech.ada.game.moviesbattle.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import java.util.UUID;

@Entity
@Table(name = "Movies")
public class Movie {

    @Id
    @GeneratedValue
    private UUID id;
    private String title;
    @Column(name = "release_year")
    private Integer year;
    private String director;
    @Column
    private String actors;
    private Float imdbRating;
    private Long imdbVotes;
    private String imdbId;

    public Movie() {
       super();
    }

    public Movie(UUID id, String title, Integer year, String director, String actors, Float imdbRating, Long imdbVotes) {
        this.id = id;
        this.title = title;
        this.year = year;
        this.director = director;
        this.actors = actors;
        this.imdbRating = imdbRating;
        this.imdbVotes = imdbVotes;
    }

    public Movie(
        String title, Integer year, String director, String actors, Float imdbRating, Long imdbVotes, String imdbId
    ) {
        this.title = title;
        this.year = year;
        this.director = director;
        this.actors = actors;
        this.imdbRating = imdbRating;
        this.imdbVotes = imdbVotes;
        this.imdbId = imdbId;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getActors() {
        return actors;
    }

    public void setActors(String actors) {
        this.actors = actors;
    }

    public Float getImdbRating() {
        return imdbRating;
    }

    public void setImdbRating(Float imdbRating) {
        this.imdbRating = imdbRating;
    }

    public Long getImdbVotes() {
        return imdbVotes;
    }

    public void setImdbVotes(Long imdbVotes) {
        this.imdbVotes = imdbVotes;
    }

    public String getImdbId() {
        return imdbId;
    }

    public void setImdbId(String imdbId) {
        this.imdbId = imdbId;
    }

    @Transient
    public Float getScore() {
        return imdbRating * imdbVotes;
    }
}

