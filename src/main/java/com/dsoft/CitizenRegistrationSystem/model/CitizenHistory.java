package com.dsoft.CitizenRegistrationSystem.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Document("citizenHistory")
public class CitizenHistory {

    @Id
    private String id;
    @NotBlank
    private String citizenId;
    private LocalDateTime timestamp;
    @NotBlank
    private String name;
    @Past
    @NotNull
    private LocalDate birthdate;
    @NotBlank
    @Indexed(unique = true)
    private String identityCard;
}
