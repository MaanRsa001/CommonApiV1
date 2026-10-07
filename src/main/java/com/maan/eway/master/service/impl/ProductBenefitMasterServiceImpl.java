package com.maan.eway.master.service.impl;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.maan.eway.bean.CompanyProductMaster;
import com.maan.eway.bean.CoverMaster;
import com.maan.eway.bean.InsuranceCompanyMaster;
import com.maan.eway.bean.ListItemValue;
import com.maan.eway.bean.ProductBenefitMaster;
import com.maan.eway.bean.ProductSectionMaster;
import com.maan.eway.common.req.ProductBenefitDropDownReq;
import com.maan.eway.common.service.impl.GenerateSeqNoServiceImpl;
import com.maan.eway.master.req.ProductBenefitChangeStatusReq;
import com.maan.eway.master.req.ProductBenefitGetAllReq;
import com.maan.eway.master.req.ProductBenefitGetReq;
import com.maan.eway.master.req.ProductBenefitSaveReq;
import com.maan.eway.master.res.ProductBenefitGetRes;
import com.maan.eway.master.service.ProductBenefitMasterService;
import com.maan.eway.repository.ListItemValueRepository;
import com.maan.eway.repository.ProductBenefitMasterRepository;
import com.maan.eway.res.ProductBenefitDropDownRes;
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
import net.coobird.thumbnailator.Thumbnails;
@Service
public class ProductBenefitMasterServiceImpl implements ProductBenefitMasterService {

	@PersistenceContext
	private EntityManager em;
	
	@Autowired
	private ProductBenefitMasterRepository repo;

	@Autowired
	private ListItemValueRepository listrepo;
	
	@Autowired
	private GenerateSeqNoServiceImpl seqService;

	Gson json = new Gson();
	
	@Value("${file.directoryPath}")
	private String directoryPath;
	
	@Value("${file.compressedImg}")
	private String compressedImg;

	
	private Logger log = LogManager.getLogger(ProductBenefitMasterServiceImpl.class);


	@Override
	public List<String> validateProductBenefit(ProductBenefitSaveReq req) {
		List<String> errorList = new ArrayList<String>();

		try {
		
			if (StringUtils.isBlank(req.getBenefitDescription())) {
			//	errorList.add(new Error("02", "Benefit Description", "Please enter Benefit Description"));
				errorList.add("1588");
			}else if (req.getBenefitDescription().length() > 1000){
				//errorList.add(new Error("02","Benefit Description", "Please Enter Benefit Description 1000 Characters")); 
				errorList.add("1589");
			}else if (StringUtils.isBlank(req.getBenefitId()) &&  StringUtils.isNotBlank(req.getCompanyId()) && StringUtils.isNotBlank(req.getSectionId())&& StringUtils.isNotBlank(req.getProductId()) &&  StringUtils.isNotBlank(req.getTypeId()) ) {
				List<ProductBenefitMaster> BenefitList = getBenefitDescriptionExistDetails(req.getBenefitDescription() , req.getCompanyId() , req.getProductId(),req.getSectionId() , req.getTypeId() );
				if (BenefitList.size()>0 ) {
			//		errorList.add(new Error("01", "Benefit Description", "This Benefit Description Already Exist "));
					errorList.add("1590");
				}
			}
			else if (StringUtils.isNotBlank(req.getBenefitId()) &&  StringUtils.isNotBlank(req.getCompanyId()) && StringUtils.isNotBlank(req.getProductId())&& StringUtils.isNotBlank(req.getSectionId()) &&  StringUtils.isNotBlank(req.getTypeId()) ) {
				List<ProductBenefitMaster> BenefitList = getBenefitDescriptionExistDetails(req.getBenefitDescription() , req.getCompanyId() , req.getProductId(), req.getSectionId(), req.getTypeId());
				
				if (BenefitList.size()>0 &&  (! req.getBenefitId().equalsIgnoreCase(BenefitList.get(0).getBenefitId().toString())) ) {
				//	errorList.add(new Error("01", "ExclusionDescription", "This ExclusionDescription Already Exist "));
					errorList.add("1591");
				}
				
			}
			
			
			if (StringUtils.isBlank(req.getCompanyId())) {
		//		errorList.add(new Error("02", "CompanyId", "Please Enter CompanyId"));
				errorList.add("1255");
			}
			
			
			if (StringUtils.isBlank(req.getRemarks())) {
			//	errorList.add(new Error("04", "Remarks", "Please Select Remarks "));
				errorList.add("1259");
			}else if (req.getRemarks().length() > 100){
			//	errorList.add(new Error("04","Remarks", "Please Enter Remarks within 100 Characters")); 
				errorList.add("1260");
			}
			
			// Date Validation 
			Calendar cal = new GregorianCalendar();
			Date today = new Date();
			cal.setTime(today);cal.add(Calendar.DAY_OF_MONTH, -1);;
			today = cal.getTime();
			if (req.getEffectiveDateStart() == null || StringUtils.isBlank(req.getEffectiveDateStart().toString())) {
			//	errorList.add(new Error("05", "EffectiveDateStart", "Please Enter Effective Date Start"));
				errorList.add("1261");

			} else if (req.getEffectiveDateStart().before(today)) {
			//	errorList.add(new Error("05", "EffectiveDateStart", "Please Enter Effective Date Start as Future Date"));
				errorList.add("1262");
				
			}
			//Status Validation
			if (StringUtils.isBlank(req.getStatus())) {
			//	errorList.add(new Error("05", "Status", "Please Select Status  "));
				errorList.add("1263");
			} else if (req.getStatus().length() > 1) {
			//	errorList.add(new Error("05", "Status", "Please Select Valid Status - One Character Only Allwed"));
				errorList.add("1264");
			}else if(!("Y".equalsIgnoreCase(req.getStatus())||"N".equalsIgnoreCase(req.getStatus())||"R".equalsIgnoreCase(req.getStatus())|| "P".equalsIgnoreCase(req.getStatus()))) {
			//	errorList.add(new Error("05", "Status", "Please Select Valid Status - Active or Deactive or Pending or Referral "));
				errorList.add("1265");
			}
			if (StringUtils.isBlank(req.getCoreAppCode())) {
			//	errorList.add(new Error("07", "CoreAppCode", "Please Select CoreAppCode"));
				errorList.add("1270");
			}else if (req.getCoreAppCode().length() > 20){
			//	errorList.add(new Error("07","CoreAppCode", "Please Enter CoreAppCode within 20 Characters")); 
				errorList.add("1271");
			}
			if (StringUtils.isBlank(req.getRegulatoryCode())) {
			//	errorList.add(new Error("08", "RegulatoryCode", "Please Select RegulatoryCode"));
				errorList.add("1268");
			}else if (req.getRegulatoryCode().length() > 20){
			//	errorList.add(new Error("08","RegulatoryCode", "Please Enter RegulatoryCode within 20 Characters")); 
				errorList.add("1269");
			}
			if (StringUtils.isBlank(req.getCreatedBy())) {
			//	errorList.add(new Error("09", "CreatedBy", "Please Select CreatedBy"));
				errorList.add("1270");
			}else if (req.getCreatedBy().length() > 100){
			//	errorList.add(new Error("09","CreatedBy", "Please Enter CreatedBy within 100 Characters")); 
				errorList.add("1271");
			}	
			
			if (StringUtils.isBlank(req.getProductId())) {
			//	errorList.add(new Error("10", "ProductId", "Please Select ProductId"));
				errorList.add("1313");
			}
			if (StringUtils.isBlank(req.getSectionId())) {
			//	errorList.add(new Error("11", "SectionId", "Please Select SectionId"));
				errorList.add("1302");
			}
			
			if (StringUtils.isBlank(req.getTypeId())) {
			//	errorList.add(new Error("12", "TypeId", "Please Select Benefit TypeId"));
				errorList.add("1314");
			}
			
//			if (StringUtils.isBlank(req.getDisplayOrder())) {
//				errorList.add(new Error("12", "DisplayOrder", "Please Enter Display Order"));
//			} else if (! req.getDisplayOrder().matches("[0-9]") ) {
//				errorList.add(new Error("12", "DisplayOrder", "Please Enter Display Order"));
//			}
		} catch (Exception e) {
		//	log.error(e);
			e.printStackTrace();
		}
		return errorList;
	}
	
