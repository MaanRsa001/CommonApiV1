package com.maan.eway.endorsment.util;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.maan.eway.bean.BuildingDetails;
import com.maan.eway.bean.CommonDataDetails;
import com.maan.eway.bean.DocumentTransactionDetails;
import com.maan.eway.bean.EndtTypeMaster;
import com.maan.eway.bean.EserviceBuildingDetails;
import com.maan.eway.bean.EserviceCommonDetails;
import com.maan.eway.bean.EserviceSectionDetails;
import com.maan.eway.bean.HomePositionMaster;
import com.maan.eway.bean.ListItemValue;
import com.maan.eway.bean.PersonalInfo;
import com.maan.eway.bean.ProductEmployeeDetails;
import com.maan.eway.bean.SectionDataDetails;
import com.maan.eway.calculator.util.RatingFactorsUtil;
import com.maan.eway.common.req.ChangeEndoStatusReq;
import com.maan.eway.common.req.SequenceGenerateReq;
import com.maan.eway.common.res.CommonCopyRes;
import com.maan.eway.common.res.EndorsementCriteriaRes;
import com.maan.eway.common.service.impl.GenerateSeqNoServiceImpl;
import com.maan.eway.common.service.impl.MotorGridServiceImpl;
import com.maan.eway.endorsment.request.Endorsment;
import com.maan.eway.repository.BuildingDetailsRepository;
import com.maan.eway.repository.CommonDataDetailsRepository;
import com.maan.eway.repository.DocumentTransactionDetailsRepository;
import com.maan.eway.repository.EServiceSectionDetailsRepository;
import com.maan.eway.repository.EserviceBuildingDetailsRepository;
import com.maan.eway.repository.EserviceCommonDetailsRepository;
import com.maan.eway.repository.HomePositionMasterRepository;
import com.maan.eway.repository.PersonalInfoRepository;
import com.maan.eway.repository.ProductEmployeesDetailsRepository;
import com.maan.eway.repository.SectionDataDetailsRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import jakarta.persistence.Query;

@Service
public class CopyCommonRaw {

	@Autowired
	private BuildingDetailsRepository buildingRepo;
	
	@Autowired
	private EserviceCommonDetailsRepository eCommonRepo;
	
	@Autowired	 
	private MotorGridServiceImpl numberGenerate ;
	
	@Autowired
	private SectionDataDetailsRepository sectionDataRepo;
	
	@Autowired
	private EServiceSectionDetailsRepository eserSecRepo;

	@Autowired
	private HomePositionMasterRepository homePosistionRepo;
	
	@Autowired
	private PersonalInfoRepository personalInforepo;

	@Autowired
	private ProductEmployeesDetailsRepository proEmplyeeRepo;
	
	@Autowired
	private CommonDataDetailsRepository commonDataRepo;
	
	@Autowired
	private EserviceBuildingDetailsRepository eserBuildingRepo;
	
	@Autowired
	private DocumentTransactionDetailsRepository coverDocUploadDetails;
	private Logger log = LogManager.getLogger(CopyCommonRaw.class);


	@Autowired
	private GenerateSeqNoServiceImpl genSeqNoService ; 

	
	@Autowired 
	private RatingFactorsUtil ratingutil;
	
