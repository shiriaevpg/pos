package ru.mipt.pos.di;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Минимальный DI-контейнер: привязка «тип (+имя) -> фабрика», ленивые синглтоны.
 * Используется только на краю приложения (Main); ни домен, ни application про него не знают.
 */
public final class Container {
    private final Map<String, Function<Container, ?>> factories = new HashMap<>();
    private final Map<String, Object> singletons = new HashMap<>();

    public <T> Container bind(Class<T> type, Function<Container, ? extends T> factory) {
        return bind(type, "", factory);
    }

    public <T> Container bind(Class<T> type, String name, Function<Container, ? extends T> factory) {
        factories.put(key(type, name), factory);
        return this;
    }

    public <T> T get(Class<T> type) { return get(type, ""); }

    public <T> T get(Class<T> type, String name) {
        String key = key(type, name);
        Object instance = singletons.get(key);
        if (instance == null) {
            Function<Container, ?> factory = factories.get(key);
            if (factory == null) throw new IllegalStateException("Не привязано: " + key);
            instance = factory.apply(this);
            singletons.put(key, instance);
        }
        return type.cast(instance);
    }

    private static String key(Class<?> type, String name) { return type.getName() + "#" + name; }
}