	public List<ProductBenefitMaster> getBenefitDescriptionExistDetails(String description , String InsuranceId , String productId, String sectionId , String typeId) {
		List<ProductBenefitMaster> list = new ArrayList<ProductBenefitMaster>();
		try {
			// Find Latest Record
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<ProductBenefitMaster> query = cb.createQuery(ProductBenefitMaster.class);

			// Find All
			Root<ProductBenefitMaster> b = query.from(ProductBenefitMaster.class);

			// Select
			query.select(b);

			// Effective Date Max Filter
			Subquery<Long> amendId = query.subquery(Long.class);
			Root<ProductBenefitMaster> ocpm1 = amendId.from(ProductBenefitMaster.class);
			amendId.select(cb.max(ocpm1.get("amendId")));
			Predicate a1 = cb.equal(ocpm1.get("benefitId"), b.get("benefitId"));
			Predicate a2 = cb.equal(ocpm1.get("companyId"), b.get("companyId"));
			Predicate a3 = cb.equal(ocpm1.get("productId"), b.get("productId"));
			Predicate a4 = cb.equal(ocpm1.get("sectionId"), b.get("sectionId"));
			Predicate a5 = cb.equal(ocpm1.get("typeId"), b.get("typeId"));
			amendId.where(a1,a2,a3,a4,a5);

			Predicate n1 = cb.equal(b.get("amendId"), amendId);
			Predicate n2 = cb.equal(cb.lower( b.get("benefitDescription")), description.trim().toLowerCase());
			Predicate n3 = cb.equal(b.get("companyId"),InsuranceId);
			Predicate n4 = cb.equal(b.get("productId"),productId);
			Predicate n5 = cb.equal(b.get("sectionId"),sectionId);
			Predicate n6 = cb.equal(b.get("typeId"),typeId);
		
			query.where(n1,n2,n3,n4,n5,n6);
			
			// Get Result
			TypedQuery<ProductBenefitMaster> result = em.createQuery(query);
			list = result.getResultList();		
		
		} catch (Exception e) {
			e.printStackTrace();
			log.info(e.getMessage());

		}
		return list;
	}
		
