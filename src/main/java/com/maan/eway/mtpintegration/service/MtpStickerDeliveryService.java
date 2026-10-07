package com.maan.eway.mtpintegration.service;


import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.maan.eway.mtpintegration.entity.MtpStickerDelivery;
import com.maan.eway.mtpintegration.repository.MtpStickerDeliveryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MtpStickerDeliveryService {

    private final MtpStickerDeliveryRepository repository;

    public void saveDownloadAttempt(String stickerReference, String vehicleNumber, String status) {
        try {
            MtpStickerDelivery delivery = MtpStickerDelivery.builder()
                    .numberPlate(vehicleNumber)
                    .stickerId(stickerReference)
                    .deliveryStatus(status)
                    .createdAt(LocalDateTime.now())
                    .build();
            repository.save(delivery);
        } catch (Exception ignored) {
            // Sticker tracking failure should not stop file download response.
        }
    }
}
