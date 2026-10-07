package com.maan.eway.auth.service.impl;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.maan.eway.bean.*;
import com.maan.eway.repository.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.maan.eway.auth.dto.ChangePasswordReq;
import com.maan.eway.auth.dto.CommonLoginRes;
import com.maan.eway.auth.dto.ForgetPasswordReq;
import com.maan.eway.auth.dto.GetEncryptionkeyReq;
import com.maan.eway.auth.dto.IpAddressAuthenticationRequest;
import com.maan.eway.auth.dto.LoginRequest;
import com.maan.eway.auth.service.LoginCriteriaQueryService;
import com.maan.eway.auth.service.LoginValidatedService;
import com.maan.eway.auth.token.passwordEnc;
import com.maan.eway.common.req.CommonErrorModuleReq;
import com.maan.eway.common.service.impl.FetchErrorDescServiceImpl;
import com.maan.eway.error.Error;
import com.maan.eway.notification.repository.NotifTransactionDetailsRepository;


@Component
public class LoginValidatedServiceImpl implements LoginValidatedService {
	
	@Autowired
	private FetchErrorDescServiceImpl errorDescService ;

	@Autowired
	private LoginCriteriaQueryService criteriaQuery;
	@Autowired
	private LoginMasterRepository loginRepo;
	@Autowired
	private SessionMasterRepository sessionRep;
	
	@Autowired
	private LoginUserInfoRepository loginUserRepo ;
	
	@Autowired
	private BlockingIpAddressRepository blockIpRepo ;

	@Autowired
	private NotifTransactionDetailsRepository notifRepo;
	
	@Autowired
	private InsuranceCompanyMasterRepository repository;

	@Autowired
	private EserviceCustomerDetailsRepository eserviceCustomerDetailsRepository;

	private Logger log = LogManager.getLogger(LoginValidatedServiceImpl.class);

	private int LOCK_CNT=3;
	
