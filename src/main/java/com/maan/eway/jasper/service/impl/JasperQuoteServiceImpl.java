package com.maan.eway.jasper.service.impl;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.maan.eway.bean.CompanyProductMaster;
import com.maan.eway.bean.EserviceMotorDetails;
import com.maan.eway.bean.EserviceSectionDetails;
import com.maan.eway.bean.HomePositionMaster;
import com.maan.eway.bean.MotorDataDetails;
import com.maan.eway.chartaccount.JpqlQueryServiceImpl;
import com.maan.eway.common.res.CommonRes;
import com.maan.eway.error.Error;
import com.maan.eway.jasper.req.JasperDocumentReq;
import com.maan.eway.jasper.req.PremiumReportReq;
import com.maan.eway.jasper.res.AttachMentRes;
import com.maan.eway.jasper.res.JasperDocumentRes;
import com.maan.eway.jasper.res.MotorCoverNoteRes;
import com.maan.eway.jasper.res.MotorPrivateRes;
import com.maan.eway.jasper.res.TravelReportRes;
import com.maan.eway.jasper.res.ViewReportDetailsRes;
import com.maan.eway.jasper.service.JasperQuoteService;
import com.maan.eway.repository.ApiDocDownloadDetailRepository;
import com.maan.eway.repository.BranchMasterRepository;
import com.maan.eway.repository.EServiceSectionDetailsRepository;
import com.maan.eway.repository.ExcessMasterRepository;
import com.maan.eway.repository.HomePositionMasterRepository;
import com.maan.eway.repository.InsuranceCompanyMasterRepository;
import com.maan.eway.repository.LoginBranchMasterRepository;
import com.maan.eway.repository.LoginMasterRepository;
import com.maan.eway.repository.MotorDataDetailsRepository;
import com.maan.eway.repository.PersonalInfoRepository;
import com.maan.eway.repository.PolicyCoverDataRepository;
import com.maan.eway.thread.GetFileFromPath;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JsonDataSource;
import net.sf.jasperreports.engine.design.JRDesignSection;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;

@SuppressWarnings("deprecation")
@Service
public class JasperQuoteServiceImpl implements JasperQuoteService{

	@Autowired
	private JasperConfiguration config;

	@Autowired
	private HomePositionMasterRepository homeRepo;

	@Autowired
	private BranchMasterRepository branchRepo ;
	
	@Autowired
	private ApiDocDownloadDetailRepository apiDocDownloadDetailRepo;
	
	@Autowired
	private InsuranceCompanyMasterRepository insuranceComMasRepo;
	
	@Autowired
	private LoginMasterRepository loginMasterRepo;
	
	@Autowired
	private PersonalInfoRepository custRepo;
	
	@Autowired
	private LoginBranchMasterRepository loginBranchRepo;
	
	@Autowired
	private ExcessMasterRepository excessRepo;
	
	@Value(value = "${travel.productId}")
	private String travelProductId;
	
	@Value(value = "${report.file.path}")
	private String policyReportPath;
	
	@Value(value = "${spring.jpa.database}")
	private String dataBaseType;
	
	@Autowired
	private jasperServiceNew jasperServiceNew;
	
	@PersistenceContext
	private EntityManager em;
	
	@Autowired
	private JasperCustomServiceImple jasperCustomeImple;
	
	@Autowired
	private JpqlQueryServiceImpl jpqlQueryServiceImpl;
	
	@Autowired
	private MotorDataDetailsRepository motorDataDetailsRepo;
	
	@Autowired
	private PolicyCoverDataRepository coverDataRepository;
	
	@Autowired
	private EServiceSectionDetailsRepository eServiceSectionDetailsRepo;
	
	@Autowired
	private Gson gson;

	Logger log = LogManager.getLogger(JasperQuoteServiceImpl.class);
	
