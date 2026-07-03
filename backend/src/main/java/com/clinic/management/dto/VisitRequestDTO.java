package com.clinic.management.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VisitRequestDTO {

    @NotBlank(message = "Patient ID is required")
    private String patientId;

    private LocalDateTime visitDate;
    
    @NotBlank(message = "Doctor name is required")
    private String doctorName;
    
    private String chiefComplaint;
    private String diagnosis;
    
    private String bloodPressure;
    private Integer pulseRate;
    private Double temperature;
    private Double height;
    private Double weight;
    
    private String notes;
    private LocalDate followUpDate;

    @Valid
    private List<TreatmentRequestDTO> treatments;
}