	public CommonLoginRes loginInputValidation(LoginRequest req) {
		CommonLoginRes commonRes = new CommonLoginRes();
		List<Error> list = new ArrayList<Error>();

		log.info(req);
		String changePwd = "N";
		try {
			
			List<SessionMaster> sessionlist = new ArrayList<SessionMaster>();
			List<LoginMaster> data  = new ArrayList<LoginMaster>();
			LoginUserInfo log=loginUserRepo.findByLoginId(req.getLoginId());
			
			String loginId = "" ;
			if (req.getLoginId() == null || StringUtils.isBlank(req.getLoginId())) {
				list.add(new Error("", "UserId", "Please enter Login Id"));	
				
			} else {
				loginId = req.getLoginId() ;
			}
			
			
		   if (req.getPassword() == null || StringUtils.isBlank(req.getPassword())) {
				list.add(new Error("", "Password", "Please enter password"));
			}
			
		   // Guest Login Checking
		/*	if( loginId.equalsIgnoreCase("guest")  ) {
				if (StringUtils.isNotBlank(req.getLoginId()) && StringUtils.isNotBlank(req.getPassword())) {
					LoginMaster loginData = loginRepo.findByLoginId(req.getLoginId());
					if (loginData ==null ) {
						list.add(new Error("", "UserId", "Please enter Valid Login Id"));
					} 
					
					if (loginData != null  ) {
						data = criteriaQuery.isvalidUser(req);
						if (CollectionUtils.isEmpty(data)) {
							list.add(new Error("", "User", "Please enter valid username/password"));
						}
					}
				} 
			}*/
			
			   // Guest Login Checking
//					if (StringUtils.isNotBlank(req.getLoginId()) && StringUtils.isNotBlank(req.getPassword())) {
//						LoginMaster loginData = loginRepo.findByLoginIdAndEffectiveDateStartLessThanEqual(req.getLoginId(), new Date());
//						if (loginData !=null && loginData.getEffectiveDateStart().after(new Date())) {
//							list.add(new Error("", "UserId", "Your  Login Id Date is not started"));
//						} 
//						
//					}
				
		

				
				if (StringUtils.isNotBlank(req.getLoginId()) && StringUtils.isNotBlank(req.getPassword())) {
					LoginMaster loginData = loginRepo.findByLoginId(req.getLoginId());
					
					Date crExpiryDate = null;

					if (log != null) {
					    crExpiryDate = log.getCrExpiryDate();
					}
					
					
					
					if (loginData ==null ) {
						list.add(new Error("", "UserId", "Please enter Valid Login Id"));
					} 
					else if( !"b2c".equalsIgnoreCase(loginData.getSubUserType())) {
						sessionlist = sessionRep.findByLoginIdOrderByEntryDateDesc(req.getLoginId());
					} 
					else if (loginData.getCompanyId().equalsIgnoreCase("100055") ) {
						
						if(crExpiryDate != null && !crExpiryDate.after(new Date())) {
				        	loginData.setStatus("D");
				        	loginRepo.saveAndFlush(loginData);
						}
			        }
					
					if (loginData != null  ) {
						if (! loginData.getStatus().equalsIgnoreCase("Y") ) {
							
							list.add(new Error("", "UserId", "This Login Id is Deactivated"));
						} else {
							//LoginMaster loginData1 = loginRepo.findByLoginId(req.getLoginId());
							
							if (loginData !=null && loginData.getEffectiveDateStart().after(new Date())) {
								list.add(new Error("", "UserId", "Your  Login Id Date is not started"));
							} 
							if(list.size()<=0) {
								data = criteriaQuery.isvalidUser(req);
								if (CollectionUtils.isEmpty(data) ) {

									String pwd = loginData.getPwdCount();
									String loginPasswordCnt = (pwd == null || pwd.trim().isEmpty()) ? "0" : pwd;

									if(Integer.parseInt(loginPasswordCnt)>=LOCK_CNT) {
										loginData.setPwdCount(String.valueOf(Integer.parseInt(loginPasswordCnt)+1));
										loginData.setStatus("T");
										loginRepo.save(loginData);
										list.add(new Error("", "User", "Your Accout is Locked.due to "+LOCK_CNT+" failed attempt." ));
									} else {
										loginData.setPwdCount(String.valueOf(Integer.parseInt(loginPasswordCnt)+1));
										loginRepo.save(loginData);
										list.add(new Error("", "User", "Please enter valid Password"+((Integer.parseInt(loginPasswordCnt)==LOCK_CNT-1)?",Your Accout will be Lock.for one more Invalid attempt.":"" )));
									}
										
								}else if(!"b2c".equalsIgnoreCase(data.get(0).getSubUserType()) &&   isExpired(data.get(0).getLpassDate())) {
									list.add(new Error("", "User", "Password Expired Please Change Your Password"));
									changePwd = "Y";
								}

							}
						}
						
					}
				} 
				
				if(StringUtils.isNotBlank(req.getIpAddress())) {
					List<BlockingIpAddress> blockList =   blockIpRepo.findByIpAddressAndStartDateLessThanEqualAndEndDateGreaterThanEqualAndStatus(req.getIpAddress(),new Date(),new Date() ,"Y");
					if(blockList.size()>0 ) {
						list.add(new Error("1001", "Error", "Cannot Continue Browsing.Try after some time"));
					}
				
				}
				
				if(req.getReLoginKey()!=null && req.getReLoginKey().equalsIgnoreCase("Y") &&  sessionlist.size()>0 ) {
					SessionMaster updatelogout = sessionlist.get(0);
						updatelogout.setLogoutDate(new Date());
						updatelogout.setStatus("DE-ACTIVE");
						sessionRep.save(updatelogout);
				}else if(sessionlist.size()!=0) {
						Calendar cal = new GregorianCalendar();
						cal.setTime(sessionlist.get(0).getStartTime());
						cal.set(Calendar.MINUTE, +45);
						Date after45Minutes = cal.getTime();
						Date today = new Date();
						if(sessionlist.get(0).getStartTime()!=null && after45Minutes.before(today) ) {
							SessionMaster updatelogout = sessionlist.get(0);
							updatelogout.setLogoutDate(new Date());
							updatelogout.setStatus("DE-ACTIVE");
							sessionRep.save(updatelogout);
							
						}else if(sessionlist.get(0).getLogoutDate()==null && list.size()<=0) {
							
						
							List<String> error = new ArrayList<String>();
							error.add("2286");

							CommonErrorModuleReq comErrDescReq = new CommonErrorModuleReq();
							comErrDescReq.setBranchCode("99999");
							comErrDescReq.setInsuranceId("99999");
							comErrDescReq.setProductId("99999");
							comErrDescReq.setModuleId("36");
							comErrDescReq.setModuleName("SESSION LOGIN");
						
							
							list = errorDescService.getErrorDesc(error ,comErrDescReq);
//							list.add(new Error("", "SessionError", "You already have an active logged in session on another device or window Do you want to start new session and terminate that session?", "", "Vous avez déjà une session active sur un autre appareil ou une autre fenêtre. Souhaitez-vous commencer une nouvelle session et mettre fin à l'autre session ?"));
							list.add(new Error("", "SessionError", "User :" + sessionlist.get(0).getUserName() + " : logged in at " +sessionlist.get(0).getEntryDate().toString()));
						}
				}
			
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
			list.add(new Error("", "CommonError", e.getMessage() ));
		}
		if(list!=null && list.size()>0  ) {
			commonRes.setErrorMessage(list);
			commonRes.setChangePasswordYn(changePwd);
			commonRes.setCommonResponse(null);
			commonRes.setIsError(true);
			commonRes.setMessage("Failed");
		}
	
		return commonRes;
	}

