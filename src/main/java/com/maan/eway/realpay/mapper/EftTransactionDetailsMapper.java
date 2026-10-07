package com.maan.eway.realpay.mapper;

import com.maan.eway.realpay.dto.EftTransactionDetailsDTO;
import com.maan.eway.realpay.model.EftTransactionDetails;
import org.springframework.stereotype.Component;

@Component
public class EftTransactionDetailsMapper {

    public EftTransactionDetailsDTO toDto(EftTransactionDetails entity) {
        if (entity == null) return null;

        return EftTransactionDetailsDTO.builder()
                .id(entity.getId())
                .modeOfPayment(entity.getModeOfPayment())
                .paymentType(entity.getPaymentType())
                .customerCode(entity.getCustomerCode())
                .customerCategory(entity.getCustomerCategory())
                .quoteNo(entity.getQuoteNo())
                .policyNo(entity.getPolicyNo())
                .policyReference(entity.getPolicyReference())
                .amountPayment(entity.getAmountPayment())
                .paymentReference(entity.getPaymentReference())
                .bankCode(entity.getBankCode())
                .bankAccountNumber(entity.getBankAccountNumber())
                .dateOfPayment(entity.getDateOfPayment())
                .chequeNo(entity.getChequeNo())
                .chequeDate(entity.getChequeDate())
                .createdBy(entity.getCreatedBy())
                .createdDate(entity.getCreatedDate())
                .updatedBy(entity.getUpdatedBy())
                .updatedDate(entity.getUpdatedDate())
                .status(entity.getStatus())
                .branchCode(entity.getBranchCode())
                .receiptNumber(entity.getReceiptNumber())
                .currencyCode(entity.getCurrencyCode())
                .exchangeRate(entity.getExchangeRate())
                .approvalStatus(entity.getApprovalStatus())
                .postedToGl(entity.getPostedToGl())
                .uwSysId(entity.getUwSysId())
                .polIdx(entity.getPolIdx())
                .installmentNo(entity.getInstallmentNo())
                .payeeType(entity.getPayeeType())
                .payeeName(entity.getPayeeName())
                .payeeContact(entity.getPayeeContact())
                .payeeEmail(entity.getPayeeEmail())
                .build();
    }

    public EftTransactionDetails toEntity(EftTransactionDetailsDTO dto) {
        if (dto == null) return null;

        return EftTransactionDetails.builder()
                .id(dto.getId())
                .modeOfPayment(dto.getModeOfPayment())
                .paymentType(dto.getPaymentType())
                .customerCode(dto.getCustomerCode())
                .customerCategory(dto.getCustomerCategory())
                .quoteNo(dto.getQuoteNo())
                .policyNo(dto.getPolicyNo())
                .policyReference(dto.getPolicyReference())
                .amountPayment(dto.getAmountPayment())
                .paymentReference(dto.getPaymentReference())
                .bankCode(dto.getBankCode())
                .bankAccountNumber(dto.getBankAccountNumber())
                .dateOfPayment(dto.getDateOfPayment())
                .chequeNo(dto.getChequeNo())
                .chequeDate(dto.getChequeDate())
                .createdBy(dto.getCreatedBy())
                .createdDate(dto.getCreatedDate())
                .updatedBy(dto.getUpdatedBy())
                .updatedDate(dto.getUpdatedDate())
                .status(dto.getStatus())
                .branchCode(dto.getBranchCode())
                .receiptNumber(dto.getReceiptNumber())
                .currencyCode(dto.getCurrencyCode())
                .exchangeRate(dto.getExchangeRate())
                .approvalStatus(dto.getApprovalStatus())
                .postedToGl(dto.getPostedToGl())
                .uwSysId(dto.getUwSysId())
                .polIdx(dto.getPolIdx())
                .installmentNo(dto.getInstallmentNo())
                .payeeType(dto.getPayeeType())
                .payeeName(dto.getPayeeName())
                .payeeContact(dto.getPayeeContact())
                .payeeEmail(dto.getPayeeEmail())
                .build();
    }
}
