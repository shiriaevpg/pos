package ru.mipt.pos.domain.model;

import ru.mipt.pos.domain.DomainException;

/** VO: идентификатор студента (логин / номер зачётки). */
public record StudentId(String value) {
    public StudentId {
        if (value == null || value.isBlank()) throw new DomainException("Идентификатор студента не может быть пустым");
        value = value.trim();
    }

    @Override public String toString() { return value; }
}
