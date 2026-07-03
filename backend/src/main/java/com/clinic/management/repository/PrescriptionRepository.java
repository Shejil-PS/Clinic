package com.clinic.management.repository;

import com.clinic.management.entity.Prescription;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PrescriptionRepository extends MongoRepository<Prescription, String> {
    
    Optional<Prescription> findByPrescriptionId(String prescriptionId);
    
    Optional<Prescription> findByVisitId(String visitId);
    
    Page<Prescription> findByPatientId(String patientId, Pageable pageable);
    
    Optional<Prescription> findTopByOrderByPrescriptionIdDesc();
}
