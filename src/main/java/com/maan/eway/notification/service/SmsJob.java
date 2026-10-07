package com.maan.eway.notification.service;

import java.util.Arrays;
import java.util.function.Consumer;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.maan.eway.notification.req.Sms;

//@Service
public class SmsJob implements Consumer<Sms> {

	/*@Autowired
	private SmsDataDetailsRepository smsRepo;*/
	/*private String type="0";	
	private String dlr="1";
	String statuscode="";
	Integer statusvalue =0;*/
	private String kafkaLink; /*
	public void pushSms(Sms m) {

		String statusResponse = null;
		try {
				
			String mobileCode="";
			RestTemplate restTemplate = new RestTemplate();
			String fooResourceUrl = m.getCredential().getHost();
			if(StringUtils.isNotBlank( m.getSmsToCode())) {
			mobileCode = m.getSmsToCode().replace("+", "");
			}
			String content="username="
					+ URLEncoder.encode(m.getCredential().getUsername(), "UTF-8") + "&password="
					+ m.getCredential().getPassword() + "&type="
					+ URLEncoder.encode(this.type, "UTF-8") + "&dlr="
					+ URLEncoder.encode(this.dlr, "UTF-8") + "&destination="
					 + URLEncoder.encode(m.getSmsBody(), "UTF-8") + "&source="
							+ URLEncoder.encode(mobileCode+m.getSmsFrom(), "UTF-8") + "&message="
							+m.getSmsBody()+m.getSmsRegards()==null?"":m.getSmsRegards();
			System.out.println("SMS request  ---> "+fooResourceUrl + "?"+content);
			
			ResponseEntity<String> response	  = restTemplate.getForEntity(fooResourceUrl + "?"+content, String.class);
			
			System.out.println("SMS Response"+response.getBody());
			statuscode = response.getStatusCode().toString();
			statusvalue = response.getStatusCodeValue();		
		} catch (Exception e) {
			e.printStackTrace();
			statusResponse = e.getLocalizedMessage();
		}

		SmsDataDetails savedata = new SmsDataDetails();

		Long sno = smsRepo.count();
		sno=sno+1;
		savedata.setMobileNo(m.getSmsTo());
		savedata.setSmsFrom(m.getSmsFrom());		
		savedata.setSmsType(m.getSmsSubject());
		savedata.setSmsContent(m.getSmsBody());
		savedata.setEntryDate(new Date());
		savedata.setSNo(sno.toString());
		if(statuscode.equalsIgnoreCase("200OK")) {
		savedata.setResStatus("OK");
		savedata.setResMessage("SMS Sent Successful");		
		}
		else {
			savedata.setResStatus("Not OK");
			savedata.setResMessage("SMS Sent Failed");					
		}
		savedata.setReqTime(new Date());
		savedata.setResTime(new Date());
		savedata.setNotifNo(m.getNotifNo());
		smsRepo.save(savedata);

	}
*/
	public SmsJob(String kafkaLinksms) {
		// TODO Auto-generated constructor stub
		kafkaLink=kafkaLinksms;
	}
	@Override
	public void accept(Sms t) {
		try {

			RestTemplate restTemplate = new RestTemplate();
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Arrays.asList(new MediaType[] { MediaType.APPLICATION_JSON }));
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.set("Authorization", "Basic dmlzaW9uOnZpc2lvbkAxMjMj");
			HttpEntity<Object> entityReq = new HttpEntity<>(t, headers);
			System.out.println(entityReq.getBody());
			ResponseEntity<Object> response = restTemplate.postForEntity(kafkaLink, entityReq, Object.class);
			System.out.println(response.getBody());
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

}
