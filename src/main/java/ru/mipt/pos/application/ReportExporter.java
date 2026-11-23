package ru.mipt.pos.application;

import java.util.List;

/** Порт выгрузки: домен и приложение не знают про CSV/JSON - формат живёт в адаптерах. */
public interface ReportExporter {
    String render(List<ReportRow> rows);
}
