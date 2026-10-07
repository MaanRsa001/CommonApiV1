package com.maan.eway.endorsment.util;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.maan.eway.bean.DocumentTransactionDetails;
import com.maan.eway.bean.EndtTypeMaster;
import com.maan.eway.bean.EserviceCustomerDetails;
import com.maan.eway.bean.EserviceSectionDetails;
import com.maan.eway.bean.EserviceTravelDetails;
import com.maan.eway.bean.EserviceTravelGroupDetails;
import com.maan.eway.bean.HomePositionMaster;
import com.maan.eway.bean.ListItemValue;
import com.maan.eway.bean.PersonalInfo;
import com.maan.eway.bean.SectionDataDetails;
import com.maan.eway.bean.TravelPassengerDetails;
import com.maan.eway.calculator.util.RatingFactorsUtil;
import com.maan.eway.common.req.ChangeEndoStatusReq;
import com.maan.eway.common.req.SequenceGenerateReq;
import com.maan.eway.common.res.EndorsementCriteriaRes;
import com.maan.eway.common.res.TravelCopyRes;
import com.maan.eway.common.res.TravelGroupGetRes;
import com.maan.eway.common.service.impl.GenerateSeqNoServiceImpl;
import com.maan.eway.common.service.impl.MotorGridServiceImpl;
import com.maan.eway.endorsment.request.Endorsment;
import com.maan.eway.repository.DocumentTransactionDetailsRepository;
import com.maan.eway.repository.EServiceSectionDetailsRepository;
import com.maan.eway.repository.EndtTypeMasterRepository;
import com.maan.eway.repository.EserviceTravelDetailsRepository;
import com.maan.eway.repository.EserviceTravelGroupDetailsRepository;
import com.maan.eway.repository.HomePositionMasterRepository;
import com.maan.eway.repository.PersonalInfoRepository;
import com.maan.eway.repository.SectionDataDetailsRepository;
import com.maan.eway.repository.TravelPassengerDetailsRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;

@Service
public class CopyTravelRaw {

	@Autowired
	private SectionDataDetailsRepository sectionDataRepo;
	@Autowired	 
	private MotorGridServiceImpl numberGenerate ;
	@Autowired
	private EndtTypeMasterRepository endtTypeRepo;
	
	@Autowired
	private EServiceSectionDetailsRepository eserSecRepo;

	@Autowired
	private HomePositionMasterRepository homePosistionRepo;
	
	@Autowired
	private PersonalInfoRepository personalInforepo;

	
	@Autowired
	private DocumentTransactionDetailsRepository coverDocUploadDetails;

	@Autowired
	private EserviceTravelDetailsRepository etravelRepo;
	@Autowired
	private TravelPassengerDetailsRepository traPassDetailsRepo;
	
	@Autowired
	private EserviceTravelGroupDetailsRepository groupRepo ;
	
	@Autowired
	private GenerateSeqNoServiceImpl genSeqNoService ; 
	
	 

