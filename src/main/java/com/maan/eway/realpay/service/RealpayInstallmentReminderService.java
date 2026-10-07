package com.maan.eway.realpay.service;


import com.maan.eway.realpay.dto.InstallmentStatus;

import com.maan.eway.realpay.dto.MailRequestDTO;
import com.maan.eway.realpay.repository.RealpayInstallmentReminder;
import com.maan.eway.realpay.repository.RealpayInstallmentReminderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class RealpayInstallmentReminderService {

    private final RealpayInstallmentReminderRepository repository;
    private final EmailNotificationService emailNotificationService;

    // Create entry
    public RealpayInstallmentReminder createReminder(
            String customerId, String policyNumber, String quoteNumber, String installmentMonth,
            BigDecimal installmentAmount, LocalDateTime dueDate, String customerName,
            String brokerId, String brokerName, String customerEmail, String brokerEmail, String companyId, String instalmentReferenceNumber, String templateName) {

        RealpayInstallmentReminder reminder =
                RealpayInstallmentReminder.builder()
                        .customerId(customerId).customerName(customerName).customerEmail(customerEmail)
                        .brokerId(brokerId).brokerName(brokerName).brokerEmail(brokerEmail)
                        .policyNumber(policyNumber).quoteNumber(quoteNumber).installmentMonth(installmentMonth)
                        .installmentAmount(installmentAmount).dueDate(LocalDate.from(dueDate)).nextNotificationDate(dueDate)
                        .reminderCount(1).status(InstallmentStatus.PENDING).companyId(companyId).instalmentReferenceNumber(instalmentReferenceNumber)
                        .templateName(templateName)
                        .build();

        return repository.save(reminder);
    }

    @Scheduled(cron = "0 * * * * *")
    public void processPendingReminders() {

        List<RealpayInstallmentReminder> reminders =
                repository.findByStatusAndNextNotificationDateLessThanEqual(
                        InstallmentStatus.PENDING,
                        LocalDateTime.now());

        for (RealpayInstallmentReminder reminder : reminders) {

            if(reminder.getReminderCount()<3){
                sendNotification(reminder);
                if(!reminder.getTemplateName().equals("PHOENIX_PREMIUM_INSTALLMENT_PAYMENT_SUCCESS")){
                    reminder.setNextNotificationDate(
                            LocalDateTime.now().plusDays(15));

                    reminder.setTemplateName(reminder.getReminderCount() == 1 ? "RealPay EFT 1st Notification"
                                :"RealPay EFT 2d Notification");

                    if(!reminder.getTemplateName().equals("PHOENIX_PREMIUM_INSTALLMENT_PAYMENT_FAILED")){
                        reminder.setReminderCount(
                                reminder.getReminderCount() + 1);
                    }
                }else{
                    reminder.setStatus(InstallmentStatus.PAID);
                }
            }else{
                reminder.setStatus(InstallmentStatus.EXCEEDED_THREE_ATTEMPTS);
            }
            repository.save(reminder);
        }
    }

    // mark paid
    public void markAsPaid(String installmentReferenceNumber) {
        try {
            RealpayInstallmentReminder reminder =
                    repository.findByInstalmentReferenceNumber(installmentReferenceNumber).orElseThrow();

            reminder.setStatus(InstallmentStatus.PAID);
            repository.save(reminder);
        }catch (Exception e){
            e.printStackTrace();
            log.error("Error in marking paid in real pay installment remainder collection for {}",installmentReferenceNumber);
        }

    }

    private void sendNotification(RealpayInstallmentReminder r) {

        System.out.println(
                "Email Remainder -> Policy: " + r.getPolicyNumber()
                        + " | Quote: " + r.getQuoteNumber()
                        + " | Month: " + r.getInstallmentMonth()
                        + " | Amount: " + r.getInstallmentAmount()
                        + " | Due: " + r.getDueDate()
        );
        MailRequestDTO mailRequestDTO = MailRequestDTO.builder()
                .templateName(r.getTemplateName())
                .companyId(r.getCompanyId())
                .productId(99999L)
                .toEmail(r.getBrokerEmail())
                .ccEmails(List.of(r.getCustomerEmail()))
                .placeholders(Map.of(
                        "{CUSTOMER_NAME}", r.getCustomerName(),
                        "{BROKER_NAME}", r.getCustomerName(),
                        "{POLICY_NUMBER}", r.getPolicyNumber(),
                        "{AMOUNT}", r.getInstallmentAmount().toString(),
                        "{DUE_DATE}", r.getDueDate().toString()
                ))
                .build();
        emailNotificationService.sendMail(mailRequestDTO);
    }
}
