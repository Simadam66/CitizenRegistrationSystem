package com.dsoft.CitizenRegistrationSystem.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class CitizenResponse {
    private String name;
    private LocalDate birthdate;
    private String identityCard;
}
