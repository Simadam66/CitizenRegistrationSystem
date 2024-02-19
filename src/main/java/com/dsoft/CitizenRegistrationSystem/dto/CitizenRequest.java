package com.dsoft.CitizenRegistrationSystem.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.Data;

import java.time.LocalDate;


@Data
public class CitizenRequest {
    @NotEmpty
    @NotBlank
    private String name;
    @Past
    @NotNull
    private LocalDate birthdate;
    @NotEmpty
    @NotBlank
    private String identityCard;
}
