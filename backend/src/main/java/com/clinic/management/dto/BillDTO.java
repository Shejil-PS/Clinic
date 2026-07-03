package com.clinic.management.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BillDTO {

    private String id;
    private String billNumber;
    private String visitId;
    private String patientId;

    private List<TreatmentDetailDTO> treatmentDetails;
    
    private Double subtotal;
    private Double discount;
    private Double grandTotal;
    
    private String paymentStatus;
    private LocalDateTime createdAt;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TreatmentDetailDTO {
        private String treatmentName;
        private Double cost;
    }
}
