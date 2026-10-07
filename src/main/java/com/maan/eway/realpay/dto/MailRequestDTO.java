package com.maan.eway.realpay.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.File;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MailRequestDTO {

    private String templateName;
    private String companyId;
    private Long productId;

    private String toEmail;
    private List<String> ccEmails;

    // For dynamic PDF attachment generation
    private String quoteNo;
    private String docType;

    // dynamic placeholders
    private Map<String, String> placeholders;

    // attachments
    private List<File> attachments;
}