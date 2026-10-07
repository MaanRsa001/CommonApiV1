package com.maan.eway.common.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dozer.DozerBeanMapper;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestHeader;

import com.google.gson.Gson;
import com.maan.eway.admin.service.RestTemplateApiService;
import com.maan.eway.bean.CompanyProductMaster;
import com.maan.eway.bean.EserviceBuildingDetails;
import com.maan.eway.bean.EserviceCommonDetails;
import com.maan.eway.bean.EserviceMotorDetails;
import com.maan.eway.bean.EserviceTravelDetails;
import com.maan.eway.bean.MsAssetDetails;
import com.maan.eway.bean.MsHumanDetails;
import com.maan.eway.bean.MsVehicleDetails;
import com.maan.eway.bean.ProductSectionMaster;
import com.maan.eway.bean.SectionCoverMaster;
import com.maan.eway.bean.UwQuestionsDetails;
import com.maan.eway.bean.UwQuestionsDetailsArch;
import com.maan.eway.common.req.UwQuestionsDetailsGetReq;
import com.maan.eway.common.req.UwQuestionsDetailsSaveReq;
import com.maan.eway.common.res.CommonRes;
import com.maan.eway.common.res.UwQuestionsDetailsRes;
import com.maan.eway.common.service.UwQuestionsDetailsService;
import com.maan.eway.error.Error;
import com.maan.eway.master.req.SectionCoverMasterSaveReq;
import com.maan.eway.repository.EServiceMotorDetailsRepository;
import com.maan.eway.repository.EserviceBuildingDetailsRepository;
import com.maan.eway.repository.EserviceCommonDetailsRepository;
import com.maan.eway.repository.EserviceTravelDetailsRepository;
import com.maan.eway.repository.MsAssetDetailsRepository;
import com.maan.eway.repository.MsCommonDetailsRepository;
import com.maan.eway.repository.MsHumanDetailsRepository;
import com.maan.eway.repository.MsVehicleDetailsRepository;
import com.maan.eway.repository.ProductSectionMasterRepository;
import com.maan.eway.repository.SectionCoverMasterRepository;
import com.maan.eway.repository.UwQuestionsDetailsArchRepository;
import com.maan.eway.repository.UwQuestionsDetailsRepository;
import com.maan.eway.res.SuccessRes;

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
@Transactional
public class UwQuesitonsDetailsServiceImpl implements UwQuestionsDetailsService {

	@Autowired
	private UwQuestionsDetailsRepository uwRepo;

	@Autowired
	private UwQuestionsDetailsArchRepository uwArchRepo;

	@Autowired
	private MsVehicleDetailsRepository msVehRepo;
	
	@Autowired
	private MsAssetDetailsRepository msAssetRepo ;
	
	@Autowired
	private MsHumanDetailsRepository msHumanRepo ;
	
	@Autowired
	private EServiceMotorDetailsRepository eserMotRepo ;
	
	@Autowired
	private EserviceCommonDetailsRepository eserHumanRepo ;
	
	@Autowired
	private EserviceBuildingDetailsRepository eserBuildRepo ;
	
	@Autowired
	private EserviceTravelDetailsRepository eserTraRepo ;
	
	@Autowired
	private MsCommonDetailsRepository msCommanRepo;
	
	@Autowired
    private ProductSectionMasterRepository productsectionrepo;
	
	
	@Autowired
	private RestTemplateApiService restTemplateApiService;
	 
	
	@Autowired
	private SectionCoverMasterRepository sectionCoverMaster;
	
	

	@Value(value = "${DefaultInsertcover}")
	private String DefaultInsertcover;
	
	@PersistenceContext
	private EntityManager em;
	
	

	Gson json = new Gson();

