package com.clinic.management.repository;

import com.clinic.management.entity.Medicine;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MedicineRepository extends MongoRepository<Medicine, String> {

    Optional<Medicine> findByMedicineId(String medicineId);

    Page<Medicine> findByAvailableTrue(Pageable pageable);

    @Query("{ 'medicineName': { $regex: ?0, $options: 'i' } }")
    Page<Medicine> searchMedicines(String searchTerm, Pageable pageable);
    
    @Query("{ 'available': true, 'medicineName': { $regex: ?0, $options: 'i' } }")
    Page<Medicine> searchAvailableMedicines(String searchTerm, Pageable pageable);

    Optional<Medicine> findTopByOrderByMedicineIdDesc();
}
