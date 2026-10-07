package com.maan.eway.report;

import java.net.ConnectException;
import java.time.Duration;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.maan.eway.report.data.GenerateAuthToken;
import com.maan.eway.report.data.GenerateReportRequest;
import com.maan.eway.report.data.GenerateReportResponse;
import com.maan.eway.report.data.ReportResponse;

@RestController
@RequestMapping("/report")
public class ReportController {

	@Value(value = "${generateToken}")
	private String generateToken;

	@Value(value = "${generateReport}")
	private String generateReport;

	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER')")
	@PostMapping("/generateReport")
	public Object generateReport(@RequestBody GenerateReportRequest request) {
		ReportResponse req = new ReportResponse();
		try {
			ResponseEntity<String> response = null;
			ResponseEntity<GenerateReportResponse> response1 = null;
			RestTemplate temp = new RestTemplateBuilder().setConnectTimeout(Duration.ofSeconds(5))
					.setReadTimeout(Duration.ofMinutes(2)).build();

			HttpHeaders header = new HttpHeaders();
			header.setContentType(MediaType.APPLICATION_JSON);
			GenerateAuthToken generateAuthToken = new GenerateAuthToken();
			generateAuthToken.setUsername("admin");
			generateAuthToken.setPassword("admin");
			HttpEntity<?> requestent = new HttpEntity<>(generateAuthToken, header);
			System.out.println("Calling External Api:  " + requestent);
			try {
				response = temp.exchange(generateToken, HttpMethod.POST, requestent, String.class);

				System.out.println("response Api:  " + response.getBody());
			} catch (RestClientException e) {
				if (e.getCause() instanceof ConnectException) {
					System.out.println("Connection refused: Unable to connect to the server at " + response);
					req.setErrorMessage("Connection refused: Unable to connect to the server");
				} else {
					System.out.println("An error occurred while making the REST call: " + e.getMessage());
					req.setErrorMessage("An error occurred while making the REST call");
				}
			}
			HttpHeaders header1 = new HttpHeaders();
			header1.setContentType(MediaType.APPLICATION_JSON);
			header1.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			header1.setBearerAuth(response.getBody());
			HttpEntity<?> requestent1 = new HttpEntity<>(request, header1);
			System.out.println("calling second Api:  " + requestent1);
			try {
				response1 = temp.exchange(generateReport, HttpMethod.POST, requestent1, GenerateReportResponse.class);
				System.out.println("report response Api:  " + response1.getBody().getBase64Files());

			} catch (RestClientException e) {
				if (e.getCause() instanceof ConnectException) {
					System.out.println("Connection refused: Unable to connect to the server at " + response1);
					req.setErrorMessage("Connection refused: Unable to connect to the server");
				} else {
					System.out.println("An error occurred while making the REST call: " + e.getMessage());
					req.setErrorMessage("An error occurred while making the REST call");
				}
			}
			if (response1 != null && response1.getBody().getBase64Files() != null
					&& !response1.getBody().getBase64Files().isEmpty()) {
				req.setResponse(response1.getBody().getBase64Files().get(0));
				return req;
			} else {
				req.setErrorMessage("Invalid policyNo");
			}
			return req;
		} catch (Exception e) {
			e.printStackTrace();
			req.setErrorMessage("Connection refused: Unable to connect to the server");
			return req;
		}
	}

}
