package ru.mipt.pos.domain.model;

/** VO: итог по студенту в процентах (0..100). Вычисляется, нигде не хранится. */
public record FinalGrade(StudentId studentId, double percent) { }
