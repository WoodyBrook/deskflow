package com.hsbc.deskflow.dto;

public record DeskResponse(
        Long id,
        String code,
        Integer floor,
        boolean hasMonitor,
        boolean active
) {
}
