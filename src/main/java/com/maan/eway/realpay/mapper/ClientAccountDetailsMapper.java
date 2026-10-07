package com.maan.eway.realpay.mapper;

import com.maan.eway.realpay.dto.ClientAccountDetailsDTO;
import com.maan.eway.realpay.model.ClientAccountDetails;

public class ClientAccountDetailsMapper {
    public static ClientAccountDetailsDTO toDto(ClientAccountDetails entity) {
        if (entity == null) {
            return null;
        }

        ClientAccountDetailsDTO dto = new ClientAccountDetailsDTO();
        dto.setId(entity.getId());
        dto.setPaymentId(entity.getPaymentId());
        dto.setQuoteNo(entity.getQuoteNo());
        dto.setMerchantReference(entity.getMerchantReference());
        dto.setPaymentType(entity.getPaymentType());
        dto.setPaymentTypeDesc(entity.getPaymentTypeDesc());
        dto.setCompanyId(entity.getCompanyId());
        dto.setClientNumber(entity.getClientNumber());
        dto.setClientName(entity.getClientName());
        dto.setIdType(entity.getIdType());
        dto.setIdNumber(entity.getIdNumber());
        dto.setCellphoneNumber(entity.getCellphoneNumber());
        dto.setEmail(entity.getEmail());
        dto.setBankCode(entity.getBankCode());
        dto.setBranchCode(entity.getBranchCode());
        dto.setAccountType(entity.getAccountType());
        dto.setAccountNumber(entity.getAccountNumber());
        dto.setAccountHolderName(entity.getAccountHolderName());
        dto.setEmployeeGroupCode(entity.getEmployeeGroupCode());
        dto.setPopInd(entity.getPopInd());
        dto.setPoBankCode(entity.getPoBankCode());
        dto.setPoBranchCode(entity.getPoBranchCode());
        dto.setPoAccountHolderName(entity.getPoAccountHolderName());
        dto.setPoAccountNumber(entity.getPoAccountNumber());
        dto.setPoAccountType(entity.getPoAccountType());
        dto.setCardNumber(entity.getCardNumber());
        dto.setCardExpiry(entity.getCardExpiry());
        dto.setPaymentPreference(entity.getPaymentPreference());
        dto.setStatus(entity.getStatus());
        dto.setMessage(entity.getMessage());
        dto.setClientReference(entity.getClientReference());
        dto.setDebitSequenceType(entity.getDebitSequenceType());
        dto.setFrequencyCode(entity.getFrequencyCode());
        dto.setTrackingCode(entity.getTrackingCode());
        dto.setCreatedDate(entity.getCreatedDate());
        dto.setUpdatedDate(entity.getUpdatedDate());
        dto.setRequestBody(entity.getRequestBody());
        dto.setResponseBody(entity.getResponseBody());
        dto.setBeneficiaryUser(entity.getBeneficiaryUser());

        return dto;
    }

    public static ClientAccountDetails toEntity(ClientAccountDetailsDTO dto) {
        if (dto == null) {
            return null;
        }

        ClientAccountDetails entity = new ClientAccountDetails();
        entity.setId(dto.getId());
        entity.setPaymentId(dto.getPaymentId());
        entity.setQuoteNo(dto.getQuoteNo());
        entity.setMerchantReference(dto.getMerchantReference());
        entity.setPaymentType(dto.getPaymentType());
        entity.setPaymentTypeDesc(dto.getPaymentTypeDesc());
        entity.setCompanyId(dto.getCompanyId());
        entity.setClientNumber(dto.getClientNumber());
        entity.setClientName(dto.getClientName());
        entity.setIdType(dto.getIdType());
        entity.setIdNumber(dto.getIdNumber());
        entity.setCellphoneNumber(dto.getCellphoneNumber());
        entity.setEmail(dto.getEmail());
        entity.setBankCode(dto.getBankCode());
        entity.setBranchCode(dto.getBranchCode());
        entity.setAccountType(dto.getAccountType());
        entity.setAccountNumber(dto.getAccountNumber());
        entity.setAccountHolderName(dto.getAccountHolderName());
        entity.setEmployeeGroupCode(dto.getEmployeeGroupCode());
        entity.setPopInd(dto.getPopInd());
        entity.setPoBankCode(dto.getPoBankCode());
        entity.setPoBranchCode(dto.getPoBranchCode());
        entity.setPoAccountHolderName(dto.getPoAccountHolderName());
        entity.setPoAccountNumber(dto.getPoAccountNumber());
        entity.setPoAccountType(dto.getPoAccountType());
        entity.setCardNumber(dto.getCardNumber());
        entity.setCardExpiry(dto.getCardExpiry());
        entity.setPaymentPreference(dto.getPaymentPreference());
        entity.setStatus(dto.getStatus());
        entity.setMessage(dto.getMessage());
        entity.setClientReference(dto.getClientReference());
        entity.setDebitSequenceType(dto.getDebitSequenceType());
        entity.setFrequencyCode(dto.getFrequencyCode());
        entity.setTrackingCode(dto.getTrackingCode());
        entity.setCreatedDate(dto.getCreatedDate());
        entity.setUpdatedDate(dto.getUpdatedDate());
        entity.setRequestBody(dto.getRequestBody());
        entity.setResponseBody(dto.getResponseBody());
        entity.setBeneficiaryUser(dto.getBeneficiaryUser());

        return entity;
    }
}
