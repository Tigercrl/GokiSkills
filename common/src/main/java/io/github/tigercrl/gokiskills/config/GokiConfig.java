package io.github.tigercrl.gokiskills.config;

public interface GokiConfig {
    default void validatePostLoad() throws ConfigException {
    }
}
