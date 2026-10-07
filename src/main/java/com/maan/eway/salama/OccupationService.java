package com.maan.eway.salama;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.maan.eway.bean.SalamaOccupation;

@Service
public class OccupationService {

    @Autowired
    private SalamaOccupationRepository repo;

    public List<OccupationRes> getOccupationList(OccupationReq req) {

        Integer companyId = Integer.valueOf(req.getCompanyId());
        Integer industryId = Integer.valueOf(req.getTypeId());

        List<SalamaOccupation> list = repo.getOccupationList(companyId, industryId);

        List<OccupationRes> response = new ArrayList<>();

        for (SalamaOccupation occ : list) {

            OccupationRes res = new OccupationRes();
            res.setSNo(occ.getSNo() == null ? "" : occ.getSNo().toString());
            res.setOccupationDescription(
                    occ.getOccupationDescription() == null ? "" : occ.getOccupationDescription()
            );

            response.add(res);
        }

        return response;
    }
}
