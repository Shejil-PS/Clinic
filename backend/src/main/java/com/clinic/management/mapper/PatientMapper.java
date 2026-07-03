package com.clinic.management.mapper;

import com.clinic.management.dto.PatientDTO;
import com.clinic.management.entity.Patient;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PatientMapper {
    PatientDTO toDto(Patient patient);
    Patient toEntity(PatientDTO patientDTO);
    void updateEntityFromDto(PatientDTO dto, @MappingTarget Patient entity);
}
