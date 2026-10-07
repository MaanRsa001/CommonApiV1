package com.maan.eway.mtpintegration.service;


import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.maan.eway.mtpintegration.entity.MtpErrorLog;
import com.maan.eway.mtpintegration.repository.MtpErrorLogRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MtpErrorLogService {

    private final MtpErrorLogRepository errorLogRepository;

    public void save(String apiName, Exception exception) {
        try {
            MtpErrorLog errorLog = MtpErrorLog.builder()
                    .apiName(apiName)
                    .errorMessage(exception.getMessage())
                    .stackTrace(toStackTrace(exception))
                    .createdAt(LocalDateTime.now())
                    .build();
            errorLogRepository.save(errorLog);
        } catch (Exception ignored) {
            // Error log failure should not hide the original exception.
        }
    }

    private String toStackTrace(Exception exception) {
        StringWriter sw = new StringWriter();
        exception.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }
}
