package com.clinic.management.controller;

import com.clinic.management.dto.TreatmentRequestDTO;
import com.clinic.management.dto.TreatmentResponseDTO;
import com.clinic.management.service.TreatmentService;
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
@RequestMapping("/api/v1/treatments")
@RequiredArgsConstructor
public class TreatmentController {

    private final TreatmentService treatmentService;

    @PostMapping("/visit/{visitId}/patient/{patientId}")
    public ResponseEntity<TreatmentResponseDTO> createTreatment(
            @PathVariable String visitId,
            @PathVariable String patientId,
            @Valid @RequestBody TreatmentRequestDTO requestDTO) {
        return new ResponseEntity<>(treatmentService.createTreatment(visitId, patientId, requestDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TreatmentResponseDTO> updateTreatment(
            @PathVariable String id,
            @Valid @RequestBody TreatmentRequestDTO requestDTO) {
        return ResponseEntity.ok(treatmentService.updateTreatment(id, requestDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTreatment(@PathVariable String id) {
        treatmentService.deleteTreatment(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<TreatmentResponseDTO> getTreatmentById(@PathVariable String id) {
        return ResponseEntity.ok(treatmentService.getTreatmentById(id));
    }

    @GetMapping("/visit/{visitId}")
    public ResponseEntity<Page<TreatmentResponseDTO>> getTreatmentsByVisitId(
            @PathVariable String visitId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? 
                Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return ResponseEntity.ok(treatmentService.getTreatmentsByVisitId(visitId, pageable));
    }
    
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<Page<TreatmentResponseDTO>> getTreatmentsByPatientId(
            @PathVariable String patientId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? 
                Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return ResponseEntity.ok(treatmentService.getTreatmentsByPatientId(patientId, pageable));
    }
    
    @GetMapping("/patient/{patientId}/search")
    public ResponseEntity<Page<TreatmentResponseDTO>> searchTreatmentsByPatient(
            @PathVariable String patientId,
            @RequestParam String term,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? 
                Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return ResponseEntity.ok(treatmentService.searchTreatmentsByPatient(patientId, term, pageable));
    }
}
