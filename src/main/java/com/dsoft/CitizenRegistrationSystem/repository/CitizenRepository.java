package com.dsoft.CitizenRegistrationSystem.repository;

import com.dsoft.CitizenRegistrationSystem.model.Citizen;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CitizenRepository extends MongoRepository<Citizen, String> {
}
