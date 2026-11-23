package ru.mipt.pos.ui;

import ru.mipt.pos.application.PosFacade;
import ru.mipt.pos.domain.DomainException;
import ru.mipt.pos.domain.model.Criterion;
import ru.mipt.pos.domain.model.Evaluation;
import ru.mipt.pos.domain.model.Student;

import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

public final class ConsoleApp {
    private static final String HELP = """
            Команды:
              add-student <id> <имя...>                  добавить студента
              add-criterion <id> <max> <вес> <название...> добавить критерий
              students | criteria                        списки
              grade <студент> <критерий> <балл> [комментарий...]   выставить / исправить оценку
              cancel <студент> <критерий>                отменить оценку
              evaluations <студент>                      оценки студента
              result <студент>                           итог по студенту (%)
              export <csv|json> [файл]                   выгрузка итогов
              help | exit""";

    private final PosFacade pos;
    private final PrintStream out;

    public ConsoleApp(PosFacade pos, PrintStream out) {
        this.pos = pos;
        this.out = out;
    }

    public void runInteractive(Scanner in) {
        out.println("ПОС: преподавательская оценка студентов. help - список команд.");
        while (true) {
            out.print("> ");
            out.flush();
            if (!in.hasNextLine() || !handle(in.nextLine())) return;
        }
    }

    public void runScript(List<String> lines) {
        for (String line : lines) {
            out.println("> " + line);
            if (!handle(line)) return;
        }
    }

    /** @return false, если пора выходить */
    boolean handle(String line) {
        String[] t = line.trim().split("\\s+");
        if (t[0].isEmpty()) return true;
        try {
            switch (t[0]) {
                case "add-student" -> { need(t, 3); pos.addStudent(t[1], rest(t, 2)); out.println("OK"); }
                case "add-criterion" -> { need(t, 5); pos.addCriterion(t[1], num(t[2]), num(t[3]), rest(t, 4)); out.println("OK"); }
                case "students" -> { for (Student s : pos.students()) out.println("  " + s.id() + "  " + s.name()); }
                case "criteria" -> { for (Criterion c : pos.criteria())
                    out.println("  " + c.id() + "  " + c.name() + "  max=" + c.maxScore() + "  вес=" + c.weight().value()); }
                case "grade" -> { need(t, 4); pos.grade(t[1], t[2], num(t[3]), rest(t, 4)); out.println("OK"); }
                case "cancel" -> { need(t, 3); pos.cancel(t[1], t[2]); out.println("OK"); }
                case "evaluations" -> { need(t, 2); for (Evaluation e : pos.evaluationsOf(t[1]))
                    out.println("  " + e.criterionId() + " = " + e.score() + "  (" + e.date() + ") " + e.comment()); }
                case "result" -> { need(t, 2);
                    out.println(String.format(Locale.ROOT, "Итог %s: %.2f%%", t[1], pos.finalGrade(t[1]).percent())); }
                case "export" -> { need(t, 2);
                    String report = pos.export(t[1]);
                    out.print(report);
                    if (t.length > 2) { Files.writeString(Path.of(t[2]), report); out.println("Сохранено: " + t[2]); } }
                case "help" -> out.println(HELP);
                case "exit" -> { return false; }
                default -> out.println("Неизвестная команда: " + t[0] + " (help)");
            }
        } catch (DomainException | IllegalArgumentException | java.io.IOException e) {
            out.println("Ошибка: " + e.getMessage());
        }
        return true;
    }

    private static void need(String[] t, int n) {
        if (t.length < n) throw new IllegalArgumentException("Недостаточно аргументов (help)");
    }

    private static double num(String s) { return Double.parseDouble(s.replace(',', '.')); }

    private static String rest(String[] t, int from) {
        return from >= t.length ? "" : String.join(" ", Arrays.copyOfRange(t, from, t.length));
    }
}
