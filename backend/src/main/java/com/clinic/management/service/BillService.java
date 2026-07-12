package com.clinic.management.service;

import com.clinic.management.dto.BillDTO;
import com.clinic.management.entity.Bill;
import com.clinic.management.exception.ResourceNotFoundException;
import com.clinic.management.mapper.BillMapper;
import com.clinic.management.repository.BillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BillService {

    private final BillRepository billRepository;
    private final BillMapper billMapper;

    public BillDTO createBill(BillDTO dto) {
        Bill bill = billMapper.toEntity(dto);
        bill.setBillNumber(generateBillNumber());
        if (bill.getPaymentStatus() == null) {
            bill.setPaymentStatus("PENDING");
        }
        bill.setCreatedAt(LocalDateTime.now());
        
        Bill saved = billRepository.save(bill);
        return billMapper.toDto(saved);
    }

    public BillDTO getBillById(String id) {
        Bill bill = billRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bill not found with id: " + id));
        return billMapper.toDto(bill);
    }
    
    public BillDTO getBillByVisitId(String visitId) {
        Bill bill = billRepository.findByVisitId(visitId)
                .orElseThrow(() -> new ResourceNotFoundException("Bill not found for visit id: " + visitId));
        return billMapper.toDto(bill);
    }
    
    public List<BillDTO> getBillsByPatientId(String patientId) {
        return billRepository.findByPatientId(patientId).stream()
                .map(billMapper::toDto).toList();
    }
    
    public BillDTO updatePaymentStatus(String id, String status) {
        Bill bill = billRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bill not found with id: " + id));
        bill.setPaymentStatus(status);
        return billMapper.toDto(billRepository.save(bill));
    }
    
    public BillDTO updateBill(String id, BillDTO dto) {
        Bill bill = billRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bill not found with id: " + id));
        
        bill.setSubtotal(dto.getSubtotal());
        bill.setDiscount(dto.getDiscount());
        bill.setGrandTotal(dto.getGrandTotal());
        
        if (dto.getTreatmentDetails() != null) {
            bill.setTreatmentDetails(dto.getTreatmentDetails().stream()
                    .map(t -> new Bill.TreatmentDetail(t.getTreatmentName(), t.getCost()))
                    .toList());
        }
        
        return billMapper.toDto(billRepository.save(bill));
    }

    private String generateBillNumber() {
        return billRepository.findTopByOrderByBillNumberDesc()
                .map(bill -> {
                    String lastId = bill.getBillNumber(); // e.g. BILL000001
                    int num = Integer.parseInt(lastId.substring(4));
                    return String.format("BILL%06d", num + 1);
                })
                .orElse("BILL000001");
    }
}
