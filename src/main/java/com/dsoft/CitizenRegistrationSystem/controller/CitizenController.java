package com.dsoft.CitizenRegistrationSystem.controller;

import com.dsoft.CitizenRegistrationSystem.dto.CitizenRequest;
import com.dsoft.CitizenRegistrationSystem.dto.CitizenResponse;
import com.dsoft.CitizenRegistrationSystem.model.Citizen;
import com.dsoft.CitizenRegistrationSystem.service.CitizenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Citizen Controller", description = "Ez a controller felelős minden citizennel kapcsolatos műveletért")
@RequiredArgsConstructor
@RestController()
@RequestMapping(path = "citizen")
public class CitizenController {

    private final CitizenService service;

    private final ModelMapper modelMapper;

    @Operation(
            summary = "Citizen létrehozása",
            description = "Egy új citizen felvétele az adatbázisba")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Sikeres létrehozás"),
            @ApiResponse(responseCode = "409", description = "A kért Citizen már létezik"),
            @ApiResponse(responseCode = "500", description = "Hiba történt a létrehozás közben")})
    @PostMapping
    public ResponseEntity<HttpStatus> createCitizen(@RequestBody @Valid CitizenRequest citizenRequest) {
        service.createCitizen(modelMapper.map(citizenRequest, Citizen.class));
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Operation(
            summary = "Személyi alapján lekérés",
            description = "Citizen lekérdezése személyi igazolványszám alapján",
            parameters = @Parameter(in = ParameterIn.PATH, name = "identityCard", description = "Személyi igazolványszám", example = "457634KU"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sikeres lekérés"),
            @ApiResponse(responseCode = "404", description = "A kért Citizen nem található"),
            @ApiResponse(responseCode = "500", description = "Hiba történt a lekérdezés közben")})
    @GetMapping(path = "{identityCard}")
    public ResponseEntity<CitizenResponse> getByIdentityCard(@PathVariable(name = "identityCard") @NotNull String identityCard) {
        CitizenResponse citizenResponse = modelMapper.map(service.getByIdentityCard(identityCard), CitizenResponse.class);
        return ResponseEntity.ok(citizenResponse);
    }
}
