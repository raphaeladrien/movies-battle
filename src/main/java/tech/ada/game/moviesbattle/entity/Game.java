package tech.ada.game.moviesbattle.entity;

import static jakarta.persistence.CascadeType.ALL;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "Games")
public class Game extends Auditable {

    @Id
    @GeneratedValue
    private UUID id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    private Integer errors;
    @Column(name = "in_progress")
    private Boolean inProgress;
    @OneToMany(mappedBy = "game", orphanRemoval = true, cascade = ALL)
    private List<Round> rounds;

    public Game(UUID id, User user, Integer errors, Boolean inProgress, List<Round> rounds) {
        this.id = id;
        this.user = user;
        this.errors = errors;
        this.inProgress = inProgress;
        this.rounds = rounds;
    }

    public Game(User user, Integer errors, Boolean inProgress, List<Round> rounds) {
        this.user = user;
        this.errors = errors;
        this.inProgress = inProgress;
        this.rounds = rounds;
    }

    public Game() {
        super();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Integer getErrors() {
        return errors;
    }

    public void setErrors(Integer errors) {
        this.errors = errors;
    }

    public Boolean getInProgress() {
        return inProgress;
    }

    public void setInProgress(Boolean inProgress) {
        this.inProgress = inProgress;
    }

    public List<Round> getRounds() {
        return rounds;
    }

    public void addRound(Round round) {
        if (rounds == null) rounds = new ArrayList<>(10);

        rounds.add(round);
    }

    public void incrementErrorCount() {
        errors++;
    }
}
