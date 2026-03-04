package com.demoqa.factory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Factory для создания тестовых данных BookStore API
 */
public class BookFactory {

    // 🔹 Валидные ISBN из DemoQA (публичный доступ для прямого использования)
    public static final List<String> VALID_ISBNS = List.of(
            "9781449325862",  // Git Pocket Guide
            "9781449337711",  // Designing Evolvable Web APIs
            "9781449365035",  // Speaking JavaScript
            "9781491950296",  // You Don't Know JS: Scope & Closures
            "9781449366179"   // Learning React
    );

    private static final Random RANDOM = new Random();

    // 🔹 Приватный конструктор — класс только со статическими методами
    private BookFactory() {}

    /**
     * Получить N уникальных валидных ISBN (случайный порядок)
     * @param count количество ISBN (макс. размер VALID_ISBNS)
     * @return список уникальных ISBN
     * @throws IllegalArgumentException если count > доступное количество ISBN
     */
    public static List<String> getValidIsbns(int count) {
        if (count < 0) {
            throw new IllegalArgumentException("Count cannot be negative: " + count);
        }
        if (count > VALID_ISBNS.size()) {
            throw new IllegalArgumentException(
                    String.format("Requested %d unique ISBNs, but only %d available",
                            count, VALID_ISBNS.size()));
        }

        // 🔹 Копируем, перемешиваем и берём первые N (уникальные, случайный порядок)
        return new ArrayList<>(VALID_ISBNS)
                .stream()
                .collect(Collectors.collectingAndThen(
                        Collectors.toCollection(ArrayList::new),
                        list -> {
                            Collections.shuffle(list, RANDOM);
                            return list.stream().limit(count).collect(Collectors.toList());
                        }
                ));
    }

    /**
     * Получить N валидных ISBN (с возможными дубликатами)
     * @param count количество ISBN
     * @return список ISBN (может содержать дубликаты)
     */
    public static List<String> getValidIsbnsWithDuplicates(int count) {
        if (count < 0) {
            throw new IllegalArgumentException("Count cannot be negative: " + count);
        }

        return IntStream.range(0, count)
                .mapToObj(i -> getRandomValidIsbn())
                .collect(Collectors.toList());
    }

    /**
     * Получить случайный валидный ISBN
     */
    public static String getRandomValidIsbn() {
        return VALID_ISBNS.get(RANDOM.nextInt(VALID_ISBNS.size()));
    }

    /**
     * Получить первый валидный ISBN (детерминировано, для стабильных тестов)
     */
    public static String getFirstValidIsbn() {
        return VALID_ISBNS.get(0);
    }

    /**
     * Получить несуществующий ISBN (для негативных тестов)
     */
    public static String getNonExistentIsbn() {
        return "9999999999999";
    }

    /**
     * Получить невалидный формат ISBN (для негативных тестов)
     */
    public static String getInvalidFormatIsbn() {
        return "not-a-valid-isbn!@#";
    }

    /**
     * Получить пустой ISBN (для негативных тестов)
     */
    public static String getEmptyIsbn() {
        return "";
    }

    /**
     * Проверить, является ли ISBN валидным (есть в DemoQA)
     */
    public static boolean isValidIsbn(String isbn) {
        return VALID_ISBNS.contains(isbn);
    }
}