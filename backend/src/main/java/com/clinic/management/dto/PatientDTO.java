package com.clinic.management.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatientDTO {
    
    private String id;
    private String patientId;

    @NotBlank(message = "Full name is required")
    private String fullName;
    
    @NotBlank(message = "Gender is required")
    private String gender;
    
    private LocalDate dateOfBirth;
    private Integer age;
    private String bloodGroup;
    private String guardianName;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^\\d{10}$", message = "Phone number must be 10 digits")
    private String phone;
    
    private String alternatePhone;
    private String email;
    private String address;
    private String occupation;
    private String medicalHistory;
    private String allergies;
    private String remarks;
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
