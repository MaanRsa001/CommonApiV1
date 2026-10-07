package com.maan.eway.thirdparty.Mapfre.service;

import java.io.File;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.maan.eway.bean.EserviceTravelDetails;
import com.maan.eway.bean.HomePositionMaster;
import com.maan.eway.bean.ListItemValue;
import com.maan.eway.bean.PersonalInfo;
import com.maan.eway.bean.ProductSectionMaster;
import com.maan.eway.bean.TravelPassengerDetails;
import com.maan.eway.repository.EserviceTravelDetailsRepository;
import com.maan.eway.repository.HomePositionMasterRepository;
import com.maan.eway.repository.ListItemValueRepository;
import com.maan.eway.repository.PersonalInfoRepository;
import com.maan.eway.repository.ProductSectionMasterRepository;
import com.maan.eway.repository.TravelPassengerDetailsRepository;
import com.maan.eway.thirdparty.Mapfre.bean.MapfreIntegrationLog;
import com.maan.eway.thirdparty.Mapfre.repo.MapfreIntegrationLogRepository;

@Component
public class MapfreClientIntegration {
	
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
	private ListItemValueRepository listRepo;
	
	@Autowired
	private ProductSectionMasterRepository productSectionMasterRepository;
	
	@Value("${mapfre.url}")
	private String mpfreUrl;
	
	@Value(value = "${report.file.path}")
	private String policyReportPath;
	

