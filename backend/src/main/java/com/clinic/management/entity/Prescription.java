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
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "prescriptions")
public class Prescription {

    @Id
    private String id;

    @Indexed(unique = true)
    private String prescriptionId;

    @Indexed
    private String patientId;

    @Indexed
    private String visitId;

    private LocalDateTime prescriptionDate;
    
    private String remarks;

    @Builder.Default
    private List<PrescribedMedicine> medicineList = new ArrayList<>();

    @CreatedDate
    private LocalDateTime createdAt;
}
