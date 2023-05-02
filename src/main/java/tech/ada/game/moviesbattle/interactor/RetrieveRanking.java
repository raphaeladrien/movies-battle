package tech.ada.game.moviesbattle.interactor;

import org.springframework.stereotype.Service;
import tech.ada.game.moviesbattle.entity.Game;
import tech.ada.game.moviesbattle.interactor.exception.NoRankingAvailableException;
import tech.ada.game.moviesbattle.repository.GameRepository;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RetrieveRanking {

    private final GameRepository gameRepository;

    public RetrieveRanking(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    public List<Ranking> call() {
        final List<Game> games = gameRepository.findAllWithRoundsAndUser()
            .orElseThrow(() -> new NoRankingAvailableException("No ranking available"));

        games.sort(Comparator.comparingDouble(Game::score).reversed());
        return games.stream().map(game -> new Ranking(game.getUser().getUsername(), game.score()))
            .collect(Collectors.toList());
    }

    public static class Ranking {
        private String username;
        private double score;

        public Ranking(String username, double score) {
            this.username = username;
            this.score = score;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public double getScore() {
            return score;
        }

        public void setScore(double score) {
            this.score = score;
        }
    }
}
