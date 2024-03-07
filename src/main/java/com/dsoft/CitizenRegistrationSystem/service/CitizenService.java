package com.dsoft.CitizenRegistrationSystem.service;

import com.dsoft.CitizenRegistrationSystem.dto.*;
import com.dsoft.CitizenRegistrationSystem.model.Citizen;
import com.dsoft.CitizenRegistrationSystem.model.CitizenHistory;
import com.dsoft.CitizenRegistrationSystem.repository.CitizenHistoryRepository;
import com.dsoft.CitizenRegistrationSystem.repository.CitizenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.transaction.annotation.Transactional;

import javax.management.relation.RelationNotFoundException;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class CitizenService {
    private static final String EQUAL_OPERATOR = "eq";
    private static final String LESS_THAN_OPERATOR = "lt";
    private static final String GREATER_THAN_OPERATOR = "gt";
    public static final String TRANSACTION_ERROR_CITIZEN_NOT_FOUND = "TRANSACTION ERROR: CITIZEN NOT FOUND";
    public static final String TRANSACTION_ERROR_HISTORY_SAVE_FAILURE = "TRANSACTION ERROR: HISTORY SAVE FAILURE";
    public static final String TRANSACTION_ERROR_CITIZEN_UPDATE_FAILURE = "TRANSACTION ERROR: CITIZEN UPDATE FAILURE";

    private final ModelMapper modelMapper;

    private final CitizenRepository citizenRepository;

    private final CitizenHistoryRepository citizenHistoryRepository;

    public Citizen createCitizen(Citizen citizen) {
        Optional<Citizen> citizenWithSameIdentity = citizenRepository.findByIdentityCard(citizen.getIdentityCard());
        if (citizenWithSameIdentity.isPresent()) {
            throw new DuplicateKeyException("This identity card is already in use");
        }
        return citizenRepository.save(citizen);
    }

    public Citizen getById(String id) {
        return citizenRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("No citizen found with the provided id"));
    }

    public Citizen filterByIdentityCard(String identityCard) {
        return  citizenRepository.findByIdentityCard(identityCard)
                          .orElseThrow(() -> new NoSuchElementException("No citizen found with the provided identity card"));
    }

    public List<Citizen> filterByBirthdate(LocalDate birthdate, String operator) {
        if (!operatorIsValid(operator)) {
            throw new IllegalArgumentException("The operator value must be eq/lt/gt");
        }
        return  citizenRepository.filterByBirthdate(birthdate, operator);
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
        citizenRepository.save(citizenToUpdate);
    }

    private boolean isIdentityCardInUse(Citizen citizenToUpdate, String newIdentityCard) {
        Optional<Citizen> citizenWithProvidedIdentity = citizenRepository.findByIdentityCard(newIdentityCard);
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
        citizenRepository.save(citizen);
    }

    public void updateIdentityCardAndName(String id, IdentityCardAndNameRequest request) {
        Citizen citizen = getById(id);
        for (String fieldName : request.getUpdate()) {
            if (isAllowedField(fieldName)) {
                String value = getObjectFieldValue(fieldName, request);
                if (isAllowedValue(citizen, fieldName, value)) {
                    updateField(citizen, Map.entry(fieldName, String.valueOf(value)));
                }
            }
        }
        citizenRepository.save(citizen);
    }

    private boolean isAllowedField(String fieldName) {
    return AllowedFields.IDENTITY_CARD_FIELD.equals(fieldName) || AllowedFields.NAME_FIELD.equals(fieldName);
    }

    private boolean isAllowedValue(Citizen citizenToUpdate, String field, String value)
    {
        boolean isAllowed;
        switch (field) {
            case AllowedFields.IDENTITY_CARD_FIELD -> {
                isAllowed = !isIdentityCardInUse(citizenToUpdate, value);
                if (!isAllowed) {
                    throw new DuplicateKeyException("This identity card is already in use");
                }
            }
            case AllowedFields.NAME_FIELD -> isAllowed = true;
            default -> isAllowed = false;
        }
        return isAllowed;
    }

    private String getObjectFieldValue(String fieldName, Object objectWithField) {
        try {
            Field requestedField =  objectWithField.getClass().getDeclaredField(fieldName);
            requestedField.setAccessible(true);
            Object value = requestedField.get(objectWithField);
            return value.toString();

        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private void updateField(Object object, Map.Entry<String, String> entry ) {
        try {
            Field field =  object.getClass().getDeclaredField(entry.getKey());
            field.setAccessible(true);
            field.set(object, entry.getValue());
        }
        catch (NoSuchFieldException ex) {
            throw new IllegalArgumentException(ex);
        }
        catch (IllegalAccessException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Transactional(noRollbackFor = {RuntimeException.class})
    public void updateIdentityCardAndNameTransaction(String id, IdentityCardAndNameRequest request) {
        List<String> exceptionMessages = new ArrayList<>();
        Citizen citizen = null;
        CitizenHistory citizenHistory = null;
        try {
            citizen = getById(id);
            citizenHistory = modelMapper.map(citizen, CitizenHistory.class);
        }
        catch (RuntimeException ex) {
            log.error(TRANSACTION_ERROR_CITIZEN_NOT_FOUND);
            exceptionMessages.add(TRANSACTION_ERROR_CITIZEN_NOT_FOUND);
        }
        if (citizen != null && citizenHistory != null) {
            try {
                updateIdentityCardAndName(id, request);
            }
            catch (RuntimeException ex) {
                log.error(TRANSACTION_ERROR_CITIZEN_UPDATE_FAILURE);
                exceptionMessages.add(TRANSACTION_ERROR_CITIZEN_UPDATE_FAILURE);
            }
            try {
                citizenHistoryRepository.save(citizenHistory);
            }
            catch (RuntimeException ex) {
                log.error(TRANSACTION_ERROR_HISTORY_SAVE_FAILURE);
                exceptionMessages.add(TRANSACTION_ERROR_HISTORY_SAVE_FAILURE);
            }
        }
        if (!exceptionMessages.isEmpty()) {
            throw new RuntimeException(aggregateMessages(exceptionMessages));
        }
    }

    private String aggregateMessages(List<String> messages) {
        StringBuilder builder = new StringBuilder();
        for (String message : messages) {
            builder.append(message);
            builder.append(" | ");
        }
        builder.setLength(builder.length() -3);
        return  builder.toString();
    }
}