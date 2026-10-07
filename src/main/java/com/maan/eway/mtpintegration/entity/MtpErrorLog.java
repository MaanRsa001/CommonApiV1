package com.maan.eway.mtpintegration.entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "MTP_ERROR_LOG")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MtpErrorLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "API_NAME", length = 100)
    private String apiName;

    @Lob
    @Column(name = "ERROR_MESSAGE")
    private String errorMessage;

    @Lob
    @Column(name = "STACK_TRACE")
    private String stackTrace;

    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt;
}