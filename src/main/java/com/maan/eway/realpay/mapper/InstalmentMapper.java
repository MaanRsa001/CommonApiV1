package com.maan.eway.realpay.mapper;

import com.maan.eway.realpay.dto.InstalmentDTO;
import com.maan.eway.realpay.model.ClientAccountDetails;
import com.maan.eway.realpay.model.Contract;
import com.maan.eway.realpay.model.Instalment;
import org.springframework.stereotype.Component;

@Component
public class InstalmentMapper {

    public static InstalmentDTO toDto(Instalment entity) {
        if (entity == null) {
            return null;
        }

        InstalmentDTO dto = new InstalmentDTO();
        dto.setId(entity.getId());
        dto.setClientAccountId(entity.getClientAccount() != null ? entity.getClientAccount().getId() : null);
        dto.setContractId(entity.getContract() != null ? entity.getContract().getId() : null);
        dto.setQuoteNo(entity.getQuoteNo());
        dto.setProductId(entity.getProductId());
        dto.setNoOfInstalment(entity.getNoOfInstalment());
        dto.setInstalmentActionDate(entity.getInstalmentActionDate()); 
        dto.setClientNumber(entity.getClientNumber());
        dto.setContractSequence(entity.getContractSequence());
        dto.setContractNumber(entity.getContractNumber());
        dto.setTrackingCode(entity.getTrackingCode());
        dto.setInstalmentAmount(entity.getInstalmentAmount());
        dto.setDebitSequenceType(entity.getDebitSequenceType());
        dto.setCtcAmount(entity.getCtcAmount());
        dto.setInstalmentReferenceNumber(entity.getInstalmentReferenceNumber());
        dto.setInstalmentStatus(entity.getInstalmentStatus());
        dto.setResponseCode(entity.getResponseCode());
        dto.setLastUpdateDate(entity.getLastUpdateDate());
        dto.setStatus(entity.getStatus());
        dto.setMessage(entity.getMessage());
        dto.setSyncStatus(entity.getSyncStatus());
        dto.setCreatedDate(entity.getCreatedDate());
        dto.setUpdatedDate(entity.getUpdatedDate());
        dto.setRequestBody(entity.getRequestBody());
        dto.setResponseBody(entity.getResponseBody());

        return dto;
    }

    public static Instalment toEntity(InstalmentDTO dto, ClientAccountDetails clientAccount, Contract contract) {
        if (dto == null) {
            return null;
        }

        Instalment entity = new Instalment();
        entity.setId(dto.getId());
        entity.setClientAccount(clientAccount);
        entity.setContract(contract);
        entity.setQuoteNo(dto.getQuoteNo());
        entity.setProductId(dto.getProductId());
        entity.setNoOfInstalment(dto.getNoOfInstalment());
        entity.setInstalmentActionDate(dto.getInstalmentActionDate()); 
        entity.setClientNumber(dto.getClientNumber());
        entity.setContractSequence(dto.getContractSequence());
        entity.setContractNumber(dto.getContractNumber());
        entity.setTrackingCode(dto.getTrackingCode());
        entity.setInstalmentAmount(dto.getInstalmentAmount());
        entity.setDebitSequenceType(dto.getDebitSequenceType());
        entity.setCtcAmount(dto.getCtcAmount());
        entity.setInstalmentReferenceNumber(dto.getInstalmentReferenceNumber());
        entity.setInstalmentStatus(dto.getInstalmentStatus());
        entity.setResponseCode(dto.getResponseCode());
        entity.setLastUpdateDate(dto.getLastUpdateDate());
        entity.setStatus(dto.getStatus());
        entity.setMessage(dto.getMessage());
        entity.setSyncStatus(dto.getSyncStatus());
        entity.setCreatedDate(dto.getCreatedDate());
        entity.setUpdatedDate(dto.getUpdatedDate());
        entity.setRequestBody(dto.getRequestBody());
        entity.setResponseBody(dto.getResponseBody());

        return entity;
    }
}
