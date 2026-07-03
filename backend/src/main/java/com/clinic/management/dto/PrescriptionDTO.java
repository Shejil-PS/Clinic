package com.clinic.management.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PrescriptionDTO {

    private String id;
    private String prescriptionId;

    @NotBlank(message = "Patient ID is required")
    private String patientId;

    @NotBlank(message = "Visit ID is required")
    private String visitId;

    private LocalDateTime prescriptionDate;
    
    private String remarks;

    @Valid
    @Builder.Default
    private List<PrescribedMedicineDTO> medicineList = new ArrayList<>();

    private LocalDateTime createdAt;
}
