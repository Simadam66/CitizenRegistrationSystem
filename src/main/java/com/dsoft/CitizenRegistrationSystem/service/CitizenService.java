package com.dsoft.CitizenRegistrationSystem.service;

import com.dsoft.CitizenRegistrationSystem.dto.CitizenResponse;
import com.dsoft.CitizenRegistrationSystem.model.Citizen;
import com.dsoft.CitizenRegistrationSystem.repository.CitizenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CitizenService {

    private final CitizenRepository repository;

    public void createCitizen(Citizen citizen) {
        repository.save(citizen);
    }

    public Citizen getByIdentityCard(String identityCard) {
        return  repository.findByIdentityCard(identityCard).orElseThrow();
    }
}
