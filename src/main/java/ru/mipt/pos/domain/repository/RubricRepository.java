package ru.mipt.pos.domain.repository;

import ru.mipt.pos.domain.model.Rubric;

/** Порт хранения рубрикатора. Сейчас курс один, поэтому рубрикатор один; под несколько курсов добавится параметр. */
public interface RubricRepository {
    Rubric get();
    void save(Rubric rubric);
}
