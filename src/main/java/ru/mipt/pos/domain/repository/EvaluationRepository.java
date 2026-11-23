package ru.mipt.pos.domain.repository;

import ru.mipt.pos.domain.model.CriterionId;
import ru.mipt.pos.domain.model.Evaluation;
import ru.mipt.pos.domain.model.StudentId;

import java.util.List;
import java.util.Optional;

/** Порт хранения оценок. */
public interface EvaluationRepository {
    void save(Evaluation evaluation);
    Optional<Evaluation> find(StudentId studentId, CriterionId criterionId);
    List<Evaluation> findByStudent(StudentId studentId);
    void delete(StudentId studentId, CriterionId criterionId);
}
