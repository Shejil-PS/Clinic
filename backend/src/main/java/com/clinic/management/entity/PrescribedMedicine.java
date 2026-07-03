package com.clinic.management.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PrescribedMedicine {
    private String medicineId;
    private String medicineName;
    private String dosage;
    private String duration;
    private Integer quantity;
    private String instruction;
    
    @Builder.Default
    private Boolean dispensedFromClinic = false;
}
