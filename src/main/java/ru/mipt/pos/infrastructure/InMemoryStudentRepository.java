package ru.mipt.pos.infrastructure;

import ru.mipt.pos.domain.model.Student;
import ru.mipt.pos.domain.model.StudentId;
import ru.mipt.pos.domain.repository.StudentRepository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class InMemoryStudentRepository implements StudentRepository {
    private final Map<StudentId, Student> store = new LinkedHashMap<>();

    @Override public void save(Student student) { store.put(student.id(), student); }
    @Override public Optional<Student> find(StudentId id) { return Optional.ofNullable(store.get(id)); }
    @Override public List<Student> findAll() { return new ArrayList<>(store.values()); }
}
