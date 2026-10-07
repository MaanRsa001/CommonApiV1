package com.maan.eway.creditAmount.service.impl;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.maan.eway.bean.DepositDetail;
import com.maan.eway.bean.DepositcbcMaster;
import com.maan.eway.creditAmount.Res.BrokerCreditDetailsRes;
import com.maan.eway.creditAmount.service.BrokerCreditService;
import com.maan.eway.repository.DepositDetailRepository;
import com.maan.eway.repository.DepositcbcMasterRepository;


@Service
public class BrokerCreditServiceImpl implements BrokerCreditService{

	    @Autowired
	    private DepositcbcMasterRepository depositcbcMasterRepo;

	    @Autowired
	    private DepositDetailRepository depositDetailRepo;
	    
	    @Override
	    public BrokerCreditDetailsRes getBrokerCreditSummary(String brokerId) {
	        double creditLimit = 0.0;
	        double utilized = 0.0;
	        double balance = 0.0; 
	        String cbcNo="";
	        List<DepositcbcMaster> masters = depositcbcMasterRepo.findByBrokerId(brokerId);
	        for (DepositcbcMaster m : masters) {
	            creditLimit += m.getDepositAmount() != null ? m.getDepositAmount() : 0.0;
	            utilized += m.getDepositUtilized() != null ? m.getDepositUtilized() : 0.0;
	            cbcNo=m.getCbcNo();
	        }

	        BrokerCreditDetailsRes response = new BrokerCreditDetailsRes();
	        response.setBrokerId(brokerId);
	        response.setDepositAmount(BigDecimal.valueOf(creditLimit).toPlainString());
	        response.setDepositUtilized(BigDecimal.valueOf(utilized).toPlainString());
	        response.setCbcNo(cbcNo);

	        return response;
	    }

	    @Override
	    public BrokerCreditDetailsRes getBrokerCreditBalance(String quoteNo) {
	        double utilized = 0.0;
	        double balance = 0.0;

	        List<DepositDetail> details = depositDetailRepo.findByQuoteNo(quoteNo);
	        for (DepositDetail d : details) {
	            balance += d.getBalanceAmount() != null ? d.getBalanceAmount() : 0.0;
	            utilized+=d.getPremiumAmount()!=null ? d.getPremiumAmount(): 0.0;
	        }

	        BrokerCreditDetailsRes response = new BrokerCreditDetailsRes();
	        response.setPremiumAmount(BigDecimal.valueOf(utilized).toPlainString());
	        response.setBalanceAmount(BigDecimal.valueOf(balance).toPlainString());;

	        return response;
	    }
}
