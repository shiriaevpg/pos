package ru.mipt.pos.application;

import ru.mipt.pos.domain.DomainException;
import ru.mipt.pos.domain.model.Criterion;
import ru.mipt.pos.domain.model.CriterionId;
import ru.mipt.pos.domain.model.Evaluation;
import ru.mipt.pos.domain.model.EvaluationFactory;
import ru.mipt.pos.domain.model.FinalGrade;
import ru.mipt.pos.domain.model.Rubric;
import ru.mipt.pos.domain.model.Score;
import ru.mipt.pos.domain.model.Student;
import ru.mipt.pos.domain.model.StudentId;
import ru.mipt.pos.domain.model.Weight;
import ru.mipt.pos.domain.repository.EvaluationRepository;
import ru.mipt.pos.domain.repository.RubricRepository;
import ru.mipt.pos.domain.repository.StudentRepository;
import ru.mipt.pos.domain.service.FinalGradeService;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * GoF (структурный): Фасад - единая точка сценариев модуля «ПОС» для UI.
 * Консоль не знает про репозитории, фабрику и сервис итога; здесь только оркестрация, правил домена тут нет.
 */
public final class PosFacade {
    private final StudentRepository students;
    private final RubricRepository rubrics;
    private final EvaluationRepository evaluations;
    private final FinalGradeService gradeService;
    private final Map<String, ReportExporter> exporters;

    public PosFacade(StudentRepository students, RubricRepository rubrics, EvaluationRepository evaluations,
                     FinalGradeService gradeService, Map<String, ReportExporter> exporters) {
        this.students = students;
        this.rubrics = rubrics;
        this.evaluations = evaluations;
        this.gradeService = gradeService;
        this.exporters = exporters;
    }

    public void addStudent(String id, String name) {
        StudentId studentId = new StudentId(id);
        if (students.find(studentId).isPresent()) throw new DomainException("Студент уже есть: " + id);
        students.save(new Student(studentId, name));
    }

    public void addCriterion(String id, double maxScore, double weight, String name) {
        Rubric rubric = rubrics.get();
        rubric.add(new Criterion(new CriterionId(id), name, new Score(maxScore), new Weight(weight)));
        rubrics.save(rubric);
    }

    public List<Student> students() { return students.findAll(); }

    public Collection<Criterion> criteria() { return rubrics.get().criteria(); }

    /** Выставить оценку; если по этому критерию она уже есть - исправить. */
    public void grade(String student, String criterion, double score, String comment) {
        StudentId studentId = studentOrFail(student);
        CriterionId criterionId = new CriterionId(criterion);
        Rubric rubric = rubrics.get();
        Optional<Evaluation> existing = evaluations.find(studentId, criterionId);
        if (existing.isPresent()) {
            Evaluation e = existing.get();
            e.correct(rubric.criterion(criterionId), new Score(score), comment);
            evaluations.save(e);
        } else {
            evaluations.save(EvaluationFactory.create(rubric, studentId, criterionId, score, comment));
        }
    }

    public void cancel(String student, String criterion) {
        StudentId studentId = studentOrFail(student);
        CriterionId criterionId = new CriterionId(criterion);
        if (evaluations.find(studentId, criterionId).isEmpty())
            throw new DomainException("Оценки нет: " + student + " / " + criterion);
        evaluations.delete(studentId, criterionId);
    }

    public List<Evaluation> evaluationsOf(String student) {
        return evaluations.findByStudent(studentOrFail(student));
    }

    public FinalGrade finalGrade(String student) {
        StudentId studentId = studentOrFail(student);
        return gradeService.calculate(studentId, rubrics.get(), evaluations.findByStudent(studentId));
    }

    public String export(String format) {
        ReportExporter exporter = exporters.get(format);
        if (exporter == null) throw new IllegalArgumentException("Неизвестный формат: " + format + " (доступны: " + exporters.keySet() + ")");
        List<ReportRow> rows = students.findAll().stream()
                .map(s -> new ReportRow(s.id().value(), s.name(), finalGrade(s.id().value()).percent()))
                .toList();
        return exporter.render(rows);
    }

    private StudentId studentOrFail(String id) {
        StudentId studentId = new StudentId(id);
        if (students.find(studentId).isEmpty()) throw new DomainException("Студент не найден: " + id);
        return studentId;
    }
}
