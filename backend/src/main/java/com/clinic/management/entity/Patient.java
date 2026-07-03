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
@Document(collection = "patients")
public class Patient {

    @Id
    private String id;

    @Indexed(unique = true)
    private String patientId;

    private String fullName;
    private String gender;
    private LocalDate dateOfBirth;
    private Integer age;
    private String bloodGroup;
    private String guardianName;

    @Indexed
    private String phone;
    
    private String alternatePhone;
    private String email;
    private String address;
    private String occupation;
    private String medicalHistory;
    private String allergies;
    private String remarks;

    @Builder.Default
    private Boolean active = true;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}
