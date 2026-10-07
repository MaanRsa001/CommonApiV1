package com.maan.eway.realpay.mapper;

import org.springframework.stereotype.Component;

import com.maan.eway.realpay.dto.AccountDTO;
import com.maan.eway.realpay.model.Account;

@Component
public class AccountMapper {

    public AccountDTO toDTO(Account entity) {
        AccountDTO dto = new AccountDTO();
        dto.setId(entity.getId());
        dto.setActionDate(entity.getActionDate());
        dto.setBankCode(entity.getBankCode());
        dto.setBranchCode(entity.getBranchCode());
        dto.setAccountType(entity.getAccountType());
        dto.setAccountNumber(entity.getAccountNumber());
        dto.setIdNumber(entity.getIdNumber());
        dto.setInitials(entity.getInitials());
        dto.setClientName(entity.getClientName());
        dto.setEmail(entity.getEmail());
        return dto;
    }

    public Account toEntity(AccountDTO dto) {
        Account entity = new Account();
        entity.setId(dto.getId());
        entity.setActionDate(dto.getActionDate());
        entity.setBankCode(dto.getBankCode());
        entity.setBranchCode(dto.getBranchCode());
        entity.setAccountType(dto.getAccountType());
        entity.setAccountNumber(dto.getAccountNumber());
        entity.setIdNumber(dto.getIdNumber());
        entity.setInitials(dto.getInitials());
        entity.setClientName(dto.getClientName());
        entity.setEmail(dto.getEmail());
        return entity;
    }
}
