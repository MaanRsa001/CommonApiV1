package com.maan.eway.realpay.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.maan.eway.realpay.dto.ClientAccountDetailsDTO;
import com.maan.eway.realpay.dto.ContractDTO;
import com.maan.eway.realpay.dto.InstalmentDTO;
import com.maan.eway.realpay.dto.UnifiedClientCreationResponseDTO;
import com.maan.eway.realpay.model.ClientAccountDetails;
import com.maan.eway.realpay.model.Contract;
import com.maan.eway.realpay.model.Instalment;
import com.maan.eway.realpay.repository.ClientAccountDetailsRepository;
import com.maan.eway.realpay.repository.ContractRepository;
import com.maan.eway.realpay.repository.InstalmentRepository;

@Service
@Transactional
public class RealPayQueryService {
    
    @Autowired
    private ClientAccountDetailsRepository clientAccountRepo;
    
    @Autowired
    private ContractRepository contractRepo;
    
    @Autowired
    private InstalmentRepository instalmentRepo;
    
    public UnifiedClientCreationResponseDTO getClientDetailsByQuoteNo(String quoteNo) {
        UnifiedClientCreationResponseDTO response = new UnifiedClientCreationResponseDTO();
        
        // Get client account details
        Optional<ClientAccountDetails> clientAccountOpt = clientAccountRepo.findByQuoteNo(quoteNo);
        if (!clientAccountOpt.isPresent()) {
            return createNotFoundResponse("No client found for quote: " + quoteNo);
        }
        
        ClientAccountDetails clientAccount = clientAccountOpt.get();
        
        // Get contract details
        List<Contract> contracts = contractRepo.findByQuoteNo(quoteNo);
        Contract contract = contracts.isEmpty() ? null : contracts.get(0);
        
        // Get installment details
        List<Instalment> installments = instalmentRepo.findByQuoteNo(quoteNo);
        
        // Build response
        return buildResponseDTO(clientAccount, contract, installments);
    }
    
    private UnifiedClientCreationResponseDTO buildResponseDTO(ClientAccountDetails clientAccount, 
                                                            Contract contract, 
                                                            List<Instalment> installments) {
        UnifiedClientCreationResponseDTO response = new UnifiedClientCreationResponseDTO();
        
        // Build status
        UnifiedClientCreationResponseDTO.ClientCreationStatus status = 
            new UnifiedClientCreationResponseDTO.ClientCreationStatus();
        
        status.setClientStatus("EXISTS");
        status.setContractStatus(contract != null ? "EXISTS" : "NOT_FOUND");
        status.setInstallmentsStatus(!installments.isEmpty() ? "EXISTS" : "NOT_FOUND");
        
        status.setClientReference(clientAccount.getClientReference());
        if (contract != null) {
            status.setContractReference(contract.getContractSequence() != null ? 
                contract.getContractSequence().toString() : contract.getContractNumber());
        }
        if (!installments.isEmpty()) {
            String installmentRefs = installments.stream()
                .map(Instalment::getInstalmentReferenceNumber)
                .filter(ref -> ref != null && !ref.trim().isEmpty())
                .collect(Collectors.joining(","));
            status.setInstallmentReference(installmentRefs);
        }
        
        response.setStatus(status);
        
        // Build client details DTO
        ClientAccountDetailsDTO clientDetails = mapToClientAccountDetailsDTO(clientAccount);
        response.setClientDetails(clientDetails);
        
        // Build contract details DTO
        if (contract != null) {
            ContractDTO contractDetails = mapToContractDTO(contract);
            response.setContractDetails(contractDetails);
        }
        
        // Build installment details DTO
        if (!installments.isEmpty()) {
            List<InstalmentDTO> installmentDetails = installments.stream()
                .map(this::mapToInstalmentDTO)
                .collect(Collectors.toList());
            response.setInstallmentDetails(installmentDetails);
        }
        
        response.setMessage(buildSuccessMessage(clientAccount, contract, installments));
        response.setTimestamp(LocalDateTime.now());
        
        return response;
    }
    
    private ClientAccountDetailsDTO mapToClientAccountDetailsDTO(ClientAccountDetails clientAccount) {
        ClientAccountDetailsDTO dto = new ClientAccountDetailsDTO();
        
        // Map fields from ClientAccountDetails entity to DTO
        dto.setId(clientAccount.getId());
        dto.setPaymentId(clientAccount.getPaymentId());
        dto.setQuoteNo(clientAccount.getQuoteNo());
        dto.setMerchantReference(clientAccount.getMerchantReference());
        dto.setPaymentType(clientAccount.getPaymentType());
        dto.setPaymentTypeDesc(clientAccount.getPaymentTypeDesc());
        dto.setCompanyId(clientAccount.getCompanyId());
        dto.setClientNumber(clientAccount.getClientNumber());
        dto.setClientName(clientAccount.getClientName());
        dto.setIdType(clientAccount.getIdType());
        dto.setIdNumber(clientAccount.getIdNumber());
        dto.setCellphoneNumber(clientAccount.getCellphoneNumber());
        dto.setEmail(clientAccount.getEmail());
        dto.setBankCode(clientAccount.getBankCode());
        dto.setBranchCode(clientAccount.getBranchCode());
        dto.setAccountType(clientAccount.getAccountType());
        dto.setAccountNumber(clientAccount.getAccountNumber());
        dto.setAccountHolderName(clientAccount.getAccountHolderName());
        dto.setEmployeeGroupCode(clientAccount.getEmployeeGroupCode());
        dto.setPopInd(clientAccount.getPopInd());
        dto.setPoBankCode(clientAccount.getPoBankCode());
        dto.setPoBranchCode(clientAccount.getPoBranchCode());
        dto.setPoAccountHolderName(clientAccount.getPoAccountHolderName());
        dto.setPoAccountNumber(clientAccount.getPoAccountNumber());
        dto.setPoAccountType(clientAccount.getPoAccountType());
        dto.setCardNumber(clientAccount.getCardNumber());
        dto.setCardExpiry(clientAccount.getCardExpiry());
        dto.setPaymentPreference(clientAccount.getPaymentPreference());
        dto.setStatus(clientAccount.getStatus());
        dto.setMessage(clientAccount.getMessage());
        dto.setClientReference(clientAccount.getClientReference());
        dto.setDebitSequenceType(clientAccount.getDebitSequenceType());
        dto.setFrequencyCode(clientAccount.getFrequencyCode());
        dto.setTrackingCode(clientAccount.getTrackingCode());
        dto.setCreatedDate(clientAccount.getCreatedDate());
        dto.setUpdatedDate(clientAccount.getUpdatedDate());
        dto.setRequestBody(clientAccount.getRequestBody());
        dto.setResponseBody(clientAccount.getResponseBody());
        
        return dto;
    }
    
