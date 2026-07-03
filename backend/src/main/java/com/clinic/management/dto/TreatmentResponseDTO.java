package com.clinic.management.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TreatmentResponseDTO {

    private String id;
    private String treatmentRecordId;
    private String treatmentId;
    private String visitId;
    private String patientId;
    private String treatmentName;
    private String toothNumber;
    private String status;
    private Double cost;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