	@Override
	public JasperDocumentRes policyform(JasperDocumentReq req) {
		JasperDocumentRes res = new JasperDocumentRes();
		List<Error> errors = new ArrayList<>();
		try {
			
			  EserviceSectionDetails homeData=eServiceSectionDetailsRepo.findFirstByRequestReferenceNo(req.getRequestReferenceNo() );
			
		
			CompanyProductMaster product =  getCompanyProductMasterDropdown(homeData.getCompanyId() , homeData.getProductId().toString());

			if(homeData!=null && Arrays.asList(5,46).contains(homeData.getProductId()) && homeData.getCompanyId().equalsIgnoreCase("100002")) {
				List<MotorDataDetails> m = motorDataDetailsRepo.findByQuoteNo(req.getQuoteNo());
				IntStream.range(0,m.size()).forEach(i -> {
					MotorDataDetails k = m.get(i);
					String stickerNo = jasperCustomeImple.getStrickerNo(k.getQuoteNo(),k.getVehicleId());
					if(StringUtils.isBlank(stickerNo) || stickerNo == null) {
						errors.add(new Error(String.valueOf(i), "StickerNumber", "Cannot generate report because the StickerNumber is missing for Quote No: " + k.getQuoteNo() + " and Vehicle ID: " + k.getVehicleId()));
					}
				});
			}
			
			  if (!errors.isEmpty()) {
		            res.setErrorMessage(errors);
		            return res;
		        }
			
				Map<String, Object> input = new HashMap<String, Object>();
				String filePath = null;String Imagepath;
				log.info("OS Using ==> "+System.getProperty("os.name").toLowerCase());
				if(System.getProperty("os.name").toLowerCase().contains("windows")) {
					Imagepath = config.getImagePath().substring(1, config.getImagePath().length()-0);
				}else {
					Imagepath = config.getImagePath().replaceAll("\\\\", "/");
				}
				

				if (StringUtils.isNotBlank(homeData.getPolicyNo())) {
					input.put("pvPolicyNo", homeData.getPolicyNo());
					input.put("pvImagepath", Imagepath);
					filePath = config.getPolicyPath() + "pdf";

				} else {
					input.put("QuoteNo", req.getQuoteNo());
					input.put("pvImagepath", Imagepath);
					filePath = config.getDraftPath() + "pdf";
				}
				
				if (null != input && input.size() > 0) {
					File theDir = new File(filePath);
					if (!theDir.exists()) {
						theDir.mkdirs();
					}
					String jasperSaveLocation = policyReportPath.replaceAll("PolicyReport", "JsonFile")+(StringUtils.isBlank(homeData.getPolicyNo())?homeData.getRequestReferenceNo().replaceAll("[\\/:*?\"<>|]*", "")
							:homeData.getPolicyNo().replaceAll("[\\/:*?\"<>|]*", ""));
					if("100020".equalsIgnoreCase(homeData.getCompanyId())) {
						Map<String,Object> EwaySchedule = jasperServiceNew.getAllDataSingleQuery(req.getRequestReferenceNo(),homeData.getProductId());
						String jsonString = gson.toJson(EwaySchedule);
						Map<String, Object> input2 = new HashMap<String, Object>();
						input2.put("attachMents", EwaySchedule.get("attachMents"));
						input2.put("policyNo", EwaySchedule.get("policyNo"));
						input2.put("pvImagepath", Imagepath);
						
						  if(  "Y".equalsIgnoreCase(req.getBrokerQuoteYn())) {
							  
							  
						      if(homeData.getProductId().equals(13) ) { 
						    	  
						        res = getCommonJasperPdfFileByJson("/report/jasper/EwayKenyaQuotation.jrxml", jasperSaveLocation, jsonString, input2, "- KenyaQuotation.json");
						        
						    }else if (homeData.getProductId().equals(87)){
						    	
						    	 res = getCommonJasperPdfFileByJson("/report/jasper/ProfessionalIndemnityQuotation.jrxml", jasperSaveLocation, jsonString, input2, "- KenyaQuotation.json");
						    }else {

						    	res = getCommonJasperPdfFileByJson("/report/jasper/DomesticQuotation.jrxml", jasperSaveLocation, jsonString, input2, "- KenyaQuotation.json");
						    }
						  }
					}
					
			}else {
				res.setErrorMessage(errors);
				res.setPdfoutfile(null);
				res.setPdfoutfilepath(null);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}
		public synchronized CompanyProductMaster getCompanyProductMasterDropdown(String companyId, String productId) {
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
				product = list.size() > 0 ? list.get(0) :null;
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
			return product;
		}

		@SuppressWarnings("unchecked")
		private JasperDocumentRes getCommonJasperPdfFileByJson(String jrxmlPath,String jasperSaveLocation, String jsonString, Map<String, Object> map,String fileNameEnd) {
			log.info("Enter into getCommonJasperPdfFileByJson");
			JasperDocumentRes res = new JasperDocumentRes();
			InputStream inputStream=null;
			int count = 0;
			log.info(fileNameEnd.substring(2).replaceAll(".json", " ")+"JsonResponse ==> "+jsonString);
			try {
				
				JsonDataSource dataSource =null;
				
				try {
				    dataSource = new JsonDataSource(new ByteArrayInputStream(jsonString.getBytes()));
				} catch (Exception ex) {
				    System.err.println("Default encoding ByteArrayInputStream failed, retrying with UTF-8: " + ex.getMessage());
				    dataSource = new JsonDataSource(new ByteArrayInputStream(jsonString.getBytes(StandardCharsets.UTF_8)));
				}
				inputStream = this.getClass().getResourceAsStream(jrxmlPath);
				JasperReport jasperReport = JasperCompileManager.compileReport(inputStream);
				JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, map,dataSource);
				JasperExportManager.exportReportToPdfFile(jasperPrint, jasperSaveLocation+fileNameEnd.replace(".json", ".pdf"));
				String path = jasperSaveLocation+fileNameEnd.replace(".json", ".pdf");
				List<AttachMentRes> attachMentList = map.get("attachMents")==null?Collections.emptyList():(List<AttachMentRes>) map.get("attachMents");
				if(!attachMentList.isEmpty()) {
					PDFMergerUtility mergerUtility = new PDFMergerUtility();
					mergerUtility.setDestinationFileName(jasperSaveLocation+fileNameEnd.replace(".json", "_merged.pdf"));
					mergerUtility.addSource(jasperSaveLocation+fileNameEnd.replace(".json", ".pdf"));
					for(AttachMentRes attMap : attachMentList) {
						count = count+1;
						OutputStream outputStream = new FileOutputStream(new File(jasperSaveLocation+fileNameEnd.replace(".json", "_"+count+".pdf")));
						PdfReader pdfReader = new PdfReader(attMap.getDocloction());
						PdfStamper pdfStamper = new PdfStamper(pdfReader, outputStream);
						for(int i = 1;i<=pdfReader.getNumberOfPages();i++) {
							PdfContentByte contentByte = pdfStamper.getOverContent(i);
							contentByte.beginText();
							contentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_BOLD, BaseFont.CP1257, BaseFont.EMBEDDED), 12);
							contentByte.setTextMatrix(125, pdfReader.getPageSizeWithRotation(i).getHeight()-20);
							contentByte.showText("Attached to and Forming Part of Policy No. "+(map.get("policyNo")==null?"":map.get("policyNo").toString()));
							contentByte.endText();
						}
						pdfStamper.close();
						File Attfile = new File(jasperSaveLocation+fileNameEnd.replace(".json", "_"+count+".pdf"));
						mergerUtility.addSource(Attfile);
					}
					mergerUtility.mergeDocuments();
					if(count>0) {
						for(int f=0;f<count;f++) {
							File Attfile = new File(jasperSaveLocation+fileNameEnd.replace(".json", "_"+count+".pdf"));
							Attfile.delete();
						}
							File file1 = new File(jasperSaveLocation+fileNameEnd.replace(".json", ".pdf"));
							file1.delete();
					}
					path = jasperSaveLocation+fileNameEnd.replace(".json", "_merged.pdf");
				}
				GetFileFromPath filePath = new GetFileFromPath(path);
				res.setPdfoutfile(filePath.call().getImgUrl());
				res.setPdfoutfilepath(path);
			}catch(Exception e) {
				log.info("Error in getCommonJasperPdfFileByJson ==> "+e.getMessage());
				e.printStackTrace();
			}finally {
				if(inputStream!=null)
					try {
						inputStream.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
			}
			log.info("Exit into getCommonJasperPdfFileByJson");
			return res;
		}


}
