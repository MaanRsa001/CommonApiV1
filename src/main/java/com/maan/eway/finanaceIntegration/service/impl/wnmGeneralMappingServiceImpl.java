package com.maan.eway.finanaceIntegration.service.impl;

import com.maan.eway.bean.WnmGeneralMapping;
import com.maan.eway.finanaceIntegration.res.InputFieldRes;
import com.maan.eway.finanaceIntegration.res.ObjectMappingRes;
import com.maan.eway.finanaceIntegration.res.WnmGeneralMappingRes;

import com.maan.eway.finanaceIntegration.service.FilterSpecificationsBuilder;
import com.maan.eway.finanaceIntegration.service.wnmGeneralMappingService;
import com.maan.eway.repository.wnmGeneralMappingRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class wnmGeneralMappingServiceImpl implements wnmGeneralMappingService {


    @Autowired
    private wnmGeneralMappingRepository repository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public List<WnmGeneralMappingRes> filterData(WnmGeneralMappingRes request) {
        List<WnmGeneralMappingRes> resList = new ArrayList<WnmGeneralMappingRes>();

        try {
            Specification<WnmGeneralMapping> spec =
                    new FilterSpecificationsBuilder<WnmGeneralMapping>()
                            .with(request)
                            .build();

            List<WnmGeneralMapping> datas = repository.findAll(spec);

            if(datas.isEmpty()) {
                return Collections.emptyList();
            } else {
                for (WnmGeneralMapping data : datas) {
                    WnmGeneralMappingRes res = new WnmGeneralMappingRes();
                    res = modelMapper.map(data, WnmGeneralMappingRes.class);
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
    public WnmGeneralMappingRes create(WnmGeneralMappingRes request) {
        WnmGeneralMapping entity = modelMapper.map(request, WnmGeneralMapping.class);
        WnmGeneralMapping saved = repository.save(entity);
        return modelMapper.map(saved, WnmGeneralMappingRes.class);
    }

    @Override
    public WnmGeneralMappingRes update(WnmGeneralMappingRes request) {
        WnmGeneralMapping entity = repository.findById(request.getGmSysId())
                .orElseThrow(() -> new RuntimeException("Record not found"));

        // Partial update (PATCH behavior)
        modelMapper.map(request, entity);
        WnmGeneralMapping updated = repository.save(entity);
        return modelMapper.map(updated, WnmGeneralMappingRes.class);
    }

    @Override
    public boolean delete(Long gmSysId) {
        repository.deleteById(gmSysId);
        return true;
    }

    @Override
    public List<ObjectMappingRes> getObjectMappings(String tableName ,String companyId) {

        List<Object[]> dataList = repository.getColumnDetails(tableName,companyId);

        return dataList.stream()
                .collect(Collectors.groupingBy(
                        row -> String.valueOf(row[0]),   // object_name
                        LinkedHashMap::new,
                        Collectors.mapping(
                                row -> new InputFieldRes(
                                        String.valueOf(row[1]), // input_name
                                        String.valueOf(row[2])  // input_type
                                ),
                                Collectors.toList()
                        )
                ))
                .entrySet()
                .stream()
                .map(e -> new ObjectMappingRes(e.getKey(), e.getValue()))
                .collect(Collectors.toList());
    }
}