	@Override
	//public SuccessRes saveProductBenefit(ProductBenefitSaveReq req , Object file) {
	public SuccessRes saveProductBenefit(ProductBenefitSaveReq req ) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		SuccessRes res = new SuccessRes();
		ProductBenefitMaster saveData = new ProductBenefitMaster();
		List<ProductBenefitMaster> list  = new ArrayList<ProductBenefitMaster>();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		try {
			Integer amendId = 0;
			Date StartDate = req.getEffectiveDateStart();
			String end = "31/12/2050";
			Date endDate = sdf.parse(end);
			long MILLS_IN_A_DAY = 1000*60*60*24;
			Date oldEndDate = new Date(req.getEffectiveDateStart().getTime()- MILLS_IN_A_DAY);
			Date entryDate = null;
			String createdBy ="";
			Integer benefitId = 0;
			String iconPath = "";
			String filePath = "";
			
			List<CoverMaster> coverList=getByCoverId(req);
			List<ListItemValue> calcTypes = getListItem("99999" , req.getBranchCode() , "CALCULATION_TYPE");
			String productName =  getCompanyProductMasterDropdown(req.getCompanyId() , req.getProductId()); 
			String sectionName =  req.getSectionId().equalsIgnoreCase("99999") ? "All" : getProductSectionDropdown(req.getCompanyId() , req.getProductId(), req.getSectionId()); 
			String companyName =  getInscompanyMasterDropdown(req.getCompanyId()) ; 
			String typeDesc    =  getTypeDesc( req.getCompanyId() , "99999", "POLICY_BENEFITS_TYPES",req.getTypeId());
			
			
			if(StringUtils.isBlank(req.getBenefitId())) {
				String seq  = seqService.generateBenefitId() ;
				benefitId =  Integer.valueOf(seq)  ;
				entryDate = new Date();
				createdBy = req.getCreatedBy();
				res.setResponse("Saved Successfully");
				res.setSuccessId(benefitId.toString());
			}
			else {
				benefitId = Integer.valueOf(req.getBenefitId());
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<ProductBenefitMaster> query = cb.createQuery(ProductBenefitMaster.class);
				//Findall
				Root<ProductBenefitMaster> b = query.from(ProductBenefitMaster.class);
				//select
				query.select(b);
				//Orderby
				List<Order> orderList = new ArrayList<Order>();
				orderList.add(cb.desc(b.get("amendId")));
				//Where
				Predicate n1 = cb.equal(b.get("benefitId"),req.getBenefitId());
				Predicate n2 = cb.equal(b.get("companyId"),req.getCompanyId());
				Predicate n3 = cb.equal(b.get("typeId"),req.getTypeId());
				Predicate n4 = cb.equal(b.get("productId"),req.getProductId());
				Predicate n5 = cb.equal(b.get("sectionId"),req.getSectionId());
				
				query.where(n1,n2,n3,n4,n5).orderBy(orderList);
				
				// Get Result
				TypedQuery<ProductBenefitMaster> result = em.createQuery(query);
				int limit=0, offset=2;
				result.setFirstResult(limit * offset);
				result.setMaxResults(offset);
				list = result.getResultList();
				if(list.size()>0) {
					Date beforeOneDay = new Date(new Date().getTime()- MILLS_IN_A_DAY);
					if(list.get(0).getEffectiveDateStart().before(beforeOneDay)) {
						amendId = list.get(0).getAmendId()+1;
						entryDate = new Date();
						createdBy = req.getCreatedBy();
						iconPath = list.get(0).getIconPath();
						filePath = list.get(0).getOriginalImagePath();
						ProductBenefitMaster lastRecord = list.get(0);
						lastRecord.setEffectiveDateEnd(oldEndDate);
						repo.saveAndFlush(lastRecord);
					}
					else {
						amendId = list.get(0).getAmendId();
						entryDate = list.get(0).getEntryDate();
						createdBy = list.get(0).getCreatedBy();
						iconPath = list.get(0).getIconPath();
						filePath = list.get(0).getOriginalImagePath();
						saveData = list.get(0);
						if(list.size()>1) {
							ProductBenefitMaster lastRecord = list.get(1);	
							lastRecord.setEffectiveDateEnd(oldEndDate);
							repo.saveAndFlush(lastRecord);
						}
					}
				}
				res.setResponse("Updated Successfully");
				res.setSuccessId(benefitId.toString());
			}
			dozerMapper.map(req, saveData);
			saveData.setBranchCode(req.getBranchCode());
			saveData.setBenefitId(benefitId);
			saveData.setEffectiveDateStart(StartDate);
			saveData.setEffectiveDateEnd(endDate);
			saveData.setCreatedBy(createdBy);
			saveData.setEntryDate(entryDate);
			saveData.setUpdatedBy(req.getCreatedBy());
			saveData.setUpdatedDate(new Date());
			saveData.setAmendId(amendId);
			saveData.setTypeId(Integer.valueOf(req.getTypeId()));
			saveData.setCompanyName(companyName);
			saveData.setProductDesc(productName);
			saveData.setSectionDesc(sectionName);
			if(StringUtils.isNotBlank(req.getCalcType())){
				String data = calcTypes.stream().filter(a->a.getItemCode().equalsIgnoreCase(req.getCalcType())).
						map(ListItemValue::getItemValue).findFirst().orElse(" ");
				saveData.setCalcTypeDesc(data);
			}
			saveData.setCoverName(coverList.get(0).getCoverName());
			saveData.setSubCoverId(coverList.get(0).getSubCoverId());
			saveData.setSubCoverName(coverList.get(0).getSubCoverName());
			//saveData.setCoverName(coverList.stream().filter( o -> o.getCoverId().equals(req.getCoverId()) ).collect(Collectors.toList()).get(0).getCoverName());
			saveData.setIconPath(iconPath);
			saveData.setOriginalImagePath(filePath);
			saveData.setTypeDesc(typeDesc);
			
		/*	if(file != null  ) {
				MultipartFile imageFile = (MultipartFile) file ; 
				if(StringUtils.isNotBlank(imageFile.getOriginalFilename()) ) {
					// File Upload 
					Random random = new Random();
				//	Timestamp timestamp = new Timestamp(System.currentTimeMillis());
					
					String newfilename  = "";
					String newfilename1 = "";
					//OrginalFile
					Path destination = Paths.get(directoryPath) ;
					newfilename= imageFile.getOriginalFilename() + "-" + random.nextInt(100) ;
					Files.copy(imageFile.getInputStream(),destination.resolve(newfilename));
					
				//	Timestamp timestamp1 = new Timestamp(System.currentTimeMillis());
					//BackupFile
					Path destination1 = Paths.get(compressedImg) ; 
					newfilename1= imageFile.getOriginalFilename() + "-" + random.nextInt(100) ;
					Files.copy(imageFile.getInputStream(),destination1.resolve(newfilename1));
				
					saveData.setIconPath(compressedImg+newfilename1);
					saveData.setOriginalImagePath(directoryPath+newfilename);
				}
				
				
			}*/
			
			repo.saveAndFlush(saveData);	
			log.info("Saved Details is --> " + json.toJson(saveData));	
			}
		catch(Exception e) {
			e.printStackTrace();
			log.info("Exception is --> " + e.getMessage());
			return null;
		}
		return res;
		}
	
	
	private String generateFileName() {
		SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyyhmmssSSSSSSa");
		Calendar cal = Calendar.getInstance();
		String date = sdf.format(cal.getTime());	
		return date;
	}
	
	public void CompressImage(File uploadFile, String documentPath) {

		try {
			String extension = FilenameUtils.getExtension(documentPath);
			File jpgoutput = new File("thumbnail." + extension);
			BufferedImage originalImage = ImageIO.read(uploadFile);
			Thumbnails.of(originalImage).size(750, 750).outputFormat(extension).toFile(jpgoutput);
			FileUtils.copyFile(jpgoutput, new File(documentPath));
			if(jpgoutput.exists()) {
				System.out.println("Thumbnail File Deleted after conversion");
				FileUtils.deleteQuietly(jpgoutput);
			}
		} catch (IOException e) {
			e.printStackTrace();
			try {
				FileUtils.copyFile(uploadFile, new File(documentPath));
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}

	}
	
	public List<CoverMaster> getByCoverId(ProductBenefitSaveReq req) {
		DozerBeanMapper mapper = new DozerBeanMapper();
		String pattern = "#####0.00";
		DecimalFormat df = new DecimalFormat(pattern);
		List<CoverMaster> list = new ArrayList<CoverMaster>();
		
		
		try {
			Date today = new Date();
			Calendar cal = new GregorianCalendar();
			cal.setTime(today);
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 1);
			today = cal.getTime();

		
			// Find Latest Record
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<CoverMaster> query = cb.createQuery(CoverMaster.class);

			// Find All
			Root<CoverMaster> b = query.from(CoverMaster.class);

			// Select
			query.select(b);

			// Amend Id Max Filter
			Subquery<Long> amendId = query.subquery(Long.class);
			Root<CoverMaster> ocpm1 = amendId.from(CoverMaster.class);
			amendId.select(cb.max(ocpm1.get("amendId")));
			Predicate a1 = cb.equal(ocpm1.get("coverId"), b.get("coverId"));
			
			amendId.where(a1);

			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(b.get("coverName")));

			// Where
			Predicate n1 = cb.equal(b.get("amendId"), amendId);
			Predicate n2 = cb.equal(b.get("coverId"), req.getCoverId());
			Predicate n3;
		    if(req.getCompanyId().equals("100040") && !req.getSubCoverId().isBlank())
		    {
		    	 n3 =  cb.equal(b.get("subCoverId"), req.getSubCoverId());
		    }
		    else {
		 n3 =  cb.equal(b.get("subCoverId"), "0" );  
		    }
			query.where(n1,n2,n3).orderBy(orderList);

			// Get Result
			TypedQuery<CoverMaster> result = em.createQuery(query);
			list = result.getResultList();
			
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return list;
	}
	public synchronized List<ListItemValue> getListItem(String insuranceId , String branchCode, String itemType) {
		List<ListItemValue> list = new ArrayList<ListItemValue>();
		try {
			Date today = new Date();
			Calendar cal = new GregorianCalendar();
			cal.setTime(today);
			today = cal.getTime();
			Date todayEnd = cal.getTime();
			
			// Criteria
	
			
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<ListItemValue> query=  cb.createQuery(ListItemValue.class);
			// Find All
			Root<ListItemValue> c = query.from(ListItemValue.class);
			
			//Select
			query.select(c);
			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(c.get("branchCode")));
			
			
			// Effective Date Start Max Filter
			Subquery<Date> effectiveDate = query.subquery(Date.class);
			Root<ListItemValue> ocpm1 = effectiveDate.from(ListItemValue.class);
			effectiveDate.select(cb.greatest(ocpm1.get("effectiveDateStart").as(Date.class)));
			Predicate a1 = cb.equal(c.get("itemId"),ocpm1.get("itemId"));
			Predicate a2 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
			effectiveDate.where(a1,a2);
			// Effective Date End Max Filter
			Subquery<Date> effectiveDate2 = query.subquery(Date.class);
			Root<ListItemValue> ocpm2 = effectiveDate2.from(ListItemValue.class);
			effectiveDate2.select(cb.greatest(ocpm2.get("effectiveDateEnd").as(Date.class)));
			Predicate a3 = cb.equal(c.get("itemId"),ocpm2.get("itemId"));
			Predicate a4 = cb.greaterThanOrEqualTo(ocpm2.get("effectiveDateEnd"), todayEnd);
			effectiveDate2.where(a3,a4);
						
			// Where
			Predicate n1 = cb.equal(c.get("status"),"Y");
			Predicate n11 = cb.equal(c.get("status"),"R");
			Predicate n12 = cb.or(n1,n11);
			Predicate n2 = cb.equal(c.get("effectiveDateStart"),effectiveDate);
			Predicate n3 = cb.equal(c.get("effectiveDateEnd"),effectiveDate2);	
			Predicate n4 = cb.equal(c.get("companyId"),insuranceId);
			Predicate n5 = cb.equal(c.get("companyId"), "99999");
			Predicate n6 = cb.equal(c.get("branchCode"),branchCode);
			Predicate n7 = cb.equal(c.get("branchCode"), "99999");
			Predicate n8 = cb.or(n4,n5);
			Predicate n9 = cb.or(n6,n7);
			Predicate n10 = cb.equal(c.get("itemType"),itemType);
			query.where(n12,n2,n3,n8,n9,n10).orderBy(orderList);
			// Get Result
			TypedQuery<ListItemValue> result = em.createQuery(query);
			list = result.getResultList();
			
			list = list.stream().filter(distinctByKey(o -> Arrays.asList(o.getItemCode()))).collect(Collectors.toList());
			list.sort(Comparator.comparing(ListItemValue :: getItemValue));
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return list ;
	}


	private static <T> java.util.function.Predicate<T> distinctByKey(java.util.function.Function<? super T, ?> keyExtractor) {
	    Map<Object, Boolean> seen = new ConcurrentHashMap<>();
	    return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
	}
	public  String getTypeDesc(String insuranceId , String branchCode , String itemType , String itemCode ) {
		String typeDesc = "" ;
		try {
			Date today = new Date();
			Calendar cal = new GregorianCalendar();
			cal.setTime(today);
			today = cal.getTime();
			Date todayEnd = cal.getTime();
			
			// Criteria
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<ListItemValue> query=  cb.createQuery(ListItemValue.class);
			// Find All
			Root<ListItemValue> c = query.from(ListItemValue.class);
			List<ListItemValue> list = new ArrayList<ListItemValue>();
			
			//Select
			query.select(c);
			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(c.get("branchCode")));
			
			
			// Effective Date Start Max Filter
			Subquery<Date> effectiveDate = query.subquery(Date.class);
			Root<ListItemValue> ocpm1 = effectiveDate.from(ListItemValue.class);
			effectiveDate.select(cb.greatest(ocpm1.get("effectiveDateStart").as(Date.class)));
			Predicate a1 = cb.equal(c.get("itemId"),ocpm1.get("itemId"));
			Predicate a4 = cb.equal(c.get("itemType"),ocpm1.get("itemType"));
			Predicate a2 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
			effectiveDate.where(a1,a2,a4);
			// Effective Date End Max Filter
			Subquery<Date> effectiveDate2 = query.subquery(Date.class);
			Root<ListItemValue> ocpm2 = effectiveDate2.from(ListItemValue.class);
			effectiveDate2.select(cb.greatest(ocpm2.get("effectiveDateEnd").as(Date.class)));
			Predicate a3 = cb.equal(c.get("itemId"),ocpm2.get("itemId"));
			Predicate a5 = cb.equal(c.get("itemType"),ocpm2.get("itemType"));
			Predicate a6 = cb.greaterThanOrEqualTo(ocpm2.get("effectiveDateEnd"), todayEnd);
			effectiveDate2.where(a3,a5,a6);
						
			// Where
			Predicate n1 = cb.equal(c.get("status"),"Y");
			Predicate n2 = cb.equal(c.get("effectiveDateStart"),effectiveDate);
			Predicate n3 = cb.equal(c.get("effectiveDateEnd"),effectiveDate2);	
			Predicate n4 = cb.equal(c.get("companyId"), insuranceId);
			Predicate n5 = cb.equal(c.get("companyId"), "99999");
			Predicate n6 = cb.equal(c.get("branchCode"), insuranceId);
			Predicate n7 = cb.equal(c.get("branchCode"), "99999");
			Predicate n8 = cb.or(n4,n5);
			Predicate n9 = cb.or(n6,n7);
			Predicate n10 = cb.equal(c.get("itemType"),itemType);
			Predicate n11 = cb.equal(c.get("itemCode"),itemCode);
			
			query.where(n1,n2,n3,n8,n9,n10,n11).orderBy(orderList);
			// Get Result
			TypedQuery<ListItemValue> result = em.createQuery(query);
			list = result.getResultList();
			typeDesc = list.size()> 0 ? list.get(0).getItemValue() : "" ;
			
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return typeDesc ;
	}

	public String getCompanyProductMasterDropdown(String companyId , String productId) {
		String productName = "";
		try {
			Date today = new Date();
			Calendar cal = new GregorianCalendar();
			cal.setTime(today);
			cal.set(Calendar.HOUR_OF_DAY, 23);;
			cal.set(Calendar.MINUTE, 1);
			today = cal.getTime();
			cal.set(Calendar.HOUR_OF_DAY, 1);
			cal.set(Calendar.MINUTE, 1);
			Date todayEnd = cal.getTime();
			
			// Criteria
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<CompanyProductMaster> query=  cb.createQuery(CompanyProductMaster.class);
			List<CompanyProductMaster> list = new ArrayList<CompanyProductMaster>();
			// Find All
			Root<CompanyProductMaster> c = query.from(CompanyProductMaster.class);
			//Select
			query.select(c);
			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(c.get("productName")));
			
			// Effective Date Start Max Filter
			Subquery<Date> effectiveDate = query.subquery(Date.class);
			Root<CompanyProductMaster> ocpm1 = effectiveDate.from(CompanyProductMaster.class);
			effectiveDate.select(cb.greatest(ocpm1.get("effectiveDateStart").as(Date.class)));
			Predicate a1 = cb.equal(c.get("productId"),ocpm1.get("productId"));
			Predicate a2 = cb.equal(c.get("companyId"),ocpm1.get("companyId"));
			Predicate a3 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
			effectiveDate.where(a1,a2,a3);
			// Effective Date End Max Filter
			Subquery<Date> effectiveDate2 = query.subquery(Date.class);
			Root<CompanyProductMaster> ocpm2 = effectiveDate2.from(CompanyProductMaster.class);
			effectiveDate2.select(cb.greatest(ocpm2.get("effectiveDateEnd").as(Date.class)));
			Predicate a4 = cb.equal(c.get("productId"),ocpm2.get("productId"));
			Predicate a5 = cb.equal(c.get("companyId"),ocpm2.get("companyId"));
			Predicate a6 = cb.greaterThanOrEqualTo(ocpm2.get("effectiveDateEnd"), todayEnd);
			effectiveDate2.where(a4,a5,a6);
			
			// Where
			Predicate n1 = cb.equal(c.get("status"),"Y");
			Predicate n2 = cb.equal(c.get("effectiveDateStart"),effectiveDate);
			Predicate n3 = cb.equal(c.get("effectiveDateEnd"),effectiveDate2);	
			Predicate n4 = cb.equal(c.get("companyId"),companyId);
			Predicate n5 = cb.equal(c.get("productId"),productId);
			query.where(n1,n2,n3,n4,n5).orderBy(orderList);
			// Get Result
			TypedQuery<CompanyProductMaster> result = em.createQuery(query);
			list = result.getResultList();
			productName  = list.size()> 0 ? list.get(0).getProductName() : "";	
		}
			catch(Exception e) {
				e.printStackTrace();
				log.info("Exception is --->"+e.getMessage());
				return null;
				}
			return productName;
		}
	
	
	public String getProductSectionDropdown(String companyId , String productId , String sectionId) {
		String sectionName = "";
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
			orderList.add(cb.asc(c.get("sectionName")));
			
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
			jakarta.persistence.criteria.Predicate n4 = cb.equal(c.get("companyId"),companyId);
			jakarta.persistence.criteria.Predicate n5 = cb.equal(c.get("productId"), productId);
			Predicate n6 = cb.equal(c.get("sectionId"), sectionId);
			query.where(n1,n2,n3,n4,n5,n6).orderBy(orderList);
			
			// Get Result
			TypedQuery<ProductSectionMaster> result = em.createQuery(query);			
			list =  result.getResultList();  
			sectionName = list.size()> 0 ? list.get(0).getSectionName() : "";	
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return sectionName;
	}
	
	public String getInscompanyMasterDropdown(String companyId ) {
		String companyName = "" ;
		try {
			Date today  = new Date();
			Calendar cal = new GregorianCalendar(); 
			cal.setTime(today);
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 1);
			today   = cal.getTime();
			cal.set(Calendar.HOUR_OF_DAY, 1);
			cal.set(Calendar.MINUTE, 1);
			Date todayEnd = cal.getTime();
			
			// Criteria
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<InsuranceCompanyMaster> query = cb.createQuery(InsuranceCompanyMaster.class);
			List<InsuranceCompanyMaster> list = new ArrayList<InsuranceCompanyMaster>();
			
			// Find All
			Root<InsuranceCompanyMaster>    c = query.from(InsuranceCompanyMaster.class);		
			
			// Select
			query.select(c );
			
		
			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(c.get("companyName")));
			
			// Effective Date Max Filter
			Subquery<Date> effectiveDate = query.subquery(Date.class);
			Root<InsuranceCompanyMaster> ocpm1 = effectiveDate.from(InsuranceCompanyMaster.class);
			effectiveDate.select(cb.greatest(ocpm1.get("effectiveDateStart").as(Date.class)));
			jakarta.persistence.criteria.Predicate a1 = cb.equal(c.get("companyId"),ocpm1.get("companyId") );
			jakarta.persistence.criteria.Predicate a2 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
			effectiveDate.where(a1,a2);
			
			// Effective Date End
			Subquery<Date> effectiveDate2 = query.subquery(Date.class);
			Root<InsuranceCompanyMaster> ocpm2 = effectiveDate2.from(InsuranceCompanyMaster.class);
			effectiveDate2.select(cb.greatest(ocpm2.get("effectiveDateEnd").as(Date.class)));
			jakarta.persistence.criteria.Predicate a3 = cb.equal(c.get("companyId"),ocpm2.get("companyId") );
			jakarta.persistence.criteria.Predicate a4 = cb.greaterThanOrEqualTo(ocpm2.get("effectiveDateEnd"), todayEnd);
			effectiveDate2.where(a3,a4);
			
		    // Where	
			jakarta.persistence.criteria.Predicate n1 = cb.equal(c.get("status"), "Y");
			jakarta.persistence.criteria.Predicate n2 = cb.equal(c.get("effectiveDateStart"), effectiveDate);
			jakarta.persistence.criteria.Predicate n3 = cb.equal(c.get("effectiveDateEnd"), effectiveDate2);
			Predicate n4 = cb.equal(c.get("companyId"), companyId);
			
			query.where(n1,n2,n3,n4).orderBy(orderList);
	
			// Get Result
			TypedQuery<InsuranceCompanyMaster> result = em.createQuery(query);
			list = result.getResultList();
			companyName  = list.size()> 0 ? list.get(0).getCompanyName() : "";	
				
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return companyName;
	}
	
	
	@Override
	public List<ProductBenefitGetRes> getallProductBenefit(ProductBenefitGetAllReq req) {
		List<ProductBenefitGetRes> resList = new ArrayList<ProductBenefitGetRes>();
		DozerBeanMapper dozerMapper = new  DozerBeanMapper();
		try {
			Date today  = new Date();
			Calendar cal = new GregorianCalendar();
			cal.setTime(today);
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 1);
			today = cal.getTime();
			cal.set(Calendar.HOUR_OF_DAY, 1);
			cal.set(Calendar.MINUTE, 1);
			Date todayEnd = cal.getTime();
			
			List<ProductBenefitMaster> list = new ArrayList<ProductBenefitMaster>();
		
			// Find Latest Record
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<ProductBenefitMaster> query = cb.createQuery(ProductBenefitMaster.class);
	
			// Find All
			Root<ProductBenefitMaster> b = query.from(ProductBenefitMaster.class);
	
			// Select
			query.select(b);
	
			// Effective Date End
			Subquery<Long> amendId = query.subquery(Long.class);
			Root<ProductBenefitMaster> ocpm1 = amendId.from(ProductBenefitMaster.class);
			amendId.select(cb.max(ocpm1.get("amendId")));
			Predicate a6 = cb.equal(ocpm1.get("companyId"), b.get("companyId"));
			Predicate a7 = cb.equal(ocpm1.get("productId"), b.get("productId"));
			Predicate a8 = cb.equal(ocpm1.get("sectionId"), b.get("sectionId"));
			Predicate a9 = cb.equal(ocpm1.get("typeId"), b.get("typeId"));
			Predicate a10 = cb.equal(ocpm1.get("benefitId"), b.get("benefitId"));
			amendId.where(a6,a7,a8,a9,a10);
			
			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(b.get("benefitDescription")));
			
			
			// Where
			Predicate n1 = cb.equal(b.get("companyId"), req.getCompanyId());
			Predicate n2 = cb.equal(b.get("productId"), req.getProductId());
			Predicate n3 = cb.equal(b.get("sectionId"),req.getSectionId() );
			Predicate n4 = cb.equal(b.get("amendId"), amendId);
			Predicate n5 = cb.equal(b.get("coverId"), req.getCoverId());
			if(req.getSubcoverid()!=null) {
			Predicate n6 = cb.equal(b.get("subCoverId"), req.getSubcoverid());
			query.where(n1,n2,n3,n4,n5,n6).orderBy(orderList);
			}
			else {
				query.where(n1,n2,n3,n4,n5).orderBy(orderList);	
			}
			// Get Result
			TypedQuery<ProductBenefitMaster> result = em.createQuery(query);
			list = result.getResultList();
			
