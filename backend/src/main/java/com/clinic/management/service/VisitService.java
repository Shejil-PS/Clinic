package com.clinic.management.service;

import com.clinic.management.dto.VisitDTO;
import com.clinic.management.entity.Visit;
import com.clinic.management.exception.ResourceNotFoundException;
import com.clinic.management.mapper.VisitMapper;
import com.clinic.management.repository.PatientRepository;
import com.clinic.management.repository.VisitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class VisitService {

    private final VisitRepository visitRepository;
    private final VisitMapper visitMapper;
    private final PatientRepository patientRepository;

    public VisitDTO createVisit(VisitDTO visitDTO) {
        if (!patientRepository.findByPatientIdAndActiveTrue(visitDTO.getPatientId()).isPresent()) {
            throw new ResourceNotFoundException("Patient not found with patientId: " + visitDTO.getPatientId());
        }

        Visit visit = visitMapper.toEntity(visitDTO);
        visit.setVisitId(generateVisitId());
        if (visit.getVisitDate() == null) {
            visit.setVisitDate(LocalDateTime.now());
        }
        visit.setCreatedAt(LocalDateTime.now());
        visit.setUpdatedAt(LocalDateTime.now());
        
        Visit savedVisit = visitRepository.save(visit);
        return visitMapper.toDto(savedVisit);
    }

    public VisitDTO updateVisit(String id, VisitDTO visitDTO) {
        Visit existingVisit = visitRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Visit not found with id: " + id));

        if (!existingVisit.getPatientId().equals(visitDTO.getPatientId())) {
            if (!patientRepository.findByPatientIdAndActiveTrue(visitDTO.getPatientId()).isPresent()) {
                throw new ResourceNotFoundException("Patient not found with patientId: " + visitDTO.getPatientId());
            }
        }

        visitMapper.updateEntityFromDto(visitDTO, existingVisit);
        existingVisit.setUpdatedAt(LocalDateTime.now());
        
        Visit updatedVisit = visitRepository.save(existingVisit);
        return visitMapper.toDto(updatedVisit);
    }

    public void deleteVisit(String id) {
        if (!visitRepository.existsById(id)) {
            throw new ResourceNotFoundException("Visit not found with id: " + id);
        }
        visitRepository.deleteById(id);
    }

    public VisitDTO getVisitById(String id) {
        Visit visit = visitRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Visit not found with id: " + id));
        return visitMapper.toDto(visit);
    }

    public Page<VisitDTO> getVisitsByPatientId(String patientId, Pageable pageable) {
        if (!patientRepository.findByPatientIdAndActiveTrue(patientId).isPresent()) {
            throw new ResourceNotFoundException("Patient not found with patientId: " + patientId);
        }
        return visitRepository.findByPatientId(patientId, pageable)
                .map(visitMapper::toDto);
    }

    public Page<VisitDTO> getAllVisits(Pageable pageable) {
        return visitRepository.findAll(pageable)
                .map(visitMapper::toDto);
    }

    private String generateVisitId() {
        return visitRepository.findTopByOrderByVisitIdDesc()
                .map(visit -> {
                    String lastId = visit.getVisitId(); // e.g. VIS000001
                    int num = Integer.parseInt(lastId.substring(3));
                    return String.format("VIS%06d", num + 1);
                })
                .orElse("VIS000001");
    }
}
