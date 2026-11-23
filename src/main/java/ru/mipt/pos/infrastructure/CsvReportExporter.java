package ru.mipt.pos.infrastructure;

import ru.mipt.pos.application.ReportExporter;
import ru.mipt.pos.application.ReportRow;

import java.util.List;
import java.util.Locale;

public final class CsvReportExporter implements ReportExporter {
    @Override public String render(List<ReportRow> rows) {
        StringBuilder sb = new StringBuilder("student_id,name,percent\n");
        for (ReportRow r : rows)
            sb.append(cell(r.studentId())).append(',').append(cell(r.name())).append(',')
              .append(String.format(Locale.ROOT, "%.2f", r.percent())).append('\n');
        return sb.toString();
    }

    private static String cell(String s) {
        return s.matches(".*[\",\\n].*") ? "\"" + s.replace("\"", "\"\"") + "\"" : s;
    }
}
