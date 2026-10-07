package com.maan.eway.finanaceIntegration.service.impl;

import com.maan.eway.bean.WntAcntDtl;
import com.maan.eway.finanaceIntegration.res.WntAcntDtlRes;
import com.maan.eway.finanaceIntegration.service.FilterSpecificationsBuilder;
import com.maan.eway.finanaceIntegration.service.WntAcntDtlService;
import com.maan.eway.repository.WntAcntDtlRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class WntAcntDtlServiceImpl implements WntAcntDtlService {

    @Autowired
    private WntAcntDtlRepository wntAcntDtlRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public List<WntAcntDtlRes> filterData(WntAcntDtlRes request) {
        List<WntAcntDtlRes> resList = new ArrayList<WntAcntDtlRes>();

        try {
            // Pass the request object directly - FilterSpecificationsBuilder uses reflection
            Specification<WntAcntDtl> spec =
                    new FilterSpecificationsBuilder<WntAcntDtl>()
                            .with(request)
                            .build();

            List<WntAcntDtl> datas = wntAcntDtlRepository.findAll(spec);

            if(datas.isEmpty()) {
                return Collections.emptyList();
            } else {
                for (WntAcntDtl data : datas) {
                    WntAcntDtlRes res = new WntAcntDtlRes();
                    res = modelMapper.map(data, WntAcntDtlRes.class);
                    resList.add(res);
                }
            }

        } catch(Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }

        return resList;
    }
}