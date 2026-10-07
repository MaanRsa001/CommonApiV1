package com.maan.eway.finanaceIntegration.service;

import com.maan.eway.finanaceIntegration.res.WntAcntDtlRes;

import java.util.List;

public interface WntAcntDtlService {
    List<WntAcntDtlRes> filterData(WntAcntDtlRes request) ;
}
