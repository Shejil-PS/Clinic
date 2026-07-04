package com.clinic.management.controller;

import com.clinic.management.dto.PatientDTO;
import com.clinic.management.dto.PatientProfileDTO;
import com.clinic.management.service.PatientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/patients")
@RequiredArgsConstructor
public class PatientController {

    private final PatientService patientService;

    @PostMapping
    public ResponseEntity<PatientDTO> createPatient(@Valid @RequestBody PatientDTO patientDTO) {
        return new ResponseEntity<>(patientService.createPatient(patientDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PatientDTO> updatePatient(
            @PathVariable String id,
            @Valid @RequestBody PatientDTO patientDTO
    ) {
        return ResponseEntity.ok(patientService.updatePatient(id, patientDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePatient(@PathVariable String id) {
        patientService.deletePatient(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<PatientDTO> getPatientById(@PathVariable String id) {
        return ResponseEntity.ok(patientService.getPatientById(id));
    }

    @GetMapping("/patient-id/{patientId}")
    public ResponseEntity<PatientDTO> getPatientByPatientId(@PathVariable String patientId) {
        return ResponseEntity.ok(patientService.getPatientByPatientId(patientId));
    }
    
    @GetMapping("/phone/{phone}")
    public ResponseEntity<PatientDTO> getPatientByPhone(@PathVariable String phone) {
        return ResponseEntity.ok(patientService.getPatientByPhone(phone));
    }
    
    @GetMapping("/profile/{patientId}")
    public ResponseEntity<PatientProfileDTO> getPatientProfile(@PathVariable String patientId) {
        return ResponseEntity.ok(patientService.getPatientProfile(patientId));
    }

    @GetMapping
    public ResponseEntity<Page<PatientDTO>> getAllPatients(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir
    ) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? 
                Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return ResponseEntity.ok(patientService.getAllPatients(pageable));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<PatientDTO>> searchPatients(
            @RequestParam String term,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "fullName") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? 
                Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return ResponseEntity.ok(patientService.searchPatients(term, pageable));
    }
}