	private Logger log = LogManager.getLogger(UwQuesitonsDetailsServiceImpl.class);
	
	
	@Override
	public List<Error> validateUwQuestions(List<UwQuestionsDetailsSaveReq> data) {
		List<Error> error = new ArrayList<Error>();

		try {
			Long row = 0L ;

			for(UwQuestionsDetailsSaveReq req : data) {
				row =row+1;
			if (StringUtils.isBlank(req.getCompanyId())) {
				error.add(new Error("01", "CompanyId", "Please Enter CompanyId" +row));
			} else if (req.getCompanyId().length() > 20) {
				error.add(new Error("01", "CompanyId", "Please Enter CompanyId within 20 Characters"+row));
			}

			if (StringUtils.isBlank(req.getProductId())) {
				error.add(new Error("02", "ProductId", "Please Enter ProductId"+row));
			}
			if (StringUtils.isNotBlank(req.getUwQuestionDesc()) &&req.getUwQuestionDesc().length() > 500) {
				error.add(new Error("03", "UwQuestionDesc", "Please Enter UwQuestionDesc within 100 Characters"+row));
			}
			if (StringUtils.isNotBlank(req.getQuestionType()) &&req.getQuestionType().length() > 100) {
				error.add(new Error("04", "QuestionType", "Please Enter QuestionType within 100 Characters"+row));
			
			if (StringUtils.isNotBlank(req.getQuestionType()) &&req.getQuestionType().equalsIgnoreCase("02") && StringUtils.isBlank(req.getValue())) {
				error.add(new Error("04", "Value", "Please Enter Value"+row));
			}
			else if(StringUtils.isNotBlank(req.getValue()) && req.getValue().length()>100) {
				error.add(new Error("04", "Value", "Please Enter Value within 100 Characters"+row));					
			}
			}
			if (StringUtils.isNotBlank(req.getRemarks()) && req.getRemarks().length() > 100) {
				error.add(new Error("05", "Remarks", "Please Enter Remarks within 100 Characters"+row));
			}
			if (StringUtils.isBlank(req.getRequestReferenceNo())) {
				error.add(new Error("06", "RequestReferenceNo", "Please Enter RequestReferenceNo"+row));
			}
			else if (req.getQuestionType().length() > 20) {
				error.add(new Error("06", "RequestReferenceNo", "Please Enter RequestReferenceNo within 20 Characters"+row));
			}
			if (StringUtils.isBlank(req.getVehicleId())) {
				error.add(new Error("07", "VehicleId", "Please Enter VehicleId"+row));
			}
			if (StringUtils.isBlank(req.getUwQuestionId())) {
				error.add(new Error("08", "UwQuestionId", "Please Enter UwQuestionId"+row));
			}
			if (StringUtils.isNotBlank(req.getValue()) &&req.getValue().length() > 100) {
				error.add(new Error("09", "Value", "Please Enter Value within 100 Characters"+row));
			}
			if (StringUtils.isNotBlank(req.getCreatedBy()) &&req.getCreatedBy().length() > 100) {
				error.add(new Error("10", "CreatedBy", "Please Enter CreatedBy within 100 Characters"+row));
			}
			if (StringUtils.isNotBlank(req.getUpdatedBy()) &&req.getUpdatedBy().length() > 100) {
				error.add(new Error("11", "UpdatedBy", "Please Enter UpdatedBy within 100 Characters"+row));
			}
			if (StringUtils.isBlank(req.getBranchCode())) {
				error.add(new Error("12", "BranchCode", "Please Enter BranchCode"+row));
			}
			else if (req.getBranchCode().length() > 20) {
				error.add(new Error("12", "BranchCode", "Please Enter BranchCode within 20 Characters"+row));
			}
			
			if (StringUtils.isNotBlank(req.getMandatoryYn()) && req.getMandatoryYn().equalsIgnoreCase("Y") && StringUtils.isBlank(req.getValue())) {
				error.add(new Error("13", "UW Question Value", "Please Enter Under Writer Question Value at row no  "+row));
			}
			
			}
		} catch (Exception e) {

			log.error(e);
			e.printStackTrace();
			error.add(new Error("15", "Common Error", e.getMessage()));
		}
		return error;
	}



