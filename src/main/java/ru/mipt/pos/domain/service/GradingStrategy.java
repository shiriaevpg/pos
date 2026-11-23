package ru.mipt.pos.domain.service;

import ru.mipt.pos.domain.model.CriterionId;
import ru.mipt.pos.domain.model.Rubric;
import ru.mipt.pos.domain.model.Score;

import java.util.Map;

/**
 * GoF (поведенческий): Стратегия - сменяемое правило расчёта итога.
 * Контракт (важен для LSP): результат всегда в диапазоне [0, 100] процентов.
 */
public interface GradingStrategy {
    double percent(Rubric rubric, Map<CriterionId, Score> scores);
}
