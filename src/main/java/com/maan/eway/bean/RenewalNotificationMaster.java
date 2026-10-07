package com.maan.eway.bean;

import java.util.Date;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@DynamicInsert
@DynamicUpdate
@Builder
@Table(name = "RENEWAL_NOTIFICATION_MASTER")
public class RenewalNotificationMaster {

	@Id
	@Column(name="SNO", length=20)
	private Long sno;
	
	@Column(name="TRAN_ID", length=20)
	private String tranId;
	
	@Column(name="NOTIFICATION_ID", length=20)
	private Long notificationId;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="POLICY_EXPIRY_DATE")
	private Date policyexpiryDate;
	
	@Column(name="SMS_COUNT", length=20)
	private String smsCount;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="SMS_SENT_DATE")
	private Date smssentdate;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="NEXT_SMS_DATE")
	private Date nextsmsdate;
	
	
}
