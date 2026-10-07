package com.maan.eway.finanaceIntegration.service.impl;

import com.maan.eway.bean.WntAcntHdr;
import com.maan.eway.finanaceIntegration.res.WntAcntHdrRes;
import com.maan.eway.finanaceIntegration.service.FilterSpecificationsBuilder;
import com.maan.eway.finanaceIntegration.service.WntAcntHdrService;
import com.maan.eway.repository.WntAcntHdrRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class WntAcntHdrServiceImpl implements WntAcntHdrService {

    @Autowired
    private WntAcntHdrRepository wntAcntHdrRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public List<WntAcntHdrRes> filterData(WntAcntHdrRes request) {
        List<WntAcntHdrRes> resList = new ArrayList<WntAcntHdrRes>();

        try {
            Specification<WntAcntHdr> spec =
                    new FilterSpecificationsBuilder<WntAcntHdr>()
                            .with(request)
                            .build();

            List<WntAcntHdr> datas = wntAcntHdrRepository.findAll(spec);

            if(datas.isEmpty()) {
                return Collections.emptyList();
            } else {
                for (WntAcntHdr data : datas) {
                    WntAcntHdrRes res = new WntAcntHdrRes();
                    res = modelMapper.map(data, WntAcntHdrRes.class);
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