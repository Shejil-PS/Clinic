package com.clinic.management.mapper;

import com.clinic.management.dto.BillDTO;
import com.clinic.management.entity.Bill;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BillMapper {
    BillDTO toDto(Bill bill);
    Bill toEntity(BillDTO dto);
}
