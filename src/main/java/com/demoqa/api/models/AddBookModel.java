package com.demoqa.api.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddBookModel {
    private String userId;
    private List<IsbnModel> collectionOfIsbns;
}
