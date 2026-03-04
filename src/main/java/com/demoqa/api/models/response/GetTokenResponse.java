package com.demoqa.api.models.response;

import lombok.Data;

@Data
public class GetTokenResponse {
    private String token;
    private String expires;
    private String status;
    private String result;
}
