package com.maan.eway.mtpintegration.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "MTP_API_AUDIT")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MtpApiAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "API_NAME", length = 100)
    private String apiName;

    @Column(name = "QUOTE_NO", length = 50)
    private String quoteNo;

    @Lob
    @Column(name = "REQUEST_DATA")
    private String requestData;

    @Lob
    @Column(name = "RESPONSE_DATA")
    private String responseData;

    @Column(name = "STATUS", length = 20)
    private String status;

    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt;
}