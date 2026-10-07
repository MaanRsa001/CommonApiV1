package com.maan.eway.notification.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.maan.eway.bean.MailMaster;
import com.maan.eway.bean.NotifTemplateMaster;
import com.maan.eway.bean.SmsConfigMaster;
import com.maan.eway.notification.bean.NotifTransactionDetails;
import com.maan.eway.notification.req.JobCredentials;
import com.maan.eway.notification.req.Mail;
import com.maan.eway.notification.req.Messenger;
import com.maan.eway.notification.req.Sms;
import com.maan.eway.notification.req.SmsConfigMasterDto;

public class PushedStateChange implements  Function<NotifTransactionDetails,List<Object>>{


	private NotifTemplateMaster master;
 	 private MailMaster mailMaster;	 
	 private SmsConfigMaster smsmaster;
	 //private NotifTransactionDetails sms;
 
	public PushedStateChange(NotifTemplateMaster master, MailMaster mailMaster, SmsConfigMaster smsmaster) {
		this.master=master;
		this.mailMaster=mailMaster;
		this.smsmaster=smsmaster;
		//this.sms =sms;
	}

	@Override
	public List<Object> apply(NotifTransactionDetails obj) {
		try {
	        ObjectMapper oMapper = new ObjectMapper();
			Map<String, Object> t = oMapper.convertValue(obj, Map.class);
			
			List<Object> a=new ArrayList<Object>();
			if(master.getWhatsappRequired().equals("Y") ) {
				Object messengerTo=getValue(t,master.getToMessengerno());
				if(messengerTo!=null) {
					Messenger m=Messenger.builder()
							.messengerBody((String) getContentFrame(t, master.getWhatsappBodyEn()))
							.messengerRegards((String) getContentFrame(t, master.getWhatsappRegards()))
							.messengerSubject((String) getContentFrame(t, master.getWhatsappSubject()))
							.messengerTo((String) messengerTo)
							.build();
					a.add(m);
				}
			}
			if(master.getSmsRequired().equals("Y") ) {
				Object smsTo= getValue(t,master.getToSmsno()) ;
				if(smsTo!=null) {
					Sms s=Sms.builder()
							.smsContent((String) getContentFrame(t, master.getSmsBodyEn()))
							.smsRegards((String) getContentFrame(t, master.getSmsRegards()))
							.smsSubject((String) getContentFrame(t, master.getSmsSubject()))
							.mobileNo((String) smsTo)	
							//.smsFrom(smsmaster.getSenderId())
							.master(SmsConfigMasterDto.builder().smsPartyUrl(smsmaster.getSmsPartyUrl()).smsUserPass(smsmaster.getSmsUserPass()).smsUserName(smsmaster.getSmsUserName())
									.senderid(smsmaster.getSenderId())
									.secureYn("Y")
									.insId(t.get("companyid")==null?BigDecimal.ZERO:new BigDecimal(t.get("companyid").toString()))
									.build())
							.mobileCode(obj.getCustomerPhoneCode().toString())
							.notifNo(t.get("notifNo")==null?0L:Long.parseLong(t.get("notifNo").toString()))
							.build();
					a.add(s);
					
				}
				 
			}
			if(master.getMailRequired().equals("Y") ) {

				Object tomailds= getValue(t,master.getToEmail());
				String[] mailArray = String.valueOf(tomailds).split(",");
				int mailSize = mailArray.length;
				if(tomailds!=null) {
					String tomaildstr=(String )tomailds;
					String tomailid=tomaildstr;
					String ccMailId ="";
					List<String> mailcc=new ArrayList<>();
					if(tomaildstr.indexOf(",")!=-1) {
						tomailid=mailArray[0];
						
						if (mailArray.length > 1) {
					        for (int i = 1; i < mailArray.length; i++) {
					            mailcc.add(mailArray[i]);
					        }

					        // limit CC to maximum 5 emails
					        if (mailcc.size() > 5) {
					            mailcc = mailcc.subList(0, 5);
					        }
					    }
						//ccMailId=tomaildstr.split(",")[mailSize-1];
						//String[] mailcsc = ccMailId.split(",");
						//List<String> asList = Arrays.asList(mailcsc);
						//mailcc= (asList.size()>5)?asList.subList(0, 5):asList;
					}
					List<String> tomails=new LinkedList<String>();
					tomails.add(tomailid);
					String mailSubject=(String) getContentFrame(t, master.getMailSubject());
					String filesStr=t.get("attachFilePath")==null?"":t.get("attachFilePath").toString();
					List<String> files=new LinkedList<String>();
					if(StringUtils.isNotBlank(filesStr) && filesStr.indexOf(";")!=-1) {
						String[] split = filesStr.split(";");
						for(int i=0;i<split.length;i++)
						files.add(split[i]);
					}else if(StringUtils.isNotBlank(filesStr))
						files.add(filesStr);
					
					String templatebody=getTemplateFrame(t, master);
					 JobCredentials master =JobCredentials.builder()
							 .address(mailMaster.getAddress())
							 .amendId(mailMaster.getAmendId())
							 .applicationId(mailMaster.getCompanyId())
							 .authorizYn("Y")
							 .branchCode(mailMaster.getBranchCode())
							 .companyId(mailMaster.getCompanyId())
							 .companyName(mailMaster.getCompanyId())
							 .expDate(mailMaster.getEffectiveDateEnd().toString())
							 .expTime(mailMaster.getEffectiveDateEnd().toString())
							 .homeApplicationId(mailMaster.getCompanyId()).mailCc(null)
							 .pwdCnt(BigDecimal.ZERO)
							 .pwdLen(BigDecimal.ONE)
							 .smtpHost(mailMaster.getSmtpHost())
							 .smtpPort(new BigDecimal(mailMaster.getSmtpPort()))
							 .smtpPwd(mailMaster.getSmtpPwd()).smtpUser(mailMaster.getSmtpUser())
							 .sNo(mailMaster.getSNo())
							 .toAddress("100020".equals(mailMaster.getCompanyId())?mailMaster.getSmtpUser():
								 "100002".equals(mailMaster.getCompanyId())?mailMaster.getSmtpUser():"")
							 .build();
							 //.convertValue( mailMaster,JobCredentials.class);
					 
					 
					Mail ml=Mail.builder()
							.mailbody(templatebody)
							.mailbodyContenttype("text/html")							
							.subject(mailSubject)
							.tomails(tomails)
							.ccmails(mailcc)
							.files(files)
							.master(master)
							//.credential(.host(mailMaster.getSmtpHost()).port(mailMaster.getSmtpPort()).isSSL(true).password(mailMaster.getSmtpPwd()).username(mailMaster.getSmtpUser()).build())
							.notifNo(Long.parseLong(t.get("notifNo").toString()))
							.build();
					a.add(ml);
				}
				
			}
			return a;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	
	private Object getValue(Map<String, Object> t, String fieldNameString) {
		 try {
			Object o=(Object) t.get(fieldNameString);
			if (o instanceof BigDecimal) {
				return o.toString();
			}
			return o;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
		
	}

	 
	private Object getContentFrame(Map<String, Object> t ,String messageTemplate) {
		try {
			 
			if(StringUtils.isNotBlank(messageTemplate)) {
				StringBuffer b=new StringBuffer(messageTemplate);
				while (b.indexOf("{")!=-1 && b.indexOf("}")!=-1) {
					String tx = b.substring(b.indexOf("{")+1, b.indexOf("}"));
					b.replace(b.indexOf("{"), b.indexOf("}")+1, String.valueOf(t.get(tx)));
				} 
				return b.toString();
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	} 
	private String getTemplateFrame(Map<String, Object> t ,NotifTemplateMaster m) {
		try {
			 String baseTemplate="<div style=\"margin: 0px auto;width: 700px;max-width: 90%;padding-top: 20px;background-color: rgb(255,255,255);\">\r\n"
			 		+ "        <div style=\"text-align: center; margin-bottom: 20px;\"> <img height=\"20px\"> </div>\r\n"
			 		+ "<div class=\"adM\" style=\"text-align: center;\"><img src=\"{xCompanyLogox}\">\r\n"
			 		+ "        \r\n"
			 		+ "        </div>"
			 		+ "        <div style=\"margin: 0px auto; width: 100%; line-height: 1.3;\"> <img width=\"100%\">\r\n"
			 		+ "          <div style=\"padding: 20px 30px;\">\r\n"
			 		+ "            <p style=\"text-transform: capitalize;\">Hi {xCustomerx},</p>\r\n"
			 		+ "            <p style=\"\r\n"
			 		+ "    font-size: 17px;\r\n"
			 		+ "\">{xsubjectx}</p>\r\n"
			 		+ "            <div style=\"border: 1px solid rgb(221, 221, 221); padding: 20px;\">\r\n"
			 		+ "              {xmailBodyx}"
			 		+ "                \r\n"
			 		+ "            </div>\r\n"			 		
			 		+ "             \r\n"
			 		+ "             \r\n"
			 		+ "            <p style=\"margin-top: 30px;\">We care,</p>\r\n"
			 		+ "            <p>{xregardsx}</p>\r\n"
			 		+ "            <div style=\"font-size: 0.8em; color: rgba(0, 0, 0, 0.4); margin-top: 30px;\">\r\n"
			 		+ "              <p>Your premium may need to be adjusted if the provided\r\n"
			 		+ "                information is incorrect.</p>\r\n"
			 		+ "            </div>\r\n"
			 		+ "            <div style=\"color: rgba(0, 0, 0, 0.4); font-size: 0.8em; border-top: 1px solid; margin-top: 30px; line-height: 0.9em; text-align: center; padding: 30px 0px;\">\r\n"
			 		+ "              <p><a href='#'/>\r\n"
			 		+ "               {xCompanyAddressx}</p>\r\n"
			 		+ "              <div>\r\n"
			 		+ "                <p style=\"font-weight: bold; color: rgb(0, 0, 0); margin-top: 20px;\">Connect with us</p>\r\n"
			 		+ "                </div>\r\n"
			 		+ "            </div>\r\n"
			 		+ "          </div>\r\n"
			 		+ "        </div>\r\n"
			 		+ "      </div>";
		  
			     String mailBody=(String) getContentFrame(t, master.getMailBody());
			     String mailSubject=(String) getContentFrame(t, master.getMailSubject());
				String mailRegards=(String) getContentFrame(t, master.getMailRegards());
				String xCustomerx="Team";
				if("customerMailid".equals(master.getToEmail())) {
					xCustomerx=(t.get("customerName")==null || StringUtils.isBlank(t.get("customerName").toString()))?"Team":t.get("customerName").toString();
				}else if("brokerMailId".equals(master.getToEmail())) {
					xCustomerx=(t.get("brokerName")==null || StringUtils.isBlank(t.get("brokerName").toString()))?"Team":t.get("brokerName").toString();
				}else if("uwMailid".equals(master.getToEmail())) {
					xCustomerx=(t.get("uwName")==null || StringUtils.isBlank(t.get("uwName").toString()))?"Team":t.get("uwName").toString();
				}
				
				Map<String,String> hmap=new HashMap<String,String>();
				hmap.put("xregardsx", mailRegards);
				hmap.put("xmailBodyx", mailBody);
				hmap.put("xCustomerx", xCustomerx);
				hmap.put("xsubjectx", mailSubject);
				hmap.put("xCompanyLogox", String.valueOf(t.get("companyLogo")));
				hmap.put("xCompanyAddressx", String.valueOf(t.get("companyAddress")));
				
			StringBuffer b=new StringBuffer(baseTemplate);
			while (b.indexOf("{")!=-1 && b.indexOf("}")!=-1) {
				 String tx = b.substring(b.indexOf("{")+1, b.indexOf("}"));
				 b.replace(b.indexOf("{"), b.indexOf("}")+1, String.valueOf(hmap.get(tx)==null?t.get(tx):hmap.get(tx)));
			} 
			return b.toString(); 
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	} 
}
