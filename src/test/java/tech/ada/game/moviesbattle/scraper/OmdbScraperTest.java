package tech.ada.game.moviesbattle.scraper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import tech.ada.game.moviesbattle.entity.Movie;

import java.util.concurrent.ExecutionException;

class OmdbScraperTest {

    private final RestTemplate restTemplate = Mockito.mock(RestTemplate.class);
    private final ObjectMapper mapper = new ObjectMapper();
    private final String apiKey = "a-super-api-key";
    private final String url = "https://www.omdbapi.com/";
    private final String randomIMDBId = "tt9582778";
    private final String finalUrl = url + "/?apikey=" + apiKey + "&i=" + randomIMDBId + "&type=movie";

    private final OmdbScraper subject = new OmdbScraper(restTemplate, mapper, apiKey, url);

    @Test
    @DisplayName("when OMDB API returns an status code different 2XX, returns null")
    void when_omdb_returns_status_code_different_2XX_returns_null() throws JsonProcessingException, ExecutionException, InterruptedException {

        final String response = """
            {"Response":"a-super-payload"}
            """;
        final ResponseEntity<String> responseEntity = new ResponseEntity<>(response, NOT_FOUND);

        when(restTemplate.getForEntity(finalUrl, String.class)).thenReturn(responseEntity);

        var result = subject.run(randomIMDBId);

        assertNull(result.get(), "Must return null when the HTTP status code is different 2XX");
    }

    @Test
    @DisplayName("when OMDB API returns an empty payload, returns null")
    void when_omdb_returns_empty_payload_returns_null() throws JsonProcessingException, ExecutionException, InterruptedException {
        final ResponseEntity<String> responseEntity = new ResponseEntity<>("", OK);

        when(restTemplate.getForEntity(finalUrl, String.class)).thenReturn(responseEntity);

        var result = subject.run(randomIMDBId);

        assertNull(result.get(), "Must return null when an empty response body isn't allowed");
    }

    @Test
    @DisplayName("when OMDB API returns an unknown error on payload, returns null")
    void when_omdb_returns_unknown_error_returns_null() throws JsonProcessingException, ExecutionException, InterruptedException {

        final String response = """
            {"Response":"False","Error":"Error getting data."}
            """;
        final ResponseEntity<String> responseEntity = new ResponseEntity<>(response, OK);

        when(restTemplate.getForEntity(finalUrl, String.class)).thenReturn(responseEntity);

        var result = subject.run(randomIMDBId);

        assertNull(result.get(), "Must return null when an error payload is provided");
    }

    @Test
    @DisplayName("when OMDB API returns a movie without imdb rating, returns null")
    void when_omdb_returns_movie_without_imdb_rating_returns_null() throws JsonProcessingException, ExecutionException, InterruptedException {
        final String response = """
            {"Title":"The Shawshank Redemption","Year":"1994","Rated":"R","Released":"14 Oct 1994",
            "Runtime":"142 min","Genre":"Drama","Director":"Frank Darabont","Writer":"Stephen King, Frank Darabont",
            "Actors":"Tim Robbins, Morgan Freeman, Bob Gunton","Plot":"Over the course of several years, two convicts 
            form a friendship, seeking consolation and, eventually, redemption through basic compassion.",
            "Language":"English","Country":"United States","Awards":"Nominated for 7 Oscars. 21 wins & 43 nominations 
            total","Poster":"","Ratings":[],"Metascore":"82",
            "imdbRating":"N/A","imdbVotes":"2,731,095","imdbID":"tt0111161",
            "Type":"movie","DVD":"21 Dec 1999","BoxOffice":"$28,767,189","Production":"N/A","Website":"N/A",
            "Response":"True"}
            """.replaceAll("[\n\r]", "");
        final ResponseEntity<String> responseEntity = new ResponseEntity<>(response, OK);

        when(restTemplate.getForEntity(finalUrl, String.class)).thenReturn(responseEntity);

        var result = subject.run(randomIMDBId);

        assertNull(result.get(), "Must return null when the payload doesn't contains imdb rating");
    }

    @Test
    @DisplayName("when OMDB API returns a movie with empty imdb rating, returns null")
    void when_omdb_returns_movie_with_empty_imdb_rating_returns_null() throws JsonProcessingException, ExecutionException, InterruptedException {
        final String response = """
            {"Title":"The Shawshank Redemption","Year":"1994","Rated":"R","Released":"14 Oct 1994",
            "Runtime":"142 min","Genre":"Drama","Director":"Frank Darabont","Writer":"Stephen King, Frank Darabont",
            "Actors":"Tim Robbins, Morgan Freeman, Bob Gunton","Plot":"Over the course of several years, two convicts 
            form a friendship, seeking consolation and, eventually, redemption through basic compassion.",
            "Language":"English","Country":"United States","Awards":"Nominated for 7 Oscars. 21 wins & 43 nominations 
            total","Poster":"","Ratings":[],"Metascore":"82",
            "imdbRating":"","imdbVotes":"2,731,095","imdbID":"tt0111161",
            "Type":"movie","DVD":"21 Dec 1999","BoxOffice":"$28,767,189","Production":"N/A","Website":"N/A",
            "Response":"True"}
            """.replaceAll("[\n\r]", "");
        final ResponseEntity<String> responseEntity = new ResponseEntity<>(response, OK);

        when(restTemplate.getForEntity(finalUrl, String.class)).thenReturn(responseEntity);

        var result = subject.run(randomIMDBId);

        assertNull(result.get(), "Must return null when the payload have an empty imdb rating");
    }

