package com.dsoft.CitizenRegistrationSystem.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class IdentityAndNameRequest {
    @NotBlank
    private String name;

    @NotBlank
    private String identityCard;

    private List<String> update;

    public IdentityAndNameRequest() {
        this.update = new ArrayList<>();
    }
}
