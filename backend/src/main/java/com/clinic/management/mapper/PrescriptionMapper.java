package com.clinic.management.mapper;

import com.clinic.management.dto.PrescriptionDTO;
import com.clinic.management.entity.Prescription;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PrescriptionMapper {
    PrescriptionDTO toDto(Prescription prescription);
    Prescription toEntity(PrescriptionDTO dto);
    void updateEntityFromDto(PrescriptionDTO dto, @MappingTarget Prescription entity);
}
