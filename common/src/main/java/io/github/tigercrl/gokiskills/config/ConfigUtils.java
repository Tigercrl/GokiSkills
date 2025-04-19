package io.github.tigercrl.gokiskills.config;

import com.google.gson.*;
import com.mojang.logging.LogUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import org.slf4j.Logger;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

public class ConfigUtils {
    private static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .setExclusionStrategies(new ExclusionStrategy() {
                @Override
                public boolean shouldSkipField(FieldAttributes fieldAttributes) {
                    return fieldAttributes.getAnnotation(GsonIgnore.class) != null;
                }

                @Override
                public boolean shouldSkipClass(Class aClass) {
                    return false;
                }
            })
            .create();
    private static final Logger LOGGER = LogUtils.getLogger();

    public static <T extends GokiConfig> T readConfig(String fileName, Class<T> configClass) {
        File file = new File("config", fileName + ".json");
        T config = null;
        try {
            config = configClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            LOGGER.error("Failed to create config file: {}.json, using default config", fileName, e);
        }
        if (file.exists()) {
            try {
                config = deserialize(Files.readString(file.toPath()), configClass);
            } catch (Exception e) {
                LOGGER.error("Failed to read config file: {}.json, using default config", fileName, e);
            }
        }
        try {
            config.validatePostLoad();
        } catch (ConfigException e) {
            LOGGER.error("Failed to validate config file: {}.json", fileName, e);
            throw e;
        }
        saveConfig(fileName, config);
        return config;
    }

    public static <T extends GokiConfig> void saveConfig(String fileName, T config) {
        try {
            File file = new File("config", fileName + ".json");
            if (!file.exists()) {
                file.getParentFile().mkdirs();
            }
            Files.writeString(file.toPath(), serialize(config), StandardOpenOption.CREATE);
        } catch (Exception e) {
            LOGGER.error("Failed to save config file: {}.json", fileName, e);
        }
    }

    public static JsonObject toJsonObject(Object object) {
        return gson.toJsonTree(object).getAsJsonObject();
    }

    public static <T> T fromJsonObject(JsonObject jsonObject, Class<T> clazz) {
        return gson.fromJson(jsonObject, clazz);
    }

    public static String serialize(Object object) {
        return gson.toJson(object);
    }

    public static <T> T deserialize(String json, Class<T> clazz) {
        return gson.fromJson(json, clazz);
    }

    public static <T> StreamCodec<ByteBuf, T> streamCodecOf(Class<T> clazz) {
        return ByteBufCodecs.STRING_UTF8.map(
                str -> ConfigUtils.deserialize(str, clazz),
                ConfigUtils::serialize
        );
    }
}
