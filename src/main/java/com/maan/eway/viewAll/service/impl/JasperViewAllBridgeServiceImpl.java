package com.maan.eway.viewAll.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.maan.eway.viewAll.dto.OverAllResForView;
import com.maan.eway.viewAll.dto.viewAllReq;
import com.maan.eway.viewAll.service.JasperViewAllBridgeService;
@Service
public class JasperViewAllBridgeServiceImpl implements JasperViewAllBridgeService{
	
	@Autowired
    private ViewAllWithLableServiceImpl viewAllWithLableServiceImpl;

    @Override
    public OverAllResForView getViewAllData(String quoteNo) {
        viewAllReq req = new viewAllReq();
        req.setQuoteNo(quoteNo);
        return viewAllWithLableServiceImpl.viewAllinKeyAndValue(req);
    }

}
