package com.hsbc.deskflow.dto;

import com.hsbc.deskflow.domain.BookingStatus;

import java.time.Instant;
import java.time.LocalDate;

public record BookingResponse(
        Long id,
        Long deskId,
        String deskCode,
        Long employeeId,
        String employeeName,
        LocalDate date,
        BookingStatus status,
        Instant createdAt,
        Instant cancelledAt,
        Long cancelledByEmployeeId,
        String cancelledByEmployeeName
) {
}
