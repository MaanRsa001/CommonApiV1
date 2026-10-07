package com.maan.eway.endorsment.util;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import com.maan.eway.bean.DocumentTransactionDetails;
import com.maan.eway.bean.EndtTypeMaster;
import com.maan.eway.bean.EserviceMotorDetails;
import com.maan.eway.bean.EserviceSectionDetails;
import com.maan.eway.bean.HomePositionMaster;
import com.maan.eway.bean.ListItemValue;
import com.maan.eway.bean.MotorDataDetails;
import com.maan.eway.bean.MotorDriverDetails;
import com.maan.eway.bean.PersonalInfo;
import com.maan.eway.bean.SectionDataDetails;
import com.maan.eway.bean.UwQuestionsDetails;
import com.maan.eway.common.req.ChangeEndoStatusReq;
import com.maan.eway.common.req.SequenceGenerateReq;
import com.maan.eway.common.res.BuildingCopyRes;
import com.maan.eway.common.service.impl.GenerateSeqNoServiceImpl;
import com.maan.eway.common.service.impl.MotorGridServiceImpl;
import com.maan.eway.endorsment.request.Endorsment;
import com.maan.eway.repository.DocumentTransactionDetailsRepository;
import com.maan.eway.repository.EServiceMotorDetailsRepository;
import com.maan.eway.repository.EServiceSectionDetailsRepository;
import com.maan.eway.repository.EserviceCustomerDetailsRepository;
import com.maan.eway.repository.HomePositionMasterRepository;
import com.maan.eway.repository.MotorDataDetailsRepository;
import com.maan.eway.repository.MotorDriverDetailsRepository;
import com.maan.eway.repository.PersonalInfoRepository;
import com.maan.eway.repository.SectionDataDetailsRepository;
import com.maan.eway.repository.UwQuestionsDetailsRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
@Service
public class CopyRawTable  {

	@Autowired
	private CopyBuildingRaw section;
	@Autowired
	private EServiceMotorDetailsRepository emotorRepo;
	
	@Autowired	 
	private MotorGridServiceImpl numberGenerate ;
/*	@Autowired
	private EndtTypeMasterRepository endtTypeRepo;
	 */
	@Autowired
	private HomePositionMasterRepository homePosistionRepo;
	
	@Autowired
	private PersonalInfoRepository personalInforepo;
	
	@Autowired
	private MotorDataDetailsRepository motorDataDetepo;
	
	@Autowired
	private EServiceMotorDetailsRepository repo;
	
	@Autowired
	private EserviceCustomerDetailsRepository custRepo ;
	
	@Autowired
	private MotorDriverDetailsRepository motordrivDetepo;
	
	@Autowired
	private DocumentTransactionDetailsRepository coverDocUploadDetails;
	
	@Autowired
	private UwQuestionsDetailsRepository uwquestionRepo;
	
	@Autowired
	private SectionDataDetailsRepository sectionDataRepo;
	@Autowired
	private EServiceSectionDetailsRepository eserSecRepo;
	
	@Autowired
	private GenerateSeqNoServiceImpl genSeqNoService ; 

	 

	@PersistenceContext
	private EntityManager em;
	private Logger log = LogManager.getLogger(MotorGridServiceImpl.class);
	