//			// Map
			for (ProductBenefitMaster data : list ) {
				ProductBenefitGetRes res = new ProductBenefitGetRes();
	
				res = dozerMapper.map(data, ProductBenefitGetRes.class);
				resList.add(res);
			}
	
		} catch (Exception e) {
			e.printStackTrace();
			log.info(e.getMessage());
			return null;
	
		}
		return resList;
	}
	
	@Override
	public List<ProductBenefitGetRes> getActiveProductBenefit(ProductBenefitGetAllReq req) {
		List<ProductBenefitGetRes> resList = new ArrayList<ProductBenefitGetRes>();
		DozerBeanMapper dozerMapper = new  DozerBeanMapper();
		try {
			Date today  = new Date();
			Calendar cal = new GregorianCalendar();
			cal.setTime(today);
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 1);
			today = cal.getTime();
			cal.set(Calendar.HOUR_OF_DAY, 1);
			cal.set(Calendar.MINUTE, 1);
			Date todayEnd = cal.getTime();
			
			List<ProductBenefitMaster> list = new ArrayList<ProductBenefitMaster>();
		
			// Find Latest Record
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<ProductBenefitMaster> query = cb.createQuery(ProductBenefitMaster.class);
	
			// Find All
			Root<ProductBenefitMaster> b = query.from(ProductBenefitMaster.class);
	
			// Select
			query.select(b);
	
			// Effective Date End
			Subquery<Long> amendId = query.subquery(Long.class);
			Root<ProductBenefitMaster> ocpm1 = amendId.from(ProductBenefitMaster.class);
			amendId.select(cb.max(ocpm1.get("amendId")));
			Predicate a6 = cb.equal(ocpm1.get("companyId"), b.get("companyId"));
			Predicate a7 = cb.equal(ocpm1.get("productId"), b.get("productId"));
			Predicate a8 = cb.equal(ocpm1.get("sectionId"), b.get("sectionId"));
			Predicate a9 = cb.equal(ocpm1.get("typeId"), b.get("typeId"));
			Predicate a10 = cb.equal(ocpm1.get("benefitId"), b.get("benefitId"));
			amendId.where(a6,a7,a8,a9,a10);
			
			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(b.get("benefitDescription")));
			
			
			// Where
			Predicate n1 = cb.equal(b.get("companyId"), req.getCompanyId());
			Predicate n2 = cb.equal(b.get("productId"), req.getProductId());
			Predicate n3 = cb.equal(b.get("sectionId"),req.getSectionId() );
			Predicate n4 = cb.equal(b.get("amendId"), amendId);
			Predicate n5 = cb.equal(b.get("coverId"), req.getCoverId());
			Predicate n6 = cb.equal(b.get("status"), "Y" );
			query.where(n1,n2,n3,n4,n5,n6).orderBy(orderList);
			
			// Get Result
			TypedQuery<ProductBenefitMaster> result = em.createQuery(query);
			list = result.getResultList();
			
