package com.clinic.management.repository;

import com.clinic.management.entity.Visit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface VisitRepository extends MongoRepository<Visit, String> {

    Optional<Visit> findByVisitId(String visitId);
    
    Page<Visit> findByPatientId(String patientId, Pageable pageable);
    
    List<Visit> findByPatientId(String patientId);
    
    List<Visit> findByVisitDateBetween(LocalDateTime start, LocalDateTime end);
    
    Page<Visit> findByVisitDateBetween(LocalDateTime start, LocalDateTime end, Pageable pageable);

    Optional<Visit> findTopByOrderByVisitIdDesc();
}
