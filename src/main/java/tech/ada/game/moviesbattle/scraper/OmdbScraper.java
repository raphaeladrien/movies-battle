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
    public CompletableFuture<Boolean> run(final String randomIMDBId) throws JsonProcessingException {
        ResponseEntity<String> response = restTemplate.getForEntity(
            url + "/?apikey=" + apiKey + "&i=" + randomIMDBId + "&type=movie", String.class
        );

        if (logger.isInfoEnabled())
            logger.info(response.getBody());

        CompletableFuture<Boolean> completedFuture = validateResponse(response);
        if (completedFuture != null) return completedFuture;

        final MovieDTO movieDTO = mapper.readValue(response.getBody(), MovieDTO.class);
        return CompletableFuture.completedFuture(movieDTO.isValid());
    }

    private CompletableFuture<Boolean> validateResponse(ResponseEntity<String> response) {
        if (!response.getStatusCode().is2xxSuccessful()) {
            if (logger.isDebugEnabled())
                logger.debug("An invalid status code was provided: {}", response.getStatusCode());
            return CompletableFuture.completedFuture(false);
        }

        var body = response.getBody();

        if (body == null || body.isBlank()) {
            if (logger.isDebugEnabled())
                logger.debug("An empty body was received: {}", response.getStatusCode());
            return CompletableFuture.completedFuture(false);
        }

        if (body.contains("Error getting data.")) {
            if (logger.isDebugEnabled())
                logger.debug("Payload contains an unknown error: {}", body);
            return CompletableFuture.completedFuture(false);
        }
        return null;
    }
}
