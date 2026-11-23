package ru.mipt.pos.infrastructure;

import ru.mipt.pos.domain.model.Rubric;
import ru.mipt.pos.domain.repository.RubricRepository;

public final class InMemoryRubricRepository implements RubricRepository {
    private Rubric rubric = new Rubric();

    @Override public Rubric get() { return rubric; }
    @Override public void save(Rubric rubric) { this.rubric = rubric; }
}
