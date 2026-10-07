package com.maan.eway.document.ai.service.impl;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.imageio.ImageIO;

import org.apache.commons.io.FilenameUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.tika.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.maan.eway.document.ai.res.RegistrationDocumentRecognitionRes;
import com.maan.eway.document.ai.service.RegistrationDocumentRecognitionService;

@Service
public class RegistrationDocumentRecognitionServiceImpl implements RegistrationDocumentRecognitionService{

	@Value("${external.api.key}")
    String apiKey;
	
	@Value("${file.directoryPath}")
	private String directoryPath;

	@Value("${file.compressedImg}")
	private String compressedImg;
	
    private final RestTemplate restTemplate = new RestTemplate();
	
	@Autowired
	private ObjectMapper mapper;
	 
	@Override
	public RegistrationDocumentRecognitionRes getDocumentResult(MultipartFile file, Object req) throws IOException{
		
		RegistrationDocumentRecognitionRes resp = new RegistrationDocumentRecognitionRes();
		try {
			Random random = new Random();
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());

			String newfilename = "";
			String newfilename1 = "";
			// OrginalFile
			Path destination = Paths.get(directoryPath);
			newfilename = "REG"+random.nextInt(100)
					+ timestamp.toString().replace(":", "T").replace(" ", "S").replace("-", "H").replace(".", "D") + "."
					+ FilenameUtils.getExtension(file.getOriginalFilename());
		//	Files.copy(file.getInputStream(), destination.resolve(newfilename));
			System.out.println("Original File Name :"+ newfilename);

			Timestamp timestamp1 = new Timestamp(System.currentTimeMillis());
			// BackupFile
			Path destination1 = Paths.get(compressedImg);
			newfilename1 = "REG"+random.nextInt(100)
					+ timestamp1.toString().replace(":", "T").replace(" ", "S").replace("-", "H").replace(".", "D")
					+ "." + FilenameUtils.getExtension(file.getOriginalFilename());
			System.out.println("BackUp File Name :"+ newfilename1);
		//	Files.copy(file.getInputStream(), destination1.resolve(newfilename1));
			String contentType = file.getContentType();
			String base64="";
			if(StringUtils.isEmpty(contentType)) {
				throw new IOException("Unknown file type");
			}else if(contentType.contentEquals("application/pdf")) {
				byte[] image = pdfToImage(file);
		    	 base64 = convertImageToBase64(image);
		    	 System.out.println("print Base 64: "+base64);
		    	 contentType = "image/jpeg";
			}else if(contentType.contentEquals("image/jpeg")) {
				base64 = convertImageToBase64(file.getBytes());
			}else if(contentType.contentEquals("image/png")) {
				base64 = convertImageToBase64(file.getBytes());
			}else if(contentType.contentEquals("image/gif")) {
				base64 = convertImageToBase64(file.getBytes());
			}else if(contentType.contentEquals("image/webp")) {
				base64 = convertImageToBase64(file.getBytes());
			}else {
				throw new IOException("Unsupported file type: " + contentType);
			}
			resp = extractReplyFromGeminiAi(base64, contentType, req);
			return  resp;
		}catch(Exception e) {
			e.printStackTrace();
			return null;
		}
		
	}
	
