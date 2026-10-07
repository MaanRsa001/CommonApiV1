package com.maan.eway.viewAll.service.impl;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.docx4j.Docx4J;
import org.docx4j.TraversalUtil;
import org.docx4j.XmlUtils;
import org.docx4j.fonts.IdentityPlusMapper;
import org.docx4j.fonts.Mapper;
import org.docx4j.fonts.PhysicalFonts;
import org.docx4j.model.datastorage.migration.VariablePrepare;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.Part;
import org.docx4j.openpackaging.parts.PartName;
import org.docx4j.openpackaging.parts.WordprocessingML.HeaderPart;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.openpackaging.parts.relationships.RelationshipsPart;
import org.docx4j.relationships.Relationship;
import org.docx4j.utils.SingleTraversalUtilVisitorCallback;
import org.docx4j.utils.TraversalUtilVisitor;
import org.docx4j.wml.P;
import org.docx4j.wml.R;
import org.docx4j.wml.Tbl;
import org.docx4j.wml.Text;
import org.docx4j.wml.Tr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.maan.eway.bean.FirstLossPayee;
import com.maan.eway.bean.HomePositionMaster;
import com.maan.eway.bean.InsuranceCompanyMaster;
import com.maan.eway.bean.LoginUserInfo;
import com.maan.eway.bean.MotorDataDetails;
import com.maan.eway.bean.PersonalInfo;
import com.maan.eway.bean.PolicyCoverData;
import com.maan.eway.bean.PolicyDrcrDetail;
import com.maan.eway.bean.SectionDataDetails;
import com.maan.eway.common.res.CommonRes;
import com.maan.eway.repository.FirstLossPayeeRepository;
import com.maan.eway.repository.HomePositionMasterRepository;
import com.maan.eway.repository.InsuranceCompanyMasterRepository;
import com.maan.eway.repository.LoginUserInfoRepository;
import com.maan.eway.repository.MotorDataDetailsRepository;
import com.maan.eway.repository.PersonalInfoRepository;
import com.maan.eway.repository.PolicyCoverDataRepository;
import com.maan.eway.repository.PolicyDrcrDetailRepository;
import com.maan.eway.repository.SectionDataDetailsRepository;
import com.maan.eway.viewAll.dto.UgandaDebitNotePIRes;
import com.maan.eway.viewAll.dto.viewAllReq;
import com.maan.eway.viewAll.service.UgandaDebiteJsonService;

import jakarta.xml.bind.JAXBElement;
@Service
public class UgandaDebiteJsonServiceImpl implements UgandaDebiteJsonService {
	
	@Autowired
	private HomePositionMasterRepository homerepo;
	
	@Autowired
	private PersonalInfoRepository piRepo;
	
	@Autowired
	private MotorDataDetailsRepository motorRepo;
	
	@Autowired
	private LoginUserInfoRepository logrepo;
	
	@Autowired
	private PolicyCoverDataRepository policyCDRepo;
	
	@Autowired
	private SectionDataDetailsRepository sectionDataRepo;
	
	@Autowired
	private FirstLossPayeeRepository firstRepo;
	@Autowired
	private InsuranceCompanyMasterRepository incomRepo;
	
	@Autowired
	private PolicyDrcrDetailRepository policydrcr;
	
