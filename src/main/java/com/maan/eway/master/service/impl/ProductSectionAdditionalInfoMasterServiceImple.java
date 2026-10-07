package com.maan.eway.master.service.impl;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.google.gson.Gson;
import com.maan.eway.bean.ProductSectionAdditionalInfoMaster;
import com.maan.eway.bean.ProductSectionMaster;
import com.maan.eway.common.res.CommonRes;
import com.maan.eway.error.Error;
import com.maan.eway.master.req.GetAllSectionAdditionalDetailsReq;
import com.maan.eway.master.req.GetOptedSectionAdditionalInfoReq;
import com.maan.eway.master.req.GetSectionAdditionalDetailsReq;
import com.maan.eway.master.req.InsertAdditionalInfoReq;
import com.maan.eway.master.req.UploadReq;
import com.maan.eway.master.res.GetOptedSectionAdditionalInfoRes;
import com.maan.eway.master.res.GetSectionAdditionalDetailsRes;
import com.maan.eway.master.service.ProductSectionAdditionalInfoMasterService;
import com.maan.eway.repository.ProductSectionAdditionalInfoMasterRepo;
import com.maan.eway.repository.ProductSectionMasterRepository;
import com.maan.eway.repository.SectionMasterRepository;
import com.maan.eway.res.SuccessRes;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.CriteriaUpdate;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;

@Service
@Transactional
public class ProductSectionAdditionalInfoMasterServiceImple implements ProductSectionAdditionalInfoMasterService{

	@PersistenceContext
	EntityManager em;
	
	DozerBeanMapper mapper = new DozerBeanMapper();
	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
	
	private org.apache.logging.log4j.Logger log = LogManager.getLogger(ProductSectionAdditionalInfoMasterServiceImple.class);
	Gson json = new Gson();
	
	@Autowired
	private ProductSectionAdditionalInfoMasterRepo repo;
	
	@Autowired
	private SectionMasterRepository smRepo;
	
	@Autowired
	private ProductSectionMasterRepository psmRepo;

	@Value("${file.directoryPath}")
	private String directoryPath;
	
