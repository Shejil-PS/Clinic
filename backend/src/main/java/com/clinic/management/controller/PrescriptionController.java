package com.clinic.management.controller;

import com.clinic.management.dto.PrescriptionDTO;
import com.clinic.management.service.PrescriptionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/prescriptions")
@RequiredArgsConstructor
public class PrescriptionController {

    private final PrescriptionService prescriptionService;

    @PostMapping
    public ResponseEntity<PrescriptionDTO> createPrescription(@Valid @RequestBody PrescriptionDTO prescriptionDTO) {
        return new ResponseEntity<>(prescriptionService.createPrescription(prescriptionDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PrescriptionDTO> updatePrescription(
            @PathVariable String id,
            @Valid @RequestBody PrescriptionDTO prescriptionDTO
    ) {
        return ResponseEntity.ok(prescriptionService.updatePrescription(id, prescriptionDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePrescription(@PathVariable String id) {
        prescriptionService.deletePrescription(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<PrescriptionDTO> getPrescriptionById(@PathVariable String id) {
        return ResponseEntity.ok(prescriptionService.getPrescriptionById(id));
    }
    
    @GetMapping("/visit/{visitId}")
    public ResponseEntity<PrescriptionDTO> getPrescriptionByVisitId(@PathVariable String visitId) {
        return ResponseEntity.ok(prescriptionService.getPrescriptionByVisitId(visitId));
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<Page<PrescriptionDTO>> getPrescriptionsByPatientId(
            @PathVariable String patientId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "prescriptionDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir
    ) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? 
                Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return ResponseEntity.ok(prescriptionService.getPrescriptionsByPatientId(patientId, pageable));
    }

    @GetMapping("/{id}/print")
    public ResponseEntity<byte[]> printPrescription(@PathVariable String id) {
        byte[] pdfBytes = prescriptionService.generatePrescriptionPdf(id);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("filename", "prescription-" + id + ".pdf");
        
        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }
}
