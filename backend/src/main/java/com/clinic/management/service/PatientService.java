package com.clinic.management.service;

import com.clinic.management.dto.PatientDTO;
import com.clinic.management.entity.Patient;
import com.clinic.management.exception.ResourceNotFoundException;
import com.clinic.management.mapper.PatientMapper;
import com.clinic.management.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PatientService {

    private final PatientRepository patientRepository;
    private final PatientMapper patientMapper;

    public PatientDTO createPatient(PatientDTO patientDTO) {
        Patient patient = patientMapper.toEntity(patientDTO);
        patient.setPatientId(generatePatientId());
        patient.setActive(true);
        patient.setCreatedAt(LocalDateTime.now());
        patient.setUpdatedAt(LocalDateTime.now());
        
        Patient savedPatient = patientRepository.save(patient);
        return patientMapper.toDto(savedPatient);
    }

    public PatientDTO updatePatient(String id, PatientDTO patientDTO) {
        Patient existingPatient = patientRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with id: " + id));

        patientMapper.updateEntityFromDto(patientDTO, existingPatient);
        existingPatient.setUpdatedAt(LocalDateTime.now());
        
        Patient updatedPatient = patientRepository.save(existingPatient);
        return patientMapper.toDto(updatedPatient);
    }

    public void deletePatient(String id) {
        Patient patient = patientRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with id: " + id));
        
        patient.setActive(false);
        patient.setUpdatedAt(LocalDateTime.now());
        patientRepository.save(patient);
    }

    public PatientDTO getPatientById(String id) {
        Patient patient = patientRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with id: " + id));
        return patientMapper.toDto(patient);
    }

    public PatientDTO getPatientByPatientId(String patientId) {
        Patient patient = patientRepository.findByPatientIdAndActiveTrue(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with patientId: " + patientId));
        return patientMapper.toDto(patient);
    }

    public Page<PatientDTO> getAllPatients(Pageable pageable) {
        return patientRepository.findByActiveTrue(pageable)
                .map(patientMapper::toDto);
    }

    public Page<PatientDTO> searchPatients(String searchTerm, Pageable pageable) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return getAllPatients(pageable);
        }
        return patientRepository.searchPatients(searchTerm, pageable)
                .map(patientMapper::toDto);
    }

    private String generatePatientId() {
        return patientRepository.findTopByOrderByPatientIdDesc()
                .map(patient -> {
                    String lastId = patient.getPatientId(); // e.g. PAT000001
                    int num = Integer.parseInt(lastId.substring(3));
                    return String.format("PAT%06d", num + 1);
                })
                .orElse("PAT000001");
    }
}