	private boolean isExpired(Date date) {

		Date d = date;
		Date d1 = new Date();

		// not expired
		if (d1.compareTo(d) < 0) {
			return false;
		} 
		// both date are same
		else if (d.compareTo(d1) == 0) {
			if (d.getTime() < d1.getTime()) {// not expired
				return false;
			} else if (d.getTime() == d1.getTime()) {// expired
				return true;
			} else {// expired
				return true;
			}			
		} 
		// expired
		else {
			return true;
		}
	}


	  public static String getToday(String format){
	     Date date = new Date();
	     return new SimpleDateFormat(format).format(date);
	 }

	  /*
	@Override
	public List<Error> LoginChangePwdValidation(ChangePasswordReq req) {

		log.info(req);

		List<Error> list = new ArrayList<Error>();

		if (req.getUserId() == null || StringUtils.isBlank(req.getUserId())) {
			
			list.add(new Error("", "UserId", "Please enter username"));
			list.add(new Error("", "ChangePassword", "You are not authorized user..!"));
			
		}else {
		
			ClaimLoginMasterId id = new ClaimLoginMasterId();
			id.setLoginId(req.getUserId());
			id.setCompanyId(req.getCompanyId());
			
			Optional<ClaimLoginMaster> model = loginRepo.findById(id);
			if (model.isPresent()) {

				String epass = "";
				passwordEnc passEnc = new passwordEnc();
				
				if (req.getNewPassword() == null || StringUtils.isBlank(req.getNewPassword())) {
					list.add(new Error("", "New password", "Please enter New password"));
				}else if (!validPassword(req.getNewPassword())) {
					list.add(new Error("", "NewPassword", "Please enter the valid password"));					
				}
				else {
					epass = passEnc.crypt(req.getNewPassword().trim());
				}	
				
				if (req.getOldpassword() == null || StringUtils.isBlank(req.getOldpassword())) {
					list.add(new Error("", "Old password", "Please enter Oldpassword"));
				} else if (model.get().getPassword().equals(epass)) {
					list.add(new Error("", "ChangePassword", "Oldpassword  and Newpassword should not match"));
				} else if(model.get().getLpass1().equals(epass) || model.get().getLpass2().equals(epass) || model.get().getLpass3().equals(epass) || model.get().getLpass4().equals(epass) || model.get().getLpass5().equals(epass)) {
					list.add(new Error("", "ChangePassword", "Newpassword should not be last 5 Password"));
				} 
			} else {
				list.add(new Error("", "ChangePassword", "You are not authorized user..!"));
			}
			
		}

 
		return list;
	}

	
	@Override
	public List<Error> InsertLoginValidation(InsertLoginMasterReq req) {

		List<Error> list = new ArrayList<Error>();
		try { 
			
	
		if ( StringUtils.isBlank(req.getLoginId())) {
			list.add(new Error("01", "LoginId", "Please enter LoginId"));
		} else if (req.getLoginId().length() <5 ) {
			list.add(new Error("01", "LoginId", "LoginId Under 5 Charecter Not Allowed"));
		} else {
			ClaimLoginMaster loginData = loginRepo.findByLoginId(req.getLoginId());
			if(loginData !=null  && StringUtils.isBlank(req.getAgencyCode()) ) {
				list.add(new Error("01", "LoginId", "This Login Id Already Available"));
			} else if (loginData !=null  && (! loginData.getAgencyCode().equalsIgnoreCase(req.getAgencyCode())  )) {
				list.add(new Error("01", "LoginId", "This Login Id Already Available"));
			}
		}
		
		if ( StringUtils.isBlank(req.getCreatedBy())) {
			list.add(new Error("03", "CreatedBy", "Please enter CreatedBy"));
		}

		if ( StringUtils.isBlank(req.getMobileNumber())) {
			list.add(new Error("04", "MobileNumber", "Please enter MobileNumber"));
		} else if (req.getMobileNumber().length() <5 ) {
			list.add(new Error("04", "LoginId", "Please Enter Valid Mobile Number"));
		} else if (! req.getMobileNumber().matches("[0-9]+") ) {
			list.add(new Error("04", "LoginId", "Please Enter Valid Mobile Number"));
		}

		if ( StringUtils.isBlank(req.getPassword())) {
			list.add(new Error("05", "Password", "Please enter Password"));
		} else if (req.getMobileNumber().length() <5 ) {
			list.add(new Error("05", "Password", "Password Under 5 Charecter Not Allowed"));
		} 

		if (StringUtils.isBlank(req.getRemarks())) {
			list.add(new Error("06", "Remarks", "Please enter Remarks"));
		}

		if (StringUtils.isBlank(req.getStatus())) {
			list.add(new Error("07", "Status", "Please Select Status"));
		}

		if (StringUtils.isBlank(req.getUserMail())) {
			list.add(new Error("08", "UserMail", "Please enter UserMail"));
		} else {
			String regex = "^[a-zA-Z0-9_+&*-]+(?:\\." + "[a-zA-Z0-9_+&*-]+)*@" + "(?:[a-zA-Z0-9-]+\\.)+[a-z"
					+ "A-Z]{2,7}$";
			Pattern pattern = Pattern.compile(regex);
			Matcher matcher = pattern.matcher(req.getUserMail());
			 if (!matcher.matches()) {
				 list.add(new Error("101", "email", "Please enter vaild email"));
			}
		}
		
		if (StringUtils.isBlank(req.getUsername())) {
			list.add(new Error("09", "Username", "Please enter Username"));
		} else if(req.getUsername().length() < 5 ||   isValidName(req.getUsername() )) {
			list.add(new Error("101", "Username Name", "Please Enter Valid Username"));
		} 
		
		if ( StringUtils.isBlank(req.getCompanyId())) {
			list.add(new Error("02", "CompanyId", "Please enter CompanyId"));
		} 

		if(req.getBranchCode().size() == 0  ) {
			list.add(new Error("10", "BranchCode", "Please Select Branches"));
		}
		
		boolean status = false ;
		
		if( StringUtils.isBlank(req.getUserType()) ) {
			list.add(new Error("11", "UserType", "Please Select UserType"));
		} else {
			if(req.getUserType().equalsIgnoreCase("UnderWritter")  ) {
				if(req.getUnderWritterList().size() ==0  ) {
					list.add(new Error("11", "UserType", "Please Add Atleast One Under Witter List with Status Y"));
				} else {
					List<Long>   ClassTypes = new ArrayList<Long>(); 
					List<Long>   PolicyTypes = new ArrayList<Long>();
					
					Long rowCount = 0L ;
					for (UnderWritterDetailsReq uw :   req.getUnderWritterList() ) {
						rowCount = rowCount + 1L;
						if ( StringUtils.isBlank(uw.getClassId())) {
							list.add(new Error("01", "ClassId", "Please Select Class In Row No : " + rowCount ));
						} else {
							List<Long> ClassTypes2 = ClassTypes.stream().filter( o ->  Long.valueOf(o).equals(Long.valueOf(uw.getClassId()))  ).collect(Collectors.toList());
							if(ClassTypes2.size() > 0  ) {
								list.add(new Error("01", "ClassId", "Class Duplicate Selected In Row No : "  + rowCount));
							}
						}
						if( StringUtils.isBlank(uw.getStatus()) ) {
							list.add(new Error("02", "Stauts", "Please Select Status In Row No :  "  + rowCount));
						} else if (uw.getStatus().equalsIgnoreCase("Y") ) {
							status = true ;
						}
					/*	if ( StringUtils.isBlank(uw.getClassDesc())) {
							list.add(new Error("02", "ClassDesc", "Please Select Class"));
						}
						if ( StringUtils.isBlank(uw.getPolicyType())) {
							list.add(new Error("02", "PolicyType", "Please Select PolicyType"));
						}
						 */
	/*					if ( StringUtils.isBlank(uw.getPolicyTypeId())) {
							list.add(new Error("02", "PolicyTypeId", "Please Select PolicyTypeId In Row No :  "  + rowCount));
						} else {
							List<Long> PolicyTypes2 = PolicyTypes.stream().filter( o ->  Long.valueOf(o).equals(Long.valueOf(uw.getPolicyTypeId()))  ).collect(Collectors.toList());
							if(PolicyTypes2.size() > 0  ) {
								list.add(new Error("01", "PolicyTypeId", "PolicyTypes Duplicate Selected In Row No : "  + rowCount));
							}
						}
						
						if ( StringUtils.isBlank(uw.getSumInsuredStart())) {
							list.add(new Error("02", "SumInsuredStart", "Please Enter SumInsuredStart In Row No :  " + rowCount ));
						
						} else if (!  uw.getSumInsuredStart().matches("[0-9.]+") ) {
							list.add(new Error("02", "SumInsuredStart", "Please Enter Valid SumInsuredStart In Row No :  " + rowCount));
						
						} else if ( StringUtils.isBlank(uw.getSumInsuredEnd())) {
							list.add(new Error("02", "SumInsuredEnd", "Please Enter SumInsuredEnd In Row No :  " + rowCount));
						
						} else if (!  uw.getSumInsuredEnd().matches("[0-9.]+") ) {
							list.add(new Error("02", "SumInsuredEnd", "Please Enter Valid SumInsuredEnd In Row No :  " + rowCount));
						
						} else if (Long.valueOf(uw.getSumInsuredEnd())< Long.valueOf(uw.getSumInsuredEnd()) ) {
							list.add(new Error("02", "SumInsuredEnd", "SumInsuredStart Greater Than SumInsuredEnd Not Allowed In Row No : "  + rowCount));
						}
						
						
					
					}
					
					if (status == false ) {
						list.add(new Error("11", "Status", "Please Add Atleast One Under Witter List with Status Y"));
					}
				}
			}
		}
		} catch (Exception e) {
			e.printStackTrace();
			list.add(new Error("01", "Common Error", e.getMessage()));
		}
		return list;
	}  */


