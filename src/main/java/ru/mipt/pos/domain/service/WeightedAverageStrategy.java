package ru.mipt.pos.domain.service;

import ru.mipt.pos.domain.model.Criterion;
import ru.mipt.pos.domain.model.CriterionId;
import ru.mipt.pos.domain.model.Rubric;
import ru.mipt.pos.domain.model.Score;

import java.util.Map;

/** Взвешенное среднее по ВСЕМУ рубрикатору: неоценённый критерий = 0 баллов. */
public final class WeightedAverageStrategy implements GradingStrategy {
    @Override public double percent(Rubric rubric, Map<CriterionId, Score> scores) {
        if (rubric.criteria().isEmpty()) return 0;
        double sum = 0;
        for (Criterion c : rubric.criteria()) {
            Score s = scores.get(c.id());
            if (s != null) sum += s.ratioTo(c.maxScore()) * c.weight().value();
        }
        return 100 * sum / rubric.totalWeight();
    }
}
