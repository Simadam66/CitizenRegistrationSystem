package com.dsoft.CitizenRegistrationSystem.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class IdentityCardAndNameRequest {
    @NotBlank
    private String name;

    @NotBlank
    private String identityCard;

    private List<String> update;

    public IdentityCardAndNameRequest() {
        this.update = new ArrayList<>();
    }
}
