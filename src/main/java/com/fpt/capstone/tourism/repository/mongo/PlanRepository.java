package com.fpt.capstone.tourism.repository.mongo;


import com.fpt.capstone.tourism.model.enums.PlanStatus;
import com.fpt.capstone.tourism.model.mongo.Plan;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;
import org.springframework.stereotype.Repository;

@Repository
public interface PlanRepository extends MongoRepository<Plan, String> {
    Page<Plan> findByCreatorIdAndPlanStatus(int creatorId, PlanStatus planStatus, Pageable pageable);

    @Query("{ '_id': ?0 }")
    @Update("{ '$set': { 'planStatus': ?1 } }")
    void updatePlanStatusById(String planId, PlanStatus status);

    void deletePlanById(String id);




}
