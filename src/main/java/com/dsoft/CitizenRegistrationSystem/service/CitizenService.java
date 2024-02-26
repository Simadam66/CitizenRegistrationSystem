package com.dsoft.CitizenRegistrationSystem.service;

import com.dsoft.CitizenRegistrationSystem.dto.*;
import com.dsoft.CitizenRegistrationSystem.model.Citizen;
import com.dsoft.CitizenRegistrationSystem.repository.CitizenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class CitizenService {

    private static final String EQUAL_OPERATOR = "eq";
    private static final String LESS_THAN_OPERATOR = "lt";
    private static final String GREATER_THAN_OPERATOR = "gt";

    private final CitizenRepository repository;

    public Citizen createCitizen(Citizen citizen) {
        Optional<Citizen> citizenWithSameIdentity = repository.findByIdentityCard(citizen.getIdentityCard());
        if (citizenWithSameIdentity.isPresent()) {
            throw new DuplicateKeyException("This identity card is already in use");
        }
        return repository.save(citizen);
    }

    public Citizen getById(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("No citizen found with the provided id"));
    }

    public Citizen filterByIdentityCard(String identityCard) {
        return  repository.findByIdentityCard(identityCard)
                          .orElseThrow(() -> new NoSuchElementException("No citizen found with the provided identity card"));
    }

    public List<Citizen> filterByBirthdate(LocalDate birthdate, String operator) {
        if (!operatorIsValid(operator)) {
            throw new IllegalArgumentException("The operator value must be eq/lt/gt");
        }
        return  repository.filterByBirthdate(birthdate, operator);
    }

    private boolean operatorIsValid(String operator) {
        return EQUAL_OPERATOR.equals(operator) || LESS_THAN_OPERATOR.equals(operator) || GREATER_THAN_OPERATOR.equals(operator);
    }

    public void updateIdentityCard(String id, IdentityCardUpdateRequest request) {
        Citizen citizenToUpdate = getById(id);
        if (isIdentityCardInUse(citizenToUpdate, request.getIdentityCard())) {
            throw new DuplicateKeyException("This identity card is already in use");
        }
        citizenToUpdate.setIdentityCard(request.getIdentityCard());
        repository.save(citizenToUpdate);
    }

    private boolean isIdentityCardInUse(Citizen citizenToUpdate, String newIdentityCard) {
        Optional<Citizen> citizenWithProvidedIdentity = repository.findByIdentityCard(newIdentityCard);
        return citizenWithProvidedIdentity.isPresent() && !citizenWithProvidedIdentity.get().getId().equals(citizenToUpdate.getId());
    }

    public void updateIdentityCardAndName(String id, List<PatchRequest> requests) {
        Citizen citizen = getById(id);
        for (PatchRequest request : requests) {
            if (request.getAction().equals(ActionEnum.UPDATE) &&
                    isAllowedField(request.getPropertyName()) &&
                    isAllowedValue(citizen, request.getPropertyName(), request.getValue())) {
                updateField(citizen, Map.entry(request.getPropertyName() , request.getValue()));
            }
        }
        repository.save(citizen);
    }

    public void updateIdentityCardAndName(String id, IdentityCardAndNameRequest request) {
        Citizen citizen = getById(id);
        for (String fieldName : request.getUpdate()) {
            if (isAllowedField(fieldName)) {
                try {
                    Field requestedField =  request.getClass().getDeclaredField(fieldName);
                    requestedField.setAccessible(true);
                    Object value = requestedField.get(request);
                    if (isAllowedValue(citizen, requestedField.getName(), value.toString())) {
                        updateField(citizen, Map.entry(fieldName, String.valueOf(value)));
                    }
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        repository.save(citizen);
    }

    private boolean isAllowedField(String fieldName) {
    return AllowedFields.IDENTITY_CARD_FIELD.equals(fieldName) || AllowedFields.NAME_FIELD.equals(fieldName);
    }

    private boolean isAllowedValue(Citizen citizenToUpdate, String field, String value)
    {
        boolean isAllowed;
        switch (field) {
            case AllowedFields.IDENTITY_CARD_FIELD -> isAllowed = !isIdentityCardInUse(citizenToUpdate, value);
            default -> isAllowed = true;
        }
        return isAllowed;
    }

    private void updateField(Object object, Map.Entry<String, String> entry ) {
        try {
            Field field =  object.getClass().getDeclaredField(entry.getKey());
            field.setAccessible(true);
            field.set(object, entry.getValue());
        }
        catch (NoSuchFieldException ex) {
            throw new IllegalArgumentException(ex);
        } catch (IllegalAccessException ex) {
            throw new RuntimeException(ex);
        }
    }
}