	public boolean isValidName(String name) {
		String s = name;
		String regx = "^[\\p{L} .'-]+$";
		Pattern p = Pattern.compile(regx);
		Matcher m = p.matcher(s);
		try {
			if (m.matches()) {
				return false;
			}

		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
			return true;
		}
		return true;
	}
	
	@Override
	public List<Error> LoginChangePasswordValidation(ChangePasswordReq req) {
		log.info(req);
		
		List<Error> list = new ArrayList<Error>();
		
		if(StringUtils.isBlank(req.getLoginId())) {
			list.add(new Error("","Login Id", "Please Enter Login Id"));
		}
		else {
			passwordEnc passEnc = new passwordEnc();
			String epass = passEnc.crypt(req.getOldpassword().trim());
			log.info("Encrypted password"+ epass);
			LoginMaster model = loginRepo.findByLoginIdAndPassword(req.getLoginId(), epass);
			if(model !=null ) {
				if(req.getNewPassword()==null || StringUtils.isBlank(req.getNewPassword())) {
					list.add(new Error ("", "New Password", "Please Enter New Password"));
				} 
				
				/*
				 * else if (!validPassword(req.getNewPassword())) { list.add(new
				 * Error("","New Password","Please Enter Valid Password")); }
				 */
				 if (!passwordvaildation(req.getNewPassword(),req.getLoginId())) {
					 String errormsg=geterrormsg(req.getLoginId());
						
						list.add(new Error("","New Password",errormsg));
				}
				
				else {
					epass = passEnc.crypt(req.getNewPassword().trim());
				}	
				
				if(StringUtils.isNotBlank(req.getType()) && req.getType().equalsIgnoreCase("ForgotPassword") ) {
					if (req.getOldpassword() == null || StringUtils.isBlank(req.getOldpassword())) {
						list.add(new Error("", "Old password", "Please enter Temporary Password"));
					} else if (model.getPassword().equalsIgnoreCase(epass)) {
						list.add(new Error("", "ChangePassword", "Temporary Password  and Newpassword should not match"));
					}
				} else {
					if (req.getOldpassword() == null || StringUtils.isBlank(req.getOldpassword())) {
						list.add(new Error("", "Old password", "Please enter Old Password"));
					} else if (model.getPassword().equalsIgnoreCase(epass)) {
						list.add(new Error("", "ChangePassword", "Old Password  and New Password should not match"));
					}
					
				else if((model.getLpass1()!=null && model.getLpass1().equals(epass)) || (model.getLpass2()!=null&&model.getLpass2().equals(epass)) || (model.getLpass3()!=null&&model.getLpass3().equals(epass)) || (model.getLpass4()!=null&&model.getLpass4().equals(epass)) || (model.getLpass5()!=null&&model.getLpass5().equals(epass))) {
					list.add(new Error("", "ChangePassword", "Newpassword should not be last 5 Password"));
				} 
			} 
			}
			
			else {
				list.add(new Error("", "ChangePassword", "You are not authorized user..!"));
			}
			
		}

 
		return list;
	}
	
