package com.maan.eway.realpay.service;

import com.maan.eway.bean.EmiTransactionDetails;
import com.maan.eway.bean.HomePositionMaster;
import com.maan.eway.realpay.dto.EftTransactionDetailsDTO;
import com.maan.eway.realpay.mapper.EftTransactionDetailsMapper;
import com.maan.eway.realpay.model.EftTransactionDetails;
import com.maan.eway.realpay.repository.EftTransactionDetailsRepository;
import com.maan.eway.repository.EmiTransactionDetailsRepository;
import com.maan.eway.repository.HomePositionMasterRepository;
import com.maan.eway.common.res.CommonRes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EftTransactionDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(EftTransactionDetailsService.class);

    @Autowired
    private EftTransactionDetailsRepository repository;
    @Autowired
    private EftTransactionDetailsMapper mapper;
    @Autowired
    private HomePositionMasterRepository homePositionMasterRepository;



    @Transactional
    public CommonRes create(EftTransactionDetailsDTO dto) {
        CommonRes res = new CommonRes();
        try {
            logger.info("Creating new EFT transaction detail for customer: {}", dto.getCustomerCode());
            EftTransactionDetails entity = mapper.toEntity(dto);
            HomePositionMaster homePositionMaster = homePositionMasterRepository.findByQuoteNo(dto.getQuoteNo());
            if (homePositionMaster != null) {
                entity.setUwSysId(homePositionMaster.getUwsysId());
                entity.setPolIdx(Math.toIntExact(homePositionMaster.getAhpolIdx()));
            }
            EftTransactionDetails saved = repository.save(entity);
            res.setCommonResponse(mapper.toDto(saved));
            res.setIsError(false);
            res.setMessage("Success");
        } catch (Exception e) {
            logger.error("Error in create EFT transaction: {}", e.getMessage(), e);
            res.setMessage(e.getMessage());
            res.setIsError(true);
        }
        return res;
    }

    @Transactional
    public CommonRes update(Long id, EftTransactionDetailsDTO dto) {
        CommonRes res = new CommonRes();
        try {
            logger.info("Updating EFT transaction detail ID: {}", id);
            Optional<EftTransactionDetails> existingOpt = repository.findById(id);
            if (existingOpt.isPresent()) {
                EftTransactionDetails existing = existingOpt.get();
                EftTransactionDetails updatedEntity = mapper.toEntity(dto);
                updatedEntity.setId(id);
                // Preserving audit fields from existing if not provided in DTO
                updatedEntity.setCreatedBy(existing.getCreatedBy());
                updatedEntity.setCreatedDate(existing.getCreatedDate());
                
                EftTransactionDetails saved = repository.save(updatedEntity);
                res.setCommonResponse(mapper.toDto(saved));
                res.setIsError(false);
                res.setMessage("Success");
            } else {
                res.setIsError(true);
                res.setMessage("Transaction not found with ID: " + id);
            }
        } catch (Exception e) {
            logger.error("Error updating EFT transaction ID {}: {}", id, e.getMessage(), e);
            res.setMessage(e.getMessage());
            res.setIsError(true);
        }
        return res;
    }

    public CommonRes getById(Long id) {
        CommonRes res = new CommonRes();
        try {
            Optional<EftTransactionDetails> entityOpt = repository.findById(id);
            if (entityOpt.isPresent()) {
                res.setCommonResponse(mapper.toDto(entityOpt.get()));
                res.setIsError(false);
                res.setMessage("Success");
            } else {
                res.setIsError(true);
                res.setMessage("Transaction not found with ID: " + id);
            }
        } catch (Exception e) {
            logger.error("Error fetching EFT transaction by ID {}: {}", id, e.getMessage(), e);
            res.setMessage(e.getMessage());
            res.setIsError(true);
        }
        return res;
    }

    public CommonRes getAll() {
        CommonRes res = new CommonRes();
        try {
            List<EftTransactionDetailsDTO> list = repository.findAll().stream()
                    .map(mapper::toDto)
                    .collect(Collectors.toList());
            res.setCommonResponse(list);
            res.setIsError(false);
            res.setMessage("Success");
        } catch (Exception e) {
            logger.error("Error fetching all EFT transactions: {}", e.getMessage(), e);
            res.setMessage(e.getMessage());
            res.setIsError(true);
        }
        return res;
    }

    @Transactional
    public CommonRes delete(Long id) {
        CommonRes res = new CommonRes();
        try {
            logger.info("Deleting EFT transaction detail ID: {}", id);
            repository.deleteById(id);
            res.setIsError(false);
            res.setMessage("Deleted Successfully");
        } catch (Exception e) {
            logger.error("Error deleting EFT transaction ID {}: {}", id, e.getMessage(), e);
            res.setMessage(e.getMessage());
            res.setIsError(true);
        }
        return res;
    }
}
