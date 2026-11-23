package ru.mipt.pos.domain.model;

import ru.mipt.pos.domain.DomainException;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/** Агрегат (корень): рубрикатор - набор критериев курса. Хранит инвариант «критерии уникальны». */
public final class Rubric {
    private final Map<CriterionId, Criterion> criteria = new LinkedHashMap<>();

    public void add(Criterion criterion) {
        if (criteria.putIfAbsent(criterion.id(), criterion) != null)
            throw new DomainException("Критерий уже есть в рубрикаторе: " + criterion.id());
    }

    /** Инвариант: оценивать можно только критерий из рубрикатора. */
    public Criterion criterion(CriterionId id) {
        Criterion c = criteria.get(id);
        if (c == null) throw new DomainException("Критерия нет в рубрикаторе: " + id);
        return c;
    }

    public Collection<Criterion> criteria() { return Collections.unmodifiableCollection(criteria.values()); }

    public double totalWeight() { return criteria.values().stream().mapToDouble(c -> c.weight().value()).sum(); }
}
