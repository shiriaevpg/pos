package ru.mipt.pos.domain.model;

import ru.mipt.pos.domain.DomainException;

/** VO: вес критерия (вклад в итог). Строго положительный. */
public record Weight(double value) {
    public Weight {
        if (!Double.isFinite(value) || value <= 0)
            throw new DomainException("Вес критерия должен быть положительным числом: " + value);
    }
}
