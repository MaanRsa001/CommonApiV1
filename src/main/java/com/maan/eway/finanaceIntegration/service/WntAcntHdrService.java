package com.maan.eway.finanaceIntegration.service;

import com.maan.eway.finanaceIntegration.res.WntAcntHdrRes;

import java.util.List;

public interface WntAcntHdrService {
    List<WntAcntHdrRes> filterData(WntAcntHdrRes request) ;
}
