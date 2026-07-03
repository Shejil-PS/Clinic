package com.clinic.management.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MedicineDTO {

    private String id;
    
    private String medicineId;

    @NotBlank(message = "Medicine name is required")
    private String medicineName;

    @Builder.Default
    private Boolean available = true;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
