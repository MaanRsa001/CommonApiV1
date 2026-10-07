package com.maan.eway.thread;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.maan.eway.renewal.service.RenewalService;

@Component
public class ScheduledTasks {

	@Autowired
	private RenewalService service;
	@Autowired
	private RenewSchedular renewSchedular;
	@Autowired
	private NotificationSchedular notificationSchedular;
	@Autowired
	private EmiNotificationSchedular eminotificationSchedular;
	
	@Autowired
	private ValuationSchedular valuationSchedular;

	private static final Logger log = LoggerFactory.getLogger(ScheduledTasks.class);
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

	// Schedular runs everyday at 4 AM//
	// @Scheduled(fixedRate=86400000)
	// @Scheduled(cron = "0 0 4 * * ?")
	//@Scheduled(cron = "${cron.renew}")
	//@EventListener(ApplicationReadyEvent.class)
	public void startRenewSchedular() {
		log.info("The time is now {startRenewSchedular}", dateFormat.format(new Date()));

		if (service.getDbStatus()) {
			renewSchedular.schedule();
		} else {
			System.out.println(
					"|************|  RENEWAL QUOTE TO POLICY API THREAD is Switched OFF from DB  |*************|");
		}
	}
	//@EventListener(ApplicationReadyEvent.class)
	public void startPremiaRenewSchedular() {
		log.info("The time is now {startPremiaRenewSchedular}", dateFormat.format(new Date()));

		if (service.getDbStatus()) {
			renewSchedular.startPremiaRenewSchedular();
		} else {
			System.out.println(
					"|************|  RENEWAL QUOTE TO POLICY API THREAD is Switched OFF from DB  |*************|");
		}
	}
	
	// @Scheduled(fixedRate=86400000)
	// @Scheduled(cron = "0 0 8 * * ?")
	//@Scheduled(cron = "0 30 1 * * ?")
	//@EventListener(ApplicationReadyEvent.class)
	public void startNotificationSMSSchedular() {
		log.info("The time is now {startNotificationSMSSchedular}", dateFormat.format(new Date()));

		if (service.getDbStatus()) {
			notificationSchedular.schedule();
		} else {
			System.out.println(
					"|************|  RENEWAL QUOTE TO POLICY API THREAD is Switched OFF from DB  |*************|");
		}
	}


	// @Scheduled(fixedRate=86400000)
	// @Scheduled(cron = "0 0 6 * * ?")
	//
	//@EventListener(ApplicationReadyEvent.class)
	public void startExpiredPolicyUpdateData() {
		log.info("The time is now {startExpiredPolicyUpdateData}", dateFormat.format(new Date()));

		if (service.getDbStatus()) {
			service.startExpiredPolicyUpdateData();
		} else {
			System.out.println(
					"|************|  RENEWAL QUOTE TO POLICY API THREAD is Switched OFF from DB  |*************|");
		}
	}
	//@EventListener(ApplicationReadyEvent.class)
	public void startEmiNotificationSMSSchedular() {
		log.info("The time is now {startEmiNotificationSMSSchedular}", dateFormat.format(new Date()));

		if (service.getDbStatus()) {
			eminotificationSchedular.Emischedule();
		} else {
			System.out.println(
					"|************|  RENEWAL QUOTE TO POLICY API THREAD is Switched OFF from DB  |*************|");
		}
	}
	@Scheduled(cron = "0 0 20 * * ?")
	public void startValuationSchedular() {
		log.info("The time is now {startValuationSchedular}", dateFormat.format(new Date()));

		if (service.getDbStatus()) {
			valuationSchedular.Valuationschedule();
		} else {
			System.out.println(
					"|************| ValuationSchedular API THREAD is Switched OFF from DB  |*************|");
		}
	}

}