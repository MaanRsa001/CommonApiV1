package com.maan.eway.jasper.service.impl;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.maan.eway.bean.ApiDocDownloadDetail;
import com.maan.eway.bean.BranchMaster;
import com.maan.eway.bean.CompanyProductMaster;
import com.maan.eway.bean.EserviceCommonDetails;
import com.maan.eway.bean.EserviceSectionDetails;
import com.maan.eway.bean.ExcessMaster;
import com.maan.eway.bean.ExcessTransactionDetails;
import com.maan.eway.bean.HomePositionMaster;
import com.maan.eway.bean.LoginBranchMaster;
import com.maan.eway.bean.MotorDataDetails;
import com.maan.eway.bean.PersonalInfo;
import com.maan.eway.bean.PolicyCoverData;
import com.maan.eway.bean.ProductSectionMaster;
import com.maan.eway.bean.ReportJasperConfigMaster;
import com.maan.eway.bean.SectionDataDetails;
import com.maan.eway.chartaccount.JpqlQueryServiceImpl;
import com.maan.eway.common.req.PortFolioDashBoardReq;
import com.maan.eway.common.req.PortFolioGridReq;
import com.maan.eway.common.res.CommonRes;
import com.maan.eway.common.res.PortFolioAdminTupleRes;
import com.maan.eway.common.res.PortFolioDashBoardRes;
import com.maan.eway.common.res.PortfolioBrokerListRes;
import com.maan.eway.common.res.PortfolioGridRes;
import com.maan.eway.common.service.impl.GridServiceImpl;
import com.maan.eway.error.Error;
import com.maan.eway.jasper.req.GetApiDocReportReq;
import com.maan.eway.jasper.req.JasperDocumentReq;
import com.maan.eway.jasper.req.JasperReportDocReq;
import com.maan.eway.jasper.req.JasperScheduleReq;
import com.maan.eway.jasper.req.PdfJsonReq;
import com.maan.eway.jasper.req.PremiumReportReq;
import com.maan.eway.jasper.req.ReportRes;
import com.maan.eway.jasper.res.ApiDocListRes;
import com.maan.eway.jasper.res.AttachMentRes;
import com.maan.eway.jasper.res.CreditNoteRes;
import com.maan.eway.jasper.res.JasperDocumentRes;
import com.maan.eway.jasper.res.MotorCoverNoteRes;
import com.maan.eway.jasper.res.MotorPrivateRes;
import com.maan.eway.jasper.res.MotorPrivateVehicleDetails;
import com.maan.eway.jasper.res.PremiumReportRes;
import com.maan.eway.jasper.res.ReportBenefitInfoRes;
import com.maan.eway.jasper.res.ReportBrokerInfoRes;
import com.maan.eway.jasper.res.ReportCompanyInfoRes;
import com.maan.eway.jasper.res.ReportCoverInfoRes;
import com.maan.eway.jasper.res.ReportCustomerInfoRes;
import com.maan.eway.jasper.res.ReportExcessInfoRes;
import com.maan.eway.jasper.res.ReportLocationInfoRes;
import com.maan.eway.jasper.res.ReportQuoteInfoRes;
import com.maan.eway.jasper.res.ReportSectionInfoRes;
import com.maan.eway.jasper.res.ReportSectionMasterRes;
import com.maan.eway.jasper.res.TaxInvoiceRes;
import com.maan.eway.jasper.res.TravelReportRes;
import com.maan.eway.jasper.res.ViewReportDetailsRes;
import com.maan.eway.jasper.risklist.CoverDetailsDTO;
import com.maan.eway.jasper.risklist.RiskDetailsService;
import com.maan.eway.jasper.risklist.SectionDetailsDTO;
import com.maan.eway.jasper.service.JasperService;
import com.maan.eway.repository.ApiDocDownloadDetailRepository;
import com.maan.eway.repository.BranchMasterRepository;
import com.maan.eway.repository.EServiceSectionDetailsRepository;
import com.maan.eway.repository.ExcessMasterRepository;
import com.maan.eway.repository.ExcessTransactionDetailsRepository;
import com.maan.eway.repository.HomePositionMasterRepository;
import com.maan.eway.repository.InsuranceCompanyMasterRepository;
import com.maan.eway.repository.LoginBranchMasterRepository;
import com.maan.eway.repository.LoginMasterRepository;
import com.maan.eway.repository.MotorDataDetailsRepository;
import com.maan.eway.repository.PersonalInfoRepository;
import com.maan.eway.repository.PolicyCoverDataRepository;
import com.maan.eway.repository.ProductSectionMasterRepository;
import com.maan.eway.thread.GetFileFromPath;
import com.maan.eway.viewAll.dto.OverAllResForView;
import com.maan.eway.viewAll.dto.viewAllReq;
import com.maan.eway.viewAll.service.JasperViewAllBridgeService;
import com.maan.eway.viewAll.service.UgandaDebiteJsonService;

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
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JRVirtualizer;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JsonDataSource;
import net.sf.jasperreports.engine.design.JRDesignSection;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.engine.fill.JRFileVirtualizer;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleXlsxReportConfiguration;

@SuppressWarnings("deprecation")
@Service
public class JasperServiceImpl implements JasperService {

    @Autowired
    private JasperConfiguration config;

    @Autowired
    private HomePositionMasterRepository homeRepo;

    @Autowired
    private BranchMasterRepository branchRepo;

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

    @Autowired
    private ExcessTransactionDetailsRepository excessTransactionDetailsRepo;

    @Autowired
    private EServiceSectionDetailsRepository eserviceSectionDetailsRepo;

    @Autowired
    private GridServiceImpl gridServiceImpl;

    @Autowired
    private MotorDataDetailsRepository motorRepo;
    
   
    
    @Autowired
    private JasperViewAllBridgeService viewAllWithLableServiceImpl;

    @Autowired
    private RiskDetailsService riskDetailsServ;

    @Value(value = "${travel.productId}")
    private String travelProductId;

    @Value(value = "${report.file.path}")
    private String policyReportPath;

    @Value(value = "${spring.jpa.database}")
    private String dataBaseType;

    @Value(value = "${report.image.path}")
    private String externalImagePath;

    @Value(value = "${report.jasper.path}")
    private String externalJasperPath;

    @Value(value="${scheduledPdf}")
    private String scheduledPdfURl;

    @Autowired
    private ProductSectionMasterRepository productSectionMasterRepo;
    
    @Autowired
    private  UgandaDebiteJsonService ugandaTax;

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
    private Gson gson;

    Logger log = LogManager.getLogger(JasperServiceImpl.class);

