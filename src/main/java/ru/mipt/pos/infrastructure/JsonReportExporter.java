package ru.mipt.pos.infrastructure;

import ru.mipt.pos.application.ReportExporter;
import ru.mipt.pos.application.ReportRow;

import java.util.List;
import java.util.Locale;

public final class JsonReportExporter implements ReportExporter {
    @Override public String render(List<ReportRow> rows) {
        StringBuilder sb = new StringBuilder("[\n");
        for (int i = 0; i < rows.size(); i++) {
            ReportRow r = rows.get(i);
            sb.append(String.format(Locale.ROOT, "  {\"studentId\": \"%s\", \"name\": \"%s\", \"percent\": %.2f}",
                    esc(r.studentId()), esc(r.name()), r.percent()));
            sb.append(i < rows.size() - 1 ? ",\n" : "\n");
        }
        return sb.append("]\n").toString();
    }

    private static String esc(String s) { return s.replace("\\", "\\\\").replace("\"", "\\\""); }
}
