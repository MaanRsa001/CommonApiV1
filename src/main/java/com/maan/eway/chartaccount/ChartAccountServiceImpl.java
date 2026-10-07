package com.maan.eway.chartaccount;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jfree.util.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.maan.eway.bean.CompanyProductMaster;
import com.maan.eway.bean.HomePositionMaster;
import com.maan.eway.bean.LoginUserInfo;
import com.maan.eway.bean.MultiplePolicyDrCrDetail;
import com.maan.eway.bean.PolicyCoverData;
import com.maan.eway.bean.PolicyDrcrDetail;
import com.maan.eway.bean.ProductTaxSetup;
import com.maan.eway.bean.SectionDataDetails;
import com.maan.eway.common.req.SequenceGenerateReq;
import com.maan.eway.common.res.CommonRes;
import com.maan.eway.common.service.impl.GenerateSeqNoServiceImpl;
import com.maan.eway.repository.CompanyProductMasterRepository;
import com.maan.eway.repository.EServiceSectionDetailsRepository;
import com.maan.eway.repository.HomePositionMasterRepository;
import com.maan.eway.repository.LoginUserInfoRepository;
import com.maan.eway.repository.MultiplePolicyDrCrDetailRepository;
import com.maan.eway.repository.PolicyCoverDataRepository;
import com.maan.eway.repository.PolicyDrcrDetailRepository;
import com.maan.eway.repository.SectionDataDetailsRepository;

@Service
public class ChartAccountServiceImpl implements ChartAccountService {
	
	Logger log = LogManager.getLogger(ChartAccountServiceImpl.class);
	
	
	@Autowired
	private HomePositionMasterRepository hpmRepo;
	
	@Autowired
	private ChartParentMasterRepository chatParentMasterRepo;
	
	@Autowired
	private PolicyDrcrDetailRepository policyDRCRRepo;
	
	@Autowired
	private JpqlQueryServiceImpl jpqlQuery;
	
	@Autowired
	private GenerateSeqNoServiceImpl genNo;
	
	@Autowired
	private SectionDataDetailsRepository secRepo ;
	
	@Autowired
	private EServiceSectionDetailsRepository serviceRepo;

	@Autowired
	private MultiplePolicyDrCrDetailRepository mpcrdrRepo;
	
	@Autowired
	private PolicyCoverDataRepository pccdRepo;
	
	@Autowired 
	private LoginUserInfoRepository loginUserInfoRepository;
	
	@Autowired 
	private CompanyProductMasterRepository companyProductMasterRepo;
	
	Gson printReq = new Gson();

