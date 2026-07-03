package com.clinic.management.repository;

import com.clinic.management.entity.Treatment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TreatmentRepository extends MongoRepository<Treatment, String> {

    Optional<Treatment> findByTreatmentRecordId(String treatmentRecordId);
    
    Page<Treatment> findByVisitId(String visitId, Pageable pageable);
    
    List<Treatment> findByVisitId(String visitId);
    
    Page<Treatment> findByPatientId(String patientId, Pageable pageable);
    
    List<Treatment> findByPatientId(String patientId);
    
    @Query("{ 'patientId': ?0, 'treatmentName': { $regex: ?1, $options: 'i' } }")
    Page<Treatment> searchTreatmentsByPatient(String patientId, String searchTerm, Pageable pageable);

    Optional<Treatment> findTopByOrderByTreatmentRecordIdDesc();
}
