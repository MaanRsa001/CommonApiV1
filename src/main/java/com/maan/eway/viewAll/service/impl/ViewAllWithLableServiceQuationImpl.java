package com.maan.eway.viewAll.service.impl;



import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.maan.eway.bean.BuildingRiskDetails;
import com.maan.eway.bean.CommonDataDetails;
import com.maan.eway.bean.EserviceBuildingDetails;
import com.maan.eway.bean.EserviceCommonDetails;
import com.maan.eway.bean.EserviceCustomerDetails;
import com.maan.eway.bean.EserviceSectionDetails;
import com.maan.eway.bean.InsuranceCompanyMaster;
import com.maan.eway.bean.LoginUserInfo;
import com.maan.eway.bean.PersonalInfo;
import com.maan.eway.jasper.res.AttachMentRes;
import com.maan.eway.repository.EServiceSectionDetailsRepository;
import com.maan.eway.repository.EserviceBuildingDetailsRepository;
import com.maan.eway.repository.EserviceCommonDetailsRepository;
import com.maan.eway.repository.EserviceCustomerDetailsRepository;
import com.maan.eway.viewAll.dto.CommonCWESetRes;
import com.maan.eway.viewAll.dto.CompanyInfoDto;
import com.maan.eway.viewAll.dto.KeyAndValueDto;
import com.maan.eway.viewAll.dto.LocationInformationKeyValueRes;
import com.maan.eway.viewAll.dto.OverAllResForView;
import com.maan.eway.viewAll.dto.QuotationReq;
import com.maan.eway.viewAll.entity.RiskInfoPdf;
import com.maan.eway.viewAll.entity.RiskInfoPdfRepo;
import com.maan.eway.viewAll.service.ViewAllWithLableServiceQuation;

public class ViewAllWithLableServiceQuationImpl implements ViewAllWithLableServiceQuation{

	@Autowired
	private EserviceCommonDetailsRepository commonRepo;
	
	@Autowired
	private EserviceBuildingDetailsRepository buldinRepo;
	
	@Autowired
	private EServiceSectionDetailsRepository sectionDataRepo;
	
	@Autowired
	private RiskInfoPdfRepo riskInfoPdfRepo;
	
	@Autowired
	private EserviceCustomerDetailsRepository customerRepo;
	
	private Logger log = LogManager.getLogger(ViewAllWithLableServiceImpl.class);
	
