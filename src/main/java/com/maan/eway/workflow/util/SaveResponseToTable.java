package com.maan.eway.workflow.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.maan.eway.bean.EserviceMotorDetails;
import com.maan.eway.common.req.EserviceMotorDetailsSaveRes;
import com.maan.eway.repository.EServiceMotorDetailsRepository;
import com.maan.eway.res.calc.Cover;
import com.maan.eway.res.calc.Tax;
import com.maan.eway.res.referal.MasterReferal;
import com.maan.eway.service.impl.FactorRatePersistenceService;
import com.maan.eway.workflow.dto.WorkEngine;

import jakarta.persistence.Tuple;

@Service
public class SaveResponseToTable {
	@Autowired
	private FactorRatePersistenceService fservice;
	
	@Autowired
	private WorkFlowFactorUtil workflow;

	
	@Autowired
	private EServiceMotorDetailsRepository eserMotorRepo;

	
	public void saveIntoFactorRequestTable(Map<String, Object> response, WorkEngine engine, Map<String, Object> request) {
		try {
			
			List<MasterReferal> masterreferral=null;
			
			Map<String, Object> data=(Map<String, Object>) response.get("data");
			
			if(data.get("summaryDetails")!=null || 	data.get("errorDetailsList")!=null) {
				masterreferral=new ArrayList<MasterReferal>();
				if(data.get("summaryDetails")!=null) {
					if(data.get("brResults")!=null) {
						List<Map<String, Object>> brResults=(List<Map<String, Object>>) data.get("brResults");
						for (Map<String, Object> map : brResults) {
							MasterReferal master = MasterReferal.builder().isreferral(true).referralDesc(map.get("message").toString()).build();
							masterreferral.add(master);
						}
					}
				}
				if(data.get("errorDetailsList")!=null) {
					List<Map<String, Object>> errorDetailsList=(List<Map<String, Object>>) data.get("errorDetailsList");
					for (Map<String, Object> map : errorDetailsList) {
						MasterReferal master = MasterReferal.builder().isreferral(true).referralDesc(map.get("errorDescription").toString()).build();
						masterreferral.add(master);
					}
				}
				
				
			}
			
			Map<String, Object> object6 =null;
			Map<String, Object> premiumResponseArr =null;
			List<Map<String, Object>> charges=null;
			String exchangeRateStr="1";
			try {

				Map<String, Object> object1 = (Map<String, Object>) response.get("data");
				Map<String, Object> object2 = (Map<String, Object>) object1.get("summaryDetails");
				Map<String, Object> quoteInfo = (Map<String, Object>) object2.get("quoteInfo");
				Map<String, Object> object3 = (Map<String, Object>) quoteInfo.get("riskInfo");
				Map<String, Object> object4 = (Map<String, Object>) object3.get("riskDetails");
				List<Map<String, Object>> object5 = (List<Map<String, Object>>) object4.get("riskDetailsArray");
				updateQuery(engine, object1);
				
				object6=(Map<String, Object>) object5.get(0).get("coverages");
				premiumResponseArr=(Map<String, Object>) object5.get(0).get("premiumResponseArr");
				
				Map<String, Object> riskPrimaryInfo = (Map<String, Object>) object5.get(0).get("riskPrimaryInfo");
				exchangeRateStr = riskPrimaryInfo.get("riskCurrencyRate")==null?"1":riskPrimaryInfo.get("riskCurrencyRate").toString(); 
				
				charges=(List<Map<String, Object>>) quoteInfo.get("charges");
			}catch (Exception e) {
				e.printStackTrace();
			}
			List<Cover> retc=new ArrayList<Cover>();
			
			if(object6!=null && object6.get("mandatoryCoveragesArray")!=null) {
				List<Map<String, Object>> mandatory=(List<Map<String, Object>>) object6.get("mandatoryCoveragesArray");
				if(mandatory.size()>0) {
					CoverFromAzentoResponse c=new CoverFromAzentoResponse("B",exchangeRateStr);
					List<Cover> mainCover = mandatory.stream().map(c).collect(Collectors.toList());
					retc.addAll(mainCover);
				}
			}
			if(object6!=null && object6.get("selectedOptionalCoveragesArray")!=null) {
				List<Map<String, Object>> optional=(List<Map<String, Object>>) object6.get("selectedOptionalCoveragesArray");
				if(optional.size()>0) {
					CoverFromAzentoResponse c=new CoverFromAzentoResponse("O",exchangeRateStr);
					List<Cover> optinalC = optional.stream().map(c).collect(Collectors.toList());
					retc.addAll(optinalC);
				}
			}
			
			if(!retc.isEmpty()) {
				
				List<Tuple> itemids = workflow.getPremiaApiDropdownMaster(engine.getCompanyId(), "SubCoverYN");
				for(Tuple p:itemids) 
				{
					 String remarks = p.get("remarks").toString();
					 List<String> item = Arrays.asList(remarks.split(","));
					 List<Cover> collect = retc.stream()
				        .filter(i -> item.stream()
				                .anyMatch(j -> i.getCoverId().equals(j)))
				        .collect(Collectors.toList());
					 if(!collect.isEmpty()) {
						 
						 Cover base = collect.stream().filter(i->i!=null).collect(Collectors.toList()).get(0);
						 base.setIsSubCover("Y");
						 base.setSubCoverId("0");
						 
						 for (Cover i : collect) {
							 i.setSubCoverId(i.getCoverId());
							 i.setCoverId(base.getCoverId());
							 i.setSubCoverDesc(i.getCoverDesc());
							 i.setSubCoverDescLocal(i.getCoverDesc());
							 i.setSubCoverName(i.getCoverName());
							 i.setSubCoverNameLocal(i.getCoverName());
							 i.setIsselected("D".equals(i.getIsselected())?"Y":i.getIsselected());
						} 
							 
							 
							 
						 base.setSubcovers(collect);
						 retc.removeAll(collect);
						 retc.add(base);						 
					 }
				}
			}
			
			
			
			EserviceMotorDetailsSaveRes resp = new EserviceMotorDetailsSaveRes();
			
			resp.setCoverList(retc);
			resp.setResponse("Saved Successfully");
			resp.setRequestReferenceNo(engine.getRequestReferenceNo());
			// response.setCustomerReferenceNo(req.getCustomerReferenceNo());
			resp.setVehicleId(engine.getVehicleId());
			resp.setVdRefNo(engine.getVdRefNo());
			resp.setCdRefNo(engine.getCdRefNo());
			resp.setInsuranceId(engine.getCompanyId());
			resp.setSectionId(engine.getSectionId());
			resp.setCreatedBy(engine.getCreatedBy());
			resp.setProductId(engine.getProductId());
			resp.setLocationId(engine.getLocationId());
			resp.setMsrefno(engine.getMsrefno());
			resp.setUpdateas(null);
			resp.setUwList(null);
			resp.setReferals(masterreferral);
			fservice.saveFactorRateRequestDetails(resp);
			
			
			Optional<Cover> first = retc.stream().filter(i -> "B".equals(i.getCoverageType()) ).findFirst();
			Cover overall = first.get();
			if(premiumResponseArr!=null) {
				overall.setCoverName("Over all Premium");
				overall.setCoverDesc("Over all Premium");
				overall.setPremiumAfterDiscount(premiumResponseArr.get("policyPremium")==null?BigDecimal.ZERO:new BigDecimal(premiumResponseArr.get("policyPremium").toString()));
				overall.setPremiumAfterDiscountLC(premiumResponseArr.get("policyPremium")==null?BigDecimal.ZERO:new BigDecimal(premiumResponseArr.get("policyPremium").toString()));
				overall.setPremiumBeforeDiscount(premiumResponseArr.get("policyPremium")==null?BigDecimal.ZERO:new BigDecimal(premiumResponseArr.get("policyPremium").toString()));
				overall.setPremiumBeforeDiscountLC(premiumResponseArr.get("policyPremium")==null?BigDecimal.ZERO:new BigDecimal(premiumResponseArr.get("policyPremium").toString()));
				overall.setPremiumExcluedTax(premiumResponseArr.get("policyPremium")==null?BigDecimal.ZERO:new BigDecimal(premiumResponseArr.get("policyPremium").toString()));
				overall.setPremiumExcluedTaxLC(premiumResponseArr.get("policyPremium")==null?BigDecimal.ZERO:new BigDecimal(premiumResponseArr.get("policyPremium").toString()));
				overall.setPremiumIncludedTax(premiumResponseArr.get("policyPremiumIncVat")==null?BigDecimal.ZERO:new BigDecimal(premiumResponseArr.get("policyPremiumIncVat").toString()));
				overall.setPremiumIncludedTaxLC(premiumResponseArr.get("policyPremiumIncVat")==null?BigDecimal.ZERO:new BigDecimal(premiumResponseArr.get("policyPremiumIncVat").toString()));
				List<Tax> taxes=new ArrayList<Tax>();
				int taxid=1;
				for(Map<String, Object> t:charges) {
					Tax d=Tax.builder()
						 	.isTaxExempted("N")
						 	.taxAmount(t.get("chargeAmount")==null?BigDecimal.ZERO:new BigDecimal(t.get("chargeAmount").toString()))
						 	.taxDesc(t.get("chargeDesciption")==null?"":t.get("chargeDesciption").toString())
						 	.taxExemptCode(null)
						 	.taxExemptType(null)
						 	.endtTypeId(null)					
						 	.taxId(String.valueOf(taxid++))
						 	.taxRate(t.get("chargeRate")==null?0D:Double.parseDouble(t.get("chargeRate").toString()))
						 	.calcType(t.get("chargeRatePer")==null?"A":"100".equals(t.get("chargeRatePer").toString())?"P":"A")
							.regulatoryCode(t.get("chargeCode")==null?"":t.get("chargeCode").toString())
							.endtTypeCount(BigDecimal.ZERO)
							.dependentYn("N")
							.taxExemptedAllowed(t.get("taxExemptAllowYn")==null?"Y":t.get("taxExemptAllowYn").toString())
							.minimumTaxAmountLc(t.get("chargeAmount")==null?BigDecimal.ZERO:new BigDecimal(t.get("chargeAmount").toString()))
							.minimumTaxAmount(t.get("chargeAmount")==null?BigDecimal.ZERO:new BigDecimal(t.get("chargeAmount").toString()))
							.taxAmountLc(BigDecimal.ZERO)
							.taxFor("")
							.extend_Cust_tax("")
						 	.build();
					taxes.add(d); 
				}
				Double totaltax = taxes.stream().mapToDouble(i->i.getTaxAmount().doubleValue()).sum();
				String pattern = "#####0.000";
				DecimalFormat dcf = new DecimalFormat(pattern);
				
				overall.setPremiumIncludedTax(new BigDecimal(dcf.format(overall.getPremiumExcluedTax().add(new BigDecimal(totaltax)))));
				overall.setPremiumIncludedTaxLC(new BigDecimal(dcf.format(overall.getPremiumIncludedTax().multiply(overall.getExchangeRate()))));
				 
				overall.setTaxes(taxes);
				List<Cover> newOveral=new ArrayList<Cover>();
				newOveral.add(overall);
				
				resp.setCoverList(newOveral);
				resp.setResponse("Saved Successfully");
				resp.setRequestReferenceNo(engine.getRequestReferenceNo());
				// response.setCustomerReferenceNo(req.getCustomerReferenceNo());
				resp.setVehicleId("99999");
				resp.setVdRefNo(engine.getVdRefNo());
				resp.setCdRefNo(engine.getCdRefNo());
				resp.setInsuranceId(engine.getCompanyId());
				resp.setSectionId("99999");
				resp.setCreatedBy(engine.getCreatedBy());
				resp.setProductId(engine.getProductId());
				resp.setLocationId(engine.getLocationId());
				resp.setMsrefno(engine.getMsrefno());
				resp.setUpdateas(null);
				resp.setUwList(null);
				resp.setReferals(masterreferral);
				fservice.saveFactorRateRequestDetails(resp);
				


			}
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	@Transactional
	private void updateQuery(WorkEngine engine, Map<String, Object> object1) {
		try {
			//String updDatequery="UPDATE eservice_motor_details SET CORE_QUOTE_NO='"+object1.get("quoteNo").toString()+"',RISK_SGSID='"+object1.get("quoteId").toString()+"' WHERE request_reference_no='"+engine.getRequestReferenceNo()+"';";
			/*String updDatequery="UPDATE eservice_motor_details SET CORE_QUOTE_NO=?,RISK_SGSID=? WHERE request_reference_no=?";
			int update = template.update(updDatequery,object1.get("quoteNo").toString(),object1.get("quoteId").toString(),engine.getRequestReferenceNo());
			
			System.out.println("record upd"+update);*/
			List<EserviceMotorDetails> emds = eserMotorRepo.findByRequestReferenceNo(engine.getRequestReferenceNo());
			 for (EserviceMotorDetails i : emds) {
				 i.setCoreQuoteNo(object1.get("quoteNo").toString());
				 i.setRiskSgsId(object1.get("quoteId").toString());
			 }
			eserMotorRepo.saveAll(emds);
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
}