	@Value("${file.pdfDirectoryPath}")
	private String directoryPath;
	
	
	private Logger log = LogManager.getLogger(UgandaDebiteJsonServiceImpl.class);
	@Override
	public CommonRes jsonFrame(viewAllReq req) {
		CommonRes res = new CommonRes();
		LinkedHashMap<String,Object> result = new LinkedHashMap<String,Object>();
		LinkedHashMap<String,Object> policyresult = new LinkedHashMap<String,Object>();
		LinkedHashMap<String,Object> premium = new LinkedHashMap<String,Object>();
		LinkedHashMap<String,Object> companyInfo = new LinkedHashMap<String,Object>();
		LinkedHashMap<String,Object> endt = new LinkedHashMap<String,Object>();
		try
		{
			List<PolicyDrcrDetail> policyDrcr = policydrcr.findByQuoteNo(req.getQuoteNo());
			SimpleDateFormat displayFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			UgandaDebitNotePIRes piRes = new UgandaDebitNotePIRes();
			HomePositionMaster home = homerepo.findByQuoteNo(req.getQuoteNo());
			List<PolicyCoverData> coverData = policyCDRepo.findByQuoteNoOrderByVehicleIdAsc(req.getQuoteNo());
			List<InsuranceCompanyMaster> comName =incomRepo.findTopByCompanyIdOrderByAmendIdDesc(home.getCompanyId());
			List<MotorDataDetails> vehicleDetails =null;
			 List<SectionDataDetails> sectionList = null;
			if("5".equalsIgnoreCase(home.getProductId().toString())) {
			 vehicleDetails = motorRepo.findByQuoteNoOrderByVehicleIdAsc(req.getQuoteNo()).stream().filter(f -> !f.getStatus().equalsIgnoreCase("D")).collect(Collectors.toList());
			}
			else
			{
				 sectionList = sectionDataRepo.findByQuoteNoAndStatusNotOrderByRiskIdAsc(home.getQuoteNo(), "D");
			}
			LoginUserInfo login = new LoginUserInfo();
			login = logrepo.findByLoginId(home.getLoginId());
			String customerId = home.getCustomerId();
			List<PersonalInfo> pi = piRepo.findByCustomerIdAndCompanyId(customerId, home.getCompanyId());
			if(pi!=null && !pi.isEmpty())
			{
				PersonalInfo single = pi.get(0);
				result.put("Insured Name", single.getClientName().toUpperCase());
				result.put("Address details", single.getAddress1());
				result.put("Customer ID", customerId);
				result.put("Vat RegNo", single.getIdNumber());
				result.put("Invoice Number", home.getDebitNoteNo());
				result.put("Invoice Date", displayFormat.format(new Date()));
				result.put("Mobile", single.getMobileNo1());
				result.put("E-mail ID", single.getEmail1());
				result.put("ProductIt", home.getProductId());
				if(vehicleDetails!=null && !vehicleDetails.isEmpty())
				{
					List<MotorDataDetails> collateralDetails = vehicleDetails.stream()
							.filter(f -> "Y".equalsIgnoreCase(Objects.requireNonNullElse(f.getCollateralYn(), "")))
							.collect(Collectors.toList());
					if(collateralDetails!=null && !collateralDetails.isEmpty()) {
						result.put("CollateralName", collateralDetails.get(0).getCollateralName());
					}
				}
				else
				{
					List<FirstLossPayee> flist = firstRepo.findByRequestReferenceNo(home.getRequestReferenceNo());
					if(flist!=null && !flist.isEmpty()) {
						result.put("CollateralName", flist.get(0).getCollateralName());
					}
				}
				piRes.setPiInfo(result);
			}
			policyresult.put("Policy Number", home.getPolicyNo());
			policyresult.put("InceptionDate", home.getInceptionDate() != null ? displayFormat.format(home.getInceptionDate()) : "");
			policyresult.put("ExpiryDate", home.getExpiryDate() != null ? displayFormat.format(home.getExpiryDate()) : "");
			policyresult.put("EffectiveDate", home.getEffectiveDate() != null ? displayFormat.format(home.getEffectiveDate()) : "");
			policyresult.put("QuoteNo", home.getQuoteNo());
			policyresult.put("Intermediary Name", login.getUserName());
			policyresult.put("CoreAppCode", login.getCoreAppBrokerCode());
			policyresult.put("Currency", home.getCurrency());
			if(vehicleDetails!=null && !vehicleDetails.isEmpty())
			{
			policyresult.put("Policy Type",home.getProductName()+ " : " + vehicleDetails.get(0).getMotorUsageDesc() +" "+ vehicleDetails.get(0).getSectionName());
			}
			else
			{
				policyresult.put("Policy Type",home.getProductName()+ " : " + sectionList.get(0).getSectionDesc());
			}
			piRes.setPolicyInfo(policyresult);
			java.text.DecimalFormat formatter = new java.text.DecimalFormat("#,##0.00");
			Map<Integer, Integer> taxOrder = new HashMap<>();
			taxOrder.put(10, 1); 
			taxOrder.put(9, 2); 
			taxOrder.put(12, 3); 
			taxOrder.put(11, 4);
		
			Map<Integer, BigDecimal> taxGroupedMap = coverData.stream()
			        .filter(tax -> tax.getTaxId() != null)
			        .filter(c -> "T".equalsIgnoreCase(c.getCoverageType()))
			        .collect(Collectors.groupingBy(
			                PolicyCoverData::getTaxId,
			                LinkedHashMap::new,
			                Collectors.reducing(
			                        BigDecimal.ZERO,
			                        tax -> tax.getTaxAmount() == null
			                                ? BigDecimal.ZERO
			                                : tax.getTaxAmount(),
			                        BigDecimal::add
			                )
			        ));
			List<Map.Entry<Integer, BigDecimal>> sortedTaxList = taxGroupedMap.entrySet()
			        .stream()
			        .sorted(Comparator.comparingInt(
			                entry -> taxOrder.getOrDefault(
			                        entry.getKey(),
			                        Integer.MAX_VALUE
			                )
			        ))
			        .toList();
			int index = 1;
			 BigDecimal premiumExclud = coverData.stream()
		                .filter(c -> !"T".equalsIgnoreCase(c.getCoverageType()))
		                .filter(c -> c.getSectionId() != null && c.getSectionId() != 99999)
		                .map(c -> c.getPremiumExcludedTaxLc() == null
		                        ? BigDecimal.ZERO
		                        : c.getPremiumExcludedTaxLc())
		                .reduce(BigDecimal.ZERO, BigDecimal::add);
			 premium.put("PremiumExcludedTaxLcVat", formatter.format(premiumExclud));
			 
			 BigDecimal premiumInclde=BigDecimal.ZERO;
			 if (policyDrcr != null && !policyDrcr.isEmpty()) {
				  premiumInclde = policyDrcr.stream()
					        .filter(d -> "DR".equalsIgnoreCase(d.getDrcrFlag()))
					        .map(PolicyDrcrDetail::getAmountFc)
					        .filter(Objects::nonNull)
					        .reduce(BigDecimal.ZERO, BigDecimal::add);
			 }else
			 {
				  premiumInclde = coverData.stream()
			                .filter(c -> !"T".equalsIgnoreCase(c.getCoverageType()))
			                .filter(c -> c.getSectionId() != null && c.getSectionId() != 99999)
			                .map(c -> c.getPremiumIncludedTaxLc() == null
			                        ? BigDecimal.ZERO
			                        : c.getPremiumIncludedTaxLc())
			                .reduce(BigDecimal.ZERO, BigDecimal::add);
			 }
			 
			 premium.put("PremiumIncludedTaxVat", formatter.format(premiumInclde));
			// premium.put("PremiumIncludedTaxVat", formatter.format(premiumInclde));  
			 BigDecimal totalTaxAmount = BigDecimal.ZERO;
			 for (Map.Entry<Integer, BigDecimal> entry : sortedTaxList) {

				    Integer taxId = entry.getKey();
				    BigDecimal totalAmount = entry.getValue();
				    totalTaxAmount = totalTaxAmount.add(totalAmount);
				    // Get first record for this taxId
				    PolicyCoverData tax = coverData.stream()
				            .filter(c -> taxId.equals(c.getTaxId()))
				            .findFirst()
				            .orElse(null);

				    if (tax == null) {
				        continue;
				    }

				    String taxName = tax.getTaxDesc() == null
				            ? ""
				            : tax.getTaxDesc();
				    if ("P".equalsIgnoreCase(tax.getTaxCalcType())
				            && tax.getTaxRate() != null) {

				        taxName += " "
				                + tax.getTaxRate()
				                        .stripTrailingZeros()
				                        .toPlainString()
				                + "%";
				    }

				    premium.put("Tax" + index, taxName);
				    premium.put("TaxAmount" + index, formatter.format(totalAmount));

				    index++;
				}
			 premium.put("TotalTaxamount" , formatter.format(totalTaxAmount));
			 String convertAmountToWords = ViewAllWithLableServiceImpl.convertAmountToWords(premiumInclde);
			 premium.put("convertAmountToWords" , convertAmountToWords);
			 premium.put("PremiumProduct" , "Premium" + " - "+ home.getProductId());
			piRes.setPremiumInfo(premium);
			if(comName!=null && !comName.isEmpty())
			{
				InsuranceCompanyMaster company = comName.get(0);
				companyInfo.put("CompanyName", company.getCompanyName());
				companyInfo.put("Company Address", company.getCompanyAddress() +" Tel :"+ company.getCompanyPhone());
				companyInfo.put("EmailAndWebSite ", "Email:" +company.getCompanyEmail() + " Website:"+ company.getCompanyWebsite());
				companyInfo.put("Signature", StringUtils.isNotBlank(company.getSignature())? company.getSignature() : "");
				
			}
			piRes.setCompanyInfo(companyInfo);
			if(StringUtils.isNoneBlank(home.getEndtTypeId()))
			{
				endt.put("EndtPolicyNo", home.getPolicyNo());
				endt.put("EndtTypeId", home.getEndtTypeId());
				endt.put("EndtDesc", home.getEndtTypeDesc());
				
			}
			piRes.setEndtInfo(endt);
			res.setCommonResponse(piRes);			
		}catch(Exception e)
		{
			e.printStackTrace();
			log.error("Exception in jsonFrame ===> {}", e.getMessage());
		}
		return res;
	}
	
