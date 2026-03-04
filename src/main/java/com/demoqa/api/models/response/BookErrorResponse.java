package com.demoqa.api.models.response;

import lombok.Data;

@Data
public class BookErrorResponse {
    private String code;
    private String message;
}
