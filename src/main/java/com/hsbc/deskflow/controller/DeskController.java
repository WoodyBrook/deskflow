package com.hsbc.deskflow.controller;

import com.hsbc.deskflow.dto.DeskResponse;
import com.hsbc.deskflow.service.BookingService;
import com.hsbc.deskflow.service.DeskService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/desks")
public class DeskController {

    private final DeskService deskService;
    private final BookingService bookingService;

    public DeskController(DeskService deskService, BookingService bookingService) {
        this.deskService = deskService;
        this.bookingService = bookingService;
    }

    @GetMapping
    public List<DeskResponse> listDesks(
            @RequestParam(required = false) Integer floor,
            @RequestParam(required = false) Boolean hasMonitor
    ) {
        return deskService.listDesks(floor, hasMonitor);
    }

    @GetMapping("/available")
    public List<DeskResponse> listAvailable(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) Integer floor
    ) {
        return bookingService.listAvailable(date, floor);
    }
}
