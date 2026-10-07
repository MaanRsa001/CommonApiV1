package com.maan.eway.finanaceIntegration.service.impl;

import com.maan.eway.bean.WnmAccountingEntryMapping;
import com.maan.eway.finanaceIntegration.res.WnmAccountingEntryMappingRes;
import com.maan.eway.finanaceIntegration.service.FilterSpecificationsBuilder;
import com.maan.eway.finanaceIntegration.service.WnmAccountingEntryMappingService;
import com.maan.eway.repository.WnmAccountingEntryMappingRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class WnmAccountingEntryMappingServiceImpl implements WnmAccountingEntryMappingService {

    @Autowired
    private WnmAccountingEntryMappingRepository wnmAccountingEntryMappingRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public List<WnmAccountingEntryMappingRes> filterData(WnmAccountingEntryMappingRes request) {
        List<WnmAccountingEntryMappingRes> resList = new ArrayList<WnmAccountingEntryMappingRes>();

        try {
            Specification<WnmAccountingEntryMapping> spec =
                    new FilterSpecificationsBuilder<WnmAccountingEntryMapping>()
                            .with(request)
                            .build();

            List<WnmAccountingEntryMapping> datas = wnmAccountingEntryMappingRepository.findAll(spec);

            if(datas.isEmpty()) {
                return Collections.emptyList();
            } else {
                for (WnmAccountingEntryMapping data : datas) {
                    WnmAccountingEntryMappingRes res = new WnmAccountingEntryMappingRes();
                    res = modelMapper.map(data, WnmAccountingEntryMappingRes.class);
                    resList.add(res);
                }
            }

        } catch(Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }

        return resList;
    }

    @Override
    public WnmAccountingEntryMappingRes create(WnmAccountingEntryMappingRes request) {
        WnmAccountingEntryMapping entity = modelMapper.map(request, WnmAccountingEntryMapping.class);

        // Set creation date if not already set
        if (entity.getAcntCrDt() == null) {
            entity.setAcntCrDt(new Date());
        }

        WnmAccountingEntryMapping saved = wnmAccountingEntryMappingRepository.save(entity);
        return modelMapper.map(saved, WnmAccountingEntryMappingRes.class);
    }

    @Override
    public WnmAccountingEntryMappingRes update(WnmAccountingEntryMappingRes request) {
        Optional<WnmAccountingEntryMapping> existingOpt =
                wnmAccountingEntryMappingRepository.findById(request.getAemSysId());

        if (existingOpt.isPresent()) {
            WnmAccountingEntryMapping existing = existingOpt.get();

            // Preserve creation info
            String originalCreator = existing.getAcntCrUid();
            Date originalCreateDate = existing.getAcntCrDt();

            // Update entity with new values (Partial update/PATCH behavior)
            modelMapper.map(request, existing);

            // Restore creation info
            existing.setAcntCrUid(originalCreator);
            existing.setAcntCrDt(originalCreateDate);

            // Set update date if not already set
            if (existing.getAcntUpdDt() == null) {
                existing.setAcntUpdDt(new Date());
            }

            WnmAccountingEntryMapping updated = wnmAccountingEntryMappingRepository.save(existing);
            return modelMapper.map(updated, WnmAccountingEntryMappingRes.class);
        }

        return null; // Record not found
    }

    @Override
    public boolean delete(Long aemSysId) {
        if (wnmAccountingEntryMappingRepository.existsById(aemSysId)) {
            wnmAccountingEntryMappingRepository.deleteById(aemSysId);
            return true;
        }
        return false;
    }
}