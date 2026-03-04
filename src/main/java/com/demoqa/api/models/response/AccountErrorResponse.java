package com.demoqa.api.models.response;

import lombok.Data;

@Data
public class AccountErrorResponse {
    private String code;
    private String message;
}
