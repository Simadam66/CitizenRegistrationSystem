package com.dsoft.CitizenRegistrationSystem.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class IdentityCardUpdateRequest {
    @NotBlank
    private String identityCard;
}
