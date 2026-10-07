package com.maan.eway.realpay.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.maan.eway.realpay.dto.MailRequestDTO;
import com.maan.eway.realpay.service.EmailNotificationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class EmailNotificationController {

    private final EmailNotificationService emailNotificationService;

    @PostMapping("/sendmail")
    public ResponseEntity<String> sendMailEndpoint(@RequestBody MailRequestDTO request) {
        try {
            emailNotificationService.processAndSendMail(request);
            return ResponseEntity.ok("Mail sent successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to send mail: " + e.getMessage());
        }
    }
}
