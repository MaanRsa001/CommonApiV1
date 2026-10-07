package com.maan.eway.thirdparty;


import java.io.File;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.maan.eway.bean.EserviceTravelDetails;
import com.maan.eway.bean.HomePositionMaster;
import com.maan.eway.bean.InsuranceCompanyMaster;
import com.maan.eway.bean.MsHumanDetails;
import com.maan.eway.bean.PersonalInfo;
import com.maan.eway.bean.PolicyCoverData;
import com.maan.eway.bean.TravelPassengerDetails;
import com.maan.eway.calculator.util.RatingFactorsUtil;
import com.maan.eway.common.req.EserviceMotorDetailsSaveRes;
import com.maan.eway.repository.EserviceTravelDetailsRepository;
import com.maan.eway.repository.HomePositionMasterRepository;
import com.maan.eway.repository.InsuranceCompanyMasterRepository;
import com.maan.eway.repository.MsHumanDetailsRepository;
import com.maan.eway.repository.PersonalInfoRepository;
import com.maan.eway.repository.PolicyCoverDataRepository;
import com.maan.eway.repository.ProductGroupMasterRepository;
import com.maan.eway.repository.ProductSectionMasterRepository;
import com.maan.eway.repository.TravelPassengerDetailsRepository;
import com.maan.eway.req.calcengine.CalcEngine;
import com.maan.eway.res.calc.Cover;
import com.maan.eway.service.FactorRateRequestDetailsService;
import com.maan.eway.service.impl.FactorRatePersistenceService;
import com.maan.eway.thirdparty.Mapfre.bean.TravelIntegrationLog;
import com.maan.eway.thirdparty.Mapfre.repo.MapfreIntegrationLogRepository;
import com.maan.eway.thirdparty.Mapfre.repo.TravelIntegrationLogRepository;
import com.maan.eway.thirdparty.response.GenericResponse;

import jakarta.persistence.Tuple;

@Service
public class TravelApiIntegration {
	
	@Autowired
	private InsuranceCompanyMasterRepository insuranceRepo;
	@Autowired
	private PolicyCoverDataRepository policyCoverData;
	
	@Autowired
	private HomePositionMasterRepository homerepo;
	
	@Autowired
	private PersonalInfoRepository personalrepo;
	
	@Autowired
	private TravelPassengerDetailsRepository travelPassengerRepo;
	
	@Autowired
	private EserviceTravelDetailsRepository eserTraRepo;
	
	@Autowired
	private MapfreIntegrationLogRepository mapfreRepo;
	
	@Autowired
	private ProductSectionMasterRepository productSectionMasterRepository;
	
	private final MsHumanDetailsRepository msHumanRepo;
	
	 @Autowired
     private TravelIntegrationLogRepository travelIntegrationLogRepository;
	
	@Autowired
	ProductGroupMasterRepository productGroupMasterRepo;
	
	@Value("${travel.phoenix.getOTA.url}")
	private String getOTAURL;
	
	@Value("${travel.phoenix.confirmpurchase.url}")
	private String getConfirmURL;
	
	@Value(value = "${report.file.path}")
	private String policyReportPath;
	
	
	private FactorRatePersistenceService fservice;
	private RatingFactorsUtil ratingutil;
	private List<Tuple> commontbl;
	private List<Tuple> vehicles;
	public TravelApiIntegration(FactorRatePersistenceService fservice, RatingFactorsUtil ratingutil, List<Tuple> commontbl, List<Tuple> vehicles,MsHumanDetailsRepository msHumanRepo,
			EserviceTravelDetailsRepository eserTraRepo) {
		this.fservice=fservice;
		this.ratingutil=ratingutil;
		this.commontbl=commontbl;
		this.vehicles=vehicles;
		this.msHumanRepo = msHumanRepo;
		this.eserTraRepo=eserTraRepo;


	}



