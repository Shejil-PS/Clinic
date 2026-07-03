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
public class PatientRegistrationDTO {

    @NotBlank(message = "Phone number is required")
    private String phone;
    
    @NotBlank(message = "Full name is required")
    private String fullName;
    
    private Integer age;
    
    private String gender;
    
    private String address;
    
    private String doctorName;
    
    private String chiefComplaint;
}