	private boolean validPassword(String newPassword) {
		
		Pattern pattern=Pattern.compile("(?=\\S+$).{5,20}");
    	Matcher matcher = pattern.matcher(newPassword);
    	return matcher.matches();
	}
	public String geterrormsg(String Loginid)
	{
		String character=null,symbols=null,error="",max=null,min=null;
		int numbg=-1,numend=-1;
	try {
      if (Loginid != null) {
	  LoginMaster logindetails = loginRepo.findByLoginId(Loginid);
	  String company_id = logindetails.getCompanyId();
	  List<InsuranceCompanyMaster> req1 = repository.findTopByCompanyIdOrderByAmendIdDesc(company_id);
	  if(req1!=null)
	  {
		  InsuranceCompanyMaster req = req1.stream()
  			    .filter(record -> "Y".equalsIgnoreCase(record.getPatternstatus() != null ? record.getPatternstatus().trim() : null))
  			    .findFirst()
  			    .orElse(null); // Throw exception if no matching record is found

		  if(req!=null) {
		  character=req.getAlphabet();
		  numbg=req.getNumericDigitsStart()==null?numbg:Integer.valueOf(req.getNumericDigitsStart());
		  numend=req.getNumericDigitsEnd()==null?numbg:Integer.valueOf(req.getNumericDigitsEnd());
		  symbols=req.getSymbols();
		 max =req.getTotalmax();
		  min=req.getTotalmin();
		  if(character!=null && numbg!=-1 && numend!=-1 && symbols!=null && max!= null && min!=null)
		    error ="The password should contains character "+req.getAlphabet()+", numberic digit from "+numbg+" to "+numend+", symbols should contain "+ symbols +" , minimum password length is "+min +" and  maximum Password length is "+max +"...";
		  
		  else   error ="The passwords should contain a combination of characters and password length between 5 to 20 characters  long.."; 
		   
		 }
		  else error ="The passwords should contain a combination of characters and password length between 5 to 20 characters  long.."; 
		   
	  }
	  else  error ="The passwords should contain a combination of characters and password length between 5 to 20 characters  long...";   
	  
			}
		} catch (Exception ex) {
			ex.getMessage();
			return "Please Enter Valid Password";
		}
	return error;
	}
	public boolean  passwordvaildation(String Password, String Loginid)
	{
		try {
		String Pattern="";
     LoginMaster logindetails=loginRepo.findByLoginId(Loginid);
     String company_id=logindetails.getCompanyId();
		
	     List<InsuranceCompanyMaster> req1= repository.findTopByCompanyIdOrderByAmendIdDesc(company_id);
	     if(req1!=null )
	     {
	    	 InsuranceCompanyMaster req = req1.stream()
	    			    .filter(record -> "Y".equalsIgnoreCase(record.getPatternstatus() != null ? record.getPatternstatus().trim() : null))
	    			    .findFirst()
	    			    .orElse(null);
	    			System.out.println("Found record: " + req);
	    if(req!=null) {
	    	int lengthmin=5;
			int max=20;
    if(!StringUtils.isBlank(req.getTotalmin()) &&!StringUtils.isBlank(req.getTotalmax()))
    {
    	lengthmin =Integer.valueOf(req.getTotalmin())-1;
    	 max=Integer.valueOf(req.getTotalmax())-1;
    }
	    	String collect2=!StringUtils.isBlank(req.getAlphabet()) ?req.getAlphabet(): "";
		       String aplhabet= !StringUtils.isBlank(req.getAlphabet()) ? "(?=.*["+req.getAlphabet()+"])" : ""; //^(?=.*[a-zA-Z])or null
		       if(aplhabet.equals("(?=.*[A-Za-z])"))
		       {
		    	   aplhabet="(?=.*[A-Za-z])(?=.*[A-Z])(?=.*[a-z])" ;//(?=.*[A-Za-z])(?=.*[a-z])
		       }
		      
		       String   number=!StringUtils.isBlank(req.getNumericDigitsStart()) && !StringUtils.isBlank(req.getNumericDigitsEnd())? req.getNumericDigitsStart()+"-"+req.getNumericDigitsEnd() : "";//0-9
			  	   String	symbols=!StringUtils.isBlank(req.getSymbols()) ? req.getSymbols() :"";//@#$%^&+=!
		  	   String length=!StringUtils.isBlank(req.getTotalmin())&&!StringUtils.isBlank(req.getTotalmax()) ? "{"+lengthmin+","+req.getTotalmax()+"}" :"";//{8,10}	
		       String collect="["+collect2+number+symbols+"^]";//
		      Pattern ="^"+(aplhabet)+("(?=.*["+number+"])")+("(?=.*["+symbols+"^])")+("["+collect2+"]")+(collect)+(length)+"$";//^(?=.*[A-Za-z])(?=.*[a-z])(?=.*[0-9])(?=.*[@#^])[A-Za-z][A-Za-z0-9@#^]{7,19}$
					
	    }else Pattern=null;
	     }
	     System.out.print("Generated Pattern is ----------->"+Pattern);
	     if(company_id==null||Pattern==null||Pattern.equals("^[]$"))
	     {
	    	 Pattern="(?=\\S+$).{5,20}"; //default pattern
	     }
	     
	    if(Password.matches(Pattern))
		   {
			   return true;
		   }
		
    
		}catch(Exception ee)
		{
			ee.printStackTrace();
		}
		return false;
	
	}
	
	

//	@Override
//	public List<Error> forgetPwdValidation(ForgetPasswordReq req) {
//		log.info(req);
//		
//		List<Error> list = new ArrayList<Error>();
//		
//		if(StringUtils.isBlank(req.getLoginId())) {
//			list.add(new Error("","Login Id", "Please Enter Login Id"));
//		}
//		else {
//			LoginMaster model =  loginRepo.findByLoginId(req.getLoginId());
//			if(model ==null ) {
//				list.add(new Error("", "ForgotPassword", "You are not authorized user..!"));
//				 	
//			} /*else if( ! model.getStatus().equalsIgnoreCase("Y") ) {
//				list.add(new Error("", "ForgotPassword", " Login Id is Deactive"));
//				
//			} */else if(StringUtils.isBlank(req.getEmailId())) {
//				list.add(new Error("", "EmailId", "Please Enter Email Id"));
//			}  else  {
//				LoginUserInfo userInfo =  loginUserRepo.findByLoginId(req.getLoginId());
//				if(userInfo!=null && userInfo.getUserMail()!=null && ! userInfo.getUserMail().equalsIgnoreCase(req.getEmailId()) ) {
//					list.add(new Error("","Login Id", "Requested Mail Id Not Matching with User Mail Id"));
//				}
//				
//			}  
//			 
//			
//		}
//		return list;
//	}
	
