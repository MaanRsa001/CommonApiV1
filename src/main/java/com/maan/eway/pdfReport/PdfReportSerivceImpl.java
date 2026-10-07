package com.maan.eway.pdfReport;

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
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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
import com.maan.eway.common.res.CommonRes;
import com.maan.eway.jasper.req.JasperDocumentReq;
import com.maan.eway.jasper.res.JasperDocumentRes;
import com.maan.eway.jasper.service.JasperService;
import com.maan.eway.viewAll.dto.UgandaDebitNotePIRes;
import com.maan.eway.viewAll.dto.viewAllReq;
import com.maan.eway.viewAll.service.UgandaDebiteJsonService;

import jakarta.xml.bind.JAXBElement;

import java.text.NumberFormat;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PdfReportSerivceImpl implements PdfReportService {
	@Autowired
	private UgandaDebiteJsonService ugandaService;
	// TODO: replace JasperReportService with the real bean type of "jasper" if the
	// guess above is wrong - this is the service whose policyform(req) method
	// already
	// backs your existing /pdf/policyform endpoint.
	@Autowired
	private JasperService jasper;
	@Value("${file.pdfDirectoryPath}")
	private String directoryPath;

//	@Override
//	public CommonRes generateDebitNoteUganda(viewAllReq req) {
//
//		CommonRes resp = new CommonRes();
//
//		try {
//
//			// ============================================================
//			// 1. Validate Request
//			// ============================================================
//
//			if (req == null || req.getQuoteNo() == null || req.getQuoteNo().trim().isEmpty()) {
//
//				resp.setIsError(true);
//				resp.setMessage("QuoteNo is required");
//				return resp;
//			}
//
//			String quoteNo = req.getQuoteNo().trim();
//
//			log.info("Generating Uganda Debit Note for QuoteNo : {}", quoteNo);
//
//			// ============================================================
//			// 2. Call Existing Uganda Tax Service
//			// ============================================================
//
//			CommonRes taxResponse = ugandaService.jsonFrame(req);
//
//			if (taxResponse == null) {
//
//				resp.setIsError(true);
//				resp.setMessage("No response received from Uganda Tax Service for QuoteNo: " + quoteNo);
//
//				return resp;
//			}
//
//			if (Boolean.TRUE.equals(taxResponse.getIsError())) {
//
//				resp.setIsError(true);
//				resp.setMessage(
//						taxResponse.getMessage() != null ? taxResponse.getMessage() : "Uganda Tax Service failed");
//
//				return resp;
//			}
//
//			if (taxResponse.getCommonResponse() == null) {
//
//				resp.setIsError(true);
//				resp.setMessage("No data found for QuoteNo: " + quoteNo);
//
//				return resp;
//			}
//
//			// ============================================================
//			// 3. Get UgandaDebitNotePIRes
//			// ============================================================
//
//			Object commonResponse = taxResponse.getCommonResponse();
//
//			log.info("COMMON RESPONSE TYPE = {}", commonResponse.getClass().getName());
//
//			log.info("COMMON RESPONSE = {}", commonResponse);
//
//			if (!(commonResponse instanceof UgandaDebitNotePIRes)) {
//
//				resp.setIsError(true);
//				resp.setMessage(
//						"Invalid response type from Uganda Tax Service. Expected UgandaDebitNotePIRes but received "
//								+ commonResponse.getClass().getName());
//
//				return resp;
//			}
//
//			UgandaDebitNotePIRes apiResult = (UgandaDebitNotePIRes) commonResponse;
//
//			// ============================================================
//			// 4. Get Individual Sections
//			// ============================================================
//
//			LinkedHashMap<String, Object> personalInfo = apiResult.getPiInfo();
//
//			LinkedHashMap<String, Object> policyInfo = apiResult.getPolicyInfo();
//
//			LinkedHashMap<String, Object> premiumInfo = apiResult.getPremiumInfo();
//
//			LinkedHashMap<String, Object> companyInfo = apiResult.getCompanyInfo();
//
//			LinkedHashMap<String, Object> endosInfo = apiResult.getEndtInfo();
//
//			// ============================================================
//			// 5. Validate Response Data
//			// ============================================================
//
//			if (personalInfo == null) {
//				personalInfo = new LinkedHashMap<>();
//			}
//
//			if (policyInfo == null) {
//				policyInfo = new LinkedHashMap<>();
//			}
//
//			if (premiumInfo == null) {
//				premiumInfo = new LinkedHashMap<>();
//			}
//
//			if (companyInfo == null) {
//				companyInfo = new LinkedHashMap<>();
//			}
//
//			if (endosInfo == null) {
//				endosInfo = new LinkedHashMap<>();
//			}
//
//			if (personalInfo.isEmpty() && policyInfo.isEmpty() && premiumInfo.isEmpty()) {
//
//				resp.setIsError(true);
//				resp.setMessage("No debit note data found for QuoteNo: " + quoteNo);
//
//				return resp;
//			}
//
//			// ============================================================
//			// 6. Policy Period
//			// ============================================================
//
//			String inceptionDate = nvl(policyInfo.get("InceptionDate"));
//
//			String expiryDate = nvl(policyInfo.get("ExpiryDate"));
//
//			// Build the policy period from RAW (un-escaped) trimmed dates, then
//			// escape the whole composed string once. Escaping twice, or escaping
//			// only parts, is how stray artifacts creep into re-escaped XML.
//			String policyPeriod = escapeXml(formatPolicyPeriodRaw(trimDate(rawNvl(policyInfo.get("InceptionDate"))),
//					trimDate(rawNvl(policyInfo.get("ExpiryDate")))));
//
//			// ============================================================
//			// 7. Printed Date
//			// ============================================================
//
//			String printedDate = escapeXml(new SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy").format(new Date()));
//
//			// ============================================================
//			// 8. Currency
//			// ============================================================
//
//			String curCode = nvl(policyInfo.get("Currency"));
//			String curCodeRaw = rawNvl(policyInfo.get("Currency"));
//
//			Map<String, String> context = new HashMap<>();
//
//			// ============================================================
//			// PERSONAL INFO
//			// ============================================================
//
//			context.put("insuredName", nvl(personalInfo.get("Insured Name")));
//
//			context.put("coInsuredName", nvl(personalInfo.get("CollateralName")));
//
//			context.put("invoiceNumber", nvl(personalInfo.get("Invoice Number")));
//
//			context.put("invoiceDate", nvl(personalInfo.get("Invoice Date")));
//
//			context.put("addressDetails", nvl(personalInfo.get("Address details")));
//
//			context.put("vatRegNo", nvl(personalInfo.get("Vat RegNo")));
//
//			context.put("customerId", nvl(personalInfo.get("Customer ID")));
//
//			// ============================================================
//			// POLICY INFO
//			// ============================================================
//
//			context.put("policyNo", nvl(policyInfo.get("Policy Number")));
//
//			context.put("intermediaryCode", nvl(policyInfo.get("CoreAppCode")));
//
//			context.put("policyPeriod", policyPeriod);
//
//			context.put("insuredCode", nvl(personalInfo.get("Customer ID")));
//
//			context.put("policyType", nvl(policyInfo.get("Policy Type")));
//
//			context.put("rmCode", "");
//
//			// ============================================================
//			// ENDORSEMENT INFO
//			// ============================================================
//
//			context.put("endorsementNo", nvl(endosInfo.get("EndtPolicyNo")));
//
//			context.put("endtCode", nvl(endosInfo.get("EndtTypeId")));
//
//			context.put("endorsementDesc", nvl(endosInfo.get("EndtDesc")));
//
//			// ============================================================
//			// PREMIUM / TAX INFO
//			// ============================================================
//
//			context.put("CurCode", curCode);
//
//			context.put("premiumAmount", nvl(premiumInfo.get("PremiumExcludedTaxLcVat")));
//
//			context.put("Tax1", nvl(premiumInfo.get("Tax1")));
//
//			context.put("Tax1Amount", nvl(premiumInfo.get("TaxAmount1")));
//
//			context.put("Tax2", nvl(premiumInfo.get("Tax2")));
//
//			context.put("Tax2Amount", nvl(premiumInfo.get("TaxAmount2")));
//
//			context.put("Tax3", nvl(premiumInfo.get("Tax3")));
//
//			context.put("Tax3Amount", nvl(premiumInfo.get("TaxAmount3")));
//
//			context.put("Tax4", nvl(premiumInfo.get("Tax4")));
//
//			context.put("Tax4Amount", nvl(premiumInfo.get("TaxAmount4")));
//
//			// Build concatenations from RAW values first, escape once at the end —
//			// never escape a piece, concatenate, then escape again (double-escaping
//			// turns "&amp;" into "&amp;amp;").
//			context.put("invoiceTotal", escapeXml(curCodeRaw + " " + rawNvl(premiumInfo.get("PremiumIncludedTaxVat"))));
//
//			context.put("taxesTotal", nvl(premiumInfo.get("TotalTaxamount")));
//
//			context.put("amountInWords", escapeXml(curCodeRaw + " " + rawNvl(premiumInfo.get("convertAmountToWords"))));
//
//			// ============================================================
//			// COMPANY INFO
//			// ============================================================
//
//			context.put("companyName", nvl(companyInfo.get("CompanyName")));
//
//			context.put("companyAddress", nvl(companyInfo.get("Company Address")));
//
//			context.put("emailAndWebSite", nvl(companyInfo.get("EmailAndWebSite ")));
//
//			context.put("signature", nvl(companyInfo.get("Signature")));
//
//			// ============================================================
//			// FOOTER
//			// ============================================================
//
//			context.put("preparedBy", "");
//
//			context.put("approvedBy", "");
//
//			context.put("printedBy", nvl(policyInfo.get("Intermediary Name")));
//
//			context.put("printedDate", printedDate);
//
//			// ============================================================
//			// 10. Log Context
//			// ============================================================
//
//			log.info("Uganda Debit Note Context for QuoteNo {} : {}", quoteNo, context);
//
//			// ============================================================
//			// 11. Load Word Template
//			// ============================================================
//
//			InputStream in = getClass().getResourceAsStream("/template/DebitPdf_Uganda_Draft.docx");
//
//			if (in == null) {
//
//				throw new FileNotFoundException("DebitPdf_Uganda_Draft.docx not found in /template/");
//			}
//
//			try (InputStream templateInputStream = in) {
//
//				byte[] templateBytes = templateInputStream.readAllBytes();
//
//				log.info("Uganda DOCX template size: {} bytes", templateBytes.length);
//
//				if (templateBytes.length < 4) {
//					log.info("DOCX template is empty or corrupted");
//				}
//
//				log.info("DOCX header: {} {} {} {}", String.format("%02X", templateBytes[0]),
//						String.format("%02X", templateBytes[1]), String.format("%02X", templateBytes[2]),
//						String.format("%02X", templateBytes[3]));
//
//				InputStream docxStream = new ByteArrayInputStream(templateBytes);
//
//				WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.load(docxStream);
//
//				// ========================================================
//				// 12. Font Mapper
//				// ========================================================
//
//				PhysicalFonts.discoverPhysicalFonts();
//
//				Mapper fontMapper = new IdentityPlusMapper();
//
//				if (PhysicalFonts.get("Carlito") != null) {
//					fontMapper.put("Calibri", PhysicalFonts.get("Carlito"));
//				}
//
//				if (PhysicalFonts.get("Arial") != null) {
//					fontMapper.put("Arial", PhysicalFonts.get("Arial"));
//				}
//
//				if (PhysicalFonts.get("Times New Roman") != null) {
//					fontMapper.put("Times New Roman", PhysicalFonts.get("Times New Roman"));
//				}
//
//				wordMLPackage.setFontMapper(fontMapper);
//
//				// ========================================================
//				// 13. Prepare Variables
//				// ========================================================
//
//				VariablePrepare.prepare(wordMLPackage);
//
//				// ========================================================
//				// 14. Replace Variables in Body
//				// ========================================================
//				String productId = nvl(personalInfo.get("ProductIt"));
//				if (!"5".equals(productId.trim())) {
//				    removeTax4Row(wordMLPackage);
//				}
//
//				MainDocumentPart documentPart = wordMLPackage.getMainDocumentPart();
//
//				documentPart.variableReplace(context);
//
//				// ========================================================
//				// 15. Replace Variables in Headers
//				// ========================================================
//
//				replaceVariablesInHeaders(wordMLPackage, context);
//
//				// ========================================================
//				// 16. Convert DOCX to PDF
//				// ========================================================
//
//				byte[] pdfArray = convertDocxToPdf(wordMLPackage);
//
//				// ========================================================
//				// 17. File Name
//				// ========================================================
//
//				String quoteNoSafe = quoteNo.replaceAll("[\\\\/:*?\"<>|]", "_");
//
//				String fileName = "DebitNote_Uganda_" + quoteNoSafe + ".pdf";
//
//				// ========================================================
//				// 18. Output Path
//				// ========================================================
//
//				Path outputPath = Paths.get(directoryPath, fileName);
//
//				Files.createDirectories(outputPath.getParent());
//
//				// ========================================================
//				// 19. Write PDF
//				// ========================================================
//
//				Files.write(outputPath, pdfArray);
//
//				log.info("DebitNote Uganda PDF Generated Successfully : {}", outputPath);
//
//				// ========================================================
//				// 20. Convert PDF to Base64
//				// ========================================================
//
//				byte[] fileBytes = Files.readAllBytes(outputPath);
//
//				String base64 = Base64.getEncoder().encodeToString(fileBytes);
//
//				String mimeType = "application/pdf";
//
//				String imgData = "data:" + mimeType + ";base64," + base64;
//
//				// ========================================================
//				// 21. Response
//				// ========================================================
//
//				Map<String, Object> docRes = new HashMap<>();
//
//				docRes.put("FileName", outputPath.toString());
//
//				docRes.put("Base64", imgData);
//
//				resp.setCommonResponse(docRes);
//
//				resp.setErrorMessage(null);
//
//				resp.setIsError(false);
//
//				resp.setMessage("Debit Note PDF generated successfully");
//
//				return resp;
//			}
//
//		} catch (Exception e) {
//
//			log.error("Exception in generateDebitNoteUganda ===> {}", e.getMessage(), e);
//
//			resp.setCommonResponse(null);
//
//			resp.setIsError(true);
//
//			resp.setMessage("Failed to generate Uganda Debit Note PDF");
//
//			return resp;
//		}
//	}

	@Override
	public CommonRes generateScheduleUganda(JasperDocumentReq req) {
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
			log.info("Generating Uganda Policy Schedule for QuoteNo : {}", quoteNo);
			// ============================================================
			// 2. Call Existing Jasper PolicyForm Service
			// ============================================================
			JasperDocumentRes jasperRes = jasper.policyform(req);
			if (jasperRes == null) {
				resp.setIsError(true);
				resp.setMessage("No response received from Policy Schedule Service for QuoteNo: " + quoteNo);
				return resp;
			}
			if (hasContent(jasperRes.getErrorMessage())) {
				resp.setIsError(true);
				resp.setMessage(jasperRes.getErrorMessage().toString());
				return resp;
			}
			// ============================================================
			LinkedHashMap<String, Object> data = toMap(jasperRes.getJsonString());
			if (data == null || data.isEmpty()) {
				resp.setIsError(true);
				resp.setMessage("No policy schedule data found for QuoteNo: " + quoteNo);
				return resp;
			}
			// ============================================================
			// 4. First Vehicle (template has no repeating vehicle row)
			// ============================================================
			List<Map<String, Object>> vehicleDetails = toListOfMaps(data.get("vehicleDetails"));
			Map<String, Object> vehicle = (vehicleDetails != null && !vehicleDetails.isEmpty()) ? vehicleDetails.get(0)
					: new LinkedHashMap<>();
			
			boolean isMultiVehicle = vehicleDetails.size() > 1;
			InputStream in = null;
			if (vehicleDetails != null && vehicleDetails.size() > 1) {
				in = getClass().getResourceAsStream("/template/Uganda-schedule-template-multiVehicle.docx");
			}else {
				in = getClass().getResourceAsStream("/template/Uganda-schedule-template.docx");
			}
			// ============================================================
			// 5. Premium Details (list of {amount, narration, status})
			// ============================================================
			List<Map<String, Object>> premiumDetails = toListOfMaps(data.get("premiumDetails"));
			String periodFromRaw = rawNvl(vehicle.get("effectiveDateStart")); // 2026-08-27 05:30:00.0
			if (periodFromRaw.isEmpty()) {
				periodFromRaw = trimDate(rawNvl(data.get("effectiveDateStart")));
			}
			String periodToRaw = rawNvl(vehicle.get("effectiveDateEnd")); // 2027-08-26 23:59:59.0
			if (periodToRaw.isEmpty()) {
				periodToRaw = trimDate(rawNvl(data.get("effectiveDateEnd")));
			}
			Map<String, String> context = new HashMap<>();
			// ---- Insured / Policy ----
			context.put("insuredCode", nvl(data.get("customerId")));
			context.put("insuredName", nvl(data.get("customerName")));
			context.put("policyNo", nvl(data.get("policyNo")));
			context.put("policyType", nvl(data.get("motorType")));
			context.put("postalAddress", nvl(data.get("postalAddress")));			
			// ASSUMPTION: subUserType ("broker") is used as the business channel.
			context.put("businessChannel", nvl(data.get("subUserType")));
			context.put("debitNoteNo", nvl(data.get("debitNoteNo")));
			// ASSUMPTION: coverNoteReferenceNo maps to "Broker Risk Note".
			context.put("brokerRiskNote", nvl(data.get("brokerRiskNote")));
			context.put("business", nvl(data.get("business")));
			// ASSUMPTION: loginId ("Uganda_Broker") is used for "Broker / Agent".
			context.put("brokerAgent", nvl(data.get("loginId")));
			context.put("periodFrom", nvl(periodFromRaw));
			context.put("periodTo", nvl(periodToRaw));
			context.put("currencyCode", nvl(data.get("currency")));
			// ---- Vehicle ----
			context.put("regNo", nvl(vehicle.get("registrationNumber")));
			context.put("makeModel",
					escapeXml((rawNvl(vehicle.get("vehicleMake")) + " " + rawNvl(vehicle.get("vehcileModel"))).trim()));
			context.put("bodyTypeCC", escapeXml(
					(rawNvl(vehicle.get("vehicleTypeDesc")) + " - " + rawNvl(vehicle.get("cubicCapacity"))).trim()));
			context.put("yearOfMake", nvl(vehicle.get("manufactureYear")));
			context.put("chassisNumber", nvl(vehicle.get("chassisNumber")));
			context.put("geographicalArea", nvl(vehicle.get("geographicArea")));
			context.put("engineNumber", nvl(vehicle.get("engineNumber")));
			context.put("sumInsured", nvl(vehicle.get("sumInsured")));
			// TODO: no sticker number field found on either the top-level payload
			// or the vehicle entry in the sample JSON. Left blank until confirmed.
			context.put("stickerNumber", nvl(vehicle.get("stickerNumber")));
			// ---- Premium Details (matched by narration, not array index) ----
			context.put("premium", formatAmount(nvl(findPremiumAmount(premiumDetails, "premium"))));

			context.put("stickerFee", formatAmount(nvl(findPremiumAmount(premiumDetails, "sticker fee"))));

			context.put("levy", formatAmount(nvl(findPremiumAmount(premiumDetails, "levy"))));

			context.put("vat", formatAmount(nvl(findPremiumAmount(premiumDetails, "vat"))));

			context.put("stampDuty", formatAmount(nvl(findPremiumAmount(premiumDetails, "stamp"))));

			String total = formatAmount(findPremiumAmount(premiumDetails, "total"));
			context.put("total", total);
			if (total.isEmpty()) {
				total = rawNvl(data.get("overAllPremium"));
			}
			context.put("total", nvl(total));
			// ---- Signature block ----
			context.put("place", nvl(data.get("branchName")));
			context.put("date", nvl(data.get("inceptionDate")));
			context.put("preparedBy", nvl(data.get("userName")));
			context.put("approvedBy", nvl(data.get("approvedBy")));
			// ---- Company / Header ----
			context.put("companyName", nvl(data.get("companyName")));
			context.put("companyAddressLine", nvl(data.get("companyAddress")));
			context.put("companyContactLine", escapeXml(buildContactLine(rawNvl(data.get("companyPhone")),
					rawNvl(data.get("companyMail")), rawNvl(data.get("companyWebsite")))));
			// ============================================================
			
			if (in == null) {
				throw new FileNotFoundException("Uganda-schedule-template.docx not found in /template/");
			}
			try (InputStream templateInputStream = in) {
				byte[] templateBytes = templateInputStream.readAllBytes();
				log.info("Uganda Schedule DOCX template size: {} bytes", templateBytes.length);
				if (templateBytes.length < 4) {
					log.info("DOCX template is empty or corrupted");
				}
				InputStream docxStream = new ByteArrayInputStream(templateBytes);
				WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.load(docxStream);
				// ========================================================
				// 11. Font Mapper
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
				// 12. Prepare Variables
				// ========================================================
				VariablePrepare.prepare(wordMLPackage);
				// ========================================================
				// 13. Replace Variables in Body (covers the schedule table too -
				// variableReplace() walks every text node in the part,
				// table cells included)
				// ========================================================
				
				if (isMultiVehicle) {
				    // NEW multi-vehicle processing
				    populateMultiVehicleTemplate(wordMLPackage, vehicleDetails);
				    
				} else {
				    // YOUR EXISTING single-vehicle processing
				    
				}
				MainDocumentPart documentPart = wordMLPackage.getMainDocumentPart();

			    documentPart.variableReplace(context);
				//MainDocumentPart documentPart = wordMLPackage.getMainDocumentPart();
				//documentPart.variableReplace(context);
				// ========================================================
				// 14. Replace Variables in Headers (${companyName},
				// ${companyAddressLine}, ${companyContactLine})
				// ========================================================
				replaceVariablesInHeaders(wordMLPackage, context);
				// ========================================================
				// 15. Convert DOCX to PDF
				// ========================================================
				byte[] pdfArray = convertDocxToPdf(wordMLPackage);
				// ========================================================
				
//				String docFileName = quoteNo + "_" + quoteNo.replaceAll("[\\\\/:*?\"<>|]", "_") + ".docx";
//
//				Path generatedDocx = Paths.get(directoryPath, docFileName);

//				Files.createDirectories(generatedDocx.getParent());
//
//				wordMLPackage.save(generatedDocx.toFile());
//
//				System.out.println("Generated DOCX: " + generatedDocx);
				// 16. File Name
				// ========================================================
				String quoteNoSafe = quoteNo.replaceAll("[\\\\/:*?\"<>|]", "_");
				String fileName = "PolicySchedule_Uganda_" + quoteNoSafe + ".pdf";
				// ========================================================
				// 17. Output Path
				// ========================================================
				Path outputPath = Paths.get(directoryPath, fileName);
				Files.createDirectories(outputPath.getParent());
				// ========================================================
				// 18. Write PDF
				// ========================================================
				Files.write(outputPath, pdfArray);
				log.info("Policy Schedule Uganda PDF Generated Successfully : {}", outputPath);
				// ========================================================
				// 19. Convert PDF to Base64
				// ========================================================
				byte[] fileBytes = Files.readAllBytes(outputPath);
				String base64 = Base64.getEncoder().encodeToString(fileBytes);
				String mimeType = "application/pdf";
				String imgData = "data:" + mimeType + ";base64," + base64;
				// ========================================================
				// 20. Response
				// ========================================================
				Map<String, Object> docRes = new HashMap<>();
				docRes.put("FileName", outputPath.toString());
				docRes.put("Base64", imgData);
				resp.setCommonResponse(docRes);
				resp.setErrorMessage(null);
				resp.setIsError(false);
				resp.setMessage("Policy Schedule PDF generated successfully");
				return resp;
			}
		} catch (Exception e) {
			log.error("Exception in generateScheduleUganda ===> {}", e.getMessage(), e);
			resp.setCommonResponse(null);
			resp.setIsError(true);
			resp.setMessage("Failed to generate Uganda Policy Schedule PDF");
			return resp;
		}
	}
	private void populateMultiVehicleTemplate(
	        WordprocessingMLPackage wordMLPackage,
	        List<Map<String, Object>> vehicleDetails) {

	    try {

	        MainDocumentPart documentPart =
	                wordMLPackage.getMainDocumentPart();

	        // Get all tables from the document
	        List<Object> tables = new ArrayList<>();

	        TraversalUtil.CallbackImpl callback =
	                new TraversalUtil.CallbackImpl() {

	                    @Override
	                    public List<Object> apply(Object o) {

	                        if (o instanceof Tbl) {
	                            tables.add(o);
	                        }

	                        return null;
	                    }
	                };

	        TraversalUtil traversalUtil =
	                new TraversalUtil(documentPart.getContent(), callback);

	        // Find the vehicle template row
	        Tbl vehicleTable = null;
	        Tr templateRow = null;

	        for (Object tableObj : tables) {

	            Tbl table = (Tbl) tableObj;

	            List<Object> rows =
	                    table.getContent();

	            for (Object rowObj : rows) {

	                Tr row = null;

	                if (rowObj instanceof Tr) {
	                    row = (Tr) rowObj;
	                } else if (rowObj instanceof JAXBElement) {

	                    Object value =
	                            ((JAXBElement<?>) rowObj).getValue();

	                    if (value instanceof Tr) {
	                        row = (Tr) value;
	                    }
	                }

	                if (row == null) {
	                    continue;
	                }

	                String rowText = getRowText(row);

	                // This identifies your vehicle template row
	                if (rowText.contains("${regNo}")) {

	                    vehicleTable = table;
	                    templateRow = row;

	                    break;
	                }
	            }

	            if (templateRow != null) {
	                break;
	            }
	        }

	        if (vehicleTable == null || templateRow == null) {

	            log.warn(
	                    "Vehicle template row containing ${regNo} was not found");

	            return;
	        }

	        log.info(
	                "Vehicle template row found. Vehicle count: {}",
	                vehicleDetails.size());

	        /*
	         * Create one row for each vehicle
	         */
	        for (Map<String, Object> vehicle : vehicleDetails) {

	            // Create context for current vehicle
	            Map<String, String> vehicleContext =
	                    createMultiVehicleContext(vehicle);

	            // Deep copy the template row
	            Tr newRow =
	                    (Tr) XmlUtils.deepCopy(templateRow);

	            // Replace placeholders in this row
	            replaceVariablesInRow(
	                    newRow,
	                    vehicleContext);

	            // Add the newly created row
	            vehicleTable.getContent().add(newRow);

	            log.info(
	                    "Vehicle row added for registration number: {}",
	                    vehicle.get("registrationNumber"));
	        }

	        /*
	         * Remove the original template row.
	         *
	         * The original row only contains placeholders,
	         * so we don't need to keep it after creating
	         * the actual vehicle rows.
	         */
	        vehicleTable.getContent().remove(templateRow);

	        log.info(
	                "Multi vehicle template populated successfully");

	    } catch (Exception e) {

	        log.error(
	                "Error while populating multi vehicle template",
	                e);

	        throw new RuntimeException(
	                "Failed to populate multi vehicle template",
	                e);
	    }
	}

	private void replaceVariablesInRow(
	        Tr row,
	        Map<String, String> context) {

	    TraversalUtil.CallbackImpl callback =
	            new TraversalUtil.CallbackImpl() {

	                @Override
	                public List<Object> apply(Object o) {

	                    if (o instanceof Text) {

	                        Text text = (Text) o;

	                        String value = text.getValue();

	                        if (value == null) {
	                            return null;
	                        }

	                        for (Map.Entry<String, String> entry
	                                : context.entrySet()) {

	                            String placeholder =
	                                    "${" + entry.getKey() + "}";

	                            String replacement =
	                                    entry.getValue() != null
	                                            ? entry.getValue()
	                                            : "";

	                            if (value.contains(placeholder)) {

	                                value = value.replace(
	                                        placeholder,
	                                        replacement);
	                            }
	                        }

	                        text.setValue(value);
	                    }

	                    return null;
	                }
	            };

	    new TraversalUtil(row, callback);
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

	private Map<String, String> createMultiVehicleContext(
	        Map<String, Object> vehicle) {

	    Map<String, String> context = new LinkedHashMap<>();

	    context.put(
	            "regNo",
	            getStringValue(vehicle.get("registrationNumber"))
	    );

	    context.put(
	            "modBodType",
	            getStringValue(vehicle.get("vehicleMake"))
	            + " / "
	            +getStringValue(vehicle.get("vehcileModel"))
	                    + " / "
	                    + getStringValue(vehicle.get("vehicleTypeDesc"))
	    );

	    context.put(
	            "cc",
	            getStringValue(vehicle.get("cubicCapacity"))
	    );

	    context.put(
	            "manYear",
	            getStringValue(vehicle.get("manufactureYear"))
	    );

	    context.put(
	            "seatCap",
	            getStringValue(vehicle.get("seatingCapacity"))
	    );

	    context.put(
	            "sumInsured",
	            getStringValue(vehicle.get("sumInsured"))
	    );

	    context.put(
	            "windScreen",
	            getStringValue(vehicle.get("windScreen"))
	    );

	    return context;
	}

	private String getStringValue(Object value) {

	    if (value == null) {
	        return "";
	    }

	    return String.valueOf(value);
	}

	private String formatAmount(String amount) {
	    if (amount == null || amount.trim().isEmpty()) {
	        return "";
	    }

	    try {
	        BigDecimal value = new BigDecimal(amount.replace(",", "").trim());

	        return NumberFormat.getNumberInstance(Locale.US).format(value);
	    } catch (NumberFormatException e) {
	        return amount;
	    }
	}

	// ========================================================================
	// Replace Variables in Header
	// ========================================================================
//	private void replaceVariablesInHeaders(WordprocessingMLPackage wordMLPackage, Map<String, String> context)
//			throws Exception {
//		RelationshipsPart rp = wordMLPackage.getMainDocumentPart().getRelationshipsPart();
//		if (rp == null || rp.getRelationships() == null || rp.getRelationships().getRelationship() == null) {
//			return;
//		}
//		List<Relationship> rels = rp.getRelationships().getRelationship();
//		for (Relationship r : rels) {
//			if (r.getType() != null && r.getType().endsWith("/header")) {
//				try {
//					String target = r.getTarget();
//					String partNameStr;
//					if (target.startsWith("/")) {
//						partNameStr = target;
//					} else {
//						partNameStr = "/word/" + target;
//					}
//					Part part = wordMLPackage.getParts().get(new PartName(partNameStr));
//					if (part instanceof HeaderPart) {
//						((HeaderPart) part).variableReplace(context);
//					}
//				} catch (Exception ex) {
//					log.warn("Could not replace variables in header part : {}", ex.getMessage());
//				}
//			}
//		}
//	}
	// ========================================================================
	// Replace Variables in Header
	// ========================================================================
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

	// ========================================================================
	// Merge split runs (Word breaks "${companyName}" into multiple <w:r>
	// elements unpredictably) into one run's text, then do a plain string
	// substitution. This replaces the direct variableReplace() call, which
	// only matched when the placeholder happened to still be a single run.
	// ========================================================================
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
	// ========================================================================
	// Remove the table row containing ${Tax4} when ProductId != 5
	// ========================================================================
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

	@SuppressWarnings("unchecked")
	private List<Map<String, Object>> toListOfMaps(Object listField) {
		if (listField == null) {
			return new ArrayList<>();
		}
		if (listField instanceof List) {
			return (List<Map<String, Object>>) listField;
		}
		ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.convertValue(listField, new TypeReference<List<Map<String, Object>>>() {
			});
		} catch (Exception e) {
			log.warn("Could not normalize list field into List<Map>: {}", e.getMessage());
			return new ArrayList<>();
		}
	}
}