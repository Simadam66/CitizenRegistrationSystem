package com.dsoft.CitizenRegistrationSystem.repository;

import com.dsoft.CitizenRegistrationSystem.model.CitizenHistory;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CitizenHistoryRepository extends MongoRepository<CitizenHistory, String> {
}
