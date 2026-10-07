package com.maan.eway.common.service.impl;

import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.maan.eway.bean.HomePositionMaster;
import com.maan.eway.bean.PersonalInfo;
import com.maan.eway.bean.SmsConfigMaster;
import com.maan.eway.bean.SmsDataDetails;
import com.maan.eway.common.req.SendSmsReq;
import com.maan.eway.repository.HomePositionMasterRepository;
import com.maan.eway.repository.NotifTemplateMasterRepository;
import com.maan.eway.repository.PersonalInfoRepository;
import com.maan.eway.repository.SmsConfigMasterRepository;
import com.maan.eway.repository.SmsDataDetailsRepository;
import com.maan.eway.res.SuccessRes;


@Service
@Transactional

public class SmsDetailsImpl {

	private Logger log=LogManager.getLogger(SmsDetailsImpl.class);

	
	@Autowired
	private HomePositionMasterRepository homerepo;
	
	@Autowired
	private SmsConfigMasterRepository smsConfigRepo;
	
	@Autowired
	private SmsDataDetailsRepository smsDetailRepo;
	
	@Autowired
	private NotifTemplateMasterRepository notifRepo;
	
	@Autowired
	private PersonalInfoRepository personalRepo;
	
	@Autowired
	private com.maan.eway.service.PrintReqService reqPrinter;
	
	
	@Autowired
	private GenerateSeqNoServiceImpl genSeqNoService ; 

	
	public SuccessRes sendSms(SendSmsReq req) {
		reqPrinter.reqPrint(req);

		SuccessRes res = new SuccessRes();
		SmsDataDetails savedata = new SmsDataDetails();
		DozerBeanMapper mapper = new DozerBeanMapper();
		try {
			Long sno = smsDetailRepo.count();
			
			List<HomePositionMaster> homeposition = homerepo.findByRequestReferenceNo(req.getRequestReferenceNo());
			List<SmsConfigMaster> smsconfig = smsConfigRepo.findByCompanyIdAndBranchCode(req.getCompanyId(),req.getBranchCode());
			PersonalInfo customerdata = personalRepo.findByCustomerId(req.getCustomerId());
			mapper.map(req,SmsDataDetails.class);

			// Customer Personal Info 
			
		//	savedata.setMobileNoDesc(req.getMobileNoDesc());
			savedata.setMobileNo(req.getMobileNo());

			// Product Details
			savedata.setSmsType(req.getSmsSubject());
			savedata.setSmsContent(req.getSmsBody());
			savedata.setEntryDate(new Date());
			savedata.setSNo(sno.toString());
			if(homeposition!=null && homeposition.size() > 0 ) {
				savedata.setPushedBy("1".equalsIgnoreCase(homeposition.get(0).getApplicationId()) ? homeposition.get(0).getLoginId() : homeposition.get(0).getApplicationId() );
			}
		//	savedata.setSenderId(smsconfig.get(0).getSenderId());
			
			
			res.setResponse("SMS Sent Successfully");
			res.setSuccessId(sno.toString());
			reqPrinter.reqPrint(res);
			
			smsDetailRepo.save(savedata);
			}
		
		catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details", e.getMessage());
			return null;
		}
		return res;
	}
		
}