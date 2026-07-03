package com.clinic.management.mapper;

import com.clinic.management.dto.MedicineDTO;
import com.clinic.management.entity.Medicine;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MedicineMapper {
    MedicineDTO toDto(Medicine medicine);
    Medicine toEntity(MedicineDTO dto);
    void updateEntityFromDto(MedicineDTO dto, @MappingTarget Medicine entity);
}
