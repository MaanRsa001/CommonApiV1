package com.maan.eway.realpay.service;

import com.maan.eway.bean.MailMaster;
import com.maan.eway.repository.MailMasterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Properties;
import lombok.extern.slf4j.Slf4j;

import static org.reflections.Reflections.log;

@Service
@RequiredArgsConstructor
public class DynamicMailSenderFactory {

    private final MailMasterRepository mailRepo;

    public JavaMailSenderImpl createMailSender(String companyId) {

        List<MailMaster> mailc = mailRepo.findByCompanyIdAndBranchCodeAndStatusOrderByAmendIdDesc(companyId,"99999","Y");
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        if(!mailc.isEmpty()){
            MailMaster config = mailc.get(0);
            mailSender.setHost(config.getSmtpHost());
            mailSender.setPort(config.getSmtpPort().intValue());
            mailSender.setUsername(config.getSmtpUser());
            mailSender.setPassword(config.getSmtpPwd());

            Properties props = mailSender.getJavaMailProperties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");

        }else{
            log.warn("Mail Master is empty for {} ,{} ,{}",companyId,"99999","Y");
        }

        return mailSender;
    }
}