    @Override
    public JasperDocumentRes policyform(JasperDocumentReq req) {
        JasperDocumentRes res = new JasperDocumentRes();
        List<Error> errors = new ArrayList<>();
        try {

            HomePositionMaster homeData = homeRepo.findByQuoteNo(req.getQuoteNo());
            CompanyProductMaster product = getCompanyProductMasterDropdown(homeData.getCompanyId(), homeData.getProductId().toString());
            System.out.println("Going to call Scheduled PDF");
            System.out.println("Home Data : "+homeData.toString());
            /// calling scheduled PDF
            if(homeData != null && homeData.getCompanyId().equals("100020")
                    && homeData.getProductId().equals(118) && homeData.getStatus().equalsIgnoreCase("P")){
                System.out.println("--> Calling Scheduled PDF for PolicyId: "+homeData.getPolicyNo());
                return getGolfersScheduledPdf(homeData.getPolicyNo());
            }

            if (homeData != null && Arrays.asList(5, 46).contains(homeData.getProductId()) && homeData.getCompanyId().equalsIgnoreCase("100002") && homeData.getStatus().equalsIgnoreCase("P")) {
                List<MotorDataDetails> m = motorDataDetailsRepo.findByQuoteNo(req.getQuoteNo());
                IntStream.range(0, m.size()).forEach(i -> {
                    MotorDataDetails k = m.get(i);
                    String stickerNo = jasperCustomeImple.getStrickerNo(k.getQuoteNo(), k.getVehicleId());
                    if (StringUtils.isBlank(stickerNo) || stickerNo == null) {
                        errors.add(new Error(String.valueOf(i), "StickerNumber", "Cannot generate report because the StickerNumber is missing for Quote No: " + k.getQuoteNo() + " and Vehicle ID: " + k.getVehicleId()));
                    }
                });
            }

            if (errors == null || errors.isEmpty()) {
                Map<String, Object> input = new HashMap<String, Object>();
                String filePath = null;
                String Imagepath;
                log.info("OS Using ==> " + System.getProperty("os.name").toLowerCase());
                String classpath = this.getClass().getClassLoader().getResource("").getPath();
                classpath = classpath.replaceAll("%20", " ");
                classpath = classpath.substring(1, classpath.length());
//                if (System.getProperty("os.name").toLowerCase().contains("windows")) {
//                    Imagepath = classpath + "report/images/";
//                } else {
//                    Imagepath = classpath + "report/images/".replaceAll("\\\\", "/");
//                }
                 Imagepath = externalImagePath;
                if (StringUtils.isNotBlank(homeData.getPolicyNo())) {
                    input.put("pvPolicyNo", homeData.getPolicyNo());
                    input.put("pvImagepath", externalImagePath);
                    filePath = config.getPolicyPath() + "pdf";

                } else {
                    input.put("QuoteNo", req.getQuoteNo());
                    input.put("pvImagepath", externalImagePath);
                    filePath = config.getDraftPath() + "pdf";
                }

                if (null != input && input.size() > 0) {
                    File theDir = new File(filePath);
                    if (!theDir.exists()) {
                        theDir.mkdirs();
                    }
                    if (StringUtils.isBlank(homeData.getPolicyNo()) && (Arrays.asList(5, 46, 63).contains(homeData.getProductId()))) {
                        if ("Y".equalsIgnoreCase(req.getBrokerQuoteYn()) && homeData.getProductId() != 63 && !homeData.getCompanyId().equalsIgnoreCase("100028") && !Arrays.asList("100046", "100047", "100048", "100049", "100050").contains(homeData.getCompanyId()) ) {
                        	
                            Map<String, Object> brokerQuotation = jasperCustomeImple.getMotorBrokerQuotation(homeData.getQuoteNo(), req.getTaxShowYn());
                            String jsonString = gson.toJson(brokerQuotation);
                            String jasperSaveLocation = policyReportPath.replaceAll("PolicyReport", "JsonFile") + homeData.getQuoteNo().replaceAll("[\\/:*?\"<>|]*", "");
                            res = getCommonJasperPdfFileByJson("/report/jasper/EwayBrokerQuotation.jrxml", jasperSaveLocation, jsonString, input, "- BrokerQuotation.json");
                        } else if ("Y".equalsIgnoreCase(req.getBrokerQuoteYn()) && homeData.getProductId() == 63) {
                            String jasperSaveLocation = policyReportPath.replaceAll("PolicyReport", "JsonFile") + homeData.getQuoteNo().replaceAll("[\\/:*?\"<>|]*", "");
                            res = getJasperPdfFile("/report/jasper/HomePremierQuotation.jrxml", jasperSaveLocation, input);
                        } else {
                            String JasperName = "MotorPrivate";
                            if ("100019".equalsIgnoreCase(homeData.getCompanyId())) {
                                JasperName = "UgandaMotorSchedule";
                            } else if (Arrays.asList("100046", "100047", "100048", "100049", "100050").contains(homeData.getCompanyId())) {
                                JasperName = "motor_schedule_format_new";
                            }
                            String jasperSaveLocation = policyReportPath.replaceAll("PolicyReport", "JsonFile") + homeData.getQuoteNo().replaceAll("[\\/:*?\"<>|]*", "");
                            if ((Arrays.asList("100046", "100047","100048", "100049").contains(homeData.getCompanyId()))) {
                       		 Map<String, Object> input2 = new HashMap<String, Object>();
                       		 viewAllReq req1 = new viewAllReq();
                                req1.setQuoteNo(homeData.getQuoteNo());
                                OverAllResForView maRes = viewAllWithLableServiceImpl.getViewAllData(homeData.getQuoteNo());
                                ObjectMapper objectMapper = new ObjectMapper();
                                String jsonString = null;
                               if(maRes != null) {
                               	Map<String,Object> jsMap = new HashMap<String,Object>();
                                   jsMap.put("Result", maRes);
                                   input2.put("policyNo", maRes.getPolicyNo());
                                   input2.put("attachMents", maRes.getAttachment());
                                   jsonString = objectMapper.writeValueAsString(jsMap);
                               }
                                if (System.getProperty("os.name").toLowerCase().contains("windows")) {
                                    input2.put("pvSubReportPath", externalJasperPath);
                                    input2.put("pvImagepath", externalImagePath);
                                } else {
                                    input2.put("pvSubReportPath", externalJasperPath);
                                    input2.put("pvImagepath", externalImagePath);
                                }
                              /* input2.put("ProductSectionMaster", jsondata.getProductSectionMaster());
                               input2.put("SelectedSectionList", jsondata.getLocationDetails().get(0).getSectionDetails());
                               input2.put("attachMents", jsondata.getAttachments());*/
                               String obj1[] = new String[4];
//                               obj[0] = config.getJasperFilePath().replaceAll("%20", " ") + "report/jasper/sub_exp_nambia_non_motor.jrxml";
//                               obj[1] = config.getJasperFilePath().replaceAll("%20", " ") + "report/jasper/NonMotorRiskDetailSub.jrxml";
//                               obj[2] = config.getJasperFilePath().replaceAll("%20", " ") + "report/jasper/sub_exp_nambia_non_motor_1.jrxml";

								
                               obj1[0] = externalJasperPath + "sub_Dynamic_motor_phoenix.jrxml";
                               obj1[1] = externalJasperPath + "NonMotorRiskDetailSub.jrxml";
                               obj1[2] = externalJasperPath + "sub_Dynamic_motor_phoenix1.jrxml";
                               obj1[3] = externalJasperPath + "sub_Dynamic_motor_LSCoverInformation.jrxml";

                               
                               /*obj[0] = config.getJasperFilePath().replaceAll("%20", " ")+"report/jasper/Sub_Main_Report.jrxml";  // name changes as PhoenixSubSchedule
								obj[1] = config.getJasperFilePath().replaceAll("%20", " ")+"report/jasper/Sub_Main_Report_2.jrxml";
								obj[2] = config.getJasperFilePath().replaceAll("%20", " ")+"report/jasper/Sub_Main_Report_3.jrxml";*/

                               //obj[1] = config.getJasperFilePath().replaceAll("%20", " ")+"report/jasper/NonMotorContent.jrxml";	// for linux system
								/*obj[1] = config.getJasperFilePath().replaceAll("%20", " ")+"report/jasper/SectionDetails.jrxml";
								obj[2] = config.getJasperFilePath().replaceAll("%20", " ")+"report/jasper/DomesticConditions.jrxml";*/
                               for (String s : obj1) {
                                   String jrxml_path = s.replace(".jasper", ".jrxml");
                                   String path;
                                   if (System.getProperty("os.name").toLowerCase().contains("windows")) {
                                       path = JasperCompileManager.compileReportToFile(jrxml_path);
                                   } else {
                                       path = JasperCompileManager.compileReportToFile("/" + jrxml_path);
                                   }
                                   System.out.println("Jasper compileToReport path" + path);
                               }
                               String reportName = jasperCustomeImple.getPDFcount(homeData.getQuoteNo());
                               input2.put("pvStatus", reportName);
                               res = getCommonJasperPdfFileByJson("/report/jasper/Dynamic_motor_phoenix.jrxml", jasperSaveLocation, jsonString, input2, "- Dynamic_motor_phoenix .json");
                       	}
                            else
                            {
                            MotorPrivateRes motPrivateRes = jasperCustomeImple.getMotorPrivate(homeData.getPolicyNo(), homeData.getQuoteNo(), req.getVehicleId());
                            String JsonString = gson.toJson(motPrivateRes);
                            jasperSaveLocation = policyReportPath.replaceAll("PolicyReport", "JsonFile") + homeData.getQuoteNo().replaceAll("[\\/:*?\"<>|]*", "");
                            res = getCommonJasperPdfFileByJson("/report/jasper/" + JasperName + ".jrxml", jasperSaveLocation, JsonString, input, "- " + JasperName + ".json");
                        }
                        }
                    } else if (product.getMotorYn().equalsIgnoreCase("H") && travelProductId.equals(homeData.getProductId().toString())) {
                        Map<String, Object> input2 = new HashMap<String, Object>();
                        input2.put("pvImagepath", externalImagePath);
                        if (System.getProperty("os.name").toLowerCase().contains("windows")) {
                            input2.put("pvSubReportPath", externalJasperPath);
                        } else {
                            input2.put("pvSubReportPath", externalJasperPath);
                        }
                        String obj = externalJasperPath + "EwayTravelSubReport.jrxml";
                        String jrxml_path = obj.replace(".jasper", ".jrxml");
                        String path = JasperCompileManager.compileReportToFile(jrxml_path);
                        System.out.println("Jasper compileToReport path" + path);
                        TravelReportRes travelRes = jasperCustomeImple.getTravelReport(homeData.getPolicyNo());
                        String jsonString = gson.toJson(travelRes);
                        String jasperSaveLocation = policyReportPath.replaceAll("PolicyReport", "JsonFile") + homeData.getPolicyNo().replaceAll("[\\/:*?\"<>|]*", "");
                        res = getCommonJasperPdfFileByJson("/report/jasper/EwayTravelReport.jrxml", jasperSaveLocation, jsonString, input2, "- TravelReport.json");
                    } else if (product.getMotorYn().equalsIgnoreCase("M") && !"46".equalsIgnoreCase(homeData.getProductId().toString())) {
                        String jasperSaveLocation = policyReportPath.replaceAll("PolicyReport", "JsonFile") + homeData.getPolicyNo().replaceAll("[\\/:*?\"<>|]*", "");
                        if (homeData.getEndtCount() != 0 && !homeData.getPolicyNo().equalsIgnoreCase(homeData.getOriginalPolicyNo()) && "E".equalsIgnoreCase(req.getEndorsementType()) && !(Arrays.asList("100046", "100047",
                                "100048", "100049", "100050").contains(homeData.getCompanyId()))) {
                            Map<String, Object> MotorEndorsementScheduleRes = jasperCustomeImple.getMotorEndorsementSchedule(homeData.getPolicyNo());
                            String jsonString = gson.toJson(MotorEndorsementScheduleRes);
                            res = getCommonJasperPdfFileByJson("/report/jasper/MotorEndorsementSchedule.jrxml", jasperSaveLocation, jsonString, input, "- MotorEndorsementSchedule.json");
                        } else if ("100004".equalsIgnoreCase(homeData.getCompanyId()) || (Arrays.asList("100046", "100047",
                                "100048", "100049", "100050").contains(homeData.getCompanyId()) && "Y".equalsIgnoreCase(req.getCertificateYn()))) { // MADISON MOTOR
                            if ("100048".equalsIgnoreCase(homeData.getCompanyId())) {
                                MotorPrivateRes motPrivateRes = jasperCustomeImple.getMotorPrivate(homeData.getPolicyNo(), "", req.getVehicleId());
                                String JsonString = gson.toJson(motPrivateRes);
                                res = getCommonJasperPdfFileByJson("/report/jasper/motor_certif_mocambique.jrxml", jasperSaveLocation, JsonString, input, "- " + "motor_certif_mocambique.json");
                            } else {
								/*if(System.getProperty("os.name").toLowerCase().contains("windows")) {
									input.put("pvSubReportPath",config.getJasperFilePath().replaceAll("%20", " ")+ "report/jasper/");
								}else {
									input.put("pvSubReportPath","/"+config.getJasperFilePath().replaceAll("%20", " ")+ "report/jasper/");
								}*/
                                System.out.print("input ==> " + input.toString());
                                List<Map<String, Object>> MadisonMotorSchedule = jasperCustomeImple.getMadisonMotorSchedule(homeData.getPolicyNo(), req.getDiskNo(),homeData);
                                String jsonString = gson.toJson(MadisonMotorSchedule);
								/*String obj[] =new String[1];
								if(System.getProperty("os.name").toLowerCase().contains("windows")) {
									obj[0] = config.getJasperFilePath().replaceAll("%20", " ")+"report/jasper/EwayMadisonMotorConditions.jrxml";
								}else {
									obj[0] = "/"+config.getJasperFilePath().replaceAll("%20", " ")+"report/jasper/EwayMadisonMotorConditions.jrxml";
								}
								for(String s :obj) {
									String jrxml_path=s.replace(".jasper", ".jrxml");
									String path = JasperCompileManager.compileReportToFile(jrxml_path);
									System.out.println("Jasper compileToReport path" +path);
								}*/
                                res = getCommonJasperPdfFileByJson("/report/jasper/EwayMadisonMotorSchedule.jrxml", jasperSaveLocation, jsonString, input, "- EwayMadisonMotorSchedule.json");
                            }
                        } else {
                        	if ( (Arrays.asList("100046", "100047","100048", "100049").contains(homeData.getCompanyId()))) {
                        		 Map<String, Object> input2 = new HashMap<String, Object>();
                        		 viewAllReq req1 = new viewAllReq();
                                 req1.setQuoteNo(homeData.getQuoteNo());
                                 OverAllResForView maRes = viewAllWithLableServiceImpl.getViewAllData(homeData.getQuoteNo());
                                 ObjectMapper objectMapper = new ObjectMapper();
                                 String jsonString = null;
                                if(maRes != null) {
                                	Map<String,Object> jsMap = new HashMap<String,Object>();
                                    jsMap.put("Result", maRes);
                                    input2.put("policyNo", maRes.getPolicyNo());
                                    input2.put("attachMents", maRes.getAttachment());
                                    jsonString = objectMapper.writeValueAsString(jsMap);
                                }
                                 if (System.getProperty("os.name").toLowerCase().contains("windows")) {
                                     input2.put("pvSubReportPath", externalJasperPath);
                                     input2.put("pvImagepath", externalImagePath);
                                 } else {
                                     input2.put("pvSubReportPath", externalJasperPath);
                                     input2.put("pvImagepath", externalImagePath);
                                 }
                               /* input2.put("ProductSectionMaster", jsondata.getProductSectionMaster());
                                input2.put("SelectedSectionList", jsondata.getLocationDetails().get(0).getSectionDetails());
                                input2.put("attachMents", jsondata.getAttachments());*/
                                String obj1[] = new String[4];
//                                obj[0] = config.getJasperFilePath().replaceAll("%20", " ") + "report/jasper/sub_exp_nambia_non_motor.jrxml";
//                                obj[1] = config.getJasperFilePath().replaceAll("%20", " ") + "report/jasper/NonMotorRiskDetailSub.jrxml";
//                                obj[2] = config.getJasperFilePath().replaceAll("%20", " ") + "report/jasper/sub_exp_nambia_non_motor_1.jrxml";

								
                                obj1[0] = externalJasperPath + "sub_Dynamic_motor_phoenix.jrxml";
                                obj1[1] = externalJasperPath + "NonMotorRiskDetailSub.jrxml";
                                obj1[2] = externalJasperPath + "sub_Dynamic_motor_phoenix1.jrxml";
                                obj1[3] = externalJasperPath + "sub_Dynamic_motor_LSCoverInformation.jrxml";

                                
                                /*obj[0] = config.getJasperFilePath().replaceAll("%20", " ")+"report/jasper/Sub_Main_Report.jrxml";  // name changes as PhoenixSubSchedule
								obj[1] = config.getJasperFilePath().replaceAll("%20", " ")+"report/jasper/Sub_Main_Report_2.jrxml";
								obj[2] = config.getJasperFilePath().replaceAll("%20", " ")+"report/jasper/Sub_Main_Report_3.jrxml";*/

                                //obj[1] = config.getJasperFilePath().replaceAll("%20", " ")+"report/jasper/NonMotorContent.jrxml";	// for linux system
								/*obj[1] = config.getJasperFilePath().replaceAll("%20", " ")+"report/jasper/SectionDetails.jrxml";
								obj[2] = config.getJasperFilePath().replaceAll("%20", " ")+"report/jasper/DomesticConditions.jrxml";*/
                                for (String s : obj1) {
                                    String jrxml_path = s.replace(".jasper", ".jrxml");
                                    String path;
                                    if (System.getProperty("os.name").toLowerCase().contains("windows")) {
                                        path = JasperCompileManager.compileReportToFile(jrxml_path);
                                    } else {
                                        path = JasperCompileManager.compileReportToFile("/" + jrxml_path);
                                    }
                                    System.out.println("Jasper compileToReport path" + path);
                                }
                                String reportName = jasperCustomeImple.getPDFcount(homeData.getQuoteNo());
                                input2.put("pvStatus", reportName);
                                res = getCommonJasperPdfFileByJson("/report/jasper/Dynamic_motor_phoenix.jrxml", jasperSaveLocation, jsonString, input2, "- Dynamic_motor_phoenix .json");
                        	}
                        	else {
                        	
                            MotorPrivateRes motPrivateRes = jasperCustomeImple.getMotorPrivate(homeData.getPolicyNo(), homeData.getQuoteNo(), req.getVehicleId());
                            String JsonString = gson.toJson(motPrivateRes);
                            String JasperName = "MotorPrivate";
                            if ("100019".equalsIgnoreCase(homeData.getCompanyId())) {        // UIA
                                if ("Y".equalsIgnoreCase(req.getStrickerYn())) {
                                    JsonString = gson.toJson(motPrivateRes.getVehicleDetails());
                                    JasperName = "Schedule_UIA";
                                } else {
                                    JsonString = gson.toJson(motPrivateRes);
                                    if ("1".equalsIgnoreCase(motPrivateRes.getVehicleDetails().get(0).getPolicyTypeId())) {
                                        input.put("attachMents", motPrivateRes.getAttachmentList());
                                        input.put("policyNo", motPrivateRes.getPolicyNo());
                                    }
                                    input.put("attachMents", motPrivateRes.getAttachmentList());
                                    JasperName = "UgandaMotorSchedule";
                                }
                                
                            } else {
                                input.put("attachMents", motPrivateRes.getAttachmentList());
                                input.put("policyNo", motPrivateRes.getPolicyNo());
                            }

                            if (Arrays.asList("100046", "100047", "100048", "100049", "100050").contains(homeData.getCompanyId())) {
                                JasperName = "motor_schedule_format_new";
                            }                           
                           
                            //res = getCommonJasperPdfFileByJson("/report/jasper/" + JasperName + ".jrxml", jasperSaveLocation, JsonString, input, "- " + JasperName + ".json");
                            //res.setJsonString(JsonString);                            
                            ObjectMapper objectMapper = new ObjectMapper();

                            JsonNode jsonNode = objectMapper.readTree(JsonString);

                            res.setJsonString(jsonNode);                           
                            
                        }
                    }
                    } else if (product.getMotorYn().equalsIgnoreCase("A") && "42".equalsIgnoreCase(homeData.getProductId().toString())) {
                        Map<String, Object> input2 = new HashMap<>();
                        input2.put("pvImagepath", externalImagePath);
                        Map<String, Object> cyberInsurance = jasperCustomeImple.getCyberInsurance(homeData.getPolicyNo());
                        String jsonString = gson.toJson(cyberInsurance);
                        String jasperSaveLocation = policyReportPath.replaceAll("PolicyReport", "JsonFile") + homeData.getPolicyNo().replaceAll("[\\/:*?\"<>|]*", "");
                        res = getCommonJasperPdfFileByJson("/report/jasper/CyberInsurance.jrxml", jasperSaveLocation, jsonString, input2, "- CyberInsurance.json");
                    } else if (product.getMotorYn().equalsIgnoreCase("M") && "46".equalsIgnoreCase(homeData.getProductId().toString())) {
                        Map<String, Object> map = new HashMap<String, Object>();
                        map.put("pvImagepath", externalImagePath);
                        String jasperSaveLocation = policyReportPath.replaceAll("PolicyReport", "JsonFile") + homeData.getPolicyNo().replaceAll("[\\/:*?\"<>|]*", "");
                        List<MotorCoverNoteRes> MotorCoverNote = jasperCustomeImple.getEwayMotorCoverNote(homeData.getPolicyNo(), req.getVehicleId(), "");
                        String jsonString = gson.toJson(MotorCoverNote);
                        res = getCommonJasperPdfFileByJson("/report/jasper/EwayMotorCoverNote.jrxml", jasperSaveLocation, jsonString, map, "- MotorCoveNote.json");
                    } else {
                        Map<String, Object> input2 = new HashMap<String, Object>();
                        input2.put("pvImagepath", externalImagePath);
                        if (System.getProperty("os.name").toLowerCase().contains("windows")) {
                            input2.put("pvSubReportPath", externalJasperPath);
                        } else {
                            input2.put("pvSubReportPath", externalJasperPath);
                        }
                        String jasperSaveLocation = policyReportPath.replaceAll("PolicyReport", "JsonFile") + (StringUtils.isBlank(homeData.getPolicyNo()) ? homeData.getQuoteNo().replaceAll("[\\/:*?\"<>|]*", "")
                                : homeData.getPolicyNo().replaceAll("[\\/:*?\"<>|]*", ""));
//                        if (homeData.getProductId() == 19) {
//                            Map<String, Object> CorporateSchedule = jasperCustomeImple.getCorporatePlusSchedule(homeData.getQuoteNo());
//                            String jsonString = gson.toJson(CorporateSchedule);
//                            String obj[] = new String[1];
//                            if (System.getProperty("os.name").toLowerCase().contains("windows")) {
//                                obj[0] = externalJasperPath + "CorporatePlusCoverageDetails.jrxml";
//                            } else {
//                                obj[0] = externalJasperPath + "CorporatePlusCoverageDetails.jrxml";
//                            }
//
//
//                            for (String s : obj) {
//                                String jrxml_path = s.replace(".jasper", ".jrxml");
//                                String path = JasperCompileManager.compileReportToFile(jrxml_path);
//                                System.out.println("Jasper compileToReport path" + path);
//                            }
//                            res = getCommonJasperPdfFileByJson("/report/jasper/CorporatePlus.jrxml", jasperSaveLocation, jsonString, input2, "- CorporatePlus.json");
//                        }
//                        else if (homeData.getProductId() == 59) {
//                            Map<String, Object> EwaySchedule = jasperCustomeImple.getEwaySchedule(homeData.getQuoteNo());
//                            String jsonString = gson.toJson(EwaySchedule);
//                            String obj[] = new String[1];
//                            obj[0] = externalJasperPath + "CoverageDetails.jrxml";
//                            //obj[1] = config.getJasperFilePath().replaceAll("%20", " ")+"report/jasper/NonMotorContent.jrxml";	// for linux system
//                            //obj[1] = config.getJasperFilePath().replaceAll("%20", " ")+"report/jasper/SectionDetails.jrxml";
//                            //obj[2] = config.getJasperFilePath().replaceAll("%20", " ")+"report/jasper/DomesticConditions.jrxml";
//                            for (String s : obj) {
//                                String jrxml_path = s.replace(".jasper", ".jrxml");
//                                String path = JasperCompileManager.compileReportToFile(jrxml_path);
//                                System.out.println("Jasper compileToReport path" + path);
//                            }
//                            String brokerYn = req.getBrokerQuoteYn();
//                            if (brokerYn != null && brokerYn.trim().equalsIgnoreCase("Y")) {
//                                res = getCommonJasperPdfFileByJson("/report/jasper/DomesticQuotation.jrxml", jasperSaveLocation, jsonString, input2, "- EwaySchedule.json");
//                            } else {
//                                res = getCommonJasperPdfFileByJson("/report/jasper/EwaySchedule.jrxml", jasperSaveLocation, jsonString, input2, "- EwaySchedule.json");
//                            }
//                        } 
                        
                //        else {
                            if ("100004".equalsIgnoreCase(homeData.getCompanyId())) {
                                Map<String, Object> EwaySchedule = jasperCustomeImple.getEwaySchedule(homeData.getQuoteNo());
                                String jsonString = gson.toJson(EwaySchedule);
                                String obj[] = new String[1];
                                obj[0] = config.getJasperFilePath().replaceAll("%20", " ") + "report/jasper/CoverageDetails.jrxml";
                                for (String s : obj) {
                                    String jrxml_path = s.replace(".jasper", ".jrxml");
                                    JasperDesign design = JRXmlLoader.load(new File(jrxml_path));
                                    JRDesignSection designSection = (JRDesignSection) design.getDetailSection();
                                    designSection.removeBand(0);
                                    JasperCompileManager.compileReportToFile(design, config.getJasperFilePath().replaceAll("%20", " ") + "report/jasper/CoverageDetails.jasper");
                                }
                                input2.put("attachMents", EwaySchedule.get("attachMents"));
                                input2.put("policyNo", EwaySchedule.get("policyNo"));
                                res = getCommonJasperPdfFileByJson("/report/jasper/MadisonSchedule.jrxml", jasperSaveLocation, jsonString, input2, "- MadisonSchedule.json");
                            } else if ("100020".equalsIgnoreCase(homeData.getCompanyId())) {
                                Map<String, Object> EwaySchedule = jasperCustomeImple.getEwaySchedule(homeData.getQuoteNo());
                                String jsonString = gson.toJson(EwaySchedule);
								/*String obj[] =new String[1];
								obj[0] = config.getJasperFilePath().replaceAll("%20", " ")+"report/jasper/CoverageDetails.jrxml";
								for(String s :obj) {
									String jrxml_path=s.replace(".jasper", ".jrxml");
									JasperDesign design = JRXmlLoader.load(new File(jrxml_path));
									JRDesignSection designSection = (JRDesignSection) design.getDetailSection();
									designSection.removeBand(0);
					                JasperCompileManager.compileReportToFile(design, config.getJasperFilePath().replaceAll("%20", " ")+"report/jasper/CoverageDetails.jasper");
								}*/
                                input2.put("attachMents", EwaySchedule.get("attachMents"));
                                input2.put("policyNo", EwaySchedule.get("policyNo"));
                                input2.put("pvImagepath", externalImagePath);
                                //res = getCommonJasperPdfFileByJson("/report/jasper/EwayKenyaSchedule.jrxml", jasperSaveLocation, jsonString, input2, "- KenyaSchedule.json");
                                if ("Y".equalsIgnoreCase(req.getBrokerQuoteYn())) {


                                    if (homeData.getProductId().equals(13)) {

                                        res = getCommonJasperPdfFileByJson("/report/jasper/EwayKenyaQuotation.jrxml", jasperSaveLocation, jsonString, input2, "- KenyaQuotation.json");

                                    } else if (homeData.getProductId().equals(87)) {

                                        res = getCommonJasperPdfFileByJson("/report/jasper/ProfessionalIndemnityQuotation.jrxml", jasperSaveLocation, jsonString, input2, "- KenyaQuotation.json");
                                    } else {

                                        res = getCommonJasperPdfFileByJson("/report/jasper/DomesticQuotation.jrxml", jasperSaveLocation, jsonString, input2, "- KenyaQuotation.json");
                                    }
                                } else {

                                    if (homeData.getProductId().equals(13)) {

                                        res = getCommonJasperPdfFileByJson("/report/jasper/EwayKenyaSchedule.jrxml", jasperSaveLocation, jsonString, input2, "- KenyaSchedule.json");
                                    } else {
                                        input2.put("attachMents", EwaySchedule.get("attachMents"));
                                        res = getCommonJasperPdfFileByJson("/report/jasper/Professional_Indemnity.jrxml", jasperSaveLocation, jsonString, input2, "- KenyaQuotation.json");

                                    }
                                }
                            } else {
                                if ("100050".equalsIgnoreCase(homeData.getCompanyId())) {
                                    Map<String, Object> EwaySchedule = jasperCustomeImple.getEwaySchedule(homeData.getQuoteNo());
                                    String jsonString = gson.toJson(EwaySchedule);
                                    if (System.getProperty("os.name").toLowerCase().contains("windows")) {
                                        input2.put("pvSubReportPath", externalJasperPath);
                                    } else {
                                        input2.put("pvSubReportPath", externalJasperPath);
                                    }
                                    String obj[] = new String[3];
                                    obj[0] = externalJasperPath + "Sub_phoenix_Main_Report.jrxml";
                                    obj[1] = externalJasperPath + "Sub_phoenix_Main_Report_2.jrxml";
                                    obj[2] = externalJasperPath + "Sub_phoenix_Main_Report_3.jrxml";

									/*obj[0] = config.getJasperFilePath().replaceAll("%20", " ")+"report/jasper/Sub_Main_Report.jrxml";  // name changes as PhoenixSubSchedule
									obj[1] = config.getJasperFilePath().replaceAll("%20", " ")+"report/jasper/Sub_Main_Report_2.jrxml";
									obj[2] = config.getJasperFilePath().replaceAll("%20", " ")+"report/jasper/Sub_Main_Report_3.jrxml";*/

                                    //obj[1] = config.getJasperFilePath().replaceAll("%20", " ")+"report/jasper/NonMotorContent.jrxml";	// for linux system
									/*obj[1] = config.getJasperFilePath().replaceAll("%20", " ")+"report/jasper/SectionDetails.jrxml";
									obj[2] = config.getJasperFilePath().replaceAll("%20", " ")+"report/jasper/DomesticConditions.jrxml";*/
                                    for (String s : obj) {
                                        String jrxml_path = s.replace(".jasper", ".jrxml");
                                        String path;
                                        if (System.getProperty("os.name").toLowerCase().contains("windows")) {
                                            path = JasperCompileManager.compileReportToFile(jrxml_path);
                                        } else {
                                            path = JasperCompileManager.compileReportToFile("/" + jrxml_path);
                                        }
                                        System.out.println("Jasper compileToReport path" + path);
                                    }
                                    String reportName = jasperCustomeImple.getPDFcount(homeData.getQuoteNo());
                                    input2.put("pvStatus", reportName);
                                    // res = getCommonJasperPdfFileByJson("/report/jasper/Main_Report.jrxml", jasperSaveLocation, jsonString, input2, "- Main_Report.json"); // name changes as PhoenixSchedule
                                    res = getCommonJasperPdfFileByJson("/report/jasper/Non_motor_schedule_format_new.jrxml", jasperSaveLocation, jsonString, input2, "- Non_motor_schedule_format_new.json");
                                } else if (Arrays.asList("100046", "100047", "100048", "100049").contains(homeData.getCompanyId())) {
                                    /*CommonRes data = new CommonRes();
                                    ViewReportDetailsRes jsondata = viewReportDetails(homeData.getQuoteNo());
                                    data.setCommonResponse(jsondata);
                                    data.setIsError(false);
                                    data.setErrorMessage(Collections.emptyList());
                                    data.setMessage("Success");*/
                                    viewAllReq req1 = new viewAllReq();
                                    req1.setQuoteNo(homeData.getQuoteNo());
                                    OverAllResForView maRes = viewAllWithLableServiceImpl.getViewAllData(homeData.getQuoteNo());
                                    ObjectMapper objectMapper = new ObjectMapper();
                                    String jsonString = null;
                                   if(maRes != null) {
                                   	Map<String,Object> jsMap = new HashMap<String,Object>();
                                       jsMap.put("Result", maRes);
                                       input2.put("policyNo", maRes.getPolicyNo());
                                       input2.put("attachMents", maRes.getAttachment());
                                       jsonString = objectMapper.writeValueAsString(jsMap);
                                   }
                                    if (System.getProperty("os.name").toLowerCase().contains("windows")) {
                                        input2.put("pvSubReportPath", externalJasperPath);
                                    } else {
                                        input2.put("pvSubReportPath", externalJasperPath);
                                    }
                                   /* input2.put("ProductSectionMaster", jsondata.getProductSectionMaster());
                                    input2.put("SelectedSectionList", jsondata.getLocationDetails().get(0).getSectionDetails());
                                    input2.put("attachMents", jsondata.getAttachments());*/
                                    String obj[] = new String[4];
//                                    obj[0] = config.getJasperFilePath().replaceAll("%20", " ") + "report/jasper/sub_exp_nambia_non_motor.jrxml";
//                                    obj[1] = config.getJasperFilePath().replaceAll("%20", " ") + "report/jasper/NonMotorRiskDetailSub.jrxml";
//                                    obj[2] = config.getJasperFilePath().replaceAll("%20", " ") + "report/jasper/sub_exp_nambia_non_motor_1.jrxml";

									
                                    obj[0] = externalJasperPath + "sub_exp_nambia_non_motor.jrxml";
                                    obj[1] = externalJasperPath + "NonMotorRiskDetailSub.jrxml";
                                    obj[2] = externalJasperPath + "sub_exp_nambia_non_motor_1.jrxml";
                                    obj[3] = externalJasperPath + "sub_exp_nambia_LSCoverInformation.jrxml";

                                    
                                    /*obj[0] = config.getJasperFilePath().replaceAll("%20", " ")+"report/jasper/Sub_Main_Report.jrxml";  // name changes as PhoenixSubSchedule
									obj[1] = config.getJasperFilePath().replaceAll("%20", " ")+"report/jasper/Sub_Main_Report_2.jrxml";
									obj[2] = config.getJasperFilePath().replaceAll("%20", " ")+"report/jasper/Sub_Main_Report_3.jrxml";*/

                                    //obj[1] = config.getJasperFilePath().replaceAll("%20", " ")+"report/jasper/NonMotorContent.jrxml";	// for linux system
									/*obj[1] = config.getJasperFilePath().replaceAll("%20", " ")+"report/jasper/SectionDetails.jrxml";
									obj[2] = config.getJasperFilePath().replaceAll("%20", " ")+"report/jasper/DomesticConditions.jrxml";*/
                                    for (String s : obj) {
                                        String jrxml_path = s.replace(".jasper", ".jrxml");
                                        String path;
                                        if (System.getProperty("os.name").toLowerCase().contains("windows")) {
                                            path = JasperCompileManager.compileReportToFile(jrxml_path);
                                        } else {
                                            path = JasperCompileManager.compileReportToFile("/" + jrxml_path);
                                        }
                                        System.out.println("Jasper compileToReport path" + path);
                                    }
                                    String reportName = jasperCustomeImple.getPDFcount(homeData.getQuoteNo());
                                    input2.put("pvStatus", reportName);
                                    res = getCommonJasperPdfFileByJson("/report/jasper/exp_nambia_non_motor.jrxml", jasperSaveLocation, jsonString, input2, "- exp_nambia_non_motor.json");
                                }else if("100002".equalsIgnoreCase(homeData.getCompanyId())) {
                                	input2.put("IMAGE_DIR", Imagepath);
                                	input2.put("SUBREPORT_DIR", config.getJasperFilePath().replaceAll("%20", " ")+ "report/jasper/");
                                	 String obj[] = new String[3];
                                	 obj[0] = config.getJasperFilePath().replaceAll("%20", " ") + "report/jasper/LocationsSubreport.jrxml";
                                     obj[1] = config.getJasperFilePath().replaceAll("%20", " ") + "report/jasper/LocationsSubreport1.jrxml";
                                     obj[2] = config.getJasperFilePath().replaceAll("%20", " ") + "report/jasper/LSCoverInformation.jrxml";
                                     for (String s : obj) {
                                         String jrxml_path = s.replace(".jasper", ".jrxml");
                                         String path;
                                         if (System.getProperty("os.name").toLowerCase().contains("windows")) {
                                             path = JasperCompileManager.compileReportToFile(jrxml_path);
                                         } else {
                                             path = JasperCompileManager.compileReportToFile("/" + jrxml_path);
                                         }
                                         System.out.println("Jasper compileToReport path" + path);
                                         viewAllReq req1 = new viewAllReq();
                                         req1.setQuoteNo(homeData.getQuoteNo());
                                         OverAllResForView maRes = viewAllWithLableServiceImpl.getViewAllData(homeData.getQuoteNo());
                                        if(maRes != null) {
                                        	Map<String,Object> jsMap = new HashMap<String,Object>();
                                            jsMap.put("Result", maRes);
                                            input2.put("policyNo", maRes.getPolicyNo());
                                            input2.put("attachMents", maRes.getAttachment());
                                            ObjectMapper objectMapper = new ObjectMapper();
                                            String jsonString = objectMapper.writeValueAsString(jsMap);
                                            res = getCommonJasperPdfFileByJson("/report/jasper/CommonNMPolicySchedule.jrxml", jasperSaveLocation, jsonString, input2, "- Taz_non_motor.json");
                                        }
                                     }
                                }else if("100019".equalsIgnoreCase(homeData.getCompanyId())) {
                                	input2.put("IMAGE_DIR", Imagepath);
                                	input2.put("SUBREPORT_DIR", config.getJasperFilePath().replaceAll("%20", " ")+ "report/jasper/");
                                	 String obj[] = new String[2];
                                	 obj[0] = config.getJasperFilePath().replaceAll("%20", " ") + "report/jasper/LocationsSubreport.jrxml";
                                     obj[1] = config.getJasperFilePath().replaceAll("%20", " ") + "report/jasper/LocationsSubreport1.jrxml";
                                     for (String s : obj) {
                                         String jrxml_path = s.replace(".jasper", ".jrxml");
                                         String path;
                                         if (System.getProperty("os.name").toLowerCase().contains("windows")) {
                                             path = JasperCompileManager.compileReportToFile(jrxml_path);
                                         } else {
                                             path = JasperCompileManager.compileReportToFile("/" + jrxml_path);
                                         }
                                         System.out.println("Jasper compileToReport path" + path);
                                         viewAllReq req1 = new viewAllReq();
                                         req1.setQuoteNo(homeData.getQuoteNo());
                                         OverAllResForView maRes = viewAllWithLableServiceImpl.getViewAllData(homeData.getQuoteNo());
                                        if(maRes != null) {
                                        	Map<String,Object> jsMap = new HashMap<String,Object>();
                                            jsMap.put("Result", maRes);
                                            ObjectMapper objectMapper = new ObjectMapper();
                                            String jsonString = objectMapper.writeValueAsString(jsMap);
                                            res = getCommonJasperPdfFileByJson("/report/jasper/CommonNMPolicySchedule.jrxml", jasperSaveLocation, jsonString, input2, "- Taz_non_motor.json");
                                        }
                                     }
                                }
                                else {
                                    Map<String, Object> CorporateSchedule = jasperCustomeImple.getCorporatePlusSchedule(homeData.getQuoteNo());
                                    String jsonStr = gson.toJson(CorporateSchedule);
                                    String obj[] = new String[1];

                                    if (System.getProperty("os.name").toLowerCase().contains("windows")) {
                                        obj[0] = externalJasperPath + "CorporatePlusCoverageDetails.jrxml";
                                    } else {
                                        obj[0] = externalJasperPath + "CorporatePlusCoverageDetails.jrxml";
                                    }
                                    for (String s : obj) {
                                        String jrxml_path = s.replace(".jasper", ".jrxml");
                                        String path = JasperCompileManager.compileReportToFile(jrxml_path);
                                        System.out.println("Jasper compileToReport path" + path);
                                    }
                                    res = getCommonJasperPdfFileByJson("/report/jasper/CorporatePlus.jrxml", jasperSaveLocation, jsonStr, input2, "- CorporatePlus.json");
									/*String obj[] =new String[1];
									obj[0] = config.getJasperFilePath().replaceAll("%20", " ")+"report/jasper/CoverageDetails.jrxml";
									//obj[1] = config.getJasperFilePath().replaceAll("%20", " ")+"report/jasper/NonMotorContent.jrxml";	// for linux system
									/*obj[1] = config.getJasperFilePath().replaceAll("%20", " ")+"report/jasper/SectionDetails.jrxml";
									obj[2] = config.getJasperFilePath().replaceAll("%20", " ")+"report/jasper/DomesticConditions.jrxml";
									for(String s :obj) {
										String jrxml_path=s.replace(".jasper", ".jrxml");
										String path = JasperCompileManager.compileReportToFile(jrxml_path);
										System.out.println("Jasper compileToReport path" +path);
									}
									res = getCommonJasperPdfFileByJson("/report/jasper/EwaySchedule.jrxml", jasperSaveLocation, jsonString, input2, "- EwaySchedule.json");*/
                                }
                            }
             //           }

                    }
                }
            } else {
                res.setErrorMessage(errors);
                res.setPdfoutfile(null);
                res.setPdfoutfilepath(null);
                }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    private JasperDocumentRes getJasperPdfFile(String jasperPath, String filePath, Map<String, Object> input) {
        JasperDocumentRes res = new JasperDocumentRes();
        Connection connection = null;
        try {
            connection = config.getDataSourceForJasper().getConnection();
            InputStream inputStream = this.getClass().getResourceAsStream(jasperPath);
            JasperReport jasperReport = JasperCompileManager.compileReport(inputStream);
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, input, connection);
            // JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport,input);

            /* servletRequest.getRealPath(getPdfOutFilePath) */
            ;
            // filePath=filePath.replaceAll("%20", " ");
            System.out.println("filePath name is ====> " + filePath);
            JasperExportManager.exportReportToPdfFile(jasperPrint, filePath);
            // res.setPdfoutfilepath(commonPath+"/"+getPdfOutFilePath);

            GetFileFromPath path = new GetFileFromPath(filePath);
            res.setPdfoutfile(path.call().getImgUrl());
            res.setPdfoutfilepath(filePath);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (connection != null)
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
        }
        return res;
    }

    private JasperDocumentRes getJasperPdfFileFromJson(String jasperPath, String filePath, Map<String, Object> input, String jsonFile) {
        JasperDocumentRes res = new JasperDocumentRes();
        InputStream inputStream = null;
        try {

            File file = new File(policyReportPath.replaceAll("PolicyReport", "IllustrationFile") + jsonFile);

            JsonDataSource ds = new JsonDataSource(file);
            inputStream = this.getClass().getResourceAsStream(jasperPath);
            JasperReport jasperReport = JasperCompileManager.compileReport(inputStream);

            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, input, ds);
            System.out.println("filePath name is ====> " + filePath);
            JasperExportManager.exportReportToPdfFile(jasperPrint, filePath);
            GetFileFromPath path = new GetFileFromPath(filePath);
            res.setPdfoutfile(path.call().getImgUrl());
            res.setPdfoutfilepath(filePath);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null)
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
        return res;
    }

