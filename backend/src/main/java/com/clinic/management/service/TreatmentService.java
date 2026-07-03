package com.clinic.management.service;

import com.clinic.management.dto.TreatmentRequestDTO;
import com.clinic.management.dto.TreatmentResponseDTO;
import com.clinic.management.entity.Treatment;
import com.clinic.management.exception.ResourceNotFoundException;
import com.clinic.management.mapper.TreatmentMapper;
import com.clinic.management.repository.PatientRepository;
import com.clinic.management.repository.TreatmentRepository;
import com.clinic.management.repository.VisitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TreatmentService {

    private final TreatmentRepository treatmentRepository;
    private final TreatmentMapper treatmentMapper;
    private final PatientRepository patientRepository;
    private final VisitRepository visitRepository;

    public TreatmentResponseDTO createTreatment(String visitId, String patientId, TreatmentRequestDTO requestDTO) {
        if (!visitRepository.findByVisitId(visitId).isPresent()) {
            throw new ResourceNotFoundException("Visit not found with visitId: " + visitId);
        }
        if (!patientRepository.findByPatientIdAndActiveTrue(patientId).isPresent()) {
            throw new ResourceNotFoundException("Patient not found with patientId: " + patientId);
        }

        Treatment treatment = treatmentMapper.toEntity(requestDTO);
        treatment.setTreatmentId(generateTreatmentId());
        treatment.setVisitId(visitId);
        treatment.setPatientId(patientId);
        treatment.setCreatedAt(LocalDateTime.now());
        treatment.setUpdatedAt(LocalDateTime.now());
        
        Treatment savedTreatment = treatmentRepository.save(treatment);
        return treatmentMapper.toResponseDto(savedTreatment);
    }
    
    public List<TreatmentResponseDTO> createTreatmentsForVisit(String visitId, String patientId, List<TreatmentRequestDTO> requestDTOs) {
        return requestDTOs.stream()
                .map(dto -> createTreatment(visitId, patientId, dto))
                .collect(Collectors.toList());
    }

    public TreatmentResponseDTO updateTreatment(String id, TreatmentRequestDTO requestDTO) {
        Treatment existingTreatment = treatmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Treatment not found with id: " + id));

        treatmentMapper.updateEntityFromDto(requestDTO, existingTreatment);
        existingTreatment.setUpdatedAt(LocalDateTime.now());
        
        Treatment updatedTreatment = treatmentRepository.save(existingTreatment);
        return treatmentMapper.toResponseDto(updatedTreatment);
    }

    public void deleteTreatment(String id) {
        if (!treatmentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Treatment not found with id: " + id);
        }
        treatmentRepository.deleteById(id);
    }

    public TreatmentResponseDTO getTreatmentById(String id) {
        Treatment treatment = treatmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Treatment not found with id: " + id));
        return treatmentMapper.toResponseDto(treatment);
    }

    public Page<TreatmentResponseDTO> getTreatmentsByVisitId(String visitId, Pageable pageable) {
        return treatmentRepository.findByVisitId(visitId, pageable)
                .map(treatmentMapper::toResponseDto);
    }
    
    public Page<TreatmentResponseDTO> getTreatmentsByPatientId(String patientId, Pageable pageable) {
        return treatmentRepository.findByPatientId(patientId, pageable)
                .map(treatmentMapper::toResponseDto);
    }
    
    public Page<TreatmentResponseDTO> searchTreatmentsByPatient(String patientId, String searchTerm, Pageable pageable) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return getTreatmentsByPatientId(patientId, pageable);
        }
        return treatmentRepository.searchTreatmentsByPatient(patientId, searchTerm, pageable)
                .map(treatmentMapper::toResponseDto);
    }

    private String generateTreatmentId() {
        return treatmentRepository.findTopByOrderByTreatmentIdDesc()
                .map(treatment -> {
                    String lastId = treatment.getTreatmentId(); // e.g. TRT000001
                    int num = Integer.parseInt(lastId.substring(3));
                    return String.format("TRT%06d", num + 1);
                })
                .orElse("TRT000001");
    }
}
