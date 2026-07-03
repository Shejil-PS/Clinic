package com.clinic.management.service;

import com.clinic.management.dto.TreatmentMasterDTO;
import com.clinic.management.entity.TreatmentMaster;
import com.clinic.management.exception.ResourceNotFoundException;
import com.clinic.management.mapper.TreatmentMasterMapper;
import com.clinic.management.repository.TreatmentMasterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TreatmentMasterService {

    private final TreatmentMasterRepository treatmentMasterRepository;
    private final TreatmentMasterMapper treatmentMasterMapper;

    public TreatmentMasterDTO createTreatmentMaster(TreatmentMasterDTO dto) {
        TreatmentMaster master = treatmentMasterMapper.toEntity(dto);
        master.setTreatmentId(generateTreatmentMasterId());
        if (master.getActive() == null) {
            master.setActive(true);
        }
        master.setCreatedAt(LocalDateTime.now());
        master.setUpdatedAt(LocalDateTime.now());
        
        TreatmentMaster saved = treatmentMasterRepository.save(master);
        return treatmentMasterMapper.toDto(saved);
    }

    public TreatmentMasterDTO updateTreatmentMaster(String id, TreatmentMasterDTO dto) {
        TreatmentMaster existing = treatmentMasterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Treatment Master not found with id: " + id));

        treatmentMasterMapper.updateEntityFromDto(dto, existing);
        existing.setUpdatedAt(LocalDateTime.now());
        
        TreatmentMaster updated = treatmentMasterRepository.save(existing);
        return treatmentMasterMapper.toDto(updated);
    }

    public void deleteTreatmentMaster(String id) {
        if (!treatmentMasterRepository.existsById(id)) {
            throw new ResourceNotFoundException("Treatment Master not found with id: " + id);
        }
        treatmentMasterRepository.deleteById(id);
    }

    public TreatmentMasterDTO getTreatmentMasterById(String id) {
        TreatmentMaster master = treatmentMasterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Treatment Master not found with id: " + id));
        return treatmentMasterMapper.toDto(master);
    }

    public Page<TreatmentMasterDTO> searchTreatments(String term, Boolean activeOnly, Pageable pageable) {
        if (term == null || term.trim().isEmpty()) {
            if (activeOnly != null && activeOnly) {
                return treatmentMasterRepository.findByActiveTrue(pageable).map(treatmentMasterMapper::toDto);
            }
            return treatmentMasterRepository.findAll(pageable).map(treatmentMasterMapper::toDto);
        }
        
        if (activeOnly != null && activeOnly) {
            return treatmentMasterRepository.searchActiveTreatments(term, pageable).map(treatmentMasterMapper::toDto);
        }
        
        return treatmentMasterRepository.searchTreatments(term, pageable).map(treatmentMasterMapper::toDto);
    }

    private String generateTreatmentMasterId() {
        return treatmentMasterRepository.findTopByOrderByTreatmentIdDesc()
                .map(master -> {
                    String lastId = master.getTreatmentId(); // e.g. TRTM000001
                    int num = Integer.parseInt(lastId.substring(4));
                    return String.format("TRTM%06d", num + 1);
                })
                .orElse("TRTM000001");
    }
}
