package com.dsoft.CitizenRegistrationSystem.service;

import com.dsoft.CitizenRegistrationSystem.dto.IdentityAndNameRequest;
import com.dsoft.CitizenRegistrationSystem.dto.IdentityCardUpdateRequest;
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

    public void createCitizen(Citizen citizen) {
        Optional<Citizen> citizenWithSameIdentity = repository.findByIdentityCard(citizen.getIdentityCard());
        if (citizenWithSameIdentity.isPresent()) {
            throw new DuplicateKeyException("This identity card is already in use");
        }
        repository.save(citizen);
    }

    public Citizen getByIdentityCard(String identityCard) {
        return  repository.findByIdentityCard(identityCard)
                          .orElseThrow(() -> new NoSuchElementException("No citizen found with the provided identity card"));
    }

    public Citizen getById(String id) {
        return repository.findById(id)
                         .orElseThrow(() -> new NoSuchElementException("No citizen found with the provided id"));
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
        Optional<Citizen> citizenWithProvidedIdentity = repository.findByIdentityCard(request.getIdentityCard());
        if (citizenWithProvidedIdentity.isPresent() && !citizenWithProvidedIdentity.get().equals(citizenToUpdate)) {
            throw new DuplicateKeyException("This identity card is already in use");
        }
        citizenToUpdate.setIdentityCard(request.getIdentityCard());
        repository.save(citizenToUpdate);
    }

    public void updateIdentityCardAndName(String id, HashMap<String, String> update) {
        Citizen citizen = getById(id);
        for (Map.Entry<String, String> entry : update.entrySet()) {
            if (isAllowedField(entry.getKey())) {
                updateField(citizen, entry);
            }
        }
        repository.save(citizen);
    }

    public void updateIdentityCardAndName(String id, IdentityAndNameRequest request) {
        Citizen citizen = getById(id);
        for (String fieldName : request.getUpdate()) {
            if (isAllowedField(fieldName)) {
                try {
                    Field requestedField =  request.getClass().getDeclaredField(fieldName);
                    requestedField.setAccessible(true);
                    Object value = requestedField.get(request);
                    updateField(citizen, Map.entry(fieldName, String.valueOf(value)));
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        repository.save(citizen);
    }

    private boolean isAllowedField(String fieldName) {
        //Simplified implementation
        return "identityCard".equals(fieldName) || "name".equals(fieldName);
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
