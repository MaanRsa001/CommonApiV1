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
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import com.maan.eway.bean.BuildingDetails;
import com.maan.eway.bean.BuildingRiskDetails;
import com.maan.eway.bean.CommonDataDetails;
import com.maan.eway.bean.ContentAndRisk;
import com.maan.eway.bean.DocumentTransactionDetails;
import com.maan.eway.bean.EndtTypeMaster;
import com.maan.eway.bean.EserviceBuildingDetails;
import com.maan.eway.bean.EserviceCommonDetails;
import com.maan.eway.bean.EserviceCustomerDetails;
import com.maan.eway.bean.EserviceSectionDetails;
import com.maan.eway.bean.HomePositionMaster;
import com.maan.eway.bean.ListItemValue;
import com.maan.eway.bean.PersonalInfo;
import com.maan.eway.bean.ProductEmployeeDetails;
import com.maan.eway.bean.SectionDataDetails;
import com.maan.eway.calculator.util.RatingFactorsUtil;
import com.maan.eway.common.req.ChangeEndoStatusReq;
import com.maan.eway.common.req.SequenceGenerateReq;
import com.maan.eway.common.res.BuildingCopyRes;
import com.maan.eway.common.res.EndorsementCriteriaRes;
import com.maan.eway.common.service.impl.BuildingGridServiceImpl;
import com.maan.eway.common.service.impl.GenerateSeqNoServiceImpl;
import com.maan.eway.common.service.impl.MotorGridServiceImpl;
import com.maan.eway.endorsment.request.EndRequest;
import com.maan.eway.endorsment.request.Endorsment;
import com.maan.eway.repository.BuildingDetailsRepository;
import com.maan.eway.repository.BuildingRiskDetailsRepository;
import com.maan.eway.repository.CommonDataDetailsRepository;
import com.maan.eway.repository.ContentAndRiskRepository;
import com.maan.eway.repository.DocumentTransactionDetailsRepository;
import com.maan.eway.repository.EServiceSectionDetailsRepository;
import com.maan.eway.repository.EndtTypeMasterRepository;
import com.maan.eway.repository.EserviceBuildingDetailsRepository;
import com.maan.eway.repository.EserviceCommonDetailsRepository;
import com.maan.eway.repository.EserviceCustomerDetailsRepository;
import com.maan.eway.repository.HomePositionMasterRepository;
import com.maan.eway.repository.PersonalInfoRepository;
import com.maan.eway.repository.ProductEmployeesDetailsRepository;
import com.maan.eway.repository.SectionDataDetailsRepository;

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
public class CopyBuildingRaw {

	@Autowired
	private CommonDataDetailsRepository commonDataRepo;
	@Autowired
	private ProductEmployeesDetailsRepository proEmplyeeRepo;
	@Autowired
	private EserviceBuildingDetailsRepository eBuildingRepo;
	@Autowired
	private BuildingRiskDetailsRepository buildRiskRepo;
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
	private BuildingDetailsRepository buildingRepo;
	
	@Autowired
	private EserviceCustomerDetailsRepository custRepo ;
	
	@Autowired
	private ProductEmployeesDetailsRepository paRepo;
	
	@Autowired
	private ContentAndRiskRepository contentAndRiskRepo;
	
	@Autowired
	private DocumentTransactionDetailsRepository coverDocUploadDetails;

	@Autowired
	private EserviceCommonDetailsRepository eserCommonRepo;

	@Autowired 
	private RatingFactorsUtil ratingutil;
	
	@Autowired
	private GenerateSeqNoServiceImpl genSeqNoService ; 
	
	@Autowired
	private BuildingGridServiceImpl buildingGridService ;	 

	 

	private Logger log = LogManager.getLogger(MotorGridServiceImpl.class);
	
