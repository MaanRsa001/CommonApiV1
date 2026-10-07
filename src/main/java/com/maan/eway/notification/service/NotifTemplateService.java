package com.maan.eway.notification.service;

import java.util.List;

import com.maan.eway.common.res.CommonRes;
import com.maan.eway.error.Error;
import com.maan.eway.notification.req.DirectMailSentReq;
import com.maan.eway.notification.req.DirectSmsSentReq;
import com.maan.eway.notification.req.NotifGetByIdReq;
import com.maan.eway.notification.req.NotifGetByQuoteNoReq;
import com.maan.eway.notification.req.NotifGetReq;
import com.maan.eway.notification.req.NotifTemplateGetReq;
import com.maan.eway.notification.req.TemplatesDropDownReq;
import com.maan.eway.notification.res.MailNotifGetRes;
import com.maan.eway.notification.res.NofiByQuoteNoRes;
import com.maan.eway.notification.res.SmsNofiGetRes;
import com.maan.eway.res.DropDownRes;

public interface NotifTemplateService {

	List<DropDownRes> getTemplatesDropDown(TemplatesDropDownReq req);

	CommonRes getMailTemplate(NotifTemplateGetReq req);

	CommonRes getSmsTemplate(NotifTemplateGetReq req);

	CommonRes sentDirectMail(DirectMailSentReq req);

	CommonRes sentDirectSms(DirectSmsSentReq req);

	List<MailNotifGetRes> getSentMailList(NotifGetReq req);

	List<SmsNofiGetRes> getSmsSentList(NotifGetReq req);

	MailNotifGetRes viewSentMail(NotifGetByIdReq req);

	SmsNofiGetRes viewSmsSent(NotifGetByIdReq req);

	List<NofiByQuoteNoRes> viewNotificationSentToQuoteNo(NotifGetByQuoteNoReq req);

	List<DropDownRes> getActiveTemplatesDropDown(TemplatesDropDownReq req);

	List<Error> validateQuotoNo(NotifGetByQuoteNoReq req);

}
