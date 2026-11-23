package ru.mipt.pos.domain.model;

import ru.mipt.pos.domain.DomainException;

/** VO: балл. Неизменяемый, сравнивается по значению; отрицательным или NaN быть не может. */
public record Score(double value) {
    public Score {
        if (!Double.isFinite(value) || value < 0)
            throw new DomainException("Балл должен быть неотрицательным числом: " + value);
    }

    public boolean exceeds(Score max) { return value > max.value; }

    public double ratioTo(Score max) { return value / max.value; }

    @Override public String toString() {
        return value % 1 == 0 ? String.valueOf((long) value) : String.valueOf(value);
    }
}
