package com.demoqa.api.models.response.registration;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class RegistrationResponse {

    @JsonProperty("userID") // <-- Критически важно для соответствия "userID" из JSON
    private String userId;

    private String username;

    private List<Object> books; // или List<String>, или List<Book> если структура известна
}
