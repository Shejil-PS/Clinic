package com.clinic.management.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TreatmentMasterDTO {

    private String id;
    private String treatmentId;

    @NotBlank(message = "Treatment name is required")
    private String treatmentName;
    
    private String description;
    
    @NotNull(message = "Cost is required")
    private Double cost;

    @Builder.Default
    private Boolean active = true;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
