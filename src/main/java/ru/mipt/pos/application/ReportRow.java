package ru.mipt.pos.application;

/** Строка выгрузки итогов. */
public record ReportRow(String studentId, String name, double percent) { }
