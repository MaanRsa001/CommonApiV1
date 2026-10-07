package com.maan.eway.preinspection;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;
import org.springframework.web.multipart.MultipartFile;
import com.maan.eway.error.Error;
import com.maan.eway.repository.HomePositionMasterRepository;
import com.maan.eway.repository.PersonalInfoRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Tuple;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;

import com.maan.eway.bean.CoverDocumentMaster;
import com.maan.eway.bean.HomePositionMaster;
import com.maan.eway.bean.PersonalInfo;
import com.maan.eway.bean.PreinspectionDataDetail;
import com.maan.eway.bean.PreinspectionImageDetail;
import com.maan.eway.bean.PreinspectionUploadDetails;
import com.maan.eway.common.res.CommonRes;
import com.maan.eway.document.service.impl.BASE64DecodedMultipartFile;





@Service
public class PreInspectionUploadDetailsServiceImpl implements PreInspectionUploadDetailsService{
	
	@Autowired
	private PreinspectionUploadDetailsRepo preInsUploadDetailsRepo;
	
	@Autowired
	private PreInspectionDataImageRepo preInspectionDataImageRepo;
	
	@Autowired
	private PreInspectionDataDetailRepo preInsDataRepo;
	
	@Autowired
	private HomePositionMasterRepository homeRepo;
	
	@Autowired
	private PersonalInfoRepository personalrepo;
	
	@Value("${file.directoryPath}")
	private String directoryPath;
	
	@PersistenceContext
	private EntityManager em;
	
	private Logger log=LogManager.getLogger(PreInspectionUploadDetailsServiceImpl.class);

