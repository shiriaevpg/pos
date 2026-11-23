package ru.mipt.pos.infrastructure;

import ru.mipt.pos.domain.model.CriterionId;
import ru.mipt.pos.domain.model.Evaluation;
import ru.mipt.pos.domain.model.StudentId;
import ru.mipt.pos.domain.repository.EvaluationRepository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class InMemoryEvaluationRepository implements EvaluationRepository {
    private record Key(StudentId student, CriterionId criterion) { }

    private final Map<Key, Evaluation> store = new LinkedHashMap<>();

    @Override public void save(Evaluation e) { store.put(new Key(e.studentId(), e.criterionId()), e); }

    @Override public Optional<Evaluation> find(StudentId s, CriterionId c) {
        return Optional.ofNullable(store.get(new Key(s, c)));
    }

    @Override public List<Evaluation> findByStudent(StudentId s) {
        return new ArrayList<>(store.values().stream().filter(e -> e.studentId().equals(s)).toList());
    }

    @Override public void delete(StudentId s, CriterionId c) { store.remove(new Key(s, c)); }
}
