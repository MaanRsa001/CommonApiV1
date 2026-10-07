package com.maan.eway.admin.service.impl;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.maan.eway.admin.req.CommonLoginCreationReq;
import com.maan.eway.admin.req.CommonLoginInformationReq;
import com.maan.eway.admin.req.CommonPersonalInforReq;
import com.maan.eway.bean.LoginBranchMaster;
import com.maan.eway.bean.LoginMaster;
import com.maan.eway.bean.LoginUserInfo;
import com.maan.eway.repository.BranchMasterRepository;
import com.maan.eway.repository.LoginBranchMasterRepository;

@Service
public class MarineLoginApi {

	@Value(value="${marine.auth.login}")
	private String authLoginLink;
	@Value(value="${marine.auth.createbroker}")
	private String createbrokerlink;
	@Value(value="${marine.auth.createproduct}")
	private String createProductLink;
	
	@Value(value="${marine.auth.createbranch}")
	private String createBranchLink;
	
	@Value(value="${marine.auth.createadmin}")
	private String createAdminLink;
	
	@Value(value="${marine.auth.createissuer}")
	private String createIssuerLink;
	
	@Value(value="${marine.auth.createuser}")
	private String createUserLink;
	
	@Value(value="${marine.auth.userproduct}")
	private String createUserProductLink;
	
	
	
	@Autowired
	private LoginBranchMasterRepository lbmRepo; 
	@Autowired
	private BranchMasterRepository bmRepo; 
	private String createLogin() {
		try {
			Map<String,Object > mainRequest=new HashMap<String, Object>();
			mainRequest.put("UserId", "guest");
			mainRequest.put("Password","Admin@01");
			mainRequest.put("LoginType","Admin");
			mainRequest.put("RegionCode","100020");
			mainRequest.put("BranchCode","46");
			
			RestTemplate   temp=new RestTemplateBuilder().setConnectTimeout(Duration.ofSeconds(5)).setReadTimeout(Duration.ofSeconds(5)).build();
			
			
			HttpHeaders header=new HttpHeaders();
			header.setContentType(MediaType.APPLICATION_JSON);
			//header.setCharset("UTF-8");
			//header.setBearerAuth(request.getTokenl());
			 


			HttpEntity<?> requestent = 
					new HttpEntity<>(mainRequest, header);

			System.out.println( new Date()+" Start "+ authLoginLink);
			ResponseEntity<Map<String, Object>> postForEntity = temp.exchange(authLoginLink,HttpMethod.POST, requestent,new ParameterizedTypeReference<Map<String, Object>>(){} );
			System.out.println( new Date()+" End "+ authLoginLink);
			Map<String,String> object =(Map<String,String>) postForEntity.getBody().get("LoginResponse");
			return object.get("Token");
		}catch (Exception e) {
			e.printStackTrace();
		}
		return "";
		
	}
	
