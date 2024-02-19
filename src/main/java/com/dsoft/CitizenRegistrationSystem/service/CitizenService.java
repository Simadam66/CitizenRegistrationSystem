package com.dsoft.CitizenRegistrationSystem.service;

import com.dsoft.CitizenRegistrationSystem.model.Citizen;
import com.dsoft.CitizenRegistrationSystem.repository.CitizenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CitizenService {

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


}