private RegistrationDocumentRecognitionRes extractReplyFromGeminiAi(String base64, String fileType , Object req) {
		
	RegistrationDocumentRecognitionRes respClass = new RegistrationDocumentRecognitionRes();
		String url = getGeminiUrl();
		
		String prompt ="";
		
		String request = req.toString();
		
		Map<String, Object> mapReq=null;
		try {
			mapReq = mapper.readValue(request, Map.class);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String companyId = mapReq.get("companyId") == null ? null : mapReq.get("companyId").toString();
		
		if(!StringUtils.isBlank(companyId)) {
			if("100046".equals(companyId)) {//zambia
				prompt = "Analyze the provided import declaration data. Extract only the English values. Find the exact JSON keys. If a key's value is not available, set it to null. Respond ONLY in valid JSON with a single top-level object containing a data key. The data object should include the following keys: RegistrationMark, ChassisNumber, EngineNumber, Make, Model, ModelNumber, Colour, VehicleCategory, ProbelledBy, NetWeighgt, GVMKg, Class, EngineCapacity, SeatingCapacity, RegistrationAuthority, YearOfMake, FirstRegistrationDate, CustomsClearanceNumber, InterpolNumber. Do not include any extra explanation or text outside the JSON block";
			}else if("100047".equals(companyId)) {//boatswana
				prompt = "Analyze the provided import declaration data. Extract only the English values. Find the exact JSON keys. If a key's value is not available, set it to null. Respond ONLY in valid JSON with a single top-level object containing a data key. The data object should include the following keys: RegistrationNumber, ChassisNo, EngineNo, Make, Model, BodyType, Colour, YearOfManufacture, FuelUsed, NumberOfAxis, UnladenWeight, EngineCapacity, GrossWeight, FirstRegistered. Do not include any extra explanation or text outside the JSON block";
			}else if("100048".equals(companyId)) {//mozambique
				prompt = "Analyze the provided import declaration data. Extract only the English values and return values in English only. Find the exact JSON keys. If a key's value is not available, set it to null. Respond ONLY in valid JSON with a single top-level object containing a data key. The data object should include the following keys: RegistrationNumber, ChassisNumber, EngineNumber, Make, Model, BodyType, Colour, YearOfManufacture, Fuel, TareWeight, Capacity. Do not include any extra explanation or text outside the JSON block";
			}else if("100049".equals(companyId)) {//swaziland
				prompt = "Analyze the provided import declaration data. Extract only the English values. Find the exact JSON keys. If a key's value is not available, set it to null. Respond ONLY in valid JSON with a single top-level object containing a data key. The data object should include the following keys: CertificateNumber, RegistrationNumber, ChassisNumber, EngineNumber, VehicleMake, VehicleType, YearOfManufacture, TareWeight, AnnualLicenseFee, HorsePower, DateRegistered, NeworSecondHand, CountryOfIssue, VehicleUsage. Do not include any extra explanation or text outside the JSON block";
			}else if("100050".equals(companyId)) {//namibia
				prompt = "Analyze the provided import declaration data. Extract only the English values. Find the exact JSON keys. If a key's value is not available, set it to null. Respond ONLY in valid JSON with a single top-level object containing a data key. The data object should include the following keys: RegistrationAuthority, VehicleRegistrationNumber, VehicleIdendicationNumber, EngineNumber, Make, SeriesName, VehicleCategory, Driven, VehicleDescription, Tare, TypeOfIdentication, VehicleStatus, IdenticationNumber. Do not include any extra explanation or text outside the JSON block";
			}
		}
		
	 
		
		Map<String, Object> requestBody = Map.of(
                "contents", List.of(
                        Map.of(
                                "parts", List.of(
                                        Map.of("text", prompt),
                                        Map.of("inlineData", Map.of(
                                                "mimeType", fileType,
                                                "data", base64
                                        ))
                                )
                        )
                )
        );
		
		 HttpHeaders headers = new HttpHeaders();
	        headers.setContentType(MediaType.APPLICATION_JSON);

	        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

	        System.out.println("AI PROCESSING...");
	        ResponseEntity<JsonNode> response = restTemplate.exchange(url,HttpMethod.POST,entity,JsonNode.class);
	        
	        try {
	            ObjectMapper mapper = new ObjectMapper();
	            JsonNode root = response.getBody();

	            String rawText = root
	                    .path("candidates").get(0)
	                    .path("content")
	                    .path("parts").get(0)
	                    .path("text").asText();

	            // Remove json and trailing
	            if (rawText.startsWith("```json") || rawText.startsWith("```")) {
	                rawText = rawText.replaceFirst("^```json\\s*", "").replaceFirst("\\s*```$", "");
	                rawText = rawText.replace("\\", "");
	                
	            }
	            
	            JsonNode resp = mapper.readTree(rawText.trim());
                if(!resp.get("data").isEmpty()) {
                	String jsonResp = resp.toString();
      	          //  ObjectMapper mapper = new ObjectMapper();
      	            Map<String,Object> roots = mapper.readValue(jsonResp, Map.class);
      	            Map<String, Object> data = (Map<String, Object>) roots.get("data");
      	          System.out.println("RESPONSE MAPPING ON PROCESS...");
      	          if("100046".equals(companyId)) {
      	        	respClass.setRegistrationNumber(data.get("RegistrationMark") == null ? null : data.get("RegistrationMark").toString());
      	        	respClass.setChassisNumber(data.get("ChassisNumber") == null ? null : data.get("ChassisNumber").toString());
      	        	respClass.setEngineNumber(data.get("EngineNumber") == null ? null : data.get("EngineNumber").toString());
      	        	respClass.setMake(data.get("Make") == null ? null : data.get("Make").toString());
      	        	respClass.setModel(data.get("Model") == null ? null : data.get("Model").toString());
      	        	respClass.setVehicleColor(data.get("Colour") == null ? null : data.get("Colour").toString());
      	        	respClass.setMotorCategory(data.get("VehicleCategory") == null ? null : data.get("VehicleCategory").toString());
      	        	respClass.setTareWeight(data.get("NetWeighgt") == null ? null : data.get("NetWeighgt").toString());
      	        	respClass.setGrossWeight(data.get("GVMKg") == null ? null : data.get("GVMKg").toString());
      	        	respClass.setEngineCapacity(data.get("EngineCapacity") == null ? null : data.get("EngineCapacity").toString());
      	        	respClass.setSeatingCapacity(data.get("SeatingCapacity") == null ? null : data.get("SeatingCapacity").toString());
      	        	respClass.setManufactureYear(data.get("YearOfMake") == null ? null : data.get("YearOfMake").toString());
      	        	
      	          }else if("100047".equals(companyId)) {
      	        	respClass.setRegistrationNumber(data.get("RegistrationNumber") == null ? null : data.get("RegistrationNumber").toString());
      	        	respClass.setChassisNumber(data.get("ChassisNo") == null ? null : data.get("ChassisNo").toString());
      	        	respClass.setEngineNumber(data.get("EngineNo") == null ? null : data.get("EngineNo").toString());
      	        	respClass.setMake(data.get("Make") == null ? null : data.get("Make").toString());
      	        	respClass.setModel(data.get("Model") == null ? null : data.get("Model").toString()); 
      	        	respClass.setBodyType(data.get("BodyType") == null ? null : data.get("BodyType").toString());
      	        	respClass.setVehicleColor(data.get("Colour") == null ? null : data.get("Colour").toString());
      	        	respClass.setManufactureYear(data.get("YearOfManufacture") == null ? null : data.get("YearOfManufacture").toString());
      	        	respClass.setFuelType(data.get("FuelUsed") == null ? null : data.get("FuelUsed").toString());
      	        	respClass.setNumberofAxis(data.get("NumberOfAxis") == null ? null : data.get("NumberOfAxis").toString());
      	        	respClass.setTareWeight(data.get("UnladenWeight") == null ? null : data.get("UnladenWeight").toString());
      	        	respClass.setGrossWeight(data.get("GrossWeight") == null ? null : data.get("GrossWeight").toString());
      	        	respClass.setEngineCapacity(data.get("EngineCapacity") == null ? null : data.get("EngineCapacity").toString());
      	        	
      	          }else if("100048".equals(companyId)) {
      	        	respClass.setRegistrationNumber(data.get("RegistrationNumber") == null ? null : data.get("RegistrationNumber").toString());
      	        	respClass.setChassisNumber(data.get("ChassisNumber") == null ? null : data.get("ChassisNumber").toString());
      	        	respClass.setEngineNumber(data.get("EngineNumber") == null ? null : data.get("EngineNumber").toString());
      	        	respClass.setMake(data.get("Make") == null ? null : data.get("Make").toString());
      	        	respClass.setModel(data.get("Model") == null ? null : data.get("Model").toString());
      	        	respClass.setBodyType(data.get("BodyType") == null ? null : data.get("BodyType").toString());
      	        	respClass.setVehicleColor(data.get("Colour") == null ? null : data.get("Colour").toString());
      	        	respClass.setManufactureYear(data.get("YearOfManufacture") == null ? null : data.get("YearOfManufacture").toString());
      	        	respClass.setFuelType(data.get("Fuel") == null ? null : data.get("Fuel").toString());
      	        	respClass.setTareWeight(data.get("TareWeight") == null ? null : data.get("TareWeight").toString());
      	        	respClass.setSeatingCapacity(data.get("Capacity") == null ? null : data.get("Capacity").toString());
      	          }else if("100049".equals(companyId)) {
      	        	respClass.setRegistrationNumber(data.get("RegistrationNumber") == null ? null : data.get("RegistrationNumber").toString());
      	        	respClass.setChassisNumber(data.get("ChassisNumber") == null ? null : data.get("ChassisNumber").toString());
      	        	respClass.setEngineNumber(data.get("EngineNumber") == null ? null : data.get("EngineNumber").toString());
      	        	respClass.setMake(data.get("VehicleMake") == null ? null : data.get("VehicleMake").toString());
      	        	respClass.setBodyType(data.get("VehicleType") == null ? null : data.get("VehicleType").toString());
      	        	respClass.setManufactureYear(data.get("YearOfManufacture") == null ? null : data.get("YearOfManufacture").toString());
      	        	respClass.setTareWeight(data.get("TareWeight") == null ? null : data.get("TareWeight").toString());
      	        	respClass.setVehicleStatus(data.get("NeworSecondHand") == null ? null : data.get("NeworSecondHand").toString());
      	          }else if("100050".equals(companyId)) {
      	        	respClass.setRegistrationNumber(data.get("VehicleRegistrationNumber") == null ? null : data.get("VehicleRegistrationNumber").toString());
      	        	respClass.setChassisNumber(data.get("VehicleIdendicationNumber") == null ? null : data.get("VehicleIdendicationNumber").toString());
      	        	respClass.setEngineNumber(data.get("EngineNumber") == null ? null : data.get("EngineNumber").toString());
      	        	respClass.setMake(data.get("Make") == null ? null : data.get("Make").toString());
      	        	respClass.setModel(data.get("SeriesName") == null ? null : data.get("SeriesName").toString());
      	        	respClass.setBodyType(data.get("VehicleDescription") == null ? null : data.get("VehicleDescription").toString());
      	        	respClass.setMotorCategory(data.get("VehicleCategory") == null ? null : data.get("VehicleCategory").toString());
      	        	respClass.setTareWeight(data.get("Tare") == null ? null : data.get("Tare").toString());
      	        	respClass.setVehicleStatus(data.get("VehicleStatus") == null ? null : data.get("VehicleStatus").toString());
      	        	
      	          }
      	          return respClass;
	            }
	            
	          //  return mapper.readTree(rawText.trim());

	        }
	        catch (Exception e) {
	        	e.printStackTrace();
	            throw new RuntimeException("Failed to parse Gemini response", e);
	        }
	        
	        return null;
	}

/*
	private JsonNode extractReplyFromGeminiAi(String base64, String fileType) {
		
		String url = getGeminiUrl();
		
		String prompt = "Analyze the provided import declaration data. Extract only the English values. Find the exact JSON keys. If a key's value is not available, set it to null. Respond ONLY in valid JSON with a single top-level object containing a data key. The data object should include the following keys: RegistrationMark, ChassisNumber, EngineNumber, Make, Model, ModelNumber, Colour, VehicleCategory, ProbelledBy, NetWeighgt, GVMKg, Class, EngineCapacity, SeatingCapacity, RegistrationAuthority, YearOfMake, FirstRegistrationDate, CustomsClearanceNumber, InterpolNumber. Do not include any extra explanation or text outside the JSON block";
		
		Map<String, Object> requestBody = Map.of(
                "contents", List.of(
                        Map.of(
                                "parts", List.of(
                                        Map.of("text", prompt),
                                        Map.of("inlineData", Map.of(
                                                "mimeType", fileType,
                                                "data", base64
                                        ))
                                )
                        )
                )
        );
		
		 HttpHeaders headers = new HttpHeaders();
	        headers.setContentType(MediaType.APPLICATION_JSON);

	        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

	        ResponseEntity<JsonNode> response = restTemplate.exchange(url,HttpMethod.POST,entity,JsonNode.class);
	        
	        try {
	            ObjectMapper mapper = new ObjectMapper();
	            JsonNode root = response.getBody();

	            String rawText = root
	                    .path("candidates").get(0)
	                    .path("content")
	                    .path("parts").get(0)
	                    .path("text").asText();

	            // Remove json and trailing
	            if (rawText.startsWith("```json") || rawText.startsWith("```")) {
	                rawText = rawText.replaceFirst("^```json\\s*", "").replaceFirst("\\s*```$", "");
	                rawText = rawText.replace("\\", "");
	                
	            }

	            return mapper.readTree(rawText.trim());

	        }
	        catch (Exception e) {
	        	e.printStackTrace();
	            throw new RuntimeException("Failed to parse Gemini response", e);

	        }
	} */
	
	private byte[] pdfToImage(MultipartFile file) throws IOException {
        try
            (PDDocument document = PDDocument.load(file.getInputStream()))
            {
                PDFRenderer renderer = new PDFRenderer(document);
                BufferedImage image = renderer.renderImageWithDPI(0, 300);
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                ImageIO.write(image, "jpg", outputStream);
                return outputStream.toByteArray();
            }
    }
	
	private String convertImageToBase64(byte[] imageByte) {
        return Base64.getEncoder().encodeToString(imageByte);
    }
   
   private String getGeminiUrl() {
        return "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=" + apiKey;
    }


}