	@Override
	public Object savePreInspectionDetails(PreInspectionUploadDetailSaveReq req) {
		
		try {
			List<Error> validation = preInsValidation(req);
			
			if(validation.isEmpty()) {
				Map<String,Object> respBlock = new HashMap<>();
				PreinspectionUploadDetails det = preInsUploadDetailsRepo.findByRegistrationNoAndChassisNo(req.getRegistrationNo(),req.getChassisNo());
				PreinspectionDataDetail dataDetails = preInsDataRepo.findByRegistrationNoAndChassisNo(req.getRegistrationNo(),req.getChassisNo());
				PreinspectionUploadDetails updateData = new PreinspectionUploadDetails();
				if(det != null) {
					updateData.setQuoteNo(req.getQuoteNo());
					updateData.setSNo(det.getSNo());
					updateData.setCustomerName(det.getCustomerName());
					updateData.setRegistrationNo(det.getRegistrationNo());
					updateData.setChassisNo(det.getChassisNo());
					updateData.setMobileNo(det.getMobileNo());
					updateData.setPolicyStartDate(det.getPolicyStartDate());
					updateData.setPolicyEndDate(det.getPolicyEndDate());
					updateData.setProductId(det.getProductId());
					updateData.setProductName(det.getProductName());
					updateData.setSectionId(det.getSectionId());
					updateData.setSectionName(det.getSectionName());
					updateData.setCompanyId(det.getCompanyId());
					updateData.setEntryDate(det.getEntryDate());
					updateData.setPremium(det.getPremium());
					updateData.setStatus(det.getStatus());
					updateData.setReferenceNo(det.getReferenceNo());
					
					preInsUploadDetailsRepo.saveAndFlush(updateData);
					
					if(dataDetails != null) {
						dataDetails.setQuoteNo(req.getQuoteNo());
						preInsDataRepo.save(dataDetails);
					} 
					
					respBlock.put("Result", "Update Successfully");
					respBlock.put("IsError", false);
					respBlock.put("ErrorMessage", Collections.emptyList());
					respBlock.put("Message", "Success");
					return respBlock;
				}else {
					PreinspectionUploadDetails detail = new PreinspectionUploadDetails();
					Long maxSno = preInsUploadDetailsRepo.count();
					maxSno = maxSno + 1;
					detail.setQuoteNo(req.getQuoteNo());
					detail.setSNo(maxSno);
					detail.setCustomerName(req.getCustomerName());
					detail.setRegistrationNo(req.getRegistrationNo());
					detail.setChassisNo(req.getChassisNo());
					detail.setMobileNo(Long.valueOf(req.getMobileNo()));
					detail.setPolicyStartDate(req.getPolicyStartDate());
					detail.setPolicyEndDate(req.getPolicyEndDate());
					detail.setProductId(Long.valueOf(req.getProductId()));
					detail.setProductName(req.getProductName());
					detail.setSectionId(Long.valueOf(req.getSectionId()));
					detail.setSectionName(req.getSectionName());
					detail.setCompanyId(Long.valueOf(req.getCompanyId()));
					detail.setEntryDate(req.getEntryDate());
					detail.setPremium(new BigDecimal(req.getPremium()));
					Long tranId = preInsDataRepo.count();
					tranId = tranId + 1;
					detail.setReferenceNo(tranId);
					detail.setStatus("Y");
					//detail.setReferenceNo(preInsDataRepo.getTranId());
					preInsUploadDetailsRepo.saveAndFlush(detail);
					
					PreinspectionDataDetail dataDetail = new PreinspectionDataDetail();
					
					dataDetail.setChassisNo(req.getChassisNo());
					dataDetail.setRegistrationNo(req.getRegistrationNo());
					dataDetail.setEntry_date(new Date());
					dataDetail.setQuoteNo(req.getQuoteNo());
					dataDetail.setTranId(detail.getReferenceNo());
					dataDetail.setStatus("Y");
					dataDetail.setMobileNo(req.getMobileNo());
					preInsDataRepo.save(dataDetail);
					
					
					respBlock.put("Result", "Successfully Insert");
					respBlock.put("IsError", false);
					respBlock.put("ErrorMessage", Collections.emptyList());
					respBlock.put("Message", "Success");
					return respBlock;
				}
				
			}else {
				Map<String,Object> respBlock = new HashMap<>();
				respBlock.put("Result", null);
				respBlock.put("IsError", true);
				respBlock.put("ErrorMessage", validation);
				respBlock.put("Message", "Failed");
				return respBlock;
			}
			
		}catch(Exception e) {
			e.printStackTrace();
			Map<String,Object> respBlock = new HashMap<>();
			respBlock.put("Result", null);
			respBlock.put("IsError", true);
			respBlock.put("ErrorMessage", e.getMessage());
			respBlock.put("Message", "Failed");
			return respBlock;
		}
	}