	public EserviceCommonDetails copyCommonRaw(Endorsment request) {
		try {
			
			// Risk
			CommonCopyRes  riskRes =  copyCommonRiskTable(request);
			
			
			// Section Copy
			List<String> sectionIds = copyBuildingSections(riskRes);
//			riskRes.setSectionIds(sectionIds);
//			riskRes.setLocationId(riskRes.getLocationId());
//			//Document Copy
//			coverDocumentUploadDetailsEndoCopyquote(riskRes);
			
			productEmpDetailsEndoCopyquote(riskRes);
			List<EserviceCommonDetails> commonData = eCommonRepo.findByRequestReferenceNo(riskRes.getRequestReferenceNo());
			commonData = commonData.stream().filter(distinctByKey(o -> Arrays.asList(o.getRequestReferenceNo()))).collect(Collectors.toList());
			//EserviceCommonDetails commonData = eCommonRepo.findByRequestReferenceNoOrderByRiskIdDesc(riskRes.getRequestReferenceNo() ); 
			EserviceCommonDetails commonData1=commonData.get(0);
			
			
			return commonData1 ;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	private static <T> java.util.function.Predicate<T> distinctByKey(java.util.function.Function<? super T, ?> keyExtractor) {
	    Map<Object, Boolean> seen = new ConcurrentHashMap<>();
	    return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
	}
	
	public CommonCopyRes copyCommonRiskTable(Endorsment ent) {
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		try {
			List<EserviceCommonDetails> CommonDatas=null;
		//	Integer count=eCommonRepo.countByOriginalPolicyNoAndRiskId(ent.getPolicyNo(),1);
			List<Object> list=getMasterTableCount(ent.getPolicyNo());
			Integer	count = list.size();
			String prevPolicyNo=null;
			String prevQuoteNo=null;
			String newRequestNo =null;
			String prevRequestRefNo=null;
			long pendingcount =0;
			if(count>0) {
				//List<EserviceCommonDetails> CommonList=eCommonRepo.findByOriginalPolicyNoAndRiskId(ent.getPolicyNo(),1);
				List<EserviceCommonDetails> CommonList=eCommonRepo.findByOriginalPolicyNoAndStatusNot(ent.getPolicyNo(),"D");
				CommonList=CommonList.stream().filter(distinctByKey(m ->m.getPolicyNo())).collect(Collectors.toList());
				//Compar
				CommonList.sort(new Comparator<EserviceCommonDetails>() {

					@Override
					public int compare(EserviceCommonDetails o1, EserviceCommonDetails o2) {
						// TODO Auto-generated method stub
						return o1.getEndtCount().compareTo(o2.getEndtCount());
					}
				}.reversed());
				
				pendingcount = CommonList.stream().filter(m->m.getEndtStatus().equals("P")).count();
				if(CommonList.stream().filter(m->(m.getEndtStatus().equals("P") && (Integer.parseInt(ent.getEndtType())==m.getEndorsementType()))).count()>0) {
					CommonCopyRes res = dozerMapper.map(CommonList.get(0) , CommonCopyRes.class);
					return res;
				}
				if (pendingcount > 0) {
					List<EserviceCommonDetails> pendingData = CommonList.stream()
							.filter(m -> m.getEndtStatus().equals("P")).collect(Collectors.toList());
					CommonDatas = pendingData;
					prevPolicyNo = CommonDatas.get(0).getEndtPrevPolicyNo();
					prevQuoteNo = CommonDatas.get(0).getEndtPrevQuoteNo();
					newRequestNo = CommonDatas.get(0).getRequestReferenceNo();
					prevRequestRefNo = CommonDatas.get(0).getRequestReferenceNo();
					List<EserviceCommonDetails> rows = eCommonRepo.findByRequestReferenceNo(prevRequestRefNo);
					List<EserviceSectionDetails> section = eserSecRepo
							.findByRequestReferenceNoAndProductId(prevRequestRefNo, ent.getProductId().toPlainString());
					if (rows.size() > 0 && rows != null) {
						eCommonRepo.deleteAllInBatch(rows);
						if (section.size() > 0 && section != null) {
							eserSecRepo.deleteAll(section);
						}

						List<ProductEmployeeDetails> empDetails = proEmplyeeRepo
								.findByRequestReferenceNo(prevRequestRefNo);
						if (empDetails.size() > 0 && empDetails != null) {
							proEmplyeeRepo.deleteAll(empDetails);
						}

						List<DocumentTransactionDetails> coverDocList = coverDocUploadDetails
								.findByRequestReferenceNo(prevRequestRefNo);
						if (coverDocList.size() > 0 && coverDocList != null) {
							coverDocUploadDetails.deleteAll(coverDocList);
						}
						proEmplyeeRepo.flush();
						coverDocUploadDetails.flush();
						eCommonRepo.flush();
						count--;
						newRequestNo = numberGenerate.generateRequestNo(ent.getCompanyId(), ent.getBranchCode(),
								String.valueOf(ent.getProductId()));
					}
					// count--;
				}else {
					CommonDatas=CommonList.stream().filter(m->m.getEndtStatus().equals("C")).collect(Collectors.toList());;
					
					if(CommonList.size()>1) {
						prevPolicyNo=CommonList.get(0).getPolicyNo();
						prevQuoteNo =CommonList.get(0).getQuoteNo();
						prevRequestRefNo=CommonList.get(0).getRequestReferenceNo();
					}else {
					//	prevPolicyNo=ent.getPolicyNo();
					//	prevQuoteNo =CommonDatas.get(0).getEndtPrevQuoteNo();
						prevPolicyNo=CommonList.get(0).getPolicyNo();
						prevQuoteNo =CommonList.get(0).getQuoteNo();
						prevRequestRefNo=CommonDatas.get(0).getRequestReferenceNo();
					}
				}
				
			}else {
				CommonDatas=eCommonRepo.findByPolicyNoAndStatus(ent.getPolicyNo(),"P");
				prevPolicyNo=ent.getPolicyNo();
				prevQuoteNo =CommonDatas.get(0).getQuoteNo();
				prevRequestRefNo=CommonDatas.get(0).getRequestReferenceNo();
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
					//endtTypeRepo.findByCompanyIdAndProductIdAndStatusAndEndtTypeIdAndEffectiveDateStartLessThanEqualAndEffectiveDateEndGreaterThanEqual(ent.getCompanyId(), ent.getProductId().intValue(), "Y",Integer.parseInt(ent.getEndtType()), new Date(), new Date());
			List<EserviceCommonDetails> CommonList=eCommonRepo.findByQuoteNoAndStatusNotOrderByRiskIdAsc(prevQuoteNo,"D");
			
			List<CommonDataDetails> commonData = 
					commonDataRepo.findByQuoteNoAndStatusNotOrderByRiskIdAsc(prevQuoteNo, "D");

			List<EserviceCommonDetails> filteredCommonList = CommonList.stream().filter(m -> commonData.stream()
					.anyMatch(risk -> m.getRiskId().equals(risk.getRiskId()) && m.getQuoteNo().equals(risk.getQuoteNo())
							&& m.getLocationId().equals(risk.getLocationId())
							&& m.getSectionId().equals(risk.getSectionId())))
					.collect(Collectors.toList());
			List<EserviceBuildingDetails> newBuildingList = new ArrayList<EserviceBuildingDetails>();
			
			List<EserviceCommonDetails> newCommonList = new ArrayList<EserviceCommonDetails>();
//			List<EserviceCommonDetails> endtList = eCommonRepo.findByPolicyNo(ent.getPolicyNo() + "-" + count);
//			if (endtList.size() > 0 && endtList.get(0).getEndorsementType() != null
//					&& endtList.get(0).getEndorsementType().equals(Integer.parseInt(ent.getEndtType()))) {
//				newCommonList = endtList;
//			} else {
//				if (endtList.size() > 0) {
//					eCommonRepo.deleteByPolicyNo(ent.getPolicyNo() + "-" + count);
//
//				}
			++count;
			for (EserviceCommonDetails m : filteredCommonList) {
				CommonDataDetails matchingRisk = commonData.stream()
						.filter(risk -> risk.getRiskId().equals(m.getRiskId())
								&& risk.getQuoteNo().equals(m.getQuoteNo())
								&& risk.getLocationId().equals(m.getLocationId())
								&& risk.getSectionId().equals(m.getSectionId()))
						.findFirst().orElse(null);

				if (matchingRisk != null) {

					EserviceCommonDetails newObject = dozerMapper.map(m, EserviceCommonDetails.class);
					newObject.setRequestReferenceNo(newRequestNo);
					newObject.setOriginalPolicyNo(ent.getPolicyNo());
					newObject.setEndorsementDate(new Date());
					newObject.setEndorsementRemarks(ent.getEndtRemarks());
					newObject.setEndorsementEffdate(ent.getEndtEffectiveDate());
					newObject.setEndtPrevPolicyNo(prevPolicyNo);
					newObject.setEndtPrevQuoteNo(prevQuoteNo);
					newObject.setEndtCount(new BigDecimal(count));
					newObject.setEndtStatus("P");
					if("851".equalsIgnoreCase(ent.getEndtType()) || "854".equalsIgnoreCase(ent.getEndtType()))
					{
						newObject.setEndtoptd("Y");					
					}

					newObject.setIsFinaceYn(entMaster.getEndtTypeCategoryId() == 2 ? "Y" : "N");
					newObject.setEndtCategDesc(entMaster.getEndtTypeCategory());
					newObject.setEndorsementType(Integer.parseInt(ent.getEndtType()));
					newObject.setEndorsementTypeDesc(entMaster.getEndtTypeDesc());
					newObject.setStatus("E");
					newObject.setPolicyNo(ent.getPolicyNo() + "-" + count);
					newObject.setQuoteNo(null);
					newObject.setApplicationId(ent.getApplicationId());
					if (ent.getLoginId() == null || StringUtils.isBlank(ent.getLoginId())) {
						newObject.setLoginId(m.getLoginId());
					} else {
						newObject.setLoginId(ent.getLoginId());
					}
					// Source Type Condtion
					String sourceType = newObject.getSourceType();
					if (StringUtils.isNotBlank(newObject.getApplicationId())
							&& !"1".equalsIgnoreCase(newObject.getApplicationId())) {
						List<ListItemValue> sourcerTypes = genSeqNoService.getSourceTypeDropdown(
								newObject.getCompanyId(), newObject.getBranchCode(), "SOURCE_TYPE");
						List<ListItemValue> acitveSourcerTypes = sourcerTypes.stream().filter(
								o -> "Y".equalsIgnoreCase(o.getStatus()) && o.getItemValue().contains(sourceType))
								.collect(Collectors.toList());
						newObject.setSourceType(acitveSourcerTypes.size() > 0 ? acitveSourcerTypes.get(0).getItemValue()
								: newObject.getSourceType());
						newObject
								.setSourceTypeId(acitveSourcerTypes.size() > 0 ? acitveSourcerTypes.get(0).getItemCode()
										: newObject.getSourceTypeId());
					}
					newObject.setSubUserType(ent.getSubUserType());
					newCommonList.add(newObject);
				}
			}
			eCommonRepo.saveAllAndFlush(newCommonList);
			//}
			
			// Response 
			
			CommonCopyRes res = dozerMapper.map(newCommonList.get(0) , CommonCopyRes.class);
			
			List<EserviceCommonDetails> prevDatas = eCommonRepo.findByPolicyNo(prevPolicyNo);
			res.setOldRequestReferenceNo(prevDatas.get(0).getRequestReferenceNo() );
			
			
			return res;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	//Count
	public List<Object> getMasterTableCount(String policyNo) {
		List<Object> list = new ArrayList<Object>();
		try {
			//List<EserviceMotorDetails> list = new ArrayList<EserviceMotorDetails>();
			// Find Latest Record
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Object> query = cb.createQuery(Object.class);
			//Find all
			Root<EserviceCommonDetails> b = query.from(EserviceCommonDetails.class);
			// Select
			query.multiselect(b.get("policyNo").alias("policyNo"));
						
			Predicate n1 = cb.equal(b.get("originalPolicyNo"),policyNo);
			query.where(n1).groupBy(b.get("policyNo"));
			
			// Get Result
			TypedQuery<Object> result = em.createQuery(query);
			list = result.getResultList();
			
		}
		catch(Exception e) {
			e.printStackTrace();
			log.info(e.getMessage());
		}
		return list;
	}
	
	private void coverDocumentUploadDetailsEndoCopyquote(CommonCopyRes riskRes) {
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		List<DocumentTransactionDetails> saveList = new ArrayList<DocumentTransactionDetails>();
		try {
			List<DocumentTransactionDetails> motorData = coverDocUploadDetails.findByQuoteNo(riskRes.getEndtPrevQuoteNo());
			List<DocumentTransactionDetails> coverDocList =null;
			coverDocList=coverDocUploadDetails.findByRequestReferenceNo(riskRes.getRequestReferenceNo());
			if (coverDocList.size() > 0) {
				coverDocUploadDetails.deleteAll(coverDocList);
			}
			if (motorData.size() > 0) {
				for (DocumentTransactionDetails data : motorData) {
					DocumentTransactionDetails savedata = new DocumentTransactionDetails();
					savedata = dozerMapper.map(data, DocumentTransactionDetails.class);
					savedata.setRequestReferenceNo(riskRes.getRequestReferenceNo());
					savedata.setQuoteNo(null);
					savedata.setEntryDate(new Date());
					savedata.setCreatedBy(riskRes.getCreatedBy());
					savedata.setOriginalPolicyNo(riskRes.getOriginalPolicyNo());
					savedata.setEndorsementDate(new Date());
					savedata.setEndorsementRemarks("Change in Document");
					savedata.setEndorsementEffdate(null);
					savedata.setEndtPrevPolicyNo(riskRes.getEndtPrevPolicyNo());
					savedata.setEndtPrevQuoteNo(riskRes.getEndtPrevQuoteNo());
					savedata.setEndtCount(riskRes.getEndtCount());
					savedata.setEndtStatus("P");
					savedata.setIsFinaceYn(riskRes.getIsFinanceYn());
					savedata.setEndtCategDesc(riskRes.getEndtCategoryDesc());
					savedata.setEndorsementType(null);
					savedata.setEndorsementTypeDesc(riskRes.getEndTypeDesc());
					savedata.setStatus("E");
					saveList.add(savedata);
					//savedata.setPolicyNo(req.getPolicyNo() + "-" + count);
					
				}
				coverDocUploadDetails.saveAllAndFlush(saveList);
			}
	
		}catch(Exception e) {
			e.printStackTrace();
		}
		
	}
	private void productEmpDetailsEndoCopyquote(CommonCopyRes req) {
		List<ProductEmployeeDetails> savelist = new ArrayList<ProductEmployeeDetails>();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		try {
			List<ProductEmployeeDetails> PA = proEmplyeeRepo.findByQuoteNo(req.getEndtPrevQuoteNo());
			
			List<ProductEmployeeDetails> productEmployee =null;
			productEmployee=proEmplyeeRepo.findByRequestReferenceNo(req.getRequestReferenceNo());
			if (productEmployee.size() > 0) {
				proEmplyeeRepo.deleteAll(productEmployee);
			}
			
			if (PA != null && PA.size() > 0) {
				for (ProductEmployeeDetails data : PA) {
					ProductEmployeeDetails savedata = new ProductEmployeeDetails();
					savedata = dozerMapper.map(data, ProductEmployeeDetails.class);
					savedata.setEntryDate(new Date());
					savedata.setRequestReferenceNo(req.getRequestReferenceNo());
					savedata.setQuoteNo(null);	 
					savedata.setCreatedBy(req.getCreatedBy());
					savedata.setOriginalPolicyNo(req.getPolicyNo());
					savedata.setEndorsementDate(new Date());
					savedata.setEndorsementRemarks(null);
					savedata.setEndorsementEffdate(new Date());
					savedata.setEndtPrevPolicyNo(req.getEndtPrevPolicyNo());
					savedata.setEndtPrevQuoteNo(req.getEndtPrevQuoteNo());
					savedata.setEndtCount(req.getEndtCount());
					savedata.setEndtStatus("P");
					savedata.setIsFinaceYn(req.getIsFinanceYn());
					savedata.setEndtCategDesc(req.getEndtCategoryDesc());
					savedata.setEndorsementType(null);
					savedata.setEndorsementTypeDesc(req.getEndTypeDesc());
					savedata.setStatus("E");
					savedata.setPolicyNo(req.getPolicyNo());
					savelist.add(savedata);			
				}
				proEmplyeeRepo.saveAllAndFlush(savelist);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return;
		}
		return;

	}

	public List<String> copyBuildingSections(CommonCopyRes buildingData) {
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		try {
			String newReqRefNo = buildingData.getRequestReferenceNo();
			// String oldReqRefNo=buildingData.getOldRequestReferenceNo() ;
			List<EserviceSectionDetails> oldSecDatas = eserSecRepo
					.findByQuoteNoOrderByRiskIdAsc(buildingData.getEndtPrevQuoteNo());

			// Building Section Insert
			List<EserviceSectionDetails> buildSecCount = eserSecRepo.findByRequestReferenceNo(newReqRefNo);
			if (buildSecCount.size() > 0 && !buildSecCount.isEmpty()) {
				eserSecRepo.deleteAll(buildSecCount);
			}
			List<SectionDataDetails> secList = sectionDataRepo
					.findByQuoteNoAndStatusNotOrderByRiskIdAsc(buildingData.getEndtPrevQuoteNo(), "D");

//			List<EserviceSectionDetails> filteredSectionList = oldSecDatas.stream()
//					.filter(m -> secList.stream()
//							.anyMatch(risk -> m.getRiskId().equals(risk.getRiskId())
//									&& m.getLocationId().equals(risk.getLocationId())
//									&& m.getSectionId().equals(risk.getSectionId())))
//					.collect(Collectors.toList());

			List<String> secListSave = new ArrayList<String>();
			List<EserviceSectionDetails> secListSave1 = new ArrayList<EserviceSectionDetails>();
			if (secList != null && secList.size() > 0) {

				for (SectionDataDetails section : secList) {
					EserviceSectionDetails secData = new EserviceSectionDetails();

					dozerMapper.map(section, secData);
					secData.setRequestReferenceNo(newReqRefNo);
					secData.setSectionName(section.getSectionDesc());
					secData.setUserOpt("Y");
					secData.setPolicyNo(buildingData.getPolicyNo());
					secData.setQuoteNo(null);

					secData.setOriginalPolicyNo(buildingData.getOriginalPolicyNo());
					secData.setEndorsementDate(new Date());

//							secData.setEndorsementEffdate(buildingData.gete());
					secData.setEndtPrevPolicyNo(buildingData.getEndtPrevPolicyNo());
					secData.setEndtPrevQuoteNo(buildingData.getEndtPrevQuoteNo());
					secData.setEndtCount(buildingData.getEndtCount());
					secData.setEndtStatus("P");
					secData.setIsFinaceYn(buildingData.getIsFinanceYn());
					secData.setEndtCategDesc(buildingData.getEndtCategoryDesc());
//							secData.setEndorsementType(Integer.parseInt(buildingData.get()));
					secData.setEndorsementTypeDesc(buildingData.getEndtCategoryDesc());
					secData.setStatus("E");
					secData.setPolicyNo(buildingData.getPolicyNo());
					secData.setQuoteNo(null);

					secListSave.add(secData.getSectionId());
					secListSave1.add(secData);
				}
				eserSecRepo.saveAllAndFlush(secListSave1);
			}

			return secListSave;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@PersistenceContext
	private EntityManager em;

//	public List<EndorsementCriteriaRes> endorsementCommonGrid(Endorsment request) {
//		 try {
//
//				// Get Datas
//				CriteriaBuilder cb = em.getCriteriaBuilder();
//				CriteriaQuery<EndorsementCriteriaRes> query = cb.createQuery(EndorsementCriteriaRes.class);
//
//				// Find All
//				Root<EserviceCustomerDetails> c = query.from(EserviceCustomerDetails.class);
//				Root<EserviceCommonDetails> m = query.from(EserviceCommonDetails.class);
//				Root<HomePositionMaster> h1 = query.from(HomePositionMaster.class);
//				Subquery<BigDecimal> endtPre = query.subquery(BigDecimal.class);
//				Root<HomePositionMaster> h = endtPre.from(HomePositionMaster.class);
//				endtPre.select(cb.sum(h.get("endtPremium") ) ) ;
//				Predicate pm1 = cb.equal(h.get("companyId"), m.get("companyId"));
//				Predicate pm2 = cb.equal(h.get("productId"), m.get("productId").as(Integer.class));
//				Predicate pm3   = cb.like(h.get("policyNo"), m.get("policyNo"));
//				endtPre.where(pm1,pm2,pm3);
//				
//
//				Subquery<Long> debitNoteNo = query.subquery(Long.class);
//				Root<HomePositionMaster> h2 = debitNoteNo.from(HomePositionMaster.class);
//				debitNoteNo.select(cb.max(h2.get("debitNoteNo"))) ;
//				Predicate pm4 = cb.equal(h2.get("companyId"), m.get("companyId"));
//				Predicate pm5 = cb.equal(h2.get("productId"), m.get("productId").as(Integer.class));
//				Predicate pm6   = cb.equal(h2.get("policyNo"), m.get("policyNo"));
//				debitNoteNo.where(pm4,pm5,pm6);
//				
//				Subquery<Long> creditNo = query.subquery(Long.class);
//				Root<HomePositionMaster> h3 = creditNo.from(HomePositionMaster.class);
//				creditNo.select(cb.max(h3.get("creditNo"))) ;
//				Predicate pm7 = cb.equal(h3.get("companyId"), m.get("companyId"));
//				Predicate pm8 = cb.equal(h3.get("productId"), m.get("productId").as(Integer.class));
//				Predicate pm9   = cb.equal(h3.get("policyNo"), m.get("policyNo"));
//				creditNo.where(pm7,pm8,pm9);
//		
//				Subquery<Long> endtPreTax = query.subquery(Long.class);
//				Root<HomePositionMaster> h4 = endtPreTax.from(HomePositionMaster.class);
//				endtPreTax.select(cb.sum(h4.get("endtPremiumTax") ) ) ;
//				Predicate pm10 = cb.equal(h4.get("companyId"), m.get("companyId"));
//				Predicate pm11 = cb.equal(h4.get("productId"), m.get("productId").as(Integer.class));
//				Predicate pm12   = cb.equal(h4.get("policyNo"), m.get("policyNo"));
//				endtPreTax.where(pm10,pm11,pm12);
//
//// UW System ID
//             Subquery<Long> uwsysId = query.subquery(Long.class);
//             Root<HomePositionMaster> h5 = uwsysId.from(HomePositionMaster.class);
//             uwsysId.select(cb.max(h5.get("uwsysId")));
//             Predicate uw1 = cb.equal(h5.get("quoteNo"), cb.max(m.get("quoteNo"))); // Changed
//             uwsysId.where(uw1);
//             
//          // Over All Premium Fc
//				Subquery<BigDecimal> overAllPremiumFc = query.subquery(BigDecimal.class);
//				Root<HomePositionMaster> ocpm1 = overAllPremiumFc.from(HomePositionMaster.class);
//				overAllPremiumFc.select(cb.sum(ocpm1.get("overallPremiumFc")));
//				Predicate a1 = cb.equal(m.get("quoteNo"),ocpm1.get("quoteNo") );
//				overAllPremiumFc.where(a1);
//				
//				// Over All Premium Lc
//				Subquery<BigDecimal> overAllPremiumLc = query.subquery(BigDecimal.class);
//				Root<HomePositionMaster> ocpm2 = overAllPremiumLc.from(HomePositionMaster.class);
//				overAllPremiumLc.select(cb.sum(ocpm2.get("overallPremiumLc")));
//				Predicate a2 = cb.equal(m.get("quoteNo"),ocpm2.get("quoteNo") );
//				overAllPremiumLc.where(a2);
//
//// Policy Index
//             Subquery<Long> polIdx = query.subquery(Long.class);
//             Root<HomePositionMaster> h6 = polIdx.from(HomePositionMaster.class);
//             polIdx.select(cb.max(h6.get("ahpolIdx")));
//             Predicate pol1 = cb.equal(h6.get("quoteNo"), cb.max(m.get("quoteNo"))); // Changed
//             polIdx.where(pol1);
//						// Select
//				query.multiselect(// cb.literal(Long.parseLong("1")).alias("idsCount"),
//						// Customer Info
//						cb.max(c.get("customerReferenceNo")).alias("customerReferenceNo"),
//						cb.max(c.get("idNumber")).alias("idNumber"), cb.max(c.get("clientName")).alias("clientName"),
//						// Vehicle Info
//						cb.max(m.get("companyId")).alias("companyId"), cb.max(m.get("productId")).alias("productId"),
//						cb.max(m.get("branchCode")).alias("branchCode"),
//						cb.max(m.get("requestReferenceNo")).alias("requestReferenceNo"),
//						cb.selectCase().when(cb.max(m.get("quoteNo")).isNotNull(), cb.max(m.get("quoteNo")))
//								.otherwise(cb.max(m.get("quoteNo"))).alias("quoteNo"),
//						cb.selectCase().when(cb.max(m.get("customerId")).isNotNull(), cb.max(m.get("customerId")))
//								.otherwise(cb.max(m.get("customerId"))).alias("customerId"),
//						cb.max(m.get("policyStartDate")).alias("policyStartDate"),
//						cb.max(m.get("policyEndDate")).alias("policyEndDate"),
//						cb.max(m.get("endorsementType")).alias("endorsementTypeId"),
//						cb.max(m.get("endorsementTypeDesc")).alias("endorsementDesc"),
//						cb.max(m.get("endtCategDesc")).alias("endorsementCategoryDesc"),
//						cb.max(m.get("endorsementEffdate")).alias("effectiveDate"),
//						cb.max(m.get("endtStatus")).alias("endorsementStatus"),
//						cb.max(m.get("policyNo")).alias("policyNo"),
//						cb.max(m.get("endorsementRemarks")).alias("endorsementRemarks"),
//						cb.max(m.get("endorsementDate")).alias("endorsementDate"),
//						// Home Position Master
//					//	cb.sum(m.get("overallPremiumLc")).alias("overallPremiumLc"),
//					//	cb.sum(m.get("overallPremiumFc")).alias("overallPremiumFc"),
//						cb.max(overAllPremiumLc).alias("overallPremiumLc"), cb.max(overAllPremiumFc).alias("overallPremiumFc"),
//						cb.sum(endtPre,endtPreTax).alias("endtPremium"), cb.max(m.get("currency")).alias("currency"),
//						debitNoteNo.alias("debitNoteNo") ,creditNo.alias("creditNo"),
//                        uwsysId.alias("uwSysId"),
//                        polIdx.alias("polIdx")
//
//                        );
//
//				// Order By
////				List<Order> orderList = new ArrayList<Order>();
////				orderList.add(cb.desc(cb.max(m.get("endorsementDate"))));
//				List<Order> orderList = new ArrayList<Order>();
//				orderList.add(cb.desc((m.get("policyNo"))));
//
//				// Where
//				Predicate n1 = cb.equal(c.get("customerReferenceNo"), m.get("customerReferenceNo"));
//				Predicate n2 = cb.equal(m.get("companyId"), request.getCompanyId());
//				Predicate n3 = cb.equal(m.get("productId"), request.getProductId().toPlainString());
//			//	Predicate n4 = cb.notEqual(m.get("status"),"D");
//				// Predicate n4 = cb.in(m.get("status")).value(Arrays.asList("E","P","D")); //
//				// m.get("status").in("E","P"));
//				Predicate n5 = cb.or(cb.like(m.get("originalPolicyNo"), request.getPolicyNo()),
//						cb.like(m.get("policyNo"), request.getPolicyNo()));
//				// Predicate n6 = cb.equal(m.get("riskId"), "1");
//				// Predicate n7 = cb.like(h.get("quoteNo"), m.get("quoteNo"));
//				// Predicate n8 = cb.like(m.get("PolicyNo"), request.getPolicyNo());
//				// Predicate n5 = cb.lessThanOrEqualTo(m.get("updatedDate"), endDate);
//				// Predicate n6 = cb.greaterThanOrEqualTo(m.get("updatedDate"), startDate);
//
//				/*
//				 * Predicate n7 = null; if (req.getApplicationId().equalsIgnoreCase("1")) { n7 =
//				 * cb.equal(m.get("loginId"), req.getLoginId()); } else { n7 =
//				 * cb.equal(m.get("applicationId"), req.getApplicationId()); }
//				 */
//
//				/*
//				 * Predicate n8 = null; if (req.getUserType().equalsIgnoreCase("Broker") ||
//				 * req.getUserType().equalsIgnoreCase("User")) { Expression<String> e0 =
//				 * m.get("brokerBranchCode"); n8 = e0.in(branches); } else { Expression<String>
//				 * e0 = m.get("branchCode"); n8 = e0.in(branches); }
//				 */
//				
//				query.where(n1, n2, n3,  n5)
//						/*
//						 * .groupBy(c.get("customerReferenceNo"), c.get("idNumber"),
//						 * c.get("clientName"), m.get("companyId"), m.get("productId"),
//						 * m.get("branchCode"), m.get("requestReferenceNo"), m.get("quoteNo"),
//						 * m.get("customerId"), m.get("policyStartDate"), m.get("policyEndDate"))
//						 */
//
//						.groupBy(/* m.get("overallPremiumLc"),m.get("overallPremiumFc"), */m.get("policyNo"))
//						.orderBy(orderList);
//
//				// Get Result
//				TypedQuery<EndorsementCriteriaRes> result = em.createQuery(query);
//				//// result.setFirstResult(500);
//				// result.setMaxResults(500);
//				List<EndorsementCriteriaRes> grids = result.getResultList();
//				return grids;
//			
//		 }catch (Exception e) {
//			 e.printStackTrace();
//		}
//		return null;
//	

	
	public List<EndorsementCriteriaRes> endorsementCommonGrid(Endorsment request) {

	    try {

	        String sql = """
	            SELECT
	                MAX(ecd1.CUSTOMER_REFERENCE_NO) AS customerReferenceNo,
	                MAX(ecd1.ID_NUMBER) AS idNumber,
	                MAX(ecd1.CLIENT_NAME) AS clientName,

	                MAX(ecd2.COMPANY_ID) AS companyId,
	                MAX(ecd2.PRODUCT_ID) AS productId,
	                MAX(ecd2.BRANCH_CODE) AS branchCode,
	                MAX(ecd2.REQUEST_REFERENCE_NO) AS requestReferenceNo,

	                MAX(ecd2.QUOTE_NO) AS quoteNo,
	                MAX(ecd2.CUSTOMER_ID) AS customerId,

	                MAX(ecd2.POLICY_START_DATE) AS policyStartDate,
	                MAX(ecd2.POLICY_END_DATE) AS policyEndDate,
	                MAX(ecd2.ENDORSEMENT_TYPE) AS endorsementTypeId,
	                MAX(ecd2.ENDORSEMENT_TYPE_DESC) AS endorsementDesc,
	                MAX(ecd2.ENDT_CATEG_DESC) AS endorsementCategoryDesc,
	                MAX(ecd2.ENDORSEMENT_EFFDATE) AS effectiveDate,
	                MAX(ecd2.ENDT_STATUS) AS endorsementStatus,
	                MAX(ecd2.POLICY_NO) AS policyNo,
	                MAX(ecd2.ENDORSEMENT_REMARKS) AS endorsementRemarks,
	                MAX(ecd2.ENDORSEMENT_DATE) AS endorsementDate,

	                /* Overall Premium LC */
	                MAX(COALESCE(qp.overallPremiumLc, 0)) AS overallPremiumLc,

	                /* Overall Premium FC */
	                MAX(COALESCE(qp.overallPremiumFc, 0)) AS overallPremiumFc,

	                /* Endorsement Premium + Tax */
	                MAX(
	                    COALESCE(pp.endtPremium, 0)
	                    + COALESCE(pp.endtPremiumTax, 0)
	                ) AS endtPremium,

	                MAX(ecd2.CURRENCY) AS currency,

	                /* Debit Note */
	                MAX(pp.debitNoteNo) AS debitNoteNo,

	                /* Credit Note */
	                MAX(pp.creditNo) AS creditNo,

	                /* UW System ID */
	                MAX(qp.uwSysId) AS uwSysId,

	                /* Policy Index */
	                MAX(qp.polIdx) AS polIdx

	            FROM eservice_customer_details ecd1

	            INNER JOIN eservice_common_details ecd2
	                ON ecd1.CUSTOMER_REFERENCE_NO =
	                   ecd2.CUSTOMER_REFERENCE_NO

	            /* -----------------------------------------
	               Quote level Home Position aggregation
	               ----------------------------------------- */
	            LEFT JOIN
	            (
	                SELECT
	                    QUOTE_NO,

	                    SUM(OVERALL_PREMIUM_LC) AS overallPremiumLc,
	                    SUM(OVERALL_PREMIUM_FC) AS overallPremiumFc,

	                    MAX(UW_SYS_ID) AS uwSysId,
	                    MAX(AH_POL_IDX) AS polIdx

	                FROM home_position_master

	                WHERE COMPANY_ID = :companyId
	                  AND PRODUCT_ID = CAST(:productId AS SIGNED)

	                GROUP BY QUOTE_NO

	            ) qp
	                ON qp.QUOTE_NO = ecd2.QUOTE_NO

	            /* -----------------------------------------
	               Policy level Home Position aggregation
	               ----------------------------------------- */
	            LEFT JOIN
	            (
	                SELECT
	                    COMPANY_ID,
	                    PRODUCT_ID,
	                    POLICY_NO,

	                    SUM(ENDT_PREMIUM) AS endtPremium,
	                    SUM(ENDT_PREMIUM_TAX) AS endtPremiumTax,

	                    MAX(DEBIT_NOTE_NO) AS debitNoteNo,
	                    MAX(CREDIT_NO) AS creditNo

	                FROM home_position_master

	                WHERE COMPANY_ID = :companyId
	                  AND PRODUCT_ID = CAST(:productId AS SIGNED)

	                GROUP BY
	                    COMPANY_ID,
	                    PRODUCT_ID,
	                    POLICY_NO

	            ) pp
	                ON pp.COMPANY_ID = ecd2.COMPANY_ID
	               AND pp.PRODUCT_ID = CAST(ecd2.PRODUCT_ID AS SIGNED)
	               AND pp.POLICY_NO = ecd2.POLICY_NO

	            WHERE ecd2.COMPANY_ID = :companyId

	              AND ecd2.PRODUCT_ID = :productId

	              AND (
	                    ecd2.ORIGINAL_POLICY_NO = :policyNo
	                    OR
	                    ecd2.POLICY_NO = :policyNo
	                  )

	            GROUP BY ecd2.POLICY_NO

	            ORDER BY ecd2.POLICY_NO DESC
	            """;


	        Query query = em.createNativeQuery(sql);

	        query.setParameter("companyId", request.getCompanyId());
	        query.setParameter("productId", request.getProductId().toPlainString());
	        query.setParameter("policyNo", request.getPolicyNo());

	        @SuppressWarnings("unchecked")
	        List<Object[]> rows = query.getResultList();

	        List<EndorsementCriteriaRes> result = new ArrayList<>();

	        for (Object[] row : rows) {

	            EndorsementCriteriaRes res = new EndorsementCriteriaRes();

	            int i = 0;

	            // Customer Info
	            res.setCustomerReferenceNo(toStringValue(row[i++]));
	            res.setIdNumber(toStringValue(row[i++]));
	            res.setClientName(toStringValue(row[i++]));

	            // Policy / Common Info
	            res.setCompanyId(toStringValue(row[i++]));
	            res.setProductId(toStringValue(row[i++]));
	            res.setBranchCode(toStringValue(row[i++]));
	            res.setRequestReferenceNo(toStringValue(row[i++]));

	            res.setQuoteNo(toStringValue(row[i++]));
	            res.setCustomerId(toStringValue(row[i++]));

	            res.setPolicyStartDate(toDate(row[i++]));
	            res.setPolicyEndDate(toDate(row[i++]));

	            res.setEndorsementTypeId(toInteger(row[i++]));
	            res.setEndorsementDesc(toStringValue(row[i++]));
	            res.setEndorsementCategoryDesc(toStringValue(row[i++]));

	            res.setEffectiveDate(toDate(row[i++]));

	            res.setEndorsementStatus(toStringValue(row[i++]));
	            res.setPolicyNo(toStringValue(row[i++]));
	            res.setEndorsementRemarks(toStringValue(row[i++]));

	            res.setEndorsementDate(toDate(row[i++]));

	            // Premium
	            res.setOverallPremiumLc(toBigDecimal(row[i++]));
	            res.setOverallPremiumFc(toBigDecimal(row[i++]));
	            res.setEndtPremium(toBigDecimal(row[i++]));

	            res.setCurrency(toStringValue(row[i++]));

	            // Debit / Credit
	            res.setDebitNoteNo(toStringValue(row[i++]));
	            res.setCreditNo(toStringValue(row[i++]));

	            // UW / Policy Index
	            res.setUwSysId(toLong(row[i++]));
	            res.setPolIdx(toLong(row[i++]));

	            result.add(res);
	        }

	        return result;

	    } catch (Exception e) {

	        e.printStackTrace();
	        return Collections.emptyList();
	    }
	}
	
	private String toStringValue(Object value) {

	    return value == null ? null : value.toString();
	}

	private BigDecimal toBigDecimal(Object value) {

	    if (value == null) {
	        return null;
	    }

	    if (value instanceof BigDecimal) {
	        return (BigDecimal) value;
	    }

	    return new BigDecimal(value.toString());
	}

	private Integer toInteger(Object value) {

	    if (value == null) {
	        return null;
	    }

	    if (value instanceof Number) {
	        return ((Number) value).intValue();
	    }

	    return Integer.valueOf(value.toString());
	}

	private Long toLong(Object value) {

	    if (value == null) {
	        return null;
	    }

	    if (value instanceof Number) {
	        return ((Number) value).longValue();
	    }

	    return Long.valueOf(value.toString());
	}

	private Date toDate(Object value) {

	    if (value == null) {
	        return null;
	    }

	    if (value instanceof Date) {
	        return (Date) value;
	    }

	    if (value instanceof java.sql.Timestamp) {
	        return new Date(((java.sql.Timestamp) value).getTime());
	    }

	    if (value instanceof java.sql.Date) {
	        return new Date(((java.sql.Date) value).getTime());
	    }

	    return null;
	}
	

	

	public EserviceCommonDetails commonRawEndtStatus(ChangeEndoStatusReq req) {
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		EserviceCommonDetails savedata = new EserviceCommonDetails();
		try {
			//Motor 
			List<EserviceCommonDetails> common = eCommonRepo.findByQuoteNoOrderByRiskIdAsc(req.getQuoteNo());

			if (common.size() > 0) {
				for (EserviceCommonDetails data : common) {
					savedata = dozerMapper.map(data, EserviceCommonDetails.class);
					savedata.setEndtStatus("C");
					savedata.setStatus("P");
					eCommonRepo.saveAndFlush(savedata);
				}

			}
			// Update EndT Status
			homeEndtStatus(req);
			personolInfoEndtStatus(req);
			sectionDataDetails(req);
			eserviceSectionDetails(req);
			coverDocumentUploadDetailsEndtStatus(req);
			productEmployee(req);
			commonDataDetails(req);
			esrviceBuildingDetailsEndtStatus(req);
			buildingDetailsEndtStatus(req);
			
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return savedata;
	}
	private BuildingDetails buildingDetailsEndtStatus(ChangeEndoStatusReq req) {
		BuildingDetails savedata = new BuildingDetails();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		try {
			List<BuildingDetails> buildData = buildingRepo.findByQuoteNo(req.getQuoteNo());
			if (buildData.size() > 0) {
				for (BuildingDetails data : buildData) {
					savedata = dozerMapper.map(data, BuildingDetails.class);
					savedata.setEndtStatus("C");
					savedata.setStatus("P");
					buildingRepo.saveAndFlush(savedata);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return savedata;

		
	}
	private EserviceBuildingDetails esrviceBuildingDetailsEndtStatus(ChangeEndoStatusReq req) {
		EserviceBuildingDetails savedata = new EserviceBuildingDetails();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		try {
			List<EserviceBuildingDetails> eserBuildings = eserBuildingRepo.findByQuoteNoOrderByRiskIdAsc(req.getQuoteNo());
			for (EserviceBuildingDetails eserBuilding : eserBuildings ) {
				savedata = dozerMapper.map(eserBuilding, EserviceBuildingDetails.class);
				savedata.setEndtStatus("C");
				savedata.setStatus("P");
				eserBuildingRepo.saveAndFlush(savedata);
			}

		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return savedata;

	}

	private CommonDataDetails commonDataDetails(ChangeEndoStatusReq req) {
		CommonDataDetails savedata = new CommonDataDetails();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		try {
			List<CommonDataDetails> commonData = commonDataRepo.findByQuoteNo(req.getQuoteNo());
			if (commonData != null&& commonData.size()>0) {
				for(CommonDataDetails data:commonData) {
				savedata = dozerMapper.map(data, CommonDataDetails.class);
				savedata.setCustomerReferenceNo(data.getCustomerReferenceNo());
				savedata.setEndtStatus("C");
				savedata.setStatus("P");
				commonDataRepo.saveAndFlush(savedata);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return savedata;

	}
	private ProductEmployeeDetails productEmployee(ChangeEndoStatusReq req) {
		ProductEmployeeDetails savedata = new ProductEmployeeDetails();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		try {
			List<ProductEmployeeDetails> proEmpList = proEmplyeeRepo.findByQuoteNo(req.getQuoteNo());
			if (proEmpList != null&& proEmpList.size()>0) {
				
				for(ProductEmployeeDetails data:proEmpList) {
				savedata = dozerMapper.map(data, ProductEmployeeDetails.class);
				savedata.setCompanyId(data.getCompanyId());
				savedata.setEmployeeId(data.getEmployeeId());
				savedata.setEndtStatus("C");
				savedata.setStatus("P");
				proEmplyeeRepo.saveAndFlush(savedata);
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
					savedata.setEffectiveDate(new Date());
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