	@Override
	public OverAllResForView viewAllQuotation(QuotationReq req) {
		//CommonRes res = new CommonRes();
		OverAllResForView ovRes= new OverAllResForView();
		try {
			SimpleDateFormat displayFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			List<EserviceSectionDetails> sectionList = sectionDataRepo.findByRequestReferenceNoAndStatusNotOrderByRiskIdAsc(req.getRequestReferenceNo(), "D");
			List<EserviceBuildingDetails> buildList = buldinRepo.findByRequestReferenceNoAndStatusNotOrderByRiskIdAsc(req.getRequestReferenceNo(), "D");
		    List<EserviceCommonDetails> commonList = commonRepo.findByRequestReferenceNoAndStatusNotOrderByRiskIdAsc(req.getRequestReferenceNo(), "D");
//			if(sectionList!=null)
//			{	
//				 RiskInfoPdf pdf =riskInfoPdfRepo.findByQuoteNoAndCompanyidAndProductId(req.getQuoteNo(),home.getCompanyId(),home.getProductId());
//				 if(pdf!= null)
//					{
//						ObjectMapper objectMapper = new ObjectMapper();
//						return objectMapper.readValue(pdf.getPdfSt(), OverAllResForView.class);
//					}
//				 String cus ="";
//				 if("A".equalsIgnoreCase(sectionList.get(0).getProductType()) ) {
//					ovRes.setInceptionDate(buildList.get(0).getPolicyStartDate() != null ? displayFormat.format(buildList.get(0).getPolicyStartDate()) : "");
//					ovRes.setExpiryDate(buildList.get(0).getPolicyEndDate()!= null ? displayFormat.format(buildList.get(0).getPolicyEndDate()) : "");
//					ovRes.setEffectiveDate(buildList.get(0).getPolicyStartDate() != null ? displayFormat.format(buildList.get(0).getPolicyStartDate()) : "");
//					ovRes.setPolicyNo(null);
//					ovRes.setPolicyNumn(null);
//					ovRes.setQuoteNo(buildList.get(0).getQuoteNo() != null ? buildList.get(0).getQuoteNo() :"" );
//					cus =buildList.get(0).getCustomerReferenceNo();
//				}
//				 else
//				 {
//					ovRes.setInceptionDate(commonList.get(0).getPolicyStartDate() != null ? displayFormat.format(commonList.get(0).getPolicyStartDate()) : "");
//					ovRes.setExpiryDate(commonList.get(0).getPolicyEndDate()!= null ? displayFormat.format(commonList.get(0).getPolicyEndDate()) : "");
//					ovRes.setEffectiveDate(commonList.get(0).getPolicyStartDate() != null ? displayFormat.format(commonList.get(0).getPolicyStartDate()) : "");
//				    ovRes.setPolicyNo(null);
//					ovRes.setPolicyNumn(null);
//					ovRes.setQuoteNo(commonList.get(0).getQuoteNo() != null ? commonList.get(0).getQuoteNo() : "");
//					cus =commonList.get(0).getCustomerReferenceNo();
//					}
//				 ovRes.setCurrencyName(sectionList.get(0).getCurrencyId());	
//				 ovRes.setProductName(sectionList.get(0).getProductDesc());
//				CommonCWESetRes comCn= new CommonCWESetRes();
//				LoginUserInfo login = new LoginUserInfo();
//				String customerId = home.getCustomerId();
//				EserviceCustomerDetails pi = customerRepo.findByCustomerReferenceNo(cus);
//				login = logrepo.findByLoginId(home.getLoginId());
//				PersonalInfo single = pi.get(0);
//				ovRes.setClientName(single.getClientName());	
//				List<KeyAndValueDto> piList = piMethodFforSet(pi);
//				List<KeyAndValueDto> policyDetails = buildPolicyDetails(home,buildList,commonList);
//				List<KeyAndValueDto> brokerDetails = buildBrokerDetails(home, login);
//				if ("1".equalsIgnoreCase(home.getApplicationId())) {
//					ovRes.setLoginId(home.getLoginId());
//					ovRes.setBrokerName(login.getUserName());
//					ovRes.setCreatedBy(home.getLoginId());
//					ovRes.setApprovedBy(home.getLoginId());
//					if("A".equalsIgnoreCase(home.getAdminReferralStatus()))
//					{
//						ovRes.setApprovedBy(home.getAdminLoginId());
//					}
//				}else
//				{
//					ovRes.setLoginId(home.getApplicationId());
//					ovRes.setBrokerName(home.getBdmName());
//					ovRes.setCreatedBy(home.getApplicationId());
//					ovRes.setApprovedBy(home.getApplicationId());
//					if("A".equalsIgnoreCase(home.getAdminReferralStatus()))
//					{
//						ovRes.setApprovedBy(home.getAdminLoginId());
//					}
//				}
//				ovRes.setBranchName(home.getBranchName());
//				List<Map<String, Object>> policycrdr= totalPremeumlist(req.getQuoteNo());
//				Set<String> sectionIds = new HashSet<>();
//				if (buildList != null && !buildList.isEmpty()) {
//				    sectionIds.addAll(buildList.stream().map(BuildingRiskDetails::getSectionId).collect(Collectors.toSet()));
//				}
//
//				if (commonList != null && !commonList.isEmpty()) {
//				    sectionIds.addAll(commonList.stream().map(CommonDataDetails::getSectionId).collect(Collectors.toSet()));
//				}
//				int size = sectionIds.size();
//				boolean is = size>1 ? true : false;
//				List<LocationInformationKeyValueRes> dynamicSetAssert = dynamicSetAssert(home,buildList,commonList,is);
//				List<Map<String, Object>> conditionList = jasperCus.getConditionListSectionCover(home.getPolicyNo(),home.getQuoteNo(),sectionIds);
//				List<Map<String, Object>> warrantyDescription = jasperCus.getWarrantyDescriptionSectionCover(home.getPolicyNo(),home.getQuoteNo(),sectionIds);
//				List<Map<String, Object>> exclusionList = jasperCus.getExclusionListSectionCover(home.getPolicyNo(),home.getQuoteNo(),sectionIds);
//				comCn.setCondition(conditionList);
//				comCn.setWarranty(warrantyDescription);
//				comCn.setExclusionList(exclusionList);
//				List<AttachMentRes> attachMentList = new ArrayList<>();
//				for (String sec : sectionIds)
//				{
//					AttachMentRes attach = getBySectionId(sec,home);
//					if(StringUtils.isNotBlank(attach.getDocloction()) && StringUtils.isNotBlank(attach.getDocRefNo()))
//						{
//						attachMentList.add(attach);
//						}
//				}
//				List<InsuranceCompanyMaster> comName =incomRepo.findTopByCompanyIdOrderByAmendIdDesc(home.getCompanyId());
//				if(!comName.isEmpty() && comName!=null)
//				{
//					CompanyInfoDto companyInfo =companyInfo(comName);
//					ovRes.setCompany(companyInfo);
//					ovRes.setCountryName(companyInfo.getCompanyName());					
//				}
//				ovRes.setPi(piList);
//				ovRes.setAttachment(attachMentList);
//				ovRes.setBrokerInfo(brokerDetails);
//				ovRes.setPolicyInfo(policyDetails);
//				ovRes.setTotalPre(policycrdr);
//				ovRes.setLocation(dynamicSetAssert);
//				ovRes.setComCon(comCn);
//				setpdfJson(ovRes, home);
//                 
//			}
		}catch(Exception e)
		{
			e.printStackTrace();
			log.error("Exception in viewAllinKeyAndValue ===> {}", e.getMessage());
		}
		return ovRes;
	}

}
