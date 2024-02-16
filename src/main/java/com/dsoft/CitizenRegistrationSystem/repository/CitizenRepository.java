package com.dsoft.CitizenRegistrationSystem.repository;

import com.dsoft.CitizenRegistrationSystem.model.Citizen;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CitizenRepository extends MongoRepository<Citizen, String> {
    Optional<Citizen> findByIdentityCard(String identityCard);
}