	public EserviceBuildingDetails copyBuildingRaw(Endorsment request) {
		try {
			
			
			// Risk Copy
			BuildingCopyRes  riskRes =  copyBuildingRiskTable(request);
			
			// Section Copy
			List<String> sectionIds = copyBuildingSections( riskRes ) ;
			riskRes.setSectionIds(sectionIds);
			riskRes.setLocationId(riskRes.getLocationId());
			
			// Personal Accident Copy
			String res = copyPersonalAccident (riskRes.getRequestReferenceNo() ,	riskRes.getOldRequestReferenceNo() ,sectionIds ,  riskRes ,request  ) ;
			
//			//Document Copy
//			coverDocumentUploadDetailsEndoCopyquote(riskRes);
//			
			//Human Additional info
			productEmpDetailsEndoCopyquote(riskRes);
			
			//Content copy
			contentAndRiskEndoCopyquote(riskRes);
			
			//Building Details copy 
			buildingDetailsEndoCopyquote(riskRes);
			
		//	EserviceBuildingDetails buildData = eBuildingRepo.findByRequestReferenceNoAndRiskId(riskRes.getRequestReferenceNo() , 1 );
			List<EserviceBuildingDetails> buildData = eBuildingRepo.findByRequestReferenceNo(riskRes.getRequestReferenceNo());
			buildData = buildData.stream().filter(distinctByKey(o -> Arrays.asList(o.getRequestReferenceNo()))).collect(Collectors.toList());
			EserviceBuildingDetails buildData1=buildData.get(0);
			return buildData1 ;
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public  void buildingDetailsEndoCopyquote(BuildingCopyRes riskRes) {
		List<BuildingDetails> savelist = new ArrayList<BuildingDetails>();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		try {
			List<BuildingDetails> buildingData=buildingRepo.findByQuoteNoOrderByRiskIdAsc(riskRes.getEndtPrevQuoteNo());
			
			List<BuildingDetails> coverDocList =null;
			coverDocList=buildingRepo.findByRequestReferenceNo(riskRes.getRequestReferenceNo());
			if (coverDocList.size() > 0) {
				buildingRepo.deleteAll(coverDocList);
			}
			if (buildingData!=null) {
				for(BuildingDetails data :buildingData) {
					BuildingDetails savedata = new BuildingDetails();
					savedata = dozerMapper.map(data, BuildingDetails.class);
					savedata.setRequestReferenceNo(riskRes.getRequestReferenceNo());
					savedata.setQuoteNo(null);
					savedata.setEntryDate(new Date());
					savedata.setCreatedBy(riskRes.getCreatedBy());
					savedata.setOriginalPolicyNo(riskRes.getOriginalPolicyNo());
					savedata.setEndorsementDate(new Date());
					savedata.setEndorsementRemarks(riskRes.getEndtCategoryDesc());
					savedata.setEndorsementEffdate(null);
					savedata.setEndtPrevPolicyNo(riskRes.getEndtPrevPolicyNo());
					savedata.setEndtPrevQuoteNo(riskRes.getEndtPrevQuoteNo());
					savedata.setEndtCount(riskRes.getEndtCount());
					savedata.setEndtStatus("P");
					savedata.setEndtCategDesc(riskRes.getEndtCategoryDesc());
					savedata.setEndorsementType(null);
					savedata.setEndorsementTypeDesc(riskRes.getEndTypeDesc());
					savedata.setStatus("E");
					savelist.add(savedata);
					
				}
				buildingRepo.saveAllAndFlush(savelist);
			}
		
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return ;
		}
		return ;
		
	}

	
	public List<DocumentTransactionDetails> coverDocumentUploadDetailsEndoCopyquote(EndRequest req) {
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		List<DocumentTransactionDetails> saveList = new ArrayList<DocumentTransactionDetails>();
		try {
			List<DocumentTransactionDetails> motorData = coverDocUploadDetails.findByQuoteNo(req.getEndtPrevQuoteNo());
			
			List<DocumentTransactionDetails> coverDocList =null;
			coverDocList=coverDocUploadDetails.findByRequestReferenceNo(req.getRequestReferenceNo());
			if (coverDocList.size() > 0) {
				coverDocUploadDetails.deleteAll(coverDocList);
			}
			if (motorData.size() > 0) {
				for (DocumentTransactionDetails data : motorData) {
					DocumentTransactionDetails savedata = new DocumentTransactionDetails();
					savedata = dozerMapper.map(data, DocumentTransactionDetails.class);
					savedata.setRequestReferenceNo(req.getRequestReferenceNo());
					savedata.setQuoteNo(req.getQuoteNo()); 
					savedata.setEntryDate(new Date());
					savedata.setCreatedBy(req.getCreatedBy());
					savedata.setOriginalPolicyNo(req.getOriginalPolicyNo());
					savedata.setEndorsementDate(new Date());
					savedata.setEndorsementRemarks(req.getEndorsementRemarks());
					savedata.setEndorsementEffdate(req.getEndorsementEffdate());
					savedata.setEndtPrevPolicyNo(req.getEndtPrevPolicyNo());
					savedata.setEndtPrevQuoteNo(req.getEndtPrevQuoteNo());
					savedata.setEndtCount(req.getEndtCount());
					savedata.setEndtStatus("P");
					savedata.setIsFinaceYn(req.getIsFinaceYn());
					savedata.setEndtCategDesc(req.getEndtCategDesc());
					savedata.setEndorsementType(null);
					savedata.setEndorsementTypeDesc(req.getEndorsementTypeDesc());
					savedata.setStatus("E");
					saveList.add(savedata);
				
					
				}
				coverDocUploadDetails.saveAllAndFlush(saveList);
			}
	
		}catch(Exception e) {
			e.printStackTrace();
		}
		return saveList;
	}
	
	public void productEmpDetailsEndoCopyquote(BuildingCopyRes req) {
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
					savedata.setEndorsementRemarks(req.getEndtCategoryDesc());
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
	
//	Content and Risk
	public void contentAndRiskEndoCopyquote(BuildingCopyRes req) {
		List<ContentAndRisk> saveList = new ArrayList<ContentAndRisk>();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		try {
			
			List<ContentAndRisk> content = contentAndRiskRepo.findByQuoteNoOrderByRiskIdAsc(req.getEndtPrevQuoteNo());
			List<ContentAndRisk> coverDocList =null;
			coverDocList=contentAndRiskRepo.findByRequestReferenceNo(req.getRequestReferenceNo());
			if (coverDocList.size() > 0) {
				contentAndRiskRepo.deleteAll(coverDocList);
			}
			if (content != null && content.size() > 0) {
				for (ContentAndRisk data : content) {
					ContentAndRisk savedata = new ContentAndRisk();
					savedata = dozerMapper.map(data, ContentAndRisk.class);
					savedata.setEntryDate(new Date());
					savedata.setRequestReferenceNo(req.getRequestReferenceNo());
					savedata.setQuoteNo(null);	 
					savedata.setCreatedBy(req.getCreatedBy());
					savedata.setOriginalPolicyNo(req.getPolicyNo());
					savedata.setEndorsementDate(new Date());
					savedata.setEndorsementRemarks(req.getEndtCategoryDesc());
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
					saveList.add(savedata);
				}
				contentAndRiskRepo.saveAllAndFlush(saveList);
			}
		
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return;
		}
		return ;
	}
	
	private static <T> java.util.function.Predicate<T> distinctByKey(java.util.function.Function<? super T, ?> keyExtractor) {
	    Map<Object, Boolean> seen = new ConcurrentHashMap<>();
	    return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
	}
	public BuildingCopyRes copyBuildingRiskTable(Endorsment ent) {
		try {
			List<EserviceBuildingDetails> BuildingDatas=null;
			Integer count=0;
			List<Object> list=getMasterTableCount(ent.getPolicyNo());
			if (list.size() > 0) {
				count = list.size();
			}
//			Integer count=eBuildingRepo.countByOriginalPolicyNo(ent.getPolicyNo());
			String prevPolicyNo=null;
			String prevQuoteNo=null;
			String newRequestNo =null;
			String prevRequestRefNo=null;
			long pendingcount =0;
			// Response 
			DozerBeanMapper dozerMapper = new DozerBeanMapper();
			if(count>0) {
				List<EserviceBuildingDetails> BuildingList=eBuildingRepo.findByOriginalPolicyNo(ent.getPolicyNo());
				//Compar
				BuildingList.sort(new Comparator<EserviceBuildingDetails>() {

					@Override
					public int compare(EserviceBuildingDetails o1, EserviceBuildingDetails o2) {
						// TODO Auto-generated method stub
						return o1.getEndtCount().compareTo(o2.getEndtCount());
					}
				}.reversed());
				
				pendingcount = BuildingList.stream().filter(m->m.getEndtStatus().equals("P")).count();
				if(BuildingList.stream().filter(m->(m.getEndtStatus().equals("P") && (Integer.parseInt(ent.getEndtType())==m.getEndorsementType()) && (m.getEndorsementEffdate().compareTo(ent.getEndtEffectiveDate())==0))).count()>0) {
					List<EserviceBuildingDetails> pendingList = BuildingList.stream().filter(m->(m.getEndtStatus().equals("P") && (Integer.parseInt(ent.getEndtType())==m.getEndorsementType()))).collect(Collectors.toList());
					BuildingCopyRes res = dozerMapper.map(pendingList.get(0) , BuildingCopyRes.class);
					
					//List<EserviceBuildingDetails> prevDatas = eBuildingRepo.findByPolicyNoAndRiskId(prevPolicyNo , 1 );
					//res.setOldRequestReferenceNo(prevDatas.get(0).getRequestReferenceNo() );
					//res.setPolicyNo(ent.getPolicyNo()+"-"+count) ;
					//return res;
				}
				if (pendingcount > 0) {
					List<EserviceBuildingDetails> pendingData = BuildingList.stream()
							.filter(m -> m.getEndtStatus().equals("P")).collect(Collectors.toList());
					BuildingDatas = pendingData;
					prevPolicyNo = BuildingDatas.get(0).getEndtPrevPolicyNo();
					prevQuoteNo = BuildingDatas.get(0).getEndtPrevQuoteNo();
					newRequestNo = BuildingDatas.get(0).getRequestReferenceNo();
					prevRequestRefNo = BuildingDatas.get(0).getRequestReferenceNo();
					List<EserviceBuildingDetails> rows = eBuildingRepo
							.findByRequestReferenceNoAndProductId(prevRequestRefNo, ent.getProductId().toPlainString());
					List<EserviceSectionDetails> section = eserSecRepo
							.findByRequestReferenceNoAndProductId(prevRequestRefNo, ent.getProductId().toPlainString());

					if (section.size() > 0 && section != null) {
						eserSecRepo.deleteAll(section);
					}

					List<ContentAndRisk> content = contentAndRiskRepo.findByRequestReferenceNo(prevRequestRefNo);
					if (content.size() > 0 && content != null) {
						contentAndRiskRepo.deleteAll(content);
					}

					List<ProductEmployeeDetails> empDetails = proEmplyeeRepo.findByRequestReferenceNo(prevRequestRefNo);
					if (empDetails.size() > 0 && empDetails != null) {
						proEmplyeeRepo.deleteAll(empDetails);
					}

					List<BuildingDetails> buildingDetails = buildingRepo.findByRequestReferenceNo(prevRequestRefNo);
					if (buildingDetails.size() > 0 && buildingDetails != null) {
						buildingRepo.deleteAll(buildingDetails);
					}

					List<DocumentTransactionDetails> coverDocList = coverDocUploadDetails
							.findByRequestReferenceNo(prevRequestRefNo);
					if (coverDocList.size() > 0 && coverDocList != null) {
						coverDocUploadDetails.deleteAll(coverDocList);
					}
					proEmplyeeRepo.flush();
					coverDocUploadDetails.flush();
					contentAndRiskRepo.flush();
					buildingRepo.flush();
					eserSecRepo.flush();
					eBuildingRepo.deleteAllInBatch(rows);
					eBuildingRepo.flush();
					count--;
				}else {
					BuildingDatas=BuildingList;
					
					if(BuildingList.size()>1) {
						prevPolicyNo=BuildingList.get(0).getPolicyNo();
						prevQuoteNo =BuildingList.get(0).getQuoteNo();
					}else {
						prevPolicyNo=BuildingList.get(0).getPolicyNo();
						prevQuoteNo =BuildingDatas.get(0).getQuoteNo();
					}
				}
				
			}else {
				BuildingDatas=eBuildingRepo.findByPolicyNoAndStatus(ent.getPolicyNo(),"P");
				prevPolicyNo=ent.getPolicyNo();
				prevQuoteNo =BuildingDatas.get(0).getQuoteNo();
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
					/*endtTypeRepo.findByCompanyIdAndProductIdAndStatusAndEndtTypeIdAndEffectiveDateStartLessThanEqualAndEffectiveDateEndGreaterThanEqual(ent.getCompanyId(), ent.getProductId().intValue(), "Y",Integer.parseInt(ent.getEndtType()),new Date(), new Date());*/
			List<EserviceBuildingDetails> BuildingList=eBuildingRepo.findByQuoteNoAndStatusNotOrderByRiskIdAsc(prevQuoteNo,"D");
			
			List<BuildingRiskDetails> buildingRisk = buildRiskRepo.findByQuoteNoAndStatusNotOrderByRiskIdAsc(prevQuoteNo,"D");
			
			List<EserviceBuildingDetails> filteredBuildingList = BuildingList.stream()
				    .filter(m -> buildingRisk.stream()
				        .anyMatch(risk -> m.getRiskId().equals(risk.getRiskId()) 
				                        && m.getQuoteNo().equals(risk.getQuoteNo())
				                        && m.getLocationId().equals(risk.getLocationId())
				                        && m.getSectionId().equals(risk.getSectionId())
				        		))
				    .collect(Collectors.toList());
			List<EserviceBuildingDetails> newBuildingList=new ArrayList<EserviceBuildingDetails>();
			++count;
			for(EserviceBuildingDetails m :filteredBuildingList) {
				BuildingRiskDetails matchingRisk = buildingRisk.stream()
						.filter(risk -> risk.getRiskId().equals(m.getRiskId())
								&& risk.getQuoteNo().equals(m.getQuoteNo())
								&& risk.getLocationId().equals(m.getLocationId())
								&& risk.getSectionId().equals(m.getSectionId()))
						.findFirst().orElse(null);

				if (matchingRisk != null) {

				EserviceBuildingDetails newObject = dozerMapper.map(m , EserviceBuildingDetails.class);
				newObject.setRequestReferenceNo(newRequestNo);
				newObject.setOriginalPolicyNo(ent.getPolicyNo());
				newObject.setEndorsementDate(new Date());
				newObject.setEndorsementRemarks(ent.getEndtRemarks());
				newObject.setEndorsementEffdate(ent.getEndtEffectiveDate());
				newObject.setEndtPrevPolicyNo(prevPolicyNo);
				newObject.setEndtPrevQuoteNo(prevQuoteNo);
				newObject.setEndtCount(new BigDecimal(count));
				newObject.setEndtStatus("P");
				if("851".equalsIgnoreCase(ent.getEndtType()) || "854".equalsIgnoreCase(ent.getEndtType()) )
				{
					newObject.setEndtoptd("Y");					
				}

				newObject.setIsFinyn(entMaster.getEndtTypeCategoryId()==2?"Y":"N");
				newObject.setEndtCategDesc(entMaster.getEndtTypeCategory());
				newObject.setEndorsementType(Integer.parseInt(ent.getEndtType()));
				newObject.setEndorsementTypeDesc(entMaster.getEndtTypeDesc());
				newObject.setStatus("E");
				newObject.setPolicyNo(ent.getPolicyNo()+"-"+count);
				newObject.setQuoteNo(null);
				newObject.setAddress(m.getAddress());				
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
				newObject.setVatPremium(matchingRisk.getVatPremium()==null ? BigDecimal.ZERO : matchingRisk.getVatPremium() );
				newObject.setEndtPremium(matchingRisk.getEndtPremium()==null ? 0 : matchingRisk.getEndtPremium() );
				newObject.setEndtVatPremium(matchingRisk.getEndtVatPremium()==null ? BigDecimal.ZERO : matchingRisk.getEndtVatPremium() );
				newObject.setActualPremiumFc(matchingRisk.getActualPremiumFc()==null ? BigDecimal.ZERO : matchingRisk.getActualPremiumFc() );
				newObject.setActualPremiumLc(matchingRisk.getActualPremiumLc()==null ? BigDecimal.ZERO : matchingRisk.getActualPremiumLc() );
				newObject.setOverallPremiumFc(matchingRisk.getOverallPremiumFc()==null ? BigDecimal.ZERO : matchingRisk.getOverallPremiumFc() );
				newObject.setOverallPremiumLc(matchingRisk.getOverallPremiumLc()==null ? BigDecimal.ZERO : matchingRisk.getOverallPremiumLc() );
				newBuildingList.add(newObject);
			}
		}
			eBuildingRepo.saveAllAndFlush(newBuildingList);
			
			
			BuildingCopyRes res = dozerMapper.map(newBuildingList.get(0) , BuildingCopyRes.class);
			
			//List<EserviceBuildingDetails> prevDatas = eBuildingRepo.findByPolicyNoAndRiskId(prevPolicyNo , 1 );
			List<EserviceBuildingDetails> prevDatas = eBuildingRepo.findByPolicyNo(prevPolicyNo);
			res.setOldRequestReferenceNo(prevDatas.get(0).getRequestReferenceNo() );
			res.setPolicyNo(ent.getPolicyNo()+"-"+count) ;
			res.setEndtPrevPolicyNo(prevDatas.get(0).getPolicyNo());
			res.setEndtCount(new BigDecimal(count));
			res.setOriginalPolicyNo(newBuildingList.get(0).getOriginalPolicyNo());
			res.setEndtStatus(newBuildingList.get(0).getEndtStatus());
			res.setIsFinanceYn(newBuildingList.get(0).getIsFinyn());
			res.setEndtCategoryDesc(newBuildingList.get(0).getEndtCategDesc());
			res.setEndTypeDesc(newBuildingList.get(0).getEndorsementTypeDesc());
			res.setApplicationId(newBuildingList.get(0).getApplicationId());
			res.setLoginId(newBuildingList.get(0).getLoginId());
			res.setSubUserType(newBuildingList.get(0).getSubUserType());
			res.setEndTypeId(ent.getEndtType());
			res.setEndtEffDate(ent.getEndtEffectiveDate());
			res.setPreviousRequestReferenceNo(prevRequestRefNo);
			return res;
			
		}catch(ObjectOptimisticLockingFailureException ex ) {
			return copyBuildingRiskTable(ent);
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	public List<Object> getMasterTableCount(String policyNo) {
		List<Object> list = new ArrayList<Object>();
		try {
			//List<EserviceMotorDetails> list = new ArrayList<EserviceMotorDetails>();
			// Find Latest Record
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Object> query = cb.createQuery(Object.class);
			//Find all
			Root<EserviceBuildingDetails> b = query.from(EserviceBuildingDetails.class);
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

	public List<String> copyBuildingSections( BuildingCopyRes buildingData ) {
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		try {
			String newReqRefNo=buildingData.getRequestReferenceNo() ;
		//	String  oldReqRefNo=buildingData.getOldRequestReferenceNo() ;
			List<EserviceSectionDetails>  oldSecDatas = eserSecRepo.findByQuoteNoAndStatusNotOrderByRiskIdAsc(buildingData.getEndtPrevQuoteNo(),"D") ;
			
			// Building Section Insert
			List<EserviceSectionDetails> buildSecCount = eserSecRepo.findByRequestReferenceNo(newReqRefNo);
			if (buildSecCount.size()>0 && ! buildSecCount.isEmpty()) {
				eserSecRepo.deleteAll(buildSecCount);
			}
			List<SectionDataDetails> secList = sectionDataRepo.findByQuoteNoAndStatusNotOrderByRiskIdAsc(buildingData.getEndtPrevQuoteNo(),"D");
			
//			List<EserviceSectionDetails> filteredSectionList = oldSecDatas.stream()
//				    .filter(m -> secList.stream()
//				        .anyMatch(risk -> m.getRiskId().equals(risk.getRiskId()) 
//				                        && m.getLocationId().equals(risk.getLocationId())
//				                        && m.getSectionId().equals(risk.getSectionId())
//				        		))
//				    .collect(Collectors.toList());
			
			List<String> secListSave = new ArrayList<String>();
			List<EserviceSectionDetails> secListSave1 = new ArrayList<EserviceSectionDetails>();
			if (secList != null && secList.size()>0 ) {
			 
			for (SectionDataDetails section : secList) {
				EserviceSectionDetails secData = new EserviceSectionDetails();
			
				dozerMapper.map(section, secData);
				secData.setSectionName(section.getSectionDesc());
				secData.setRequestReferenceNo(newReqRefNo);
				secData.setUserOpt("Y");
				secData.setPolicyNo(buildingData.getPolicyNo());
				secData.setQuoteNo(null);
				
				secData.setOriginalPolicyNo(buildingData.getOriginalPolicyNo());
				secData.setEndorsementDate(new Date());
				
				secData.setEndorsementEffdate(buildingData.getEndtEffDate());
				secData.setEndtPrevPolicyNo(buildingData.getEndtPrevPolicyNo());
				secData.setEndtPrevQuoteNo(buildingData.getEndtPrevQuoteNo());
				secData.setEndtCount(buildingData.getEndtCount());
				secData.setEndtStatus("P");
				secData.setIsFinaceYn(buildingData.getIsFinanceYn());
				secData.setEndtCategDesc(buildingData.getEndtCategoryDesc());
				secData.setEndorsementType(Integer.parseInt(buildingData.getEndTypeId()));
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
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public String  copyPersonalAccident(String newReqRefNo , String  oldReqRefNo , List<String> sectionIds ,  BuildingCopyRes buildingData ,Endorsment request ) {
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		String res = "" ;
		try {
			List<EserviceCommonDetails> endtData  = eserCommonRepo.findByRequestReferenceNo(newReqRefNo);
			boolean newInsert = false ; 
			
			if(endtData.size() > 0 ) {
				EserviceCommonDetails data = endtData.get(0)   ;
				Integer endtType  = data.getEndorsementType() == null ? 0 : data.getEndorsementType() ;
				if( endtType.equals(Integer.valueOf(request.getEndtType())) && request.getEndtEffectiveDate().compareTo(data.getEndorsementEffdate())==0 ) {
					newInsert = false ;
				} else {
					newInsert = true ;
					eserCommonRepo.deleteAll(endtData)	;
				}
			} else {
				newInsert = true ;
			}
			
			if(newInsert == true) {
				List<EserviceCommonDetails> oldAccData = eserCommonRepo.findByQuoteNoAndStatusNotOrderByRiskIdAsc(buildingData.getEndtPrevQuoteNo() , "D");
				if(oldAccData.size() >0) {
					if( oldAccData !=null && oldAccData.size() > 0) {
						List<EserviceCommonDetails> humanList = new ArrayList<EserviceCommonDetails>();
						for (EserviceCommonDetails old : oldAccData) {
							EserviceCommonDetails accdata = new EserviceCommonDetails();
							
								dozerMapper.map(old , accdata);
								accdata.setRequestReferenceNo(newReqRefNo);
								accdata.setOriginalPolicyNo(buildingData.getOriginalPolicyNo());
								accdata.setEndorsementDate(new Date());
								accdata.setEndorsementRemarks(request.getEndtRemarks());
								accdata.setEndorsementEffdate(request.getEndtEffectiveDate());
								accdata.setEndtPrevPolicyNo(buildingData.getEndtPrevPolicyNo());
								accdata.setEndtPrevQuoteNo(buildingData.getEndtPrevQuoteNo());
								accdata.setEndtCount(buildingData.getEndtCount());
								accdata.setEndtStatus(buildingData.getEndtStatus());
								accdata.setIsFinaceYn(buildingData.getIsFinanceYn());
								accdata.setEndtCategDesc(buildingData.getEndtCategoryDesc());
								accdata.setEndorsementType(Integer.parseInt(request.getEndtType()));
								accdata.setEndorsementTypeDesc(buildingData.getEndTypeDesc());
								accdata.setStatus("E");
								accdata.setPolicyNo(buildingData.getPolicyNo());
								accdata.setQuoteNo(null);
								accdata.setApplicationId(request.getApplicationId());
								accdata.setLoginId(request.getLoginId()==null?buildingData.getLoginId():(request.getLoginId()));
								accdata.setSubUserType(request.getSubUserType());
								humanList.add(accdata);							
						}
							eserCommonRepo.saveAll(humanList);
						
					}
				}
			}
			res = "Saved Succefully" ;
			
			return res;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@PersistenceContext
	private EntityManager em;

	public List<EndorsementCriteriaRes> endorsementBuildingGrid(Endorsment request) {

		 try {

			 
				// Get Datas
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<EndorsementCriteriaRes> query = cb.createQuery(EndorsementCriteriaRes.class);

				// Find All
				Root<EserviceCustomerDetails> c = query.from(EserviceCustomerDetails.class);
				Root<EserviceBuildingDetails> m = query.from(EserviceBuildingDetails.class);
				
				Subquery<BigDecimal> endtPre = query.subquery(BigDecimal.class);
				Root<HomePositionMaster> h = endtPre.from(HomePositionMaster.class);
				endtPre.select(cb.sum(h.get("endtPremium")) ) ;
				Predicate pm1 = cb.equal(h.get("companyId"), m.get("companyId"));
				Predicate pm2 = cb.equal(h.get("productId"), m.get("productId").as(Integer.class));
				Predicate pm3   = cb.like(h.get("policyNo"), m.get("policyNo"));
				endtPre.where(pm1,pm2,pm3);
		
				// Over All Premium Fc
				Subquery<BigDecimal> overAllPremiumFc = query.subquery(BigDecimal.class);
				Root<HomePositionMaster> ocpm1 = overAllPremiumFc.from(HomePositionMaster.class);
				overAllPremiumFc.select(cb.sum(ocpm1.get("overallPremiumFc")));
				Predicate a1 = cb.equal(m.get("quoteNo"),ocpm1.get("quoteNo") );
				overAllPremiumFc.where(a1);
				
				// Over All Premium Lc
				Subquery<BigDecimal> overAllPremiumLc = query.subquery(BigDecimal.class);
				Root<HomePositionMaster> ocpm2 = overAllPremiumLc.from(HomePositionMaster.class);
				overAllPremiumLc.select(cb.sum(ocpm2.get("overallPremiumLc")));
				Predicate a2 = cb.equal(m.get("quoteNo"),ocpm2.get("quoteNo") );
				overAllPremiumLc.where(a2);
				
				Subquery<Long> debitNoteNo = query.subquery(Long.class);
				Root<HomePositionMaster> h2 = debitNoteNo.from(HomePositionMaster.class);
				debitNoteNo.select(cb.max(h2.get("debitNoteNo"))) ;
				Predicate pm4 = cb.equal(h2.get("companyId"), m.get("companyId"));
				Predicate pm5 = cb.equal(h2.get("productId"), m.get("productId").as(Integer.class));
				Predicate pm6   = cb.equal(h2.get("policyNo"), m.get("policyNo"));
				debitNoteNo.where(pm4,pm5,pm6);
				
				Subquery<Long> creditNo = query.subquery(Long.class);
				Root<HomePositionMaster> h3 = creditNo.from(HomePositionMaster.class);
				creditNo.select(cb.max(h3.get("creditNo"))) ;
				Predicate pm7 = cb.equal(h3.get("companyId"), m.get("companyId"));
				Predicate pm8 = cb.equal(h3.get("productId"), m.get("productId").as(Integer.class));
				Predicate pm9   = cb.equal(h3.get("policyNo"), m.get("policyNo"));
				creditNo.where(pm7,pm8,pm9);
				
				Subquery<Long> endtPreTax = query.subquery(Long.class);
				Root<HomePositionMaster> h4 = endtPreTax.from(HomePositionMaster.class);
				endtPreTax.select(cb.sum(h4.get("endtPremiumTax") ) ) ;
				Predicate pm10 = cb.equal(h4.get("companyId"), m.get("companyId"));
				Predicate pm11 = cb.equal(h4.get("productId"), m.get("productId").as(Integer.class));
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
						cb.max(m.get("policyStartDate")).alias("policyStartDate"), cb.max(m.get("policyEndDate")).alias("policyEndDate"),
						cb.max(m.get("endorsementType")).alias("endorsementTypeId"),
						cb.max(m.get("endorsementTypeDesc")).alias("endorsementDesc"),
						cb.max(m.get("endtCategDesc")).alias("endorsementCategoryDesc"),
						cb.max(m.get("endorsementEffdate")).alias("effectiveDate"),
						cb.max(m.get("endtStatus")).alias("endorsementStatus"),
						cb.max(m.get("policyNo")).alias("policyNo"),
						cb.max(m.get("endorsementRemarks")).alias("endorsementRemarks"),
						cb.max(m.get("endorsementDate")).alias("endorsementDate"),
						//Home Position Master
						cb.max(overAllPremiumLc).alias("overallPremiumLc"), cb.max(overAllPremiumFc).alias("overallPremiumFc"),
						cb.sum(endtPre,endtPreTax).alias("endtPremium"),cb.max( m.get("currency")).alias("currency"),
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
				Predicate n3 = cb.equal(m.get("productId"), request.getProductId().toPlainString());
			//	 Predicate n4 = cb.notEqual(m.get("status"),"D");
				//Predicate n4 = cb.in(m.get("status")).value(Arrays.asList("E","P","D"));  
				// m.get("status").in("E","P"));
				Predicate n5 = cb.or(cb.like(m.get("originalPolicyNo"), request.getPolicyNo()),cb.like(m.get("policyNo"), request.getPolicyNo()));
				//Predicate n6 = cb.equal(h.get("quoteNo"), m.get("quoteNo"));
//				Predicate n6 = cb.equal(m.get("sectionId"),"0");

				query.where(n1, n2, n3, n5)
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

	public EserviceBuildingDetails buildingRawEndtStatus(ChangeEndoStatusReq req) {
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		EserviceBuildingDetails savedata = new EserviceBuildingDetails();
		try {
			//Motor 
			List<EserviceBuildingDetails> build = eBuildingRepo.findByQuoteNoOrderByRiskIdAsc(req.getQuoteNo());

			if (build.size() > 0) {
				for (EserviceBuildingDetails data : build) {
					savedata = dozerMapper.map(data, EserviceBuildingDetails.class);
					savedata.setEndtStatus("C");
					savedata.setStatus("P");
					eBuildingRepo.saveAndFlush(savedata);
				}

			}
			// Update EndT Status
			homeEndtStatus(req);
			personolInfoEndtStatus(req);
			contentAndRiskEndtStatus(req);
			buildingDetailsEndtStatus(req);
			coverDocumentUploadDetailsEndtStatus(req);
			//eserviceCustDetailsChangeStatus(req);
			personalAccident(req);
			eserviceCommon(req);
			sectionDataDetails(req);
			eserviceSectionDetails(req);
			//productEmployee(req);
			commonDataDetails(req);
			buildingRiskDetailsEndtStatus(req);
			
			
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return savedata;
	}
	
	private BuildingRiskDetails buildingRiskDetailsEndtStatus(ChangeEndoStatusReq req) {
		BuildingRiskDetails savedata=null;
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		try {
			List<BuildingRiskDetails> commonData = buildRiskRepo.findByQuoteNoOrderByRiskIdAsc(req.getQuoteNo());
			
			if (commonData != null&& commonData.size()>0) {
				for(BuildingRiskDetails data: commonData ) {
					 savedata = new BuildingRiskDetails();
					savedata = dozerMapper.map(data, BuildingRiskDetails.class);
					savedata.setEndtStatus("C");
					savedata.setStatus("P");
					buildRiskRepo.saveAndFlush(savedata);
				}
				
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
	private ProductEmployeeDetails personalAccident(ChangeEndoStatusReq req) {
		ProductEmployeeDetails savedata = new ProductEmployeeDetails();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		try {
			List<ProductEmployeeDetails> pa = paRepo.findByQuoteNo(req.getQuoteNo());
			if (pa.size() > 0) {
				for (ProductEmployeeDetails data : pa) {
					savedata = dozerMapper.map(data, ProductEmployeeDetails.class);
					savedata.setEndtStatus("C");
					savedata.setStatus("P");
					paRepo.saveAndFlush(savedata);
				}
			}
		
		
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return savedata;

		
	}

	private EserviceCommonDetails eserviceCommon(ChangeEndoStatusReq req) {
		EserviceCommonDetails savedata = new EserviceCommonDetails();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		try {
			List<EserviceCommonDetails> common = eserCommonRepo.findByQuoteNoOrderByRiskIdAsc(req.getQuoteNo());
			if (common.size() > 0) {
				for (EserviceCommonDetails data : common) {
					savedata = dozerMapper.map(data, EserviceCommonDetails.class);
					savedata.setEndtStatus("C");
					savedata.setStatus("P");
					eserCommonRepo.saveAndFlush(savedata);
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
			List<DocumentTransactionDetails> motorData = coverDocUploadDetails.findByQuoteNoAndStatusNot(req.getQuoteNo(),"N");
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

	private ContentAndRisk contentAndRiskEndtStatus(ChangeEndoStatusReq req) {
		ContentAndRisk savedata = new ContentAndRisk();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		try {
			List<ContentAndRisk> content=contentAndRiskRepo.findByQuoteNo(req.getQuoteNo());
			if (content.size() > 0) {
				for (ContentAndRisk data : content) {
					savedata = dozerMapper.map(data, ContentAndRisk.class);
					savedata.setEndtStatus("C");
					savedata.setStatus("P");
					contentAndRiskRepo.saveAndFlush(savedata);
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
				savedata.setStatus("P");
				savedata.setIntegrationStatus("S");
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
	private ProductEmployeeDetails productEmployee(ChangeEndoStatusReq req) {
		ProductEmployeeDetails savedata = new ProductEmployeeDetails();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		try {
			List<ProductEmployeeDetails> proEmpList = proEmplyeeRepo.findByQuoteNo(req.getQuoteNo());
			if (proEmpList != null&& proEmpList.size()>0) {
				savedata = dozerMapper.map(proEmpList, ProductEmployeeDetails.class);
				savedata.setEndtStatus("C");
				savedata.setStatus("P");
				proEmplyeeRepo.saveAndFlush(savedata);
			}

		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return savedata;

	}
	/*private EserviceCustomerDetails eserviceCustDetailsChangeStatus(ChangeEndoStatusReq req) {
		EserviceCustomerDetails savedata = new EserviceCustomerDetails();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		try {
			HomePositionMaster homeData=homePosistionRepo.findByQuoteNo(req.getQuoteNo());
			String olsCustomerId=homeData.getCustomerId();
			
			PersonalInfo personalInfoData=personalInforepo.findByCustomerId(olsCustomerId);
			EserviceCustomerDetails custData = custRepo.findByCustomerReferenceNo(personalInfoData.getCustomerReferenceNo());
			if (custData!=null) 
					savedata = dozerMapper.map(custData, EserviceCustomerDetails.class);
					savedata.setEndtStatus("P");
					custRepo.saveAndFlush(savedata);
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return savedata;

		
	}*/
	

}