	@Override
	public CommonRes generateDebitNoteUganda(viewAllReq req) {

		CommonRes resp = new CommonRes();

		try {

			// ============================================================
			// 1. Validate Request
			// ============================================================

			if (req == null || req.getQuoteNo() == null || req.getQuoteNo().trim().isEmpty()) {

				resp.setIsError(true);
				resp.setMessage("QuoteNo is required");
				return resp;
			}

			String quoteNo = req.getQuoteNo().trim();

			log.info("Generating Uganda Debit Note for QuoteNo : {}", quoteNo);

			// ============================================================
			// 2. Call Existing Uganda Tax Service
			// ============================================================

			CommonRes taxResponse = jsonFrame(req);

			if (taxResponse == null) {

				resp.setIsError(true);
				resp.setMessage("No response received from Uganda Tax Service for QuoteNo: " + quoteNo);

				return resp;
			}

			if (Boolean.TRUE.equals(taxResponse.getIsError())) {

				resp.setIsError(true);
				resp.setMessage(
						taxResponse.getMessage() != null ? taxResponse.getMessage() : "Uganda Tax Service failed");

				return resp;
			}

			if (taxResponse.getCommonResponse() == null) {

				resp.setIsError(true);
				resp.setMessage("No data found for QuoteNo: " + quoteNo);

				return resp;
			}

			// ============================================================
			// 3. Get UgandaDebitNotePIRes
			// ============================================================

			Object commonResponse = taxResponse.getCommonResponse();

			log.info("COMMON RESPONSE TYPE = {}", commonResponse.getClass().getName());

			log.info("COMMON RESPONSE = {}", commonResponse);

			if (!(commonResponse instanceof UgandaDebitNotePIRes)) {

				resp.setIsError(true);
				resp.setMessage(
						"Invalid response type from Uganda Tax Service. Expected UgandaDebitNotePIRes but received "
								+ commonResponse.getClass().getName());

				return resp;
			}

			UgandaDebitNotePIRes apiResult = (UgandaDebitNotePIRes) commonResponse;

			// ============================================================
			// 4. Get Individual Sections
			// ============================================================

			LinkedHashMap<String, Object> personalInfo = apiResult.getPiInfo();

			LinkedHashMap<String, Object> policyInfo = apiResult.getPolicyInfo();

			LinkedHashMap<String, Object> premiumInfo = apiResult.getPremiumInfo();

			LinkedHashMap<String, Object> companyInfo = apiResult.getCompanyInfo();

			LinkedHashMap<String, Object> endosInfo = apiResult.getEndtInfo();

			// ============================================================
			// 5. Validate Response Data
			// ============================================================

			if (personalInfo == null) {
				personalInfo = new LinkedHashMap<>();
			}

			if (policyInfo == null) {
				policyInfo = new LinkedHashMap<>();
			}

			if (premiumInfo == null) {
				premiumInfo = new LinkedHashMap<>();
			}

			if (companyInfo == null) {
				companyInfo = new LinkedHashMap<>();
			}

			if (endosInfo == null) {
				endosInfo = new LinkedHashMap<>();
			}

			if (personalInfo.isEmpty() && policyInfo.isEmpty() && premiumInfo.isEmpty()) {

				resp.setIsError(true);
				resp.setMessage("No debit note data found for QuoteNo: " + quoteNo);

				return resp;
			}

			// ============================================================
			// 6. Policy Period
			// ============================================================

			String inceptionDate = nvl(policyInfo.get("InceptionDate"));

			String expiryDate = nvl(policyInfo.get("ExpiryDate"));

			// Build the policy period from RAW (un-escaped) trimmed dates, then
			// escape the whole composed string once. Escaping twice, or escaping
			// only parts, is how stray artifacts creep into re-escaped XML.
			String policyPeriod = escapeXml(formatPolicyPeriodRaw(trimDate(rawNvl(policyInfo.get("InceptionDate"))),
					trimDate(rawNvl(policyInfo.get("ExpiryDate")))));

			// ============================================================
			// 7. Printed Date
			// ============================================================

			String printedDate = escapeXml(new SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy").format(new Date()));

			// ============================================================
			// 8. Currency
			// ============================================================

			String curCode = nvl(policyInfo.get("Currency"));
			String curCodeRaw = rawNvl(policyInfo.get("Currency"));

			Map<String, String> context = new HashMap<>();

			// ============================================================
			// PERSONAL INFO
			// ============================================================

			context.put("insuredName", nvl(personalInfo.get("Insured Name")));

			context.put("coInsuredName", nvl(personalInfo.get("CollateralName")));

			context.put("invoiceNumber", nvl(personalInfo.get("Invoice Number")));

			context.put("invoiceDate", nvl(personalInfo.get("Invoice Date")));

			context.put("addressDetails", nvl(personalInfo.get("Address details")));

			context.put("vatRegNo", nvl(personalInfo.get("Vat RegNo")));

			context.put("customerId", nvl(personalInfo.get("Customer ID")));

			// ============================================================
			// POLICY INFO
			// ============================================================

			context.put("policyNo", nvl(policyInfo.get("Policy Number")));

			context.put("intermediaryCode", nvl(policyInfo.get("CoreAppCode")));

			context.put("policyPeriod", policyPeriod);

			context.put("insuredCode", nvl(personalInfo.get("Customer ID")));

			context.put("policyType", nvl(policyInfo.get("Policy Type")));

			context.put("rmCode", "");

			// ============================================================
			// ENDORSEMENT INFO
			// ============================================================

			context.put("endorsementNo", nvl(endosInfo.get("EndtPolicyNo")));

			context.put("endtCode", nvl(endosInfo.get("EndtTypeId")));

			context.put("endorsementDesc", nvl(endosInfo.get("EndtDesc")));

			// ============================================================
			// PREMIUM / TAX INFO
			// ============================================================

			context.put("CurCode", curCode);

			context.put("premiumAmount", nvl(premiumInfo.get("PremiumExcludedTaxLcVat")));

			context.put("Tax1", nvl(premiumInfo.get("Tax1")));

			context.put("Tax1Amount", nvl(premiumInfo.get("TaxAmount1")));

			context.put("Tax2", nvl(premiumInfo.get("Tax2")));

			context.put("Tax2Amount", nvl(premiumInfo.get("TaxAmount2")));

			context.put("Tax3", nvl(premiumInfo.get("Tax3")));

			context.put("Tax3Amount", nvl(premiumInfo.get("TaxAmount3")));

			context.put("Tax4", nvl(premiumInfo.get("Tax4")));

			context.put("Tax4Amount", nvl(premiumInfo.get("TaxAmount4")));

			// Build concatenations from RAW values first, escape once at the end —
			// never escape a piece, concatenate, then escape again (double-escaping
			// turns "&amp;" into "&amp;amp;").
			context.put("invoiceTotal", escapeXml(curCodeRaw + " " + rawNvl(premiumInfo.get("PremiumIncludedTaxVat"))));

			context.put("taxesTotal", nvl(premiumInfo.get("TotalTaxamount")));

			context.put("amountInWords", escapeXml(curCodeRaw + " " + rawNvl(premiumInfo.get("convertAmountToWords"))));

			// ============================================================
			// COMPANY INFO
			// ============================================================

			context.put("companyName", nvl(companyInfo.get("CompanyName")));

			context.put("companyAddress", nvl(companyInfo.get("Company Address")));

			context.put("emailAndWebSite", nvl(companyInfo.get("EmailAndWebSite ")));

			context.put("signature", nvl(companyInfo.get("Signature")));

			// ============================================================
			// FOOTER
			// ============================================================

			context.put("preparedBy", "");

			context.put("approvedBy", "");

			context.put("printedBy", nvl(policyInfo.get("Intermediary Name")));

			context.put("printedDate", printedDate);

			// ============================================================
			// 10. Log Context
			// ============================================================

			log.info("Uganda Debit Note Context for QuoteNo {} : {}", quoteNo, context);

			// ============================================================
			// 11. Load Word Template
			// ============================================================

			InputStream in = getClass().getResourceAsStream("/template/DebitPdf_Uganda_Draft.docx");

			if (in == null) {

				throw new FileNotFoundException("DebitPdf_Uganda_Draft.docx not found in /template/");
			}

			try (InputStream templateInputStream = in) {

				byte[] templateBytes = templateInputStream.readAllBytes();

				log.info("Uganda DOCX template size: {} bytes", templateBytes.length);

				if (templateBytes.length < 4) {
					log.info("DOCX template is empty or corrupted");
				}

				log.info("DOCX header: {} {} {} {}", String.format("%02X", templateBytes[0]),
						String.format("%02X", templateBytes[1]), String.format("%02X", templateBytes[2]),
						String.format("%02X", templateBytes[3]));

				InputStream docxStream = new ByteArrayInputStream(templateBytes);

				WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.load(docxStream);

				// ========================================================
				// 12. Font Mapper
				// ========================================================

				PhysicalFonts.discoverPhysicalFonts();

				Mapper fontMapper = new IdentityPlusMapper();

				if (PhysicalFonts.get("Carlito") != null) {
					fontMapper.put("Calibri", PhysicalFonts.get("Carlito"));
				}

				if (PhysicalFonts.get("Arial") != null) {
					fontMapper.put("Arial", PhysicalFonts.get("Arial"));
				}

				if (PhysicalFonts.get("Times New Roman") != null) {
					fontMapper.put("Times New Roman", PhysicalFonts.get("Times New Roman"));
				}

				wordMLPackage.setFontMapper(fontMapper);

				// ========================================================
				// 13. Prepare Variables
				// ========================================================

				VariablePrepare.prepare(wordMLPackage);

				// ========================================================
				// 14. Replace Variables in Body
				// ========================================================
				String productId = nvl(personalInfo.get("ProductIt"));
				if (!"5".equals(productId.trim())) {
				    removeTax4Row(wordMLPackage);
				}

				MainDocumentPart documentPart = wordMLPackage.getMainDocumentPart();

				documentPart.variableReplace(context);

				// ========================================================
				// 15. Replace Variables in Headers
				// ========================================================

				replaceVariablesInHeaders(wordMLPackage, context);

				// ========================================================
				// 16. Convert DOCX to PDF
				// ========================================================

				byte[] pdfArray = convertDocxToPdf(wordMLPackage);

				// ========================================================
				// 17. File Name
				// ========================================================

				String quoteNoSafe = quoteNo.replaceAll("[\\\\/:*?\"<>|]", "_");

				String fileName = "DebitNote_Uganda_" + quoteNoSafe + ".pdf";

				// ========================================================
				// 18. Output Path
				// ========================================================

				Path outputPath = Paths.get(directoryPath, fileName);

				Files.createDirectories(outputPath.getParent());

				// ========================================================
				// 19. Write PDF
				// ========================================================

				Files.write(outputPath, pdfArray);

				log.info("DebitNote Uganda PDF Generated Successfully : {}", outputPath);

				// ========================================================
				// 20. Convert PDF to Base64
				// ========================================================

				byte[] fileBytes = Files.readAllBytes(outputPath);

				String base64 = Base64.getEncoder().encodeToString(fileBytes);

				String mimeType = "application/pdf";

				String imgData = "data:" + mimeType + ";base64," + base64;

				// ========================================================
				// 21. Response
				// ========================================================

				Map<String, Object> docRes = new HashMap<>();

				docRes.put("FileName", outputPath.toString());

				docRes.put("Base64", imgData);

				resp.setCommonResponse(docRes);

				resp.setErrorMessage(null);

				resp.setIsError(false);

				resp.setMessage("Debit Note PDF generated successfully");

				return resp;
			}

		} catch (Exception e) {

			log.error("Exception in generateDebitNoteUganda ===> {}", e.getMessage(), e);

			resp.setCommonResponse(null);

			resp.setIsError(true);

			resp.setMessage("Failed to generate Uganda Debit Note PDF");

			return resp;
		}
	}
	
	// ========================================================================
	// Convert DOCX to PDF
	// ========================================================================
	public byte[] convertDocxToPdf(WordprocessingMLPackage wordMLPackage) throws Exception {
		ByteArrayOutputStream pdfOut = new ByteArrayOutputStream();
		Docx4J.toPDF(wordMLPackage, pdfOut);
		return pdfOut.toByteArray();
	}

	// ========================================================================
	// Null Value Handler (returns RAW, un-escaped trimmed string)
	// ========================================================================
	private String rawNvl(Object val) {
		if (val == null) {
			return "";
		}
		return val.toString().trim();
	}

	// ========================================================================
	// Null Value Handler + XML escape.
	// Use this (not rawNvl) for every value that goes directly into the
	// docx4j `context` map, since variableReplace() substitutes the string
	// straight into XML text nodes without escaping it itself.
	// ========================================================================
	private String nvl(Object val) {
		return escapeXml(rawNvl(val));
	}

	// ========================================================================
	// XML-escape a string so it is safe to inject into XML text content.
	// Order matters: '&' MUST be escaped first, otherwise the '&' produced
	// by escaping '<', '>', etc. would itself get re-escaped.
	// ========================================================================
	private String escapeXml(String value) {
		if (value == null || value.isEmpty()) {
			return "";
		}
		return value.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;")
				.replace("'", "&apos;");
	}

	// ========================================================================
	// Policy Period (raw, un-escaped — caller escapes the composed result)
	// ========================================================================
	private String formatPolicyPeriodRaw(String from, String to) {
		return from + "   To :  " + to;
	}

	// ========================================================================
	// Trim Date
	// ========================================================================
	private String trimDate(String dateTime) {

		if (dateTime == null || dateTime.trim().isEmpty()) {
			return "";
		}

		return dateTime.trim();
	}

	// ========================================================================
	// Build a single "contact line" for the header out of whichever of phone /
	// email / website are non-empty, e.g. "Tel: 256... | Email: info@... | www...".
	// ========================================================================
	private String buildContactLine(String phone, String email, String website) {
		List<String> parts = new ArrayList<>();
		if (phone != null && !phone.isEmpty()) {
			parts.add("Tel: " + phone);
		}
		if (email != null && !email.isEmpty()) {
			parts.add("Email: " + email);
		}
		if (website != null && !website.isEmpty()) {
			parts.add(website);
		}
		return String.join(" | ", parts);
	}
	private String findPremiumAmount(List<Map<String, Object>> premiumDetails, String narrationContains) {
		if (premiumDetails == null || narrationContains == null) {
			return "";
		}
		String needle = narrationContains.toLowerCase();
		for (Map<String, Object> row : premiumDetails) {
			Object narrationObj = row.get("narration");
			if (narrationObj == null) {
				continue;
			}
			String narration = narrationObj.toString().trim().toLowerCase();
			if (narration.startsWith(needle) || narration.contains(needle)) {
				Object amount = row.get("amount");
				return amount == null ? "" : amount.toString().trim();
			}
		}
		return "";
	}
	private boolean hasContent(Object val) {
		if (val == null) {
			return false;
		}
		if (val instanceof java.util.Collection) {
			return !((java.util.Collection<?>) val).isEmpty();
		}
		return !val.toString().trim().isEmpty() && !"[]".equals(val.toString().trim());
	}
	@SuppressWarnings("unchecked")
	private LinkedHashMap<String, Object> toMap(Object jsonStringField) {
		if (jsonStringField == null) {
			return new LinkedHashMap<>();
		}
		if (jsonStringField instanceof LinkedHashMap) {
			return (LinkedHashMap<String, Object>) jsonStringField;
		}
		ObjectMapper mapper = new ObjectMapper();
		try {
			if (jsonStringField instanceof Map) {
				return mapper.convertValue(jsonStringField, new TypeReference<LinkedHashMap<String, Object>>() {
				});
			}
			if (jsonStringField instanceof String) {
				String s = ((String) jsonStringField).trim();
				if (s.isEmpty()) {
					return new LinkedHashMap<>();
				}
				return mapper.readValue(s, new TypeReference<LinkedHashMap<String, Object>>() {
				});
			}
			// Typed POJO fallback.
			return mapper.convertValue(jsonStringField, new TypeReference<LinkedHashMap<String, Object>>() {
			});
		} catch (Exception e) {
			log.warn("Could not normalize JsonString into a Map: {}", e.getMessage());
			return new LinkedHashMap<>();
		}
	}

	private void replaceVariablesInHeaders(WordprocessingMLPackage wordMLPackage, Map<String, String> context)
			throws Exception {
		RelationshipsPart rp = wordMLPackage.getMainDocumentPart().getRelationshipsPart();
		if (rp == null || rp.getRelationships() == null || rp.getRelationships().getRelationship() == null) {
			return;
		}
		List<Relationship> rels = rp.getRelationships().getRelationship();
		for (Relationship r : rels) {
			if (r.getType() != null && r.getType().endsWith("/header")) {
				try {
					String target = r.getTarget();
					String partNameStr;
					if (target.startsWith("/")) {
						partNameStr = target;
					} else {
						partNameStr = "/word/" + target;
					}
					Part part = wordMLPackage.getParts().get(new PartName(partNameStr));
					if (part instanceof HeaderPart) {
						mergeRunsAndReplace((HeaderPart) part, context);
					}
				} catch (Exception ex) {
					log.warn("Could not replace variables in header part : {}", ex.getMessage());
				}
			}
		}
	}
	
	private void mergeRunsAndReplace(HeaderPart headerPart, Map<String, String> context) throws Exception {
	    List<Object> paragraphs = new ArrayList<>();

	    TraversalUtilVisitor<P> visitor = new TraversalUtilVisitor<P>() {
	        public void apply(P p) {
	            paragraphs.add(p);
	        }
	    };

	    new TraversalUtil(headerPart.getContent(), new SingleTraversalUtilVisitorCallback(visitor));

	    for (Object o : paragraphs) {
	        P paragraph = (P) o;
	        List<Text> textElements = new ArrayList<>();
	        StringBuilder combined = new StringBuilder();
	        collectTextElements(paragraph.getContent(), textElements, combined);

	        if (textElements.isEmpty() || !combined.toString().contains("${")) {
	            continue;
	        }

	        String replaced = combined.toString();
	        for (Map.Entry<String, String> entry : context.entrySet()) {
	            replaced = replaced.replace("${" + entry.getKey() + "}", entry.getValue() == null ? "" : entry.getValue());
	        }

	        textElements.get(0).setValue(replaced);
	        textElements.get(0).setSpace("preserve");
	        for (int i = 1; i < textElements.size(); i++) {
	            textElements.get(i).setValue("");
	        }
	    }
	}
		private void collectTextElements(List<Object> runContent, List<Text> out, StringBuilder combined) {
			for (Object o : runContent) {
				Object unwrapped = XmlUtils.unwrap(o);
				if (unwrapped instanceof R) {
					for (Object rc : ((R) unwrapped).getContent()) {
						Object runChild = XmlUtils.unwrap(rc);
						if (runChild instanceof Text) {
							Text t = (Text) runChild;
							combined.append(t.getValue());
							out.add(t);
						}
					}
				}
			}
		}
		
		private void removeTax4Row(WordprocessingMLPackage wordMLPackage) {
		    try {
		        MainDocumentPart documentPart = wordMLPackage.getMainDocumentPart();

		        List<Object> tables = new ArrayList<>();

		        TraversalUtil.CallbackImpl callback = new TraversalUtil.CallbackImpl() {
		            @Override
		            public List<Object> apply(Object o) {
		                if (o instanceof Tbl) {
		                    tables.add(o);
		                }
		                return null;
		            }
		        };

		        new TraversalUtil(documentPart.getContent(), callback);

		        for (Object tableObj : tables) {
		            Tbl table = (Tbl) tableObj;
		            List<Object> rows = table.getContent();
		            Object rowToRemove = null;

		            for (Object rowObj : rows) {
		                Tr row = null;

		                if (rowObj instanceof Tr) {
		                    row = (Tr) rowObj;
		                } else if (rowObj instanceof JAXBElement) {
		                    Object value = ((JAXBElement<?>) rowObj).getValue();
		                    if (value instanceof Tr) {
		                        row = (Tr) value;
		                    }
		                }

		                if (row == null) continue;

		                String rowText = getRowText(row);

		                if (rowText.contains("${Tax4}") || rowText.contains("${Tax4Amount}")) {
		                    rowToRemove = rowObj;
		                    break;
		                }
		            }

		            if (rowToRemove != null) {
		                rows.remove(rowToRemove);
		                log.info("Tax4 row removed for ProductId != 5");
		                break;
		            }
		        }

		    } catch (Exception e) {
		        log.warn("Could not remove Tax4 row: {}", e.getMessage());
		    }
		}
		
		private String getRowText(Tr row) {

		    StringBuilder text =
		            new StringBuilder();

		    TraversalUtil.CallbackImpl callback =
		            new TraversalUtil.CallbackImpl() {

		                @Override
		                public List<Object> apply(Object o) {

		                    if (o instanceof Text) {

		                        Text t = (Text) o;

		                        if (t.getValue() != null) {
		                            text.append(t.getValue());
		                        }
		                    }

		                    return null;
		                }
		            };

		    new TraversalUtil(row, callback);

		    return text.toString();
		}

}