	@Override
	public List<Error> forgetPwdValidation(ForgetPasswordReq req) {

	    List<Error> list = new ArrayList<>();

	    if (StringUtils.isBlank(req.getLoginId())) {
	        list.add(new Error("", "LoginId", "Please Enter Login Id"));
	    }

	    if (StringUtils.isBlank(req.getEmailId())) {
	        list.add(new Error("", "EmailId", "Please Enter Email Id"));
	    }

	    return list;
	}

	@Override 
	public List<Error> validateTinyUrlId(String object,String tinyGroupId) {
		try {
			
			Integer count=notifRepo.countByTinyUrlActiveAndTinyUrlId("Y",object);
			if(count<1) {
				List<Error> erros=new ArrayList<Error>();
				Error err=new Error();
				err.setCode("9844");
				err.setField("TinyUrl");
				err.setMessage("Tiny Url is Deactivated");
				erros.add(err);
				return erros;
			}else {
				//notifRepo.updateOtherActiveTinyUrl(tinyGroupId,object);
			}
		}catch(Exception e) {
			e.printStackTrace();
			
		}
		return null;
	}

	@Override
	public void updateTinyUrlId(String object,String tinyGroupId) {
		notifRepo.updateOtherActiveTinyUrl(tinyGroupId,object);
		
	}

