package com.clinic.management.repository;

import com.clinic.management.entity.TreatmentMaster;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TreatmentMasterRepository extends MongoRepository<TreatmentMaster, String> {

    Optional<TreatmentMaster> findByTreatmentId(String treatmentId);
    
    Page<TreatmentMaster> findByActiveTrue(Pageable pageable);

    @Query("{ 'treatmentName': { $regex: ?0, $options: 'i' } }")
    Page<TreatmentMaster> searchTreatments(String searchTerm, Pageable pageable);
    
    @Query("{ 'active': true, 'treatmentName': { $regex: ?0, $options: 'i' } }")
    Page<TreatmentMaster> searchActiveTreatments(String searchTerm, Pageable pageable);

    Optional<TreatmentMaster> findTopByOrderByTreatmentIdDesc();
}
