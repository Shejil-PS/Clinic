package com.clinic.management.controller;

import com.clinic.management.dto.BillDTO;
import com.clinic.management.service.BillService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/bills")
@RequiredArgsConstructor
public class BillController {

    private final BillService billService;

    @GetMapping("/{id}")
    public ResponseEntity<BillDTO> getBillById(@PathVariable String id) {
        return ResponseEntity.ok(billService.getBillById(id));
    }

    @GetMapping("/visit/{visitId}")
    public ResponseEntity<BillDTO> getBillByVisitId(@PathVariable String visitId) {
        return ResponseEntity.ok(billService.getBillByVisitId(visitId));
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<BillDTO>> getBillsByPatientId(@PathVariable String patientId) {
        return ResponseEntity.ok(billService.getBillsByPatientId(patientId));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<BillDTO> updatePaymentStatus(
            @PathVariable String id,
            @RequestParam String status) {
        return ResponseEntity.ok(billService.updatePaymentStatus(id, status));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BillDTO> updateBill(@PathVariable String id, @RequestBody BillDTO billDTO) {
        return ResponseEntity.ok(billService.updateBill(id, billDTO));
    }
}
