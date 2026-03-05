package com.demoqa.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class JwtUtils {
    private static final ObjectMapper mapper = new ObjectMapper();

    /**
     * Распарсить payload JWT токена
     */
    public static JsonNode parsePayload(String jwt) {
        try {
            String[] parts = jwt.split("\\.");
            if (parts.length != 3) {
                Assertions.fail("Invalid JWT format: expected 3 parts, got " + parts.length);
            }
            String payload = new String(
                    Base64.getUrlDecoder().decode(parts[1]),
                    StandardCharsets.UTF_8
            );
            return mapper.readTree(payload);
        } catch (JsonProcessingException e) {
            Assertions.fail("Failed to parse JWT payload: " + e.getMessage());
            return null; // никогда не выполнится
        }
    }

    /**
     * Получить username из JWT токена
     */
    public static String getUsername(String jwt) {
        return parsePayload(jwt).get("userName").asText();
    }

    /**
     * Получить userId из JWT токена
     */
    public static String getUserId(String jwt) {
        return parsePayload(jwt).get("userId").asText();
    }
    public static boolean isValidFormat(String token) {
        if (token == null || token.isEmpty()) return false;
        String[] parts = token.split("\\.", -1); // -1 сохраняет пустые части
        if (parts.length != 3) return false;

        // Базовая проверка: каждая часть содержит только допустимые символы
        String jwtRegex = "^[A-Za-z0-9\\-_=]*$";
        for (String part : parts) {
            if (!part.matches(jwtRegex)) return false;
        }
        return true;
    }

    /**
     * Проверить, что JWT payload не содержит чувствительных полей
     * @param jwt токен
     * @param forbiddenFields список имён полей, которые не должны присутствовать
     * @return true если все поля отсутствуют
     */
    public static boolean doesNotContainSensitiveFields(String jwt, String... forbiddenFields) {
        try {
            JsonNode payload = parsePayload(jwt); // используем существующий метод

            for (String field : forbiddenFields) {
                if (payload.has(field)) {
                    return false; // поле найдено → нарушение
                }
            }
            return true;
        } catch (Exception e) {
            // Если не смогли распарсить — считаем это нарушением (токен невалиден)
            return false;
        }
    }

    /**
     * Удобный метод для проверки на пароль
     */
    public static boolean doesNotContainPassword(String jwt) {
        return doesNotContainSensitiveFields(jwt, "password", "pass", "pwd");
    }

}