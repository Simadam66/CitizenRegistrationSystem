package com.dsoft.CitizenRegistrationSystem.controller;

import com.dsoft.CitizenRegistrationSystem.dto.*;
import com.dsoft.CitizenRegistrationSystem.exception.ErrorResponse;
import com.dsoft.CitizenRegistrationSystem.model.Citizen;
import com.dsoft.CitizenRegistrationSystem.service.CitizenService;
import com.dsoft.CitizenRegistrationSystem.utils.MappingUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

@Tag(name = "Citizen Controller", description = "Ez a controller felelős minden citizennel kapcsolatos műveletért")
@RequiredArgsConstructor
@RestController()
@RequestMapping(path = "/citizens")
public class CitizenController {

    private final CitizenService service;

    private final ModelMapper modelMapper;

    private final MappingUtils<Citizen, CitizenResponse> mappingUtils;

    @Operation(
            summary = "Citizen létrehozása",
            description = "Egy új citizen felvétele az adatbázisba")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Sikeres létrehozás", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "400", description = "Paraméterhiba", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Már létezik ilyen Citizen ezzel a személyi igazolvánnyal", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Hiba történt a létrehozás közben", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))})
    @PostMapping
    public ResponseEntity<CitizenResponse> createCitizen(@RequestBody @Valid CitizenRequest citizenRequest) {
        Citizen savedCitizen = service.createCitizen(modelMapper.map(citizenRequest, Citizen.class));
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                                                  .path("/{id}")
                                                  .buildAndExpand(savedCitizen.getId())
                                                  .toUri();
        return ResponseEntity.created(location).body(modelMapper.map(savedCitizen, CitizenResponse.class));
    }

    @Operation(
            summary = "Id alapú lekérés",
            description = "Citizen lekérdezése egyéni azonosító alapján",
            parameters = @Parameter(in = ParameterIn.PATH, name = "id", description = "Egyéni azonosító", example = "64cf8085f51d72128c364016"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sikeres lekérés"),
            @ApiResponse(responseCode = "400", description = "Hibás szűrési feltétel", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "A kért Citizen nem található", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Hiba történt a lekérdezés közben", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))})
    @GetMapping("/{id}")
    public ResponseEntity<CitizenResponse> getCitizen(@PathVariable(name = "id") @NotBlank String id) {
        CitizenResponse citizenResponse = modelMapper.map(service.getById(id), CitizenResponse.class);
        return ResponseEntity.ok(citizenResponse);
    }

    @Operation(
            summary = "Személyi alapján lekérés",
            description = "Citizen lekérdezése személyi igazolványszám alapján",
            parameters = @Parameter(in = ParameterIn.QUERY, name = "identityCard", description = "Személyi igazolványszám", example = "457634KU"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sikeres lekérés"),
            @ApiResponse(responseCode = "400", description = "Hibás szűrési feltétel", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "A kért Citizen nem található", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Hiba történt a lekérdezés közben", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))})
    @GetMapping("/filterByIdentityCard")
    public ResponseEntity<CitizenResponse> filterByIdentityCard(@RequestParam(name = "identityCard") @NotBlank String identityCard) {
        CitizenResponse citizenResponse = modelMapper.map(service.filterByIdentityCard(identityCard), CitizenResponse.class);
        return ResponseEntity.ok(citizenResponse);
    }

    @Operation(
            summary = "Citizenek szűrése kor alapján",
            description = "Egyidősek, fiatalabbak és idősebbek szűrésére való lehetőség",
            parameters = {@Parameter(in = ParameterIn.QUERY, name = "birthdate", description = "Születési idő", example = "1969-07-14"),
                    @Parameter(in = ParameterIn.QUERY, name = "operator", description = "eq=egykorú, lt=idősebb, gt=fiatalabb", example = "eq")
            })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sikeres lekérdezés"),
            @ApiResponse(responseCode = "400", description = "Hibás szűrési feltétel", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Hiba történt a művelet közben", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))})
    @GetMapping("/filterByBirthdate")
    public ResponseEntity<List<CitizenResponse>> filterByBirthdate(@RequestParam(name = "birthdate") @Past @NotNull LocalDate birthdate,
                                                                   @RequestParam(name = "operator") @NotBlank String operator) {
        List<CitizenResponse> citizens = mappingUtils.mapList(service.filterByBirthdate(birthdate, operator), CitizenResponse.class);
        return ResponseEntity.ok(citizens);
    }

    @Operation(
            summary = "Személyi frissítése",
            description = "Citizen személyi igazolványszámának frissítése",
            parameters = @Parameter(in = ParameterIn.PATH, name = "id", description = "Citizen egyedi azonosítója", example = "64cf8085f51d72128c364016"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sikeres frissítés", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "400", description = "Paraméterhiba", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "A kért Citizen nem található", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Már létezik ilyen Citizen ezzel a személyi igazolvánnyal", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Hiba történt a művelet közben", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))})
    @PatchMapping(path = "/{id}/identityCard")
    public ResponseEntity<HttpStatus> updateIdentityCard(@PathVariable(name = "id") @NotBlank String id,
                                                         @RequestBody @Valid IdentityCardUpdateRequest request) {
        service.updateIdentityCard(id, request);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PatchMapping(path = "/{id}/updateIdentityCardAndName")
    public ResponseEntity<HttpStatus> updateIdentityCardAndName(@PathVariable(name = "id") @NotBlank String id,
                                                            @RequestBody @Valid List<PatchRequest> requests) {
        service.updateIdentityCardAndName(id, requests);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PatchMapping(path = "/{id}/updateIdentityCardAndNameAlternative")
    public ResponseEntity<HttpStatus> updateIdentityCardAndName(@PathVariable(name = "id") @NotBlank String id,
                                                            @RequestBody @Valid IdentityCardAndNameRequest request) {
        service.updateIdentityCardAndName(id, request);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
