package com.clinic.management.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "bills")
public class Bill {

    @Id
    private String id;

    @Indexed(unique = true)
    private String billNumber;

    @Indexed
    private String visitId;

    @Indexed
    private String patientId;

    private List<TreatmentDetail> treatmentDetails;
    
    private Double subtotal;
    private Double discount;
    private Double grandTotal;
    
    private String paymentStatus; // PENDING, PAID

    @CreatedDate
    private LocalDateTime createdAt;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TreatmentDetail {
        private String treatmentName;
        private Double cost;
    }
}