	@Override
	public CommonRes drcrEntry(ChartAccountRequest req) {
		CommonRes response = new CommonRes();
		try {
			
			log.info("drcrEntry request : "+printReq.toJson(req));
			
			if("N".equals(req.getDiscountYn())) {
				
				Integer del_count =chatParentMasterRepo.deleteDrCrDataByQuoteNo(req.getQuoteNo());
				Integer count_=chatParentMasterRepo.deleteMultipleDrCrDataByQuoteNo(req.getQuoteNo());
				
				log.info("policyDRCRRepo.deleteDrCrDataByQuoteNo(req.getQuoteNo()) count :: "+del_count);
				log.info("policyDRCRRepo.deleteMultipleDrCrDataByQuoteNo(req.getQuoteNo()) count :: "+count_);

				String quoteNo =req.getQuoteNo();
				HomePositionMaster hpm =hpmRepo.findByQuoteNo(quoteNo);
				Integer companyId =Integer.valueOf(hpm.getCompanyId());
				Integer productId =hpm.getProductId();
				//Integer sectionId =hpm.getSectionId();
				List<SectionDataDetails> sections = secRepo.findByQuoteNo(quoteNo);
				List<Integer> sectionIds = new ArrayList<Integer>(); 
				sections.forEach( o -> {
					sectionIds.add(Integer.valueOf(o.getSectionId()));
				}); 
				
				sectionIds.add(99999);
				BigDecimal brokerCommision =hpm.getCommissionPercentage() != null ? hpm.getCommissionPercentage() : BigDecimal.ZERO;
				String taxFor ="";
				String endorsmentType =StringUtils.isBlank(hpm.getEndtTypeId())?"":hpm.getEndtTypeId();
				
				BigDecimal endtPremium =StringUtils.isNotBlank(endorsmentType)?hpm.getEndtPremium():new BigDecimal(0);
				
				List<ChartParentMaster> cpm =jpqlQuery.getChartParentMasterDetails(companyId);
				List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
				
				// Generate Policy Seq
				SequenceGenerateReq generateSeqReq = new SequenceGenerateReq();
			 	generateSeqReq.setInsuranceId(hpm.getCompanyId());  
			 	generateSeqReq.setProductId(hpm.getProductId().toString());
			 	List<String> params = new ArrayList<String>();
			 	params.add(hpm.getQuoteNo());
			 	generateSeqReq.setType("7");
			 	generateSeqReq.setParams(params);
			 	String crnumber =  genNo.generateSeqCall(generateSeqReq);//genNo.generateCreditNo(hpm.getBrokerCode());
				
				generateSeqReq.setType("6");
			 	generateSeqReq.setTypeDesc("DEBIT_NO");
			 	generateSeqReq.setParams(params);
			 	String drnumber =  genNo.generateSeqCall(generateSeqReq);//genNo.generateDebitNo(hpm.getBrokerCode());
				
				int index =1;
				
				List<PolicyCoverData> pcd_list = pccdRepo.findByQuoteNo(hpm.getQuoteNo());
	
//				CompanyProductMaster comp =
//					    companyProductMasterRepo.findByProductIdAndCompanyIdAndStatus(
//					        productId,
//					        hpm.getCompanyId(),
//					        "Y"
//					    );
				CompanyProductMaster comp =
					    companyProductMasterRepo.findTopByProductIdAndCompanyIdAndStatusOrderByAmendIdDesc(
					        productId,
					        hpm.getCompanyId(),
					        "Y"
					    );
				
				if (comp == null) {
				    throw new IllegalStateException(
				        "CompanyProductMaster not found for companyId="
				        + hpm.getCompanyId() + ", productId=" + productId
				    );
				}
				
				String motorYn = comp.getMotorYn();
				
				String vehicle_type;

				if ("M".equalsIgnoreCase(motorYn)) {

				    long riskCount = pcd_list.stream()
				        .map(PolicyCoverData::getVehicleId)
				        .filter(id -> id != null && id != 99999)
				        .distinct()
				        .count();

				    vehicle_type = riskCount > 1 ? "M" : "S";

				} else {
				    
				    long locationCount = pcd_list.stream()
				        .map(PolicyCoverData::getLocationId)
				        .filter(Objects::nonNull)
				        .distinct()
				        .count();

				    vehicle_type = locationCount > 1 ? "M" : "S";
				}

				
				log.info("drcrEntry || quoteno = "+req.getQuoteNo()+" vehicle_type = "+vehicle_type+" ");
				mpcrdrRepo.deleteByQuoteNo(req.getQuoteNo());
				
				
				for(ChartParentMaster c : cpm) {
					
					List<ChartAccountChildMaster> charAccount =jpqlQuery.getChildChartAccountData(companyId,productId,sectionIds,c);
					
					BigDecimal premiumFc =null;
					String documentType ="";
					String docId ="";
					String  documentNo ="";
					BigDecimal premiumFcWithT=null;
					String drcrFlag ="";
					String narration="";
					String status ="Y";
					Boolean bokerCommiCheck=false;
				
					List<Integer> coverIds =charAccount.stream().map(c1 -> c1.getId().getCoverId()).collect(Collectors.toList());
		 
					if("DR".equalsIgnoreCase(c.getAccountType()) && "C".equalsIgnoreCase(c.getCharactersticType())) {
								
												
						 List<PolicyCoverData> pcdList =jpqlQuery.getPolicyCoverDataPremium(quoteNo, coverIds);
						 
						 boolean hasOverallRow = pcdList.stream()
							        .anyMatch(p -> p.getVehicleId()==99999 && p.getSectionId()==99999);
						
						 
						 if(StringUtils.isBlank(endorsmentType)) {
						 
							 if (hasOverallRow) {
								    premiumFc = pcdList.stream()
								            .filter(p -> p.getTaxId()==0)
								            .filter(p -> p.getDiscLoadId()==0)
								            .filter(p -> p.getVehicleId()==99999 && p.getSectionId()==99999)
								            .map(p -> p.getPremiumExcludedTaxFc())
								            .reduce(new BigDecimal(0), (a,b) -> a==null?BigDecimal.ZERO: a.add(b==null?BigDecimal.ZERO:b)).abs();
								} else {
								    premiumFc = pcdList.stream().filter(p->p.getTaxId()==0 )
								             .filter(p ->p.getDiscLoadId()==0)
								             .filter(p ->p.getVehicleId()!=99999)
								             .map(p ->p.getPremiumExcludedTaxFc())
								             .reduce(new BigDecimal(0), (a,b) ->a==null?BigDecimal.ZERO: a.add( b==null?BigDecimal.ZERO:b)).abs();
								}

						 }else {
							 
							 						 
							 premiumFc =pcdList.stream().filter(p->p.getTaxId()==0 )
									 .filter(p ->p.getDiscLoadId()!=0)
									 .filter(p ->p.getVehicleId()!=99999)
									 .filter(p ->p.getCoverageType().equals("E"))
									 .map(p ->p.getPremiumExcludedTaxFc())
									 .reduce(new BigDecimal(0), (a,b) ->a==null?BigDecimal.ZERO: a.add( b==null?BigDecimal.ZERO:b)).abs();
							 
							
						 }
						 
						 if(endtPremium.doubleValue()<0) {
							 drcrFlag="CR";
							 documentType="C";
							 documentNo =crnumber;
	
						 }else {
							 documentType="C";
							 documentNo =drnumber;
	
						 }
						 	
						 docId=hpm.getCustomerId();
						 narration=c.getChartAccountDesc();
						 bokerCommiCheck=true;
						
						 
						 
						 if("M".equals(vehicle_type)) {
							 Map<Integer,List<PolicyCoverData>> group =pcdList.stream()
									 .filter(p ->p.getVehicleId()!=99999)
									 .collect(Collectors.groupingBy(p ->p.getVehicleId()));
							 insertMultiplePolicyCrDr_1(vehicle_type,group,hpm,c,drcrFlag,documentType,documentNo,docId,narration,req); 
						 }
						 
					}else if("DR".equalsIgnoreCase(c.getAccountType()) && "T".equalsIgnoreCase(c.getCharactersticType())) {
						
						
						 List<PolicyCoverData> pcdList =jpqlQuery.getPolicyCoverDataTax(quoteNo, coverIds);
						 
						 boolean hasOverallRow = pcdList.stream()
							        .anyMatch(p -> p.getVehicleId()==99999 && p.getSectionId()==99999);
						 
						 Optional<PolicyCoverData> pcd =pcdList.stream().filter(p->p.getTaxId()!=0 )
							 .filter(p ->p.getDiscLoadId()==0)
							 .findFirst();
						 
						 narration =pcd.isPresent()?pcd.get().getTaxCalcType().equals("P")?c.getChartAccountDesc() +" @ "+pcd.get().getTaxRate().toString()+"%"
							 	 :c.getChartAccountDesc() :"";
								
						 
						 if(StringUtils.isBlank(endorsmentType)) {
						 
							 if (hasOverallRow) {
								    premiumFc = pcdList.stream()
								            .filter(p -> p.getTaxId()!=0)
								            .filter(p -> p.getVehicleId()==99999 && p.getSectionId()==99999)
								            .filter(p -> p.getDiscLoadId()==0)
								            .map(p -> p.getTaxAmount())
								            .reduce(new BigDecimal(0), (a,b) -> a==null?BigDecimal.ZERO: a.add(b==null?BigDecimal.ZERO:b));

								    status = pcdList.stream().filter(p -> p.getTaxId()!=0)
								            .filter(p -> p.getVehicleId()==99999 && p.getSectionId()==99999)
								            .filter(p -> p.getDiscLoadId()==0)
								            .filter(p -> "Y".equalsIgnoreCase(p.getIsTaxExtempted()))
								            .map(m -> "CV")
								            .findFirst().orElse("Y");
								} else {
								    premiumFc =pcdList.stream().filter(p ->p.getTaxId()!=0)
								             .filter(p ->p.getVehicleId()!=99999)
								             .filter(p ->p.getDiscLoadId()==0)
								             .map(p ->p.getTaxAmount())
								             .reduce(new BigDecimal(0),(a,b) ->a==null?BigDecimal.ZERO: a.add( b==null?BigDecimal.ZERO:b));

								    status = pcdList.stream().filter(p ->p.getTaxId()!=0)
								             .filter(p ->p.getVehicleId()!=99999)
								             .filter(p ->p.getDiscLoadId()==0)
								             .filter(p -> "Y".equalsIgnoreCase(p.getIsTaxExtempted()))
								             .map(m -> "CV")
								             .findFirst().orElse("Y");
								}
							 
							 
						 }else {
							 
							 premiumFc=pcdList.stream().filter(p ->p.getTaxId()!=0)
									 .filter(p ->p.getVehicleId()!=99999)
									 .filter(p ->p.getDiscLoadId()!=0)
									 .filter(p ->p.getCoverageType().equals("T"))
									 .map(p ->p.getTaxAmount())
									 .reduce(new BigDecimal(0), (a,b) ->a==null?BigDecimal.ZERO: a.add( b==null?BigDecimal.ZERO:b)).abs();
						 }
						
						 if(endtPremium.doubleValue()<0) {
							 drcrFlag="CR";
							 documentType="C";
							 documentNo =crnumber;
						 }else {
							 documentType="C";
							documentNo =drnumber;
	
						 }
						 
						docId=hpm.getCustomerId();
						bokerCommiCheck=true;
						
						
						
						 
						 if("M".equals(vehicle_type)) {
							 Map<Integer,List<PolicyCoverData>> group =pcdList.stream()
									 .filter(p ->p.getVehicleId()!=99999)
									 .collect(Collectors.groupingBy(p ->p.getVehicleId()));
							 insertMultiplePolicyCrDr_2(vehicle_type,group,hpm,c,drcrFlag,documentType,documentNo,docId,narration,req); 
						 }
						
					}else if("CR".equalsIgnoreCase(c.getAccountType()) && brokerCommision.doubleValue()>0 &&
							"C".equalsIgnoreCase(c.getCharactersticType())) {
						
						
						 List<PolicyCoverData> pcdList =jpqlQuery.getPolicyCoverDataPremium(quoteNo, coverIds);
						 boolean hasOverallRow = pcdList.stream()
							        .anyMatch(p -> p.getVehicleId()==99999 && p.getSectionId()==99999);
							
						 if(StringUtils.isBlank(endorsmentType)) {
							 
							 if (hasOverallRow) {
								    premiumFcWithT = pcdList.stream()
								            .filter(p -> p.getTaxId()==0)
								            .filter(p -> p.getVehicleId()==99999 && p.getSectionId()==99999)
								            .filter(p -> p.getDiscLoadId()==0)
								            .map(p -> p.getPremiumExcludedTaxFc())
								            .reduce(new BigDecimal(0), (a,b) -> a==null?BigDecimal.ZERO: a.add(b==null?BigDecimal.ZERO:b));
								} else {
								    premiumFcWithT =pcdList.stream().filter(p->p.getTaxId()==0 )
								             .filter(p ->p.getVehicleId()!=99999)
								             .filter(p ->p.getDiscLoadId()==0)
								             .map(p ->p.getPremiumExcludedTaxFc())
								            .reduce(new BigDecimal(0),(a,b) -> a==null?BigDecimal.ZERO: a.add( b==null?BigDecimal.ZERO:b));
								}
									 
						 }else {
							 premiumFcWithT =pcdList.stream().filter(p->p.getTaxId()==0 )
									 .filter(p ->p.getVehicleId()!=99999)
									 .filter(p ->p.getDiscLoadId()!=0)
									 .filter(p ->p.getCoverageType().equals("E"))
									 .map(p ->p.getPremiumExcludedTaxFc())
									 .reduce(new BigDecimal(0), (a,b) -> a==null?BigDecimal.ZERO: a.add( b==null?BigDecimal.ZERO:b)).abs();
									 
						 }
						 
						 if(endtPremium.doubleValue()<0) {
							 drcrFlag="DR";
							 documentType="B";
							 documentNo =drnumber;
	
						 }else {
							 documentType="B";
							 documentNo =crnumber;
						 }
						 	 
						premiumFc =premiumFcWithT.multiply(brokerCommision).divide(new BigDecimal(100)).setScale(2,BigDecimal.ROUND_HALF_EVEN);
						hpm.setCommission(premiumFc);
						hpmRepo.saveAndFlush(hpm); 
						docId=hpm.getLoginId();
						narration=c.getChartAccountDesc() +" @ "+new DecimalFormat("#,##0.0").format(brokerCommision.doubleValue())+"%";
						bokerCommiCheck=true;
						
						 if("M".equals(vehicle_type)) {
							 Map<Integer,List<PolicyCoverData>> group =pcdList.stream()
									 .filter(p ->p.getVehicleId()!=99999)
									 .collect(Collectors.groupingBy(p ->p.getVehicleId()));
							 insertMultiplePolicyCrDr_3(vehicle_type,group,hpm,c,drcrFlag,documentType,documentNo,docId,narration,req); 
						 }
				
						
					}else if("CR".equalsIgnoreCase(c.getAccountType()) && brokerCommision.doubleValue()>0
							&& "T".equalsIgnoreCase(c.getCharactersticType())) {
													
						
						
						if(endtPremium.doubleValue()<0) {
							drcrFlag="DR";
							documentType="B";
							documentNo =drnumber;
							taxFor="ER";
						}else if(endtPremium.doubleValue()>0) {
							 documentType="B";
							 documentNo =crnumber;
							 taxFor="EC";
						 } else {
							 taxFor="NB";
							 documentType="B";
							 documentNo =crnumber;
						 }
						
						LoginUserInfo loginUserInfo = loginUserInfoRepository.findByLoginId(hpm.getLoginId());

						String brokerTaxExempted = loginUserInfo.getTaxExemptedYn();
						
						if (!"Y".equalsIgnoreCase(brokerTaxExempted)) {
						ProductTaxSetup taxProductTaxSetup =jpqlQuery.getProductTaxSetup(companyId,coverIds,productId,hpm.getBranchCode(),taxFor); 
						 
						BigDecimal taxPer =new BigDecimal(taxProductTaxSetup.getValue());
						premiumFc =hpm.getCommission().multiply(taxPer).divide(new BigDecimal(100)).setScale(2,BigDecimal.ROUND_HALF_EVEN);
						docId=hpm.getLoginId();
						bokerCommiCheck=true;
						narration =c.getChartAccountDesc() +" @ "+new DecimalFormat("#,##0.0").format(taxPer.doubleValue())+"%";
							 	
						if("M".equals(vehicle_type))	
							insertMultiplePolicyCrDr_4(vehicle_type,taxProductTaxSetup,hpm,c,drcrFlag,documentType,documentNo,docId,narration,req); 
						}else {
							log.info("Broker Tax Exempted. Tax calculation skipped for QuoteNo: " + hpm.getQuoteNo());
						}
						
					}
					
					if(bokerCommiCheck && !"OTHERS".equalsIgnoreCase(c.getAccountType())) {
					
						HashMap<String,Object> saveReq =new HashMap<>();
						saveReq.put("PolicyNo", StringUtils.isBlank(req.getPolicyNo())?hpm.getPolicyNo():req.getPolicyNo());
						saveReq.put("QuoteNo", hpm.getQuoteNo());
						saveReq.put("ChargeCode", c.getChartAccountCode());
						saveReq.put("ChargeId", index);
						saveReq.put("DocNo", documentNo);
						saveReq.put("DocType", documentType);
						saveReq.put("DocId", docId);
						saveReq.put("CompanyId", companyId);
						saveReq.put("ProductId", productId);
						saveReq.put("BranchCode", hpm.getBranchCode());
						saveReq.put("DRCRFlag", StringUtils.isBlank(drcrFlag)?c.getAccountType():drcrFlag);
						saveReq.put("AmountFC",premiumFc);
						saveReq.put("ChargeAccountDesc", c.getChartAccountDesc());
						saveReq.put("Narration", narration);
						saveReq.put("DisplayOrder",c.getDisplayOrder());
						saveReq.put("vehicle_type", vehicle_type);
						saveReq.put("status", status);
						list.add(saveReq);
						
						index++;
					}
					
				}
				
				List<PolicyDrcrDetail> drcrList =saveDRCR(list);
			
				
				if("M".equals(vehicle_type)) {
				
					List<PolicyCoverData> pcdData =pccdRepo.findByQuoteNo(req.getQuoteNo());
					List<MultiplePolicyDrCrDetail> mpcdData =mpcrdrRepo.findByQuoteNo(req.getQuoteNo());
					
					// VALIDATE PREMIUM AND UPDATE ERROR BLOCK
					Map<Integer,List<PolicyCoverData>> pcd_group =pcdData.stream().filter(p ->p.getVehicleId()!=99999)
							.collect(Collectors.groupingBy(p ->p.getVehicleId()));
					Map<Integer,List<MultiplePolicyDrCrDetail>> mcd_group =mpcdData.stream().collect(Collectors.groupingBy(p ->p.getRiskId()));
					
					log.info("quote_no : "+req.getQuoteNo()+"|| MultiplePolicyDrCrDetail groubby size : "+mcd_group.size() +" || PolicyCoverData groupby size : "+pcd_group.size()+"||  is Equal : "+(mcd_group.size()==pcd_group.size()));
	
					
					BigDecimal coverdata_premium =BigDecimal.ZERO;
					BigDecimal vat_premium =BigDecimal.ZERO;				
					BigDecimal drcr_premium =BigDecimal.ZERO;
					BigDecimal drcr_vat =BigDecimal.ZERO;
	
	
					if(pcd_group.size()!=mcd_group.size()) {
						
						Integer count =mpcrdrRepo.updateDrCrErrorStatus(req.getQuoteNo(),"Vehicle count mismatched ");
						log.info("updateDrCrErrorStatus vehicle count :: quote_no = "+req.getQuoteNo()+" || result = "+count+"");
						
					}else if(pcd_group.size()==mcd_group.size()) {
					
	
						//policy cover data premium & vat
						 if(StringUtils.isBlank(endorsmentType)) {
							 coverdata_premium =pcdData.stream().filter(p->p.getTaxId()==0 ).filter(p ->p.getDiscLoadId()==0).filter(p ->p.getVehicleId()!=99999)
									 .map(p ->p.getPremiumExcludedTaxFc()).reduce(new BigDecimal(0), (a,b) ->a==null?BigDecimal.ZERO: a.add( b==null?BigDecimal.ZERO:b)).abs();
						
							// =======================
							// POLICY TAX (SOURCE OF TRUTH)
							// =======================
							Map<Integer, BigDecimal> policyTaxByRisk =
							    pcdData.stream()
							        .filter(p -> p.getTaxId() != 0)
							        .filter(p -> p.getVehicleId() != 99999)
							        .collect(
							            Collectors.groupingBy(
							                PolicyCoverData::getVehicleId,
							                Collectors.reducing(
							                    BigDecimal.ZERO,
							                    PolicyCoverData::getTaxAmount,
							                    BigDecimal::add
							                )
							            )
							        );

							// Total policy tax (all risks)
							vat_premium =
							    policyTaxByRisk.values().stream()
							        .reduce(BigDecimal.ZERO, BigDecimal::add);

							 
//							vat_premium =
//									    pcdData.stream()
//									        .filter(p -> p.getTaxId() != 0)
//									        .map(PolicyCoverData::getTaxAmount)
//									        .reduce(BigDecimal.ZERO, BigDecimal::add);
						 
						 }else { 						 
							 coverdata_premium =pcdData.stream().filter(p->p.getTaxId()==0 ).filter(p ->p.getDiscLoadId()!=0).filter(p ->p.getVehicleId()!=99999).filter(p ->p.getCoverageType().equals("E"))
									 .map(p ->p.getPremiumExcludedTaxFc()) .reduce(new BigDecimal(0), (a,b) ->a==null?BigDecimal.ZERO: a.add( b==null?BigDecimal.ZERO:b)).abs();
						
							 vat_premium=pcdData.stream().filter(p ->p.getTaxId()!=0).filter(p ->p.getVehicleId()!=99999) .filter(p ->p.getDiscLoadId()!=0).filter(p ->p.getCoverageType().equals("T"))
									  .map(p ->p.getTaxAmount()).reduce(new BigDecimal(0), (a,b) ->a==null?BigDecimal.ZERO: a.add( b==null?BigDecimal.ZERO:b)).abs();		
			
						 }
						 	
						 // policy drcr premium & vat
						 drcr_premium = mpcdData.stream()
								    .filter(p -> "P".equals(p.getType()))
								    .map(p -> p.getAmountFc())
								    .filter(Objects::nonNull)
								    .reduce(BigDecimal.ZERO, BigDecimal::add)
								    .abs();
						 
						// =======================
						// DRCR TAX (POSTED VALUES)
						// =======================
						Map<Integer, BigDecimal> drcrTaxByRisk =
						    mpcdData.stream()
						        .filter(p -> "V".equals(p.getType()))   // only tax entries
						        .collect(
						            Collectors.groupingBy(
						                MultiplePolicyDrCrDetail::getRiskId,
						                Collectors.reducing(
						                    BigDecimal.ZERO,
						                    MultiplePolicyDrCrDetail::getAmountFc,
						                    BigDecimal::add
						                )
						            )
						        );

						// Total DRCR tax (all risks)
						 drcr_vat =
						    drcrTaxByRisk.values().stream()
						        .reduce(BigDecimal.ZERO, BigDecimal::add);


//								drcr_vat = mpcdData.stream()
//								    .filter(p -> "V".equals(p.getType()))
//								    .map(p -> p.getAmountFc())
//								    .filter(Objects::nonNull)
//								    .reduce(BigDecimal.ZERO, BigDecimal::add)
//								    .abs();

								
						 
							log.info( "quote_no = "+req.getQuoteNo()+" || Total Premium of Policy cover data = "+coverdata_premium+" || Total premium of policy crdr ="+drcr_premium+" ");
	
							log.info( "quote_no = "+req.getQuoteNo()+" || Total vat of Policy cover data = "+vat_premium+" || Total vat of policy crdr ="+drcr_vat+" ");
	
						 String errorMesaage ="";
						 
						 if(!coverdata_premium.equals(drcr_premium)) {
							 	errorMesaage ="TOTAL PREMIUM IS MISMATCHED || policy_coverdata_premimum = "+coverdata_premium+" && policy_drcr_premium = "+drcr_premium+" ";
	
							 	Integer count =mpcrdrRepo.updateDrCrErrorStatus(req.getQuoteNo(),"Premium mismatched ");
								
								log.info("updateDrCrErrorStatus premium mismatched :: quote_no = "+req.getQuoteNo()+" || result = "+count+"");
							
						 }else if(!vat_premium.equals(drcr_vat)) {
							    errorMesaage =" VEHICLE BASED VAT IS MISMATCHED || policy_coverdata_vat = "+vat_premium+" && policy_drcr_vat = "+drcr_vat+" ";
							 
							    Integer count =mpcrdrRepo.updateDrCrErrorStatus(req.getQuoteNo(),"Premium vat mismatched ");
								
								log.info("updateDrCrErrorStatus premium vat mismatched :: quote_no = "+req.getQuoteNo()+" || result = "+count+"");
							
						 }else {
						 
						 
							 	//log.info("quote_no : "+req.getQuoteNo()+" || total_policy_coverdata_premium = "+coverdata_premium+" & total_drcr_premium ="+drcr_premium+" || Result is "+coverdata_premium.equals(drcr_premium)+"");							 
								 
								//log.info("quote_no : "+req.getQuoteNo()+" || total_policy_coverdata_vat = "+vat_premium+" & total_drcr_vat ="+drcr_vat+" || Result is "+vat_premium.equals(drcr_vat)+"");							 
								
								for(Map.Entry<Integer, List<PolicyCoverData>> entry: pcd_group.entrySet()) {
									
									Integer key =entry.getKey();
									
									List<PolicyCoverData> pcd =entry.getValue();
									
									//premium
									 if(StringUtils.isBlank(endorsmentType)) {
										 coverdata_premium =pcd.stream().filter(p->p.getTaxId()==0 ).filter(p ->p.getDiscLoadId()==0).filter(p ->p.getVehicleId()!=99999)
												 .map(p ->p.getPremiumExcludedTaxFc()).reduce(new BigDecimal(0), (a,b) ->a==null?BigDecimal.ZERO: a.add( b==null?BigDecimal.ZERO:b)).abs();
									
										 vat_premium =pcd.stream().filter(p ->p.getTaxId()!=0).filter(p ->p.getVehicleId()!=99999).filter(p ->p.getDiscLoadId()==0).map(p ->p.getTaxAmount())
												 .reduce(new BigDecimal(0),(a,b) ->a==null?BigDecimal.ZERO: a.add( b==null?BigDecimal.ZERO:b));
									 
									 }else { 						 
										 coverdata_premium =pcd.stream().filter(p->p.getTaxId()==0 ).filter(p ->p.getDiscLoadId()!=0).filter(p ->p.getVehicleId()!=99999).filter(p ->p.getCoverageType().equals("E"))
												 .map(p ->p.getPremiumExcludedTaxFc()) .reduce(new BigDecimal(0), (a,b) ->a==null?BigDecimal.ZERO: a.add( b==null?BigDecimal.ZERO:b)).abs();
									
										 vat_premium=pcd.stream().filter(p ->p.getTaxId()!=0).filter(p ->p.getVehicleId()!=99999) .filter(p ->p.getDiscLoadId()!=0).filter(p ->p.getCoverageType().equals("T"))
												  .map(p ->p.getTaxAmount()).reduce(new BigDecimal(0), (a,b) ->a==null?BigDecimal.ZERO: a.add( b==null?BigDecimal.ZERO:b)).abs();		
						
									 }
									 	
									 
									 List<MultiplePolicyDrCrDetail> single_vehicle =mcd_group.get(key);
									 
									 drcr_premium= single_vehicle.stream().filter(p ->p.getType().equals("P")).map(p ->p.getAmountFc())
									 	.reduce(BigDecimal.ZERO ,(a,b)->a.add(b)).abs();
									
									 drcr_vat= single_vehicle.stream().filter(p ->p.getType().equals("V")).map(p ->p.getAmountFc())
											 	.reduce(BigDecimal.ZERO ,(a,b)->a.add(b)).abs();
									
									 Boolean premium_status =coverdata_premium.equals(drcr_premium)?true:false;
									 Boolean vat_status =vat_premium.equals(drcr_vat)?true:false;
									 
									 log.info("quote_no = "+req.getQuoteNo()+" || vehicle_id = "+key+"|| premium_status is : "+premium_status+" || vat_status is : "+vat_status+"");
										
									 log.info("quote_no = "+req.getQuoteNo()+" || vehicle_id = "+key+"|| policy_coverdata_premium = "+coverdata_premium+" || policy_crdr_premium = "+drcr_premium+"");
	
									 log.info("quote_no = "+req.getQuoteNo()+" || vehicle_id = "+key+"|| policy_coverdata_vat = "+vat_premium+" || policy_crdr_vat = "+drcr_vat+"");
	
									 Integer count =0;
									 
									 								 
									 if(!premium_status) {
										
										 errorMesaage =" VEHICLE BASED PREMIUM IS MISMATCHED || policy_coverdata_premimum = "+coverdata_premium+" && policy_drcr_premium = "+drcr_premium+" ";
										 
										 count =mpcrdrRepo.updateDrCrErrorStatus(req.getQuoteNo(),errorMesaage,key.toString());
	
										 log.info("quote_no = "+req.getQuoteNo()+" || vehicle_id = "+key+" || Premium mismatched ",key.toString()+" || update count : "+count+"");
	
									 }else if(!vat_status) {
										
										 errorMesaage =" VEHICLE BASED VAT IS MISMATCHED || policy_coverdata_vat = "+vat_premium+" && policy_drcr_vat = "+drcr_vat+" ";
	
										 count =mpcrdrRepo.updateDrCrErrorStatus(req.getQuoteNo(),errorMesaage,key.toString());
	
										 log.info("quote_no = "+req.getQuoteNo()+" || vehicle_id = "+key+" || Premium mismatched ",key.toString()+" || update count : "+count+"");
	
									 }
										 
	
	
								}
								 
								
							 }
									
					}
					
					List<MultiplePolicyDrCrDetail> mpdcd = mpcrdrRepo.findByQuoteNoAndStatusIgnoreCase(quoteNo,"E");
					
					if(mpdcd.size()<=0) {
						response.setCommonResponse(drcrList);
						response.setMessage("Success");
					}else {
						response.setCommonResponse(Collections.EMPTY_LIST);
						response.setMessage("CRDR Premium does not calculated or something went wrong");
					}
				}else {
					response.setCommonResponse(drcrList);
					response.setMessage("Success");
				}
				log.info("FINAL PREMIUM DATA || "+printReq.toJson(response));

			}else if("Y".equals(req.getDiscountYn())) {
				
				
//				List<EserviceSectionDetails> sectionList = serviceRepo.findByRequestReferenceNo(req.getRequestRefNo());
//				
//				List<Integer> sectionId =sectionList.stream().map(p ->Integer.valueOf(p.getSectionId()))
//						.collect(Collectors.toList());
//				
//				
//				List<ChartAccountChildMaster> childMaster =jpqlQuery.getChildChartAccountData(req.getCompanyId(), req.getProductId(), sectionId, req.getChartId());
//				
//				List<Integer> coverIds =childMaster.stream().map(p ->p.getId().getCoverId())
//						.collect(Collectors.toList());
//				
//				BigDecimal premium =jpqlQuery.getPremium(req.getRequestRefNo(),coverIds,req);
//				
//				response.setCommonResponse(premium);
//				response.setMessage("Success");
				
				BigDecimal premium = jpqlQuery.getPremium(req.getRequestRefNo(), req);

			    response.setCommonResponse(premium);
			    response.setMessage("Success");
				
			}
			
		}catch (Exception e) {
			e.printStackTrace();
			log.error(e);
		}
		return response;
	}

