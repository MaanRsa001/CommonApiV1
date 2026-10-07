package com.maan.eway.realpay.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.maan.eway.realpay.dto.AccountDTO;
import com.maan.eway.realpay.mapper.AccountMapper;
import com.maan.eway.realpay.repository.AccountRepository;

@Service
public class AccountService {

    private final AccountRepository repository;
    private final AccountMapper mapper;

    public AccountService(AccountRepository repository, AccountMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public AccountDTO create(AccountDTO dto) {
        return mapper.toDTO(repository.save(mapper.toEntity(dto)));
    }

    public List<AccountDTO> findAll() {
        return repository.findAll().stream().map(mapper::toDTO).collect(Collectors.toList());
    }

    public AccountDTO findById(Long id) {
        return repository.findById(id).map(mapper::toDTO).orElse(null);
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }
}
