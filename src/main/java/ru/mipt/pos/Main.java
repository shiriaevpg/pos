package ru.mipt.pos;

import ru.mipt.pos.application.PosFacade;
import ru.mipt.pos.application.ReportExporter;
import ru.mipt.pos.di.Container;
import ru.mipt.pos.domain.repository.EvaluationRepository;
import ru.mipt.pos.domain.repository.RubricRepository;
import ru.mipt.pos.domain.repository.StudentRepository;
import ru.mipt.pos.domain.service.FinalGradeService;
import ru.mipt.pos.domain.service.GradingStrategy;
import ru.mipt.pos.domain.service.RoundedStrategy;
import ru.mipt.pos.domain.service.WeightedAverageStrategy;
import ru.mipt.pos.infrastructure.CsvReportExporter;
import ru.mipt.pos.infrastructure.InMemoryEvaluationRepository;
import ru.mipt.pos.infrastructure.InMemoryRubricRepository;
import ru.mipt.pos.infrastructure.InMemoryStudentRepository;
import ru.mipt.pos.infrastructure.JsonReportExporter;
import ru.mipt.pos.ui.ConsoleApp;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/** Composition root: единственное место, где собирается граф зависимостей. */
public final class Main {
    private static final List<String> DEMO = List.of(
            "add-student ivanov Иван Иванов",
            "add-student petrova Мария Петрова",
            "add-criterion hw 10 3 Домашние задания",
            "add-criterion exam 10 5 Экзамен",
            "add-criterion activity 5 2 Активность на семинарах",
            "students",
            "criteria",
            "grade ivanov hw 9 Хорошая работа",
            "grade ivanov exam 8",
            "grade ivanov activity 5 Всегда на связи",
            "grade petrova hw 10",
            "grade petrova exam 6 Пересдача не нужна",
            "grade ivanov hw 12",            // нельзя: 12 из 10
            "grade ivanov swimming 5",       // нельзя: критерия нет в рубрикаторе
            "grade ivanov exam 7 Исправлено после апелляции",
            "evaluations ivanov",
            "result ivanov",
            "result petrova",                // активность не оценена -> считается как 0
            "cancel petrova exam",
            "result petrova",
            "export csv",
            "export json");

    public static void main(String[] args) {
        System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));

        Container c = new Container()
                .bind(StudentRepository.class, k -> new InMemoryStudentRepository())
                .bind(RubricRepository.class, k -> new InMemoryRubricRepository())
                .bind(EvaluationRepository.class, k -> new InMemoryEvaluationRepository())
                .bind(GradingStrategy.class, k -> new RoundedStrategy(new WeightedAverageStrategy(), 2))
                .bind(FinalGradeService.class, k -> new FinalGradeService(k.get(GradingStrategy.class)))
                .bind(ReportExporter.class, "csv", k -> new CsvReportExporter())
                .bind(ReportExporter.class, "json", k -> new JsonReportExporter())
                .bind(PosFacade.class, k -> new PosFacade(
                        k.get(StudentRepository.class), k.get(RubricRepository.class), k.get(EvaluationRepository.class),
                        k.get(FinalGradeService.class),
                        Map.of("csv", k.get(ReportExporter.class, "csv"), "json", k.get(ReportExporter.class, "json"))))
                .bind(ConsoleApp.class, k -> new ConsoleApp(k.get(PosFacade.class), System.out));

        ConsoleApp app = c.get(ConsoleApp.class);
        if (args.length > 0 && args[0].equals("--demo")) app.runScript(DEMO);
        else app.runInteractive(new Scanner(System.in, StandardCharsets.UTF_8));
    }
}
