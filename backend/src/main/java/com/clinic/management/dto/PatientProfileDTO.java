package com.clinic.management.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatientProfileDTO {
    private PatientDTO patientInfo;
    private List<VisitDTO> visitHistory;
    private List<TreatmentResponseDTO> treatmentHistory;
    private List<PrescriptionDTO> prescriptionHistory;
}
