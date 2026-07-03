package com.clinic.management.service;

import com.clinic.management.dto.PrescriptionDTO;
import com.clinic.management.entity.Patient;
import com.clinic.management.entity.Prescription;
import com.clinic.management.entity.Visit;
import com.clinic.management.exception.DuplicateResourceException;
import com.clinic.management.exception.ResourceNotFoundException;
import com.clinic.management.mapper.PrescriptionMapper;
import com.clinic.management.repository.PatientRepository;
import com.clinic.management.repository.PrescriptionRepository;
import com.clinic.management.repository.VisitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;
    private final PrescriptionMapper prescriptionMapper;
    private final PatientRepository patientRepository;
    private final VisitRepository visitRepository;
    private final PdfGenerationService pdfGenerationService;

    public PrescriptionDTO createPrescription(PrescriptionDTO prescriptionDTO) {
        if (prescriptionRepository.findByVisitId(prescriptionDTO.getVisitId()).isPresent()) {
            throw new DuplicateResourceException("Prescription already exists for this visit");
        }
        
        if (!patientRepository.findByPatientIdAndActiveTrue(prescriptionDTO.getPatientId()).isPresent()) {
            throw new ResourceNotFoundException("Patient not found");
        }
        
        if (!visitRepository.findByVisitId(prescriptionDTO.getVisitId()).isPresent()) {
            throw new ResourceNotFoundException("Visit not found");
        }

        Prescription prescription = prescriptionMapper.toEntity(prescriptionDTO);
        prescription.setPrescriptionId(generatePrescriptionId());
        
        if (prescription.getPrescriptionDate() == null) {
            prescription.setPrescriptionDate(LocalDateTime.now());
        }
        prescription.setCreatedAt(LocalDateTime.now());
        
        Prescription savedPrescription = prescriptionRepository.save(prescription);
        return prescriptionMapper.toDto(savedPrescription);
    }

    public PrescriptionDTO updatePrescription(String id, PrescriptionDTO prescriptionDTO) {
        Prescription existingPrescription = prescriptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Prescription not found with id: " + id));

        prescriptionMapper.updateEntityFromDto(prescriptionDTO, existingPrescription);
        
        Prescription updatedPrescription = prescriptionRepository.save(existingPrescription);
        return prescriptionMapper.toDto(updatedPrescription);
    }

    public void deletePrescription(String id) {
        if (!prescriptionRepository.existsById(id)) {
            throw new ResourceNotFoundException("Prescription not found with id: " + id);
        }
        prescriptionRepository.deleteById(id);
    }

    public PrescriptionDTO getPrescriptionById(String id) {
        Prescription prescription = prescriptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Prescription not found with id: " + id));
        return prescriptionMapper.toDto(prescription);
    }
    
    public PrescriptionDTO getPrescriptionByVisitId(String visitId) {
        Prescription prescription = prescriptionRepository.findByVisitId(visitId)
                .orElseThrow(() -> new ResourceNotFoundException("Prescription not found with visitId: " + visitId));
        return prescriptionMapper.toDto(prescription);
    }

    public Page<PrescriptionDTO> getPrescriptionsByPatientId(String patientId, Pageable pageable) {
        return prescriptionRepository.findByPatientId(patientId, pageable)
                .map(prescriptionMapper::toDto);
    }

    public byte[] generatePrescriptionPdf(String id) {
        Prescription prescription = prescriptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Prescription not found"));
                
        Patient patient = patientRepository.findByPatientIdAndActiveTrue(prescription.getPatientId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found"));
                
        Visit visit = visitRepository.findByVisitId(prescription.getVisitId())
                .orElseThrow(() -> new ResourceNotFoundException("Visit not found"));

        Map<String, Object> variables = new HashMap<>();
        variables.put("clinicName", "CarePlus Clinic"); // You could inject this from properties
        variables.put("patientName", patient.getFullName());
        variables.put("patientId", patient.getPatientId());
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a");
        variables.put("visitDate", prescription.getPrescriptionDate().format(formatter));
        
        variables.put("age", patient.getAge() != null ? patient.getAge().toString() : "N/A");
        variables.put("gender", patient.getGender() != null ? patient.getGender() : "N/A");
        
        variables.put("diagnosis", visit.getDiagnosis() != null ? visit.getDiagnosis() : "N/A");
        variables.put("bp", visit.getBloodPressure() != null ? visit.getBloodPressure() : "N/A");
        variables.put("pulse", visit.getPulseRate() != null ? visit.getPulseRate().toString() + " bpm" : "N/A");
        variables.put("temp", visit.getTemperature() != null ? visit.getTemperature().toString() + " F" : "N/A");
        
        variables.put("medicines", prescription.getMedicineList());
        variables.put("doctorName", visit.getDoctorName() != null ? visit.getDoctorName() : "Dr. Default");

        return pdfGenerationService.generatePdf("prescription-template", variables);
    }

    private String generatePrescriptionId() {
        return prescriptionRepository.findTopByOrderByPrescriptionIdDesc()
                .map(prescription -> {
                    String lastId = prescription.getPrescriptionId(); // e.g. PRE000001
                    int num = Integer.parseInt(lastId.substring(3));
                    return String.format("PRE%06d", num + 1);
                })
                .orElse("PRE000001");
    }
}
