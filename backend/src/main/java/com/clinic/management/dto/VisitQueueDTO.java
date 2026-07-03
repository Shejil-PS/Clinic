package com.clinic.management.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VisitQueueDTO {

    private String visitId;
    private String patientId;
    private String patientName;
    private String phone;
    private LocalDateTime registrationTime;
    private String visitStatus;
    
}
