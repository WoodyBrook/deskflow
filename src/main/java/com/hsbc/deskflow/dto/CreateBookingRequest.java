package com.hsbc.deskflow.dto;

import java.time.LocalDate;

public record CreateBookingRequest(
        Long deskId,
        Long employeeId,
        String employeeName,
        LocalDate date
) {
}
