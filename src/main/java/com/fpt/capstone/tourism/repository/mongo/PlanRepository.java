package com.fpt.capstone.tourism.repository.mongo;


import com.fpt.capstone.tourism.model.mongo.Plan;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlanRepository extends MongoRepository<Plan, Long> {
}