	@Override
	public CommonLoginRes loginIpAddressValidation(IpAddressAuthenticationRequest req) {
		CommonLoginRes commonRes = new CommonLoginRes();
		List<Error> list = new ArrayList<Error>(); 
		try {
			if(StringUtils.isBlank(req.getIpAddress())) {
				list.add(new Error("","Ip Address", "Please Enter Ip Address"));
			}
			if(StringUtils.isBlank(req.getUserType())) {
				list.add(new Error("","UserType", "Please Enter UserType"));
			} else 	if(StringUtils.isNotBlank(req.getUserType()) && ! req.getUserType().equalsIgnoreCase("B2C") ) {
				list.add(new Error("","UserType", "Please Enter Valid UserType"));
			}
		} catch (Exception e ) {
			e.printStackTrace();
			list.add(new Error("","Common Error", "Please Enter Ip Address"));
		}
		if(list!=null && list.size()>0  ) {
			commonRes.setErrorMessage(list);
			commonRes.setChangePasswordYn("N");
			commonRes.setCommonResponse(null);
			commonRes.setIsError(true);
			commonRes.setMessage("Failed");
		}
		return commonRes;
	}

	@Override
	public List<Error> getEncryptionkeyReq(GetEncryptionkeyReq req) {
		List<Error> list = new ArrayList<Error>();
		if (Objects.equals(req.getIsEmail(), "Y")) {
			EserviceCustomerDetails customerDetails =
					eserviceCustomerDetailsRepository.findByEmail1AndCompanyId(
							req.getLoginId(),
							req.getInsuranceId()
					);

			if (customerDetails != null) {
				req.setLoginId(
						customerDetails.getMobileCode1() + customerDetails.getMobileNo1()
				);
			}
		}
		Optional<LoginMaster> model =  Optional.ofNullable(loginRepo.findByLoginId(req.getLoginId()));
		try {
			if(StringUtils.isBlank(req.getLoginId())) {
				list.add(new Error("","Login Id", "Please Enter Login Id"));
			}else {
				if(model.isEmpty()) {
					list.add(new Error("", "Login", "You are not authorized user..!"));
					 	
				} 
			}
			if(StringUtils.isBlank(req.getProductId())) {
				list.add(new Error("", "ProductId", "Please Enter ProductId..!"));
			}
			if(StringUtils.isBlank(req.getInsuranceId())) {
				list.add(new Error("", "InsuranceId", "Please Enter InsuranceId..!"));
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		return list;
	}



		

}