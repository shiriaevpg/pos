package ru.mipt.pos.domain;

/** Нарушение правила предметной области (инварианта). */
public class DomainException extends RuntimeException {
    public DomainException(String message) {
        super(message);
    }
}
