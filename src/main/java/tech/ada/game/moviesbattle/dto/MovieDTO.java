package tech.ada.game.moviesbattle.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.math.NumberUtils;

@JsonIgnoreProperties(ignoreUnknown = true)
public record MovieDTO(
    @JsonProperty("Title")
    String title,
    @JsonProperty("Year")
    String year,
    @JsonProperty("Director")
    String director,
    @JsonProperty("Actors")
    String actors,
    @JsonProperty("imdbRating")
    String imdbRating,
    @JsonProperty("imdbVotes")
    String imdbVotes,
    @JsonProperty("imdbID")
    String imdbId
) {
    public boolean isValid() {
        if (imdbRating == null || imdbRating.isBlank()) {
            return false;
        }

        if (imdbVotes == null || imdbVotes.isBlank()) {
            return false;
        }

        return NumberUtils.isCreatable(imdbVotes.replace(",", "")) &&
            NumberUtils.isCreatable(imdbRating);
    }

    public Integer parsedYear() {
        return Integer.parseInt(year);
    }

    public Float parsedImdbRating() {
        return NumberUtils.createFloat(imdbRating);
    }

    public Long parsedImdbVotes() {
        return NumberUtils.createLong(imdbVotes.replace(",", ""));
    }
}
