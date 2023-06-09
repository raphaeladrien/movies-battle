package tech.ada.game.moviesbattle.controller;

import org.springframework.hateoas.RepresentationModel;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.ada.game.moviesbattle.interactor.BetMovie;
import tech.ada.game.moviesbattle.interactor.BetMovie.BetResponse;
import tech.ada.game.moviesbattle.interactor.FinishGame;
import tech.ada.game.moviesbattle.interactor.FinishGame.FinishResponse;
import tech.ada.game.moviesbattle.interactor.RetrieveGameOptions;
import tech.ada.game.moviesbattle.interactor.RetrieveGameOptions.MovieResponse;
import tech.ada.game.moviesbattle.interactor.RetrieveRanking;
import tech.ada.game.moviesbattle.interactor.StartGame;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/movies-battle/game")
public class GameController {

    private final StartGame startGame;
    private final RetrieveGameOptions retrieveGameOptions;
    private final BetMovie betMovie;
    private final FinishGame finishGame;
    private final RetrieveRanking retrieveRankingInteractor;

    public GameController(
        StartGame startGame,
        RetrieveGameOptions retrieveGameOptions,
        BetMovie betMovie,
        FinishGame finishGame,
        RetrieveRanking retrieveRankingInteractor
    ) {
        this.startGame = startGame;
        this.retrieveGameOptions = retrieveGameOptions;
        this.betMovie = betMovie;
        this.finishGame = finishGame;
        this.retrieveRankingInteractor = retrieveRankingInteractor;
    }

    @PostMapping("/start")
    public ResponseEntity<StartResponse> start() {
        final UUID id = startGame.call();
        final URI location = ServletUriComponentsBuilder.fromCurrentContextPath()
            .path("/movies-battle/game/{id}/round")
            .buildAndExpand(id)
            .toUri();

        return ResponseEntity.created(location).body(StartResponse.build(id));
    }

    @GetMapping("/{id}/round")
    public ResponseEntity<RoundResponse> round(@PathVariable("id") final UUID gameId) {
        final List<RetrieveGameOptions.MovieResponse> movies = retrieveGameOptions.call(gameId);
        return ResponseEntity.ok(RoundResponse.build(gameId, movies));
    }

    @PostMapping("/{id}/bet")
    public ResponseEntity<BetMovieResponse> bet(
        @PathVariable("id") final UUID gameId,
        @RequestBody final BetRequest betRequest
    ) {
        final BetResponse response = betMovie.call(gameId, betRequest.movieId);
        return ResponseEntity.ok(BetMovieResponse.build(response, gameId));
    }

    @PostMapping("/finish")
    public ResponseEntity<StopResponse> finish() {
        final FinishResponse response = finishGame.call();
        return ResponseEntity.ok(StopResponse.buid(response.getGameId()));
    }

    @GetMapping("/ranking")
    public ResponseEntity<RankingResponse> ranking() {
        final List<RetrieveRanking.Ranking> response = retrieveRankingInteractor.call();
        return ResponseEntity.ok(RankingResponse.buid(response));
    }

    public static class BetRequest {
        private UUID movieId;

        public BetRequest(UUID movieId) {
            this.movieId = movieId;
        }

        public BetRequest() {
            super();
        }

        public UUID getMovieId() {
            return movieId;
        }

        public void setMovieId(UUID movieId) {
            this.movieId = movieId;
        }
    }

    private static class StartResponse extends RepresentationModel<StartResponse> {
        private final UUID id;

        private StartResponse(UUID id) {
            this.id = id;
        }

        public UUID getId() {
            return id;
        }

        public static StartResponse build(UUID id) {
            final StartResponse response = new StartResponse(id);

            response.add(linkTo(methodOn(GameController.class).start()).withSelfRel());
            response.add(linkTo(methodOn(GameController.class).round(id)).withRel("round"));
            response.add(linkTo(methodOn(GameController.class).finish()).withRel("finish"));
            response.add(linkTo(methodOn(GameController.class).ranking()).withRel("ranking"));

            return response;
        }
    }

    private static class RoundResponse extends RepresentationModel<RoundResponse> {
        private final List<MovieResponse> movies;

        private RoundResponse(List<MovieResponse> movies) {
            this.movies = movies;
        }

        public List<MovieResponse> getMovies() {
            return movies;
        }

        public static RoundResponse build(UUID id, List<MovieResponse> movies) {
            final RoundResponse response = new RoundResponse(movies);

            response.add(linkTo(methodOn(GameController.class).round(id)).withSelfRel());
            response.add(linkTo(methodOn(GameController.class).bet(id, new BetRequest())).withRel("bet"));
            response.add(linkTo(methodOn(GameController.class).finish()).withRel("finish"));
            response.add(linkTo(methodOn(GameController.class).ranking()).withRel("ranking"));

            return response;
        }
    }

    private static class BetMovieResponse extends RepresentationModel<BetMovieResponse> {
        private final BetResponse betResponse;

        private BetMovieResponse(BetResponse betResponse) {
            this.betResponse = betResponse;
        }

        public BetResponse getBetResponse() {
            return betResponse;
        }

        public static BetMovieResponse build(BetResponse betResponse, UUID id) {
            final BetMovieResponse response = new BetMovieResponse(betResponse);

            response.add(linkTo(methodOn(GameController.class).bet(id, new BetRequest())).withSelfRel());
            response.add(linkTo(methodOn(GameController.class).round(id)).withRel("round"));
            response.add(linkTo(methodOn(GameController.class).finish()).withRel("finish"));
            response.add(linkTo(methodOn(GameController.class).ranking()).withRel("ranking"));

            return response;
        }
    }

    private static class StopResponse extends RepresentationModel<StopResponse> {
        private final UUID gameId;

        private StopResponse(UUID gameId) {
            this.gameId = gameId;
        }

        public UUID getGameId() {
            return gameId;
        }

        public static StopResponse buid(UUID gameId) {
            final StopResponse response = new StopResponse(gameId);

            response.add(linkTo(methodOn(GameController.class).finish()).withSelfRel());
            response.add(linkTo(methodOn(GameController.class).start()).withRel("start"));
            response.add(linkTo(methodOn(GameController.class).ranking()).withRel("ranking"));

            return response;
        }
    }

    private static class RankingResponse extends RepresentationModel<RankingResponse> {
        private final List<RetrieveRanking.Ranking> ranking;

        private RankingResponse(List<RetrieveRanking.Ranking> ranking) {
            this.ranking = ranking;
        }

        public List<RetrieveRanking.Ranking> getRanking() {
            return ranking;
        }

        public static RankingResponse buid(List<RetrieveRanking.Ranking> ranking) {
            final RankingResponse response = new RankingResponse(ranking);

            response.add(linkTo(methodOn(GameController.class).ranking()).withSelfRel());
            response.add(linkTo(methodOn(GameController.class).start()).withRel("start"));
            response.add(linkTo(methodOn(GameController.class).finish()).withRel("finish"));

            return response;
        }
    }

}
