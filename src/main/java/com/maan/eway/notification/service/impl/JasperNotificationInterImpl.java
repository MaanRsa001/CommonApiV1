package com.maan.eway.notification.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.maan.eway.bean.MailMaster;
import com.maan.eway.bean.NotifTemplateMaster;
import com.maan.eway.bean.SmsConfigMaster;
import com.maan.eway.jasper.req.JasperDocumentReq;
import com.maan.eway.jasper.res.JasperDocumentRes;
import com.maan.eway.jasper.service.JasperService;
import com.maan.eway.notification.bean.NotifTransactionDetails;
import com.maan.eway.notification.req.Mail;
import com.maan.eway.notification.req.Messenger;
import com.maan.eway.notification.req.Sms;
import com.maan.eway.notification.service.JasperNotificationInter;
import com.maan.eway.notification.service.MailJob;
import com.maan.eway.notification.service.PushedStateChange;
import com.maan.eway.notification.service.SmsJob;
import com.maan.eway.repository.MailMasterRepository;
import com.maan.eway.repository.NotifTemplateMasterRepository;
import com.maan.eway.repository.SmsConfigMasterRepository;
@Service
public class JasperNotificationInterImpl {

	
	@Value(value = "${kafka.push.mail}")
	private String kafkaLink;
	
	@Value(value = "${kafka.push.sms}")
	private String kafkaLinksms;
	
	@Autowired
	private MailMasterRepository mailRepo;

	@Autowired
	private NotifTemplateMasterRepository masterRepo;
	@Autowired
	private SmsConfigMasterRepository smsRepo;
	
	public void jobProcess(List<NotifTransactionDetails> transDetails) {		
	
		List<List<Object>> collect =null;
		Date d=new Date();		
		if(transDetails.size()>0) {		
			try {
			//List<Tuple> ne = rat.loadNotificationPending();
			transDetails.stream().forEach(tr-> tr.setNotifPushedStatus("Y"));
			//notRepo.saveAll(transDetails);

			Map<String, Map<Integer, Map<String, List<NotifTransactionDetails>>>> groups = transDetails.stream().collect(Collectors.groupingBy(NotifTransactionDetails::getCompanyid,
					Collectors.groupingBy(NotifTransactionDetails::getProductid,
							Collectors.groupingBy(NotifTransactionDetails::getNotifTemplatename))));



			synchronized (transDetails) {

				for (Entry<String, Map<Integer, Map<String, List<NotifTransactionDetails>>>> g : groups.entrySet()){
					Map<Integer, Map<String, List<NotifTransactionDetails>>> h = g.getValue();
					for (Entry<Integer, Map<String, List<NotifTransactionDetails>>> h1 : h.entrySet()) {
						Map<String, List<NotifTransactionDetails>> h2 = h1.getValue();
						for (Entry<String, List<NotifTransactionDetails>> h3 : h2.entrySet()) {

							List<NotifTransactionDetails> n=h3.getValue();
							List<NotifTemplateMaster> templat = masterRepo.findByCompanyIdAndProductIdAndStatusAndNotifTemplatenameIgnoreCaseOrderByAmendIdDesc(n.get(0).getCompanyid(),Long.valueOf(n.get(0).getProductid()),"Y",n.get(0).getNotifTemplatename());
							if(!templat.isEmpty()) {
								
								List<MailMaster> mailc = mailRepo.findByCompanyIdAndBranchCodeAndStatusOrderByAmendIdDesc(n.get(0).getCompanyid(),"99999","Y");													
								List<SmsConfigMaster> smsc = smsRepo.findByCompanyIdAndBranchCodeAndStatusOrderByAmendIdDesc(n.get(0).getCompanyid(),"99999","Y");													

							
								
								PushedStateChange p=new PushedStateChange(templat.get(0),mailc.get(0),smsc.isEmpty()?null:smsc.get(0));
								collect = n.stream().map(p).filter(dd->dd!=null).collect(Collectors.toList());					
								List<Mail> totalMailJob=new ArrayList<Mail>();
								
					
								
								List<Sms> totalSmSJob=new ArrayList<Sms>();
								
								List<Messenger> totalMessnJob=new ArrayList<Messenger>();

								if(!collect.isEmpty()) {
									for (List<Object> list : collect) {
										//totalJob.addAll(list);
										for (Object o:list) {

											if(o instanceof Mail) {
												totalMailJob.add((Mail) o);
											}else if(o instanceof Sms) {
												totalSmSJob.add((Sms) o);
											}else if(o instanceof Messenger) {
												totalMessnJob.add((Messenger) o);
											}

										}
									}
									if(!totalMailJob.isEmpty()) {
										MailJob job=new MailJob(kafkaLink);
										totalMailJob.stream().forEach(job);									
									}
									if(!totalSmSJob.isEmpty()) {
										SmsJob sms=new SmsJob(kafkaLinksms);
										totalSmSJob.stream().forEach(sms);									
									}


								}
							}
							
						}
					}
				}


			}

			transDetails.stream().forEach(tr-> tr.setNotifPushedStatus("C"));
			
			}catch (Exception e) {
				e.printStackTrace();
				transDetails.stream().forEach(tr-> tr.setNotifPushedStatus("E"));
			
			}finally {
				// notifTrans.saveAll(transDetails);
			}
		}

		

	}

}
