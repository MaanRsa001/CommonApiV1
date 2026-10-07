package com.maan.eway.realpay.repository;

import com.maan.eway.realpay.dto.InstallmentStatus;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface RealpayInstallmentReminderRepository
        extends JpaRepository<RealpayInstallmentReminder, Long> {

    List<RealpayInstallmentReminder>
    findByStatusAndNextNotificationDateLessThanEqual(
            InstallmentStatus status,
            LocalDateTime date);

    Optional<RealpayInstallmentReminder>
    findByInstalmentReferenceNumber(String installmentReferenceNumber);

}