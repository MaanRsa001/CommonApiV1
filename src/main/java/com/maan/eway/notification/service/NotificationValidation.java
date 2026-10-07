package com.maan.eway.notification.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.maan.eway.error.Error;
import com.maan.eway.notification.req.Broker;
import com.maan.eway.notification.req.Customer;
import com.maan.eway.notification.req.Notification;
import com.maan.eway.notification.req.UnderWriter;
@Service
public class NotificationValidation {

	public List<Error> pushValidation(Notification n) {
		Broker broker = n.getBroker();
		Customer customer = n.getCustomer();
		List<UnderWriter> underwriters = n.getUnderwriters();
		List<Error>  errors = new ArrayList<Error>();
		if(broker!=null) {   
			if(StringUtils.isBlank(broker.getBrokerCompanyName())) {
				errors.add(new Error("01","BrokerCompanyName" , "Please Select BrokerCompanyName" ));
			}

			if(StringUtils.isBlank(broker.getBrokerMailId())) {
				errors.add(new Error("02","BrokerMailId" , "Please Select BrokerMailId" ));
			}

			if(StringUtils.isBlank(broker.getBrokerName())) {
				errors.add(new Error("03","BrokerName" , "Please Select BrokerName" ));
			}

			if(broker.getBrokerMessengerCode()==null) {
				errors.add(new Error("04","BrokerMessengerCode" , "Please Select BrokerMessengerCode" ));
			}

			if(broker.getBrokerMessengerPhone()==null) {
				errors.add(new Error("04","BrokerMessengerPhone" , "Please Select BrokerMessengerPhone" ));
			}

			if(broker.getBrokerPhoneCode()==null) {
				errors.add(new Error("04","BrokerPhoneCode" , "Please Select BrokerPhoneCode" ));
			}

			if(broker.getBrokerPhoneNo()==null) {
				errors.add(new Error("04","BrokerPhoneNo" , "Please Select BrokerPhoneNo" ));
			}
		}
		
		/*if(StringUtils.isBlank(customer.getCustomerMailid())) {
			errors.add(new Error("04","CustomerMailid" , "Please Select CustomerMailid" ));
		}
		*/
		if(StringUtils.isBlank(customer.getCustomerName())) {
			errors.add(new Error("04","CustomerName" , "Please Select CustomerName" ));
		}
		
		if(customer.getCustomerMessengerCode()==null) {
			errors.add(new Error("04","CustomerMessengerCode" , "Please Select CustomerMessengerCode" ));
		}
		if(customer.getCustomerMessengerPhone()==null) {
			errors.add(new Error("04","CustomerMessengerPhone" , "Please Select CustomerMessengerPhone" ));
		}
		
		if(customer.getCustomerPhoneCode()==null) {
			errors.add(new Error("04","CustomerPhoneCode" , "Please Select CustomerPhoneCode" ));
		}
		
		if(customer.getCustomerPhoneNo()==null) {
			errors.add(new Error("04","CustomerPhoneNo" , "Please Select CustomerPhoneNo" ));
		}
		if(underwriters!=null) {
		for(UnderWriter underwriter: underwriters) {
			if(StringUtils.isBlank(underwriter.getUwMailid())) {
				errors.add(new Error("04","UwMailid" , "Please Select UwMailid" ));
			}

			if(StringUtils.isBlank(underwriter.getUwName())) {
				errors.add(new Error("04","UwName" , "Please Select UwName" ));
			}

			if(underwriter.getUwMessengerCode()==null) {
				errors.add(new Error("04","UwMessengerCode" , "Please Select UwMessengerCode" ));
			}
			if(underwriter.getUwMessengerPhone()==null) {
				errors.add(new Error("04","UwMessengerPhone" , "Please Select UwMessengerPhone" ));
			}
			if(underwriter.getUwPhonecode()==null) {
				errors.add(new Error("04","UwPhonecode" , "Please Select UwPhonecode" ));
			}
			if(underwriter.getUwPhoneNo()==null) {
				errors.add(new Error("04","UwPhoneNo" , "Please Select UwPhoneNo" ));
			} 
		}}
		if(n.getCompanyid()==null) {
			errors.add(new Error("04","CompanyId" , "Please Select CompanyId" ));
		}
		if(n.getProductid()==null) {
			errors.add(new Error("04","Productid" , "Please Select ProductId" ));
		}
		return errors;
		
	}
}
