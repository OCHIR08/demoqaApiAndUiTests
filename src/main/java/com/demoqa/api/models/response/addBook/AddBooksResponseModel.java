package com.demoqa.api.models.response.addBook;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddBooksResponseModel {
    private List<BookItem> books;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BookItem {
        private String isbn;
    }
}
