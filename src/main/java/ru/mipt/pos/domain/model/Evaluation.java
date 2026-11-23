package ru.mipt.pos.domain.model;

import ru.mipt.pos.domain.DomainException;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Агрегат: оценка — факт «этому студенту по этому критерию поставили такой балл».
 * Entity: идентичность — пара (студент, критерий); балл и комментарий можно исправить, оценка остаётся той же.
 * Конструктор package-private: создать оценку в обход EvaluationFactory нельзя.
 * На студента и критерий ссылается по id (границы агрегатов).
 */
public final class Evaluation {
    private final StudentId studentId;
    private final CriterionId criterionId;
    private Score score;
    private String comment;
    private LocalDate date;

    Evaluation(StudentId studentId, Criterion criterion, Score score, String comment) {
        criterion.ensureAccepts(score);
        this.studentId = Objects.requireNonNull(studentId);
        this.criterionId = criterion.id();
        this.score = score;
        this.comment = comment == null ? "" : comment.trim();
        this.date = LocalDate.now();
    }

    /** Исправление оценки — те же инварианты, что и при создании. */
    public void correct(Criterion criterion, Score newScore, String newComment) {
        if (!criterion.id().equals(criterionId)) throw new DomainException("Оценка относится к другому критерию");
        criterion.ensureAccepts(newScore);
        this.score = newScore;
        this.comment = newComment == null ? "" : newComment.trim();
        this.date = LocalDate.now();
    }

    public StudentId studentId() { return studentId; }
    public CriterionId criterionId() { return criterionId; }
    public Score score() { return score; }
    public String comment() { return comment; }
    public LocalDate date() { return date; }

    @Override public boolean equals(Object o) {
        return o instanceof Evaluation e && studentId.equals(e.studentId) && criterionId.equals(e.criterionId);
    }
    @Override public int hashCode() { return Objects.hash(studentId, criterionId); }
}
