package me.ryan.yibotv2.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

public class Services {

    private static final Map<Class<?>, RegisteredServiceProvider<?>> SERVICES = new HashMap<>();

    public static <T> void register(Class<T> clazz, T instance) {
        SERVICES.put(clazz, new RegisteredServiceProvider<>(clazz, instance));
    }

    public static <T> T load(Class<T> clazz) {
        if (!SERVICES.containsKey(clazz))
            throw new IllegalStateException("No registration present for service '" + clazz.getName() + "'");
        return clazz.cast(SERVICES.get(clazz).getInstance());
    }

    @AllArgsConstructor
    @Getter
    private static class RegisteredServiceProvider<T> {
        private final Class<T> clazz;
        private final T instance;
    }

}
