package com.dsoft.CitizenRegistrationSystem.exception;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ErrorResponse {

    @JsonProperty("error_message")
    private String errorMessage;
}
