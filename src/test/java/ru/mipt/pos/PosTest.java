package ru.mipt.pos;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.mipt.pos.domain.DomainException;
import ru.mipt.pos.domain.model.*;
import ru.mipt.pos.domain.service.*;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class PosTest {
    private final StudentId ivanov = new StudentId("ivanov");
    private Rubric rubric;

    @BeforeEach
    void setUp() {
        rubric = new Rubric();
        rubric.add(new Criterion(new CriterionId("hw"), "ДЗ", new Score(10), new Weight(3)));
        rubric.add(new Criterion(new CriterionId("exam"), "Экзамен", new Score(10), new Weight(5)));
        rubric.add(new Criterion(new CriterionId("act"), "Активность", new Score(5), new Weight(2)));
    }

    private Evaluation eval(String criterion, double value) {
        return EvaluationFactory.create(rubric, ivanov, new CriterionId(criterion), value, "");
    }

    // ---- инварианты ----
    @Test void scoreAboveMaxIsRejected() {
        assertThrows(DomainException.class, () -> eval("hw", 12));
    }

    @Test void negativeScoreIsRejected() {
        assertThrows(DomainException.class, () -> eval("hw", -1));
    }

    @Test void scoreEqualToMaxIsAllowed() {
        assertEquals(new Score(10), eval("hw", 10).score());
    }

    @Test void criterionOutsideRubricIsRejected() {
        assertThrows(DomainException.class, () -> eval("swimming", 5));
    }

    @Test void correctionKeepsInvariants() {
        Evaluation e = eval("hw", 5);
        Criterion hw = rubric.criterion(new CriterionId("hw"));
        assertThrows(DomainException.class, () -> e.correct(hw, new Score(11), ""));
        assertEquals(new Score(5), e.score());          // неудачное исправление ничего не поменяло
        e.correct(hw, new Score(8), "ок");
        assertEquals(new Score(8), e.score());
    }

    @Test void duplicateCriterionIsRejected() {
        assertThrows(DomainException.class,
                () -> rubric.add(new Criterion(new CriterionId("hw"), "Ещё раз", new Score(1), new Weight(1))));
    }

    // ---- Domain Service + стратегии ----
    @Test void weightedAverageTreatsMissingAsZero() {
        var service = new FinalGradeService(new WeightedAverageStrategy());
        // (0.9*3 + 0.8*5 + 1.0*2) / 10 = 87%
        var grade = service.calculate(ivanov, rubric, List.of(eval("hw", 9), eval("exam", 8), eval("act", 5)));
        assertEquals(87.0, grade.percent(), 1e-9);
        // экзамен не оценён: (2.7 + 2) / 10 = 47%
        assertEquals(47.0, service.calculate(ivanov, rubric, List.of(eval("hw", 9), eval("act", 5))).percent(), 1e-9);
    }

    @Test void gradedOnlyIgnoresMissing() {
        var service = new FinalGradeService(new GradedOnlyStrategy());
        // (2.7 + 2) / (3 + 2) = 94%
        assertEquals(94.0, service.calculate(ivanov, rubric, List.of(eval("hw", 9), eval("act", 5))).percent(), 1e-9);
    }

    @Test void noEvaluationsGivesZero() {
        assertEquals(0, new FinalGradeService(new WeightedAverageStrategy()).calculate(ivanov, rubric, List.of()).percent());
        assertEquals(0, new FinalGradeService(new GradedOnlyStrategy()).calculate(ivanov, rubric, List.of()).percent());
    }

    @Test void roundedDecoratorRoundsAndStaysInContract() {
        // 1/3 от 10 при единственном критерии -> 33.333...
        Rubric r = new Rubric();
        r.add(new Criterion(new CriterionId("a"), "A", new Score(3), new Weight(1)));
        var scores = Map.of(new CriterionId("a"), new Score(1));
        assertEquals(33.33, new RoundedStrategy(new WeightedAverageStrategy(), 2).percent(r, scores));
        assertEquals(33.0, new RoundedStrategy(new WeightedAverageStrategy(), 0).percent(r, scores));
        var full = Map.of(new CriterionId("a"), new Score(3));
        assertEquals(100.0, new RoundedStrategy(new GradedOnlyStrategy(), 2).percent(r, full));
    }

    @Test void finalGradeRejectsForeignEvaluation() {
        var service = new FinalGradeService(new WeightedAverageStrategy());
        Evaluation foreign = EvaluationFactory.create(rubric, new StudentId("petrova"), new CriterionId("hw"), 5, "");
        assertThrows(DomainException.class, () -> service.calculate(ivanov, rubric, List.of(foreign)));
    }
}