	public EserviceMotorDetailsSaveRes pushZeus_GetAvailablePlansOTAWithRiders(CalcEngine engine) {
		try {
			String API_URL = "http://65.0.113.151:8085/TravelIntegration/zeus/GetAvailablePlansOTAWithRiders";
//			List<Tuple> infant = vehicles.stream().filter(t -> "1".equals(t.get("groupId").toString())).collect(Collectors.toList());
//			long  InfantSum = infant.stream().mapToLong(t->Long.parseLong(t.get("groupCount").toString())).sum();
//
//			List<Tuple> childs = vehicles.stream().filter(t -> "1".equals(t.get("groupId").toString())).collect(Collectors.toList());
//			long  childSum = childs.stream().mapToLong(t->Long.parseLong(t.get("groupCount").toString())).sum();
//			List<Tuple> adults = vehicles.stream().filter(t -> !"1".equals(t.get("groupId").toString())).collect(Collectors.toList());
//			long adultsum = adults.stream().mapToLong(t->Long.parseLong(t.get("groupCount").toString())).sum();
			
//			List<Tuple> infants = vehicles.stream()
//			    .filter(t -> "1".equals(t.get("groupId").toString()))
//			    .collect(Collectors.toList());
//
//			long infantSum = infants.stream()
//			    .mapToLong(t -> Long.parseLong(t.get("groupCount").toString()))
//			    .sum();
//
//			List<Tuple> children = vehicles.stream()
//			    .filter(t -> "2".equals(t.get("groupId").toString()))
//			    .collect(Collectors.toList());
//
//			long childSum = children.stream()
//			    .mapToLong(t -> Long.parseLong(t.get("groupCount").toString()))
//			    .sum();
//
//			List<Tuple> adults = vehicles.stream()
//			    .filter(t -> "3".equals(t.get("groupId").toString()))
//			    .collect(Collectors.toList());
//
//			long adultSum = adults.stream()
//			    .mapToLong(t -> Long.parseLong(t.get("groupCount").toString()))
//			    .sum();
//
//			
//			Tuple tuple=vehicles.get(0);
			
			List<MsHumanDetails> humans = msHumanRepo.findByRequestReferenceNo(engine.getRequestReferenceNo());

			 EserviceTravelDetails travelDetail = eserTraRepo.findByRequestReferenceNo(engine.getRequestReferenceNo());

			// Default to "N/A" in case values are missing
			String currency = "AED";
			String sourceCountry = "AE";
			String destinationCountry = "US";

			// Use Optional to fetch first non-null values
			Optional<MsHumanDetails> anyHuman = humans.stream().findFirst();

			if (anyHuman.isPresent()) {
			    MsHumanDetails first = anyHuman.get();
			    currency = first.getCurrency() != null ? first.getCurrency() : currency;
			    sourceCountry = first.getSourceCountry() != null ? first.getSourceCountry() : sourceCountry;
			    destinationCountry = first.getDestinationCountry() != null ? first.getDestinationCountry() : destinationCountry;
			}

			// Group count calculation
			long infantSum = humans.stream()
			    .filter(h -> h.getGroupId() != null && h.getGroupId() == 1)
			    .mapToLong(h -> h.getGroupCount() != null ? h.getGroupCount() : 0)
			    .sum();

			long childSum = humans.stream()
			    .filter(h -> h.getGroupId() != null && h.getGroupId() == 2)
			    .mapToLong(h -> h.getGroupCount() != null ? h.getGroupCount() : 0)
			    .sum();

			long adultSum = humans.stream()
			    .filter(h -> h.getGroupId() != null && h.getGroupId() == 3)
			    .mapToLong(h -> h.getGroupCount() != null ? h.getGroupCount() : 0)
			    .sum();


			// Setting up headers
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.setAccept(List.of(MediaType.ALL));

			// Creating request body
			Map<String, Object> requestBody = new HashMap<>();
			Map<String, Object> genericRequest = new HashMap<>();
			Map<String, Object> authentication = new HashMap<>();
			authentication.put("Username", "UAT_DEMO_ZM");
			authentication.put("Password", "fplJPnsUp6bk");

			genericRequest.put("Authentication", authentication);

			Map<String, Object> header = new HashMap<>();
			header.put("Channel", "IBE_B2BZM");
			header.put("Currency", "ZMW");                     
			header.put("CountryCode", "ZW"); 
			header.put("CultureCode", "EN");
			header.put("TotalAdults", String.valueOf(adultSum)); 
			header.put("TotalChild", String.valueOf(childSum));  
			header.put("TotalInfants", String.valueOf(infantSum));
//			header.put("TotalInfants", "0");//this key 
			header.put("TotalPackagePrice", "");
			header.put("Attachment", "");
			header.put("PackageType", "");

			genericRequest.put("Header", header);

			SimpleDateFormat DD_MM_YYYY = new SimpleDateFormat("yyyy-MM-dd");

			Map<String, Object> flights = new HashMap<>();
			flights.put("DepartCountryCode",  "ZM"); 
			flights.put("DepartStationCode", ""); 
			flights.put("ArrivalCountryCode", travelDetail.getDestinationCountry());
			flights.put("ArrivalStationCode", "");
			flights.put("DepartAirlineCode", "");
			flights.put("DepartDateTime", DD_MM_YYYY.format(engine.getEffectiveDate()));//this key
			flights.put("ReturnAirlineCode", "");
			flights.put("ReturnDateTime", DD_MM_YYYY.format(engine.getPolicyEndDate()));//this key
			flights.put("DepartFlightNo", "");
			flights.put("ReturnFlightNo", "");

			genericRequest.put("Flights", flights);
			requestBody.put("GenericRequestOTALite", genericRequest);

			System.out.println(new Gson().toJson(requestBody)); ;
			// Wrapping request body and headers in HttpEntity
			HttpEntity<String> requestEntity = new HttpEntity<>( new Gson().toJson(requestBody).toString(), headers);

			RestTemplate restTemplate = new RestTemplate();	        
			ResponseEntity<GenericResponse> responseapi = restTemplate.exchange(
					API_URL,
					HttpMethod.POST,
					requestEntity,
					//new ParameterizedTypeReference<List<AvailablePlan>>() {}
					new ParameterizedTypeReference<GenericResponse>() {}
					);

			System.out.println("JSON: " + responseapi.getStatusCode());

			System.out.println("Response Status Code: " + responseapi.getStatusCode());
			System.out.println("Response Body: " + responseapi.getBody());
			GenericResponse body = responseapi.getBody();
			CoverAvailablePlan mapper=new CoverAvailablePlan(engine);
	        List<Cover> retc = body.getAvailablePlan().stream().map(mapper).collect(Collectors.toList());	         
	        CoverAvailableUpsellPlan umapper=new CoverAvailableUpsellPlan(engine);
	        List<Cover> collect = body.getAvailableUpsellPlans().stream().map(umapper).collect(Collectors.toList());
	        retc.addAll(collect);
	        
			EserviceMotorDetailsSaveRes response = new EserviceMotorDetailsSaveRes();
			response.setCoverList(retc);
			response.setResponse("Saved Successfully");
			response.setRequestReferenceNo(engine.getRequestReferenceNo());
			// response.setCustomerReferenceNo(req.getCustomerReferenceNo());
			response.setVehicleId(engine.getVehicleId());
			response.setVdRefNo(engine.getVdRefNo());
			response.setCdRefNo(engine.getCdRefNo());
			response.setInsuranceId(engine.getInsuranceId());
			response.setSectionId(engine.getSectionId());
			response.setCreatedBy(engine.getCreatedBy());
			response.setProductId(engine.getProductId());
			response.setLocationId(engine.getLocationId());
			response.setMsrefno(engine.getMsrefno());
			response.setUpdateas(null);
			response.setUwList(null);
			response.setReferals(null);
			response.setCoverId(engine.getCoverId());
			fservice.saveFactorRateRequestDetails(response);
			return response;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	 public Map<String, Object> push_confirmPurchace(String quoteNo, String requestReferenceNo) {
	    
//		 ResponseEntity<Map<String, Object>> response=null;
	    	Map<String,Object> resMap = new HashMap<String,Object>();
		 try {

	        final String API_URL = "http://65.0.113.151:8085/TravelIntegration/zeus/ConfirmPurchase";
	        String policyURLLink=null;

	        HomePositionMaster home = homerepo.findByQuoteNo(quoteNo);
	        EserviceTravelDetails travelDetail = eserTraRepo.findByRequestReferenceNo(requestReferenceNo);
	        PersonalInfo customerDetail = personalrepo.findByCustomerId(home.getCustomerId());

	        // Fetch passengers
	        List<TravelPassengerDetails> travelPassengers =
	                travelPassengerRepo.findByQuoteNoAndRequestReferenceNo(quoteNo, requestReferenceNo);

	        // Product group mapping for counting passenger categories
//	        Map<Integer, String> groupMap = productGroupMasterRepo
//	                .findByCompanyIdAndProductId(home.getCompanyId(), home.getProductId())
//	                .stream()
//	                .collect(Collectors.toMap(ProductGroupMaster::getGroupId, ProductGroupMaster::getGroupDesc));
//
//	        int totalInfants = 0, totalChild = 0, totalAdult = 0;
//
//	        for (TravelPassengerDetails passenger : travelPassengers) {
//	            String groupName = groupMap.get(passenger.getGroupId());
//	            if (groupName == null) continue;
//	            switch (groupName.toLowerCase()) {
//	                case "infant(0-0.25)" -> totalInfants++;
//	                case "child(0.33-18)" -> totalChild++;
//	                case "adult(19-100)" -> totalAdult++;
//	            }
//	        }

	        // Travel duration
	        long days = ChronoUnit.DAYS.between(
	                travelDetail.getTravelStartDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate(),
	                travelDetail.getTravelEndDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
	        ) + 1;

	        System.out.println("Total travel days: " + days);

	        // Fetch policy cover data
	        List<PolicyCoverData> codes = policyCoverData.findByQuoteNoAndCoverageType(quoteNo, "B");
	        String ssrfeeCode = codes.get(0).getCoverBasedOn();
	        String planCode = codes.get(0).getRegulatoryCode();

	        // Get currency and country info
	        List<MsHumanDetails> humans = msHumanRepo.findByRequestReferenceNo(requestReferenceNo);
	        MsHumanDetails firstHuman = humans.stream().findFirst().orElse(null);
	        
	        Optional<MsHumanDetails> anyHuman = humans.stream().findFirst();

			// Group count calculation
			long infantSum = humans.stream()
			    .filter(h -> h.getGroupId() != null && h.getGroupId() == 1)
			    .mapToLong(h -> h.getGroupCount() != null ? h.getGroupCount() : 0)
			    .sum();

			long childSum = humans.stream()
			    .filter(h -> h.getGroupId() != null && h.getGroupId() == 2)
			    .mapToLong(h -> h.getGroupCount() != null ? h.getGroupCount() : 0)
			    .sum();

			long adultSum = humans.stream()
			    .filter(h -> h.getGroupId() != null && h.getGroupId() == 3)
			    .mapToLong(h -> h.getGroupCount() != null ? h.getGroupCount() : 0)
			    .sum();
			 SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");

			    
	        // Prepare headers
	        HttpHeaders headers = new HttpHeaders();
	        headers.setContentType(MediaType.APPLICATION_JSON);
	        headers.setAccept(List.of(MediaType.ALL));

	        Map<String, Object> requestBody = new HashMap<>();

	        // Authentication
	        Map<String, Object> authentication = Map.of(
	                "Username", "UAT_DEMO_ZM",
	                "Password", "fplJPnsUp6bk"
	        );
	        requestBody.put("Authentication", authentication);

	        // Header
	        Map<String, Object> header = new HashMap<>();
	        header.put("Channel", "IBE_B2BZM");
	        header.put("Currency", home.getCurrency());
	        header.put("CountryCode", "ZM");
	        header.put("CultureCode", "EN");
	        header.put("TotalAdults",adultSum);
	        header.put("TotalChild", childSum);
	        header.put("TotalInfants", infantSum);
	        header.put("TotalPackagePrice", "");
	        header.put("Attachment", "");
	        header.put("PackageType", "");
	        header.put("ItineraryID", "");
	        header.put("PNR", home.getQuoteNo());
	        header.put("PolicyNo", "");
	        header.put("PurchaseDate", travelDetail.getEffectiveDate());
	        header.put("SSRFeeCode", ssrfeeCode); 
	        header.put("FeeDescription", "");

	        List<InsuranceCompanyMaster> insInfo = insuranceRepo
	                .findByCompanyIdAndStatusAndEffectiveDateStartBeforeAndEffectiveDateEndAfter(
	                        home.getCompanyId(), "Y", new Date(), new Date()
	                );

	        BigDecimal totalPremium = insInfo.get(0).getCurrencyId().equals(home.getCurrency())
	                ? home.getOverallPremiumLc()
	                : home.getOverallPremiumFc();

	        header.put("TotalPremium", String.valueOf(totalPremium));
	        // header.put("PaymentMethod", "");
	        // header.put("PaymentReference", "");
	        requestBody.put("Header", header);

	        // ContactDetails
	        Map<String, Object> contactDetails = new HashMap<>();
	        contactDetails.put("ContactPerson", customerDetail.getClientName());
	        contactDetails.put("Address1", "");
	        contactDetails.put("Address2", "");
	        contactDetails.put("Address3", "");
	        contactDetails.put("HomePhoneNum", "");
	        contactDetails.put("MobilePhoneNum", "");
	        contactDetails.put("OtherPhoneNum", "");
	        contactDetails.put("PostCode", "");
	        contactDetails.put("City", "");
	        contactDetails.put("State", "");
	        contactDetails.put("Country", "");
	        contactDetails.put("EmailAddress", customerDetail.getEmail1());
	        requestBody.put("ContactDetails", contactDetails);

//	         ApplicantDetail (keep commented)
	         Map<String, Object> applicantDetail = new HashMap<>();
	         applicantDetail.put("Name", customerDetail.getFirstName() + " " +customerDetail.getLastName());
	         String custformattedDob = outputFormat.format(customerDetail.getDobOrRegDate()); 
	         applicantDetail.put("DOB", custformattedDob);
	         applicantDetail.put("Contact", customerDetail.getMobileCode2() + customerDetail.getMobileNo1());
	         applicantDetail.put("Address", "Zambia");
	         requestBody.put("ApplicantDetail", applicantDetail);

	        // Flights array
	        Map<String, Object> flight = Map.of(
	                "DepartCountryCode", "ZM",
	                "DepartStationCode", "",
	                "ArrivalCountryCode", travelDetail.getDestinationCountry(),
	                "ArrivalStationCode", "",
	                "DepartAirlineCode", "",
	                "DepartDateTime", new SimpleDateFormat("yyyy-MM-dd").format(travelDetail.getTravelStartDate()),
	                "ReturnAirlineCode", "",
	                "ReturnDateTime", new SimpleDateFormat("yyyy-MM-dd").format(travelDetail.getTravelEndDate()),
	                "DepartFlightNo", "",
	                "ReturnFlightNo", ""
	        );
	        requestBody.put("Flights", List.of(flight));

	        // Passengers
	        List<Map<String, Object>> passengerList = new ArrayList<>();
	        for (TravelPassengerDetails tp : travelPassengers) {
	            Map<String, Object> passenger = new HashMap<>();
	            passenger.put("IsInfant", infantSum != 0 ? "0" : String.valueOf(infantSum));
	            passenger.put("FirstName", tp.getPassengerFirstName());
	            passenger.put("LastName", tp.getPassengerLastName());
	            passenger.put("Gender", tp.getGenderDesc());
			    String formattedDob = outputFormat.format(tp.getDob()); 

			    passenger.put("DOB", formattedDob);
	            passenger.put("Age", String.valueOf(tp.getAge()));
	            passenger.put("IdentityType", "Passport");
	            passenger.put("IdentityNo", tp.getPassportNo());
	            passenger.put("IsQualified", "1");
	            passenger.put("Nationality", "ZM");
	            passenger.put("CountryOfResidence", "ZM");
	            passenger.put("SelectedPlanCode", planCode); 
	            passenger.put("SelectedSSRFeeCode", ssrfeeCode); 
	            passenger.put("CurrencyCode", tp.getCurrency());
	            passenger.put("PassengerPremiumAmount", tp.getOverallPremiumLc());
	            passenger.put("EmailAddress", "");
	            passenger.put("PhoneNumber", "");
	            passenger.put("Address", "");
	            // passenger.put("PassportIssueDate", "");
	            // passenger.put("PassportExpiryDate", "");
	            passengerList.add(passenger);
	        }
	        requestBody.put("Passengers", Map.of("Passengers", passengerList));
	        
	        HttpEntity<String> requestEntity = new HttpEntity<>( new Gson().toJson(requestBody).toString(), headers);

	        // API call
	        LocalDateTime requestTime = LocalDateTime.now();
	        LocalDateTime responseTime;
	        String status = "PENDING";
	        String policyNo = null;
	        String requestJson = null;
	        String responseJson = null;

	        try {
	            // Convert request body to JSON
	            requestJson = new ObjectMapper().writeValueAsString(requestBody);

	            // Send request
	            RestTemplate restTemplate = new RestTemplate();
	            System.out.println("Payload: " + requestJson);

	            ResponseEntity<String> response = restTemplate.exchange(
	            	    API_URL,
	            	    HttpMethod.POST,
	            	    requestEntity,
	            	    String.class
	            	);

	            responseTime = LocalDateTime.now();
	            String responseBody = response.getBody();

	            // Parse JSON array
	            ObjectMapper mapper = new ObjectMapper();
	            List<Map<String, Object>> responseList = mapper.readValue(
	                responseBody, new TypeReference<List<Map<String, Object>>>() {}
	            );

	            if (responseList != null && !responseList.isEmpty()) {
	                Map<String, Object> responseMap = responseList.get(0); // Get first object
	                responseJson = mapper.writeValueAsString(responseMap);

	                if (responseMap.get("policyNo") != null) {
	                    status = "SUCCESS";
	                    policyNo = (String) responseMap.get("policyNo");

	                    Map<String, Object> confirmedPassengers = 
	                        (Map<String, Object>) responseMap.get("confirmedPassengers");
	                    if (confirmedPassengers != null) {
	                        List<Map<String, Object>> confirmedPassengerList =
	                            (List<Map<String, Object>>) confirmedPassengers.get("confirmedPassenger");

	                        if (confirmedPassengerList != null && !confirmedPassengerList.isEmpty()) {
	                            Map<String, Object> firstPassenger = confirmedPassengerList.get(0);
	                            policyURLLink = (String) firstPassenger.get("policyURLLink");
	                            System.out.println("Policy URL Link: " + policyURLLink);
	                        }
	                    }
	                } else {
	                    status = "FAILED";
	                }
	            } else {
	                status = "FAILED";
	            }

	            resMap.put("Body", response.getBody());
	            

	        } catch (Exception e) {
	            responseTime = LocalDateTime.now();
	            status = "FAILED";
	            responseJson = e.getMessage();
	            e.printStackTrace();
	        }
	        
	        String pdfFileName = quoteNo + ".pdf";
            String folderPath ="";
            if (policyURLLink != null && !policyURLLink.isEmpty()) {
                HttpClient client = HttpClient.newHttpClient();
                
                HttpRequest request = HttpRequest.newBuilder()
                		.uri(URI.create(policyURLLink))
                		.GET()
                		.build();
                
                folderPath = policyReportPath.replaceAll("PolicyReport", "travelDocuments");
                folderPath.replaceAll("%20", " ");
                File directory = new File(folderPath);
                if (!directory.exists()) {
                    directory.mkdirs();
                }                
                try {
                	HttpResponse<byte[]> httpRes = client.send(request, HttpResponse.BodyHandlers.ofByteArray());
                	byte[] pdfbytes = httpRes.body();
                	Path path = Paths.get(folderPath + pdfFileName);
                	Files.write(path,pdfbytes);
                	System.out.println("PDF file created successfully at: " + path.toAbsolutePath());
                }catch(Exception e) {
                	e.printStackTrace();
                }
            }
	        
	        System.out.println("Response: " + responseJson);
	        
	        resMap.put("PolicyNumber", policyNo);
            resMap.put("PolicyURL", folderPath+pdfFileName);
	        
	       
	        TravelIntegrationLog log = new TravelIntegrationLog();
	        log.setQuoteNo(quoteNo);
	        log.setRequestReferenceNo(requestReferenceNo);
	        log.setRequestPayload(requestJson);
	        log.setResponsePayload(responseJson);
	        log.setRequestTime(requestTime);
	        log.setResponseTime(responseTime);
	        log.setStatus(TravelIntegrationLog.ResponseStatus.valueOf(status));
	        log.setPolicyNumber(policyNo);

	        travelIntegrationLogRepository.save(log);
		 }catch (Exception e) {
	            e.printStackTrace();
	        }

	        return resMap;
	 }
	 
	 

	
}