	@Value("${file.compressedImg}")
	private String compressedImg;
	
	
	@Override
	public List<GetOptedSectionAdditionalInfoRes> getOptedSectionAdditionalInfo(GetOptedSectionAdditionalInfoReq req) {	 //
		List<GetOptedSectionAdditionalInfoRes> resList = new ArrayList<GetOptedSectionAdditionalInfoRes>();
		List<ProductSectionAdditionalInfoMaster> list = new ArrayList<ProductSectionAdditionalInfoMaster>();
	
		try{
			
		if(req.getOptedSectionIds().size()>0) {
			for(Integer secId : req.getOptedSectionIds()) {
			
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<ProductSectionAdditionalInfoMaster> query = cb.createQuery(ProductSectionAdditionalInfoMaster.class);

				Root<ProductSectionAdditionalInfoMaster> b = query.from(ProductSectionAdditionalInfoMaster.class);

				query.select(b);

				Subquery<Long> amendId = query.subquery(Long.class);
				Root<ProductSectionAdditionalInfoMaster> ocpm1 = amendId.from(ProductSectionAdditionalInfoMaster.class);
				amendId.select(cb.max(ocpm1.get("amendId")));
				Predicate a1 = cb.equal(ocpm1.get("productId"), b.get("productId"));
				Predicate a2 = cb.equal(ocpm1.get("companyId"), b.get("companyId"));
				Predicate a6 = cb.equal(ocpm1.get("productId"), b.get("productId"));
				Predicate a7 = cb.equal(ocpm1.get("sectionId"), b.get("sectionId"));

				amendId.where(a1,a2,a6,a7);

				Predicate n1 = cb.equal(b.get("amendId"), amendId);
				Predicate n2 = cb.equal(b.get("companyId"), req.getCompanyId());
				Predicate n3 = cb.equal(b.get("productId"), req.getProductId());
				Predicate n6 = cb.equal(b.get("sectionId"), secId);
				Predicate n7 = cb.equal(b.get("status"), "Y");
				Predicate n4 = cb.lessThanOrEqualTo(b.get("effectiveDateStart"), new Date());
				Predicate n5 = cb.greaterThanOrEqualTo(b.get("effectiveDateEnd"), new Date());
			
				query.where(n1,n2,n3,n4,n5,n6, n7);
			
				TypedQuery<ProductSectionAdditionalInfoMaster> result = em.createQuery(query);
				list = result.getResultList();		
				
				if(list.size()>0) {
					ProductSectionAdditionalInfoMaster data = list.get(0);
					GetOptedSectionAdditionalInfoRes res = new GetOptedSectionAdditionalInfoRes();
					mapper.map(data, res);
					res.setAddDetailYn(data.getAddDetailYn()==null?"":data.getAddDetailYn());
					res.setGetallUrl(data.getGetallUrl()==null?"":data.getGetallUrl());
					res.setGetUrl(data.getGetUrl()==null?"":data.getGetUrl());
					res.setJsonPath(data.getJsonPath()==null?"":data.getJsonPath());
					res.setRemarks(data.getRemarks()==null?"":data.getRemarks());
					res.setSaveUrl(data.getSaveUrl()==null?"":data.getSaveUrl());
					res.setSectionId(data.getSectionId()==null?0:data.getSectionId());
					res.setSectionName(data.getSectionName()==null?"":data.getSectionName());
					res.setStatus(data.getStatus()==null?"":data.getStatus());
					resList.add(res);
				}
				
			}
		}	
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		return resList;
	}


	@Override
	public List<Error> validateAdditionalInfo(InsertAdditionalInfoReq req) {
		List<Error> errorList = new ArrayList<Error>();

		try {
			
			if (StringUtils.isBlank(req.getCompanyId())) {
				errorList.add(new Error("02", "CompanyId", "Please Enter CompanyId"));
			}
			if (StringUtils.isBlank(req.getProductId())) {
				errorList.add(new Error("02", "ProductId", "Please Enter ProductId"));
			}
			if (req.getSectionId()==null) {
				errorList.add(new Error("02", "SectionId", "Please Enter SectionId"));
			}
			if (StringUtils.isBlank(req.getCreatedBy())) {
				errorList.add(new Error("09", "CreatedBy", "Please Enter CreatedBy"));
			}else if (req.getCreatedBy().length() > 100){
				errorList.add(new Error("09","CreatedBy", "Please Enter CreatedBy within 100 Characters")); 
			}	
			
			if( StringUtils.isBlank(req.getAddDetailYn())) {
				errorList.add(new Error("05", "Additional Detail", "Please Select Additional Detail Yes/No"));
				
			} else if (req.getAddDetailYn().equalsIgnoreCase("Y") ) {
			
				// Date Validation 
				Calendar cal = new GregorianCalendar();
				Date today = new Date();
				cal.setTime(today);cal.add(Calendar.DAY_OF_MONTH, -1);
				today = cal.getTime();
				if (req.getEffectiveDateStart() == null || StringUtils.isBlank(req.getEffectiveDateStart().toString())) {
					errorList.add(new Error("05", "EffectiveDateStart", "Please Enter Effective Date Start"));
	
				} else if (req.getEffectiveDateStart().before(today)) {
					errorList.add(new Error("05", "EffectiveDateStart", "Please Enter Effective Date Start as Future Date"));
				}
				
				
				
				//Status Validation
				if (StringUtils.isBlank(req.getStatus())) {
					errorList.add(new Error("05", "Status", "Please Select Status"));
				} else if (req.getStatus().length() > 1) {
					errorList.add(new Error("05", "Status", "Please Select Valid Status - One Character Only Allwed"));
				}else if(!("Y".equalsIgnoreCase(req.getStatus())||"N".equalsIgnoreCase(req.getStatus())||"R".equalsIgnoreCase(req.getStatus())|| "P".equalsIgnoreCase(req.getStatus()))) {
					errorList.add(new Error("05", "Status", "Please Select Valid Status - Active or Deactive or Pending or Referral "));
				}
	
				if (StringUtils.isNotBlank(req.getRemarks()) && req.getRemarks().length() > 100) {
					errorList.add(new Error("03", "Remark", "Please Enter Remark within 100 Characters"));
				}
				
				
				
	//			if(StringUtils.isBlank(req.getJsonPath())) {
	//				errorList.add(new Error("09", "Json File Path", "Please Enter File Path"));
	//			}
				if(StringUtils.isBlank(req.getSaveUrl())) {
					errorList.add(new Error("09", "Save URL", "Please Enter Save URL"));
				}
				if(StringUtils.isBlank(req.getGetallUrl())) {
					errorList.add(new Error("09", "Get All URL", "Please Enter Get All URL"));
				}
				if(StringUtils.isBlank(req.getGetUrl())) {
					errorList.add(new Error("09", "Get URL", "Please Enter Get URL"));
				}
			}
			
		} catch (Exception e) {
			log.error(e);
			e.printStackTrace();
		}
		return errorList;
	
	}


	@Override
	public SuccessRes insertAdditionalInfo(InsertAdditionalInfoReq req) {
		SuccessRes res = new SuccessRes();
		ProductSectionAdditionalInfoMaster saveData = new ProductSectionAdditionalInfoMaster();
		List<ProductSectionAdditionalInfoMaster> list  = new ArrayList<ProductSectionAdditionalInfoMaster>();
		
		try {
			Integer amendId = 0;
			Date StartDate = req.getEffectiveDateStart();
			String end = "31/12/2050";
			Date endDate = sdf.parse(end);
			long MILLS_IN_A_DAY = 1000*60*60*24;
			Date oldEndDate = new Date(req.getEffectiveDateStart().getTime()- MILLS_IN_A_DAY);
			Date entryDate = null;
			String createdBy ="";
			
			List<ProductSectionAdditionalInfoMaster> old = repo.findByProductIdAndSectionIdAndCompanyIdOrderByAmendIdDesc(Integer.valueOf(req.getProductId()),Integer.valueOf(req.getSectionId()),req.getCompanyId());
					
			
			if(old.size()<=0 ) {   //Insert
				
				entryDate = new Date();
				createdBy = req.getCreatedBy();
				res.setResponse("Saved Successfully");
				
			}
			else {  //update
				
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<ProductSectionAdditionalInfoMaster> query = cb.createQuery(ProductSectionAdditionalInfoMaster.class);
			
				Root<ProductSectionAdditionalInfoMaster> b = query.from(ProductSectionAdditionalInfoMaster.class);
				
				query.select(b);

				List<Order> orderList = new ArrayList<Order>();
			    orderList.add(cb.desc(b.get("amendId")));
				
				Predicate n2 = cb.equal(b.get("companyId"),req.getCompanyId());
				Predicate n3 = cb.equal(b.get("productId"),req.getProductId());
				Predicate n4 = cb.equal(b.get("sectionId"),req.getSectionId());
				
				query.where(n2,n3,n4).orderBy(orderList) ;
				
			
				TypedQuery<ProductSectionAdditionalInfoMaster> result = em.createQuery(query);

				list = result.getResultList();
				
				
				if(list.size()>0) {
					
					Date beforeOneDay = new Date(new Date().getTime() - MILLS_IN_A_DAY);
				
					if ( list.get(0).getEffectiveDateStart().before(beforeOneDay)  ) { 	// if old start is past,
					
						amendId = list.get(0).getAmendId() + 1 ;
						entryDate = new Date() ;
						createdBy = req.getCreatedBy();
					
							CriteriaBuilder cb2 = em.getCriteriaBuilder();
						
							CriteriaUpdate<ProductSectionAdditionalInfoMaster> update = cb2.createCriteriaUpdate(ProductSectionAdditionalInfoMaster.class);
						
							Root<ProductSectionAdditionalInfoMaster> m = update.from(ProductSectionAdditionalInfoMaster.class);
						
							update.set("updatedBy", req.getCreatedBy());
							update.set("updatedDate", entryDate);
							update.set("effectiveDateEnd", oldEndDate);
							
							List<Predicate> predics = new ArrayList<Predicate>();
							predics.add(cb2.equal(m.get("companyId"), req.getCompanyId()));
							predics.add(cb2.equal(m.get("amendId"), list.get(0).getAmendId() ));
							predics.add(cb2.equal(m.get("productId"),req.getProductId()));
							predics.add(cb2.equal(m.get("sectionId"),req.getSectionId()));
							
							update.where(predics.toArray(new Predicate[0]) );
					
							em.createQuery(update).executeUpdate();
						
					} else { //future or today

						amendId = list.get(0).getAmendId();
						entryDate = list.get(0).getEntryDate();
						createdBy = list.get(0).getCreatedBy();
					
						if(list.size()>1) {
							ProductSectionAdditionalInfoMaster lastRecord = list.get(1);	
							lastRecord.setEffectiveDateEnd(oldEndDate);
							repo.saveAndFlush(lastRecord);
						}
				    }
				}		
				res.setResponse("Updated Successfully");
			}
			
			mapper.map(req, saveData);
			if(old.size()>0) {
				saveData.setJsonPath(old.get(0).getJsonPath()==null?"":old.get(0).getJsonPath());
				saveData.setFileName(old.get(0).getFileName()==null?"":old.get(0).getFileName());				
			}
			
			List<ProductSectionMaster> sm = psmRepo.findByProductIdAndSectionIdAndCompanyIdOrderByAmendIdDesc(Integer.valueOf(req.getProductId()), 
					Integer.valueOf(req.getSectionId()), req.getCompanyId());
			if(sm.size()>0)
				saveData.setSectionName(sm.get(0).getSectionName());
			
			saveData.setEffectiveDateStart(StartDate);
			saveData.setEffectiveDateEnd(endDate);
			saveData.setCreatedBy(createdBy);
			saveData.setEntryDate(entryDate);
			saveData.setUpdatedBy(req.getCreatedBy());
			saveData.setUpdatedDate(new Date());
			saveData.setAmendId(amendId);
			
			repo.saveAndFlush(saveData);
			res.setSuccessId(req.getSectionId().toString());
			log.info("Saved Details is --> " + json.toJson(saveData));	
		
		}	catch(Exception e) {
			e.printStackTrace();
			log.info("Exception is --> " + e.getMessage());
			return null;
		}
		return res;
	}


	@Override
	public GetSectionAdditionalDetailsRes getSectionAdditionalDetails(GetSectionAdditionalDetailsReq req) {
		GetSectionAdditionalDetailsRes res = new GetSectionAdditionalDetailsRes();
		try {

			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<ProductSectionAdditionalInfoMaster> query = cb.createQuery(ProductSectionAdditionalInfoMaster.class);

			Root<ProductSectionAdditionalInfoMaster> b = query.from(ProductSectionAdditionalInfoMaster.class);

			query.select(b);

			Subquery<Long> amendId = query.subquery(Long.class);
			Root<ProductSectionAdditionalInfoMaster> ocpm1 = amendId.from(ProductSectionAdditionalInfoMaster.class);
			amendId.select(cb.max(ocpm1.get("amendId")));
			Predicate a1 = cb.equal(ocpm1.get("productId"), b.get("productId"));
			Predicate a2 = cb.equal(ocpm1.get("companyId"), b.get("companyId"));
			Predicate a6 = cb.equal(ocpm1.get("productId"), b.get("productId"));
			Predicate a7 = cb.equal(ocpm1.get("sectionId"), b.get("sectionId"));

			amendId.where(a1,a2,a6,a7);

			Predicate n1 = cb.equal(b.get("amendId"), amendId);
			Predicate n2 = cb.equal(b.get("companyId"), req.getCompanyId());
			Predicate n3 = cb.equal(b.get("productId"), req.getProductId());
			Predicate n6 = cb.equal(b.get("sectionId"), req.getSectionId());
			Predicate n7 = cb.equal(b.get("status"), "Y");
			Predicate n4 = cb.lessThanOrEqualTo(b.get("effectiveDateStart"), new Date());
			Predicate n5 = cb.greaterThanOrEqualTo(b.get("effectiveDateEnd"), new Date());
		
			query.where(n1,n2,n3,n4,n5,n6, n7);
		
			TypedQuery<ProductSectionAdditionalInfoMaster> result = em.createQuery(query);
			List<ProductSectionAdditionalInfoMaster> list = result.getResultList();		
			
			if(list.size()>0) {
				ProductSectionAdditionalInfoMaster data = list.get(0);
				mapper.map(data, res);
				res.setAddDetailYn(data.getAddDetailYn()==null?"":data.getAddDetailYn());
				res.setGetallUrl(data.getGetallUrl()==null?"":data.getGetallUrl());
				res.setGetUrl(data.getGetUrl()==null?"":data.getGetUrl());
				res.setJsonPath(data.getJsonPath()==null?"":data.getJsonPath());
				res.setRemarks(data.getRemarks()==null?"":data.getRemarks());
				res.setSaveUrl(data.getSaveUrl()==null?"":data.getSaveUrl());
				res.setSectionId(data.getSectionId()==null?0:data.getSectionId());
				res.setSectionName(data.getSectionName()==null?"":data.getSectionName());
				res.setStatus(data.getStatus()==null?"":data.getStatus());
				res.setFileName(data.getFileName()==null?"":data.getFileName());
			}
			
			}catch(Exception e) {
			e.printStackTrace();
			log.info("Exception is --> " + e.getMessage());
			return null;
		}
		return res;
	}


	@Override
	public List<Error> docvalidation( MultipartFile file, UploadReq req) {
		 List<Error>  errorList = new ArrayList<Error>();
		try {
			
			long fileSizeInBytes = file.getSize();
			double size_kb = fileSizeInBytes / 1024;
			double size_mb = size_kb / 1024;
	
			if (size_mb > 25) {
				errorList.add(new Error("01", "FileSize", "File Size Must Not Greater Than 25Mb Current file value is" + size_mb
						+ "MB"));
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		return errorList;
	}


	@Override
	public CommonRes fileupload( MultipartFile file, UploadReq req) {			
		CommonRes res = new CommonRes();
		try {
			
			// Copy File 
			Random random = new Random();
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			
			String newfilename  = "";
			String newfilename1 = "";
			//OrginalFile
			Path destination = Paths.get(directoryPath) ;
			newfilename= random.nextInt(100) + timestamp.toString().replace(":", "T").replace(" ", "S").replace("-", "H").replace(".", "D") +"."+FilenameUtils.getExtension(file.getOriginalFilename());
			Files.copy(file.getInputStream(),destination.resolve(newfilename));
			
			Timestamp timestamp1 = new Timestamp(System.currentTimeMillis());
			//BackupFile
			Path destination1 = Paths.get(compressedImg) ; 
			newfilename1= random.nextInt(100) + timestamp1.toString().replace(":", "T").replace(" ", "S").replace("-", "H").replace(".", "D") +"."+FilenameUtils.getExtension(file.getOriginalFilename());
			Files.copy(file.getInputStream(),destination1.resolve(newfilename1));
		
			String path = (directoryPath+newfilename);
			//String path = (directoryPath+req.getFileName());
			
			// path save in table
			List<ProductSectionAdditionalInfoMaster> list = repo.findByProductIdAndSectionIdAndCompanyIdOrderByAmendIdDesc(Integer.valueOf(req.getProductId()), Integer.valueOf(req.getSectionId()), req.getCompanyId());
			if(list.size()>0) {
				ProductSectionAdditionalInfoMaster pathsave = list.get(0) ;
				pathsave.setJsonPath(path);
				pathsave.setFileName(req.getFileName());
				repo.saveAndFlush(pathsave);
			}
			
			
			res.setCommonResponse(path);
			res.setIsError(false);						
		} catch (Exception e) {
			e.printStackTrace();
			res.setCommonResponse(null);
			List<Error> error = new ArrayList<Error>();
			error.add(new Error( "01" , "Upload Error" ,e.getMessage()));
			res.setErrorMessage(error);
			res.setIsError(true);
		}
		return res;
	}


	@Override
	public List<GetSectionAdditionalDetailsRes> getAllSectionAdditionalDetails(GetAllSectionAdditionalDetailsReq req) {
		List<GetSectionAdditionalDetailsRes> resList = new ArrayList<GetSectionAdditionalDetailsRes>();
		try {
			Date today  = new Date();
			Calendar cal = new GregorianCalendar(); 
			cal.setTime(today);cal.set(Calendar.HOUR_OF_DAY, 23);cal.set(Calendar.MINUTE, 1);
			today   = cal.getTime();
			cal.set(Calendar.HOUR_OF_DAY, 1);cal.set(Calendar.MINUTE, 1);
			Date todayEnd = cal.getTime();
			
			// Criteria
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<ProductSectionMaster> query = cb.createQuery(ProductSectionMaster.class);
			List<ProductSectionMaster> list = new ArrayList<ProductSectionMaster>();
			
			// Find All
			Root<ProductSectionMaster>    c = query.from(ProductSectionMaster.class);		
			
			// Select
			query.select(c );
			
		
			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.desc(c.get("amendId")));
			
			// Effective Date Max Filter
			Subquery<Date> effectiveDate = query.subquery(Date.class);
			Root<ProductSectionMaster> ocpm1 = effectiveDate.from(ProductSectionMaster.class);
			effectiveDate.select(cb.greatest(ocpm1.get("effectiveDateStart").as(Date.class)));
			Predicate a1 = cb.equal(c.get("sectionId"),ocpm1.get("sectionId") );
			Predicate a2 = cb.equal(c.get("companyId"), ocpm1.get("companyId") );
			Predicate a3 = cb.equal(c.get("productId"), ocpm1.get("productId") );
			Predicate a4 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
			effectiveDate.where(a1,a2,a3,a4);
			
			// Effective Date End
			Subquery<Date> effectiveDate2 = query.subquery(Date.class);
			Root<ProductSectionMaster> ocpm2 = effectiveDate2.from(ProductSectionMaster.class);
			effectiveDate2.select(cb.greatest(ocpm2.get("effectiveDateEnd").as(Date.class)));
			jakarta.persistence.criteria.Predicate a5 = cb.equal(c.get("sectionId"), ocpm2.get("sectionId"));
			Predicate a7 = cb.equal(c.get("companyId"), ocpm2.get("companyId") );
			Predicate a8 = cb.equal(c.get("productId"), ocpm2.get("productId") );
			
			jakarta.persistence.criteria.Predicate a6 = cb.greaterThanOrEqualTo(ocpm2.get("effectiveDateEnd"), todayEnd);
			effectiveDate2.where(a5, a6,a7,a8);

		    // Where	
			jakarta.persistence.criteria.Predicate n1 = cb.equal(c.get("status"), "Y");
			jakarta.persistence.criteria.Predicate n2 = cb.equal(c.get("effectiveDateStart"), effectiveDate);
			jakarta.persistence.criteria.Predicate n3 = cb.equal(c.get("effectiveDateEnd"), effectiveDate2);
			jakarta.persistence.criteria.Predicate n4 = cb.equal(c.get("companyId"), req.getCompanyId());
			jakarta.persistence.criteria.Predicate n5 = cb.equal(c.get("productId"), req.getProductId());
			Predicate n6 = cb.equal(c.get("status"),"R");
			Predicate n7 = cb.or(n1,n6);
			query.where(n7,n2,n3,n4,n5).orderBy(orderList);
			
			// Get Result
			TypedQuery<ProductSectionMaster> result = em.createQuery(query);			
			list =  result.getResultList();  
			
			List<ProductSectionAdditionalInfoMaster> all = repo.findByProductIdAndCompanyId(Integer.valueOf(req.getProductId()),  req.getCompanyId());
			for(ProductSectionMaster data1 : list ) {
				GetSectionAdditionalDetailsRes res = new GetSectionAdditionalDetailsRes();
				
				List<ProductSectionAdditionalInfoMaster> sec = all.stream().filter(o -> o.getSectionId().equals(data1.getSectionId())).collect(Collectors.toList());
				ProductSectionAdditionalInfoMaster data = sec.stream().max(Comparator.comparingInt(ProductSectionAdditionalInfoMaster :: getAmendId)).orElse(null);
				if(data!=null) {
					
					mapper.map(data, res);
					res.setAddDetailYn(data.getAddDetailYn()==null?"":data.getAddDetailYn());
					res.setGetallUrl(data.getGetallUrl()==null?"":data.getGetallUrl());
					res.setGetUrl(data.getGetUrl()==null?"":data.getGetUrl());
					res.setJsonPath(data.getJsonPath()==null?"":data.getJsonPath());
					res.setRemarks(data.getRemarks()==null?"":data.getRemarks());
					res.setSaveUrl(data.getSaveUrl()==null?"":data.getSaveUrl());
					res.setSectionId(data.getSectionId()==null?0:data.getSectionId());
					res.setSectionName(data.getSectionName()==null?"":data.getSectionName());
					res.setStatus(data.getStatus()==null?"":data.getStatus());
					res.setFileName(data.getFileName()==null?"":data.getFileName());
					resList.add(res);
				} else {
					res.setSectionId(data1.getSectionId()==null?0:data1.getSectionId());
					res.setSectionName(data1.getSectionName()==null?"":data1.getSectionName());
					res.setAddDetailYn("N");
					resList.add(res);
				}
				
			}	
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return resList;
	
	}
	

}
