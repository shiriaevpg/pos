package ru.mipt.pos.domain.model;

/**
 * GoF (порождающий): Фабрика. Единственная точка создания Evaluation:
 * проверяет принадлежность критерия рубрикатору и диапазон балла (через конструктор Evaluation).
 */
public final class EvaluationFactory {
    private EvaluationFactory() { }

    public static Evaluation create(Rubric rubric, StudentId studentId, CriterionId criterionId,
                                    double value, String comment) {
        Criterion criterion = rubric.criterion(criterionId);   // нет в рубрикаторе -> DomainException
        return new Evaluation(studentId, criterion, new Score(value), comment);
    }
}
