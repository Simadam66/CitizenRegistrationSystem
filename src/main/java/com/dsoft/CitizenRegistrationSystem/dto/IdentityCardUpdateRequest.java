package com.dsoft.CitizenRegistrationSystem.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class IdentityCardUpdateRequest {
    @NotEmpty
    @NotBlank
    private String identityCard;
}
