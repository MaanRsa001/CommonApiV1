package com.maan.eway.realpay.service;

import com.maan.eway.bean.NotifTemplateMaster;
import com.maan.eway.realpay.dto.MailRequestDTO;
import com.maan.eway.repository.NotifTemplateMasterRepository;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.maan.eway.jasper.service.JasperService;
import com.maan.eway.jasper.req.JasperDocumentReq;
import com.maan.eway.jasper.res.JasperDocumentRes;

@Service
@RequiredArgsConstructor
public class EmailNotificationService {

    private final NotifTemplateMasterRepository templateRepo;
    private final DynamicMailSenderFactory mailFactory;
    private final JasperService jasperService;

    @Async
    public void processAndSendMail(MailRequestDTO request) throws Exception {
        if (StringUtils.isNotBlank(request.getQuoteNo()) && StringUtils.isNotBlank(request.getDocType())) {
            attachDynamicPdf(request);
        }
        sendMail(request);
    }

    private void attachDynamicPdf(MailRequestDTO request) throws Exception {
        if (request.getDocType() == null) return;
        String docType = request.getDocType().trim().toUpperCase();

        if ("ALL".equals(docType)) {
            // Attempt all 3
            try { processAndAttachSinglePdf(jasperService.creditNote(request.getQuoteNo()), "CREDITNOTE", request); } catch(Exception e) {}
            try { processAndAttachSinglePdf(jasperService.taxInvoice(request.getQuoteNo()), "TAXINVOICE", request); } catch(Exception e) {}
            
            JasperDocumentReq jReq = new JasperDocumentReq();
            jReq.setQuoteNo(request.getQuoteNo());
            jReq.setProductId(request.getProductId() != null ? String.valueOf(request.getProductId()) : null);
            try { processAndAttachSinglePdf(jasperService.policyform(jReq), "POLICYFORM", request); } catch(Exception e) {}
            
        } else if ("CREDITNOTE".equals(docType) || "CREDIT_NOTE".equals(docType)) {
            processAndAttachSinglePdf(jasperService.creditNote(request.getQuoteNo()), "CREDITNOTE", request);
        } else if ("TAXINVOICE".equals(docType) || "TAX_INVOICE".equals(docType)) {
            processAndAttachSinglePdf(jasperService.taxInvoice(request.getQuoteNo()), "TAXINVOICE", request);
        } else if ("POLICYFORM".equals(docType) || "POLICY_FORM".equals(docType)) {
            JasperDocumentReq jReq = new JasperDocumentReq();
            jReq.setQuoteNo(request.getQuoteNo());
            jReq.setProductId(request.getProductId() != null ? String.valueOf(request.getProductId()) : null);
            processAndAttachSinglePdf(jasperService.policyform(jReq), "POLICYFORM", request);
        } else {
            throw new RuntimeException("Unsupported DocType for PDF generation: " + docType);
        }
    }

    private void processAndAttachSinglePdf(JasperDocumentRes pdfRes, String filePrefix, MailRequestDTO request) throws Exception {
        if (pdfRes == null || (StringUtils.isBlank(pdfRes.getPdfoutfilepath()) && StringUtils.isBlank(pdfRes.getPdfoutfile()))) {
            // If PDF is not generated, log warning and skip attachment instead of failing email
            return;
        }

        String filePath = null;
        if (StringUtils.isNotBlank(pdfRes.getPdfoutfile())) {
            // Always prefer decoding Base64 if available, as requested
            String base64 = pdfRes.getPdfoutfile();
            String pureBase64 = base64.contains(",") ? base64.split(",", 2)[1] : base64;
            byte[] pdfBytes = Base64.getDecoder().decode(pureBase64);

            Path tempDir = Path.of(System.getProperty("java.io.tmpdir"), "pdf_mail");
            Files.createDirectories(tempDir);

            String safeName = filePrefix + "_" + request.getQuoteNo().replaceAll("[^a-zA-Z0-9\\-_]", "_") + ".pdf";
            File tempFile = tempDir.resolve(safeName).toFile();

            try (FileOutputStream fos = new FileOutputStream(tempFile)) {
                fos.write(pdfBytes);
            }
            filePath = tempFile.getAbsolutePath();
        } else if (StringUtils.isNotBlank(pdfRes.getPdfoutfilepath())) {
            // Fallback to file path if Base64 is missing
            filePath = pdfRes.getPdfoutfilepath();
        }

        if (StringUtils.isNotBlank(filePath)) {
            if (request.getAttachments() == null) {
                request.setAttachments(new ArrayList<>());
            }
            File attachFile = new File(filePath);
            if(attachFile.exists()) {
                request.getAttachments().add(attachFile);
            }
        }
    }

    @Value("${realpay.email.namibia}")
    private String namibiaEmailCC;

    @Value("${realpay.email.swaziland}")
    private String swazilandEmailCC;

    public void sendMail(MailRequestDTO request) {

        List<NotifTemplateMaster> templateList =
                templateRepo.findByCompanyIdAndProductIdAndStatusAndNotifTemplatenameIgnoreCaseOrderByAmendIdDesc(
                        request.getCompanyId(),
                        request.getProductId(),
                        "Y",
                        request.getTemplateName());

        if (templateList.isEmpty()) {
            throw new RuntimeException("Email template not found");
        }

        NotifTemplateMaster template = templateList.get(0);

        String subject = replacePlaceholders(
                template.getMailSubject(),
                request.getPlaceholders());

        String body = replacePlaceholders(
                template.getMailBody(),
                request.getPlaceholders());

        var mailSender = mailFactory.createMailSender(request.getCompanyId());

        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

            helper.setFrom(mailSender.getUsername());
            helper.setTo(request.getToEmail());

            if (request.getCcEmails() != null && !request.getCcEmails().isEmpty()) {
                helper.setCc(request.getCcEmails().toArray(new String[0]));
            }

            helper.setSubject(subject);
            helper.setText(body + "\n\n" + template.getMailRegards(), true);
            if(request.getCompanyId().equals("100049")||request.getCompanyId().equals("100050")){
                helper.setCc(request.getCompanyId().equals("100049")?swazilandEmailCC:namibiaEmailCC);
            }
            // attachments
            if (request.getAttachments() != null) {
                for (File file : request.getAttachments()) {
                    helper.addAttachment(file.getName(), file);
                }
            }

            mailSender.send(mimeMessage);

        } catch (Exception e) {
        	e.printStackTrace();
            throw new RuntimeException("Error sending mail", e);
        }
    }
    private String replacePlaceholders(String text, Map<String, String> map) {
        if (text == null || map == null) return text;

        for (Map.Entry<String, String> e : map.entrySet()) {
            text = text.replace(e.getKey(),
                    e.getValue() == null ? "" : e.getValue());
        }
        return text;
    }


}