    @Override
    public JasperDocumentRes proposalform(JasperDocumentReq req) {
        JasperDocumentRes res = null;
        String getPdfOutFilePath = "";
        try {
            HomePositionMaster homeData = homeRepo.findByQuoteNo(req.getQuoteNo());
            CompanyProductMaster product = getCompanyProductMasterDropdown(homeData.getCompanyId(), homeData.getProductId().toString());

            Map<String, Object> input = new HashMap<String, Object>();
            input.put("QuoteNo", req.getQuoteNo());
            input.put("imagePath", config.getImagePath());

            // HomePositionMaster homeData = homeRepo.findByQuoteNo(req.getQuoteNo()) ;
            if (product.getMotorYn().equalsIgnoreCase("H") && travelProductId.equals(req.getProductId())) {
                if (null != input && input.size() > 0) {

                    // String directoryname=null ;
                    // File Save Path
                    String filePath = null;

                    // directoryname=req.getQuoteNo().replaceAll("[\\/:*?\"<>|]*", "");
                    filePath = config.getProposalPath() + "pdf";
                    getPdfOutFilePath = filePath + "/" + req.getQuoteNo() + ".pdf";

                    File theDir = new File(filePath);
                    if (!theDir.exists()) {
                        theDir.mkdirs();
                    }
                    res = getJasperPdfFile("/report/jasper/TravelReport.jrxml", getPdfOutFilePath, input);
                }
            } else if (product.getMotorYn().equalsIgnoreCase("M")) {
                // Temporary
                res = getJasperPdfFile("/report/jasper/MotorPrivate.jrxml", getPdfOutFilePath, input);
                String filePath = config.getPolicyPath() + "pdf/MOTOR PRIVATE.pdf";
                GetFileFromPath path = new GetFileFromPath(filePath);
                res.setPdfoutfile(path.call().getImgUrl());
                res.setPdfoutfilepath(filePath);
            } else {

                input.put("pvSubReportPath", externalJasperPath);
                res = getJasperPdfFile("/report/jasper/EwaySchedule.jrxml", getPdfOutFilePath, input);
                String filePath = config.getPolicyPath() + "pdf/EWAY SCHEDULE.pdf";
                GetFileFromPath path = new GetFileFromPath(filePath);
                res.setPdfoutfile(path.call().getImgUrl());
                res.setPdfoutfilepath(filePath);
            }


//				res = new JasperDocumentRes();
//				String filePath = config.getPolicyPath() + "pdf/PERSONAL PLUS.pdf";
//			    GetFileFromPath path = new GetFileFromPath(filePath);
//				res.setPdfoutfile(path.call().getImgUrl());
//				res.setPdfoutfilepath(filePath);

//			} else {
//				res = new JasperDocumentRes();
//				String filePath = config.getPolicyPath() + "pdf/GROUP PERSONAL ACCIDENT.pdf";
//				GetFileFromPath path = new GetFileFromPath(filePath);
//				res.setPdfoutfile(path.call().getImgUrl());
//				res.setPdfoutfilepath(filePath);
//			}
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
            product = list.size() > 0 ? list.get(0) : null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return product;
    }

    @Override
    public JasperDocumentRes policyreportform(JasperReportDocReq req) {
        JasperDocumentRes res = null;
        String getPdfOutFilePath = "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdf2 = new SimpleDateFormat("dd-MM-yyyy");
        try {
            CompanyProductMaster product = getCompanyProductMasterDropdown(req.getInsuranceId(), req.getProductId());
            List<BranchMaster> branchList = branchRepo.findTopByCompanyIdAndBranchCodeOrderByAmendIdDesc(req.getInsuranceId(), req.getBranchCode());
            String branchName = branchList.size() > 0 ? branchList.get(0).getBranchName() : req.getBranchCode();

            Map<String, Object> input = new HashMap<String, Object>();
            input.put("pvStartDate", sdf.format(req.getStartDate()));
            input.put("pvImagepath", externalImagePath);
            input.put("pvEndDate", sdf.format(req.getEndDate()));
            input.put("pvBranch", req.getBranchCode());
            input.put("pvLoginId", req.getLoginId());

            getPdfOutFilePath = policyReportPath + "pdf/" + req.getLoginId() + ":" + "Branch-" + branchName + "(" + sdf2.format(req.getStartDate()) + "To" + sdf2.format(req.getEndDate()) + ") Policy Report.pdf";

            if (product.getMotorYn().equalsIgnoreCase("H") && travelProductId.equals(req.getProductId())) {
                res = getJasperPdfFile("/report/jasper/EwayPremiumReport.jrxml", getPdfOutFilePath, input);


            } else if (product.getMotorYn().equalsIgnoreCase("M")) {

                res = getJasperPdfFile("/report/jasper/EwayPremiumReport.jrxml", getPdfOutFilePath, input);

            } else {

                res = getJasperPdfFile("/report/jasper/EwayPremiumReport.jrxml", getPdfOutFilePath, input);

            }


        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    @Override
    public JasperDocumentRes taxInvoice(String quoteNo) {
        JasperDocumentRes res = new JasperDocumentRes();
        HomePositionMaster homeData = homeRepo.findByQuoteNo(quoteNo);
        try {
            if (StringUtils.isNotBlank(homeData.getPolicyNo())) {
            	
            	if("100019".equalsIgnoreCase(homeData.getCompanyId()))
            	{
            		viewAllReq req = new viewAllReq();
            		req.setQuoteNo(quoteNo);
            		CommonRes debitNoteUganda = ugandaTax.generateDebitNoteUganda(req);
            		Map<String, Object> map = (Map<String, Object>) debitNoteUganda.getCommonResponse();
            		String fileName = map.get("FileName").toString();
            		String base64 = map.get("Base64").toString();
            		res.setPdfoutfile(base64);
            		res.setPdfoutfilepath(fileName);
            		return res;
            		}
            	
                Map<String, Object> map = new HashMap<String, Object>();
                log.info("OS Using ==> " + System.getProperty("os.name").toLowerCase());
                if (System.getProperty("os.name").toLowerCase().contains("windows")) {
                    map.put("pvImagepath", externalImagePath);
                } else {
                    map.put("pvImagepath", externalImagePath);
                }
                TaxInvoiceRes taxRes = jasperCustomeImple.getTaxInvoiceRes(homeData.getPolicyNo() , homeData);
                String JsonString = gson.toJson(taxRes);
                String jasperSaveLocation = policyReportPath.replaceAll("PolicyReport", "JsonFile") + homeData.getPolicyNo().replaceAll("[\\/:*?\"<>|]*", "");
                String jasperName = "";
                if (homeData.getCompanyId().equalsIgnoreCase("100004")) {
                    jasperName = "/report/jasper/EwayMadisonTaxInvoice.jrxml";
                } else if (Arrays.asList("100046", "100047", "100048", "100049", "100050").contains(homeData.getCompanyId())) {
                    jasperName = "/report/jasper/EwayPhoenixTaxInvoice.jrxml";
                } else {
                    jasperName = "/report/jasper/EwayTaxInvoice.jrxml";
                }
                res = getCommonJasperPdfFileByJson(jasperName, jasperSaveLocation, JsonString, map, "- TaxInvoice.json");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    @Override
    public JasperDocumentRes creditNote(String quoteNo) {
        JasperDocumentRes res = new JasperDocumentRes();
        HomePositionMaster homeData = homeRepo.findByQuoteNo(quoteNo);
        try {
            if (StringUtils.isNotBlank(homeData.getPolicyNo())) {
            	Map<String, Object> input = new HashMap<String, Object>();
                log.info("OS Using ==> " + System.getProperty("os.name").toLowerCase());
                if (System.getProperty("os.name").toLowerCase().contains("windows")) {
                    input.put("pvImagepath", externalImagePath);
                } else {
                    input.put("pvImagepath", externalImagePath);
                }
                CreditNoteRes creditRes = jasperCustomeImple.getCreditNoteRes(homeData.getPolicyNo(),homeData);
                String JsonString = gson.toJson(creditRes);
                String jasperSaveLocation = policyReportPath.replaceAll("PolicyReport", "JsonFile") + homeData.getPolicyNo().replaceAll("[\\/:*?\"<>|]*", "");
                String jasperName = "/report/jasper/EwayCreditNote.jrxml";
                if (Arrays.asList("100046", "100047", "100048", "100049", "100050").contains(homeData.getCompanyId())) {
                    jasperName = "/report/jasper/EwayPhoenixCreditNote.jrxml";
                }

                res = getCommonJasperPdfFileByJson(jasperName, jasperSaveLocation, JsonString, input, "- CreditNote.json");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    public CommonRes getPremiumReport(PremiumReportReq req) {
        log.info("Enter into PremiumReport ==> " + gson.toJson(req));
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PremiumReportRes preRes = new PremiumReportRes();
        CommonRes response = new CommonRes();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String fileName = "", prefix = "", companylogo = "";
        try {
            PortFolioGridReq req1 = new PortFolioGridReq();
            req1.setBranchCode(req.getBranchCode());
            req1.setBusinessType(req.getBusinessType());
            req1.setEndDate(sdf.parse(req.getEndDate()));
            req1.setStartDate(sdf.parse(req.getStartDate()));
            req1.setInsuranceId(req.getInsuranceId());
            req1.setProductId(req.getProductId());
            req1.setLoginId(req.getLoginId());
            req1.setShowAllDataYn("Y");
            req1.setUserType(req.getUserType());
            req1.setPortFolioYn(req.getPortFolioYn());

            List<PortfolioGridRes> result = gridServiceImpl.getAllPolicyGrid(req1);

            if (!result.isEmpty()) {
                result.stream().filter(f -> Arrays.asList("5", "46").contains(f.getProductId())).forEach(o -> {
                    List<MotorDataDetails> vehicleDetails = motorRepo.findByQuoteNoOrderByVehicleIdAsc(o.getQuoteNo())
                            .stream().filter(f -> !f.getStatus().equalsIgnoreCase("D")).collect(Collectors.toList());

                    List<MotorPrivateVehicleDetails> vehicleDetailsRes = new ArrayList<MotorPrivateVehicleDetails>();
                    vehicleDetails.forEach(k -> {
                        MotorPrivateVehicleDetails t = MotorPrivateVehicleDetails.builder()
                                .vehicleId(k.getVehicleId() == null ? "" : k.getVehicleId().toString())
                                .registrationNumber(k.getRegistrationNumber() == null ? "" : k.getRegistrationNumber().toString())
                                .vehicleMake(k.getVehicleMakeDesc() == null ? "" : k.getVehicleMakeDesc().toString())
                                .vehcileModel(k.getVehcileModelDesc() == null ? "" : k.getVehcileModelDesc().toString())
                                .vehicleTypeDesc(k.getVehicleTypeDesc() == null ? "" : k.getVehicleTypeDesc().toString())
                                .cubicCapacity(k.getCubicCapacity() == null ? "" : k.getCubicCapacity().toString())
                                .manufactureYear(k.getManufactureYear() == null ? "" : k.getManufactureYear().toString())
                                .seatingCapacity(k.getSeatingCapacity() == null ? null : k.getSeatingCapacity().toString())
                                .colorDesc(k.getColorDesc() == null ? "" : k.getColorDesc().toString())
                                .policyTypeDesc(k.getPolicyTypeDesc() == null ? "" : k.getPolicyTypeDesc().toString())
                                .policyTypeId(k.getPolicyType() == null ? "" : k.getPolicyType())
                                .windScreenSumInsuredLc(k.getWindScreenSumInsured() == null ? null : new BigDecimal(Double.parseDouble(k.getWindScreenSumInsured().toString())).toString())
                                .sumInsured(k.getSumInsured() == null ? null : new BigDecimal(Double.parseDouble(k.getSumInsured().toString())).toString())
                                .stickerNumber(jasperCustomeImple.getStrickerNo(k.getQuoteNo(), k.getVehicleId()))
                                .grossWeight(k.getGrossWeight() == null ? null : k.getGrossWeight().toString())
                                .insTypeDesc(k.getInsuranceTypeDesc() == null ? "" : k.getInsuranceTypeDesc())
                                .engineNumber(k.getEngineNumber() == null ? "" : k.getEngineNumber())
                                .tPPDIncreaseLimit(k.getTppdIncreaeLimit() == null ? null : new BigDecimal(Double.parseDouble(k.getTppdIncreaeLimit().toString())).toString())
                                .chassisNumber(k.getChassisNumber() == null ? "" : k.getChassisNumber())
                                .fuelType(k.getFuelTypeDesc() == null ? "" : k.getFuelTypeDesc())
                                .premium(new BigDecimal(Double.parseDouble(k.getOverallPremiumFc().toString())).toString())
                                .build();
                        vehicleDetailsRes.add(t);
                    });

                    o.setVehicleDetails(vehicleDetailsRes);
                });
            }

            List<Map<String, Object>> companyDetails = insuranceComMasRepo.getCompanyDetailsById(req.getInsuranceId());
            if (!companyDetails.isEmpty()) {
                companylogo = companyDetails.get(0).get("COMPANY_LOGO") == null ? "" : companyDetails.get(0).get("COMPANY_LOGO").toString();
            }

            String classpath = this.getClass().getClassLoader().getResource("").getPath();
            classpath = classpath.replaceAll("%20", " ");
            classpath = classpath.substring(1, classpath.length());

           // String imagepath = classpath + "report/images/" + companylogo; //windows system path

            Map<String, Object> map = new HashMap<String, Object>();
            map.put("pvImagepath", externalImagePath+companylogo);

			/*result.sort(Comparator
			        .comparing(PortfolioGridRes::getBranchName, String.CASE_INSENSITIVE_ORDER)
			        .thenComparing(PortfolioGridRes::getProductName, String.CASE_INSENSITIVE_ORDER)
			        .thenComparing(PortfolioGridRes::getBrokerName, String.CASE_INSENSITIVE_ORDER)
			);*/

            result.sort(
                    Comparator
                            .comparing(PortfolioGridRes::getBranchName,
                                    Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER))
                            .thenComparing(PortfolioGridRes::getProductName,
                                    Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER))
                            .thenComparing(PortfolioGridRes::getBrokerName,
                                    Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER))
            );

            String jsonString = gson.toJson(result);
            log.info("PremiumReport Response ==> " + jsonString);

            if ("Y".equalsIgnoreCase(req.getExcelYn())) {
                fileName = "PremiumRegister";
                prefix = "data:application/vnd.ms-excel;base64,";
                JsonDataSource dataSource = new JsonDataSource(new ByteArrayInputStream(jsonString.getBytes(StandardCharsets.UTF_8)));
                InputStream inputStream = this.getClass().getResourceAsStream("/report/jasper/EwayPremiumReportSql.jrxml");
                JasperReport jasperReport = JasperCompileManager.compileReport(inputStream);
                JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, map, dataSource);
                JRXlsxExporter exporter = new JRXlsxExporter();
                exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
                exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(output));

                SimpleXlsxReportConfiguration configuration = new SimpleXlsxReportConfiguration();
                configuration.setOnePagePerSheet(false);
                configuration.setDetectCellType(true);
                configuration.setCollapseRowSpan(false);

                exporter.setConfiguration(configuration);
                try {
                    exporter.exportReport();
                } catch (Exception e) {
                    log.info("Error in PremiumReport ==> " + e.getMessage());
                    e.printStackTrace();

                }
                log.info("PremiumReport Report Created");
            } else {
                fileName = "PremiumRegister";
                prefix = "data:application/pdf;base64,";
                JsonDataSource dataSource = new JsonDataSource(new ByteArrayInputStream(jsonString.getBytes(StandardCharsets.UTF_8)));
                InputStream inputStream = this.getClass().getResourceAsStream("/report/jasper/EwayPremiumReportSql.jrxml");
                JasperReport jasperReport = JasperCompileManager.compileReport(inputStream);
                JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, map, dataSource);
                JRPdfExporter pdfExporter = new JRPdfExporter();
                pdfExporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
                pdfExporter.setParameter(JRExporterParameter.OUTPUT_STREAM, output);
                pdfExporter.exportReport();
            }
            String jasperPath = policyReportPath + req.getLoginId() + System.currentTimeMillis() + ("Y".equalsIgnoreCase(req.getExcelYn()) ? ".xlsx" : ".pdf");
            log.info("PremiumReport byte Part");
            byte[] bs = output.toByteArray();
            String encodeToString = Base64.getEncoder().encodeToString(bs);
            FileOutputStream fs = new FileOutputStream(new File(jasperPath));
            fs.write(bs);
            fs.flush();
            fs.close();

            preRes = PremiumReportRes.builder()
                    .base64(prefix + encodeToString)
                    .fileName(fileName)
                    .filePath(jasperPath)
                    .build();

            log.info("PremiumReport res set");
            response.setCommonResponse(preRes);
            response.setIsError(false);
            response.setErrorMessage(Collections.emptyList());
            response.setMessage("Success");
        } catch (Exception e) {
            log.info("Error in PremiumReport ==> " + e);
            response.setCommonResponse(null);
            response.setIsError(true);
            response.setErrorMessage(Collections.emptyList());
            response.setMessage("Failed");
            e.printStackTrace();
        }
        return response;
    }

	/*@Override
	public CommonRes getPremiumReport(PremiumReportReq req) {
		log.info("Enter into PremiumReport ==> "+gson.toJson(req));
		CommonRes response = new CommonRes();
		PremiumReportRes preRes = new PremiumReportRes();
		String fileName="",prefix="",companylogo="";
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		Connection connection=null;
		try {
			String classpath = this.getClass().getClassLoader().getResource("").getPath();
			classpath = classpath.replaceAll("%20", " ");
			classpath = classpath.substring(1, classpath.length());

				LoginMaster loginData = loginMasterRepo.findByLoginId(req.getLoginId());
				if(loginData!=null) {
					String companycode = loginData.getCompanyId()==null?"":loginData.getCompanyId();
					List<Map<String,Object>> companyDetails = insuranceComMasRepo.getCompanyDetailsById(companycode);
					if(!companyDetails.isEmpty()) {
						companylogo = companyDetails.get(0).get("COMPANY_LOGO")==null?"":companyDetails.get(0).get("COMPANY_LOGO").toString();
					}
				}

				String imagepath = classpath + "report/images/"+companylogo; //windows system path

			String jasperPath = policyReportPath+req.getLoginId()+System.currentTimeMillis()+("Y".equalsIgnoreCase(req.getExcelYn())?".xlsx":".pdf");
			log.info("PremiumReport jasperPath ==> "+ jasperPath);
			HashMap<String, Object> jasperParameter = new HashMap<String, Object>();
			InputStream is;
			jasperParameter.put("pvBranch", StringUtils.isBlank(req.getBranchCode())?"99999":req.getBranchCode());
			jasperParameter.put("pvImagepath", imagepath);
			jasperParameter.put("pvLoginId", req.getLoginId());
			jasperParameter.put("pvProductId", req.getProductId());
			jasperParameter.put("pvCode", StringUtils.isBlank(req.getCode())?"99999":req.getCode());
			jasperParameter.put("pvUserType", StringUtils.isBlank(req.getUserType())?"99999":req.getUserType());
			if(dataBaseType.contains("oracle")) {
				jasperParameter.put("pvStartDate", req.getStartDate());
				jasperParameter.put("pvEndDate", req.getEndDate());
				is = this.getClass().getResourceAsStream("/report/jasper/EwayPremiumReport.jrxml");
			}else {
				jasperParameter.put("pvStartDate", getFormattedDate(req.getStartDate()));
				jasperParameter.put("pvEndDate", getFormattedDate(req.getEndDate()));
				is = this.getClass().getResourceAsStream("/report/jasper/EwayPremiumReportSql.jrxml");
			}
			log.info("PremiumReport jasperParameter ==> "+gson.toJson(jasperParameter));
			connection=config.getDataSourceForJasper().getConnection();
			if("Y".equalsIgnoreCase(req.getExcelYn())) {
				fileName ="PremiumRegister";
				prefix="data:application/vnd.ms-excel;base64,";
				JasperReport jasperReport = JasperCompileManager.compileReport(is);
				JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, jasperParameter, connection);
				/*JasperDesign design = JRXmlLoader.load(is);
				design.setPageFooter(null);
				design.setLeftMargin(0);
				JasperReport jasperReport = JasperCompileManager.compileReport(design);
				JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, jasperParameter, connection);
				SimpleXlsReportConfiguration configuration = new SimpleXlsReportConfiguration();
				configuration.setRemoveEmptySpaceBetweenRows(true);
				configuration.setWhitePageBackground(false);
				configuration.setDetectCellType(true);*/
				/*JRXlsExporter exporter = new JRXlsExporter();
				exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
				exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(output));
				try{
					exporter.exportReport();
				}catch(Exception e) {
					log.info("Error in PremiumReport ==> "+e.getMessage());
					e.printStackTrace();

				}
				log.info("PremiumReport Report Created");
			}else {
				fileName ="PremiumRegister";
				prefix="data:application/pdf;base64,";
				JasperReport jasperReport = JasperCompileManager.compileReport(is);
				JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, jasperParameter, connection);
				JRPdfExporter pdfExporter = new JRPdfExporter();
				pdfExporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
				pdfExporter.setParameter(JRExporterParameter.OUTPUT_STREAM, output);
				pdfExporter.exportReport();
			}
			log.info("PremiumReport byte Part");
			byte [] bs = output.toByteArray();
			String encodeToString = Base64.getEncoder().encodeToString(bs);
			FileOutputStream fs = new FileOutputStream(new File(jasperPath));
			fs.write(bs);
			fs.flush();
			fs.close();
			log.info("PremiumReport byte Part");

            preRes = PremiumReportRes.builder()
            		.base64(prefix+encodeToString)
            		.fileName(fileName)
            		.filePath(jasperPath)
            		.build();
            log.info("PremiumReport res set");
            response.setCommonResponse(preRes);
            response.setIsError(false);
            response.setErrorMessage(Collections.emptyList());
            response.setMessage("Success");
		}catch (Exception e) {
			response.setCommonResponse(null);
            response.setIsError(true);
            response.setErrorMessage(Collections.emptyList());
            response.setMessage("Failed");
			e.printStackTrace();
		}finally {
			if(connection!=null)
				try {
					connection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}

		}
		return response;
	}*/

    private static String getFormattedDate(String input) {
        String output = "";
        SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date = sdf1.parse(input);
            output = sdf2.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return output;
    }

    @Override
    public CommonRes getPremiumReportDetails(PremiumReportReq req) {
        CommonRes response = new CommonRes();
        System.out.println("Enter Into getPremiumReportDetails");
        try {
			/*int limit = StringUtils.isBlank(req.getLimit()) ? 0 : Integer.valueOf(req.getLimit());
			int offset = StringUtils.isBlank(req.getOffset()) ? 100 : Integer.valueOf(req.getOffset());
			int start =  limit * offset + 1 ;
			int end =  limit * offset + offset ;
//			int start =  limit; */
//			int end = offset ;
            List<Map<String, Object>> list = new ArrayList<>();
            String branchCode = StringUtils.isBlank(req.getBranchCode()) ? "99999" : req.getBranchCode();
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            String date1;
            String date2;
            if (dataBaseType.contains("oracle")) {
                date1 = req.getStartDate();
                date2 = req.getEndDate();
                list = branchRepo.getPremiumReportDetails(req.getProductId(), branchCode, date1, date2, req.getLoginId(), req.getUserType(), req.getCode());
            } else {
                date1 = new SimpleDateFormat("yyyy-MM-dd").format(sdf.parse(req.getStartDate()));
                date2 = new SimpleDateFormat("yyyy-MM-dd").format(sdf.parse(req.getEndDate()));

                String effectiveUserType = req.getUserType();

                if (StringUtils.isNotBlank(req.getCode())) {
                    Integer bdmExists = branchRepo.existsByBdmCode(req.getCode());
                    boolean isBdmCode = bdmExists != null && bdmExists == 1;
                    if (isBdmCode) {
                        effectiveUserType = "Premia Broker";
                    }
                }


                list = branchRepo.getPremiumReportDetailsSql(req.getProductId(), branchCode, date1, date2, req.getLoginId(), effectiveUserType, req.getCode());
            }
            ReportRes res = new ReportRes();
            List<Map<String, Object>> uniqueList = list.stream().collect(Collectors.collectingAndThen(Collectors.toMap(
                    m -> String.valueOf(m.get("QUOTE_NO")),  // unique key
                    m -> m,
                    (existing, duplicate) -> existing), m -> new ArrayList<>(m.values())));
            Integer count = uniqueList.size();
            if (list.size() > 0) {
                List<Map<String, Object>> dataRes = uniqueList.parallelStream().map(p -> {
                    LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
                    map.put("LoginId", p.get("LOGIN_ID") == null ? "" : p.get("LOGIN_ID"));
                    map.put("QuoteNo", p.get("QUOTE_NO") == null ? "" : p.get("QUOTE_NO"));
                    map.put("PolicyNo", p.get("POLICY_NO") == null ? "" : p.get("POLICY_NO"));
                    map.put("OriginalPolicyNo", p.get("ORIGINAL_POLICY_NO") == null ? "" : p.get("ORIGINAL_POLICY_NO"));
                    map.put("CustomerName", p.get("CUSTOMER_NAME") == null ? "" : p.get("CUSTOMER_NAME"));
                    map.put("StartDate", p.get("START_DATE") == null ? "" : sdf.format(p.get("START_DATE")));
                    map.put("EndDate", p.get("END_DATE") == null ? "" : sdf.format(p.get("END_DATE")));
                    map.put("IssueDate", p.get("ISSUED_DATE") == null ? "" : p.get("ISSUED_DATE"));
                    map.put("BranchName", p.get("BRANCH_NAME") == null ? "" : p.get("BRANCH_NAME"));
                    map.put("BrokerName", p.get("BROKER_NAME") == null ? "" : p.get("BROKER_NAME"));
                    map.put("SumInured", p.get("SUM_INSURED") == null ? "" : p.get("SUM_INSURED"));
                    map.put("Premium", p.get("PERMIUM") == null ? "" : p.get("PERMIUM"));
                    map.put("PaymentType", p.get("PAYMENT_TYPE") == null ? "" : p.get("PAYMENT_TYPE"));
                    map.put("Currency", p.get("CURRENCY") == null ? "" : p.get("CURRENCY"));
                    map.put("PolicyDesc", p.get("POLICY_TYPE_DESC") == null ? "" : p.get("POLICY_TYPE_DESC"));
                    map.put("CommisionAmt", p.get("COMMISSION_AMOUNT") == null ? "" : p.get("COMMISSION_AMOUNT"));
                    map.put("ProductName", p.get("PRODUCT_NAME") == null ? "" : p.get("PRODUCT_NAME"));
                    map.put("CreditLimit", p.get("CREDIT_LIMIT") == null ? "" : p.get("CREDIT_LIMIT"));
                    return map;
                }).collect(Collectors.toList());
                //Count
                res.setTotalCount(count.toString());
                res.setReportList(dataRes);
                response.setCommonResponse(res);
                response.setIsError(false);
                response.setErrorMessage(Collections.emptyList());
                response.setMessage("Success");

            } else {
                response.setCommonResponse(null);
                response.setIsError(true);
                response.setErrorMessage(Collections.emptyList());
                response.setMessage("Failed");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setCommonResponse(null);
            response.setIsError(true);
            response.setErrorMessage(Collections.emptyList());
            response.setMessage("Failed");
        }
        return response;
    }

    @Override
    public JasperDocumentRes illustration(String jsonFile) {
        try {
            //String filePath = config.getPolicyPath() + "pdf";
            // 	String filePath="d:\\"+Instant.now().toEpochMilli();
            String filePath = config.getPolicyPath() + Instant.now().toEpochMilli();
            //String filePath="d:\\"+Instant.now().toEpochMilli();
            String getPdfOutFilePath = filePath + ".pdf";
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("pvImagepath", externalImagePath);
            //map.put("pvPolicyNo", homeData.getPolicyNo());

            JasperDocumentRes res = getJasperPdfFileFromJson("/report/jasper/Illestration.jrxml", getPdfOutFilePath, map, jsonFile);
            return res;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

//    @SuppressWarnings("unchecked")
//    private JasperDocumentRes getCommonJasperPdfFileByJson(String jrxmlPath, String jasperSaveLocation, String jsonString, Map<String, Object> map, String fileNameEnd) {
//        log.info("Enter into getCommonJasperPdfFileByJson");
//        JasperDocumentRes res = new JasperDocumentRes();
//        InputStream inputStream = null;
//        int count = 0;
//        log.info(fileNameEnd.substring(2).replaceAll(".json", " ") + "JsonResponse ==> " + jsonString);
//        try {
//
//            JsonDataSource dataSource = null;
//
//            try {
//                dataSource = new JsonDataSource(new ByteArrayInputStream(jsonString.getBytes()));
//            } catch (Exception ex) {
//                System.err.println("Default encoding ByteArrayInputStream failed, retrying with UTF-8: " + ex.getMessage());
//                dataSource = new JsonDataSource(new ByteArrayInputStream(jsonString.getBytes(StandardCharsets.UTF_8)));
//            }
//            inputStream = this.getClass().getResourceAsStream(jrxmlPath);
//            JasperReport jasperReport = JasperCompileManager.compileReport(inputStream);
//            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, map, dataSource);
//            JasperExportManager.exportReportToPdfFile(jasperPrint, jasperSaveLocation + fileNameEnd.replace(".json", ".pdf"));
//            String path = jasperSaveLocation + fileNameEnd.replace(".json", ".pdf");
//            List<AttachMentRes> attachMentList = map.get("attachMents") == null ? Collections.emptyList() : (List<AttachMentRes>) map.get("attachMents");
//            if (!attachMentList.isEmpty()) {
//                PDFMergerUtility mergerUtility = new PDFMergerUtility();
//                mergerUtility.setDestinationFileName(jasperSaveLocation + fileNameEnd.replace(".json", "_merged.pdf"));
//                mergerUtility.addSource(jasperSaveLocation + fileNameEnd.replace(".json", ".pdf"));
//                for (AttachMentRes attMap : attachMentList) {
//                    count = count + 1;
//                    OutputStream outputStream = new FileOutputStream(new File(jasperSaveLocation + fileNameEnd.replace(".json", "_" + count + ".pdf")));
//                    PdfReader pdfReader = new PdfReader(attMap.getDocloction());
//                    PdfStamper pdfStamper = new PdfStamper(pdfReader, outputStream);
//                    for (int i = 1; i <= pdfReader.getNumberOfPages(); i++) {
//                        PdfContentByte contentByte = pdfStamper.getOverContent(i);
//                        contentByte.beginText();
//                        contentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_BOLD, BaseFont.CP1257, BaseFont.EMBEDDED), 12);
//                        contentByte.setTextMatrix(125, pdfReader.getPageSizeWithRotation(i).getHeight() - 20);
//                        contentByte.showText("Attached to and Forming Part of Policy No. " + (map.get("policyNo") == null ? "" : map.get("policyNo").toString()));
//                        contentByte.endText();
//                    }
//                    pdfStamper.close();
//                    File Attfile = new File(jasperSaveLocation + fileNameEnd.replace(".json", "_" + count + ".pdf"));
//                    mergerUtility.addSource(Attfile);
//                }
//                mergerUtility.mergeDocuments();
//                if (count > 0) {
//                    for (int f = 0; f < count; f++) {
//                        File Attfile = new File(jasperSaveLocation + fileNameEnd.replace(".json", "_" + count + ".pdf"));
//                        Attfile.delete();
//                    }
//                    File file1 = new File(jasperSaveLocation + fileNameEnd.replace(".json", ".pdf"));
//                    file1.delete();
//                }
//                path = jasperSaveLocation + fileNameEnd.replace(".json", "_merged.pdf");
//            }
//            GetFileFromPath filePath = new GetFileFromPath(path);
//            res.setPdfoutfile(filePath.call().getImgUrl());
//            res.setPdfoutfilepath(path);
//        } catch (Exception e) {
//            log.info("Error in getCommonJasperPdfFileByJson ==> " + e.getMessage());
//            e.printStackTrace();
//        } finally {
//            if (inputStream != null)
//                try {
//                    inputStream.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//        }
//        log.info("Exit into getCommonJasperPdfFileByJson");
//        return res;
//    }

    private static final Map<JasperPrint, JRVirtualizer> VIRTUALIZER_REGISTRY = Collections.synchronizedMap(new WeakHashMap<>());

    @SuppressWarnings("unchecked")
    private JasperDocumentRes getCommonJasperPdfFileByJson(String jrxmlPath, String jasperSaveLocation, String jsonString, Map<String, Object> map, String fileNameEnd) {
        log.info("Enter into getCommonJasperPdfFileByJson");
        JasperDocumentRes res = new JasperDocumentRes();
        InputStream inputStream = null;
        int count = 0;
        log.info(fileNameEnd.substring(2).replaceAll(".json", " ") + "JsonResponse ==> " + jsonString);
        JRVirtualizer virtualizer = null;
        try {

            JsonDataSource dataSource = null;
            try {
                dataSource = new JsonDataSource(new ByteArrayInputStream(jsonString.getBytes()));
            } catch (Exception ex) {
                System.err.println("Default encoding ByteArrayInputStream failed, retrying with UTF-8: " + ex.getMessage());
                dataSource = new JsonDataSource(new ByteArrayInputStream(jsonString.getBytes(StandardCharsets.UTF_8)));
            }

            inputStream = this.getClass().getResourceAsStream(jrxmlPath);
            JasperReport jasperReport = JasperCompileManager.compileReport(inputStream);
            virtualizer = new JRFileVirtualizer(2, policyReportPath + "VRFiles/");
            map.put(JRParameter.REPORT_VIRTUALIZER, virtualizer);
            map.putIfAbsent(
                    net.sf.jasperreports.engine.query.JRJdbcQueryExecuterFactory.PROPERTY_JDBC_FETCH_SIZE,
                    200
            );
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, map, dataSource);

            // stash virtualizer reference on the print object itself for later cleanup
            jasperPrint.getPropertiesMap().setProperty("__virtualizerRef", String.valueOf(System.identityHashCode(virtualizer)));
            VIRTUALIZER_REGISTRY.put(jasperPrint, virtualizer);

            String basePdfPath = jasperSaveLocation + fileNameEnd.replace(".json", ".pdf");
            JasperExportManager.exportReportToPdfFile(jasperPrint, basePdfPath);

            String path = basePdfPath;

            List<AttachMentRes> attachMentList = map == null || map.get("attachMents") == null
                    ? Collections.emptyList()
                    : (List<AttachMentRes>) map.get("attachMents");

            if (!attachMentList.isEmpty()) {
                String mergedPdfPath = jasperSaveLocation + fileNameEnd.replace(".json", "_merged.pdf");
                String policyNo = map.get("policyNo") == null ? "" : map.get("policyNo").toString();
                String headerText = "Attached to and Forming Part of Policy No. " + policyNo;

                // Step 1: Stamp each attachment using iText
                List<String> stampedPaths = new ArrayList<>();
                for (AttachMentRes attMap : attachMentList) {
                    count++;
                    String stampedPath = jasperSaveLocation + fileNameEnd.replace(".json", "_" + count + ".pdf");
                    PdfReader pdfReader = new PdfReader(attMap.getDocloction());
                    OutputStream outputStream = new FileOutputStream(new File(stampedPath));
                    PdfStamper pdfStamper = new PdfStamper(pdfReader, outputStream);
                    for (int i = 1; i <= pdfReader.getNumberOfPages(); i++) {
                        PdfContentByte contentByte = pdfStamper.getOverContent(i);
                        contentByte.beginText();
                        contentByte.setFontAndSize(
                            BaseFont.createFont(BaseFont.TIMES_BOLD, BaseFont.CP1257, BaseFont.EMBEDDED), 12);
                        contentByte.setTextMatrix(125, pdfReader.getPageSizeWithRotation(i).getHeight() - 20);
                        contentByte.showText(headerText);
                        contentByte.endText();
                    }
                    pdfStamper.close();
                    pdfReader.close();
                    stampedPaths.add(stampedPath);
                }

                // Step 2: Merge base PDF + stamped attachments using iText PdfCopy
                Document document = new Document();
                PdfCopy copy = new PdfCopy(document, new FileOutputStream(mergedPdfPath));
                document.open();

                // Add base jasper PDF first
                PdfReader baseReader = new PdfReader(basePdfPath);
                for (int i = 1; i <= baseReader.getNumberOfPages(); i++) {
                    copy.addPage(copy.getImportedPage(baseReader, i));
                }
                baseReader.close();

                // Add each stamped attachment
                for (String stampedPath : stampedPaths) {
                    PdfReader stampedReader = new PdfReader(stampedPath);
                    for (int i = 1; i <= stampedReader.getNumberOfPages(); i++) {
                        copy.addPage(copy.getImportedPage(stampedReader, i));
                    }
                    stampedReader.close();
                }
                document.close();

                // Step 3: Cleanup temp files
                for (String stampedPath : stampedPaths) {
                    File f = new File(stampedPath);
                    if (f.exists()) f.delete();
                }
                new File(basePdfPath).delete();

                path = mergedPdfPath;
            }
            GetFileFromPath filePath = new GetFileFromPath(path);
            res.setPdfoutfile(filePath.call().getImgUrl());
            res.setPdfoutfilepath(path);
           // res.setJsonString(jsonString);
        } catch (Exception e) {
            log.info("Error in getCommonJasperPdfFileByJson ==> " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (virtualizer != null) {
                try {
                    virtualizer.cleanup();
                } catch (Exception e) {
                    log.error("Virtualizer cleanup failed", e);
                }
            }
        }
        log.info("Exit into getCommonJasperPdfFileByJson");
        return res;
    }
    @Override
    public JasperDocumentRes getInalipaSchedule(String policyNo) {
        log.info("Enter into getInalipaSchedule \n Argument ==> " + policyNo);
        JasperDocumentRes res = new JasperDocumentRes();
        try {
            Map<String, Object> response = jasperCustomeImple.getInalipaSchedule(policyNo);
            String jsonString = gson.toJson(response);
            String jasperSaveLocation = policyReportPath.replaceAll("PolicyReport", "JsonFile") + policyNo.replaceAll("[\\/:*?\"<>|]*", "");
            res = getCommonJasperPdfFileByJson("/report/jasper/InalipaSchedule.jrxml", jasperSaveLocation, jsonString, null, "- InalipaSchedule.json");
            log.info("Exit into getInalipaSchedule");
        } catch (Exception e) {
            log.info("Error in getInalipaSchedule ==> " + e.getMessage());
            e.printStackTrace();
        }
        return res;
    }

    @SuppressWarnings("unchecked")
    public CommonRes getSchedule(JasperScheduleReq req) {
        CommonRes response = new CommonRes();
        try {
            HomePositionMaster hpm = homeRepo.findByQuoteNo(req.getQuoteNo());
            String companyId = hpm.getCompanyId();
            Integer productId = hpm.getProductId();
            String quoteNo = hpm.getQuoteNo();

            List<ReportJasperConfigMaster> list = jpqlQueryServiceImpl.getJasperReportConfigMaster(companyId, productId, Integer.valueOf(req.getReportId()));

            if (!list.isEmpty()) {
                ReportJasperConfigMaster report = list.get(0);
                String jasperReportJrxml = report.getJasperPath().trim();
                String jasperName = report.getJasperName();

                String classpath = this.getClass().getClassLoader().getResource("").getPath();
                classpath = classpath.replaceAll("%20", " ");
                classpath = classpath.substring(1, classpath.length());
                String imagepath = classpath + "report/images/";


                if ("Y".equals(report.getSubJasperYn())) { // for if subjasper yes

                    String[] subJasperArray = report.getSubJasperName().split(",");

                    for (String subJasperJrxml : subJasperArray) {
                        String jrxmlPath = classpath + "report/jasper/" + subJasperJrxml.replace(".jasper", ".jrxml");
                        if ("100004".equalsIgnoreCase(report.getId().getCompanyId()) && report.getId().getReportId() == 1) {
                            JasperDesign design = JRXmlLoader.load(new File(jrxmlPath));
                            JRDesignSection designSection = (JRDesignSection) design.getDetailSection();
                            designSection.removeBand(0);
                            JasperCompileManager.compileReportToFile(design, classpath + "report/jasper/" + subJasperJrxml.replace("jrxml", "jasper"));
                        } else {
                            String path = JasperCompileManager.compileReportToFile(jrxmlPath);
                            log.info("Jasper compileToReport path" + path);
                        }
                    }
                }

                HashMap<String, Object> jasperParameter = new HashMap<String, Object>();
                jasperParameter.put("pvImagepath", externalImagePath);
                jasperParameter.put("pvSubReportPath", externalJasperPath);

                JasperDocumentRes reponse = new JasperDocumentRes();
                Object result = jasperCustomeImple.callReport(req);
                if ("100019".equalsIgnoreCase(report.getId().getCompanyId())) {
                    if (result instanceof MotorPrivateRes) {
                        MotorPrivateRes motorPrivateRes = (MotorPrivateRes) result;
                        jasperParameter.put("attachMents", motorPrivateRes.getAttachmentList());
                        jasperParameter.put("policyNo", motorPrivateRes.getPolicyNo());
                    }
                } else if ("100004".equalsIgnoreCase(report.getId().getCompanyId())) {
                    Map<String, Object> reportRes = (Map<String, Object>) result;
                    jasperParameter.put("attachMents", (List<AttachMentRes>) reportRes.get("attachMents"));
                    jasperParameter.put("policyNo", reportRes.get("policyNo"));
                }
                String jsonString = gson.toJson(result);
                String jasperSaveLocation = policyReportPath.replaceAll("PolicyReport", "JsonFile") + quoteNo.replaceAll("[\\/:*?\"<>|]*", "");
                reponse = getCommonJasperPdfFileByJson(jasperReportJrxml, jasperSaveLocation, jsonString, jasperParameter, "- " + jasperName + ".json");
                response.setCommonResponse(reponse);
            }

        } catch (Exception e) {
            log.info("Error in getSchedule ==> " + e.getMessage());
            e.printStackTrace();
        }
        return response;
    }

    @Override
    public CommonRes PdfJsonResponse(PdfJsonReq req) {
        log.info("Enter in PdfJsonResponse => " + gson.toJson(req));
        CommonRes response = new CommonRes();
        Object Result = null;
        try {
            HomePositionMaster hpmData = homeRepo.findByQuoteNo(req.getQuoteNo());
            if (StringUtils.isNotBlank(hpmData.getQuoteNo())) {
                if (StringUtils.isBlank(req.getTaxInvoiceYn()) && StringUtils.isBlank(req.getCreditYn()) && StringUtils.isBlank(req.getEndtSchedule())) {
                    if (hpmData.getProductId() == 4) {
                        TravelReportRes res = jasperCustomeImple.getTravelReport(hpmData.getPolicyNo());
                        Result = res;
                    } else if (hpmData.getProductId() == 42) {
                        Result = jasperCustomeImple.getCyberInsurance(hpmData.getPolicyNo());
                    } else if (hpmData.getProductId() == 46) {
                        List<MotorCoverNoteRes> res = jasperCustomeImple.getEwayMotorCoverNote(hpmData.getPolicyNo(), req.getVehicleId(), "");
                        Result = res;
                    } else if (hpmData.getProductId() == 5) {
                        if ("100004".equalsIgnoreCase(hpmData.getCompanyId())) {
                            Result = jasperCustomeImple.getMadisonMotorSchedule(hpmData.getPolicyNo(), "",hpmData);
                        } else if ("100015".equalsIgnoreCase(hpmData.getCompanyId())) {
                            Result = jasperCustomeImple.getInalipaSchedule(hpmData.getPolicyNo());
                        } else {
                            MotorPrivateRes res = jasperCustomeImple.getMotorPrivate(hpmData.getPolicyNo(), hpmData.getQuoteNo(), req.getVehicleId());
                            Result = res;
                        }
                    } else {
                        Result = jasperCustomeImple.getEwaySchedule(hpmData.getQuoteNo());
                    }
                } else if (StringUtils.isNotBlank(hpmData.getPolicyNo())) {
                    if ("Y".equalsIgnoreCase(req.getTaxInvoiceYn())) {
                        TaxInvoiceRes res = jasperCustomeImple.getTaxInvoiceRes(hpmData.getPolicyNo(),hpmData);
                        Result = res;
                    } else if ("Y".equalsIgnoreCase(req.getCreditYn())) {
                        CreditNoteRes res = jasperCustomeImple.getCreditNoteRes(hpmData.getPolicyNo(),hpmData);
                        Result = res;
                    } else if ("Y".equalsIgnoreCase(req.getEndtSchedule())) {
                        Result = jasperCustomeImple.getMotorEndorsementSchedule(hpmData.getPolicyNo());
                    }
                }
                response.setCommonResponse(Result);
                response.setMessage("SUCCESS");
                response.setErrorMessage(null);
                response.setIsError(false);
            } else {
                response.setCommonResponse(null);
                response.setMessage("FAILED");
                response.setIsError(true);
            }
            log.info("Exit into PdfJsonResponse");
        } catch (Exception e) {
            log.info("Error in PdfJsonResponse ==> " + e.getMessage());
            e.printStackTrace();
        }
        return response;
    }

    @Override
    public JasperDocumentRes GetKenyaMOTbyRefNo(String requestRefNo) {
        log.info("Enter Into GetReportByRequestRefNo \n Argument ==> " + requestRefNo);
        JasperDocumentRes res = new JasperDocumentRes();
        try {
            Map<String, Object> resMap = jasperCustomeImple.GetKenyaMotorScheduleByRequestRefNo(requestRefNo);
            if (resMap != null) {
                Map<String, Object> map = new HashMap<String, Object>();
                log.info("OS Using ==> " + System.getProperty("os.name").toLowerCase());
                if (System.getProperty("os.name").toLowerCase().contains("windows")) {
                    map.put("pvImagepath", externalImagePath);
                } else {
                    map.put("pvImagepath", externalImagePath);
                }
                String jsonString = gson.toJson(resMap);
                String jasperSaveLocation = policyReportPath.replaceAll("PolicyReport", "JsonFile") + requestRefNo.replaceAll("[\\/:*?\"<>|]*", "");
                res = getCommonJasperPdfFileByJson("/report/jasper/KenyaMotorSchedule.jrxml", jasperSaveLocation, jsonString, map, "- BrokerQuotation.json");
                return res;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public JasperDocumentRes GetTravelQuotation(String requestRefNo) {
        log.info("Enter Into GetReportByRequestRefNo \n Argument ==> " + requestRefNo);
        JasperDocumentRes res = new JasperDocumentRes();
        try {
            TravelReportRes resMap = jasperCustomeImple.GetTravelQuotationByRequestRefNo(requestRefNo);

            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writerWithDefaultPrettyPrinter()
                    .writeValueAsString(resMap);

            log.info("TravelReportRes:\n{}", json);
            if (resMap != null) {
                Map<String, Object> map = new HashMap<String, Object>();
                log.info("OS Using ==> " + System.getProperty("os.name").toLowerCase());
                if (System.getProperty("os.name").toLowerCase().contains("windows")) {
                    map.put("pvImagepath", externalImagePath);
                } else {
                    map.put("pvImagepath", externalImagePath);
                }
                String jsonString = gson.toJson(resMap);
                String jasperSaveLocation = policyReportPath.replaceAll("PolicyReport", "JsonFile") + requestRefNo.replaceAll("[\\/:*?\"<>|]*", "");
                res = getCommonJasperPdfFileByJson("/report/jasper/EwayTravelQuotation.jrxml", jasperSaveLocation, jsonString, map, "- BrokerQuotation.json");
                return res;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<ApiDocListRes> getApiDocList(String quoteNo) {
        log.info("Enter into getApiDocList :: " + quoteNo);
        List<ApiDocListRes> res = new ArrayList<ApiDocListRes>();
        try {
            res = jasperCustomeImple.getApiDocList(quoteNo);
            return res;
        } catch (Exception e) {
            log.info("Error in getApiDocList || " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public JasperDocumentRes getApiDocReport(GetApiDocReportReq req) {
        log.info("Enter Into getApiDocReport \n Argument ==> " + gson.toJson(req));
        JasperDocumentRes res = new JasperDocumentRes();
        try {
            if ("Y".equalsIgnoreCase(req.getTravelYN())) {
                HomePositionMaster homeData = homeRepo.findByQuoteNo(req.getQuoteNo());
                if (homeData != null) {
                    File file = new File(homeData.getResponseStatusDesc());
                    if (file.exists()) {
                        byte[] pdfBytes = Files.readAllBytes(file.toPath());
                        String base64Encoded = Base64.getEncoder().encodeToString(pdfBytes);
                        String header = new String(Arrays.copyOfRange(pdfBytes, 0, 5));
                        System.out.println("PDF header: " + header);  // Should print "%PDF-"
                        String dataUri = "data:application/pdf;base64," + base64Encoded;
                        res.setPdfoutfile(dataUri);
                        res.setPdfoutfilepath(homeData.getResponseStatusDesc());
                    }
                    return res;
                }
            } else {
                ApiDocDownloadDetail resMap = apiDocDownloadDetailRepo.findByQuoteNoAndSgsId(req.getQuoteNo(), req.getFileCode());
                if (resMap != null) {
                    File file = new File(resMap.getFilePath() + resMap.getDocName());
                    if (file.exists()) {
                        GetFileFromPath filePath = new GetFileFromPath(resMap.getFilePath() + resMap.getDocName());
                        res.setPdfoutfile(filePath.call().getImgUrl());
                        res.setPdfoutfilepath(resMap.getFilePath() + resMap.getDocName());
                    }
                    return res;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public JasperDocumentRes getEagleQuotation(String requestReferenceNo, String computationSheetYn, String vehicleId) {
        log.info("Enter Into getEagleQuotation \n Argument ==> RequestReferenceNo : " + requestReferenceNo + "\n computationSheetYn : " + computationSheetYn + " \n vehicleId : " + vehicleId);
        JasperDocumentRes res = new JasperDocumentRes();
        try {
            Map<String, Object> input = new HashMap<String, Object>();
            String Imagepath;
            log.info("OS Using ==> " + System.getProperty("os.name").toLowerCase());
            if (System.getProperty("os.name").toLowerCase().contains("windows")) {
                Imagepath = config.getImagePath().substring(1, config.getImagePath().length() - 0);
            } else {
                Imagepath = config.getImagePath().replaceAll("\\\\", "/");
            }
            input.put("pvImagepath", externalImagePath);
            if ("Y".equalsIgnoreCase(computationSheetYn)) {
                List<Map<String, Object>> brokerQuotation = jasperCustomeImple.getEagleMotorComputationSheet(requestReferenceNo);
                String jsonString = gson.toJson(brokerQuotation);
                String jasperSaveLocation = policyReportPath.replaceAll("PolicyReport", "JsonFile") + requestReferenceNo.replaceAll("[\\/:*?\"<>|]*", "");
                res = getCommonJasperPdfFileByJson("/report/jasper/EaglePremiumComputation.jrxml", jasperSaveLocation, jsonString, input, "- EaglePremiumComputation.json");
            } else {
                List<MotorCoverNoteRes> brokerQuotation = jasperCustomeImple.getEagleMotorQuotation(requestReferenceNo, vehicleId);
                String jsonString = gson.toJson(brokerQuotation);
                String jasperSaveLocation = policyReportPath.replaceAll("PolicyReport", "JsonFile") + requestReferenceNo.replaceAll("[\\/:*?\"<>|]*", "");
                res = getCommonJasperPdfFileByJson("/report/jasper/EagleMotorBrokerQuotation.jrxml", jasperSaveLocation, jsonString, input, "- EagleMotorBrokerQuotation.json");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    @Override
    public ViewReportDetailsRes viewReportDetails(String quoteNo) {
        log.info("Enter in viewReportDetails.\n" + quoteNo);
        ViewReportDetailsRes response = new ViewReportDetailsRes();
        List<AttachMentRes> attachments = new ArrayList<AttachMentRes>();
        DecimalFormat df = new DecimalFormat("#,###");
        try {
            DozerBeanMapper dozerMappper = new DozerBeanMapper();
            HomePositionMaster homeData = homeRepo.findByQuoteNo(quoteNo);
            //CustomerInfo
            PersonalInfo custData = custRepo.findByCustomerId(homeData.getCustomerId());
            ReportCustomerInfoRes custRes = new ReportCustomerInfoRes();
            if (custData != null) {
                custRes = dozerMappper.map(custData, ReportCustomerInfoRes.class);
            }
            response.setCustomerDetails(custRes);

            //QuoteInfo
            ReportQuoteInfoRes quoteRes = new ReportQuoteInfoRes();
            quoteRes = dozerMappper.map(homeData, ReportQuoteInfoRes.class);
            quoteRes.setOverAllPremiumFc(homeData.getOverallPremiumFc() == null ? "" : homeData.getOverallPremiumFc().toPlainString());
            quoteRes.setOverAllPremiumLc(homeData.getOverallPremiumLc() == null ? "" : homeData.getOverallPremiumLc().toPlainString());
            quoteRes.setPremiumFc(homeData.getPremiumFc() == null ? "" : homeData.getPremiumFc().toPlainString());
            quoteRes.setPremiumLc(homeData.getPremiumLc() == null ? "" : homeData.getPremiumLc().toPlainString());
            quoteRes.setCommissionPercentage(homeData.getCommissionPercentage() == null ? "" : homeData.getCommissionPercentage().toPlainString());
            quoteRes.setVatCommission(homeData.getVatCommission() == null ? "" : homeData.getVatCommission().toPlainString());
            quoteRes.setAdminRemarks(homeData.getAdminRemarks());
            quoteRes.setReferalRemarks(homeData.getReferralDescription());
            quoteRes.setBrokerBranchCode(homeData.getBrokerBranchCode());
            quoteRes.setBrokerCode(homeData.getBrokerCode());
            quoteRes.setUserType(homeData.getUserType());
            quoteRes.setProductName(homeData.getProductName());
            quoteRes.setCompanyName(homeData.getCompanyName());
            quoteRes.setCustomerCode(homeData.getCustomerCode());
            quoteRes.setBranchName(homeData.getBranchName());
            quoteRes.setBrokerBranchName(homeData.getBrokerBranchName());
            quoteRes.setPolicyNo(homeData.getPolicyNo() == null ? "" : homeData.getPolicyNo());
            response.setQuoteDetails(quoteRes);

            //CompanyInfo
            ReportCompanyInfoRes companyRes = new ReportCompanyInfoRes();
            List<Map<String, Object>> companyDetails = insuranceComMasRepo.getCompanyDetailsById(homeData.getCompanyId());
            if (!companyDetails.isEmpty()) {
                companyRes.setCompanyName(companyDetails.get(0).get("COMPANY_NAME") == null ? "" : companyDetails.get(0).get("COMPANY_NAME").toString());
                companyRes.setCompanyLogo(companyDetails.get(0).get("COMPANY_LOGO") == null ? "" : companyDetails.get(0).get("COMPANY_LOGO").toString());
                companyRes.setCompanySignature(companyDetails.get(0).get("SIGNATURE") == null ? "" : companyDetails.get(0).get("SIGNATURE").toString());
                companyRes.setCompanyWebsite(companyDetails.get(0).get("COMPANY_WEBSITE") == null ? "" : companyDetails.get(0).get("COMPANY_WEBSITE").toString());
                companyRes.setCompanyMail(companyDetails.get(0).get("COMPANY_EMAIL") == null ? "" : companyDetails.get(0).get("COMPANY_EMAIL").toString());
                companyRes.setCompanyPhone(companyDetails.get(0).get("COMPANY_PHONE") == null ? "" : companyDetails.get(0).get("COMPANY_PHONE").toString());
                companyRes.setCompanyAddress(companyDetails.get(0).get("COMPANY_ADDRESS") == null ? "" : companyDetails.get(0).get("COMPANY_ADDRESS").toString());
                companyRes.setCompanyPoBox(companyDetails.get(0).get("PO_BOX") == null ? "" : companyDetails.get(0).get("PO_BOX").toString());
            }
            response.setCompanydetails(companyRes);

            //BrokerInfo
            ReportBrokerInfoRes brokerDetails = new ReportBrokerInfoRes();
            if ("broker".equalsIgnoreCase(homeData.getUserType().toLowerCase())) {
                LoginBranchMaster brokerBranch = loginBranchRepo.findByLoginIdAndBranchCodeAndCompanyId(homeData.getLoginId(), homeData.getBranchCode(), homeData.getCompanyId());
                if (brokerBranch != null) {
                    brokerDetails.setBrokerName(brokerBranch.getCustomerName() == null ? "" : brokerBranch.getCustomerName());
                    brokerDetails.setBrokerBranchName(brokerBranch.getBrokerBranchName() == null ? "" : brokerBranch.getBrokerBranchName());
                    brokerDetails.setCoreAppCode(brokerBranch.getCoreAppCode() == null ? "" : brokerBranch.getCoreAppCode());
                }
            }
            response.setBrokerDetails(brokerDetails);

            //SectionInfo
            List<ReportLocationInfoRes> locationRes = new ArrayList<ReportLocationInfoRes>();
            List<PolicyCoverData> coverData = coverDataRepository.findByQuoteNo(homeData.getQuoteNo());
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Tuple> cq1 = cb.createQuery(Tuple.class);
            Root<PolicyCoverData> pcdRoot = cq1.from(PolicyCoverData.class);
            Root<SectionDataDetails> sddRoot = cq1.from(SectionDataDetails.class);

            List<Predicate> predicate = new ArrayList<Predicate>();
            predicate.add(cb.equal(pcdRoot.get("quoteNo"), homeData.getQuoteNo()));
            predicate.add(cb.equal(pcdRoot.get("quoteNo"), sddRoot.get("quoteNo")));
            predicate.add(cb.equal(pcdRoot.get("sectionId").as(String.class), sddRoot.get("sectionId")));
            predicate.add(cb.equal(pcdRoot.get("taxId"), "0"));
            predicate.add(cb.equal(pcdRoot.get("discLoadId"), "0"));
            predicate.add(cb.equal(pcdRoot.get("subCoverId"), "0"));
            predicate.add(cb.equal(pcdRoot.get("locationId"), sddRoot.get("locationId")));
            predicate.add(cb.equal(pcdRoot.get("coverId"), sddRoot.get("coverId")));

            Predicate[] predicateArray = new Predicate[predicate.size()];
            predicate.toArray(predicateArray);

            Subquery<String> occDesc = cq1.subquery(String.class);
            Root<EserviceCommonDetails> ecdRoot = occDesc.from(EserviceCommonDetails.class);
            occDesc.select(ecdRoot.get("occupationDesc")).where(cb.equal(pcdRoot.get("quoteNo"), ecdRoot.get("quoteNo")), cb.equal(pcdRoot.get("sectionId").as(String.class), ecdRoot.get("sectionId")),
                    cb.equal(pcdRoot.get("vehicleId"), ecdRoot.get("riskId")), cb.equal(pcdRoot.get("productId").as(String.class), ecdRoot.get("productId")),
                    cb.equal(pcdRoot.get("companyId"), ecdRoot.get("companyId")), cb.equal(pcdRoot.get("locationId"), ecdRoot.get("locationId")),
                    cb.equal(pcdRoot.get("coverId"), ecdRoot.get("coverId")));

            cq1.multiselect(sddRoot.get("sectionId").alias("sectionId"), sddRoot.get("sectionDesc").alias("sectionDesc"), pcdRoot.get("coverDesc").alias("coverDesc"),
                            pcdRoot.get("coverId").alias("coverId"), pcdRoot.get("coverageType").alias("coverageType"), sddRoot.get("coverNoteReferenceNo").alias("coverNoteReferenceNo"),
                            pcdRoot.get("sumInsured").alias("sumInsured"), pcdRoot.get("rate").alias("rate"), pcdRoot.get("premiumIncludedTaxLc").alias("premiumIncludedTaxLc"),
                            pcdRoot.get("premiumIncludedTaxFc").alias("premiumIncludedTaxFc"), occDesc.alias("occupationDesc"),
                            pcdRoot.get("premiumExcludedTaxLc").alias("premiumExcludedTaxLc"), pcdRoot.get("premiumExcludedTaxFc").alias("premiumExcludedTaxFc"),
                            sddRoot.get("locationId").alias("locationId"), sddRoot.get("locationName").alias("locationName"), sddRoot.get("productType").alias("productType"))
                    .where(predicateArray).orderBy(cb.asc(sddRoot.get("sectionId")));

            List<Tuple> Slist = em.createQuery(cq1).getResultList();

            Map<String, List<Map<String, Object>>> secGroupList = new HashMap<>();
            List<SectionDetailsDTO> riskSectionDtl = new ArrayList<>();

            String token = null;
            HttpHeaders headers1 = null;
            String apiBaseUrl = null;

            if (Arrays.asList(101, 85).contains(homeData.getProductId())) {
                String CurrentURL = jasperCustomeImple.currentRequestURL();

                URI uri = new URI(CurrentURL);
                String host = uri.getHost();
                String scheme = uri.getScheme();

                if (host != null && host.matches(".*[a-zA-Z].*")) {
                    scheme = "https";
                }
                apiBaseUrl = scheme + "://" + host;
                int port = uri.getPort();
                if (port != -1) {
                    apiBaseUrl += ":" + port;
                } else {
                    apiBaseUrl += "/EwayCommonApi";
                }

                RestTemplate restTemplate = new RestTemplate();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Arrays.asList(new MediaType[]{MediaType.APPLICATION_JSON}));
                headers.setContentType(MediaType.APPLICATION_JSON);

                Map<String, Object> req = new HashMap<>();
                req.put("LoginId", "guest");
                req.put("Password", "Admin@01");
                req.put("ReLoginKey", "Y");

                log.info("Current URL :: " + apiBaseUrl);

                HttpEntity<Object> entityReq = new HttpEntity<>(req, headers);
                ResponseEntity<Map> TokenRes = restTemplate.postForEntity(apiBaseUrl + "/authentication/login", entityReq, Map.class);

                Map<String, Object> result = TokenRes.getBody() != null ? (Map<String, Object>) TokenRes.getBody().get("Result") : null;
                if (result != null) {
                    token = result.get("Token") == null ? "" : result.get("Token").toString();

                    headers1 = new HttpHeaders();
                    headers1.setAccept(Arrays.asList(new MediaType[]{MediaType.APPLICATION_JSON}));
                    headers1.setContentType(MediaType.APPLICATION_JSON);
                    headers1.setBearerAuth(token);

                    ResponseEntity<Map> engRes = restTemplate.postForEntity(
                            apiBaseUrl.replace("8086", "8085").replace("Common", "Motor")
                                    + "/api/getEngineerInfo?RequestReferenceno=" + homeData.getRequestReferenceNo(),
                            new HttpEntity<>(Map.of("RequestReferenceNo", homeData.getRequestReferenceNo()), headers1),
                            Map.class
                    );
                    List<Map<String, Object>> enginfoList = engRes.getBody() != null
                            ? (List<Map<String, Object>>) engRes.getBody().get("Result")
                            : null;
                    if (enginfoList != null && enginfoList.size() > 0) {
                        secGroupList = enginfoList.stream()
                                .collect(Collectors.groupingBy(a -> (String) a.get("SectionId"), Collectors.toList()));
                    }
                }
            } else {
                riskSectionDtl = riskDetailsServ.getDetailsByQuoteNo(quoteNo).getSectionDetails();
            }


            List<Map<String, Object>> collateralList = null;
            if (token != null && headers1 != null) {
                Map<String, Object> collReq = new HashMap<>();
                collReq.put("RequestReferenceNo", homeData.getRequestReferenceNo());

                HttpEntity<Object> collEntity = new HttpEntity<>(collReq, headers1);

                RestTemplate restTemplate = new RestTemplate();
                ResponseEntity<Map> collRes = restTemplate.postForEntity(
                        apiBaseUrl.replace("8086", "8085").replace("Common", "Motor")
                                + "/api/getCollateralvalue?RequestReferenceno=" + homeData.getRequestReferenceNo(),
                        collEntity,
                        Map.class
                );

                collateralList = collRes.getBody() != null
                        ? (List<Map<String, Object>>) collRes.getBody().get("Result")
                        : null;

                log.info("COLLATERAL LIST :: {}", new Gson().toJson(collateralList));
            }


            Set<Integer> findlocationid = Slist.stream().map(m -> Integer.parseInt(m.get("locationId").toString())).distinct()
                    .collect(Collectors.toSet());
            for (Integer d : findlocationid) {
                ReportLocationInfoRes locRes = new ReportLocationInfoRes();
                List<Tuple> filter = Slist.stream()
                        .filter(o -> Integer.valueOf(o.get("locationId").toString()).equals(d))
                        .distinct()
                        .collect(Collectors.toList());
                locRes.setLocationId(filter.get(0).get("locationId") == null ? "" : filter.get(0).get("locationId").toString());
                locRes.setLocationName(filter.get(0).get("locationName") == null ? "" : filter.get(0).get("locationName").toString());
                List<ReportSectionInfoRes> sectionRes = new ArrayList<ReportSectionInfoRes>();
                List<String> existingSections = new ArrayList<>();
                for (Tuple sec : filter) {
                    if (!existingSections.contains(sec.get("sectionId") == null ? "" : sec.get("sectionId").toString())) {
                        existingSections.add(sec.get("sectionId") == null ? "" : sec.get("sectionId").toString());
                        ReportSectionInfoRes secRes = new ReportSectionInfoRes();
                        secRes.setSectionId(sec.get("sectionId") == null ? "" : sec.get("sectionId").toString());
                        secRes.setSectionName(sec.get("sectionDesc") == null ? "" : sec.get("sectionDesc").toString());
                        secRes.setPremium(filter.stream()
                                .filter(y -> y.get("sectionId").toString().equalsIgnoreCase(sec.get("sectionId").toString()))
                                .map(p -> p.get("premiumExcludedTaxFc") == null ? BigDecimal.ZERO : new BigDecimal(p.get("premiumExcludedTaxFc").toString()))
                                .collect(Collectors.summingDouble(BigDecimal::doubleValue)));
                        secRes.setSumInsured(filter.stream().filter(t -> t.get("sectionId").toString().equalsIgnoreCase(sec.get("sectionId").toString()))
                                .map(r -> r.get("sumInsured") == null ? BigDecimal.ZERO : new BigDecimal(r.get("sumInsured").toString()))
                                .collect(Collectors.summingDouble(BigDecimal::doubleValue)));

                        List<ReportCoverInfoRes> coverRes = new ArrayList<ReportCoverInfoRes>();
                        List<PolicyCoverData> CoverList = coverData.stream()
                                .filter(u -> !"A".equalsIgnoreCase(u.getCoverageType()) && u.getTaxId() == 0 && u.getDiscLoadId() == 0
                                        && u.getSectionId() == Integer.parseInt(sec.get("sectionId").toString()))
                                .collect(Collectors.toList());
                        for (PolicyCoverData cov : CoverList) {
                            ReportCoverInfoRes covRes = new ReportCoverInfoRes();
                            covRes.setCoverId(cov.getCoverId() == null ? "" : cov.getCoverId().toString());
                            covRes.setCovername(cov.getCoverName() == null ? "" : cov.getCoverName());
                            covRes.setSumInsured(cov.getSumInsured() == null ? "" : df.format(cov.getSumInsured()));
                            covRes.setAnnually(cov.getPremiumExcludedTaxFc() == null ? "" : df.format(cov.getPremiumExcludedTaxFc()));
                            covRes.setMonthly(cov.getPremiumExcludedTaxFc() == null ? "" : df.format(cov.getPremiumExcludedTaxFc().divide(BigDecimal.valueOf(12), RoundingMode.HALF_UP)));
                            coverRes.add(covRes);
                        }
                        secRes.setCoverDetails(coverRes);

                        List<ReportBenefitInfoRes> benefitCoverRes = new ArrayList<ReportBenefitInfoRes>();
                        List<PolicyCoverData> benefitCoverList = coverData.stream()
                                .filter(u -> "A".equalsIgnoreCase(u.getCoverageType()) && u.getTaxId() == 0 && u.getDiscLoadId() == 0
                                        && u.getSectionId() == Integer.parseInt(sec.get("sectionId").toString()))
                                .collect(Collectors.toList());
                        for (PolicyCoverData cov : benefitCoverList) {
                            ReportBenefitInfoRes covRes = new ReportBenefitInfoRes();
                            covRes.setCoverName(cov.getCoverName() == null ? "" : cov.getCoverName());
                            covRes.setCoverLimit(cov.getCoverageLimit() == null ? "" : df.format(cov.getCoverageLimit()));
                            benefitCoverRes.add(covRes);
                        }
                        secRes.setBenefitDetails(benefitCoverRes);

                        List<ReportExcessInfoRes> excessdetails = new ArrayList<ReportExcessInfoRes>();
                        List<ExcessMaster> excessList = excessRepo
                                .findByProductIdAndCompanyIdAndSectionId(
                                        homeData.getProductId().toString(), homeData.getCompanyId(), sec.get("sectionId") == null ? "" : sec.get("sectionId").toString());

                        List<ExcessTransactionDetails> excessTransactionDetails = excessTransactionDetailsRepo.findByRequestReferenceNoAndProductIdAndSectionId(
                                homeData.getRequestReferenceNo().toString(), homeData.getProductId().toString(), sec.get("sectionId") == null ? "" : sec.get("sectionId").toString()
                        );

                        if (excessTransactionDetails != null && excessTransactionDetails.size() > 0) {
                            excessTransactionDetails.forEach(e_l -> {
                                ReportExcessInfoRes e = new ReportExcessInfoRes();
                                e.setExcessDescription(e_l.getExcessDescription() == null ? "" : e_l.getExcessDescription());
                                e.setExcessAmount(e_l.getExcessAmount() == null ? 0.00 : e_l.getExcessAmount());
                                e.setExcessPercent(e_l.getExcessPercentage() == null ? 0 : e_l.getExcessPercentage());
                                excessdetails.add(e);
                            });
                        } else {
                            if (excessList != null && excessList.size() > 0) {
                                excessList.forEach(e_l -> {
                                    ReportExcessInfoRes e = new ReportExcessInfoRes();
                                    e.setExcessDescription(e_l.getExcessDescription() == null ? "" : e_l.getExcessDescription());
                                    e.setExcessAmount(e_l.getExcessAmount() == null ? 0.00 : e_l.getExcessAmount());
                                    e.setExcessPercent(e_l.getExcessPercentage() == null ? 0 : e_l.getExcessPercentage());
                                    excessdetails.add(e);
                                });
                            }
                        }
                        secRes.setExcessDetails(excessdetails);


                        // CONDITIONS
                        List<Map<String, Object>> conditionList = jasperCustomeImple.getConditionList(homeData.getPolicyNo(), homeData.getQuoteNo(), sec.get("sectionId") == null ? "" : sec.get("sectionId").toString());

                        // EXCLUSION
                        List<Map<String, Object>> exclusionRes = jasperCustomeImple.getExclusionList(homeData.getPolicyNo(), homeData.getQuoteNo(), sec.get("sectionId") == null ? "" : sec.get("sectionId").toString());
                        List<Map<String, Object>> exclusionList = exclusionRes.stream().map(k -> {
                            Map<String, Object> eMap = new HashMap<String, Object>();
                            eMap.put("conditionTerms", k.get("exclusioTerms"));
                            eMap.put("SectionId", k.get("SectionId"));
                            return eMap;
                        }).collect(Collectors.toList());

                        //WARRANTY
                        List<Map<String, Object>> warrantyList = jasperCustomeImple.getWarrantyDescription(homeData.getPolicyNo(), homeData.getQuoteNo(), sec.get("sectionId") == null ? "" : sec.get("sectionId").toString());

                        List<LinkedHashMap<String, Object>> termsAndconditions = Stream.of(warrantyList, exclusionList).flatMap(Collection::stream)
                                .sorted(Comparator.comparing(p -> {
                                    if (p.get("Sno") == null || p.get("Sno").toString().isEmpty()) {
                                        return Integer.MAX_VALUE;
                                    }
                                    try {
                                        return Integer.parseInt(p.get("Sno").toString());
                                    } catch (NumberFormatException e) {
                                        return Integer.MAX_VALUE;
                                    }
                                }))
                                .map(u -> {
                                    LinkedHashMap<String, Object> m = new LinkedHashMap<String, Object>();
                                    m.put("conditionTerms", u.get("conditionTerms") == null ? "" : u.get("conditionTerms").toString());
                                    return m;
                                }).distinct().collect(Collectors.toList());

                        int conditionsize = termsAndconditions.size();
                        List<LinkedHashMap<String, Object>> firstHalf, secondHalf = new ArrayList<LinkedHashMap<String, Object>>();
                        if (conditionsize > 10) {
                            int midIndex = conditionsize / 2;
                            firstHalf = termsAndconditions.subList(0, midIndex);
                            secondHalf = termsAndconditions.subList(midIndex, conditionsize);
                        } else {
                            firstHalf = termsAndconditions.subList(0, conditionsize);
                        }

                        secRes.setFirstconditionsDetails(firstHalf);
                        secRes.setSecondconditionsDetails(secondHalf);
                        secRes.setClausesList(conditionList);
                        sectionRes.add(secRes);


                        List<ProductSectionMaster> psmList =
                                productSectionMasterRepo.findByProductIdAndCompanyIdOrderByAmendIdDesc(
                                        homeData.getProductId(),
                                        homeData.getCompanyId()
                                );

                        List<ReportSectionMasterRes> psmRes = psmList.stream()
                                .map(p -> {
                                    ReportSectionMasterRes res = new ReportSectionMasterRes();
                                    res.setSectionId(p.getSectionId().toString());
                                    res.setSectionName(p.getSectionName());
                                    res.setPremium(Slist.stream()
                                            .filter(t -> t.get("sectionId").toString().equalsIgnoreCase(p.getSectionId().toString()))
                                            .map(r -> r.get("premiumExcludedTaxFc") == null ? BigDecimal.ZERO : new BigDecimal(r.get("premiumExcludedTaxFc").toString()))
                                            .collect(Collectors.summingDouble(BigDecimal::doubleValue)));
                                    res.setMonthly(Slist.stream()
                                            .filter(t -> t.get("sectionId").toString().equalsIgnoreCase(p.getSectionId().toString()))
                                            .map(r -> r.get("premiumExcludedTaxFc") == null ?
                                                    BigDecimal.ZERO :
                                                    new BigDecimal(r.get("premiumExcludedTaxFc").toString())
                                                    .divide(BigDecimal.valueOf(12), RoundingMode.HALF_UP))
                                            .collect(Collectors.summingDouble(BigDecimal::doubleValue)));
                                    res.setSumInsured(Slist.stream()
                                            .filter(t -> t.get("sectionId").toString().equalsIgnoreCase(p.getSectionId().toString()))
                                            .map(r -> r.get("sumInsured") == null ? BigDecimal.ZERO : new BigDecimal(r.get("sumInsured").toString()))
                                            .collect(Collectors.summingDouble(BigDecimal::doubleValue)));

                                    return res;
                                })
                                .collect(Collectors.toList());

                        response.setProductSectionMaster(psmRes);

							/*List<CommonSectionRes> commonList = new ArrayList<>();
							for (ReportSectionMasterRes psm : psmRes) {

							    CommonSectionRes cs = new CommonSectionRes();
							    cs.setSectionId(psm.getSectionId());
							    cs.setSectionName(psm.getSectionName());

							    if (sectionRes.get(0).getSectionId().equalsIgnoreCase(psm.getSectionId()) ) {

							        cs.setStatus("Y");
							        cs.setPremium(sectionRes.get(0).getPremium());
							        cs.setSumInsured(sectionRes.get(0).getSumInsured());

							    } else {

							        cs.setStatus("N");
							        cs.setPremium(0.0);
							        cs.setSumInsured(BigDecimal.ZERO);
							    }

							    commonList.add(cs);
							    response.setCommonSectionRes(commonList);
							}*/


                        ProductSectionMaster psmData = psmList.stream()
                                .filter(v -> v.getSectionId().toString()
                                        .equalsIgnoreCase(sec.get("sectionId") == null ? "" : sec.get("sectionId").toString()))
                                .max(Comparator.comparing(ProductSectionMaster::getAmendId))
                                .orElse(null);
                        if (psmData != null && StringUtils.isNotBlank(psmData.getFilePathOrginal()) && StringUtils.isNotBlank(psmData.getOrginalFileName())) {
                            AttachMentRes att = AttachMentRes.builder()
                                    .docloction(psmData.getFilePathOrginal() == null ? "" : psmData.getFilePathOrginal())
                                    .docRefNo(psmData.getOrginalFileName() == null ? "" : psmData.getOrginalFileName())
                                    .build();
                            attachments.add(att);
                        }

                        if (secGroupList != null) {
                            List<Map<String, Object>> secEngList = secGroupList.get(sec.get("sectionId") == null ? "" : sec.get("sectionId").toString());
                            secRes.setEngInfoList(secEngList);
                        }

                        if (riskSectionDtl != null && riskSectionDtl.size() > 0) {
                            SectionDetailsDTO secRiskDetail = riskSectionDtl.stream()
                                    .filter(g -> g.getSectionId().equalsIgnoreCase(sec.get("sectionId") == null ? "" : sec.get("sectionId").toString()))
                                    .findFirst()
                                    .orElse(null);

                            if (secRiskDetail != null) {
                                List<CoverDetailsDTO> tempCoverDTL = secRiskDetail.getCoverDetails().stream()
                                        .map(k -> {
                                            CoverDetailsDTO l = new CoverDetailsDTO();
                                            l.setKey(
                                                    coverRes.stream()
                                                            .filter(o -> o.getCoverId().equalsIgnoreCase(k.getKey()))
                                                            .map(q -> q.getCovername())
                                                            .findFirst()
                                                            .orElse(null));
                                            l.setValue(k.getValue());
                                            return l;
                                        }).collect(Collectors.toList());
                                secRiskDetail.setCoverDetails(tempCoverDTL);
                                secRes.setSectionRiskDetails(secRiskDetail);
                            }
                        }
                    }
                }
                locRes.setSectionDetails(sectionRes);
                locationRes.add(locRes);
            }
            response.setLocationDetails(locationRes);
            if (collateralList != null && response.getLocationDetails() != null) {

                for (ReportLocationInfoRes locRes : response.getLocationDetails()) {

                    if (locRes.getSectionDetails() == null) continue;

                    for (ReportSectionInfoRes secRes : locRes.getSectionDetails()) {

                        List<CollateralRes> secCollateralList =
                                collateralList.stream()
                                        .filter(c ->
                                                c.get("SECTION_ID") != null &&
                                                        secRes.getSectionId() != null &&
                                                        c.get("SECTION_ID").toString().trim()
                                                                .equalsIgnoreCase(secRes.getSectionId().trim())
                                        )
                                        .map(c -> {
                                            CollateralRes cr = new CollateralRes();
                                            cr.setCollateralDescription(
                                                    c.get("COLLATERAL_DESC") == null
                                                            ? ""
                                                            : c.get("COLLATERAL_DESC").toString()
                                            );
                                            cr.setCollateralValue(
                                                    c.get("COLLATERL_VALUE") == null
                                                            ? ""
                                                            : c.get("COLLATERL_VALUE").toString()
                                            );
                                            cr.setCollateralparam1(
                                                    c.get("PARAM1") == null
                                                            ? ""
                                                            : c.get("PARAM1").toString()
                                            );
                                            return cr;
                                        })
                                        .collect(Collectors.toList());

                        secRes.setCollateralValueList(secCollateralList);
                    }
                }
            }


            response.setAttachments(attachments);

            log.info("viewReportDetails Res ==> " + new Gson().toJson(response));
            log.info("Exit into viewReportDetails");
        } catch (Exception e) {
            log.info("Error in viewReportDetails ==> " + e.getMessage());
            e.printStackTrace();
        }
        return response;
    }

    @Override
    public List<PortFolioDashBoardRes> getAllAdminPortfolio(PortFolioDashBoardReq req) {
        List<PortFolioDashBoardRes> resList = new ArrayList<PortFolioDashBoardRes>();
        DecimalFormat df = new DecimalFormat("0.##");
        try {
            List<CompanyProductMaster> productList = jasperCustomeImple.getCompanyProductList(req.getInsuranceId());
            List<PortFolioAdminTupleRes> list = jasperCustomeImple.getPortFolioDashBoard(req);

            // Group By Product Id
            // Map<Integer ,List<PortFolioAdminTupleRes>> groupByProductId =
            // list.stream().collect(Collectors.groupingBy(PortFolioAdminTupleRes ::
            // getProductId )) ;
            for (CompanyProductMaster product : productList) {

                if (StringUtils.isBlank(req.getProductId()) || "99999".equalsIgnoreCase(req.getProductId())
                        || product.getProductId().equals(Integer.valueOf(req.getProductId()))) {
                    int counter = 1;

//	    for (String path : filePaths) {
//	        AttachMentRes res = AttachMentRes.builder()
//	                .docRefNo(String.valueOf(counter))
//	                .docloction(path)
//	                .build();

                    List<PortFolioAdminTupleRes> filterProduct = list.stream()
                            .filter(o -> o.getBrokerName() != null && o.getBrokerName().equals(product.getProductId()))
                            .collect(Collectors.toList());

                    // Map Broker List
                    List<PortfolioBrokerListRes> brokerResList = new ArrayList<PortfolioBrokerListRes>();
                    for (PortFolioAdminTupleRes data : filterProduct) {
                        PortfolioBrokerListRes brokerRes = new PortfolioBrokerListRes();

//						if(StringUtils.isNotBlank(data.getBdmCode())){
//							brokerRes.setBrokerCode(data.getCustomerCode() == null ? "0" : data.getCustomerCode().toString());
//							brokerRes.setBrokerName(data.getCustomerName());
//						} else {
                        brokerRes.setBrokerCode(data.getOaCode() == null ? "0" : data.getOaCode().toString());
                        brokerRes.setBrokerName(data.getBrokerName());
                        brokerRes.setProductName(data.getProductName());
//						}
//						brokerRes.setBrokerLoginId(data.getLoginId());
//						brokerRes.setSubUserType(data.getSubUserType());
                        brokerRes.setTotalCount(data.getCount() == null ? 0 : data.getCount());
                        brokerRes.setTotalPremiumLc(data.getOverallPremiumLc() == null ? "0"
                                : df.format(Double.valueOf(data.getOverallPremiumLc().toPlainString())));
                        brokerRes.setTotalPremiumFc(data.getOverallPremiumFc() == null ? "0"
                                : df.format(Double.valueOf(data.getOverallPremiumFc().toPlainString())));
                        brokerRes.setUserType(data.getUserType());
                        brokerRes.setSourceType(data.getSourceType());
                        brokerRes.setBdmCode(data.getBdmCode());
                        brokerResList.add(brokerRes);
                    }
                    brokerResList.sort(Comparator.comparing(PortfolioBrokerListRes::getTotalCount).reversed());

                    // Response
                    PortFolioDashBoardRes res = new PortFolioDashBoardRes();
                    res.setBrokerList(brokerResList);
                    res.setProductId(product.getProductId().toString());
                    res.setProductName(product.getProductName());
                    res.setBranchName(req.getBranchCode());
//					res.getBrokerName(req.getBranchCode());
                    res.setBrokerCount(brokerResList.size() > 0 ? Long.valueOf(brokerResList.size()) : 0);
                    resList.add(res);
                }

            }
            resList.sort(Comparator.comparing(PortFolioDashBoardRes::getBrokerCount).reversed());

        } catch (Exception e) {
            e.printStackTrace();
            log.info("Log Details" + e.getMessage());
            return null;
        }
        return resList;
    }


    @Override
    public JasperDocumentRes policyformByRequestRef(JasperDocumentReq req) {
        JasperDocumentRes res = new JasperDocumentRes();
        List<Error> errors = new ArrayList<>();
        try {
            List<EserviceSectionDetails> homeDataList = eserviceSectionDetailsRepo.findByRequestReferenceNoOrderByRiskIdAsc(req.getRequestReferenceNo());
            EserviceSectionDetails homeData = homeDataList.get(0);
            String classpath = this.getClass().getClassLoader().getResource("").getPath();
            classpath = classpath.replaceAll("%20", " ");
            classpath = classpath.substring(1, classpath.length());

            String Imagepath = classpath + "report/images/";

            String jasperSaveLocation = policyReportPath.replaceAll("PolicyReport", "JsonFile") +
                    req.getRequestReferenceNo().replaceAll("[\\/:*?\"<>|]*", "");

            if ("100020".equalsIgnoreCase(homeData.getCompanyId())) {
            	
            	
            	if (homeData.getProductId().equalsIgnoreCase("118")) {
                    res = getGolfersQuotationPdf(req.getRequestReferenceNo());
                    return res;
                }
            	
                Map<String, Object> input2 = new HashMap<String, Object>();
                input2.put("pvImagepath", externalImagePath);


                Map<String, Object> EwaySchedule = jasperCustomeImple.getEwayScheduleByRequestRef(req.getRequestReferenceNo(), homeData.getProductId());


                String jsonString = gson.toJson(EwaySchedule);

                input2.put("attachMents", EwaySchedule.get("attachMents"));
                input2.put("policyNo", EwaySchedule.get("policyNo"));

                if (homeData.getProductId().equalsIgnoreCase("87")) {
                    res = getCommonJasperPdfFileByJson("/report/jasper/ProfessionalIndemnityQuotation.jrxml",
                            jasperSaveLocation, jsonString, input2, "- ProfessionalIndemnityQuotation.json");
                } else if (homeData.getProductId().equalsIgnoreCase("87") && "Y".equalsIgnoreCase(req.getCertificateYn())) {
                    input2.put("attachMents", EwaySchedule.get("attachMents"));
                    res = getCommonJasperPdfFileByJson("/report/jasper/Professional_Indemnity.jrxml",
                            jasperSaveLocation, jsonString, input2, "- KenyaQuotation.json");
                } else if (homeData.getProductId().equalsIgnoreCase("59")) {
                    res = getCommonJasperPdfFileByJson("/report/jasper/DomesticQuotation.jrxml",
                            jasperSaveLocation, jsonString, input2, "- DomesticQuotation.json");
                } else {
                    res = getCommonJasperPdfFileByJson("/report/jasper/EwayKenyaSchedule.jrxml",
                            jasperSaveLocation, jsonString, input2, "- EwayKenyaSchedule.json");
                }
            } else {
                errors.add(new Error("", "UNSUPPORTED_COMPANY",
                        "This API currently only supports companyId 100020. RequestReferenceNo: " + req.getRequestReferenceNo()));
                res.setErrorMessage(errors);
                res.setPdfoutfile(null);
                res.setPdfoutfilepath(null);
                return res;
            }

        } catch (Exception e) {
            log.error("Error in policyformByRequestRef: ", e);
            errors.add(new Error("", "EXCEPTION", e.getMessage()));
            res.setErrorMessage(errors);
            res.setPdfoutfile(null);
            res.setPdfoutfilepath(null);
        }
        return res;
    }

    @Override
    public String callThirdPartyApi(String tokenUrl, String apiUrl, String userName, String password, String policyNo) {
        try {
            System.out.println("Entering API Call....");
            String token = getTiraAuthToken(tokenUrl, userName, password);

            if (StringUtils.isBlank(token)) {
                log.error("Failed to retrieve auth token from: {}", tokenUrl);
                System.out.println("Failed to retrieve auth token from: " + tokenUrl);
                return null;
            }

            log.info("Token received successfully, calling TIRA API...");
            System.out.println("Token received successfully, calling TIRA API...");

            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + token);

            Map<String, Object> inParams = new HashMap<>();
            inParams.put("P_PTL_POL_NO", policyNo);
            inParams.put("P_END_NO_IDX", 0);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("procedureName", "WNPRC_GET_TRA_STS");
            requestBody.put("inParams", inParams);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            log.info("Calling TIRA Status API => URL: {}, PolicyNo: {}", apiUrl, policyNo);
            System.out.println("Calling TIRA Status API => URL: " + apiUrl + ", PolicyNo: " + policyNo);

            ResponseEntity<String> response = restTemplate.exchange(
                    apiUrl,
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            log.info("TIRA API Response => Status: {}, Body: {}", response.getStatusCode(), response.getBody());
            System.out.println("TIRA API Response => Status: " + response.getStatusCode() + ", Body: " + response.getBody());

            if (response.getStatusCode() == HttpStatus.OK && StringUtils.isNotBlank(response.getBody())) {


                ObjectMapper objectMapper = new ObjectMapper();
                Map<String, Object> jsonResponse = objectMapper.readValue(response.getBody().trim(), Map.class);

                String status = (String) jsonResponse.get("status");
                System.out.println("TIRA Response Status: " + status);

                Map<String, Object> dataMap = (Map<String, Object>) jsonResponse.get("Data");
                if (dataMap != null) {
                    Object respMsg = dataMap.get("P_RESP_MSG");
                    Object respMsgtoken = dataMap.get("P_TRA_LINK");

                    if (respMsg != null) {
                        System.out.println("TIRA P_RESP_MSG: " + respMsg.toString());

                        System.out.println("TIRA P_RESP_MSG: " + respMsgtoken.toString());
                        return respMsgtoken.toString().trim();
                    }
                }
            }

        } catch (HttpClientErrorException | HttpServerErrorException e) {
            log.error("TIRA API HTTP error: Status={}, Body={}", e.getStatusCode(), e.getResponseBodyAsString());
            System.out.println("TIRA API HTTP error: Status=" + e.getStatusCode() + ", Body=" + e.getResponseBodyAsString());
            e.printStackTrace();
        } catch (Exception e) {
            log.error("TIRA API call failed: {}", e.getMessage());
            System.out.println("TIRA API call failed: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    private String getTiraAuthToken(String tokenUrl, String userName, String password) {
        try {
            RestTemplate restTemplate = new RestTemplate();

            String credentials = userName + ":" + password;
            String base64Credentials = Base64.getEncoder()
                    .encodeToString(credentials.getBytes(StandardCharsets.UTF_8));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Basic " + base64Credentials);

            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("username", userName);
            requestBody.put("password", password);

            HttpEntity<Map<String, String>> entity = new HttpEntity<>(requestBody, headers);

            log.info("Calling Token API => URL: {}", tokenUrl);
            System.out.println("Calling Token API => URL: " + tokenUrl);

            ResponseEntity<String> response = restTemplate.exchange(
                    tokenUrl,
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            log.info("Token API Response => Status: {}", response.getStatusCode());
            System.out.println("Token API Response => Status: " + response.getStatusCode());

            if (response.getStatusCode() == HttpStatus.OK && StringUtils.isNotBlank(response.getBody())) {
                return response.getBody().trim();
            }

        } catch (HttpClientErrorException | HttpServerErrorException e) {
            log.error("Token API HTTP error: Status={}, Body={}", e.getStatusCode(), e.getResponseBodyAsString());
            System.out.println("Token API HTTP error: Status=" + e.getStatusCode() + ", Body=" + e.getResponseBodyAsString());
            e.printStackTrace();
        } catch (Exception e) {
            log.error("Token API call failed: {}", e.getMessage());
            System.out.println("Token API call failed: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public JasperDocumentRes downloadPolicyWording(String quoteNo) {
        log.info("Enter into downloadPolicyWording\n Args => " + quoteNo);
        JasperDocumentRes res = new JasperDocumentRes();
        try {
            List<AttachMentRes> attachMentList = jasperCustomeImple.downloadPolicyWorking(quoteNo);
            if (attachMentList != null) {
                HomePositionMaster homeData = homeRepo.findByQuoteNo(quoteNo);
                String jasperSaveLocation = policyReportPath.replaceAll("PolicyReport", "JsonFile") + quoteNo.replaceAll("[\\/:*?\"<>|]*", "") + ".pdf";
                File outputDir = new File(jasperSaveLocation).getParentFile();
                if (!outputDir.exists()) {
                    outputDir.mkdirs();
                }
                PDFMergerUtility mergerUtility = new PDFMergerUtility();
                mergerUtility.setDestinationFileName(jasperSaveLocation);
                mergerUtility.addSource(jasperSaveLocation);
                for (AttachMentRes attMap : attachMentList) {
                    OutputStream outputStream = new FileOutputStream(new File(jasperSaveLocation));
                    PdfReader pdfReader = new PdfReader(attMap.getDocloction());
                    PdfStamper pdfStamper = new PdfStamper(pdfReader, outputStream);
                    for (int i = 1; i <= pdfReader.getNumberOfPages(); i++) {
                        PdfContentByte contentByte = pdfStamper.getOverContent(i);
                        contentByte.beginText();
                        contentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_BOLD, BaseFont.CP1257, BaseFont.EMBEDDED), 12);
                        contentByte.setTextMatrix(125, pdfReader.getPageSizeWithRotation(i).getHeight() - 20);
                        contentByte.showText("Attached to and Forming Part of Policy No. " + (homeData.getPolicyNo()));
                        contentByte.endText();
                    }
                    pdfStamper.close();
                    File Attfile = new File(jasperSaveLocation);
                    mergerUtility.addSource(Attfile);
                }
                mergerUtility.mergeDocuments();
                String path = jasperSaveLocation;
                GetFileFromPath filePath = new GetFileFromPath(path);
                res.setPdfoutfile(filePath.call().getImgUrl());
                res.setPdfoutfilepath(path);
            }
            log.info("Exit into downloadPolicyWording");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }
    
  
    private JasperDocumentRes getGolfersQuotationPdf(String requestReferenceNo) {
        JasperDocumentRes res = new JasperDocumentRes();
        List<Error> errors = new ArrayList<>();

        try {
            // ── 1. Build request payload ──────────────────────────────────────────
            Map<String, String> payload = new HashMap<>();
            payload.put("docTemplateName", "golfers_quotation");
            payload.put("request_reference_no", requestReferenceNo);

            String requestBody = gson.toJson(payload);
            URL url = new URL("http://172.17.0.28:6060/api/reports/generate");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            conn.setDoOutput(true);
            conn.setConnectTimeout(10_000);   
            conn.setReadTimeout(60_000);     

            try (OutputStream os = conn.getOutputStream()) {
                os.write(requestBody.getBytes(StandardCharsets.UTF_8));
            }

            int httpStatus = conn.getResponseCode();

            InputStream is = (httpStatus >= 200 && httpStatus < 300)
                    ? conn.getInputStream()
                    : conn.getErrorStream();

            String responseBody;
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(is, StandardCharsets.UTF_8))) {
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) sb.append(line);
                responseBody = sb.toString();
            }
            conn.disconnect();

            
            @SuppressWarnings("unchecked")
            Map<String, Object> extRes = gson.fromJson(responseBody, Map.class);

            String status   = (String) extRes.get("status");
            String fileName = (String) extRes.get("fileName");
            String base64   = (String) extRes.get("base64Pdf");

            if (!"success".equalsIgnoreCase(status) || base64 == null) {
                errors.add(new Error("", "EXTERNAL_API_ERROR",
                        "External golfers_quotation API returned status: " + status
                        + " for RequestReferenceNo: " + requestReferenceNo));
                res.setErrorMessage(errors);
                res.setPdfoutfile(null);
                res.setPdfoutfilepath(null);
                return res;
            }

            String savePath = policyReportPath + requestReferenceNo.replaceAll("[\\/:*?\"<>|]*", "")
                    + File.separator + fileName;

            File saveDir = new File(savePath).getParentFile();
            if (!saveDir.exists()) saveDir.mkdirs();

            byte[] pdfBytes = Base64.getDecoder().decode(base64);
            try (FileOutputStream fos = new FileOutputStream(savePath)) {
                fos.write(pdfBytes);
            }
           
            res.setPdfoutfilepath(savePath);
            res.setPdfoutfile("data:application/pdf;base64,"+base64);          
            res.setErrorMessage(Collections.emptyList());

        } catch (Exception e) {
            log.error("Error calling golfers_quotation external API: ", e);
            errors.add(new Error("", "EXCEPTION", e.getMessage()));
            res.setErrorMessage(errors);
            res.setPdfoutfile(null);
            res.setPdfoutfilepath(null);
        }

        return res;
    }

    private JasperDocumentRes getGolfersScheduledPdf(String policyNumber) {
        JasperDocumentRes res = new JasperDocumentRes();
        List<Error> errors = new ArrayList<>();

        try {
            // ── 1. Build request payload ──────────────────────────────────────────
            Map<String, String> payload = new HashMap<>();
            payload.put("docTemplateName", "golfers_schedule");
            payload.put("policyno", policyNumber);

            String requestBody = gson.toJson(payload);
            URL url = new URL(scheduledPdfURl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            conn.setDoOutput(true);
            conn.setConnectTimeout(10_000);
            conn.setReadTimeout(60_000);

            try (OutputStream os = conn.getOutputStream()) {
                os.write(requestBody.getBytes(StandardCharsets.UTF_8));
            }

            int httpStatus = conn.getResponseCode();

            InputStream is = (httpStatus >= 200 && httpStatus < 300)
                    ? conn.getInputStream()
                    : conn.getErrorStream();

            String responseBody;
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(is, StandardCharsets.UTF_8))) {
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) sb.append(line);
                responseBody = sb.toString();
            }
            conn.disconnect();
            System.out.println("Scheduled PDF External Call Res: "+responseBody);

            @SuppressWarnings("unchecked")
            Map<String, Object> extRes = gson.fromJson(responseBody, Map.class);

            String status   = (String) extRes.get("status");
            String fileName = (String) extRes.get("fileName");
            String base64   = (String) extRes.get("base64Pdf");

            System.out.println("Scheduled PDF External Call Res");
            if (!"success".equalsIgnoreCase(status) || base64 == null) {
                errors.add(new Error("", "EXTERNAL_API_ERROR",
                        "External golfers_schedule API returned status: " + status
                                + " for PolicyNo: " + policyNumber));
                res.setErrorMessage(errors);
                res.setPdfoutfile(null);
                res.setPdfoutfilepath(null);
                return res;
            }

            String savePath = policyReportPath + policyNumber.replaceAll("[\\/:*?\"<>|]*", "")
                    + File.separator + fileName;

            File saveDir = new File(savePath).getParentFile();
            if (!saveDir.exists()) saveDir.mkdirs();

            byte[] pdfBytes = Base64.getDecoder().decode(base64);
            try (FileOutputStream fos = new FileOutputStream(savePath)) {
                fos.write(pdfBytes);
            }

            res.setPdfoutfilepath(savePath);
            res.setPdfoutfile("data:application/pdf;base64,"+base64);
            res.setErrorMessage(Collections.emptyList());

        } catch (Exception e) {
            log.error("Error calling golfers_quotation external API: ", e);
            errors.add(new Error("", "EXCEPTION", e.getMessage()));
            res.setErrorMessage(errors);
            res.setPdfoutfile(null);
            res.setPdfoutfilepath(null);
        }

        return res;
    }
    
}