	@Override
	public SuccessRes saveUwQuestions(List<UwQuestionsDetailsSaveReq> req) {
		SuccessRes res = new SuccessRes();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		
		try {
			Date entryDate = new Date();
			String refNo = req.get(0).getRequestReferenceNo();
			Integer vehId = Integer.valueOf(req.get(0).getVehicleId());
			String companyId = req.get(0).getCompanyId();
			String productId = req.get(0).getProductId();
			String sectionId = req.get(0).getProductId();
			
			// Save Old Datas
			List<UwQuestionsDetails> oldDatas = uwRepo.findByRequestReferenceNo(refNo);
			List<UwQuestionsDetailsArch> saveArchs = new ArrayList<UwQuestionsDetailsArch>();
			Long count = uwArchRepo.countByRequestReferenceNo(refNo);
			//Integer sno = Integer.valueOf(count.toString())+1;
			if(count > 0 ) {
//				uwArchRepo.deleteByRequestReferenceNoAndVehicleId(refNo , vehId);

				uwArchRepo.deleteByRequestReferenceNo(refNo);
			}
			if ( oldDatas.size() > 0 ) {
				entryDate = oldDatas.get(0).getEntryDate() !=null ? oldDatas.get(0).getEntryDate() : new Date()  ;
			}
			
			oldDatas.forEach( o -> {
				UwQuestionsDetailsArch arch = new UwQuestionsDetailsArch();
				dozerMapper.map(o, arch);
				arch.setArchId(o.getVehicleId());
				saveArchs.add(arch);
					
			});
			uwArchRepo.saveAllAndFlush(saveArchs);
			uwRepo.deleteAll(oldDatas);	
			
			List<UwQuestionsDetails> saveList = new ArrayList<UwQuestionsDetails>();
			BigDecimal totalUwLoading = BigDecimal.ZERO ;
			for(UwQuestionsDetailsSaveReq data : req) {
				UwQuestionsDetails saveData = new UwQuestionsDetails();
				
				saveData = dozerMapper.map(data,UwQuestionsDetails.class);
				saveData.setEntryDate(entryDate);		
				saveData.setUpdatedDate(new Date());			
				saveData.setStatus(data.getStatus());
				saveData.setTextValue(data.getTextValue());
				saveData.setStatus(data.getStatus());
				saveData.setQuestionCategory(data.getQuestionCategory());
				saveData.setQuestionCategoryDesc(data.getQuestionCategoryDesc());
				if((StringUtils.isNotBlank(data.getStatus())) && (data.getStatus().equalsIgnoreCase("R")) ){
					saveData.setIsReferral("Y");
				}
				else {
					saveData.setIsReferral("N");							
				}
				//saveData.setLoading(data.getLoadingPercent()==null? BigDecimal.ZERO : new BigDecimal (data.getLoadingPercent()));
				totalUwLoading = totalUwLoading.add(saveData.getLoading());
				saveList.add(saveData);
			
			}
						
			
			CompanyProductMaster product = getCompanyProductMasterDropdown(companyId , productId); 
			Integer cdRefNo = null;
			Integer vdRefNo = null;
			Integer msRefNo = null;
			
			if(product.getMotorYn().equalsIgnoreCase("M") ) {
				EserviceMotorDetails motData = eserMotRepo.findByRequestReferenceNoAndRiskId(refNo, vehId)	;	
				MsVehicleDetails vehicleData = msVehRepo.findByVdRefno(Long.valueOf(motData.getVdRefNo()) );
				if(vehicleData!=null) {
					cdRefNo = motData.getCdRefno() ;
					vdRefNo = motData.getVdRefNo();
					msRefNo = motData.getMsRefno();
					
					vehicleData.setUwLoading(totalUwLoading);
					msVehRepo.saveAndFlush(vehicleData);
					
				}
			} else if(product.getMotorYn().equalsIgnoreCase("H") && "4".equalsIgnoreCase(productId) ) {
				EserviceTravelDetails traData = eserTraRepo.findByRequestReferenceNo(refNo)	;
				MsHumanDetails humanData = msHumanRepo.findByVdRefno(Long.valueOf(traData.getVdRefNo()) );
				if(humanData!=null) {
					cdRefNo = traData.getCdRefno() ;
					vdRefNo = traData.getVdRefNo();
					msRefNo = traData.getMsRefno();
					
					humanData.setUwLoading(totalUwLoading);
					msHumanRepo.saveAndFlush(humanData);
					
				}
			} else if(product.getMotorYn().equalsIgnoreCase("H") ) {
				List<EserviceCommonDetails> comDatas = eserHumanRepo.findByRequestReferenceNo(refNo )	;
				for (EserviceCommonDetails comData : comDatas) {
					MsHumanDetails humanData = msHumanRepo.findByVdRefno(Long.valueOf(comData.getVdRefNo()) );
					if(humanData !=null) {
						cdRefNo = comData.getCdRefno() ;
						vdRefNo = comData.getVdRefNo();
						msRefNo = comData.getMsRefno();
						
						humanData.setUwLoading(totalUwLoading);
						msHumanRepo.saveAndFlush(humanData);
						
					}
					
			}
			}else  {
					List<EserviceBuildingDetails> buildings = eserBuildRepo.findByRequestReferenceNo(refNo )	;
					for (EserviceBuildingDetails buildData : buildings) {
						MsAssetDetails assetData = msAssetRepo.findByVdRefno(Long.valueOf(buildData.getVdRefno()) );
						if(assetData !=null) {
							cdRefNo = buildData.getCdRefno() ;
							vdRefNo = buildData.getVdRefno();
							msRefNo = buildData.getMsRefno();
							
				//			assetData.setUwLoading(totalUwLoading);
							msAssetRepo.saveAndFlush(assetData);
						}
					}
					
					
			}
			
			
			for (UwQuestionsDetails  o : saveList )  {
				o.setVdRefNo(vdRefNo);
				o.setCdRefno(cdRefNo);
				o.setMsRefno(msRefNo);
			}
			uwRepo.saveAll(saveList);	
			res.setResponse("Updated Successfully");
				
			} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --->" + e.getMessage());
			return null;
		}
		return res;
	}
	
	public SuccessRes saveUwQuestion(List<UwQuestionsDetailsSaveReq> req, @RequestHeader("Authorization") String token) {
		SuccessRes res = new SuccessRes();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		try {

			Date entryDate = new Date();
			String refNo = req.get(0).getRequestReferenceNo();
			Integer vehId = Integer.valueOf(req.get(0).getVehicleId());
			String companyId = req.get(0).getCompanyId();
			String productId = req.get(0).getProductId();
			String sectionId = req.get(0).getSectionId();
					
			// Check for old Records 
		
			List<UwQuestionsDetails> oldDatas = uwRepo.findByRequestReferenceNo(refNo);
			List<UwQuestionsDetailsArch> saveArchs = new ArrayList<UwQuestionsDetailsArch>();
			Long count = uwArchRepo.countByRequestReferenceNo(refNo);
			//Integer sno = Integer.valueOf(count.toString())+1;
			if(count > 0 ) {
//				uwArchRepo.deleteByRequestReferenceNoAndVehicleId(refNo , vehId);

				uwArchRepo.deleteByRequestReferenceNo(refNo);
			}
			if ( oldDatas.size() > 0 && oldDatas.get(0)!=null  ) {
				
				entryDate = oldDatas.get(0).getEntryDate() !=null ? oldDatas.get(0).getEntryDate() : new Date()  ;
				oldDatas.forEach( o -> {
					UwQuestionsDetailsArch arch = new UwQuestionsDetailsArch();
					dozerMapper.map(o, arch);
					arch.setArchId(o.getVehicleId());
					saveArchs.add(arch);
					uwRepo.deleteAll(oldDatas);	

						
				});
			}
			
			
			uwArchRepo.saveAllAndFlush(saveArchs);
			
			// Save for new Records 
			List<UwQuestionsDetails> saveList = new ArrayList<UwQuestionsDetails>();
			for(UwQuestionsDetailsSaveReq data : req) {
				UwQuestionsDetails saveData = new UwQuestionsDetails();
				
				saveData = dozerMapper.map(data,UwQuestionsDetails.class);
				saveData.setEntryDate(entryDate);		
				saveData.setUpdatedDate(new Date());			
				saveData.setStatus(data.getStatus());
				saveData.setTextValue(data.getTextValue());
				saveData.setStatus(data.getStatus());
				//saveData.setQuestfionCategory(data.getQuestionCategory());
				saveData.setQuestionCategory(data.getQuestionCategory());
				saveData.setQuestionCategoryDesc(data.getQuestionCategoryDesc());
				saveData.setSectionId(data.getSectionId());

				if((StringUtils.isNotBlank(data.getStatus())) && (data.getStatus().equalsIgnoreCase("R")) ){
					saveData.setIsReferral("Y");
				}
				else {
					saveData.setIsReferral("N");							
				}
				
				// save Uw Loading in One Time Tabel..
				UwQuestionsDetails loadingdetails=	OTTUwLoading(saveData,data.getLocationId(),req);
				if(loadingdetails!=null )
				{
					saveData.setVdRefNo(loadingdetails.getVdRefNo());
					saveData.setCdRefno(loadingdetails.getCdRefno());
					saveData.setMsRefno(loadingdetails.getMsRefno());
					saveData.setLoading(loadingdetails.getLoading()!=null?loadingdetails.getLoading(): new BigDecimal(0));
				}
				saveList.add(saveData);
			
			}
			uwRepo.saveAllAndFlush(saveList);	
			
			// Default Cover Insert .............
			Map<String, BigDecimal> sectionLoading = saveList.stream()
				    .collect(Collectors.toMap(
				        UwQuestionsDetails::getSectionId,   // Key: sectionId
				        UwQuestionsDetails::getLoading,     // Value: loading
				        (existing, replacement) -> replacement // Replace old value with new one
				    ));
			InsertDEfaultCover(sectionLoading,productId,companyId,req.get(0).getCreatedBy(),token);
			res.setResponse("Updated Successfully");
			
		} catch (Exception Ex) {
			System.out.println("Exception Occured In SaveUW Question Details ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
			Ex.printStackTrace();
			log.info("Exception is --->" + Ex.getMessage());
			res.setResponse("Failed");
		}
	return res;
	}
	
	public void InsertDEfaultCover(Map<String, BigDecimal> sectionLoading,String pid,String Cid,String createdby,String token)
	{
		try {
			Date entryDate = new Date();
			String url = DefaultInsertcover;
			List<Integer> coverid = List.of(90001, 90002);
			for (Map.Entry<String, BigDecimal> entry : sectionLoading.entrySet()) {
				Integer sectionId = Integer.valueOf(entry.getKey());
				Integer loading = entry.getValue().intValue();
				Integer pid1 = Integer.valueOf(pid);
				System.out.println("Section: " + sectionId + ", Loading: " + loading);

				List<SectionCoverMaster> covermaster2 = sectionCoverMaster
						.findByCompanyIdAndProductIdAndSectionIdAndCoverIdOrderByAmendIdDesc(Cid, pid1, sectionId,
								90001);
				List<SectionCoverMaster> covermaster1 = sectionCoverMaster
						.findByCompanyIdAndProductIdAndSectionIdAndCoverIdOrderByAmendIdDesc(Cid, pid1, sectionId,
								90002);
               
				List<SectionCoverMasterSaveReq> reqlist = new ArrayList();
				if (covermaster2 == null || covermaster2.isEmpty() || covermaster2.size() < 0) {
					// Request Mapping
					
					SectionCoverMasterSaveReq req = new SectionCoverMasterSaveReq();
					req.setSectionId(sectionId.toString());
					req.setProductId(pid1.toString());
					req.setCoverId("90001");
					req.setCompanyId(Cid);
					req.setCreatedBy(createdby);
					req.setStatus("Y");
					req.setEffectiveDateStart(entryDate);
					req.setAgencyCode("99999");
					req.setBranchCode("99999");
					reqlist.add(req);
				}
				else if(covermaster1 == null || covermaster1.isEmpty() || covermaster1.size() < 0) {
					SectionCoverMasterSaveReq req = new SectionCoverMasterSaveReq();
					req.setSectionId(sectionId.toString());
					req.setProductId(pid1.toString());
					req.setCoverId("90002");
					req.setCompanyId(Cid);
					req.setCreatedBy(createdby);
					req.setStatus("Y");
					req.setEffectiveDateStart(entryDate);
					req.setAgencyCode("99999");
					req.setBranchCode("99999");
					reqlist.add(req);
				}

				
				System.out.println("The Cover Insert :"+reqlist);	
					String removedBearer = token.replaceAll("Bearer ", "").split(",")[0];
					CommonRes CommonRes1 = restTemplateApiService.callcoverinsertapi(url, reqlist, removedBearer);
                  
					
					List<SectionCoverMaster> basecover = sectionCoverMaster
							.findByCompanyIdAndProductIdAndSectionIdAndStatusOrderByAmendIdDesc(Cid, pid1, sectionId,
									"Y");
					Integer Discouncoverid = basecover.stream().filter(a -> a.getCoverageType().equals("B"))
							.map(SectionCoverMaster::getCoverId).findFirst().orElse(null);
					
					// update the base rate 1
					List<SectionCoverMaster> coverdetails =	sectionCoverMaster.findByCompanyIdAndProductIdAndSectionIdAndCoverIdInOrderByAmendIdDesc(Cid,pid1,sectionId,coverid);
                      if(coverdetails != null || !coverdetails.isEmpty() || coverdetails.size() > 0)
                      {
                    	  coverdetails= coverdetails.stream().map(a->{
                    		 a.setBaseRate(BigDecimal.ONE);
                    		 a.setCoverBasedOn("uwLoading");
                    		 a.setDiscountCoverId(Discouncoverid);
                    		 a.setCoverageLimit(new BigDecimal("1000000000"));
                    		 return a;
                    	  }).collect(Collectors.toList());
                    	  
                    	  sectionCoverMaster.saveAll(coverdetails); 
                      }
                 }	

				

			

		} catch (Exception EE) {
			EE.printStackTrace();
			log.info("Exception is --->" + EE.getMessage());
		}
	}
			
	
	
	public UwQuestionsDetails OTTUwLoading(UwQuestionsDetails d,String Locationid,List<UwQuestionsDetailsSaveReq> req)
	{
		UwQuestionsDetails data = new UwQuestionsDetails();
	try {
		
		String Companyid=d.getCompanyId();
		String pid= d.getProductId()!=null?d.getProductId().toString():"0";
		Integer LocationId= Locationid!=null?Integer.parseInt(Locationid):0;
	// check Product Type 
      // CompanyProductMaster product = getCompanyProductMasterDropdown(Companyid,pid); 
		List<ProductSectionMaster> product=productsectionrepo.findByProductIdAndSectionIdAndCompanyIdOrderByAmendIdDesc(Integer.valueOf(pid),Integer.valueOf(d.getSectionId()),d.getCompanyId());
     	String ProductType =product.isEmpty()?"Empty": product.get(0).getMotorYn();
		//update Loading For Motor
           if(ProductType.equalsIgnoreCase("M") ) {
        	   System.out.println(" Motor ---------");
			EserviceMotorDetails motData = eserMotRepo.findByRequestReferenceNoAndRiskId(d.getRequestReferenceNo(), d.getVehicleId())	;	
			MsVehicleDetails vehicleData = msVehRepo.findByVdRefno(Long.valueOf(motData.getVdRefNo()) );
			if(vehicleData!=null) {
				BigDecimal uwloading = req.stream().filter(a->a.getSectionId().equals(motData.getSectionId())).map(a -> {
			        try {
			            return a.getLoadingPercent();
			        } catch (Exception e) {
			            return BigDecimal.ZERO;  // Handle exception and default to 0
			        }
			    }).reduce(BigDecimal.ZERO, BigDecimal::add);
				log.info("UwLoading is  --->" +uwloading);
				log.info("SectionId is  --->" +motData.getSectionId());

				data.setCdRefno( motData.getCdRefno()) ;
				data.setVdRefNo(motData.getVdRefNo());
				data.setMsRefno(motData.getMsRefno());
 				data.setSectionId(motData.getSectionId());
 				data.setLocationId(motData.getLocationId().toString());
				data.setLoading(uwloading);
				vehicleData.setUwLoading(uwloading);
				msVehRepo.saveAndFlush(vehicleData);
				
			}}
           //Update Loading for Common 
           else if(ProductType.equalsIgnoreCase("H"))
           {
        	   System.out.println(" Common ---------");
        	   
        	EserviceCommonDetails comDatas = eserHumanRepo.findByRequestReferenceNoAndRiskIdAndSectionIdAndLocationId(d.getRequestReferenceNo(),d.getVehicleId(),d.getSectionId(),LocationId );
			if(comDatas!=null) {
        	MsHumanDetails humanData = msHumanRepo.findByVdRefno(Long.valueOf(comDatas.getVdRefNo()) );
        	if(humanData !=null) {
        		BigDecimal uwloading = req.stream().filter(a->a.getSectionId().equals(comDatas.getSectionId())).map(a -> {
			        try {
			            return a.getLoadingPercent();
			        } catch (Exception e) {
			            return BigDecimal.ZERO;  // Handle exception and default to 0
			        }
			    }).reduce(BigDecimal.ZERO, BigDecimal::add);
				log.info("UwLoading is  --->" +uwloading);
				log.info("SectionId is  --->" +comDatas.getSectionId());
        		data.setCdRefno( comDatas.getCdRefno()) ;
				data.setVdRefNo(comDatas.getVdRefNo());
				data.setMsRefno(comDatas.getMsRefno());
				data.setSectionId(comDatas.getSectionId());
				data.setLocationId(comDatas.getLocationId().toString());
				data.setLoading(uwloading);
				humanData.setUwLoading(uwloading);
				msHumanRepo.saveAndFlush(humanData);
				
			}
			}

           }
           //Travel
           else if(ProductType.equalsIgnoreCase("H") && "4".equalsIgnoreCase(pid) ) {
        	   System.out.println(" Travel ---------");
				EserviceTravelDetails traData = eserTraRepo.findByRequestReferenceNo(d.getRequestReferenceNo())	;
				MsHumanDetails humanData = msHumanRepo.findByVdRefno(Long.valueOf(traData.getVdRefNo()) );
				if(humanData!=null) {
					BigDecimal uwloading = req.stream().filter(a->a.getSectionId().equals(traData.getSectionId())).map(a -> {
				        try {
				            return a.getLoadingPercent();
				        } catch (Exception e) {
				            return BigDecimal.ZERO;  // Handle exception and default to 0
				        }
				    }).reduce(BigDecimal.ZERO, BigDecimal::add);
					log.info("UwLoading is  --->" +uwloading);
					log.info("SectionId is  --->" +traData.getSectionId());
					data.setCdRefno( traData.getCdRefno()) ;
					data.setVdRefNo(traData.getVdRefNo());
					data.setMsRefno(traData.getMsRefno());
					data.setSectionId(traData.getSectionId());
                    data.setLocationId(traData.getLocationId());
                	data.setLoading(uwloading);
					humanData.setUwLoading(uwloading);
					msHumanRepo.saveAndFlush(humanData);
					
				}
			}
           else {
        	   System.out.println(" Assest ---------");
        	   EserviceBuildingDetails buildings = eserBuildRepo.findByRequestReferenceNoAndRiskIdAndSectionIdAndLocationId(d.getRequestReferenceNo(),d.getVehicleId(),d.getSectionId(),LocationId )	;
				 if(buildings!=null) {
					MsAssetDetails assetData = msAssetRepo.findByVdRefno(Long.valueOf(buildings.getVdRefno()) );
					if(assetData !=null) {
						
						BigDecimal uwloading = req.stream().filter(a->a.getSectionId().equals(buildings.getSectionId())).map(a -> {
					        try {
					        	return a.getLoadingPercent() != null ? a.getLoadingPercent() : BigDecimal.ZERO;
					        } catch (Exception e) {
					        	System.err.println("Exception occurred while getting loading percent: " + e.getMessage());
					        	return BigDecimal.ZERO;  // Handle exception and default to 0
					        }
					    }).reduce(BigDecimal.ZERO, BigDecimal::add);
						log.info("UwLoading is  --->" +uwloading);
						log.info("SectionId is  --->" +buildings.getSectionId());
						
						
						data.setCdRefno( buildings.getCdRefno()) ;
						data.setVdRefNo(buildings.getVdRefno());
						data.setMsRefno(buildings.getMsRefno());
						data.setSectionId(buildings.getSectionId());
						data.setLoading(uwloading);
						data.setLocationId(buildings.getLocationId().toString());
						data.setCompanyId(buildings.getCompanyId());
				//		assetData .setUwLoading(uwloading);
						
						msAssetRepo.saveAndFlush(assetData);
					
				}  }
        	   
           }
           
           System.out.println(" The Update UwLoading details in One Time Table "+data);
		
	}catch(Exception cc)
	{
		System.out.println("Exception Occured In One Time Table ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
		cc.printStackTrace();
		log.info("Exception is --->" + cc.getMessage());
		return null;
		
	}
	return data;	
	}
	
	public CompanyProductMaster getCompanyProductMasterDropdown(String companyId, String productId) {
		CompanyProductMaster product = new CompanyProductMaster();
		try {
			Date today = new Date();
			Calendar cal = new GregorianCalendar();
			cal.setTime(today);
			cal.set(Calendar.HOUR_OF_DAY, 23);
			;
			cal.set(Calendar.MINUTE, 1);
			today = cal.getTime();
			cal.set(Calendar.HOUR_OF_DAY, 1);
			cal.set(Calendar.MINUTE, 1);
			Date todayEnd = cal.getTime();

			// Criteria
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<CompanyProductMaster> query = cb.createQuery(CompanyProductMaster.class);
			List<CompanyProductMaster> list = new ArrayList<CompanyProductMaster>();
			// Find All
			Root<CompanyProductMaster> c = query.from(CompanyProductMaster.class);
			// Select
			query.select(c);
			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(c.get("productName")));

			// Effective Date Start Max Filter
			Subquery<Date> effectiveDate = query.subquery(Date.class);
			Root<CompanyProductMaster> ocpm1 = effectiveDate.from(CompanyProductMaster.class);
			effectiveDate.select(cb.greatest(ocpm1.get("effectiveDateStart").as(Date.class)));
			Predicate a1 = cb.equal(c.get("productId"), ocpm1.get("productId"));
			Predicate a2 = cb.equal(c.get("companyId"), ocpm1.get("companyId"));
			Predicate a3 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
			effectiveDate.where(a1, a2, a3);
			// Effective Date End Max Filter
			Subquery<Date> effectiveDate2 = query.subquery(Date.class);
			Root<CompanyProductMaster> ocpm2 = effectiveDate2.from(CompanyProductMaster.class);
			effectiveDate2.select(cb.greatest(ocpm2.get("effectiveDateEnd").as(Date.class)));
			Predicate a4 = cb.equal(c.get("productId"), ocpm2.get("productId"));
			Predicate a5 = cb.equal(c.get("companyId"), ocpm2.get("companyId"));
			Predicate a6 = cb.greaterThanOrEqualTo(ocpm2.get("effectiveDateEnd"), todayEnd);
			effectiveDate2.where(a4, a5, a6);

			// Where
			Predicate n1 = cb.equal(c.get("status"), "Y");
			Predicate n2 = cb.equal(c.get("effectiveDateStart"), effectiveDate);
			Predicate n3 = cb.equal(c.get("effectiveDateEnd"), effectiveDate2);
			Predicate n4 = cb.equal(c.get("companyId"), companyId);
			Predicate n5 = cb.equal(c.get("productId"), productId);
			query.where(n1, n2, n3, n4, n5).orderBy(orderList);
			// Get Result
			TypedQuery<CompanyProductMaster> result = em.createQuery(query);
			list = result.getResultList();
			product = list.size() > 0 ? list.get(0) : null;
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --->" + e.getMessage());
			return null;
		}
		return product;
	}
	@Override
	public List<UwQuestionsDetailsRes> getUwQuestionsDetails(UwQuestionsDetailsGetReq req) {
		List<UwQuestionsDetailsRes> resList = new ArrayList<UwQuestionsDetailsRes>();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		ModelMapper modelMapper = new ModelMapper();
		
		try {
			

			List<UwQuestionsDetails> datas = new ArrayList<UwQuestionsDetails>();

			if (req.getSectionId() == null || req.getSectionId().isEmpty()) {

				datas = uwRepo.findByCompanyIdAndProductIdAndRequestReferenceNo(req.getCompanyId(),
						Integer.valueOf(req.getProductId()), req.getRequestReferenceNo());

			} else if (req.getSectionId() == "99999") {
				datas= uwRepo.findByCompanyIdAndProductIdAndRequestReferenceNoAndSectionId(req.getCompanyId(),
						Integer.valueOf(req.getProductId()), req.getRequestReferenceNo(), req.getSectionId());
			} else {
				datas= uwRepo.findByCompanyIdAndProductIdAndRequestReferenceNoAndSectionId(req.getCompanyId(),
						Integer.valueOf(req.getProductId()), req.getRequestReferenceNo(), req.getSectionId());
			}
			
			if(datas.isEmpty()) {
				log.info("No data found for the given request.");
			    return Collections.emptyList();
			}
			else {

			for (UwQuestionsDetails data : datas) {
				UwQuestionsDetailsRes res = new UwQuestionsDetailsRes();
				res = modelMapper.map(data, UwQuestionsDetailsRes.class);
				res.setValue(data.getValue() == null ? "" : data.getValue());
				resList.add(res);
			}
			}

		}
		catch(Exception e)
		{
		e.printStackTrace();
		log.info("Exception is --->" + e.getMessage());
		 return Collections.emptyList(); 
	}
	return resList;
}

	
}
