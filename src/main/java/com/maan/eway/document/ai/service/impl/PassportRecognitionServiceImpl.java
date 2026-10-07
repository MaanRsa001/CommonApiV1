package com.maan.eway.document.ai.service.impl;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.imageio.ImageIO;

import org.apache.commons.io.FilenameUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.maan.eway.bean.EserviceCustomerDetails;
import com.maan.eway.bean.HomePositionMaster;
import com.maan.eway.bean.MotorDataDetails;
import com.maan.eway.bean.PersonalInfo;
import com.maan.eway.bean.ProductGroupMaster;
import com.maan.eway.document.ai.service.PassportRecognitionService;
import com.maan.eway.document.req.DocumentUploadReq;
import com.maan.eway.repository.EserviceCustomerDetailsRepository;
import com.maan.eway.repository.HomePositionMasterRepository;
import com.maan.eway.repository.MotorDataDetailsRepository;
import com.maan.eway.repository.PersonalInfoRepository;
import com.maan.eway.repository.ProductGroupMasterRepository;

import io.micrometer.common.util.StringUtils;

@Service
public class PassportRecognitionServiceImpl implements PassportRecognitionService{
	
	@Value("${external.api.key}")
    String apiKey;
	
	@Value("${file.directoryPath}")
	private String directoryPath;

	@Value("${file.compressedImg}")
	private String compressedImg;
	
	@Value("${travel.passport.details.save}")
	private String passportSaveApi;
	
	private final RestTemplate restTemplate = new RestTemplate();
	
	@Autowired
	private EserviceCustomerDetailsRepository cusRepo;
	
	@Autowired
	private ProductGroupMasterRepository productGruopMasterRepo;
	
	@Autowired
	private HomePositionMasterRepository homeRepo;
	
	@Autowired
	private PersonalInfoRepository personalInfoRepo;
	
	@Autowired
	private MotorDataDetailsRepository motorDataDetailsRepo;

	@Override
	public JsonNode generateAiResult(MultipartFile file) throws IOException {

		//	byte[] image = pdfToImage(file);
	    //    String base64 = convertImageToBase64(image);
	     //   return extractReplyFromGeminiAi(base64);
			//return null;
			
		//	public byte[] handleFile(MultipartFile file) throws IOException {
			    String contentType = file.getContentType();
			    String base64 ="";
			    if (contentType == null) {
			        throw new IOException("Unknown file type");
			    }else if(contentType.contentEquals("application/pdf")) {
			    	byte[] image = pdfToImage(file);
			    	 base64 = convertImageToBase64(image);
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
			    return extractReplyFromGeminiAi(base64, contentType);

			   // switch (contentType) {
			   //     case "application/pdf":
			   //         return pdfToImage(file); // PDF → Image byte[]
			    //    case "image/jpeg":
			   //     case "image/png":
			   //     case "image/gif":
			   //         return file.getBytes(); // Already an image
			     //   case "text/plain":
			     //       return handleTextFile(file); // Optional: text → byte[]
			     //   default:
			     //       throw new IOException("Unsupported file type: " + contentType);
			   // }
			//}

		
	}
	
	private JsonNode extractReplyFromGeminiAi(String base64,String fileType) {



        String url = getGeminiUrl();

        String prompt = "Analyze the provided import declaration data. Extract only the English values. Find the exact JSON keys. If a key's value is not available, set it to null. Format all dates as 'dd/MM/yyyy'. Calculate the age based on the Date of Birth and the current date. Respond ONLY in valid JSON with a single top-level object containing a data key. The data object should include the following keys: Type, CountryCode, PassportNo, GivenName, Surname, Nationality, Sex, DateofBirth, PlaceofBirth, PlaceofIssue, DateofIssue, DateofExpiry. The Age key should also be included inside the data object. Do not include any extra explanation or text outside the JSON block";
        
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

        ResponseEntity<JsonNode> response = restTemplate.exchange(
                "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=" + apiKey,
                HttpMethod.POST,
                entity,
                JsonNode.class
        );
        
      //  JsonNode candidates = response.getBody().path("candidates");

       // HttpHeaders headers = new HttpHeaders();
       // headers.setContentType(MediaType.APPLICATION_JSON);
       // HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

      //  RestTemplate restTemplate = new RestTemplate();
      //  ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

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
    
	}
	
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
	        return "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=" + apiKey;
	    }

