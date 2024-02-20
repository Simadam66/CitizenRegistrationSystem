package com.dsoft.CitizenRegistrationSystem.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Document("citizen")
public class Citizen {

    @Id
    private String id;
    @NotBlank
    private String name;
    @Past
    @NotNull
    private LocalDate birthdate;
    @NotBlank
    @Indexed(unique = true)
    private String identityCard;
}