//			// Map
			for (ProductBenefitMaster data : list ) {
				ProductBenefitGetRes res = new ProductBenefitGetRes();
	
				res = dozerMapper.map(data, ProductBenefitGetRes.class);
				resList.add(res);
			}
	
		} catch (Exception e) {
			e.printStackTrace();
			log.info(e.getMessage());
			return null;
	
		}
		return resList;
	}
	
	@Override
	public ProductBenefitGetRes getByProductBenefitId(ProductBenefitGetReq req) {
		ProductBenefitGetRes res = new ProductBenefitGetRes();
		DozerBeanMapper dozerMapper = new  DozerBeanMapper();
		try {
			Date today  = new Date();
			Calendar cal = new GregorianCalendar();
			cal.setTime(today);
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 1);
			today = cal.getTime();
			cal.set(Calendar.HOUR_OF_DAY, 1);
			cal.set(Calendar.MINUTE, 1);
			Date todayEnd = cal.getTime();
			
			List<ProductBenefitMaster> list = new ArrayList<ProductBenefitMaster>();
		
			// Find Latest Record
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<ProductBenefitMaster> query = cb.createQuery(ProductBenefitMaster.class);
	
			// Find All
			Root<ProductBenefitMaster> b = query.from(ProductBenefitMaster.class);
	
			// Select
			query.select(b);
	
			// Effective Date End
			Subquery<Long> amendId = query.subquery(Long.class);
			Root<ProductBenefitMaster> ocpm1 = amendId.from(ProductBenefitMaster.class);
			amendId.select(cb.max(ocpm1.get("amendId")));
			Predicate a6 = cb.equal(ocpm1.get("companyId"), b.get("companyId"));
			Predicate a7 = cb.equal(ocpm1.get("productId"), b.get("productId"));
			Predicate a8 = cb.equal(ocpm1.get("sectionId"), b.get("sectionId"));
			Predicate a9 = cb.equal(ocpm1.get("typeId"), b.get("typeId"));
			Predicate a10 = cb.equal(ocpm1.get("benefitId"), b.get("benefitId"));
			amendId.where(a6,a7,a8,a9,a10);
			
			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(b.get("benefitDescription")));
			
			
			// Where
			Predicate n1 = cb.equal(b.get("companyId"), req.getCompanyId());
			Predicate n2 = cb.equal(b.get("productId"), req.getProductId());
			Predicate n3 = cb.equal(b.get("sectionId"),req.getSectionId() );
			Predicate n4 = cb.equal(b.get("amendId"), amendId);
			Predicate n5 = cb.equal(b.get("coverId"), req.getCoverId());
			Predicate n6 = cb.equal(b.get("benefitId"), req.getBenefitId() );
			query.where(n1,n2,n3,n4,n5,n6).orderBy(orderList);
			
			// Get Result
			TypedQuery<ProductBenefitMaster> result = em.createQuery(query);
			list = result.getResultList();
			
