package ru.mipt.pos.domain.model;

import ru.mipt.pos.domain.DomainException;

import java.util.Objects;

public final class Student {
    private final StudentId id;
    private final String name;

    public Student(StudentId id, String name) {
        if (name == null || name.isBlank()) throw new DomainException("Имя студента не может быть пустым");
        this.id = Objects.requireNonNull(id);
        this.name = name.trim();
    }

    public StudentId id() { return id; }
    public String name() { return name; }

    @Override public boolean equals(Object o) { return o instanceof Student s && id.equals(s.id); }
    @Override public int hashCode() { return id.hashCode(); }
}
