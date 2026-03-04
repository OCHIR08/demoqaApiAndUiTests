package com.demoqa.api.models.response.book;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Book {

    private String isbn;
    private String title;

    @JsonProperty("subTitle")  // 🔥 CamelCase в JSON
    private String subTitle;

    private String author;

    @JsonProperty("publish_date")  // 🔥 underscore в JSON → camelCase в Java
    private String publishDate;

    private String publisher;
    private Integer pages;
    private String description;
    private String website;

    // 🔥 Может присутствовать, если ответ — книги пользователя
    private String userId;
}