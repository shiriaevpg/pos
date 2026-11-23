package ru.mipt.pos.domain.service;

import ru.mipt.pos.domain.DomainException;
import ru.mipt.pos.domain.model.CriterionId;
import ru.mipt.pos.domain.model.Evaluation;
import ru.mipt.pos.domain.model.FinalGrade;
import ru.mipt.pos.domain.model.Rubric;
import ru.mipt.pos.domain.model.Score;
import ru.mipt.pos.domain.model.StudentId;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Domain Service: итог - это правило над двумя агрегатами (оценки + рубрикатор),
 * поэтому оно не живёт ни в Evaluation, ни в Rubric. Итог не хранится, считается из текущих оценок.
 */
public final class FinalGradeService {
    private final GradingStrategy strategy;

    public FinalGradeService(GradingStrategy strategy) {
        this.strategy = strategy;
    }

    public FinalGrade calculate(StudentId studentId, Rubric rubric, Collection<Evaluation> evaluations) {
        Map<CriterionId, Score> scores = new HashMap<>();
        for (Evaluation e : evaluations) {
            if (!e.studentId().equals(studentId))
                throw new DomainException("Оценка другого студента (" + e.studentId() + ") не может входить в итог " + studentId);
            rubric.criterion(e.criterionId());   // оценка обязана ссылаться на критерий рубрикатора
            scores.put(e.criterionId(), e.score());
        }
        return new FinalGrade(studentId, strategy.percent(rubric, scores));
    }
}
