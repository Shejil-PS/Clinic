package com.clinic.management.service;

import com.clinic.management.dto.MedicineDTO;
import com.clinic.management.entity.Medicine;
import com.clinic.management.exception.ResourceNotFoundException;
import com.clinic.management.mapper.MedicineMapper;
import com.clinic.management.repository.MedicineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class MedicineService {

    private final MedicineRepository medicineRepository;
    private final MedicineMapper medicineMapper;

    public MedicineDTO createMedicine(MedicineDTO medicineDTO) {
        Medicine medicine = medicineMapper.toEntity(medicineDTO);
        medicine.setMedicineId(generateMedicineId());
        if (medicine.getAvailable() == null) {
            medicine.setAvailable(true);
        }
        medicine.setCreatedAt(LocalDateTime.now());
        medicine.setUpdatedAt(LocalDateTime.now());
        
        Medicine savedMedicine = medicineRepository.save(medicine);
        return medicineMapper.toDto(savedMedicine);
    }

    public MedicineDTO updateMedicine(String id, MedicineDTO medicineDTO) {
        Medicine existingMedicine = medicineRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Medicine not found with id: " + id));

        medicineMapper.updateEntityFromDto(medicineDTO, existingMedicine);
        existingMedicine.setUpdatedAt(LocalDateTime.now());
        
        Medicine updatedMedicine = medicineRepository.save(existingMedicine);
        return medicineMapper.toDto(updatedMedicine);
    }

    public void deleteMedicine(String id) {
        if (!medicineRepository.existsById(id)) {
            throw new ResourceNotFoundException("Medicine not found with id: " + id);
        }
        // Requirement was delete medicine. Assuming hard delete for medicine or soft delete via 'available' flag.
        // Usually medicines are hard deleted if unused, but let's just use hard delete as per generic "Delete Medicine"
        // unless soft delete was specified for Medicine. The prompt only said "Delete Medicine".
        medicineRepository.deleteById(id);
    }

    public MedicineDTO getMedicineById(String id) {
        Medicine medicine = medicineRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Medicine not found with id: " + id));
        return medicineMapper.toDto(medicine);
    }

    public Page<MedicineDTO> searchMedicines(String term, Boolean availableOnly, Pageable pageable) {
        if (term == null || term.trim().isEmpty()) {
            if (availableOnly != null && availableOnly) {
                return medicineRepository.findByAvailableTrue(pageable).map(medicineMapper::toDto);
            }
            return medicineRepository.findAll(pageable).map(medicineMapper::toDto);
        }
        
        if (availableOnly != null && availableOnly) {
            return medicineRepository.searchAvailableMedicines(term, pageable).map(medicineMapper::toDto);
        }
        
        return medicineRepository.searchMedicines(term, pageable).map(medicineMapper::toDto);
    }

    private String generateMedicineId() {
        return medicineRepository.findTopByOrderByMedicineIdDesc()
                .map(medicine -> {
                    String lastId = medicine.getMedicineId(); // e.g. MED000001
                    int num = Integer.parseInt(lastId.substring(3));
                    return String.format("MED%06d", num + 1);
                })
                .orElse("MED000001");
    }
}