	public List<EserviceMotorDetails> copyMotorRaw(Endorsment ent, EndtTypeMaster entMaster) {
		try {
			List<EserviceMotorDetails> motor=null;
			//Integer count=emotorRepo.countByOriginalPolicyNo(ent.getPolicyNo());
			List<Object> list=getMasterTableCount(ent.getPolicyNo());
			Integer	count = list.size();
			String prevPolicyNo=null;
			String prevQuoteNo=null;
			String prevRequestRefNo=null;
			String newRequestNo =null;
			long pendingcount =0;
			if(count>0) {
				List<EserviceMotorDetails> motors=emotorRepo.findByOriginalPolicyNo(ent.getPolicyNo());
				motors=motors.stream().filter(distinctByKey(m ->m.getPolicyNo())).collect(Collectors.toList());
				//Compar
				motors.sort(new Comparator<EserviceMotorDetails>() {

					@Override
					public int compare(EserviceMotorDetails o1, EserviceMotorDetails o2) {
						// TODO Auto-generated method stub
						return (o1.getEndtCount().compareTo(o2.getEndtCount()));
					}
				}.reversed());
				
				pendingcount = motors.stream().filter(m->(m.getEndtStatus().equals("P") )).count();
				
				if(motors.stream().filter(m->(m.getEndtStatus().equals("P") && (Integer.parseInt(ent.getEndtType())==m.getEndorsementType())) && m.getEndorsementEffdate().compareTo(ent.getEndtEffectiveDate())==0).count()>0) {
					//return motors;
				}
				if(pendingcount>0) {					  
					 List<EserviceMotorDetails> pendingData = motors.stream().filter(m->(m.getEndtStatus().equals("P")) ).collect(Collectors.toList());
					 motor= pendingData;
					 prevPolicyNo=motor.get(0).getEndtPrevPolicyNo();
					 prevQuoteNo=motor.get(0).getEndtPrevQuoteNo();
					 newRequestNo=motor.get(0).getRequestReferenceNo();
					 prevRequestRefNo=motor.get(0).getRequestReferenceNo();
					 
					 List<EserviceMotorDetails> rows = emotorRepo.findByRequestReferenceNo(prevRequestRefNo);
						emotorRepo.deleteAllInBatch(rows);
						emotorRepo.flush();
					 count--;
				}else {
					motor=motors.stream().filter(m->m.getEndtStatus().equals("C")).collect(Collectors.toList());
					
					if(motors.size()>1) {
						prevPolicyNo=motors.get(0).getPolicyNo();
						prevQuoteNo =motors.get(0).getQuoteNo();
						prevRequestRefNo=motor.get(0).getRequestReferenceNo();
					}else {
						prevPolicyNo=motor.get(0).getPolicyNo();
						prevQuoteNo =motor.get(0).getQuoteNo();
						prevRequestRefNo=motor.get(0).getRequestReferenceNo();
					}
				}
				
			}else {
				motor=emotorRepo.findByPolicyNoAndStatus(ent.getPolicyNo(),"P");
				prevPolicyNo=ent.getPolicyNo();
				prevQuoteNo =motor.get(0).getQuoteNo();
				prevRequestRefNo=motor.get(0).getRequestReferenceNo();

			}
			if(pendingcount==0) {
			//	newRequestNo=numberGenerate.generateRequestNo(ent.getCompanyId(), ent.getBranchCode(), String.valueOf(ent.getProductId()));
				// Generate Seq
	 			SequenceGenerateReq generateSeqReq = new SequenceGenerateReq();
	 		 	generateSeqReq.setInsuranceId(ent.getCompanyId());  
	 		 	generateSeqReq.setProductId(ent.getProductId().toString());
	 		 	generateSeqReq.setType("2");
	 		 	generateSeqReq.setTypeDesc("REQUEST_REFERENCE_NO");
	 		 	newRequestNo =  genSeqNoService.generateSeqCall(generateSeqReq);
			}
			List<EserviceMotorDetails> save=savemotor(ent, entMaster, prevQuoteNo, count, newRequestNo, prevPolicyNo, prevRequestRefNo);
			// Section Copy
			BuildingCopyRes riskRes=new BuildingCopyRes();
			riskRes.setRequestReferenceNo(newRequestNo);
			riskRes.setOldRequestReferenceNo(prevRequestRefNo );
			riskRes.setPolicyNo(ent.getPolicyNo()+"-"+count) ;
			riskRes.setEndtPrevPolicyNo(prevPolicyNo);
			riskRes.setEndtPrevQuoteNo(prevQuoteNo);
			riskRes.setEndtCount(new BigDecimal(count));
			riskRes.setOriginalPolicyNo(save.get(0).getOriginalPolicyNo());
			riskRes.setEndtStatus(save.get(0).getEndtStatus());
			riskRes.setIsFinanceYn(save.get(0).getIsFinaceYn());
			riskRes.setEndtCategoryDesc(save.get(0).getEndtCategDesc());
			riskRes.setEndTypeDesc(save.get(0).getEndorsementTypeDesc());
			riskRes.setApplicationId(save.get(0).getApplicationId());
			riskRes.setLoginId(save.get(0).getLoginId());
			riskRes.setEndTypeId(ent.getEndtType());
			riskRes.setEndtEffDate(ent.getEndtEffectiveDate());
			riskRes.setSubUserType(save.get(0).getSubUserType());
			List<String> sectionIds = section.copyBuildingSections( riskRes ) ;
			return save;
		}catch(ObjectOptimisticLockingFailureException ex ) {
			return copyMotorRaw(ent, entMaster);
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public  <T> java.util.function.Predicate<T> distinctByKey(Function<? super T, Object> keyExtractor) {
		Map<Object, Boolean> uniqueMap = new ConcurrentHashMap<>();
		return t -> uniqueMap.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
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
					Root<EserviceMotorDetails> b = query.from(EserviceMotorDetails.class);
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
	private List<EserviceMotorDetails> savemotor(Endorsment ent, EndtTypeMaster entMaster,String prevQuoteNo,Integer count,String newRequestNo,String prevPolicyNo,String prevRequestRefNo){
		//EndtTypeMaster entMaster=endtTypeRepo.findByCompanyIdAndProductIdAndStatusAndEndtTypeIdAndEffectiveDateStartGreaterThanEqualAndEffectiveDateEndLessThanEqual(ent.getCompanyId(), ent.getProductId().intValue(), "Y",Integer.parseInt(ent.getEndtType()), new Date(), new Date());
		List<EserviceMotorDetails> motors=emotorRepo.findByQuoteNoAndStatusOrderByRiskIdAsc(prevQuoteNo,"P");
		List<EserviceMotorDetails> newMotors=new ArrayList<EserviceMotorDetails>();
		++count;
		for(EserviceMotorDetails m :motors) {
			DozerBeanMapper dozerMapper = new DozerBeanMapper();
			EserviceMotorDetails newObject = dozerMapper.map(m , EserviceMotorDetails.class);
			newObject.setCustomerId(m.getCustomerId());
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
			newMotors.add(newObject);
			
			List<UwQuestionsDetails> olduwquestion = uwquestionRepo.findByCompanyIdAndProductIdAndRequestReferenceNoAndVehicleId(ent.getCompanyId(),ent.getProductId().intValue(),prevRequestRefNo,newObject.getRiskId());
			List<UwQuestionsDetails> newuwquestions=new ArrayList<UwQuestionsDetails>();
			for (UwQuestionsDetails ouw : olduwquestion) {
				UwQuestionsDetails newuw = dozerMapper.map(ouw , UwQuestionsDetails.class);
				newuw.setRequestReferenceNo(newRequestNo);
				newuwquestions.add(newuw);
			}
			uwquestionRepo.deleteAllInBatch(newuwquestions);
			uwquestionRepo.saveAllAndFlush(newuwquestions);
		}
		List<EserviceMotorDetails> save = emotorRepo.saveAllAndFlush(newMotors);
		return save;
				
	}
	
	public EserviceMotorDetails eserviceMotorEndtStatus(ChangeEndoStatusReq req) {
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		EserviceMotorDetails savedata = new EserviceMotorDetails();
		try {
			//Motor 
			List<EserviceMotorDetails> motors = repo.findByQuoteNoOrderByRiskIdAsc(req.getQuoteNo());

			if (motors.size() > 0) {
				for (EserviceMotorDetails data : motors) {
					savedata = dozerMapper.map(data, EserviceMotorDetails.class);
					savedata.setEndtStatus("C");
					savedata.setStatus("P");
					repo.saveAndFlush(savedata);
				}

			}
			// Update EndT Status
			homeEndtStatus(req);
			personolInfoEndtStatus(req);
			motorDataDetailsEndtStatus(req);
			motorDriverDetailsEndtStatus(req);
			coverDocumentUploadDetailsEndtStatus(req);
			sectionDataDetails(req);
			eserviceSectionDetails(req);
			//eserviceCustDetailsChangeStatus(req);

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

	private MotorDriverDetails motorDriverDetailsEndtStatus(ChangeEndoStatusReq req) {
		MotorDriverDetails savedata = new MotorDriverDetails();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		try {
			List<MotorDriverDetails> motorDriverData = motordrivDetepo.findByQuoteNo(req.getQuoteNo());
			if (motorDriverData.size() > 0) {
				for (MotorDriverDetails data : motorDriverData) {
					savedata = dozerMapper.map(data, MotorDriverDetails.class);
					savedata.setEndtStatus("C");
					savedata.setStatus("P");
					motordrivDetepo.saveAndFlush(savedata);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return savedata;

		
	}

	private MotorDataDetails motorDataDetailsEndtStatus(ChangeEndoStatusReq req) {
		MotorDataDetails savedata = new MotorDataDetails();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		try {
			List<MotorDataDetails> motorData=motorDataDetepo.findByQuoteNo(req.getQuoteNo());
			if (motorData.size() > 0) {
				for (MotorDataDetails data : motorData) {
					savedata = dozerMapper.map(data, MotorDataDetails.class);
					savedata.setEndtStatus("C");
					savedata.setStatus("P");
					motorDataDetepo.saveAndFlush(savedata);
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
				savedata.setEffectiveDate(new Date());;
				homePosistionRepo.saveAndFlush(savedata);
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
					savedata.setStatus("P");
					custRepo.saveAndFlush(savedata);
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return savedata;

		
	}*/


}