    @Test
    @DisplayName("when OMDB API returns a movie without imdb votes, returns null")
    void when_omdb_returns_movie_without_imdb_votes_returns_null() throws JsonProcessingException, ExecutionException, InterruptedException {
        final String response = """
            {"Title":"The Shawshank Redemption","Year":"1994","Rated":"R","Released":"14 Oct 1994",
            "Runtime":"142 min","Genre":"Drama","Director":"Frank Darabont","Writer":"Stephen King, Frank Darabont",
            "Actors":"Tim Robbins, Morgan Freeman, Bob Gunton","Plot":"Over the course of several years, two convicts 
            form a friendship, seeking consolation and, eventually, redemption through basic compassion.",
            "Language":"English","Country":"United States","Awards":"Nominated for 7 Oscars. 21 wins & 43 nominations 
            total","Poster":"","Ratings":[],"Metascore":"82",
            "imdbRating":"9.3","imdbVotes":"N/A","imdbID":"tt0111161",
            "Type":"movie","DVD":"21 Dec 1999","BoxOffice":"$28,767,189","Production":"N/A","Website":"N/A",
            "Response":"True"}
            """.replaceAll("[\n\r]", "");
        final ResponseEntity<String> responseEntity = new ResponseEntity<>(response, OK);

        when(restTemplate.getForEntity(finalUrl, String.class)).thenReturn(responseEntity);

        var result = subject.run(randomIMDBId);

        assertNull(result.get(), "Must return null when the payload doesn't contains imdb votes");
    }

    @Test
    @DisplayName("when OMDB API returns a movie with empty imdb rating, returns null")
    void when_omdb_returns_movie_with_empty_imdb_votes_returns_null() throws JsonProcessingException, ExecutionException, InterruptedException {
        final String response = """
            {"Title":"The Shawshank Redemption","Year":"1994","Rated":"R","Released":"14 Oct 1994",
            "Runtime":"142 min","Genre":"Drama","Director":"Frank Darabont","Writer":"Stephen King, Frank Darabont",
            "Actors":"Tim Robbins, Morgan Freeman, Bob Gunton","Plot":"Over the course of several years, two convicts 
            form a friendship, seeking consolation and, eventually, redemption through basic compassion.",
            "Language":"English","Country":"United States","Awards":"Nominated for 7 Oscars. 21 wins & 43 nominations 
            total","Poster":"","Ratings":[],"Metascore":"82",
            "imdbRating":"9.3","imdbVotes":"","imdbID":"tt0111161",
            "Type":"movie","DVD":"21 Dec 1999","BoxOffice":"$28,767,189","Production":"N/A","Website":"N/A",
            "Response":"True"}
            """.replaceAll("[\n\r]", "");
        final ResponseEntity<String> responseEntity = new ResponseEntity<>(response, OK);

        when(restTemplate.getForEntity(finalUrl, String.class)).thenReturn(responseEntity);

        var result = subject.run(randomIMDBId);

        assertNull(result.get(), "Must return null when the payload have an empty imdb votes");
    }

    @Test
    @DisplayName("when OMDB API returns a movie with imdb rating and votes, returns movie")
    void when_omdb_returns_movie_with_imdb_rating_votes_returns_movie() throws JsonProcessingException, ExecutionException, InterruptedException {
        final String response = """
            {"Title":"The Shawshank Redemption","Year":"1994","Rated":"R","Released":"14 Oct 1994",
            "Runtime":"142 min","Genre":"Drama","Director":"Frank Darabont","Writer":"Stephen King, Frank Darabont",
            "Actors":"Tim Robbins, Morgan Freeman, Bob Gunton","Plot":"Over the course of several years, two convicts 
            form a friendship, seeking consolation and, eventually, redemption through basic compassion.",
            "Language":"English","Country":"United States","Awards":"Nominated for 7 Oscars. 21 wins & 43 nominations 
            total","Poster":"","Ratings":[],"Metascore":"82",
            "imdbRating":"9.3","imdbVotes":"2,731,095","imdbID":"tt0111161",
            "Type":"movie","DVD":"21 Dec 1999","BoxOffice":"$28,767,189","Production":"N/A","Website":"N/A",
            "Response":"True"}
            """.replaceAll("[\n\r]", "");
        final Movie movie = new Movie(
            "The Shawshank Redemption",
            1994,
            "Frank Darabont",
            "Tim Robbins, Morgan Freeman, Bob Gunton",
            9.3F,
            2731095L,
            "tt0111161"
        );
        final ResponseEntity<String> responseEntity = new ResponseEntity<>(response, OK);

        when(restTemplate.getForEntity(finalUrl, String.class)).thenReturn(responseEntity);

        var result = subject.run(randomIMDBId);

        assertThat(movie).usingRecursiveComparison().isEqualTo(result.get());
    }
}
