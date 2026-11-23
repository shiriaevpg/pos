package ru.mipt.pos.domain.service;

import ru.mipt.pos.domain.model.CriterionId;
import ru.mipt.pos.domain.model.Rubric;
import ru.mipt.pos.domain.model.Score;

import java.util.Map;

/** GoF (структурный): Декоратор: добавляет округление к любой стратегии, не меняя её код. */
public final class RoundedStrategy implements GradingStrategy {
    private final GradingStrategy delegate;
    private final double factor;

    public RoundedStrategy(GradingStrategy delegate, int decimals) {
        this.delegate = delegate;
        this.factor = Math.pow(10, decimals);
    }

    @Override public double percent(Rubric rubric, Map<CriterionId, Score> scores) {
        return Math.round(delegate.percent(rubric, scores) * factor) / factor;
    }
}