    private ContractDTO mapToContractDTO(Contract contract) {
        ContractDTO dto = ContractDTO.builder()
            .id(contract.getId())
            .clientAccountId(contract.getClientAccount() != null ? 
                contract.getClientAccount().getId() : null)
            .clientNumber(contract.getClientNumber())
            .contractNumber(contract.getContractNumber())
            .contractType(contract.getContractType())
            .originalContractNumber(contract.getOriginalContractNumber())
            .mandateType(contract.getMandateType())
            .quoteNo(contract.getQuoteNo())
            .contractSequence(contract.getContractSequence() != null ? 
                contract.getContractSequence().toString() : null)
            .frequencyCode(contract.getFrequencyCode())
            .collectionDay(contract.getCollectionDay())
            .trackingCode(contract.getTrackingCode())
            .debitSequenceType(contract.getDebitSequenceType())
            .firstCollectionDate(contract.getFirstCollectionDate())
            
            .firstCollectionAmount(contract.getFirstCollectionAmount())
            .instalmentStartDate(contract.getInstalmentStartDate())
            .instalmentAmount(contract.getInstalmentAmount())
            .numberOfInstalments(contract.getNumberOfInstalments())
            .ctcPercentage(contract.getCtcPercentage() != null ? 
                contract.getCtcPercentage().intValue() : null)
            .status(contract.getStatus())
            .message(contract.getMessage())
            .createdDate(contract.getCreatedDate())
            .updatedDate(contract.getUpdatedDate())
            .requestBody(contract.getRequestBody())
            .responseBody(contract.getResponseBody())
            .build();
        
        return dto;
    }
    
    private InstalmentDTO mapToInstalmentDTO(Instalment installment) {
        InstalmentDTO dto = InstalmentDTO.builder()
            .id(installment.getId())
            .clientAccountId(installment.getClientAccount() != null ? 
                installment.getClientAccount().getId() : null)
            .contractId(installment.getContract() != null ? 
                installment.getContract().getId() : null)
            .quoteNo(installment.getQuoteNo())
            .productId(installment.getProductId())
            .noOfInstalment(installment.getNoOfInstalment())
            .instalmentActionDate(installment.getInstalmentActionDate())
            .clientNumber(installment.getClientNumber())
            .contractSequence(installment.getContractSequence())
            .contractNumber(installment.getContractNumber())
            .trackingCode(installment.getTrackingCode())
            .instalmentAmount(installment.getInstalmentAmount())
            .debitSequenceType(installment.getDebitSequenceType())
            .ctcAmount(installment.getCtcAmount())
            .instalmentReferenceNumber(installment.getInstalmentReferenceNumber())
            .instalmentStatus(installment.getInstalmentStatus())
            .responseCode(installment.getResponseCode())
            .lastUpdateDate(installment.getLastUpdateDate())
            .status(installment.getStatus())
            .message(installment.getMessage())
            .syncStatus(installment.getSyncStatus())
            .createdDate(installment.getCreatedDate())
            .updatedDate(installment.getUpdatedDate())
            .requestBody(installment.getRequestBody())
            .responseBody(installment.getResponseBody())
            .webhookUpdated(installment.getWebhookUpdated())
            .webhookUpdatedDate(installment.getWebhookUpdatedDate())
            .build();
        
        return dto;
    }
    
    private String buildSuccessMessage(ClientAccountDetails clientAccount, Contract contract, List<Instalment> installments) {
        if (clientAccount == null) {
            return "No client data found";
        }
        
        StringBuilder message = new StringBuilder();
        message.append("Client ");
        message.append("SUCCESS".equals(clientAccount.getStatus()) ? "already exists" : "processing status: " + clientAccount.getStatus());
        
        if (contract != null) {
            message.append(", contract ");
            message.append("SUCCESS".equals(contract.getStatus()) ? "exists" : "processing status: " + contract.getStatus());
        } else {
            message.append(", contract not found");
        }
        
        if (!installments.isEmpty()) {
            message.append(", installments exist");
        } else {
            message.append(", installments not found");
        }
        
        return message.toString();
    }
    
    private UnifiedClientCreationResponseDTO createNotFoundResponse(String message) {
        UnifiedClientCreationResponseDTO response = new UnifiedClientCreationResponseDTO();
        UnifiedClientCreationResponseDTO.ClientCreationStatus status = 
            new UnifiedClientCreationResponseDTO.ClientCreationStatus();
        
        status.setClientStatus("NOT_FOUND");
        status.setContractStatus("NOT_FOUND");
        status.setInstallmentsStatus("NOT_FOUND");
        
        response.setStatus(status);
        response.setMessage(message);
        response.setTimestamp(LocalDateTime.now());
        
        return response;
    }
}