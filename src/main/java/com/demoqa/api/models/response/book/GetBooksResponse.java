package com.demoqa.api.models.response.book;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetBooksResponse {

    private String userId;
    private String username;
    private List<Book> books;

    // 🔹 Хелпер: проверить, есть ли книга с заданным ISBN
    public boolean hasBookWithIsbn(String isbn) {
        if (books == null || books.isEmpty()) {
            return false;
        }
        return books.stream()
                .anyMatch(book -> isbn.equals(book.getIsbn()));
    }

    // 🔹 Хелпер: получить книгу по ISBN
    public Book getBookByIsbn(String isbn) {
        if (books == null || books.isEmpty()) {
            return null;
        }
        return books.stream()
                .filter(book -> isbn.equals(book.getIsbn()))
                .findFirst()
                .orElse(null);
    }

    // 🔹 Хелпер: количество книг
    public int getBooksCount() {
        return books != null ? books.size() : 0;
    }
}