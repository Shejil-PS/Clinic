package com.clinic.management.service;

import com.clinic.management.dto.VisitDTO;
import com.clinic.management.entity.Visit;
import com.clinic.management.exception.ResourceNotFoundException;
import com.clinic.management.mapper.VisitMapper;
import com.clinic.management.dto.FinishConsultationRequestDTO;
import com.clinic.management.dto.PatientRegistrationDTO;
import com.clinic.management.dto.PrescriptionDTO;
import com.clinic.management.dto.VisitQueueDTO;
import com.clinic.management.dto.BillDTO;
import com.clinic.management.entity.Bill;
import com.clinic.management.entity.Patient;
import com.clinic.management.repository.PatientRepository;
import com.clinic.management.repository.VisitRepository;
import com.clinic.management.dto.VisitRequestDTO;
import com.clinic.management.service.TreatmentService;
import com.clinic.management.service.PrescriptionService;
import com.clinic.management.service.BillService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VisitService {

    private final VisitRepository visitRepository;
    private final VisitMapper visitMapper;
    private final PatientRepository patientRepository;
    private final TreatmentService treatmentService;
    private final PrescriptionService prescriptionService;
    private final BillService billService;

    @Transactional
    public VisitDTO createVisit(VisitRequestDTO visitRequestDTO) {
        if (!patientRepository.findByPatientIdAndActiveTrue(visitRequestDTO.getPatientId()).isPresent()) {
            throw new ResourceNotFoundException("Patient not found with patientId: " + visitRequestDTO.getPatientId());
        }

        Visit visit = new Visit();
        visit.setPatientId(visitRequestDTO.getPatientId());
        visit.setDoctorName(visitRequestDTO.getDoctorName());
        visit.setChiefComplaint(visitRequestDTO.getChiefComplaint());
        visit.setDiagnosis(visitRequestDTO.getDiagnosis());
        visit.setBloodPressure(visitRequestDTO.getBloodPressure());
        visit.setPulseRate(visitRequestDTO.getPulseRate());
        visit.setTemperature(visitRequestDTO.getTemperature());
        visit.setHeight(visitRequestDTO.getHeight());
        visit.setWeight(visitRequestDTO.getWeight());
        visit.setNotes(visitRequestDTO.getNotes());
        visit.setFollowUpDate(visitRequestDTO.getFollowUpDate());

        visit.setVisitId(generateVisitId());
        
        if (visitRequestDTO.getStatus() != null) {
            visit.setStatus(visitRequestDTO.getStatus());
        } else {
            visit.setStatus("WAITING");
        }
        
        if (visitRequestDTO.getVisitDate() == null) {
            visit.setVisitDate(LocalDateTime.now());
        } else {
            visit.setVisitDate(visitRequestDTO.getVisitDate());
        }
        visit.setCreatedAt(LocalDateTime.now());
        visit.setUpdatedAt(LocalDateTime.now());
        
        Visit savedVisit = visitRepository.save(visit);

        if (visitRequestDTO.getTreatments() != null && !visitRequestDTO.getTreatments().isEmpty()) {
            treatmentService.createTreatmentsForVisit(savedVisit.getVisitId(), savedVisit.getPatientId(), visitRequestDTO.getTreatments());
        }

        return visitMapper.toDto(savedVisit);
    }

    public VisitDTO updateVisit(String id, VisitDTO visitDTO) {
        Visit existingVisit = visitRepository.findByVisitId(id)
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
        Visit existingVisit = visitRepository.findByVisitId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Visit not found with id: " + id));
        visitRepository.delete(existingVisit);
    }

    public VisitDTO getVisitById(String id) {
        Visit visit = visitRepository.findByVisitId(id)
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

    @Transactional
    public VisitDTO registerConsultation(PatientRegistrationDTO registrationDTO) {
        List<Patient> existingPatients = patientRepository.findByPhoneAndActiveTrue(registrationDTO.getPhone());
        
        Patient patient;
        if (!existingPatients.isEmpty()) {
            patient = existingPatients.get(0);
        } else {
            patient = new Patient();
            patient.setPatientId(generatePatientId());
            patient.setPhone(registrationDTO.getPhone());
            patient.setFullName(registrationDTO.getFullName());
            patient.setAge(registrationDTO.getAge());
            patient.setGender(registrationDTO.getGender());
            patient.setAddress(registrationDTO.getAddress());
            patient.setActive(true);
            patient.setCreatedAt(LocalDateTime.now());
            patient.setUpdatedAt(LocalDateTime.now());
            patient = patientRepository.save(patient);
        }

        Visit visit = new Visit();
        visit.setPatientId(patient.getPatientId());
        visit.setDoctorName(registrationDTO.getDoctorName());
        visit.setChiefComplaint(registrationDTO.getChiefComplaint());
        visit.setVisitId(generateVisitId());
        visit.setVisitDate(LocalDateTime.now());
        visit.setStatus("WAITING");
        visit.setCreatedAt(LocalDateTime.now());
        visit.setUpdatedAt(LocalDateTime.now());
        
        Visit savedVisit = visitRepository.save(visit);
        return visitMapper.toDto(savedVisit);
    }
    
    public Page<VisitQueueDTO> getConsultationQueue(LocalDate date, Pageable pageable) {
        if (date == null) {
            date = LocalDate.now();
        }
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);
        
        return visitRepository.findByVisitDateBetween(startOfDay, endOfDay, pageable)
                .map(visit -> {
                    VisitQueueDTO queueDTO = new VisitQueueDTO();
                    queueDTO.setVisitId(visit.getVisitId());
                    queueDTO.setPatientId(visit.getPatientId());
                    queueDTO.setRegistrationTime(visit.getCreatedAt());
                    queueDTO.setVisitStatus(visit.getStatus());
                    
                    patientRepository.findByPatientIdAndActiveTrue(visit.getPatientId())
                            .ifPresent(patient -> {
                                queueDTO.setPatientName(patient.getFullName());
                                queueDTO.setPhone(patient.getPhone());
                            });
                            
                    return queueDTO;
                });
    }

    @Transactional
    public VisitDTO finishConsultation(String id, FinishConsultationRequestDTO requestDTO) {
        Visit visit = visitRepository.findByVisitId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Visit not found with id: " + id));

        // 1. Update Visit details
        if (requestDTO.getDiagnosis() != null) visit.setDiagnosis(requestDTO.getDiagnosis());
        if (requestDTO.getNotes() != null) visit.setNotes(requestDTO.getNotes());
        if (requestDTO.getBloodPressure() != null) visit.setBloodPressure(requestDTO.getBloodPressure());
        if (requestDTO.getPulseRate() != null) visit.setPulseRate(requestDTO.getPulseRate());
        if (requestDTO.getTemperature() != null) visit.setTemperature(requestDTO.getTemperature());
        if (requestDTO.getHeight() != null) visit.setHeight(requestDTO.getHeight());
        if (requestDTO.getWeight() != null) visit.setWeight(requestDTO.getWeight());
        
        visit.setStatus("CONSULTED");
        visit.setUpdatedAt(LocalDateTime.now());
        Visit savedVisit = visitRepository.save(visit);

        // 2. Save Treatments
        double totalTreatmentCost = 0.0;
        if (requestDTO.getTreatments() != null && !requestDTO.getTreatments().isEmpty()) {
            var savedTreatments = treatmentService.createTreatmentsForVisit(savedVisit.getVisitId(), savedVisit.getPatientId(), requestDTO.getTreatments());
            totalTreatmentCost = savedTreatments.stream().mapToDouble(t -> t.getCost() != null ? t.getCost() : 0.0).sum();
        }

        // 3. Save Prescription
        if (requestDTO.getPrescription() != null) {
            PrescriptionDTO prescriptionDTO = requestDTO.getPrescription();
            prescriptionDTO.setVisitId(savedVisit.getVisitId());
            prescriptionDTO.setPatientId(savedVisit.getPatientId());
            prescriptionService.createPrescription(prescriptionDTO);
        }

        // 4. Create Bill
        if (requestDTO.getTreatments() != null && !requestDTO.getTreatments().isEmpty()) {
            List<Bill.TreatmentDetail> billTreatments = requestDTO.getTreatments().stream().map(t -> {
                Bill.TreatmentDetail detail = new Bill.TreatmentDetail();
                detail.setTreatmentName(t.getTreatmentName());
                detail.setCost(t.getCost() != null ? t.getCost() : 0.0);
                return detail;
            }).toList();

            BillDTO billDTO = new BillDTO();
            billDTO.setVisitId(savedVisit.getVisitId());
            billDTO.setPatientId(savedVisit.getPatientId());
            billDTO.setSubtotal(totalTreatmentCost);
            billDTO.setDiscount(0.0);
            billDTO.setGrandTotal(totalTreatmentCost);
            
            // Map to the inner DTO class
            List<BillDTO.TreatmentDetailDTO> detailDTOs = billTreatments.stream().map(bt -> 
                new BillDTO.TreatmentDetailDTO(bt.getTreatmentName(), bt.getCost())
            ).toList();
            billDTO.setTreatmentDetails(detailDTOs);
            
            billService.createBill(billDTO);
        }

        return visitMapper.toDto(savedVisit);
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
