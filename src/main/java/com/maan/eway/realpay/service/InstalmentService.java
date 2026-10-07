package com.maan.eway.realpay.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.maan.eway.bean.EmiTransactionDetails;
import com.maan.eway.realpay.dto.InstalmentDTO;
import com.maan.eway.realpay.mapper.InstalmentMapper;
import com.maan.eway.realpay.model.ClientAccountDetails;
import com.maan.eway.realpay.model.Contract;
import com.maan.eway.realpay.model.Instalment;
import com.maan.eway.realpay.repository.ClientAccountDetailsRepository;
import com.maan.eway.realpay.repository.ContractRepository;
import com.maan.eway.realpay.repository.InstalmentRepository;
import com.maan.eway.repository.EmiTransactionDetailsRepository;

@Service
@Transactional
public class InstalmentService {

    @Autowired
    private InstalmentRepository instalmentRepository;
    
    @Autowired
    private ClientAccountDetailsRepository clientAccountRepository;
    
    @Autowired
    private ContractRepository contractRepository;
    
    @Autowired
    private EmiTransactionDetailsRepository emiTransactionRepository;

    public List<InstalmentDTO> getAll() {
        return instalmentRepository.findAll()
                .stream()
                .map(InstalmentMapper::toDto)
                .collect(Collectors.toList());
    }

    public InstalmentDTO getById(Long id) {
        return instalmentRepository.findById(id)
                .map(InstalmentMapper::toDto)
                .orElse(null);
    }

    public InstalmentDTO create(InstalmentDTO dto) {
        Optional<ClientAccountDetails> clientAccount = clientAccountRepository.findById(dto.getClientAccountId());
        if (clientAccount.isEmpty()) {
            throw new RuntimeException("Client account not found with id: " + dto.getClientAccountId());
        }
        
        Optional<Contract> contract = contractRepository.findById(dto.getContractId());
        if (contract.isEmpty()) {
            throw new RuntimeException("Contract not found with id: " + dto.getContractId());
        }
        
        Instalment entity = InstalmentMapper.toEntity(dto, clientAccount.get(), contract.get());
        Instalment saved = instalmentRepository.save(entity);
        return InstalmentMapper.toDto(saved);
    }
    
//    public void wecoreEmitoRealPayInstallment(InstalmentDTO dto) {
//        ClientAccountDetails clientAccount = clientAccountRepository.findById(dto.getClientAccountId())
//            .orElseThrow(() -> new RuntimeException("Client account not found"));
//
//        Contract contract = contractRepository.findById(dto.getContractId())
//            .orElseThrow(() -> new RuntimeException("Contract not found"));
//
//        List<EmiTransactionDetails> transactions = emiTransactionRepository.findByQuoteNo(dto.getQuoteNo());
//        List<Instalment> instalmentsToSave = new ArrayList<>();
//
//        for (EmiTransactionDetails tx : transactions) {
//            boolean exists = instalmentRepository.existsByQuoteNoAndProductIdAndNoOfInstalmentAndInstalmentActionDateAndClientNumber(
//                tx.getQuoteNo(),
//                tx.getProductId(),
//                Long.valueOf(tx.getInstalment()),
//                tx.getDueDate().toString(), 
//                clientAccount.getClientNumber()
//            );
//
//            if (exists) {
//                continue; 
//            }
//
//            Instalment instalment = new Instalment();
//            instalment.setClientAccount(clientAccount);
//            instalment.setContract(contract);
//            instalment.setClientNumber(clientAccount.getClientNumber());
//            instalment.setContractNumber(contract.getContractNumber());
//            instalment.setContractSequence(contract.getContractSequence());
//            
//            instalment.setQuoteNo(tx.getQuoteNo());
//            instalment.setProductId(tx.getProductId());
//            instalment.setNoOfInstalment(Long.valueOf(tx.getInstalment()));
//            instalment.setInstalmentActionDate(tx.getDueDate().toString()); 
//            instalment.setInstalmentAmount(BigDecimal.valueOf(tx.getDueAmount() != null ? tx.getDueAmount() : 0));
//            instalment.setTrackingCode(dto.getTrackingCode());
//
//            instalment.setDebitSequenceType(dto.getDebitSequenceType());
//            instalment.setCtcAmount(BigDecimal.ZERO);
//
//            instalmentsToSave.add(instalment);
//        }
//        
//        if (!instalmentsToSave.isEmpty()) {
//            instalmentRepository.saveAll(instalmentsToSave);
//        }
//    }



    public InstalmentDTO update(Long id, InstalmentDTO dto) {
        return instalmentRepository.findById(id).map(existing -> {
            // Find the client account
            Optional<ClientAccountDetails> clientAccount = clientAccountRepository.findById(dto.getClientAccountId());
            if (clientAccount.isEmpty()) {
                throw new RuntimeException("Client account not found with id: " + dto.getClientAccountId());
            }
            
            // Find the contract
            Optional<Contract> contract = contractRepository.findById(dto.getContractId());
            if (contract.isEmpty()) {
                throw new RuntimeException("Contract not found with id: " + dto.getContractId());
            }
            
            Instalment updated = InstalmentMapper.toEntity(dto, clientAccount.get(), contract.get());
            updated.setId(id);
            Instalment saved = instalmentRepository.save(updated);
            return InstalmentMapper.toDto(saved);
        }).orElse(null);
    }

    public void delete(Long id) {
        instalmentRepository.deleteById(id);
    }
}