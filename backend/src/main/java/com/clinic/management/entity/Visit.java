package com.clinic.management.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "visits")
public class Visit {

    @Id
    private String id;

    @Indexed(unique = true)
    private String visitId;

    @Indexed
    private String patientId;

    private LocalDateTime visitDate;
    
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

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}
