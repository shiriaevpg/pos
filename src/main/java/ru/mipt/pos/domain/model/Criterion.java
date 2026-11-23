package ru.mipt.pos.domain.model;

import ru.mipt.pos.domain.DomainException;

import java.util.Objects;

/** Entity внутри агрегата Rubric: критерий - за что ставят балл (максимум + вклад в итог). */
public final class Criterion {
    private final CriterionId id;
    private final String name;
    private final Score maxScore;
    private final Weight weight;

    public Criterion(CriterionId id, String name, Score maxScore, Weight weight) {
        if (name == null || name.isBlank()) throw new DomainException("Название критерия не может быть пустым");
        if (maxScore.value() <= 0) throw new DomainException("max_score критерия должен быть больше нуля");
        this.id = Objects.requireNonNull(id);
        this.name = name.trim();
        this.maxScore = maxScore;
        this.weight = Objects.requireNonNull(weight);
    }

    /** Инвариант: балл не выходит за пределы критерия. */
    public void ensureAccepts(Score score) {
        if (score.exceeds(maxScore))
            throw new DomainException("Балл " + score + " выходит за пределы критерия «" + name + "» (максимум " + maxScore + ")");
    }

    public CriterionId id() { return id; }
    public String name() { return name; }
    public Score maxScore() { return maxScore; }
    public Weight weight() { return weight; }

    @Override public boolean equals(Object o) { return o instanceof Criterion c && id.equals(c.id); }
    @Override public int hashCode() { return id.hashCode(); }
}
