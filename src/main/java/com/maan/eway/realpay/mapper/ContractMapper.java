package com.maan.eway.realpay.mapper;

import com.maan.eway.realpay.dto.ContractDTO;
import com.maan.eway.realpay.model.ClientAccountDetails;
import com.maan.eway.realpay.model.Contract;
import org.springframework.stereotype.Component;

@Component
public class ContractMapper {

    public static ContractDTO toDto(Contract entity) {
        if (entity == null) {
            return null;
        }

        ContractDTO dto = new ContractDTO();
        dto.setId(entity.getId());
        dto.setClientAccountId(entity.getClientAccount() != null ? entity.getClientAccount().getId() : null);
        dto.setClientNumber(entity.getClientNumber());
        dto.setContractNumber(entity.getContractNumber());
        
        dto.setContractType(entity.getContractType());
        dto.setOriginalContractNumber(entity.getOriginalContractNumber());
        dto.setMandateType(entity.getMandateType());
        
        dto.setContractSequence(entity.getContractSequence());
        dto.setQuoteNo(entity.getQuoteNo());
        dto.setFrequencyCode(entity.getFrequencyCode());
        dto.setCollectionDay(entity.getCollectionDay());
        dto.setTrackingCode(entity.getTrackingCode());
        dto.setFirstCollectionDate(entity.getFirstCollectionDate());
        dto.setFirstCollectionAmount(entity.getFirstCollectionAmount());
        dto.setInstalmentStartDate(entity.getInstalmentStartDate());
        dto.setInstalmentAmount(entity.getInstalmentAmount());
        dto.setDebitSequenceType(entity.getDebitSequenceType());
        dto.setNumberOfInstalments(entity.getNumberOfInstalments());
        dto.setCtcPercentage(entity.getCtcPercentage());
        dto.setStatus(entity.getStatus());
        dto.setMessage(entity.getMessage());
        dto.setCreatedDate(entity.getCreatedDate());
        dto.setUpdatedDate(entity.getUpdatedDate());
        dto.setRequestBody(entity.getRequestBody());
        dto.setResponseBody(entity.getResponseBody());

        return dto;
    }

    public static Contract toEntity(ContractDTO dto, ClientAccountDetails clientAccount) {
        if (dto == null) {
            return null;
        }

        Contract entity = new Contract();
        entity.setId(dto.getId());
        entity.setClientAccount(clientAccount);
        entity.setClientNumber(dto.getClientNumber());
        entity.setContractNumber(dto.getContractNumber());
        
        entity.setContractType(dto.getContractType());
        entity.setOriginalContractNumber(dto.getOriginalContractNumber());
        entity.setMandateType(dto.getMandateType());
        
        entity.setContractSequence(dto.getContractSequence());
        entity.setQuoteNo(dto.getQuoteNo());
        entity.setFrequencyCode(dto.getFrequencyCode());
        entity.setCollectionDay(dto.getCollectionDay());
        entity.setTrackingCode(dto.getTrackingCode());
        entity.setFirstCollectionDate(dto.getFirstCollectionDate());
        entity.setFirstCollectionAmount(dto.getFirstCollectionAmount());
        entity.setInstalmentStartDate(dto.getInstalmentStartDate());
        entity.setInstalmentAmount(dto.getInstalmentAmount());
        entity.setDebitSequenceType(dto.getDebitSequenceType());
        entity.setNumberOfInstalments(dto.getNumberOfInstalments());
        entity.setCtcPercentage(dto.getCtcPercentage());
        entity.setStatus(dto.getStatus());
        entity.setMessage(dto.getMessage());
        entity.setCreatedDate(dto.getCreatedDate());
        entity.setUpdatedDate(dto.getUpdatedDate());
        entity.setRequestBody(dto.getRequestBody());
        entity.setResponseBody(dto.getResponseBody());

        return entity;
    }
}
