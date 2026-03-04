package com.demoqa.api.models.request.addBook;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddBooksModel {

    private String userId;
    private List<IsbnItem> collectionOfIsbns;

    // 🔹 ==================== СТАТИЧЕСКИЕ ХЕЛПЕРЫ ====================

    /**
     * Создать AddBooksModel из userId и списка ISBN-строк
     * Автоматически конвертирует List<String> → List<IsbnItem>
     *
     * @param userId ID пользователя
     * @param isbns список ISBN как строк (например, List.of("978...", "979..."))
     * @return готовый AddBooksModel
     */
    public static AddBooksModel of(String userId, List<String> isbns) {
        return AddBooksModel.builder()
                .userId(userId)
                .collectionOfIsbns(isbns.stream()
                        .map(IsbnItem::new)  // 🔥 Конвертируем String → IsbnItem
                        .collect(Collectors.toList()))
                .build();
    }

    /**
     * Создать AddBooksModel для одной книги
     */
    public static AddBooksModel ofSingle(String userId, String isbn) {
        return of(userId, List.of(isbn));
    }
}