//			// Map
			res = dozerMapper.map(list.get(0) , ProductBenefitGetRes.class);
//			if(StringUtils.isNotBlank(list.get(0).getIconPath()) && new File(list.get(0).getIconPath()).exists()) {
//				res.setImageFile(new GetFileFromPath(list.get(0).getIconPath()).call().getImgUrl());
//			}
	
		} catch (Exception e) {
			e.printStackTrace();
			log.info(e.getMessage());
			return null;
	
		}
		return res;
	}
	
	@Override
	public SuccessRes changeStatusOfProductBenefit(ProductBenefitChangeStatusReq req) {
		SuccessRes res = new SuccessRes();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		try {
			List<ProductBenefitMaster> list = new ArrayList<ProductBenefitMaster>();
			
			// Find Latest Record
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<ProductBenefitMaster> query = cb.createQuery(ProductBenefitMaster.class);
			// Find all
			Root<ProductBenefitMaster> b = query.from(ProductBenefitMaster.class);
			//Select
			query.select(b);
	
			// Amend ID Max Filter
			Subquery<Long> amendId = query.subquery(Long.class);
			Root<ProductBenefitMaster> ocpm1 = amendId.from(ProductBenefitMaster.class);
			amendId.select(cb.max(ocpm1.get("amendId")));
			Predicate a1 = cb.equal(ocpm1.get("typeId"), b.get("typeId"));
			Predicate a2 = cb.equal(ocpm1.get("companyId"), b.get("companyId"));
			Predicate a3 = cb.equal(ocpm1.get("productId"),b.get("productId"));
			Predicate a4 = cb.equal(ocpm1.get("sectionId"),b.get("sectionId"));
			Predicate a5 = cb.equal(ocpm1.get("benefitId"),b.get("benefitId"));
	
			amendId.where(a1, a2,a3,a4,a5);
	
			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(b.get("branchCode")));
	
			// Where
			Predicate n1 = cb.equal(b.get("amendId"), amendId);
			Predicate n2 = cb.equal(b.get("companyId"), req.getCompanyId());
//			Predicate n3 = cb.equal(b.get("typeId"), req.getTypeId());
			Predicate n4 = cb.equal(b.get("benefitId"), req.getBenefitId());
			Predicate n5 = cb.equal(b.get("productId"),req.getProductId());
			Predicate n6 = cb.equal(b.get("sectionId"),req.getSectionId());
			
			query.where(n1,n2,n4,n5,n6).orderBy(orderList);
			
			// Get Result 
			TypedQuery<ProductBenefitMaster> result = em.createQuery(query);
			list = result.getResultList();
			ProductBenefitMaster updateRecord = list.get(0);
			updateRecord.setStatus(req.getStatus());
			repo.save(updateRecord);
			
			// Perform Update
			res.setResponse("Status Changed");
			res.setSuccessId(req.getBenefitId());
		}
		catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --> " + e.getMessage());
			return null;
			}
		return res;
	}
		
		
	@Override
	public List<ProductBenefitDropDownRes> getProductBenefitDropdown(ProductBenefitDropDownReq req) {
		List<ProductBenefitDropDownRes> resList = new ArrayList<ProductBenefitDropDownRes>();
		try {
 			Date today = new Date();
			Calendar cal = new GregorianCalendar();
			cal.setTime(today);
			today = cal.getTime();
			Date todayEnd = cal.getTime();
			
			// Criteria
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<ProductBenefitMaster> query=  cb.createQuery(ProductBenefitMaster.class);
			List<ProductBenefitMaster> list = new ArrayList<ProductBenefitMaster>();
			// Find All
			Root<ProductBenefitMaster> c = query.from(ProductBenefitMaster.class);
			//Select
			query.select(c);
			// Order By                        c
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(c.get("benefitDescription")));
			
			// Effective Date Start Max Filter
			Subquery<Date> effectiveDate = query.subquery(Date.class);
			Root<ProductBenefitMaster> ocpm1 = effectiveDate.from(ProductBenefitMaster.class);
			effectiveDate.select(cb.greatest(ocpm1.get("effectiveDateStart").as(Date.class)));
			Predicate a1 = cb.equal(c.get("benefitId"),ocpm1.get("benefitId"));
			Predicate a2 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
			Predicate a5 = cb.equal(c.get("companyId"),ocpm1.get("companyId"));
			Predicate a9 = cb.equal(c.get("productId"),ocpm1.get("productId"));
			Predicate a10 = cb.equal(c.get("sectionId"),ocpm1.get("sectionId"));
			Predicate a13 = cb.equal(c.get("typeId"),ocpm1.get("typeId"));
			Predicate a14 = cb.equal(c.get("benefitId"),ocpm1.get("benefitId"));
			
			effectiveDate.where(a1,a2,a5,a9,a10,a13,a14);
			
			// Effective Date End Max Filter
			Subquery<Date> effectiveDate2 = query.subquery(Date.class);
			Root<ProductBenefitMaster> ocpm2 = effectiveDate2.from(ProductBenefitMaster.class);
			effectiveDate2.select(cb.greatest(ocpm2.get("effectiveDateEnd").as(Date.class)));
			Predicate a3 = cb.equal(c.get("benefitId"),ocpm2.get("benefitId"));
			Predicate a4 = cb.greaterThanOrEqualTo(ocpm2.get("effectiveDateEnd"), todayEnd);
			Predicate a7 = cb.equal(c.get("companyId"),ocpm2.get("companyId"));
			Predicate a11 = cb.equal(c.get("productId"),ocpm2.get("productId"));
			Predicate a12 = cb.equal(c.get("sectionId"),ocpm2.get("sectionId"));
			Predicate a15 = cb.equal(c.get("typeId"),ocpm2.get("typeId"));
			Predicate a16 = cb.equal(c.get("benefitId"),ocpm2.get("benefitId"));
			
		
			effectiveDate2.where(a3,a4,a7,a11,a12,a15,a16);
			
			// Where
			Predicate n1 = cb.equal(c.get("status"),"Y");
			Predicate n12 = cb.equal(c.get("status"),"R");
			Predicate n13 = cb.or(n1,n12);
			Predicate n2 = cb.equal(c.get("effectiveDateStart"),effectiveDate);
			Predicate n3 = cb.equal(c.get("effectiveDateEnd"),effectiveDate2);	
			Predicate n4 = cb.equal(c.get("companyId"),req.getCompanyId());
			Predicate n8 = cb.equal(c.get("productId"), req.getProductId());
			Predicate n11 = cb.equal(c.get("sectionId"), req.getSectionId());
			Predicate n14 = cb.equal(c.get("coverId"), req.getCoverId());
			if(req.getSubcoverid()!=null) {
				Predicate n15 = cb.equal(c.get("subCoverId"), req.getSubcoverid());
				query.where(n13,n2,n3,n4,n8,n11,n14,n15).orderBy(orderList);
			}else
			{
			query.where(n13,n2,n3,n4,n8,n11,n14).orderBy(orderList);
			}
			// Get Result
			TypedQuery<ProductBenefitMaster> result = em.createQuery(query);
			list = result.getResultList();
			
//			// Grouping
//			Map<Integer ,List<ProductBenefitMaster>> groupByType = list.stream().collect(Collectors.groupingBy(ProductBenefitMaster :: getTypeId )) ;
//			
//			for (Integer type : groupByType.keySet()) { 
//				ProductBenefitDropDownRes btype = new ProductBenefitDropDownRes();
//				List<ProductBenefitMaster> filterType = groupByType.get(type);
//				filterType.sort(Comparator.comparing(ProductBenefitMaster :: getDisplayOrder));
//				
//			//	btype.setTypeId(filterType.get(0).getTypeId().toString() );
//			//	btype.setTypeDesc(filterType.get(0).getTypeDesc())
//				;
//				List<ProductBenefits> productBenefits = new ArrayList<ProductBenefits>();	
//				for(ProductBenefitMaster data :  filterType) {
//					ProductBenefits befit = new ProductBenefits();
//					befit.setCode(data.getBenefitId().toString()  );
//					befit.setCodeDesc(data.getBenefitDescription());
//					if(StringUtils.isNotBlank(data.getIconPath()) && new File(data.getIconPath()).exists()) {
//						befit.setImage(new GetFileFromPath(data.getIconPath()).call().getImgUrl());
//					}
//					//befit.setImage(data.getIconPath());
//					productBenefits.add(befit);
//					
//				}
//				btype.setProductBenefits(productBenefits);
//				resList.add(btype);				
//			}
				
			for (ProductBenefitMaster data : list) {
				// Response
				ProductBenefitDropDownRes res = new ProductBenefitDropDownRes();
				res.setBenefitId(data.getBenefitId().toString());
				res.setBenefitDescription(data.getBenefitDescription().toString());
				res.setCodeDescLocal(data.getBenefitDescriptionLocal());
				res.setSectionDesc(data.getSectionDesc());
				
				res.setLongDesc(data.getLongDesc());
				res.setCalcType(data.getCalcTypeDesc());
				res.setValue(data.getValue()==null?null: data.getValue().toString());
				resList.add(res);
			}
		}
			catch(Exception e) {
				e.printStackTrace();
				log.info("Exception is --->"+e.getMessage());
				return null;
				}
			return resList;
		}
	


		
		
	

	
	
	
	
}