    public Map<String, Object> call_mapfreIntegration(String quoteNo,String requestReferenceNo) {
    	
    	ResponseEntity<Map<String, Object>> response=null;
    	Map<String,Object> resMap = new HashMap<String,Object>();
    	
        try {
        	
        	
        	HomePositionMaster home= homerepo.findByQuoteNo(quoteNo);
        	
        	if(home.getOriginalPolicyNo()!=null) {
        		
        	}
        	EserviceTravelDetails travelDetail=eserTraRepo.findByRequestReferenceNo(requestReferenceNo);
        	
        	String customerId=home.getCustomerId();
        	
        	PersonalInfo customerdetail=personalrepo.findByCustomerId(customerId);
        	
        	List<TravelPassengerDetails> travelPassenger= travelPassengerRepo.findByQuoteNoAndRequestReferenceNo(quoteNo, requestReferenceNo);
        	
        	Optional<String> selfPassportNo = travelPassenger.stream()
        	        .filter(p -> p.getRelationId() == 9 || p.getRelationId() == 10)
        	        .map(TravelPassengerDetails::getPassportNo)
        	        .findFirst();
        	
        	System.out.println("Total Passport: " + selfPassportNo);
        	        
        	String passportNo = selfPassportNo.orElse(null);
        	
        	Date startDate = travelDetail.getTravelStartDate(); 
            Date endDate = travelDetail.getTravelEndDate();     

            LocalDate localStartDate = startDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate localEndDate = endDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

            long days = ChronoUnit.DAYS.between(localStartDate, localEndDate) + 1;

            System.out.println("Total travel days: " + days);
        	
        	LocalDate today = LocalDate.now();

        	ProductSectionMaster productsection = productSectionMasterRepository
        	    .findByCompanyIdAndProductIdAndSectionIdAndDateRangeWithMaxAmendId(
        	        home.getCompanyId(),
        	        home.getProductId(),
        	        home.getSectionId(),
        	        today
        	    )
        	    .orElseThrow(() -> new RuntimeException("No active ProductSectionMaster found for the given criteria"));
        	
        	ListItemValue mpafreCode=listRepo.findByItemTypeAndItemCodeAndCompanyId("PLAN_CODE", String.valueOf(home.getSectionId()), home.getCompanyId());
        	
        	String fullMobileNumber = customerdetail.getMobileCode1() + customerdetail.getMobileNo1();

        	
            Map<String, Object> payload = new HashMap<>();
            
            
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

            String policyStartDate = sdf.format(home.getInceptionDate());
            String policyEndDate = sdf.format(home.getExpiryDate());
            String policyExpiryDate = sdf.format(home.getExpiryDate());

            // policyData
            Map<String, Object> policyData = new HashMap<>();
            policyData.put("contratoReplicadoV2", "");
            policyData.put("txtPrecioBrutoTotal", "0");
            policyData.put("txtNpoliza", "AUTO");
            policyData.put("tipoPagador", "");
            policyData.put("txtFhInicio",policyStartDate);
            policyData.put("txtFhFin", policyEndDate);
            policyData.put("txtProducto", mpafreCode.getCategoryLocal());
            policyData.put("txtDivisaProducto", "");
            policyData.put("idRegProducto",mpafreCode.getParam1());
            policyData.put("txtComentario", "");
            policyData.put("idRegFranquicia", "-1");
            policyData.put("idRegDivisaProducto", "");
            policyData.put("idRegRegion", "-1");
            policyData.put("txtFHExpiracion", policyExpiryDate);
            policyData.put("idRegProductoComisionVariable", "-1");
            policyData.put("txtCodDealer", mpafreCode.getRegulatoryCode());
            policyData.put("txtFhCambioDivisa", "");
            policyData.put("txtCodPromocion", "-1");
            policyData.put("txtSufijo",productsection.getSectionNameLocal());
            policyData.put("txtDuracion", String.valueOf(days));
            payload.put("policyData", policyData);

            // riskData
            Map<String, Object> riskData = new HashMap<>();
            riskData.put("txtAttribute020", mpafreCode.getItemTypeLocal());
            riskData.put("txtAttribute019", mpafreCode.getItemValue());
            riskData.put("cmbdestinosv", mpafreCode.getParam2()); 
            riskData.put("txtPaisDestino", travelDetail.getDestinationCountry());
            payload.put("riskData", riskData);

            // insuredData 
            List<Map<String, Object>> insuredList = new ArrayList<>();
            
            SimpleDateFormat inDob = new SimpleDateFormat("yyyy-MM-dd");

            for (TravelPassengerDetails passenger : travelPassenger) {
                Map<String, Object> insured = new HashMap<>();
                insured.put("txtNmAsegurado", "Test");   //passenger.getPassengerFirstName()
                insured.put("txtApeAsegurado","Test"); //passenger.getPassengerLastName()
                String passengerDob = inDob.format(passenger.getDob());
                insured.put("txtFhNacimiento", passengerDob);    
                insured.put("TXTEDADSV", String.valueOf(passenger.getAge())); 
                insured.put("txtIdFiscal", passenger.getPassportNo());  
                insured.put("txtMovil", fullMobileNumber); 
                insuredList.add(insured);
            }
            payload.put("insuredData", insuredList);

            // coberData
            Map<String, Object> coberData = new HashMap<>();
            coberData.put("chk_B0002", "0");
            coberData.put("chk_D0001", "1");
            coberData.put("chk_AC3_21_40", "1");
            coberData.put("chk_B0000", "1");
            coberData.put("chk_F0000", "1");
            coberData.put("chk_D0000", "1");
            coberData.put("chk_C0000", "1");
            coberData.put("chk_G0000", "1");
            
            Map<String, Object> coberturaLimites = new HashMap<>();
            coberturaLimites.put("chk_D0001_56", "");
            coberturaLimites.put("chk_D0001_31", "");
            coberturaLimites.put("chk_AC3_21_40_973", "");
            coberturaLimites.put("chk_B0000_737", "");
            coberturaLimites.put("chk_B0000_733", "");
            coberturaLimites.put("chk_B0000_87", "");
            coberturaLimites.put("chk_B0000_60", "");
            coberturaLimites.put("chk_B0000_59", "");
            coberturaLimites.put("chk_B0000_22", "");
            coberturaLimites.put("chk_B0000_16", "");
            coberturaLimites.put("chk_B0000_15", "");
            coberturaLimites.put("chk_B0000_11", "");
            coberturaLimites.put("chk_B0000_10", "");
            coberturaLimites.put("chk_B0000_6", "");
            coberturaLimites.put("chk_B0000_4", "");
            coberturaLimites.put("chk_B0000_2", "");
            coberturaLimites.put("chk_F0000_35", "");
            coberturaLimites.put("chk_F0000_32", "");
            coberturaLimites.put("chk_D0000_38", "");
            coberturaLimites.put("chk_D0000_37", "");
            coberturaLimites.put("chk_C0000_30", "");
            coberturaLimites.put("chk_C0000_28", "");
            coberturaLimites.put("chk_C0000_27", "");
            coberturaLimites.put("chk_C0000_26", "");
            coberturaLimites.put("chk_G0000_36", "");
            coberturaLimites.put("chk_G0000_21", "");
            coberturaLimites.put("chk_G0000_18", "");
            coberturaLimites.put("chk_D0001_29", "");
            coberData.put("CoberturaLimites", coberturaLimites);
            payload.put("coberData", coberData);
            
            
            SimpleDateFormat tom = new SimpleDateFormat("yyyy-MM-dd");
            
            String passengerDob = tom.format(customerdetail.getDobOrRegDate());
            
            // tomadorData
            Map<String, Object> tomadorData = new HashMap<>();
            tomadorData.put("txtNmAsegurado_policyHolder", "Test"); //customerdetail.getFirstName()
            tomadorData.put("txtApeAsegurado_policyHolder", "Test2"); //customerdetail.getLastName()
            tomadorData.put("txtFhNacimiento_policyHolder",passengerDob);
            tomadorData.put("TXTEDADSV_policyHolder",String.valueOf(customerdetail.getAge()));
            tomadorData.put("txtIdFiscal_policyHolder", passportNo);
            tomadorData.put("txtEmail_policyHolder",customerdetail.getEmail1() );
            tomadorData.put("txtMovil_policyHolder", fullMobileNumber);
            tomadorData.put("txtTelefono_policyHolder", fullMobileNumber);
            tomadorData.put("txtUF_policyHolder", customerdetail.getKraPin());
            tomadorData.put("txtDirAsegurado_policyHolder", customerdetail.getAddress1());
            payload.put("tomadorData", tomadorData);

            // parameters
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("action", "A");
            parameters.put("origenRecepcion", "2");
            parameters.put("posicionTomador", "1");
            payload.put("parameters", parameters);
            

            RestTemplate restTemplate = new RestTemplate();
//            String url = "http://localhost:8080/mapfre/issuing";

            HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.setAccept(List.of(MediaType.ALL));

            HttpEntity<String> requestEntity = new HttpEntity<>( new Gson().toJson(payload).toString(), headers);
	        
            LocalDateTime requestTime = LocalDateTime.now();

            
            LocalDateTime responseTime;
            String status = "PENDING";
            
            System.out.println("Payload: " + new ObjectMapper().writeValueAsString(payload));

            try {
            	System.out.println("Calling travel integration");
            	response = restTemplate.exchange(
            			mpfreUrl,
            		    HttpMethod.POST,
            		    requestEntity,
            		    new ParameterizedTypeReference<Map<String, Object>>() {}
            		);

    			System.out.println("Response Status Code: " + response.getStatusCode());
    			System.out.println("Response Body: " + response.getBody());
    			
                responseTime = LocalDateTime.now();
                if (response.getBody() != null && response.getBody().get("numContrato") != null) {
                    status = "SUCCESS";
                    
                }
            } catch (Exception e) {
            	System.out.println("Error calling travel integration");
                responseTime = LocalDateTime.now();
                status = "FAILED";
                e.printStackTrace(); // Log exception if needed
            }

            String policyNo = null;
            String pdfLink = null;
            if (response != null && response.getBody() != null && status.equals("SUCCESS")) {
                Map<String, Object> body = response.getBody();
                policyNo = (String) body.get("numContrato");
                pdfLink = (String) body.get("file");
            }
            
            if (response != null && response.getBody() != null && "SUCCESS".equals(status)) {
                Map<String, Object> body = response.getBody();
                policyNo = (String) body.get("numContrato");
                pdfLink = (String) body.get("file");
            }

            String pdfFileName = quoteNo + ".pdf";
            String folderPath ="";
            if (pdfLink != null && !pdfLink.isEmpty()) {
                HttpClient client = HttpClient.newHttpClient();
                
                HttpRequest request = HttpRequest.newBuilder()
                		.uri(URI.create(pdfLink))
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
            // Save to DB
            MapfreIntegrationLog log = MapfreIntegrationLog.builder()
                    .quoteNo(quoteNo)
                    .requestRefNo(requestReferenceNo)
                    .mapfreRequest(new ObjectMapper().writeValueAsString(payload))
                    .mapfreResponse(response != null ? new ObjectMapper().writeValueAsString(response.getBody()) : null)
                    .requestTime(requestTime)
                    .responseTime(responseTime)
                    .status(MapfreIntegrationLog.ResponseStatus.valueOf(status))
                    .policyNo(policyNo)
                    .pdfLink(folderPath + pdfFileName)
                    .build();

            mapfreRepo.save(log);
            
            resMap.put("Body",response.getBody());
            resMap.put("PdfLink", folderPath+pdfFileName);
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.singletonMap("error", e.getMessage());
        }
        return resMap;
    }
}
