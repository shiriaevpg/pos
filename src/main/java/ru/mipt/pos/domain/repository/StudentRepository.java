package ru.mipt.pos.domain.repository;

import ru.mipt.pos.domain.model.Student;
import ru.mipt.pos.domain.model.StudentId;

import java.util.List;
import java.util.Optional;

/** Порт хранения студентов. Домен не знает, что за ним (память, файл, БД). */
public interface StudentRepository {
    void save(Student student);
    Optional<Student> find(StudentId id);
    List<Student> findAll();
}
