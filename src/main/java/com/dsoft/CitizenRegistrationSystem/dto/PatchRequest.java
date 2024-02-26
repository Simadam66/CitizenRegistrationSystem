package com.dsoft.CitizenRegistrationSystem.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.Data;

import java.util.HashMap;

@Data
public class PatchRequest {
    @NotBlank
    private String propertyName;
    @NotBlank
    private String value;
    @NotNull
    private ActionEnum action;
}
