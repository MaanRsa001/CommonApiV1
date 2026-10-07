package com.maan.eway.mtpintegration.entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "MTP_STICKER_DELIVERY")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MtpStickerDelivery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "NUMBER_PLATE", length = 50)
    private String numberPlate;

    @Column(name = "STICKER_ID", length = 100)
    private String stickerId;

    @Column(name = "DELIVERY_STATUS", length = 50)
    private String deliveryStatus;

    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt;
}