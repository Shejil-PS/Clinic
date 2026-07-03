package com.clinic.management.controller;

import com.clinic.management.dto.TreatmentMasterDTO;
import com.clinic.management.service.TreatmentMasterService;
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
@RequestMapping("/api/v1/treatments/master")
@RequiredArgsConstructor
public class TreatmentMasterController {

    private final TreatmentMasterService treatmentMasterService;

    @PostMapping
    public ResponseEntity<TreatmentMasterDTO> createTreatmentMaster(@Valid @RequestBody TreatmentMasterDTO dto) {
        return new ResponseEntity<>(treatmentMasterService.createTreatmentMaster(dto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TreatmentMasterDTO> updateTreatmentMaster(
            @PathVariable String id,
            @Valid @RequestBody TreatmentMasterDTO dto
    ) {
        return ResponseEntity.ok(treatmentMasterService.updateTreatmentMaster(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTreatmentMaster(@PathVariable String id) {
        treatmentMasterService.deleteTreatmentMaster(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<TreatmentMasterDTO> getTreatmentMasterById(@PathVariable String id) {
        return ResponseEntity.ok(treatmentMasterService.getTreatmentMasterById(id));
    }

    @GetMapping
    public ResponseEntity<Page<TreatmentMasterDTO>> searchTreatments(
            @RequestParam(required = false) String term,
            @RequestParam(required = false) Boolean activeOnly,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "treatmentName") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? 
                Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return ResponseEntity.ok(treatmentMasterService.searchTreatments(term, activeOnly, pageable));
    }
}