	public void createMarineBroker(LoginUserInfo userInfo,String mode, CommonLoginCreationReq req,LoginMaster saveLogin) {
		try {
		/*	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy"); 
			List<BranchMaster> branchlist=bmRepo.findByCompanyId(req.getLoginInformation().getCompanyId());
			String branchCode=branchlist.get(0).getBranchCode();
			List<String> attachBranch = new ArrayList<String>();
			if (branchlist != null) {
				for (int i = 1; i <= branchlist.size(); i++) {
					attachBranch.add(branchlist.get(i).toString());
				}
			}
			
//			List<String> companyId = new ArrayList<String>();
//			String region=companyId.get(0);
			List<String> attachRegion = new ArrayList<String>();
//			if (companyId != null) {
//				for (int i = 1; i <= companyId.size(); i++) {
					attachRegion.add(req.getLoginInformation().getCompanyId());
//				}
//			}
			Map<String,Object > mainRequest=new HashMap<String, Object>();
			mainRequest.put("Address1",userInfo.getAddress1());
			mainRequest.put("Address2",userInfo.getAddress2());
			mainRequest.put("AgencyCode",userInfo.getAgencyCode());
			mainRequest.put("Approvedby",userInfo.getUpdatedBy());
			mainRequest.put("AttachedBranchInfo",attachBranch);
			mainRequest.put("AttachedRegionInfo",attachRegion);
			mainRequest.put("BorkerOrganization",userInfo.getCustomerName());
			mainRequest.put("BranchCode",userInfo.getBranchCode());
			mainRequest.put("BrokerCode",userInfo.getCustomerCode());
			mainRequest.put("City","0");
			mainRequest.put("Country","1");
			mainRequest.put("CustFirstName",userInfo.getCustomerName());
			mainRequest.put("CustLastName","");
			mainRequest.put("CustomerId","");
			mainRequest.put("DateOfBirth","");
			mainRequest.put("EffectiveDate",sdf.format(userInfo.getEntryDate()));
			mainRequest.put("Email",userInfo.getUserMail());
			mainRequest.put("EmergencyFee","");
			mainRequest.put("EmergencyFund","");
			mainRequest.put("Executive","5");
			mainRequest.put("Fax","0");
			mainRequest.put("Gender","");
			mainRequest.put("GovetFee","");
			mainRequest.put("GovtFeeStatus","");
			mainRequest.put("LoginId",userInfo.getLoginId());
			mainRequest.put("MissippiId","");
			mainRequest.put("MobileNo",userInfo.getUserMobile());
			mainRequest.put("Mode",mode);
			mainRequest.put("Nationality","1");
			mainRequest.put("Occupation",userInfo.getDesignation());
			mainRequest.put("OneOffCommission","");
			mainRequest.put("OpenCoverCommission","");
			mainRequest.put("Password","Admin@01");
			mainRequest.put("PoBox",userInfo.getPobox());
			mainRequest.put("PolicyFee","");
			mainRequest.put("PolicyFeeStatus","");
			mainRequest.put("RePassword","Admin@01");
			mainRequest.put("RegionCode",saveLogin.getCompanyId());
			mainRequest.put("Status",userInfo.getStatus());
			mainRequest.put("SubBranchCode","");
			mainRequest.put("TaxApplicable","");
			mainRequest.put("TelephoneNo","");
			mainRequest.put("Title","");
			mainRequest.put("ValidNcheck",""); 
 			String token = createLogin();
			RestTemplate   temp=new RestTemplateBuilder().setConnectTimeout(Duration.ofSeconds(5)).setReadTimeout(Duration.ofSeconds(5)).build();
			
			
			HttpHeaders header=new HttpHeaders();
			header.setContentType(MediaType.APPLICATION_JSON);
			//header.setCharset("UTF-8");
			header.setBearerAuth(token);
			 


			HttpEntity<?> requestent = 
					new HttpEntity<>(mainRequest, header);

			System.out.println( new Date()+" Start "+ createbrokerlink);
			System.out.println("request"+requestent);
			ResponseEntity<Map<String, Object>> postForEntity = temp.exchange(createbrokerlink,HttpMethod.POST, requestent,new ParameterizedTypeReference<Map<String, Object>>(){} );
			System.out.println( new Date()+" End "+ createbrokerlink);
			System.out.println("response"+postForEntity.getBody());*/
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public void createProductLink() {
		try {
			Map<String,Object > mainRequest=new HashMap<String, Object>();
			mainRequest.put("AgencyCode","");
			mainRequest.put("BackDateAllowed","");
			mainRequest.put("Commission","");
			mainRequest.put("CustomerId","");
			mainRequest.put("CustomerName","");
			mainRequest.put("DiscountPremium","");
			mainRequest.put("Freight","N");
			mainRequest.put("InsuranceEndLimit","10000000000000");
			mainRequest.put("LoadingPremium",null);
			mainRequest.put("MinPremiumAmount","1000");
			mainRequest.put("PayReceip","N");
			mainRequest.put("ProductId","");
			mainRequest.put("Provision","N");
			mainRequest.put("Remarks","N");
			mainRequest.put("UserId","");			
			
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public void createBranch(LoginBranchMaster save) {
		try {
			Map<String,Object > mainRequest=new HashMap<String, Object>();
			String mainBranch="";
					
			List<Map<String,Object>> AttachedBranchInfo=new ArrayList<Map<String,Object>>();
			
			
			Map<String,Object> AttachedBranchId=new HashMap<String, Object>();
			AttachedBranchId.put("AttachedBranchId", save.getBranchCode());
			AttachedBranchInfo.add(AttachedBranchId);
			
			List<LoginBranchMaster> branches = lbmRepo.findByLoginId(save.getLoginId());
			for(LoginBranchMaster branch:branches) {
				Map<String,Object> AttachedBranch=new HashMap<String, Object>();
				AttachedBranch.put("AttachedBranchId", branch.getBranchCode());
				AttachedBranchInfo.add(AttachedBranch);
				mainBranch= "Main".equalsIgnoreCase(branch.getBranchType())?branch.getBranchCode():"";
			}
			mainBranch=StringUtils.isBlank(mainBranch)?save.getBranchCode():mainBranch;
			
			mainRequest.put("AttachedBranchInfo", AttachedBranchInfo);
			
			List<Map<String,Object>> AttachedRegionInfo=new ArrayList<Map<String,Object>>();
			
			Map<String,Object> RegionCode=new HashMap<String, Object>();
			RegionCode.put("RegionCode", "01");
			
			mainRequest.put("AttachedRegionInfo", AttachedRegionInfo);
			
			mainRequest.put("BranchCode", mainBranch);
			mainRequest.put("LoginId", save.getLoginId());
			mainRequest.put("RegionCode", "01");
			 
			
			String token = createLogin();
			RestTemplate   temp=new RestTemplateBuilder().setConnectTimeout(Duration.ofSeconds(5)).setReadTimeout(Duration.ofSeconds(5)).build();
			
			
			HttpHeaders header=new HttpHeaders();
			header.setContentType(MediaType.APPLICATION_JSON);
			//header.setCharset("UTF-8");
			header.setBearerAuth(token);
			 


			HttpEntity<?> requestent = 
					new HttpEntity<>(mainRequest, header);

			System.out.println( new Date()+" Start "+ createBranchLink);
			ResponseEntity<Map<String, Object>> postForEntity = temp.exchange(createBranchLink,HttpMethod.POST, requestent,new ParameterizedTypeReference<Map<String, Object>>(){} );
			System.out.println( new Date()+" End "+ createBranchLink);
			System.out.println("response"+postForEntity.getBody());
			//createBranchLink
		}catch(Exception e) {
			e.printStackTrace();
		}
	}

	
	//Admin Insert
	
	public void createMarineAdmin(LoginUserInfo userInfo, LoginMaster saveLogin, String mode, CommonLoginCreationReq req) { 
		CommonLoginInformationReq logreq = req.getLoginInformation();
		CommonPersonalInforReq perreq = req.getPersonalInformation();
		
		
		try {
			/*List<String> companyId = new ArrayList<String>();
			companyId=logreq.getAttachedCompanies();
			List<BranchMaster> branchlist=bmRepo.findByCompanyId(companyId.get(0).toString());
//			String branchCode=branchlist.get(0).getBranchCode();
			String branchCode="46";
			List<String> attachBranch = new ArrayList<String>();
			if (branchlist != null && branchlist.size()>0) {
				for (int i = 0; i < branchlist.size(); i++) {
//					attachBranch.add(branchlist.get(i).getBranchCode().toString());
					attachBranch.add("46");
				}
			}
		
			String region=companyId.get(0);
			List<String> attachRegion = new ArrayList<String>();
			if (companyId != null && companyId.size()>0) {
				for (int i = 0; i < companyId.size(); i++) {
					attachRegion.add(companyId.get(i).toString());
				}
			}
			List<String> products = new ArrayList<String>();
			products.add("3");
			products.add("11");
			List<String> underWriter = new ArrayList<String>();
			underWriter.add("6");
			underWriter.add("7");
			underWriter.add("8");
			underWriter.add("9");
			List<String> broker = new ArrayList<String>();
			broker.add("0");
			List<String> menu = new ArrayList<String>();
			menu.add("0");
			
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy"); 			
			Map<String,Object > mainRequest=new HashMap<String, Object>();
			
			mainRequest.put("LoginId",userInfo.getLoginId());
			mainRequest.put("Password",logreq.getPassword() );
			mainRequest.put("UserType", "admin"  );
			mainRequest.put("UserName",userInfo.getUserName());
			mainRequest.put("BranchCode", branchCode);
			mainRequest.put("RegionCode", region); 	//logreq.getAttachedRegions()
			mainRequest.put("Email", userInfo.getUserMail() );
			mainRequest.put("Status", userInfo.getStatus() );
			mainRequest.put("Mode", mode );
			mainRequest.put("MenuInfo", menu );
			mainRequest.put("ProductInfo",products);
			mainRequest.put("BrokerInfo", broker);
			mainRequest.put("UnderWriterInfo", underWriter);
			mainRequest.put("AttachedBranchInfo", attachBranch);
			mainRequest.put("AttachedRegionInfo", attachRegion);
			
 			String token = createLogin();
			RestTemplate   temp=new RestTemplateBuilder().setConnectTimeout(Duration.ofSeconds(5)).setReadTimeout(Duration.ofSeconds(5)).build();
			
			
			HttpHeaders header=new HttpHeaders();
			header.setContentType(MediaType.APPLICATION_JSON);
			//header.setCharset("UTF-8");
			header.setBearerAuth(token);


			HttpEntity<?> requestent = new HttpEntity<>(mainRequest, header);
					

			System.out.println( new Date()+" Start "+ createAdminLink);
			System.out.println("request"+requestent);
			ResponseEntity<Map<String, Object>> postForEntity = temp.exchange(createAdminLink,HttpMethod.POST, requestent,new ParameterizedTypeReference<Map<String, Object>>(){} );
			System.out.println( new Date()+" End "+ createAdminLink);
			System.out.println("response"+postForEntity.getBody());*/
		}catch (Exception e) {
			e.printStackTrace();
		}
	
		
	}

	//Issuer Insert
	public void createMarineIssuer(LoginUserInfo userInfo, LoginMaster updateLogin, CommonLoginCreationReq req, String mode) {
		CommonLoginInformationReq logreq = req.getLoginInformation();
		CommonPersonalInforReq perreq = req.getPersonalInformation();
		
		try {
			/*List<BranchMaster> branchlist=bmRepo.findByCompanyId(req.getLoginInformation().getAttachedCompanies().get(0));
//			String branchCode=branchlist.get(0).getBranchCode();
			String branchCode="46";
			List<String> attachBranch = new ArrayList<String>();
			if (branchlist != null && branchlist.size()>0) {
				for (int i = 0; i < branchlist.size(); i++) {
//					attachBranch.add(branchlist.get(i).getBranchCode().toString());
					attachBranch.add("46");
				}
			}
			
			List<String> companyId = new ArrayList<String>();
			companyId=logreq.getAttachedCompanies();
			String region=companyId.get(0);
			List<String> attachRegion = new ArrayList<String>();
			if (companyId != null && companyId.size()>0) {
				for (int i = 0; i < companyId.size(); i++) {
					attachRegion.add(companyId.get(i).toString());
				}
			}
			List<String> products = new ArrayList<String>();
			products.add("3");
			products.add("11");
			
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy"); 			
			Map<String,Object > mainRequest=new HashMap<String, Object>();
			
			mainRequest.put("LoginId",userInfo.getLoginId());
			mainRequest.put("Password",logreq.getPassword() );
			mainRequest.put("LoginUserType", "RSAIssuer"  );
			mainRequest.put("BranchCode", branchCode );
			mainRequest.put("RegionCode",region ); 	//logreq.getAttachedRegions()
			mainRequest.put("EmailId", userInfo.getUserMail() );
			mainRequest.put("Status", userInfo.getStatus() );
			mainRequest.put("IssuerName", userInfo.getUserName()  );
//			mainRequest.put("CoreLoginId",userInfo.getLoginId());
			mainRequest.put("CoreLoginId","122");
			mainRequest.put("EffectiveDate", sdf.format(userInfo.getEntryDate()));
			mainRequest.put("OptionMode", mode);
			mainRequest.put("BrokerLinkLocation", null);
			mainRequest.put("AttachedBranchInfo", attachBranch);
			mainRequest.put("ProductInfo", products);
			mainRequest.put("AttachedRegionInfo", attachRegion);
			
 			String token = createLogin();
			RestTemplate   temp=new RestTemplateBuilder().setConnectTimeout(Duration.ofSeconds(5)).setReadTimeout(Duration.ofSeconds(5)).build();
			
			
			HttpHeaders header=new HttpHeaders();
			header.setContentType(MediaType.APPLICATION_JSON);
			//header.setCharset("UTF-8");
			header.setBearerAuth(token);


			HttpEntity<?> requestent = new HttpEntity<>(mainRequest, header);
					

			System.out.println( new Date()+" Start "+ createIssuerLink);
			System.out.println("request"+requestent);
			ResponseEntity<Map<String, Object>> postForEntity = temp.exchange(createIssuerLink,HttpMethod.POST, requestent,new ParameterizedTypeReference<Map<String, Object>>(){} );
			System.out.println( new Date()+" End "+ createIssuerLink);
			System.out.println("response"+postForEntity.getBody());*/
		}catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	//User 
	public void createMarineUser(LoginUserInfo userInfo, LoginMaster updateLogin, CommonLoginCreationReq req,	String mode) {
		
		CommonLoginInformationReq logreq = req.getLoginInformation();
		CommonPersonalInforReq perreq = req.getPersonalInformation();
		
		try {
			/*List<BranchMaster> branchlist=bmRepo.findByCompanyId(req.getLoginInformation().getCompanyId());
			String branchCode=branchlist.get(0).getBranchCode();
			List<String> attachBranch = new ArrayList<String>();
			if (branchlist != null) {
				for (int i = 1; i <= branchlist.size(); i++) {
					attachBranch.add(branchlist.get(i).toString());
				}
			}
			
//			List<String> companyId = new ArrayList<String>();
//			companyId=logreq.getAttachedCompanies();
//			String region=companyId.get(0);
			List<String> attachRegion = new ArrayList<String>();
//			if (companyId != null) {
//				for (int i = 1; i <= companyId.size(); i++) {
					attachRegion.add(req.getLoginInformation().getCompanyId());
//				}
//			}
			List<String> products = new ArrayList<String>();
			products.add("3");
			products.add("11");
			
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy"); 			
			Map<String,Object > mainRequest=new HashMap<String, Object>();
			
			mainRequest.put("UserAgencyCode", logreq.getAgencyCode());
//			mainRequest.put("CustomerName", perreq.getCustomerName() );
			mainRequest.put("AgencyCode", logreq.getAgencyCode() );
			mainRequest.put("CustomerId", perreq.getCustomerId() );
			mainRequest.put("ProductInfo", products ); 	//logreq.getAttachedRegions()
			mainRequest.put("OpenCoverInfo", null);
			mainRequest.put("CustFirstName", perreq.getCustomerName());
			mainRequest.put("Nationality", "");
			mainRequest.put("City", perreq.getCityCode() );
			mainRequest.put("PoBox", perreq.getPobox() );
			mainRequest.put("Country", perreq.getCountryCode() ); 
			mainRequest.put("TelephoneNo", null );
			mainRequest.put("MobileNo",perreq.getUserMobile()); 
			mainRequest.put("Email", perreq.getUserMail() );
			mainRequest.put("LoginId",logreq.getLoginId()); 
			mainRequest.put("Mode", mode );
			mainRequest.put("Password", logreq.getPassword() );
			mainRequest.put("RePassword", logreq.getPassword() );
			mainRequest.put("Status", logreq.getStatus() );
			mainRequest.put("Title", "" );
			mainRequest.put("CustLastName", "" );
			mainRequest.put("DateOfBirth", null );
			mainRequest.put("Gender",null  );
			mainRequest.put("Fax",perreq.getFax()  );
			mainRequest.put("Address1",perreq.getAddress1()  );
			mainRequest.put("Address2", perreq.getAddress2() );
			mainRequest.put("Occupation", null );
			mainRequest.put("SubBranchCode",null );
			mainRequest.put("UserId", null );
			mainRequest.put("UserType",logreq.getUserType() );
			mainRequest.put("AttachedBranchInfo", attachBranch );
			mainRequest.put("AttachedRegionInfo",attachRegion );
			
 			String token = createLogin();
			RestTemplate   temp=new RestTemplateBuilder().setConnectTimeout(Duration.ofSeconds(5)).setReadTimeout(Duration.ofSeconds(5)).build();
			
			HttpHeaders header=new HttpHeaders();
			header.setContentType(MediaType.APPLICATION_JSON);
			//header.setCharset("UTF-8");
			header.setBearerAuth(token);

			HttpEntity<?> requestent = new HttpEntity<>(mainRequest, header);

			System.out.println( new Date()+" Start "+ createUserLink);
			System.out.println("request"+requestent);
			ResponseEntity<Map<String, Object>> postForEntity = temp.exchange(createUserLink,HttpMethod.POST, requestent, new ParameterizedTypeReference<Map<String, Object>>(){} );
			System.out.println(new Date()+" End "+ createUserLink);
			System.out.println("response"+postForEntity.getBody());*/
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}
	//for user
	public void createUserProductLink() {
		try {
			
			Map<String,Object > mainRequest=new HashMap<String, Object>();
			mainRequest.put("OpenCoverInfo","");
			mainRequest.put("UserAgencyCode","");
			mainRequest.put("CustomerName","");
			mainRequest.put("AgencyCode","");
			mainRequest.put("CustomerId","");
			mainRequest.put("ProductInfo",null);
			
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
}
