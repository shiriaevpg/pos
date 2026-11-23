package ru.mipt.pos.domain.model;

import ru.mipt.pos.domain.DomainException;

/** VO: код критерия в рубрикаторе (например, "hw", "exam"). */
public record CriterionId(String value) {
    public CriterionId {
        if (value == null || value.isBlank()) throw new DomainException("Код критерия не может быть пустым");
        value = value.trim();
    }

    @Override public String toString() { return value; }
}
