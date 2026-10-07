package com.maan.eway.Rtsa;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.maan.eway.Rtsa.Req.GetRegDetailsReq;
import com.maan.eway.Rtsa.res.GetRtsaListRes;
import com.maan.eway.bean.ListItemValue;
import com.maan.eway.bean.RtsaVehicleDetail;
import com.maan.eway.common.res.CommonRes;
import com.maan.eway.repository.ListItemValueRepository;
import com.maan.eway.repository.RtsaVehicleDetailRepository;

@Service
public class RSTAServiceImple implements RSTAService {
	
	@Autowired
	private ListItemValueRepository itemValueRepository;
	
	@Autowired
	private RtsaVehicleDetailRepository detailRepository;

	private static final Logger logger = LogManager.getLogger(RSTAServiceImple.class);
	
	@Override
	public CommonRes getToken() {
		logger.info("Enter in Token");
		CommonRes res = new CommonRes();
		List<Map<String,Object>> finalResList = new ArrayList<Map<String,Object>>();
		try {
			List<ListItemValue> itemValues = itemValueRepository.findByItemTypeAndStatusAndCompanyIdOrderByItemCodeDesc("RTSA_TOKEN", "Y", "100004");
			if(!itemValues.isEmpty()) {
				ListItemValue m = itemValues.get(0);
				HttpHeaders headers = new HttpHeaders();
				headers.set("Authorization", "Basic "+m.getItemValue());
				headers.setContentType(MediaType.APPLICATION_JSON);
				Map<String,Object> map = new HashMap<String,Object>();
				map.put("Username", m.getParam1());
				map.put("Password", m.getParam2());
				RestTemplate restTemplate = new RestTemplate();
				HttpEntity<Object> headerReq = new HttpEntity<>(map,headers);
				ResponseEntity<String> result = restTemplate.postForEntity(m.getRemarks(), headerReq, String.class);
				 String payRes = (String)result.getBody();
				 logger.info("TokenResponse || " + payRes);
				 Map<String,Object> resMap = new ObjectMapper().readValue(payRes, new TypeReference<Map<String, Object>>() {});
				 Map<String,Object> finalRes = new HashMap<String,Object>();
				 for (Map.Entry<String, Object> entry : resMap.entrySet()) {
					 finalRes.put(entry.getKey() , entry.getValue());
		            }
				 finalResList.add(finalRes);
				 res.setCommonResponse(finalResList);
				 res.setMessage("SUCCESS");
				 res.setIsError(false);
				 return res;
			}
			
		logger.info("Exit into Token");	
		}catch (HttpClientErrorException ex) {
		    logger.error("HTTP error occurred: " + ex.getStatusCode() + " - " + ex.getStatusText());
		    logger.error("Response body: " + ex.getResponseBodyAsString());
		} catch (Exception ex) {
		    logger.error("Error occurred: " + ex.getMessage(), ex);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public CommonRes GetRegDetails(GetRegDetailsReq req) {
		logger.info("Enter in GetRegDetails");
		CommonRes res = new CommonRes();
		List<Map<String,Object>> finalResList = new ArrayList<Map<String,Object>>();
		List<RtsaVehicleDetail> rtsaVehicleDetails = new ArrayList<>();
		try {
			List<ListItemValue> itemValues = itemValueRepository.findByItemTypeAndStatusAndCompanyIdOrderByItemCodeDesc("RTSA_REG", "Y", "100004");
			SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
			if(!itemValues.isEmpty()) {
				ListItemValue k = itemValues.get(0);
				HttpHeaders headers = new HttpHeaders();
				headers.set("Authorization", "Bearer "+req.getRequestToken());
				headers.setContentType(MediaType.APPLICATION_JSON);
				RestTemplate restTemplate = new RestTemplate();
				HttpEntity<Object> headerReq = new HttpEntity<>(headers);
				ResponseEntity<String> result = restTemplate.exchange(k.getRemarks().replaceAll("<RegNo>", req.getRegNo()), HttpMethod.GET, headerReq, String.class);
				int code = result.getStatusCodeValue();
				String payRes = (String)result.getBody();
				Map<String,Object> resMap = new ObjectMapper().readValue(payRes, new TypeReference<Map<String, Object>>() {});
				 Map<String,Object> bodyMap = (Map<String,Object>) resMap.get("body");
				if(code == 200 && bodyMap.get("errorMessage") ==null && resMap.get("code").equals(200)) {
					 Map<String,Object> finalRes = new HashMap<String,Object>();
					 for (Map.Entry<String, Object> entry : bodyMap.entrySet()) {
						 finalRes.put(entry.getKey() , entry.getValue());
			            }
					 finalResList.add(finalRes);
					 if(!finalResList.isEmpty()) {
						 finalResList.forEach(o -> {
							 RtsaVehicleDetail v = RtsaVehicleDetail.builder()
									 .registrationNo(req.getRegNo())
									 .code(String.valueOf(code))
									 .message(o.get("message")==null?null:o.get("message").toString())
									 .response(payRes)
									 .responseTime(sdfDate.format(new Date()))
									 .makeName(o.get("make")==null?"":o.get("make").toString())
									 .modelName(o.get("model")==null?"":o.get("model").toString())
									 .engineNo(o.get("engineNo")==null?"":o.get("engineNo").toString())
									 .chassisNo(o.get("chassisNo")==null?"":o.get("chassisNo").toString())
									 .yearMake(o.get("yearMake")==null?"":o.get("yearMake").toString())
									 .gvm(o.get("gvm")==null?"":o.get("gvm").toString())
									 .bodyType(o.get("bodyType")==null?"":o.get("bodyType").toString())
									 .color(o.get("mainColor")==null?"":o.get("mainColor").toString())
									 .numberOfSeats(o.get("numberOfSeats")==null?"":o.get("numberOfSeats").toString())
									 .firstRegDate(o.get("firstRegDate")==null?null:o.get("firstRegDate").toString())
									 .currentLinenseExpDate(o.get("currentLicenseExpiryDate")==null?null:o.get("currentLicenseExpiryDate").toString())
									 .roadWortExpDate(o.get("roadWorthinessExpiryDate")==null?null:o.get("roadWorthinessExpiryDate").toString())
									 .regStatus(o.get("registrationStatus")==null?"":o.get("registrationStatus").toString())
									 .build();
							 rtsaVehicleDetails.add(v);
						 });
						 detailRepository.saveAll(rtsaVehicleDetails);
						 res.setCommonResponse("Inserted Successfully......");
						 res.setMessage("SUCCESS");
						 res.setIsError(false);
						 return res;
					 }
				}else if(resMap.get("code").equals(400)) {
					RtsaVehicleDetail i = RtsaVehicleDetail.builder()
							.registrationNo(req.getRegNo())
							.code(resMap.get("code")==null?"":resMap.get("code").toString())
							.message(bodyMap.get("errorMessage")==null?"N/A":bodyMap.get("errorMessage").toString())
							.responseTime(sdfDate.format(new Date()))
							.response(payRes)
							.build();
					detailRepository.save(i);
					 res.setCommonResponse(i.getMessage());
					 res.setMessage("SUCCESS");
					 res.setIsError(false);
					 return res;
				}else {
					RtsaVehicleDetail i = RtsaVehicleDetail.builder()
							.registrationNo(req.getRegNo())
							.code(resMap.get("StatusCode")==null?"":resMap.get("StatusCode").toString())
							.message(bodyMap.get("Message")==null?"N/A":bodyMap.get("Message").toString())
							.responseTime(sdfDate.format(new Date()))
							.response(payRes)
							.build();
					detailRepository.save(i);
					res.setCommonResponse(i.getMessage());
					 res.setMessage("SUCCESS");
					 res.setIsError(false);
					 return res;
				}
			}
			logger.info("Exit into GetRegDetails");	
		}catch (HttpClientErrorException ex) {
		    logger.error("HTTP error occurred: " + ex.getStatusCode() + " - " + ex.getStatusText());
		    logger.error("Response body: " + ex.getResponseBodyAsString());
		} catch (Exception ex) {
		    logger.error("Error occurred: " + ex.getMessage(), ex);
		}
		return null;
	}

	@Override
	public CommonRes getRtsaList(String RegNo) {
		logger.info("Enter in getRtsaList");
		CommonRes res = new CommonRes();
		List<GetRtsaListRes> result = new ArrayList<>();
		SimpleDateFormat sdfDate = new SimpleDateFormat("dd/MM/yyyy");
		try {
			List<RtsaVehicleDetail> vehicleDetails = new ArrayList<RtsaVehicleDetail>();
			if(StringUtils.isNotBlank(RegNo)) {
				vehicleDetails = detailRepository.findByRegistrationNoAndCodeAndMessageIsNull(RegNo,"200");
			}else {
				vehicleDetails = detailRepository.findByCodeAndMessageIsNull("200");
			}
			if(!vehicleDetails.isEmpty()) {
				vehicleDetails.forEach(k -> {
					GetRtsaListRes h;
					try {
						h = GetRtsaListRes.builder()
								.registrationNo(k.getRegistrationNo())
								.makeName(k.getMakeName())
								.modelName(k.getModelName())
								.chassisNo(k.getChassisNo())
								.engineNo(k.getEngineNo())
								.yearMake(k.getYearMake())
								.gvm(k.getGvm())
								.bodyType(k.getBodyType())
								.color(k.getColor())
								.numberOfSeats(k.getNumberOfSeats())
								.regStatus(k.getRegStatus())
								.firstRegDate(sdfDate.format(new SimpleDateFormat("yyyy-MM-dd").parse(k.getFirstRegDate())))
								.currentLinenseExpDate(sdfDate.format(new SimpleDateFormat("yyyy-MM-dd").parse(k.getCurrentLinenseExpDate())))
								.roadWortExpDate(sdfDate.format(new SimpleDateFormat("yyyy-MM-dd").parse(k.getRoadWortExpDate())))
								.build();
						result.add(h);
					} catch (ParseException e) {
						e.printStackTrace();
					}
				});
				res.setCommonResponse(result);
				res.setMessage("SUCCESS");
				res.setIsError(false);
				return res;
			}
			logger.info("Enter in getRtsaList");
		}catch(Exception e) {
			logger.info("Error in getRtsaList  :: "+e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

}
