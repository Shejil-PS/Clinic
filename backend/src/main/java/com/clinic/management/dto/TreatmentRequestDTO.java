package com.clinic.management.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TreatmentRequestDTO {

    @NotBlank(message = "Treatment name is required")
    private String treatmentName;
    
    private String toothNumber;
    
    @NotBlank(message = "Status is required")
    private String status;
    
    private String notes;
}
