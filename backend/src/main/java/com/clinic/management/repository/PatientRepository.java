package com.clinic.management.repository;

import com.clinic.management.entity.Patient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PatientRepository extends MongoRepository<Patient, String> {

    Optional<Patient> findByPatientIdAndActiveTrue(String patientId);
    
    Optional<Patient> findByIdAndActiveTrue(String id);
    
    Optional<Patient> findByPhoneAndActiveTrue(String phone);

    Page<Patient> findByActiveTrue(Pageable pageable);

    @Query("{ 'active': true, '$or': [ " +
            "{ 'fullName': { $regex: ?0, $options: 'i' } }, " +
            "{ 'phone': { $regex: ?0, $options: 'i' } }, " +
            "{ 'patientId': { $regex: ?0, $options: 'i' } } " +
            "] }")
    Page<Patient> searchPatients(String searchTerm, Pageable pageable);
    
    // To generate the next PAT sequence
    Optional<Patient> findTopByOrderByPatientIdDesc();
}
