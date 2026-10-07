package com.maan.eway.excelupload.service;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.dhatim.fastexcel.reader.ReadableWorkbook;
import org.dhatim.fastexcel.reader.Row;
import org.dhatim.fastexcel.reader.Sheet;
import org.json.JSONObject;
import org.json.XML;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.maan.eway.common.res.CommonRes;
import com.maan.eway.excelupload.dto.PassengerDto;
import com.maan.eway.excelupload.dto.SavePassengersRequest;
import com.maan.eway.excelupload.dto.SavePassengersResponse;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TravelPassengerUploadService {
	
	@Value("${save.passenger}")
	private String savePassenger;

    @Autowired
    private RestTemplate restTemplate;

    // Point this at wherever /savepassengers actually lives for your env
    private static final String SAVE_PASSENGERS_URL = "http://localhost:8085/api/savepassengers";

    private static final String H_FIRST = "First name";
    private static final String H_LAST = "Last name";
    private static final String H_GENDER = "Gender";
    private static final String H_RELATION = "Relationship";
    private static final String H_DOB = "DOB";
    private static final String H_NATIONALITY = "Nationality";
    private static final String H_PASSPORT = "Passport no";

    /**
     * Full flow: parse the uploaded excel, build the savepassengers payload, call it,
     * and return its response. This is the only method the controller should call.
     */
    public ResponseEntity<Map<String, Object>> processUpload(
            MultipartFile file,
            String quoteNo,
            String requestReferenceNo,
            String token) {

        try {

            List<PassengerDto> passengers = parsePassengers(file);

            HttpHeaders headers = buildHeaders(token);

            if (passengers.isEmpty()) {
                Map<String, Object> error = new HashMap<>();
                error.put("isError", true);
                error.put("message", "No passenger rows found in the sheet");
                return ResponseEntity.ok().body(error);
            }

            SavePassengersRequest req = new SavePassengersRequest();
            req.setQuoteNo(quoteNo);
            req.setCreatedBy(currentUsername());
            req.setLocationId(resolveLocationId(quoteNo, requestReferenceNo));
            req.setPassengerList(passengers);
            
            log.info("Save Passenger Request: {}", 
                    new ObjectMapper().writeValueAsString(req));

            return callSavePassengers(req, headers);

        } catch (Exception e) {

            Map<String, Object> error = new HashMap<>();
            error.put("isError", true);
            error.put("message", e.getMessage());

            return ResponseEntity.badRequest().body(error);
        }
    }


    private ResponseEntity<Map<String, Object>> callSavePassengers(
            SavePassengersRequest req,
            HttpHeaders headers) {

        HttpEntity<SavePassengersRequest> entity =
                new HttpEntity<>(req, headers);

        ResponseEntity<String> saveRes = restTemplate.exchange(
        		savePassenger,
                HttpMethod.POST,
                entity,
                String.class);

        JSONObject json = XML.toJSONObject(saveRes.getBody());

        @SuppressWarnings("unchecked")
        Map<String, Object> response =
                (Map<String, Object>) json.toMap();

        return ResponseEntity
                .status(saveRes.getStatusCode())
                .body(response);
    }

    private List<PassengerDto> parsePassengers(MultipartFile file) throws Exception {
        List<PassengerDto> result = new ArrayList<>();

        try (InputStream is = file.getInputStream();
             ReadableWorkbook wb = new ReadableWorkbook(is)) {

            Sheet sheet = wb.getFirstSheet();

            try (Stream<Row> rowStream = sheet.openStream()) {
                Iterator<Row> it = rowStream.iterator();
                if (!it.hasNext()) return result;

                Row header = it.next();
                Map<String, Integer> colIndex = new HashMap<>();
                for (int i = 0; i < header.getCellCount(); i++) {
                    final int idx = i;
                    header.getCellAsString(i).ifPresent(h -> colIndex.put(h.trim(), idx));
                }

                boolean firstRow = true;

                while (it.hasNext()) {
                    Row r = it.next();

                    String first = getStr(r, colIndex, H_FIRST);
                    String last = getStr(r, colIndex, H_LAST);
                    String gender = getStr(r, colIndex, H_GENDER);
                    String relation = getStr(r, colIndex, H_RELATION);
                    String dob = getDate(r, colIndex, H_DOB);
                    String nationality = getStr(r, colIndex, H_NATIONALITY);
                    String passport = getStr(r, colIndex, H_PASSPORT);

                    boolean allEmpty = isBlank(first) && isBlank(last) && isBlank(gender)
                            && isBlank(dob) && isBlank(passport);
                    if (allEmpty) continue;

                    if (isBlank(gender)) {
                        throw new RuntimeException("Gender is required for passenger: " + first + " " + last);
                    }
                    gender = gender.trim().toUpperCase();
                    if (!gender.equals("M") && !gender.equals("F")) {
                        throw new RuntimeException("Gender must be M or F, got '" + gender + "'");
                    }

                    String relationId;
                    if (firstRow) {
                        // First passenger row is always Self, regardless of what's in the cell
                        relationId = RelationshipMaster.selfCodeFor(gender);
                    } else {
                        relationId = RelationshipMaster.codeFor(gender, relation);
                        if (relationId == null) {
                            throw new RuntimeException("Invalid relationship '" + relation
                                    + "' for gender '" + gender + "' (row: " + first + " " + last + ")");
                        }
                    }
                    

                    PassengerDto p = new PassengerDto();
                    p.setPassengerFirstName(first);
                    p.setPassengerLastName(last);
                    p.setGenderId(gender);
                    p.setRelationId(relationId);
                    p.setDob(dob);
                    p.setNationality(nationality);
                    p.setPassportNo(passport);
                    result.add(p);

                    firstRow = false;
                }
            }
        }
        return result;
    }

    private String getStr(Row r, Map<String, Integer> colIndex, String header) {

        Integer idx = colIndex.get(header);
        if (idx == null || idx >= r.getCellCount()) {
            return null;
        }

        try {
            return r.getCellAsString(idx).map(String::trim).orElse(null);
        } catch (Exception e) {
            System.out.println("Header : " + header);
            System.out.println("Column : " + idx);
            throw e;
        }
    }

    private boolean isBlank(String s) {
        return s == null || s.isEmpty();
    }

    private String currentUsername() {
        try {
            return SecurityContextHolder.getContext().getAuthentication().getName();
        } catch (Exception e) {
            return "system";
        }
    }

    /**
     * LocationId isn't in the upload request per your spec — resolve it internally.
     * Wire this to whatever actually maps QuoteNo/RequestReferenceNo to LocationId in your
     * schema (e.g. the travel master / branch lookup you use elsewhere) — placeholder below.
     */
    private String resolveLocationId(String quoteNo, String requestReferenceNo) {
        // TODO: replace with your real lookup, e.g.:
        // return travelMasterRepo.findByQuoteNo(quoteNo).getLocationId();
        return "1";
    }

    private SavePassengersResponse errorResponse(String message) {
        SavePassengersResponse res = new SavePassengersResponse();
        res.setIsError(true);
        res.setMessage(message);
        return res;
    }
    
    
    private HttpHeaders buildHeaders(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);
        return headers;
    }
    
    private String getDate(Row r, Map<String, Integer> colIndex, String header) {

        Integer idx = colIndex.get(header);
        if (idx == null || idx >= r.getCellCount()) {
            return null;
        }

        try {
            return r.getCellAsString(idx)
                    .map(String::trim)
                    .orElse(null);

        } catch (Exception ex) {

            try {
                LocalDateTime date = r.getCellAsDate(idx).orElse(null);

                if (date != null) {
                    return date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                }

            } catch (Exception ignore) {
            }

            return null;
        }
    }
    
}