	private void insertMultiplePolicyCrDr_4(String vehicle_type, ProductTaxSetup taxProductTaxSetup,
			HomePositionMaster hpm, ChartParentMaster c, String drcrFlag, String documentType, String documentNo,
			String docId, String narration, ChartAccountRequest req) {
		try {
			
			
			List<SectionDataDetails> sdd_list =	secRepo.findByQuoteNo(hpm.getQuoteNo());
			
			BigDecimal tax_percentage =taxProductTaxSetup.getValue()==null?BigDecimal.ZERO : new BigDecimal(taxProductTaxSetup.getValue());
			
			for(SectionDataDetails sdd : sdd_list ) {
				
				Long count =mpcrdrRepo.countByCompanyIdAndProductIdAndQuoteNo(hpm.getCompanyId(), hpm.getProductId().toString(), hpm.getQuoteNo());
				Long chgId =count<=0 ?1 :count + 1;
				
				
				BigDecimal premiumFc = sdd.getCommissionAmount() != null ? sdd.getCommissionAmount()
						.multiply(tax_percentage).divide(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_EVEN)
						: BigDecimal.ZERO;
				
				
				MultiplePolicyDrCrDetail mpcd = MultiplePolicyDrCrDetail.builder()
						 .policyNo(StringUtils.isBlank(req.getPolicyNo())?hpm.getPolicyNo():req.getPolicyNo())
						 .quoteNo(StringUtils.isBlank(req.getQuoteNo())?hpm.getQuoteNo():req.getQuoteNo())
						 .chgId(new BigDecimal(chgId))
						 .companyId(hpm.getCompanyId())
						 .productId(hpm.getProductId().toString())
						 .branchCode(hpm.getBranchCode())
						 .riskId(sdd.getRiskId())
						 .chargeCode(new BigDecimal(c.getChartAccountCode()))
						 .docNo(documentNo)
						 .docType(documentType)
						 .docId(docId)
						 .drcrFlag(StringUtils.isBlank(drcrFlag)?c.getAccountType():drcrFlag)
						 .amountFc(premiumFc)
						 .chargeAccountDesc(c.getChartAccountDesc())
						 .narration(narration)
						 .displayOrder(c.getDisplayOrder())
						 .status("Y")
						 .type("BV")
						 .entryDate(new Date())
						 .build();
				
				 mpcrdrRepo.save(mpcd);
			}
		
		}catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	private void insertMultiplePolicyCrDr_1(String vehicle_type, Map<Integer, List<PolicyCoverData>> groupData,
			HomePositionMaster hpm, ChartParentMaster c, String drcrFlag, String documentType, String documentNo, String docId,
			String narration, ChartAccountRequest req) {
		try {
			String endorsmentType =StringUtils.isBlank(hpm.getEndtTypeId())?"":hpm.getEndtTypeId();

			for(Map.Entry<Integer, List<PolicyCoverData>> entry :groupData.entrySet()) {
				
				Long count =mpcrdrRepo.countByCompanyIdAndProductIdAndQuoteNo(hpm.getCompanyId(), hpm.getProductId().toString(), hpm.getQuoteNo());
				Long chgId =count<=0 ?1 :count + 1;
			

				Integer riskId =entry.getKey();
				
				List<PolicyCoverData> data = entry.getValue();
				
				BigDecimal premiumFc =BigDecimal.ZERO;
				 
				 if(StringUtils.isBlank(endorsmentType)) {
				 
					 premiumFc =data.stream().filter(p->p.getTaxId()==0 )
							 .filter(p ->p.getDiscLoadId()==0)
							 .map(p ->p.getPremiumExcludedTaxFc())
							 .reduce(new BigDecimal(0), (a,b) ->a==null?BigDecimal.ZERO: a.add( b==null?BigDecimal.ZERO:b)).abs();
							 
			
				 }else {
					 
					 						 
					 premiumFc =data.stream().filter(p->p.getTaxId()==0 )
							 .filter(p ->p.getDiscLoadId()!=0)
							 .filter(p ->p.getCoverageType().equals("E"))
							 .map(p ->p.getPremiumExcludedTaxFc())
							 .reduce(new BigDecimal(0), (a,b) ->a==null?BigDecimal.ZERO: a.add( b==null?BigDecimal.ZERO:b)).abs();
					 
				 }
			
				
				 MultiplePolicyDrCrDetail mpcd = MultiplePolicyDrCrDetail.builder()
						 .policyNo(StringUtils.isBlank(req.getPolicyNo())?hpm.getPolicyNo():req.getPolicyNo())
						 .quoteNo(StringUtils.isBlank(req.getQuoteNo())?hpm.getQuoteNo():req.getQuoteNo())
						 .chgId(new BigDecimal(chgId))
						 .companyId(hpm.getCompanyId())
						 .productId(hpm.getProductId().toString())
						 .branchCode(hpm.getBranchCode())
						 .riskId(riskId)
						 .chargeCode(new BigDecimal(c.getChartAccountCode()))
						 .docNo(documentNo)
						 .docType(documentType)
						 .docId(docId)
						 .drcrFlag(StringUtils.isBlank(drcrFlag)?c.getAccountType():drcrFlag)
						 .amountFc(premiumFc)
						 .chargeAccountDesc(c.getChartAccountDesc())
						 .narration(narration)
						 .displayOrder(c.getDisplayOrder())
						 .status("Y")
						 .type("P")
						 .entryDate(new Date())
						 .build();
				
				 mpcrdrRepo.save(mpcd);
			}
			
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	private void insertMultiplePolicyCrDr_2(String vehicle_type, Map<Integer, List<PolicyCoverData>> groupData,
			HomePositionMaster hpm, ChartParentMaster c, String drcrFlag, String documentType, String documentNo, String docId,
			String narration, ChartAccountRequest req) {
		try {
			String endorsmentType =StringUtils.isBlank(hpm.getEndtTypeId())?"":hpm.getEndtTypeId();

			for(Map.Entry<Integer, List<PolicyCoverData>> entry :groupData.entrySet()) {
				
				Long count =mpcrdrRepo.countByCompanyIdAndProductIdAndQuoteNo(hpm.getCompanyId(), hpm.getProductId().toString(), hpm.getQuoteNo());
				Long chgId =count<=0 ?1 :count + 1;
			
				Integer riskId =entry.getKey();
				
				List<PolicyCoverData> data = entry.getValue();
				
				BigDecimal premiumFc =BigDecimal.ZERO;
				 
				 if(StringUtils.isBlank(endorsmentType)) {
					 
					 premiumFc =data.stream().filter(p ->p.getTaxId()!=0)
							 .filter(p ->p.getDiscLoadId()==0)
							 .map(p ->p.getTaxAmount())
							 .reduce(new BigDecimal(0),(a,b) ->a==null?BigDecimal.ZERO: a.add( b==null?BigDecimal.ZERO:b));
					 
					 
				 }else {
					 
					 premiumFc = data.stream()
							    .filter(p -> p.getTaxId() != 0)
							    .filter(p -> c.getChartAccountDesc().equalsIgnoreCase(p.getTaxDesc()))
							    .map(PolicyCoverData::getTaxAmount)
							    .reduce(BigDecimal.ZERO, BigDecimal::add);
							
				 }
			
				
				 MultiplePolicyDrCrDetail mpcd = MultiplePolicyDrCrDetail.builder()
						 .policyNo(StringUtils.isBlank(req.getPolicyNo())?hpm.getPolicyNo():req.getPolicyNo())
						 .quoteNo(StringUtils.isBlank(req.getQuoteNo())?hpm.getQuoteNo():req.getQuoteNo())
						 .chgId(new BigDecimal(chgId))
						 .companyId(hpm.getCompanyId())
						 .productId(hpm.getProductId().toString())
						 .branchCode(hpm.getBranchCode())
						 .riskId(riskId)
						 .chargeCode(new BigDecimal(c.getChartAccountCode()))
						 .docNo(documentNo)
						 .docType(documentType)
						 .docId(docId)
						 .drcrFlag(StringUtils.isBlank(drcrFlag)?c.getAccountType():drcrFlag)
						 .amountFc(premiumFc)
						 .chargeAccountDesc(c.getChartAccountDesc())
						 .narration(narration)
						 .displayOrder(c.getDisplayOrder())
						 .status("Y")
						 .type("V")
						 .entryDate(new Date())
						 .build();
				
				 mpcrdrRepo.save(mpcd);
			}
			
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	private void insertMultiplePolicyCrDr_3(String vehicle_type, Map<Integer, List<PolicyCoverData>> groupData,
			HomePositionMaster hpm, ChartParentMaster c, String drcrFlag, String documentType, String documentNo, String docId,
			String narration, ChartAccountRequest req) {
		try {
			String endorsmentType =StringUtils.isBlank(hpm.getEndtTypeId())?"":hpm.getEndtTypeId();
			BigDecimal brokerCommision =hpm.getCommissionPercentage();
			
			for(Map.Entry<Integer, List<PolicyCoverData>> entry :groupData.entrySet()) {
				
				Long count =mpcrdrRepo.countByCompanyIdAndProductIdAndQuoteNo(hpm.getCompanyId(),hpm.getProductId().toString(), hpm.getQuoteNo());
				Long chgId =count<=0 ?1 :count + 1;
				
				Integer riskId =entry.getKey();
				
				List<PolicyCoverData> data = entry.getValue();
				
				BigDecimal premiumFc =BigDecimal.ZERO;
				BigDecimal premiumFcWithT =BigDecimal.ZERO;
				 
				 if(StringUtils.isBlank(endorsmentType)) {
					 
					 premiumFcWithT =data.stream().filter(p->p.getTaxId()==0 )
							 .filter(p ->p.getDiscLoadId()==0)
							 .map(p ->p.getPremiumExcludedTaxFc())
							.reduce(new BigDecimal(0),(a,b) -> a==null?BigDecimal.ZERO: a.add( b==null?BigDecimal.ZERO:b));
							 
				 }else {
					 
					 
					 premiumFcWithT =data.stream().filter(p->p.getTaxId()==0 )
							 .filter(p ->p.getDiscLoadId()!=0)
							 .filter(p ->p.getCoverageType().equals("E"))
							 .map(p ->p.getPremiumExcludedTaxFc())
							 .reduce(new BigDecimal(0), (a,b) -> a==null?BigDecimal.ZERO: a.add( b==null?BigDecimal.ZERO:b)).abs();
							 
				 }
			
				
				premiumFc =premiumFcWithT.multiply(brokerCommision).divide(new BigDecimal(100)).setScale(2,BigDecimal.ROUND_HALF_EVEN);

				 
				 MultiplePolicyDrCrDetail mpcd = MultiplePolicyDrCrDetail.builder()
						 .policyNo(StringUtils.isBlank(req.getPolicyNo())?hpm.getPolicyNo():req.getPolicyNo())
						 .quoteNo(StringUtils.isBlank(req.getQuoteNo())?hpm.getQuoteNo():req.getQuoteNo())
						 .chgId(new BigDecimal(chgId))
						 .companyId(hpm.getCompanyId())
						 .productId(hpm.getProductId().toString())
						 .branchCode(hpm.getBranchCode())
						 .riskId(riskId)
						 .chargeCode(new BigDecimal(c.getChartAccountCode()))
						 .docNo(documentNo)
						 .docType(documentType)
						 .docId(docId)
						 .drcrFlag(StringUtils.isBlank(drcrFlag)?c.getAccountType():drcrFlag)
						 .amountFc(premiumFc)
						 .chargeAccountDesc(c.getChartAccountDesc())
						 .narration(narration)
						 .displayOrder(c.getDisplayOrder())
						 .status("Y")
						 .type("BP")
						 .entryDate(new Date())
						 .build();
				
				 mpcrdrRepo.save(mpcd);
				 
				 Integer result =mpcrdrRepo.updateBrokerCommissionByRiskId(hpm.getQuoteNo(),riskId,premiumFc.toString(),brokerCommision.toString());
				 
				 Log.info("updateBrokerCommissionByRiskId result : "+result);
			}
			
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	private List<PolicyDrcrDetail> saveDRCR(List<Map<String, Object>> list) {

		try {
			return list.stream().map(p ->{
				
				String vehicle_type=p.get("vehicle_type")==null?"":p.get("vehicle_type").toString();

				PolicyDrcrDetail drcrDetail = PolicyDrcrDetail.builder()
						.amountFc(p.get("AmountFC")==null?new BigDecimal(0):new BigDecimal(p.get("AmountFC").toString()))
						.amountLc(p.get("AmountFC")==null?new BigDecimal(0):new BigDecimal(p.get("AmountFC").toString()))
						.branchCode(p.get("BranchCode")==null?null:p.get("BranchCode").toString())
						.chargeAccountDesc(p.get("ChargeAccountDesc")==null?null:p.get("ChargeAccountDesc").toString())
						.chargeCode(p.get("ChargeCode")==null?new BigDecimal(0):BigDecimal.valueOf(Double.valueOf(p.get("ChargeCode").toString())))
						.chgId(p.get("ChargeId")==null?new BigDecimal(0):BigDecimal.valueOf(Double.valueOf(p.get("ChargeId").toString())))
						.companyId(p.get("CompanyId")==null?null:p.get("CompanyId").toString())
						.displayOrder(p.get("DisplayOrder")==null?null:Integer.valueOf(p.get("DisplayOrder").toString()))
						.docId(p.get("DocId")==null?null:p.get("DocId").toString())
						.docType(p.get("DocType")==null?null:p.get("DocType").toString())
						.docNo(p.get("DocNo")==null?null:p.get("DocNo").toString())
						.drcrFlag(p.get("DRCRFlag")==null?null:p.get("DRCRFlag").toString())
						.entryDate(new Date())
						.narration(p.get("Narration")==null?null:p.get("Narration").toString())
						.policyNo(p.get("PolicyNo")==null?null:p.get("PolicyNo").toString())
						.productId(p.get("ProductId")==null?null:p.get("ProductId").toString())
						.quoteNo(p.get("QuoteNo")==null?null:p.get("QuoteNo").toString())
						.vehicleType(vehicle_type)
						.vehiclelTypeDesc("M".equals(vehicle_type)?"MULTIPLE":"SINGLE")
						.status(p.get("status")==null?null:p.get("status").toString())
						.build();
				
				return policyDRCRRepo.save(drcrDetail);
			}).collect(Collectors.toList());
			
		}catch (Exception e) {
			e.printStackTrace();
			log.error(e);
		}
		return null;
	}

	

}
