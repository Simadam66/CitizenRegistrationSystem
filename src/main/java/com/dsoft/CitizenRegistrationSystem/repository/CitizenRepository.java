package com.dsoft.CitizenRegistrationSystem.repository;

import com.dsoft.CitizenRegistrationSystem.model.Citizen;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface CitizenRepository extends MongoRepository<Citizen, String> {
    Optional<Citizen> findByIdentityCard(String identityCard);

    @Query("{birthdate: {'$?1': ?0}}")
    List<Citizen> filterByBirthdate(LocalDate birthdate, String operator);
}