	private List<Error> preInsValidation(PreInspectionUploadDetailSaveReq req) {
		List<Error> validations = new ArrayList<>();
		try {
			//PreinspectionUploadDetails det = preInsUploadDetailsRepo.findByQuoteNo(req.getQuoteNo());
			//if(det == null) {
				if(StringUtils.isBlank(req.getQuoteNo())) {
					//validations.add(new Error("Quote Number doest not allowed empty value","QuoteNo","01"));
					validations.add(new Error("01","QuoteNo","Quote Number doest not allowed empty value"));
				}
				
				if(StringUtils.isBlank(req.getCustomerName())) {
				//	validations.add(new Error("Customer Name doest not allowed empty value","CustomerName","02"));
					validations.add(new Error("02","CustomerName","Customer Name doest not allowed empty value"));
				}
				
				if(StringUtils.isBlank(req.getRegistrationNo())) {
					//validations.add(new Error("Registration Number doest not allowed empty value","RegistrationNo","03"));
					validations.add(new Error("03","RegistrationNo","Registration Number doest not allowed empty value"));
				}
				//else {
				//	PreinspectionUploadDetails details = preInsUploadDetailsRepo.findByRegistrationNo(req.getRegistrationNo());
				//	if(details != null) {
					//	details = details.stream().filter(d -> d.getRegistrationNo().equalsIgnoreCase(req.getRegistrationNo())).toList();
					//	if(details.size() > 0) {
					//		validations.add(new Error("Registration Number is Already Available","RegistrationNo","03"));
						//}
				//	}
					
			//	}
				
				if(StringUtils.isBlank(req.getChassisNo())) {
					//validations.add(new Error("Chassis Number doest not allowed empty value","ChassisNo","04"));
					validations.add(new Error("04","ChassisNo","Chassis Number doest not allowed empty value"));
				}
				//else {
				//	PreinspectionUploadDetails details = preInsUploadDetailsRepo.findByChassisNo(req.getChassisNo());
				//	if(details != null) {
					//	details = details.stream().filter(d -> d.getChassisNo().equalsIgnoreCase(req.getChassisNo())).toList();
					//	if(details.size() > 0) {
				//			validations.add(new Error("Chassis Number is Already Available","RegistrationNo","03"));
					//	}
					/*if(det != null) {
						if(det.getChassisNo().equalsIgnoreCase(req.getChassisNo())) {
							validations.add(new Error("Registration Number is Already Available","ChassisNo","04"));
						} */
				//	}
					
				//}
				
				if(StringUtils.isBlank(req.getMobileNo())) {
				//	validations.add(new Error("Mobile Number doest not allowed empty value","MobileNo","05"));
					validations.add(new Error("05","MobileNo","Mobile Number doest not allowed empty value"));
				}
				if(StringUtils.isBlank(req.getProductId())) {
				//	validations.add(new Error("Product Id doest not allowed empty value","ProductId","06"));
					validations.add(new Error("06","ProductId","Product Id doest not allowed empty value"));
				}
				if(StringUtils.isBlank(req.getProductName())) {
					//validations.add(new Error("Product Name doest not allowed empty value","ProductName","07"));
					validations.add(new Error("07","ProductName","Product Name doest not allowed empty value"));
				}
				if(StringUtils.isBlank(req.getSectionId())) {
					//validations.add(new Error("Section Id doest not allowed empty value","SectionId","08"));
					validations.add(new Error("08","SectionId","Section Id doest not allowed empty value"));
				}
				if(StringUtils.isBlank(req.getSectionName())) {
					//validations.add(new Error("Section Name doest not allowed empty value","SectionName","09"));
					validations.add(new Error("09","SectionName","Section Name doest not allowed empty value"));
				}
				if(StringUtils.isBlank(req.getCompanyId())) {
				//	validations.add(new Error("Company Id doest not allowed empty value","CompanyId","10"));
					validations.add(new Error("10","CompanyId","Company Id doest not allowed empty value"));
				}
				if(StringUtils.isBlank(req.getPremium())) {
					//validations.add(new Error("Premium doest not allowed empty value","Premium","11"));
					validations.add(new Error("11","Premium","Premium doest not allowed empty value"));
				}
				
		//	}
			return validations;
		}catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public GetPreInspectionUploadDetailRes getPreInspectionDetails(GetPreInspectionUploadDetailReq req) {
		GetPreInspectionUploadDetailRes res = new GetPreInspectionUploadDetailRes();
		try {
			Long tranId = 0l;
			List<GetPreInspectionDocumentRes> documentList = new ArrayList<>();
			List<GetPreInspectinUploadList> uploadList = new ArrayList<>();
			PreinspectionUploadDetails preinspectionUploadDetails = null;
			if (req.getQuoteNo() != null) {
				preinspectionUploadDetails = preInsUploadDetailsRepo.findByQuoteNo(req.getQuoteNo());
			} else {
				PreinspectionUploadDetails byRegistrationNo = preInsUploadDetailsRepo
						.findByRegistrationNo(req.getRegistrationNo());
				preinspectionUploadDetails = byRegistrationNo;
			}

			if (preinspectionUploadDetails != null) {
				res.setChassisNo(preinspectionUploadDetails.getChassisNo());
				res.setCompanyId(preinspectionUploadDetails.getCompanyId().toString());
				res.setCustomerName(preinspectionUploadDetails.getCustomerName());
				res.setMobileNo(preinspectionUploadDetails.getMobileNo().toString());
				res.setPolicyEndDate(preinspectionUploadDetails.getPolicyEndDate());
				res.setPolicyStartDate(preinspectionUploadDetails.getPolicyStartDate());
				res.setPremium(preinspectionUploadDetails.getPremium().toString());
				res.setProductId(preinspectionUploadDetails.getProductId().toString());
				res.setQuoteNo(preinspectionUploadDetails.getQuoteNo());
				res.setProductName(preinspectionUploadDetails.getProductName());
				res.setRegistrationNo(preinspectionUploadDetails.getRegistrationNo());
				res.setSectionId(preinspectionUploadDetails.getSectionId().toString());
				res.setSectionName(preinspectionUploadDetails.getSectionName());
				res.setReferenceNo(preinspectionUploadDetails.getReferenceNo().toString());

				List<PreinspectionDataDetail> byRegistrationNo = preInsDataRepo
						.findByRegistrationNo(preinspectionUploadDetails.getRegistrationNo());
				if (byRegistrationNo != null && !byRegistrationNo.isEmpty()) {
					tranId = byRegistrationNo.get(0).getTranId();
				} else {
					List<PreinspectionDataDetail> byChassisNo = preInsDataRepo
							.findByChassisNo(preinspectionUploadDetails.getChassisNo());
					if (byChassisNo != null && !byChassisNo.isEmpty()) {
						tranId = byChassisNo.get(0).getTranId();
					}
				}

				List<PreinspectionImageDetail> tranIdList = preInspectionDataImageRepo.findByTranId(tranId);
				tranIdList = tranIdList.stream().filter(s -> s.getStatus().equalsIgnoreCase("Y")).toList();
				if (tranIdList != null && !tranIdList.isEmpty()) {
					for (PreinspectionImageDetail preinspectionImageDetail : tranIdList) {
						GetPreInspectinUploadList uploadRes = new GetPreInspectinUploadList();
						uploadRes.setImageName(preinspectionImageDetail.getImageName());
						uploadRes.setImageFilePath(preinspectionImageDetail.getImageFilePath());
						uploadRes.setEntry_date(preinspectionImageDetail.getEntry_date());
						uploadRes.setReferenceNo(preinspectionImageDetail.getTranId().toString());
						
						String imagUrl = GetFileFromPath(uploadRes.getImageFilePath());
						uploadRes.setImgUrl(imagUrl == null ? "" : imagUrl);
						uploadList.add(uploadRes);
					}
				}

				List<CoverDocumentMaster> docList = getDocMasterDropdown(
						preinspectionUploadDetails.getCompanyId().toString(), preinspectionUploadDetails.getProductId().toString(), preinspectionUploadDetails.getSectionId().toString());
			/*	List<WhatsappTemplateMaster> templateMasterList = whatsappTemplateMasterRepo
						.findByRemarksAndIsdocuplyn("MOT010", "Y");
				if (templateMasterList != null && !templateMasterList.isEmpty()) {
					for (WhatsappTemplateMaster whatsappTemplateMaster : templateMasterList) {
						GetPreInspectionDocumentRes documentres = new GetPreInspectionDocumentRes();
						documentres.setDocumentName(whatsappTemplateMaster.getStage_desc());
						documentList.add(documentres);
					}
				}*/
				
				    String documentType = "";

			        // Check Section ID
			        if ("99999".equals(preinspectionUploadDetails.getSectionId().toString()) && req.getQuoteNo() != null) {  // Section 99999 means customer-specific
			            HomePositionMaster homeData = homeRepo.findByQuoteNo(req.getQuoteNo());

			            if (homeData != null && homeData.getCustomerId() != null) {
			                PersonalInfo personalInfo = personalrepo.findByCustomerId(homeData.getCustomerId());

			                if (personalInfo != null && StringUtils.isNotBlank(personalInfo.getPolicyHolderType())) {
			                    if ("2".equalsIgnoreCase(personalInfo.getPolicyHolderType())) {
			                        documentType = "2"; // Corporate
			                    } else if ("1".equalsIgnoreCase(personalInfo.getPolicyHolderType())) {
			                        documentType = "1"; // Individual
			                    }
			                }
			            }
			        } else {
			            // If sectionId != 99999 — it's always Risk type (3)
			            documentType = "3";
			        }

			        String finalDocType = documentType; // final for lambda

			        List<CoverDocumentMaster> filterDocList = docList.stream()
			                .filter(o -> o.getDocumentType().equalsIgnoreCase(finalDocType))
			                .collect(Collectors.toList());
				if(docList != null && !docList.isEmpty()) {
					for(CoverDocumentMaster list : filterDocList) {
						GetPreInspectionDocumentRes documentres = new GetPreInspectionDocumentRes();
						documentres.setDocumentName(list.getDocumentName());
						documentres.setDocumentId(list.getDocumentId().toString());
						documentList.add(documentres);
					}
				}
				res.setUploadList(uploadList);
				res.setDocumentList(documentList);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}

	@Override
	public Object getPreInspectionImageDetails(GetPreInspectionUploadDetailReq req) {
		Map<String,Object> resp = new HashMap<>();
		List<GetPreInspectinUploadList> resList = new ArrayList<>();
		try {
			List<PreinspectionImageDetail> listImage = preInspectionDataImageRepo.findByTranIdAndQuoteNoAndStatus(Long.valueOf(req.getReferenceNo()),req.getQuoteNo(),"Y");
			
			if(!listImage.isEmpty() && listImage.size() >0) {
				for(PreinspectionImageDetail det : listImage) {
					GetPreInspectinUploadList res = new GetPreInspectinUploadList();
					res.setEntry_date(det.getEntry_date() == null ? null : det.getEntry_date());
					res.setImageFilePath(det.getImageFilePath() == null? null : det.getImageFilePath());
					res.setImageName(det.getImageName() == null? null : det.getImageName() );
					res.setReferenceNo(det.getTranId() == null ? null : det.getTranId().toString());
					
					String imagUrl = GetFileFromPath(res.getImageFilePath());
					res.setImgUrl(imagUrl == null ? "" : imagUrl);
					
					resList.add(res);
				}
				resp.put("Message", "Success");
				resp.put("IsError", false);
				resp.put("ErrorMessage", Collections.EMPTY_LIST);
				
				resp.put("Result", resList);
				
				return resp;	
			}
			resp.put("Message", "Success");
			resp.put("IsError", false);
			resp.put("ErrorMessage", Collections.EMPTY_LIST);
			
			resp.put("Result", null);
			
			return resp;
		}catch(Exception e) {
			e.printStackTrace();
			resp.put("Message", "Failed");
			resp.put("IsError", true);
			resp.put("ErrorMessage", e.getMessage());
			
			resp.put("Result", null);
			
			return resp;
		}
	}

	private String GetFileFromPath(String path) throws IOException {
		File file=new File(path);
		 if(StringUtils.isNotBlank(path) && new File(path).exists())  {
			 byte[] array = FileUtils.readFileToByteArray(new File(path));
			 
			 
			 
			 MultipartFile baseM = new BASE64DecodedMultipartFile(array,file.getName());
			 String contenttype=baseM.getContentType();
			 String prefix = "data:"+contenttype+";base64,";
			 
			// Document doc= new Document();
			 String imgurlen=Base64Utils.encodeToString(array);
			 imgurlen = prefix+imgurlen;
			 return imgurlen;
		 }else {
			 System.out.println("File Is Not Found");
		 }
		return null;
	}

	@Override
	public Object deletePreInspectionImage(GetPreInspectionUploadDetailReq req) {
		Map<String,Object> res = new HashMap<>();
		Map<String,Object> result = new HashMap<>();
		try {
			List<PreinspectionImageDetail> imgList = preInspectionDataImageRepo.findByTranIdAndImageName(Long.valueOf(req.getReferenceNo()),req.getImageName());
			
			if(!imgList.isEmpty() && imgList.size() >0) {
				for(PreinspectionImageDetail det : imgList) {
					det.setStatus("N");
					preInspectionDataImageRepo.saveAndFlush(det);
				}
				
				res.put("Message", "Success");
				res.put("IsError", false);
				res.put("ErrorMessage", Collections.EMPTY_LIST);
				
				
				result.put("Response", "Document Deleted Sucessfully");
				
				res.put("Result", result);
				
				return res;	
			}
			res.put("Message", "Success");
			res.put("IsError", false);
			res.put("ErrorMessage", Collections.EMPTY_LIST);
			
			
			result.put("Response", null);
			
			res.put("Result", result);
			
			return res;	
		}catch(Exception e) {
			e.printStackTrace();
			res.put("Message", "Failed");
			res.put("IsError", true);
			res.put("ErrorMessage", e.getMessage());
			
			
			result.put("Response", "Document Deleted Failed");
			
			res.put("Result", result);
			
			return res;	
		}
	}

	@Override
	public FileUploadRes uploadFile(PreFileUploadReq req) {
		FileUploadRes response = new FileUploadRes();
		String isCamera ="N";
		try {
			//log.info("upload file request ::: "+cs.reqPrint(req));
			final String constant_path= directoryPath;
				String exif_status ="";
				Date exifDate =null;
				String base64Image = req.getBase64().split(",")[1];
				byte [] image  =Base64.getDecoder().decode(base64Image);
				String [] array =req.getOriginalFileName().split("[/]");
				
				String originalName = req.getOriginalFileName(); 
				String extension = "jpg"; 

				if (originalName != null && originalName.contains(".")) {
				    extension = originalName.substring(originalName.lastIndexOf('.') + 1);
				}

				String safeName = req.getFileName().replaceAll("\\s+", "_");

				String file_path = constant_path
				        + safeName
				        + "_"
				        + System.currentTimeMillis()
				        + "."
				        + extension;

				//String file_path =constant_path+array[0]+"_"+System.currentTimeMillis()+"."+array[1];
				Path path =Paths.get(file_path);
				Files.write(path, image);
				File file =new File(file_path);		
				
			/*	Metadata metadata1 = ImageMetadataReader.readMetadata(file);			          			              
				if("N".equals(isCamera)) {
				    //ExifSubIFDDirectory directory = metadata1.getFirstDirectoryOfType(ExifSubIFDDirectory.class);
				   // log.info("/metadata response :"+cs.reqPrint(metadata1));
				   // log.info("/ExifSubIFDDirectory response :"+cs.reqPrint(directory));
				    if(directory!=null) {
					    	//exifDate = directory.getDate(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL);					   
					    	log.info("Extracted exifDate from Image ==>"+ exifDate);
					    	if(exifDate!=null) {
						    	LocalDate systemDate =LocalDate.now();			            		 
							    LocalDate date = exifDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
							    if(date.isBefore(systemDate) || date.isAfter(systemDate)) {
							    	//exif_status="INVALID";
							    	exif_status="VALID";
							    	log.info("Image date invalid ==>"+ date);
							    }else {
							    	log.info("Image date valid ==>"+ date);
							    	exif_status="VALID";
							    }
					    	}else {
					    		//exif_status="INVALID";
					    		exif_status="VALID";
					    	}
					    	
					 }else {
						 
						 exif_status="VALID";
						//exif_status="INVALID";
						 
					 }
				}else if ("Y".equals(isCamera)) {
					exif_status ="VALID";
					//FileSystemDirectory fsd = metadata1.getFirstDirectoryOfType(FileSystemDirectory.class);					   
					exifDate=fsd.getDate(3);
					
				} */
				
			if("image/jpeg".equalsIgnoreCase(req.getOriginalFileName()))	
				req.setOriginalFileName(req.getFileName().trim()+".jpeg");
			
			
		//	preInsImgRepo.insertImageDetails(req.getTranId(),req.getFileName(),file_path,exifDate,exif_status,req.getOriginalFileName(),isCamera);	
			
			PreinspectionImageDetail uploadImg = new PreinspectionImageDetail();
			uploadImg.setEntry_date(new Date());
			uploadImg.setImageFilePath(file_path);
			uploadImg.setImageName(req.getFileName());
			Long maxSno = preInspectionDataImageRepo.count();
			uploadImg.setSno(maxSno+1);
			uploadImg.setStatus("Y");
			uploadImg.setTranId(Long.valueOf(req.getTranId()));
			uploadImg.setExifImageDate(exifDate);
			uploadImg.setExifImageStatus(exif_status);
			uploadImg.setOriginalFileName(req.getOriginalFileName());
			uploadImg.setIsCaptureUpload(isCamera);
			uploadImg.setQuoteNo(req.getQuoteNo() == null ? null : req.getQuoteNo());
			
			preInspectionDataImageRepo.saveAndFlush(uploadImg);
			response.setResponse("SUCCESS");
			
		}catch (Exception e) {
			log.error(e);
			e.printStackTrace();
			response.setResponse("FAILED");
		}
		return response;
	}
	
	public List<CoverDocumentMaster> getDocMasterDropdown(String companyId, String productId, String sectionId) {
		List<CoverDocumentMaster> resList = new ArrayList<CoverDocumentMaster>();
		try {
			Date today = new Date();
			Calendar cal = new GregorianCalendar();
			cal.setTime(today);
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 1);
			today = cal.getTime();
			cal.set(Calendar.HOUR_OF_DAY, 1);
			cal.set(Calendar.MINUTE, 1);
			Date todayEnd = cal.getTime();
			// Criteria
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<CoverDocumentMaster> query = cb.createQuery(CoverDocumentMaster.class);

			Root<CoverDocumentMaster> c = query.from(CoverDocumentMaster.class);

			query.select(c);

			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(c.get("documentDesc")));

			// Effective Date Max Filter
			Subquery<Date> effectiveDate = query.subquery(Date.class);
			Root<CoverDocumentMaster> ocpm1 = effectiveDate.from(CoverDocumentMaster.class);
			effectiveDate.select(cb.greatest(ocpm1.get("effectiveDateStart").as(Date.class)));

			Predicate a10 = cb.equal(c.get("documentId"), ocpm1.get("documentId"));
			Predicate a1 = cb.equal(c.get("productId"), ocpm1.get("productId"));
			Predicate a3 = cb.equal(c.get("sectionId"), ocpm1.get("sectionId"));
			Predicate a4 = cb.equal(c.get("companyId"), ocpm1.get("companyId"));
			Predicate a5 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
			effectiveDate.where(a1, a3, a4, a5, a10);

			// Effective Date Max Filter
			Subquery<Date> effectiveDate2 = query.subquery(Date.class);
			Root<CoverDocumentMaster> ocpm2 = effectiveDate2.from(CoverDocumentMaster.class);
			effectiveDate2.select(cb.greatest(ocpm2.get("effectiveDateEnd").as(Date.class)));
			Predicate a11 = cb.equal(c.get("documentId"), ocpm2.get("documentId"));
			Predicate a6 = cb.equal(c.get("productId"), ocpm2.get("productId"));
			Predicate a7 = cb.equal(c.get("sectionId"), ocpm2.get("sectionId"));
			Predicate a8 = cb.equal(c.get("companyId"), ocpm2.get("companyId"));
			Predicate a9 = cb.greaterThanOrEqualTo(ocpm2.get("effectiveDateEnd"), todayEnd);
			effectiveDate2.where(a6, a7, a8, a9, a11);

			// Where
			Predicate p1 = cb.equal(c.get("status"), "Y");
			Predicate p2 = cb.equal(c.get("productId"), productId);
			Predicate p3 = cb.equal(c.get("companyId"), companyId);
			Predicate p4 = cb.equal(c.get("sectionId"), sectionId);
			Predicate p5 = cb.equal(c.get("effectiveDateStart"), effectiveDate);
			Predicate p6 = cb.equal(c.get("effectiveDateEnd"), effectiveDate2);
			query.where(p1, p2, p3, p4, p6, p5).orderBy(orderList);

			TypedQuery<CoverDocumentMaster> result = em.createQuery(query);
			resList = result.getResultList();

		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
		}
		return resList;
	}

	@Override
	public CommonRes preInspectionDashboard(GetPreInspectionUploadDetailReq req) {
		CommonRes res = new CommonRes();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		List<PreinspectionUploadDetails> upList = new ArrayList<>();
		List<GetPreInspectinUploadList> imgageList = new ArrayList<>();
		List<PreInspectionDashboardRes> totRes = new ArrayList<>();
		try {
			
			Date entryDate1 = sdf.parse(req.getFromDate());
			Date entryDate2 = sdf.parse(req.getToDate());
			Calendar cal = new GregorianCalendar();
			cal.setTime(entryDate1);
			cal.add(Calendar.DAY_OF_MONTH, -1);cal.set(Calendar.HOUR_OF_DAY, 23);cal.set(Calendar.MINUTE, 59);
			Date startDate = cal.getTime() ;
			cal.setTime(entryDate2);
			cal.add(Calendar.DAY_OF_MONTH, 0);cal.set(Calendar.HOUR_OF_DAY,23 );cal.set(Calendar.MINUTE, 59);
			Date endDate = cal.getTime() ;
			
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<PreinspectionUploadDetails> query = cb.createQuery(PreinspectionUploadDetails.class);

			Root<PreinspectionUploadDetails> c = query.from(PreinspectionUploadDetails.class);

			query.select(c);
			
			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(c.get("entryDate")));
						
			Predicate n1 = cb.equal(c.get("companyId"), Long.valueOf(req.getCompanyId()));
			Predicate n2=cb.between(c.get("entryDate"), startDate, endDate);

			query.where(n1,n2).orderBy(orderList);
			TypedQuery<PreinspectionUploadDetails> result = em.createQuery(query);
			upList = result.getResultList();
			System.out.println(upList);
			if(upList != null && !upList.isEmpty()) {
				for(PreinspectionUploadDetails updlis : upList) {
					PreInspectionDashboardRes dasRes = new PreInspectionDashboardRes();
					dasRes.setQuoteNo(updlis.getQuoteNo() == null ? null : updlis.getQuoteNo());
					dasRes.setChassisNo(updlis.getChassisNo() == null ? null : updlis.getChassisNo());
					dasRes.setRegistrationNo(updlis.getRegistrationNo() == null ? null : updlis.getRegistrationNo());
					dasRes.setReferenceNo(updlis.getReferenceNo() == null ? null : updlis.getReferenceNo().toString());
					dasRes.setCompanyId(updlis.getCompanyId() == null ? null : updlis.getCompanyId().toString());
					
					List<PreinspectionImageDetail> imageList = preInspectionDataImageRepo.findByQuoteNoAndStatus(updlis.getQuoteNo(),"Y");
					if(!imageList.isEmpty()) {
						for(PreinspectionImageDetail img : imageList) {
							GetPreInspectinUploadList imgres = new GetPreInspectinUploadList();
							imgres.setOriginalFileName(img.getOriginalFileName() == null ? null : img.getOriginalFileName());
							imgres.setImageFilePath(img.getImageFilePath() == null ? null : img.getImageFilePath());
							imgres.setImageName(img.getImageName() == null ? null : img.getImageName());
							imgres.setEntry_date(img.getEntry_date() == null ? null : img.getEntry_date());
							imgres.setSNo(img.getSno() == null ? null : img.getSno().toString());
							imgres.setAdminStatus(img.getExifImageStatus() == null ? null : img.getExifImageStatus());
							
							String imagUrl = GetFileFromPath(imgres.getImageFilePath());
							imgres.setImgUrl(imagUrl == null ? "" : imagUrl);
							
							imgageList.add(imgres);
							dasRes.setImageDetails(imgageList);
						}						
					}
					
					totRes.add(dasRes);
				}
				res.setCommonResponse(totRes);
		        res.setIsError(false);
		        res.setMessage("Success");
			}
		}catch(Exception e) {
			res.setIsError(true);
		    res.setMessage("Failed");
		}
		return res;
	}

}
