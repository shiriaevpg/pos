package ru.mipt.pos.domain.service;

import ru.mipt.pos.domain.model.Criterion;
import ru.mipt.pos.domain.model.CriterionId;
import ru.mipt.pos.domain.model.Rubric;
import ru.mipt.pos.domain.model.Score;

import java.util.Map;

/** Взвешенное среднее только по уже оценённым критериям (промежуточный итог посреди семестра). */
public final class GradedOnlyStrategy implements GradingStrategy {
    @Override public double percent(Rubric rubric, Map<CriterionId, Score> scores) {
        double sum = 0, weights = 0;
        for (Criterion c : rubric.criteria()) {
            Score s = scores.get(c.id());
            if (s == null) continue;
            sum += s.ratioTo(c.maxScore()) * c.weight().value();
            weights += c.weight().value();
        }
        return weights == 0 ? 0 : 100 * sum / weights;
    }
}
