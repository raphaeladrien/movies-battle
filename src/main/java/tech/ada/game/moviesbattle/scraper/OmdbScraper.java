package tech.ada.game.moviesbattle.scraper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import tech.ada.game.moviesbattle.dto.MovieDTO;
import tech.ada.game.moviesbattle.entity.Movie;

import java.util.concurrent.CompletableFuture;

@Service
public class OmdbScraper {

    private static final Logger logger = LoggerFactory.getLogger(OmdbScraper.class);

    private final RestTemplate restTemplate;
    private final ObjectMapper mapper;
    private final String apiKey;
    private final String url;

    public OmdbScraper(
        RestTemplate restTemplate,
        ObjectMapper mapper,
        @Value("${omdb.apikey}") String apiKey,
        @Value("${omdb.url}") String url
    ) {
        this.restTemplate = restTemplate;
        this.mapper = mapper;
        this.apiKey = apiKey;
        this.url = url;
    }

    @Async
    public CompletableFuture<Movie> run(final String randomIMDBId) throws JsonProcessingException {
        ResponseEntity<String> response = restTemplate.getForEntity(
            url + "/?apikey=" + apiKey + "&i=" + randomIMDBId + "&type=movie", String.class
        );

        if (logger.isInfoEnabled())
            logger.info(response.getBody());

        final boolean isValid = isResponseSuccessful(response);
        if (!isValid) return CompletableFuture.completedFuture(null);

        final MovieDTO movieDTO = mapper.readValue(response.getBody(), MovieDTO.class);
        if (!movieDTO.isValid()) return CompletableFuture.completedFuture(null);

        final Movie movie = new Movie(
            movieDTO.title(),
            movieDTO.parsedYear(),
            movieDTO.director(),
            movieDTO.actors(),
            movieDTO.parsedImdbRating(),
            movieDTO.parsedImdbVotes(),
            movieDTO.imdbId()
        );

        return CompletableFuture.completedFuture(movie);
    }

    private Boolean isResponseSuccessful(ResponseEntity<String> response) {
        if (!response.getStatusCode().is2xxSuccessful()) {
            if (logger.isDebugEnabled())
                logger.debug("An invalid status code was provided: {}", response.getStatusCode());
            return false;
        }

        var body = response.getBody();

        if (body == null || body.isBlank()) {
            if (logger.isDebugEnabled())
                logger.debug("An empty body was received: {}", response.getStatusCode());
            return false;
        }

        if (body.contains("Error getting data.")) {
            if (logger.isDebugEnabled())
                logger.debug("Payload contains an unknown error: {}", body);
            return false;
        }

        return true;
    }
}