	@Autowired 
	private RatingFactorsUtil ratingutil;
	private Logger log = LogManager.getLogger(CopyTravelRaw.class);
	public EserviceTravelDetails copyTravelRaw(Endorsment request) {
		try {
			
			// Risk
			TravelCopyRes  riskRes =  copyTravelRiskTable(request);
			
			// Group
			List<TravelGroupGetRes> travelGroupList = copyTravelRiskGroup(riskRes.getRequestReferenceNo() , riskRes.getOldRequestReferenceNo() ,riskRes,request);
			riskRes.setGroupDetails(travelGroupList);
			
			EserviceTravelDetails travelData = etravelRepo.findByRequestReferenceNo(riskRes.getRequestReferenceNo() ); 
			
			return travelData  ;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public TravelCopyRes copyTravelRiskTable(Endorsment ent) {
		try {
			List<EserviceTravelDetails> travelDatas=null;
			Integer count=etravelRepo.countByOriginalPolicyNoAndRiskId(ent.getPolicyNo(),1);
			String prevPolicyNo=null;
			String prevQuoteNo=null;
			String newRequestNo =null;
			long pendingcount =0;
			if(count>0) {
				List<EserviceTravelDetails> travelList=etravelRepo.findByOriginalPolicyNoAndRiskId(ent.getPolicyNo(),1);
				//Compar
				travelList.sort(new Comparator<EserviceTravelDetails>() {

					@Override
					public int compare(EserviceTravelDetails o1, EserviceTravelDetails o2) {
						// TODO Auto-generated method stub
						return o1.getEndtCount().compareTo(o2.getEndtCount());
					}
				}.reversed());
				
				pendingcount = travelList.stream().filter(m->m.getEndtStatus().equals("P")).count();
				if(pendingcount>0) {
					 List<EserviceTravelDetails> pendingData = travelList.stream().filter(m->m.getEndtStatus().equals("P")).collect(Collectors.toList());
					 travelDatas= pendingData;
					 prevPolicyNo=travelDatas.get(0).getEndtPrevPolicyNo();
					 prevQuoteNo=travelDatas.get(0).getEndtPrevQuoteNo();
					 newRequestNo=travelDatas.get(0).getRequestReferenceNo();
					 count--;
				}else {
					travelDatas=travelList;
					
					if(travelList.size()>1) {
						prevPolicyNo=travelList.get(1).getPolicyNo();
						prevQuoteNo =travelList.get(1).getQuoteNo();
					}else {
						prevPolicyNo=ent.getPolicyNo();
						prevQuoteNo =travelDatas.get(0).getEndtPrevQuoteNo();
					}
				}
				
			}else {
				travelDatas=etravelRepo.findByPolicyNoAndStatus(ent.getPolicyNo(),"P");
				prevPolicyNo=ent.getPolicyNo();
				prevQuoteNo =travelDatas.get(0).getQuoteNo();
			}
			if(pendingcount==0) {
				//newRequestNo=numberGenerate.generateRequestNo(ent.getCompanyId(), ent.getBranchCode(), String.valueOf(ent.getProductId()));
				// Generate Seq
	 			SequenceGenerateReq generateSeqReq = new SequenceGenerateReq();
	 		 	generateSeqReq.setInsuranceId(ent.getCompanyId());  
	 		 	generateSeqReq.setProductId(ent.getProductId().toString());
	 		 	generateSeqReq.setType("2");
	 		 	generateSeqReq.setTypeDesc("REQUEST_REFERENCE_NO");
	 		 	newRequestNo =  genSeqNoService.generateSeqCall(generateSeqReq);
			}
			
			EndtTypeMaster entMaster=ratingutil.getEndtMasterData(ent.getCompanyId(),ent.getProductId().toPlainString(),ent.getEndtType());

			List<EserviceTravelDetails> travelList=etravelRepo.findByQuoteNoOrderByRiskIdAsc(prevQuoteNo);
			List<EserviceTravelDetails> newtravelList=new ArrayList<EserviceTravelDetails>();
			++count;
			for(EserviceTravelDetails m :travelList) {
				DozerBeanMapper dozerMapper = new DozerBeanMapper();
				EserviceTravelDetails newObject = dozerMapper.map(m , EserviceTravelDetails.class);
				newObject.setRequestReferenceNo(newRequestNo);
				newObject.setOriginalPolicyNo(ent.getPolicyNo());
				newObject.setEndorsementDate(new Date());
				newObject.setEndorsementRemarks(ent.getEndtRemarks());
				newObject.setEndorsementEffdate(ent.getEndtEffectiveDate());
				newObject.setEndtPrevPolicyNo(prevPolicyNo);
				newObject.setEndtPrevQuoteNo(prevQuoteNo);
				newObject.setEndtCount(new BigDecimal(count));
				newObject.setEndtStatus("P");
				newObject.setIsFinaceYn(entMaster.getEndtTypeCategoryId()==2?"Y":"N");
				newObject.setEndtCategDesc(entMaster.getEndtTypeCategory());
				newObject.setEndorsementType(Integer.parseInt(ent.getEndtType()));
				newObject.setEndorsementTypeDesc(entMaster.getEndtTypeDesc());
				newObject.setStatus("E");
				newObject.setPolicyNo(ent.getPolicyNo()+"-"+count);
				newObject.setQuoteNo(null);
				newObject.setApplicationId(ent.getApplicationId());
				if(ent.getLoginId()==null || StringUtils.isBlank(ent.getLoginId())) {
					newObject.setLoginId(m.getLoginId());
				}else {
					newObject.setLoginId(ent.getLoginId());
				}
				// Source Type Condtion
				String sourceType = newObject.getSourceType() ;
				if(StringUtils.isNotBlank(newObject.getApplicationId()) && ! "1".equalsIgnoreCase(newObject.getApplicationId()) ) {
					List<ListItemValue> sourcerTypes = genSeqNoService.getSourceTypeDropdown(newObject.getCompanyId() , newObject.getBranchCode() ,"SOURCE_TYPE"); 
					List<ListItemValue> acitveSourcerTypes = sourcerTypes.stream().filter( o -> "Y".equalsIgnoreCase(o.getStatus()) 
							&& o.getItemValue().contains(sourceType) ).collect(Collectors.toList()); 							
					newObject.setSourceType(acitveSourcerTypes.size() > 0 ? acitveSourcerTypes.get(0).getItemValue()	: 	newObject.getSourceType());			
					newObject.setSourceTypeId(acitveSourcerTypes.size() > 0 ? acitveSourcerTypes.get(0).getItemCode()	: 	newObject.getSourceTypeId());
				}
				newObject.setSubUserType(ent.getSubUserType());
				newtravelList.add(newObject);
			}
			etravelRepo.saveAllAndFlush(newtravelList);
			
			// Response 
			DozerBeanMapper dozerMapper = new DozerBeanMapper();
			TravelCopyRes res = dozerMapper.map(newtravelList.get(0) , TravelCopyRes.class);
			
			//List<EserviceTravelDetails> prevDatas = etravelRepo.findByPolicyNoAndRiskId(prevPolicyNo , 1 );
			List<EserviceTravelDetails> prevDatas = etravelRepo.findByPolicyNo(prevPolicyNo);
			res.setOldRequestReferenceNo(prevDatas.get(0).getRequestReferenceNo() );
			
			res.setPolicyNo(ent.getPolicyNo()+"-"+count) ;
			res.setEndtPrevPolicyNo(prevDatas.get(0).getPolicyNo());
			res.setEndtCount(new BigDecimal(count));

			res.setEndtStatus(newtravelList.get(0).getEndtStatus());
			res.setIsFinanceYn(newtravelList.get(0).getIsFinaceYn());
			res.setEndtCategoryDesc(newtravelList.get(0).getEndtCategDesc());
			res.setEndTypeDesc(newtravelList.get(0).getEndorsementTypeDesc());
			
			return res;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	public List<TravelGroupGetRes> copyTravelRiskGroup(String newReqRefNo , String  oldReqRefNo,TravelCopyRes  riskRes,Endorsment request) {
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		try {
			List<EserviceTravelGroupDetails>  oldGroupDatas = groupRepo.findByRequestReferenceNoOrderByGroupIdAsc(oldReqRefNo) ;
			
			// Delete NEw Tavel Group Details
			long groupCount =  groupRepo.countByRequestReferenceNo(newReqRefNo);
			if(groupCount > 0 ) {
				groupRepo.deleteByRequestReferenceNo(newReqRefNo);
			}
			
			// Insert Tavel Group Details
			List<TravelGroupGetRes> travelGroupList = new ArrayList<TravelGroupGetRes>();
			List<EserviceTravelGroupDetails> saveNewGroups = new ArrayList<EserviceTravelGroupDetails>();
			for (EserviceTravelGroupDetails data : oldGroupDatas) {
				EserviceTravelGroupDetails saveGroup = new EserviceTravelGroupDetails();
				
				// Save
				dozerMapper.map(data, saveGroup);
				saveGroup.setRequestReferenceNo(newReqRefNo);
				saveGroup.setOriginalPolicyNo(riskRes.getPolicyNo());
				saveGroup.setEndorsementDate(new Date());
				saveGroup.setEndorsementRemarks(request.getEndtRemarks());
				saveGroup.setEndorsementEffdate(request.getEndtEffectiveDate());
				saveGroup.setEndtPrevPolicyNo(riskRes.getEndtPrevPolicyNo());
				saveGroup.setEndtPrevQuoteNo(riskRes.getEndtPrevQuoteNo());
				saveGroup.setEndtCount(riskRes.getEndtCount());
				saveGroup.setEndtStatus(riskRes.getEndtStatus());
				saveGroup.setIsFinaceYn(riskRes.getIsFinanceYn());
				saveGroup.setEndtCategDesc(riskRes.getEndtCategoryDesc());
				saveGroup.setEndorsementType(Integer.parseInt(request.getEndtType()));
				saveGroup.setEndorsementTypeDesc(riskRes.getEndTypeDesc());
				saveGroup.setStatus("E");
				saveGroup.setPolicyNo(riskRes.getPolicyNo());
				saveGroup.setQuoteNo(null);
				saveGroup.setRequestReferenceNo(newReqRefNo);	
				saveGroup.setEntryDate(new Date());
				saveGroup.setStatus("Y");
				saveNewGroups.add(saveGroup);
				
				// Group Res
				TravelGroupGetRes groupRes = new TravelGroupGetRes(); 
				groupRes.setGroupId(saveGroup.getGroupId().toString());
				groupRes.setGroupMembers(saveGroup.getGrouppMembers().toString());
				groupRes.setTravelId(saveGroup.getTravelId().toString());
				travelGroupList.add(groupRes);
				
			}
			
			groupRepo.saveAllAndFlush(saveNewGroups);
			
			return travelGroupList;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@PersistenceContext
	private EntityManager em;
	
	public List<EndorsementCriteriaRes> endorsementTravelGrid(Endorsment request) {
		 try {

			 
				// Get Datas
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<EndorsementCriteriaRes> query = cb.createQuery(EndorsementCriteriaRes.class);

				// Find All
				Root<EserviceCustomerDetails> c = query.from(EserviceCustomerDetails.class);
				Root<EserviceTravelDetails> m = query.from(EserviceTravelDetails.class);
				
				Subquery<Long> endtPre = query.subquery(Long.class);
				Root<HomePositionMaster> h = endtPre.from(HomePositionMaster.class);
				endtPre.select(cb.sum(h.get("endtPremium") ) ) ;
				Predicate pm1 = cb.equal(h.get("companyId"), m.get("companyId"));
				Predicate pm2 = cb.equal(h.get("productId"), m.get("productId"));
				Predicate pm3   = cb.like(h.get("policyNo"), m.get("policyNo"));
				endtPre.where(pm1,pm2,pm3);
				// Over All Premium Fc
				Subquery<Long> overAllPremiumFc = query.subquery(Long.class);
				Root<HomePositionMaster> ocpm1 = overAllPremiumFc.from(HomePositionMaster.class);
				overAllPremiumFc.select(cb.sum(ocpm1.get("overallPremiumFc")));
				Predicate a1 = cb.equal(m.get("quoteNo"),ocpm1.get("quoteNo") );
				overAllPremiumFc.where(a1);
				
				// Over All Premium Lc
				Subquery<Long> overAllPremiumLc = query.subquery(Long.class);
				Root<HomePositionMaster> ocpm2 = overAllPremiumLc.from(HomePositionMaster.class);
				overAllPremiumLc.select(cb.sum(ocpm2.get("overallPremiumLc")));
				Predicate a2 = cb.equal(m.get("quoteNo"),ocpm2.get("quoteNo") );
				overAllPremiumLc.where(a2);
				
				
				Subquery<Long> debitNoteNo = query.subquery(Long.class);
				Root<HomePositionMaster> h2 = debitNoteNo.from(HomePositionMaster.class);
				debitNoteNo.select(cb.max(h2.get("debitNoteNo"))) ;
				Predicate pm4 = cb.equal(h2.get("companyId"), m.get("companyId"));
				Predicate pm5 = cb.equal(h2.get("productId"), m.get("productId"));
				Predicate pm6   = cb.equal(h2.get("policyNo"), m.get("policyNo"));
				debitNoteNo.where(pm4,pm5,pm6);
				
				Subquery<Long> creditNo = query.subquery(Long.class);
				Root<HomePositionMaster> h3 = creditNo.from(HomePositionMaster.class);
				creditNo.select(cb.max(h3.get("creditNo"))) ;
				Predicate pm7 = cb.equal(h3.get("companyId"), m.get("companyId"));
				Predicate pm8 = cb.equal(h3.get("productId"), m.get("productId"));
				Predicate pm9   = cb.equal(h3.get("policyNo"), m.get("policyNo"));
				creditNo.where(pm7,pm8,pm9);
		
				Subquery<Long> endtPreTax = query.subquery(Long.class);
				Root<HomePositionMaster> h4 = endtPreTax.from(HomePositionMaster.class);
				endtPreTax.select(cb.sum(h4.get("endtPremiumTax") ) ) ;
				Predicate pm10 = cb.equal(h4.get("companyId"), m.get("companyId"));
				Predicate pm11 = cb.equal(h4.get("productId"), m.get("productId"));
				Predicate pm12   = cb.equal(h4.get("policyNo"), m.get("policyNo"));
				endtPreTax.where(pm10,pm11,pm12);

// UW System ID
             Subquery<Long> uwsysId = query.subquery(Long.class);
             Root<HomePositionMaster> h5 = uwsysId.from(HomePositionMaster.class);
             uwsysId.select(cb.max(h5.get("uwsysId")));
             Predicate uw1 = cb.equal(h5.get("quoteNo"), cb.max(m.get("quoteNo"))); // Changed
             uwsysId.where(uw1);

// Policy Index
             Subquery<Long> polIdx = query.subquery(Long.class);
             Root<HomePositionMaster> h6 = polIdx.from(HomePositionMaster.class);
             polIdx.select(cb.max(h6.get("ahpolIdx")));
             Predicate pol1 = cb.equal(h6.get("quoteNo"), cb.max(m.get("quoteNo"))); // Changed
             polIdx.where(pol1);
				// Select
				query.multiselect(//cb.literal(Long.parseLong("1")).alias("idsCount"),
						// Customer Info
						cb.max(c.get("customerReferenceNo")).alias("customerReferenceNo"), cb.max(c.get("idNumber")).alias("idNumber"),
						cb.max(c.get("clientName")).alias("clientName"),
						// Vehicle Info
						cb.max(m.get("companyId")).alias("companyId"), cb.max(m.get("productId")).alias("productId"),
						cb.max(m.get("branchCode")).alias("branchCode"), cb.max(m.get("requestReferenceNo")).alias("requestReferenceNo"),
						cb.selectCase().when(cb.max(m.get("quoteNo")).isNotNull(), cb.max(m.get("quoteNo"))).otherwise(cb.max(m.get("quoteNo")))
								.alias("quoteNo"),
						cb.selectCase().when(cb.max(m.get("customerId")).isNotNull(), cb.max(m.get("customerId")))
								.otherwise(cb.max(m.get("customerId"))).alias("customerId"),
						cb.max(m.get("travelStartDate")).alias("policyStartDate"), cb.max(m.get("travelEndDate")).alias("policyEndDate"),
						cb.max(m.get("endorsementType")).alias("endorsementTypeId"),
						cb.max(m.get("endorsementTypeDesc")).alias("endorsementDesc"),
						cb.max(m.get("endtCategDesc")).alias("endorsementCategoryDesc"),
						cb.max(m.get("endorsementEffdate")).alias("effectiveDate"),
						cb.max(m.get("endtStatus")).alias("endorsementStatus"),
						cb.max(m.get("policyNo")).alias("policyNo"),
						cb.max(m.get("endorsementRemarks")).alias("endorsementRemarks"),
						cb.max(m.get("endorsementDate")).alias("endorsementDate"),
						//Home Position Master
						cb.sum(overAllPremiumLc).alias("overallPremiumLc"), cb.sum(overAllPremiumFc).alias("overallPremiumFc"),
						cb.sum(endtPre,endtPreTax).alias("endtPremium"),/*cb.sum(m.get("endtPremium")).alias("endtPremium")*/cb.max( m.get("currency")).alias("currency"),
						debitNoteNo.alias("debitNoteNo") ,creditNo.alias("creditNo"),
                        uwsysId.alias("uwsysId"),
                        polIdx.alias("polIdx"),
						cb.max(m.get("emiYn")).alias("emiYn")

                        );
			 
				// Order By
//				List<Order> orderList = new ArrayList<Order>();
//				orderList.add(cb.desc(cb.max(m.get("endorsementDate"))));
				
				List<Order> orderList = new ArrayList<Order>();
				orderList.add(cb.desc((m.get("policyNo"))));

				// Where
				Predicate n1 = cb.equal(c.get("customerReferenceNo"), m.get("customerReferenceNo"));
				Predicate n2 = cb.equal(m.get("companyId"), request.getCompanyId());
				Predicate n3 = cb.equal(m.get("productId"), request.getProductId());
			//	Predicate n4 = cb.notEqual(m.get("status"),"D");
			//	Predicate n4 = cb.in(m.get("status")).value(Arrays.asList("E","P","D"));  // m.get("status").in("E","P"));
				Predicate n5 = cb.or(cb.like(m.get("originalPolicyNo"), request.getPolicyNo()),cb.like(m.get("policyNo"), request.getPolicyNo()));
				//Predicate n5 = cb.lessThanOrEqualTo(m.get("updatedDate"), endDate);
				//Predicate n6 = cb.greaterThanOrEqualTo(m.get("updatedDate"), startDate);

			
			/*	Predicate n7 = null;
				if (req.getApplicationId().equalsIgnoreCase("1")) {
					n7 = cb.equal(m.get("loginId"), req.getLoginId());
				} else {
					n7 = cb.equal(m.get("applicationId"), req.getApplicationId());
				}*/

				/*Predicate n8 = null;
				if (req.getUserType().equalsIgnoreCase("Broker") || req.getUserType().equalsIgnoreCase("User")) {
					Expression<String> e0 = m.get("brokerBranchCode");
					n8 = e0.in(branches);
				} else {
					Expression<String> e0 = m.get("branchCode");
					n8 = e0.in(branches);
				}*/

				query.where(n1, n2, n3,n5)
						.groupBy(/*c.get("customerReferenceNo"), c.get("idNumber"), c.get("clientName"), m.get("companyId"),
								m.get("productId"), m.get("branchCode"), m.get("requestReferenceNo"), m.get("quoteNo"),
								m.get("customerId"), m.get("policyStartDate"), m.get("policyEndDate")*/m.get("policyNo"))
						.orderBy(orderList);

				// Get Result
				TypedQuery<EndorsementCriteriaRes> result = em.createQuery(query);
				////result.setFirstResult(500);
				//result.setMaxResults(500);
				  List<EndorsementCriteriaRes> grids = result.getResultList();
				  
				  return grids;
			
		 }catch (Exception e) {
			 e.printStackTrace();
		}
		return null;
	}

	public EserviceTravelDetails travelRawEndtStatus(ChangeEndoStatusReq req) {
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		EserviceTravelDetails savedata = new EserviceTravelDetails();
		try {
			//Motor 
			List<EserviceTravelDetails> travel = etravelRepo.findByQuoteNoOrderByRiskIdAsc(req.getQuoteNo());

			if (travel.size() > 0) {
				for (EserviceTravelDetails data : travel) {
					savedata = dozerMapper.map(data, EserviceTravelDetails.class);
					savedata.setEndtStatus("C");
					savedata.setStatus("P");
					savedata.setEffectiveDate(new Date());
					etravelRepo.saveAndFlush(savedata);
				}

			}
			// Update EndT Status
			homeEndtStatus(req);
			personolInfoEndtStatus(req);
			coverDocumentUploadDetailsEndtStatus(req);
			sectionDataDetails(req);
			eserviceSectionDetails(req);
			travelPassengerDetails(req);
			eserviceTravelgroupDetails(req);
			
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return savedata;
	}
	private EserviceTravelGroupDetails eserviceTravelgroupDetails(ChangeEndoStatusReq req) {
		EserviceTravelGroupDetails savedata = new EserviceTravelGroupDetails();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		try {
			List<EserviceTravelGroupDetails> travelPass = groupRepo.findByQuoteNo(req.getQuoteNo());
			if (travelPass.size() > 0) {
				for (EserviceTravelGroupDetails data : travelPass) {
					savedata = dozerMapper.map(data, EserviceTravelGroupDetails.class);
					savedata.setEndtStatus("C");
					savedata.setStatus("P");
					groupRepo.saveAndFlush(savedata);
				}
			}
		
		
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return savedata;

		
	}
	private TravelPassengerDetails travelPassengerDetails(ChangeEndoStatusReq req) {
		TravelPassengerDetails savedata = new TravelPassengerDetails();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		try {
			List<TravelPassengerDetails> travelPass = traPassDetailsRepo.findByQuoteNo(req.getQuoteNo());
			if (travelPass.size() > 0) {
				for (TravelPassengerDetails data : travelPass) {
					savedata = dozerMapper.map(data, TravelPassengerDetails.class);
					savedata.setEndtStatus("C");
					savedata.setStatus("P");
					traPassDetailsRepo.saveAndFlush(savedata);
				}
			}
		
		
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return savedata;

		
	}
	private SectionDataDetails sectionDataDetails(ChangeEndoStatusReq req) {
		SectionDataDetails savedata = new SectionDataDetails();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		try {
			List<SectionDataDetails> motorData = sectionDataRepo.findByQuoteNoAndStatusNot(req.getQuoteNo(),"D");
			if (motorData.size() > 0) {
				for (SectionDataDetails data : motorData) {
					savedata = dozerMapper.map(data, SectionDataDetails.class);
					savedata.setEndtStatus("C");
					savedata.setStatus("P");
					sectionDataRepo.saveAndFlush(savedata);
				}
			}
		
		
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return savedata;

		
	}
	private EserviceSectionDetails eserviceSectionDetails(ChangeEndoStatusReq req) {
		EserviceSectionDetails savedata = new EserviceSectionDetails();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		try {
			List<EserviceSectionDetails> motorData = eserSecRepo.findByQuoteNo(req.getQuoteNo());
			if (motorData.size() > 0) {
				for (EserviceSectionDetails data : motorData) {
					savedata = dozerMapper.map(data, EserviceSectionDetails.class);
					savedata.setEndtStatus("C");
					savedata.setStatus("P");
					eserSecRepo.saveAndFlush(savedata);
				}
			}
		
		
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return savedata;
	}
		private DocumentTransactionDetails coverDocumentUploadDetailsEndtStatus(ChangeEndoStatusReq req) {
			DocumentTransactionDetails savedata = new DocumentTransactionDetails();
			DozerBeanMapper dozerMapper = new DozerBeanMapper();
			try {
				List<DocumentTransactionDetails> motorData = coverDocUploadDetails.findByQuoteNo(req.getQuoteNo());
				if (motorData.size() > 0) {
					for (DocumentTransactionDetails data : motorData) {
						savedata = dozerMapper.map(data, DocumentTransactionDetails.class);
						savedata.setEndtStatus("C");
						savedata.setStatus("P");
						coverDocUploadDetails.saveAndFlush(savedata);
					}
				}
			
			
			} catch (Exception e) {
				e.printStackTrace();
				log.info("Exception is ---> " + e.getMessage());
				return null;
			}
			return savedata;

			
		}
		private PersonalInfo personolInfoEndtStatus(ChangeEndoStatusReq req) {
			PersonalInfo savedata = new PersonalInfo();
			DozerBeanMapper dozerMapper = new DozerBeanMapper();
			try {
				HomePositionMaster homeData=homePosistionRepo.findByQuoteNo(req.getQuoteNo());
				String customerId=homeData.getCustomerId();
				PersonalInfo personalInfoData=personalInforepo.findByCustomerId(customerId);
				savedata = dozerMapper.map(personalInfoData, PersonalInfo.class);
				savedata.setEndtStatus("C");
				savedata.setStatus("P");
				personalInforepo.saveAndFlush(savedata);

			} catch (Exception e) {
				e.printStackTrace();
				log.info("Exception is --->" + e.getMessage());
				return null;
			}
			return savedata;
			
		}

		private HomePositionMaster homeEndtStatus(ChangeEndoStatusReq req) {
			HomePositionMaster savedata = new HomePositionMaster();
			DozerBeanMapper dozerMapper = new DozerBeanMapper();
			try {
				HomePositionMaster homeData = homePosistionRepo.findByQuoteNo(req.getQuoteNo());
				if (homeData != null) {
					savedata = dozerMapper.map(homeData, HomePositionMaster.class);
					savedata.setEndtStatus("C");
					savedata.setIntegrationStatus("S");
					savedata.setStatus("P");
					homePosistionRepo.saveAndFlush(savedata);
				}

			} catch (Exception e) {
				e.printStackTrace();
				log.info("Exception is ---> " + e.getMessage());
				return null;
			}
			return savedata;

		}
		
}
