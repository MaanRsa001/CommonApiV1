package com.maan.eway.realpay.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.maan.eway.realpay.dto.ContractDTO;
import com.maan.eway.realpay.mapper.ContractMapper;
import com.maan.eway.realpay.model.ClientAccountDetails;
import com.maan.eway.realpay.model.Contract;
import com.maan.eway.realpay.repository.ClientAccountDetailsRepository;
import com.maan.eway.realpay.repository.ContractRepository;

@Service
@Transactional
public class ContractService {

    @Autowired
    private ContractRepository contractRepository;
    
    @Autowired
    private ClientAccountDetailsRepository clientAccountRepository;

    public List<ContractDTO> getAll() {
        return contractRepository.findAll()
                .stream()
                .map(ContractMapper::toDto)
                .collect(Collectors.toList());
    }

    public ContractDTO getById(Long id) {
        return contractRepository.findById(id)
                .map(ContractMapper::toDto)
                .orElse(null);
    }

    public ContractDTO create(ContractDTO dto) {
        // Find the client account
        Optional<ClientAccountDetails> clientAccount = clientAccountRepository.findById(dto.getClientAccountId());
        if (clientAccount.isEmpty()) {
            throw new RuntimeException("Client account not found with id: " + dto.getClientAccountId());
        }
        
        Contract entity = ContractMapper.toEntity(dto, clientAccount.get());
        Contract saved = contractRepository.save(entity);
        return ContractMapper.toDto(saved);
    }

    public ContractDTO update(Long id, ContractDTO dto) {
        return contractRepository.findById(id).map(existing -> {
            // Find the client account
            Optional<ClientAccountDetails> clientAccount = clientAccountRepository.findById(dto.getClientAccountId());
            if (clientAccount.isEmpty()) {
                throw new RuntimeException("Client account not found with id: " + dto.getClientAccountId());
            }
            
            Contract updated = ContractMapper.toEntity(dto, clientAccount.get());
            updated.setId(id);
            Contract saved = contractRepository.save(updated);
            return ContractMapper.toDto(saved);
        }).orElse(null);
    }

    public void delete(Long id) {
        contractRepository.deleteById(id);
    }
}