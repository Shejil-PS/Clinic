package com.clinic.management.mapper;

import com.clinic.management.dto.TreatmentMasterDTO;
import com.clinic.management.entity.TreatmentMaster;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TreatmentMasterMapper {
    TreatmentMasterDTO toDto(TreatmentMaster treatmentMaster);
    TreatmentMaster toEntity(TreatmentMasterDTO dto);
    void updateEntityFromDto(TreatmentMasterDTO dto, @MappingTarget TreatmentMaster entity);
}
