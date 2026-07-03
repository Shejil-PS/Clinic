package com.clinic.management.mapper;

import com.clinic.management.dto.VisitDTO;
import com.clinic.management.entity.Visit;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface VisitMapper {
    VisitDTO toDto(Visit visit);
    Visit toEntity(VisitDTO dto);
    void updateEntityFromDto(VisitDTO dto, @MappingTarget Visit entity);
}
