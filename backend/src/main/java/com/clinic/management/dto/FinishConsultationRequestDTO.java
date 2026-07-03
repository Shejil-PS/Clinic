package com.clinic.management.dto;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FinishConsultationRequestDTO {

    private String diagnosis;
    private String notes;
    
    private String bloodPressure;
    private Integer pulseRate;
    private Double temperature;
    private Double height;
    private Double weight;

    @Valid
    private List<TreatmentRequestDTO> treatments;
    
    @Valid
    private PrescriptionDTO prescription;
}
