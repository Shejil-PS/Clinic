package com.clinic.management.mapper;

import com.clinic.management.dto.TreatmentRequestDTO;
import com.clinic.management.dto.TreatmentResponseDTO;
import com.clinic.management.entity.Treatment;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TreatmentMapper {
    TreatmentResponseDTO toResponseDto(Treatment treatment);
    Treatment toEntity(TreatmentRequestDTO dto);
    void updateEntityFromDto(TreatmentRequestDTO dto, @MappingTarget Treatment entity);
}