	   @Override
		public JsonNode pinCertificateAiResult(MultipartFile file) throws IOException {
		   try {
			   Random random = new Random();
				Timestamp timestamp = new Timestamp(System.currentTimeMillis());

				String newfilename = "";
				String newfilename1 = "";
				// OrginalFile
				Path destination = Paths.get(directoryPath);
				newfilename = "KRAPIN"+random.nextInt(100)
						+ timestamp.toString().replace(":", "T").replace(" ", "S").replace("-", "H").replace(".", "D") + "."
						+ FilenameUtils.getExtension(file.getOriginalFilename());
			//	Files.copy(file.getInputStream(), destination.resolve(newfilename));
				System.out.println("Original File Name :"+ newfilename);

				Timestamp timestamp1 = new Timestamp(System.currentTimeMillis());
				// BackupFile
				Path destination1 = Paths.get(compressedImg);
				newfilename1 = "KRAPIN"+random.nextInt(100)
						+ timestamp1.toString().replace(":", "T").replace(" ", "S").replace("-", "H").replace(".", "D")
						+ "." + FilenameUtils.getExtension(file.getOriginalFilename());
				System.out.println("BackUp File Name :"+ newfilename1);
				 String contentType = file.getContentType();
				    String base64 ="";
				    if (contentType == null) {
				        throw new IOException("Unknown file type");
				    }else if(contentType.contentEquals("application/pdf")) {
				    	byte[] image = pdfToImage(file);
				    	 base64 = convertImageToBase64(image);
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
				    return extractPinCertificateResultFromGeminiAi(base64, contentType); 
		   }catch(Exception e) {
				e.printStackTrace();
				return null;
			}
		  
		}

		private JsonNode extractPinCertificateResultFromGeminiAi(String base64, String fileType) {


			 System.out.println("AI EXTRACTION START FOR KRA PIN");

	        String url = getGeminiUrl();

	        String prompt = "Analyze the provided import declaration data. Extract only the English values. Find the exact JSON keys. If a key's value is not available, set it to null. Respond ONLY in valid JSON with a single top-level object containing a data key. The data object should include the following keys: CertificateDate, PersonalIdNo, Name, EmailAddress, LRNumber, StreetOrRoad, Country, TaxArea, POBox, Building, CityOrTown, District, Station, PostalCode. Do not include any extra explanation or text outside the JSON block";
	        
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

	        ResponseEntity<JsonNode> response = restTemplate.exchange(
	                "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=" + apiKey,
	                HttpMethod.POST,
	                entity,
	                JsonNode.class
	        );
	        
	      //  JsonNode candidates = response.getBody().path("candidates");

	       // HttpHeaders headers = new HttpHeaders();
	       // headers.setContentType(MediaType.APPLICATION_JSON);
	       // HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

	      //  RestTemplate restTemplate = new RestTemplate();
	      //  ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

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
		}

		@Override
		public Boolean checkPinNumber(String pinNo, Object req) {
			try {
				System.out.println("PIN Number :"+ pinNo);
				ObjectMapper mapper = new ObjectMapper();
				Map<String,Object> keys = mapper.readValue(req.toString(), new TypeReference<Map<String, Object>>() {});
				
				String quoteNo = keys.get("QuoteNo") == null ? null : keys.get("QuoteNo").toString();
				String cusRefNo = keys.get("CustomerReferenceNo") == null ? null : keys.get("CustomerReferenceNo").toString();
				
				EserviceCustomerDetails cusDetails = cusRepo.findByCustomerReferenceNo(cusRefNo);
				
				String kraPin = cusDetails.getKraPin() == null ? null : cusDetails.getKraPin();
				if(StringUtils.isNotBlank(kraPin)) {
					if(!pinNo.equalsIgnoreCase(kraPin)) {
						 return true;
					}
				}else {
					return true;
				}
				
				return false;	
			}catch(Exception e) {
				e.printStackTrace();
				return null;
			}	
		}

		@Override
		public JsonNode savePassportDetails(JsonNode resp,Object request, String token) {
			ResponseEntity<JsonNode> response = null;
			try {
				//String passportReq = resp.get("data").toString();
				ObjectMapper mapper = new ObjectMapper();
				Map<String,Object> objRequest = mapper.readValue(request.toString(), Map.class);
				String requestRefrenceNo ="",productId="",companyId="",createdby="";
				if(objRequest != null) {
					System.out.println("DataComing");
					requestRefrenceNo = objRequest.get("RequestReferenceNo") == null ? null : objRequest.get("RequestReferenceNo").toString();
					productId = objRequest.get("ProductId") == null ? null : objRequest.get("ProductId").toString();
					companyId = objRequest.get("InsuranceId") == null ? null : objRequest.get("InsuranceId").toString();
					createdby = objRequest.get("CreatedBy") == null ? null : objRequest.get("CreatedBy").toString();
				}
				HomePositionMaster home = homeRepo.findFirstByRequestReferenceNo(requestRefrenceNo);
				Map<String,Object> convertReq = mapper.convertValue(resp, Map.class);
				Map<String,Object> passportReq = null;
				String passportType="",passportNo="",givenName="",surName="",nationality="",sex="",dob="",
						placeofBirth="", placeofIssue="", dateofIssue="",dateofExpiry="";
				if(convertReq != null) {
					passportReq = mapper.readValue(mapper.writeValueAsString(convertReq.get("data")), Map.class);
					
					if(passportReq != null) {
						passportType = passportReq.get("Type") == null ? null : passportReq.get("Type").toString();
						passportNo = passportReq.get("PassportNo") == null ? null : passportReq.get("PassportNo").toString();
						givenName = passportReq.get("GivenName") == null ? null : passportReq.get("GivenName").toString();
						surName = passportReq.get("Surname") == null ? null : passportReq.get("Surname").toString();
						nationality = passportReq.get("Nationality") == null ? null : passportReq.get("Nationality").toString();
						sex = passportReq.get("Sex") == null ? null : passportReq.get("Sex").toString();
						dob = passportReq.get("DateofBirth") == null ? null : passportReq.get("DateofBirth").toString();
						placeofBirth = passportReq.get("PlaceofBirth") == null ? null : passportReq.get("PlaceofBirth").toString();
						placeofIssue = passportReq.get("PlaceofIssue") == null ? null : passportReq.get("PlaceofIssue").toString();
						dateofIssue = passportReq.get("DateofIssue") == null ? null : passportReq.get("DateofIssue").toString();
						dateofExpiry = passportReq.get("DateofExpiry") == null ? null : passportReq.get("DateofExpiry").toString();
						
						//Age calculation - for Age Band
						DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
						LocalDate dateOfbirth = LocalDate.parse(dob,formatter);
						LocalDate sysDate = LocalDate.now();
						int age = Period.between(dateOfbirth, sysDate).getYears();
						System.out.println("Age :"+age);
						
						ProductGroupMaster ageBand = productGruopMasterRepo.findByCompanyIdAndProductIdAndGroupFromLessThanEqualAndGroupToGreaterThanEqual(
								companyId,Integer.valueOf(productId),Integer.valueOf(age),Integer.valueOf(age));
						String bandId= ageBand.getGroupId().toString();
						String bandDesc = ageBand.getBandDesc();
						
						System.out.println("BandId :"+bandId+" BandDesc :"+ bandDesc);
						
						//SaveRequest form
						Map<String,Object> SaveReq = new HashMap<>();
						SaveReq.put("CreatedBy", createdby);
						SaveReq.put("QuoteNo", home.getQuoteNo());
						SaveReq.put("LocationId", "1");
						
						Map<String,Object> passengerReq = new HashMap<>();
						passengerReq.put("GroupId",bandId);
						passengerReq.put("GroupDesc",bandDesc);
						passengerReq.put("GroupSuminsured","");
						passengerReq.put("Dob",dob);
						passengerReq.put("GenderId",sex);
						passengerReq.put("Nationality",nationality);
						passengerReq.put("PassengerFirstName",givenName);
						passengerReq.put("PassengerLastName",surName);
						passengerReq.put("PassportNo",passportNo);
						passengerReq.put("RelationId","009");
						passengerReq.put("PassportType", passportType);
						passengerReq.put("PlaceOfBirth",placeofBirth);
						passengerReq.put("PlaceOfIssue",placeofIssue);
						passengerReq.put("PassportIssueDate",dateofIssue);
						passengerReq.put("PassportExpiryDate",dateofExpiry);
						/*
                        if("M".equalsIgnoreCase(sex)) {
                        	passengerReq.put("RelationId","009");
						}else if("F".equalsIgnoreCase(sex)) {
							passengerReq.put("RelationId","009");
						}else {
							passengerReq.put("RelationId","009");
						} */
                        
                        SaveReq.put("PassengerList", Arrays.asList(passengerReq));
                        System.out.println("API Req "+mapper.writeValueAsString(SaveReq));
                        
                        HttpHeaders headers = new HttpHeaders();
                        headers.setContentType(MediaType.APPLICATION_JSON);
                        //System.out.println("Token :"+ token);
                        token = token.substring(0, token.indexOf(","));                        
                       // System.out.println("Token :"+ token);
                        headers.set("Authorization", token);

                        HttpEntity<String> entity = new HttpEntity<>(mapper.writeValueAsString(SaveReq), headers);

                        response = restTemplate.exchange(passportSaveApi,HttpMethod.POST, entity,JsonNode.class);   
                        
                        System.out.println(response.getBody());
                        
					}
				}
				//String passportType
				//System.out.println(passportReq);
				
			}catch(Exception e) {
				e.printStackTrace();
			}
			return response.getBody();
		}

		@Override
		public JsonNode generateDocResultFromAI(MultipartFile file,Object request) throws IOException {

			   try {
				   Random random = new Random();
					Timestamp timestamp = new Timestamp(System.currentTimeMillis());

					String newfilename = "";
					String newfilename1 = "";
					
					ObjectMapper mapper = new ObjectMapper();
					Map<String,Object> req = mapper.readValue(request.toString(),
			                                        new TypeReference<Map<String, Object>>() {});
					String docType = req.get("DocType") == null ? "" : req.get("DocType").toString();
					Path destination = Paths.get(directoryPath);
					String contentType = file.getContentType();
					String base64 = "";
					if ("IdNumber".equalsIgnoreCase(docType)) {
/*
						newfilename = "ID-CERT"
								+ random.nextInt(100) + timestamp.toString().replace(":", "T").replace(" ", "S")
										.replace("-", "H").replace(".", "D")
								+ "." + FilenameUtils.getExtension(file.getOriginalFilename());
						// Files.copy(file.getInputStream(), destination.resolve(newfilename));
						System.out.println("Original File Name :" + newfilename);

						Timestamp timestamp1 = new Timestamp(System.currentTimeMillis());
						// BackupFile
						Path destination1 = Paths.get(compressedImg);
						newfilename1 = "ID-CERT"
								+ random.nextInt(100) + timestamp1.toString().replace(":", "T").replace(" ", "S")
										.replace("-", "H").replace(".", "D")
								+ "." + FilenameUtils.getExtension(file.getOriginalFilename());
						System.out.println("BackUp File Name :" + newfilename1);
						*/
						if (contentType == null) {
							throw new IOException("Unknown file type");
						} else if (contentType.contentEquals("application/pdf")) {
							byte[] image = pdfToImage(file);
							base64 = convertImageToBase64(image);
							contentType = "image/jpeg";
						} else if (contentType.contentEquals("image/jpeg")) {
							base64 = convertImageToBase64(file.getBytes());
						} else if (contentType.contentEquals("image/png")) {
							base64 = convertImageToBase64(file.getBytes());
						} else if (contentType.contentEquals("image/gif")) {
							base64 = convertImageToBase64(file.getBytes());
						} else if (contentType.contentEquals("image/webp")) {
							base64 = convertImageToBase64(file.getBytes());
						} else {
							throw new IOException("Unsupported file type: " + contentType);
						}
						return extractIdCertificateResultFromGeminiAi(base64, contentType);
					} else if ("Passport".equalsIgnoreCase(docType)) {
						 if (contentType == null) {
						        throw new IOException("Unknown file type");
						    }else if(contentType.contentEquals("application/pdf")) {
						    	byte[] image = pdfToImage(file);
						    	 base64 = convertImageToBase64(image);
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
						    return extractReplyFromGeminiAi(base64, contentType);

					}else if("KRAPin".equalsIgnoreCase(docType)) {
						/*
						newfilename = "KRAPIN"
								+ random.nextInt(100) + timestamp.toString().replace(":", "T").replace(" ", "S")
										.replace("-", "H").replace(".", "D")
								+ "." + FilenameUtils.getExtension(file.getOriginalFilename());
						// Files.copy(file.getInputStream(), destination.resolve(newfilename));
						System.out.println("Original File Name :" + newfilename);

						Timestamp timestamp1 = new Timestamp(System.currentTimeMillis());
						// BackupFile
						Path destination1 = Paths.get(compressedImg);
						newfilename1 = "KRAPIN"
								+ random.nextInt(100) + timestamp1.toString().replace(":", "T").replace(" ", "S")
										.replace("-", "H").replace(".", "D")
								+ "." + FilenameUtils.getExtension(file.getOriginalFilename());
						System.out.println("BackUp File Name :" + newfilename1);
						*/
					//	String contentType = file.getContentType();
					//	String base64 = "";
						if (contentType == null) {
							throw new IOException("Unknown file type");
						} else if (contentType.contentEquals("application/pdf")) {
							byte[] image = pdfToImage(file);
							base64 = convertImageToBase64(image);
							contentType = "image/jpeg";
						} else if (contentType.contentEquals("image/jpeg")) {
							base64 = convertImageToBase64(file.getBytes());
						} else if (contentType.contentEquals("image/png")) {
							base64 = convertImageToBase64(file.getBytes());
						} else if (contentType.contentEquals("image/gif")) {
							base64 = convertImageToBase64(file.getBytes());
						} else if (contentType.contentEquals("image/webp")) {
							base64 = convertImageToBase64(file.getBytes());
						} else {
							throw new IOException("Unsupported file type: " + contentType);
						}
						return extractPinCertificateResultFromGeminiAi(base64, contentType);
					}else if("LogBook".equalsIgnoreCase(docType)) {
						/*
						newfilename = "LOGBOOK"
								+ random.nextInt(100) + timestamp.toString().replace(":", "T").replace(" ", "S")
										.replace("-", "H").replace(".", "D")
								+ "." + FilenameUtils.getExtension(file.getOriginalFilename());
						// Files.copy(file.getInputStream(), destination.resolve(newfilename));
						System.out.println("Original File Name :" + newfilename);

						Timestamp timestamp1 = new Timestamp(System.currentTimeMillis());
						// BackupFile
						Path destination1 = Paths.get(compressedImg);
						newfilename1 = "LOGBOOK"
								+ random.nextInt(100) + timestamp1.toString().replace(":", "T").replace(" ", "S")
										.replace("-", "H").replace(".", "D")
								+ "." + FilenameUtils.getExtension(file.getOriginalFilename());
						System.out.println("BackUp File Name :" + newfilename1);
						*/
					//	String contentType = file.getContentType();
					//	String base64 = "";
						if (contentType == null) {
							throw new IOException("Unknown file type");
						} else if (contentType.contentEquals("application/pdf")) {
							byte[] image = pdfToImage(file);
							base64 = convertImageToBase64(image);
							contentType = "image/jpeg";
						} else if (contentType.contentEquals("image/jpeg")) {
							base64 = convertImageToBase64(file.getBytes());
						} else if (contentType.contentEquals("image/png")) {
							base64 = convertImageToBase64(file.getBytes());
						} else if (contentType.contentEquals("image/gif")) {
							base64 = convertImageToBase64(file.getBytes());
						} else if (contentType.contentEquals("image/webp")) {
							base64 = convertImageToBase64(file.getBytes());
						} else {
							throw new IOException("Unsupported file type: " + contentType);
						}
						return extractLogBookCertificateResultFromGeminiAi(base64, contentType);
					}
					// OrginalFile
					
					
			   }catch(Exception e) {
					e.printStackTrace();
					return null;
				}
			  
			return null;
		}

		private JsonNode extractLogBookCertificateResultFromGeminiAi(String base64, String fileType) {


			 System.out.println("AI EXTRACTION START FOR LOGBOOK");

	        String url = getGeminiUrl();

	        String prompt = "Analyze the provided import declaration data. Extract only the English values. Find the exact JSON keys. If a key's value is not available, set it to null. Respond ONLY in valid JSON with a single top-level object containing a data key. The data object should include the following keys: EntryNo, Transfer, Registration, ChasisorFrame, MakeofVehicle, Model, TypeofVehicle, BodyType, FuelType, ManufactureYear, Rating, EngineNo, Colour, DateofRegistration, GrossWeight, Duty, NoofPreviousOwner, NumberofPassengers, TareWeight, TaxClass, LoadCapacity, PreviousRegCountry, PreviousRegistration, RegisteredOwnerPin, RegisteredOwnerName, ChargesPin, ChargesFinancialAssetProvider. Do not include any extra explanation or text outside the JSON block";
	        
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

	        ResponseEntity<JsonNode> response = restTemplate.exchange(
	                "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=" + apiKey,
	                HttpMethod.POST,
	                entity,
	                JsonNode.class
	        );
	        
	      //  JsonNode candidates = response.getBody().path("candidates");

	       // HttpHeaders headers = new HttpHeaders();
	       // headers.setContentType(MediaType.APPLICATION_JSON);
	       // HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

	      //  RestTemplate restTemplate = new RestTemplate();
	      //  ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

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
		}

		private JsonNode extractIdCertificateResultFromGeminiAi(String base64, String fileType) {


			 System.out.println("AI EXTRACTION START FOR NATIONAL ID");

	        String url = getGeminiUrl();

	        String prompt = "Analyze the provided import declaration data. Extract only the English values. Find the exact JSON keys. If a key's value is not available, set it to null. Respond ONLY in valid JSON with a single top-level object containing a data key. The data object should include the following keys: SerialNumber, IdNumber, FullName, DateofBirth, Sex, DisrtictofBirth, PlaceofIssue, DateofIssue, District, Division, Location, SubLocation. Do not include any extra explanation or text outside the JSON block";
	        
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

	        ResponseEntity<JsonNode> response = restTemplate.exchange(
	                "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=" + apiKey,
	                HttpMethod.POST,
	                entity,
	                JsonNode.class
	        );
	        
	      //  JsonNode candidates = response.getBody().path("candidates");

	       // HttpHeaders headers = new HttpHeaders();
	       // headers.setContentType(MediaType.APPLICATION_JSON);
	       // HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

	      //  RestTemplate restTemplate = new RestTemplate();
	      //  ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

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
		}

		@Override
		public Boolean checkIdNumber(String idNo, Object req) {
			try {
				System.out.println("PIN Number :"+ idNo);
				ObjectMapper mapper = new ObjectMapper();
				Map<String,Object> keys = mapper.readValue(req.toString(), new TypeReference<Map<String, Object>>() {});
				
				String quoteNo = keys.get("QuoteNo") == null ? null : keys.get("QuoteNo").toString();
				String cusRefNo = keys.get("CustomerReferenceNo") == null ? null : keys.get("CustomerReferenceNo").toString();
				
			//	EserviceCustomerDetails cusDetails = cusRepo.findByCustomerReferenceNo(cusRefNo);
				HomePositionMaster home = homeRepo.findByQuoteNo(quoteNo);
				PersonalInfo cusDetails = personalInfoRepo.findByCustomerId(home.getCustomerId());
				String idNum = cusDetails.getIdNumber() == null ? null : cusDetails.getIdNumber();
				if(StringUtils.isNotBlank(idNum)) {
					if(!idNo.equalsIgnoreCase(idNum)) {
						 return true;
					}
				}else {
					return true;
				}
				
				return false;	
			}catch(Exception e) {
				e.printStackTrace();
				return null;
			}	
		}

		@Override
		public Boolean checkLogBook(String pinNo, String regNo, String chassisNo, String engineNo, Object req) {
			try {
				System.out.println("PIN Number :"+ pinNo);
				System.out.println("REG Number :"+ regNo);
				System.out.println("Chassis Number :"+ chassisNo);
				System.out.println("Engine Number :"+ engineNo);
				ObjectMapper mapper = new ObjectMapper();
				Map<String,Object> keys = mapper.readValue(req.toString(), new TypeReference<Map<String, Object>>() {});
				
				String quoteNo = keys.get("QuoteNo") == null ? null : keys.get("QuoteNo").toString();
				String cusRefNo = keys.get("CustomerReferenceNo") == null ? null : keys.get("CustomerReferenceNo").toString();
				
			//	EserviceCustomerDetails cusDetails = cusRepo.findByCustomerReferenceNo(cusRefNo);
				HomePositionMaster home = homeRepo.findByQuoteNo(quoteNo);
				PersonalInfo cusDetails = personalInfoRepo.findByCustomerId(home.getCustomerId());
				String idNum = cusDetails.getKraPin() == null ? null : cusDetails.getKraPin();
				List<MotorDataDetails> motDet = motorDataDetailsRepo.findByQuoteNoOrderByVehicleIdAsc(quoteNo);
				
				boolean checkLockbook = motDet.stream()
				        .anyMatch(d ->
				                d.getRegistrationNumber().equalsIgnoreCase(regNo)
				                && d.getChassisNumber().equalsIgnoreCase(chassisNo)
				                && d.getEngineNumber().equalsIgnoreCase(engineNo)
				        );
				if(StringUtils.isNotBlank(idNum)) {
					if((!pinNo.equalsIgnoreCase(idNum)) && (checkLockbook) ) {
						 return true;
					}
				}else {
					return true;
				}
				
				return false;	
			}catch(Exception e) {
				e.printStackTrace();
				return null;
			}	
		}

		

		@Override
		public JsonNode CheckValidationWithAi(MultipartFile file, String documentName) {

			   try {
				   
					String contentType = file.getContentType();
					String base64 = "";
					if ("National ID".equalsIgnoreCase(documentName)) {

						if (contentType == null) {
							throw new IOException("Unknown file type");
						} else if (contentType.contentEquals("application/pdf")) {
							byte[] image = pdfToImage(file);
							base64 = convertImageToBase64(image);
							contentType = "image/jpeg";
						} else if (contentType.contentEquals("image/jpeg")) {
							base64 = convertImageToBase64(file.getBytes());
						} else if (contentType.contentEquals("image/png")) {
							base64 = convertImageToBase64(file.getBytes());
						} else if (contentType.contentEquals("image/gif")) {
							base64 = convertImageToBase64(file.getBytes());
						} else if (contentType.contentEquals("image/webp")) {
							base64 = convertImageToBase64(file.getBytes());
						} else {
							throw new IOException("Unsupported file type: " + contentType);
						}
						return extractIdCertificateResultFromGeminiAi(base64, contentType);
					}else if("KRA Pin".equalsIgnoreCase(documentName)) {
						
						if (contentType == null) {
							throw new IOException("Unknown file type");
						} else if (contentType.contentEquals("application/pdf")) {
							byte[] image = pdfToImage(file);
							base64 = convertImageToBase64(image);
							contentType = "image/jpeg";
						} else if (contentType.contentEquals("image/jpeg")) {
							base64 = convertImageToBase64(file.getBytes());
						} else if (contentType.contentEquals("image/png")) {
							base64 = convertImageToBase64(file.getBytes());
						} else if (contentType.contentEquals("image/gif")) {
							base64 = convertImageToBase64(file.getBytes());
						} else if (contentType.contentEquals("image/webp")) {
							base64 = convertImageToBase64(file.getBytes());
						} else {
							throw new IOException("Unsupported file type: " + contentType);
						}
						return extractPinCertificateResultFromGeminiAi(base64, contentType);
					}else if("Vehicle logbook".equalsIgnoreCase(documentName)) {
						
						if (contentType == null) {
							throw new IOException("Unknown file type");
						} else if (contentType.contentEquals("application/pdf")) {
							byte[] image = pdfToImage(file);
							base64 = convertImageToBase64(image);
							contentType = "image/jpeg";
						} else if (contentType.contentEquals("image/jpeg")) {
							base64 = convertImageToBase64(file.getBytes());
						} else if (contentType.contentEquals("image/png")) {
							base64 = convertImageToBase64(file.getBytes());
						} else if (contentType.contentEquals("image/gif")) {
							base64 = convertImageToBase64(file.getBytes());
						} else if (contentType.contentEquals("image/webp")) {
							base64 = convertImageToBase64(file.getBytes());
						} else {
							throw new IOException("Unsupported file type: " + contentType);
						}
						return extractLogBookCertificateResultFromGeminiAi(base64, contentType);
					}else if("Certificate of Incorporation".equalsIgnoreCase(documentName)) {
						
						if (contentType == null) {
							throw new IOException("Unknown file type");
						} else if (contentType.contentEquals("application/pdf")) {
							byte[] image = pdfToImage(file);
							base64 = convertImageToBase64(image);
							contentType = "image/jpeg";
						} else if (contentType.contentEquals("image/jpeg")) {
							base64 = convertImageToBase64(file.getBytes());
						} else if (contentType.contentEquals("image/png")) {
							base64 = convertImageToBase64(file.getBytes());
						} else if (contentType.contentEquals("image/gif")) {
							base64 = convertImageToBase64(file.getBytes());
						} else if (contentType.contentEquals("image/webp")) {
							base64 = convertImageToBase64(file.getBytes());
						} else {
							throw new IOException("Unsupported file type: " + contentType);
						}
						return extractInCorporationCertificateResultFromGeminiAi(base64, contentType);
					}else if("CR12".equalsIgnoreCase(documentName)) {
						
						if (contentType == null) {
							throw new IOException("Unknown file type");
						} else if (contentType.contentEquals("application/pdf")) {
							byte[] image = pdfToImage(file);
							base64 = convertImageToBase64(image);
							contentType = "image/jpeg";
						} else if (contentType.contentEquals("image/jpeg")) {
							base64 = convertImageToBase64(file.getBytes());
						} else if (contentType.contentEquals("image/png")) {
							base64 = convertImageToBase64(file.getBytes());
						} else if (contentType.contentEquals("image/gif")) {
							base64 = convertImageToBase64(file.getBytes());
						} else if (contentType.contentEquals("image/webp")) {
							base64 = convertImageToBase64(file.getBytes());
						} else {
							throw new IOException("Unsupported file type: " + contentType);
						}
						return extractCR12CertificateResultFromGeminiAi(base64, contentType);
					}
					// OrginalFile
					
					
			   }catch(Exception e) {
					e.printStackTrace();
					return null;
				}
			  
			return null;
		}

		private JsonNode extractCR12CertificateResultFromGeminiAi(String base64, String fileType) {



			System.out.println("AI EXTRACTION START FOR CR12");
			
	        String url = getGeminiUrl();

	        String prompt = "Analyze the provided import declaration data. Extract only the English values. Find the exact JSON keys. If a key's value is not available, set it to null. Respond ONLY in valid JSON with a single top-level object containing a data key. The data object should include the following keys: Company, CompanyNumber, NominalShareCapital, NumberandTypeofShares, DateofRegistration, RegisteredOffice, PostalAddress. Do not include any extra explanation or text outside the JSON block";
	        
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

	        ResponseEntity<JsonNode> response = restTemplate.exchange(
	                "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=" + apiKey,
	                HttpMethod.POST,
	                entity,
	                JsonNode.class
	        );
	        
	      //  JsonNode candidates = response.getBody().path("candidates");

	       // HttpHeaders headers = new HttpHeaders();
	       // headers.setContentType(MediaType.APPLICATION_JSON);
	       // HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

	      //  RestTemplate restTemplate = new RestTemplate();
	      //  ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

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
		}

		private JsonNode extractInCorporationCertificateResultFromGeminiAi(String base64, String fileType) {


          System.out.println("AI EXTRACTION START FOR COI");

	        String url = getGeminiUrl();

	        String prompt = "Analyze the provided import declaration data. Extract only the English values. Find the exact JSON keys. If a key's value is not available, set it to null. Respond ONLY in valid JSON with a single top-level object containing a data key. The data object should include the following keys: Number, CertificateName. Do not include any extra explanation or text outside the JSON block";
	        
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

	        ResponseEntity<JsonNode> response = restTemplate.exchange(
	                "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=" + apiKey,
	                HttpMethod.POST,
	                entity,
	                JsonNode.class
	        );
	        
	      //  JsonNode candidates = response.getBody().path("candidates");

	       // HttpHeaders headers = new HttpHeaders();
	       // headers.setContentType(MediaType.APPLICATION_JSON);
	       // HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

	      //  RestTemplate restTemplate = new RestTemplate();
	      //  ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

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
		}

		@Override
		public Boolean checkValidIdNumber(String idNumber, DocumentUploadReq req) {
			try {
				System.out.println(">>>> VALIDATE ID >>>>");
				System.out.println("ID Number :"+ idNumber);
				
				
			//	EserviceCustomerDetails cusDetails = cusRepo.findByCustomerReferenceNo(cusRefNo);
				HomePositionMaster home = homeRepo.findByQuoteNo(req.getQuoteNo());
				PersonalInfo cusDetails = personalInfoRepo.findByCustomerId(home.getCustomerId());
				String idNum = cusDetails.getIdNumber() == null ? null : cusDetails.getIdNumber();
				if(StringUtils.isNotBlank(idNum)) {
					if(!idNumber.equalsIgnoreCase(idNum)) {
						 return true;
					}
				}else {
					return true;
				}
				
				return false;	
			}catch(Exception e) {
				e.printStackTrace();
				return null;
			}	
		}

		@Override
		public Boolean checkValidPinNumber(String pinNo, DocumentUploadReq req) {
			try {
				System.out.println(">>>> VALIDATE PIN >>>>");
				System.out.println("PIN Number :"+ pinNo);
				
				
				//EserviceCustomerDetails cusDetails = cusRepo.findByCustomerReferenceNo(cusRefNo);
				HomePositionMaster home = homeRepo.findByQuoteNo(req.getQuoteNo());
				PersonalInfo cusDetails = personalInfoRepo.findByCustomerId(home.getCustomerId());
				String kraPin = cusDetails.getKraPin() == null ? null : cusDetails.getKraPin();
				if(StringUtils.isNotBlank(kraPin)) {
					if(!pinNo.equalsIgnoreCase(kraPin)) {
						 return true;
					}
				}else {
					return true;
				}
				
				return false;	
			}catch(Exception e) {
				e.printStackTrace();
				return null;
			}	
		}

		@Override
		public Boolean checkValidLogBook(String regNo, String chassisNo, String engineNo, DocumentUploadReq req) {
			try {
				System.out.println(">>>> VALIDATE LOGBOOK >>>>");
				System.out.println("REG Number :"+ regNo);
				System.out.println("Chassis Number :"+ chassisNo);
				System.out.println("Engine Number :"+ engineNo);
				
			//	EserviceCustomerDetails cusDetails = cusRepo.findByCustomerReferenceNo(cusRefNo);
				List<MotorDataDetails> motDet = motorDataDetailsRepo.findByQuoteNoOrderByVehicleIdAsc(req.getQuoteNo());
				System.out.println("REG Number :"+ motDet.get(0).getRegistrationNumber());
				System.out.println("Chassis Number :"+ motDet.get(0).getChassisNumber());
				System.out.println("Engine Number :"+ motDet.get(0).getEngineNumber());
				boolean checkLockbook = motDet.stream()
				        .anyMatch(d ->
				                d.getRegistrationNumber().equalsIgnoreCase(regNo)
				                && d.getChassisNumber().equalsIgnoreCase(chassisNo)
				                && d.getEngineNumber().equalsIgnoreCase(engineNo)
				        );
				
					if(checkLockbook) {
						 return false;
					}else {
						return true;
					}
					
			}catch(Exception e) {
				e.printStackTrace();
				return null;
			}	
		}

		@Override
		public Boolean checkValidCOI(String pinNo, DocumentUploadReq req) {
			try {
				System.out.println(">>>> VALIDATE COI >>>>");
				System.out.println("COI Number :"+ pinNo);
				//EserviceCustomerDetails cusDetails = cusRepo.findByCustomerReferenceNo(cusRefNo);
				HomePositionMaster home = homeRepo.findByQuoteNo(req.getQuoteNo());
				PersonalInfo cusDetails = personalInfoRepo.findByCustomerId(home.getCustomerId());
				String COI = cusDetails.getIdNumber() == null ? null : cusDetails.getIdNumber();
				if(StringUtils.isNotBlank(COI)) {
					if(!pinNo.equalsIgnoreCase(COI)) {
						 return true;
					}
				}else {
					return true;
				}
				
				return false;	
			}catch(Exception e) {
				e.printStackTrace();
				return null;
			}	
		}

		@Override
		public Boolean checkValidCR12(String compNo, DocumentUploadReq req) {
			try {
				System.out.println(">>>> VALIDATE CR12 >>>>");
				System.out.println("Company Number :"+ compNo);
				//EserviceCustomerDetails cusDetails = cusRepo.findByCustomerReferenceNo(cusRefNo);
				HomePositionMaster home = homeRepo.findByQuoteNo(req.getQuoteNo());
				PersonalInfo cusDetails = personalInfoRepo.findByCustomerId(home.getCustomerId());
				String COI = cusDetails.getIdNumber() == null ? null : cusDetails.getIdNumber();
				if(StringUtils.isNotBlank(COI)) {
					if(!compNo.equalsIgnoreCase(COI)) {
						 return true;
					}
				}else {
					return true;
				}
				
				return false;	
			}catch(Exception e) {
				e.printStackTrace();
				return null;
			}	
		}


}
