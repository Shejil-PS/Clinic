package com.clinic.management.repository;

import com.clinic.management.entity.Bill;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface BillRepository extends MongoRepository<Bill, String> {

    Optional<Bill> findByBillNumber(String billNumber);
    
    Optional<Bill> findByVisitId(String visitId);
    
    List<Bill> findByPatientId(String patientId);

    Optional<Bill> findTopByOrderByBillNumberDesc();
}
