package com.hsbc.deskflow.service;

import com.hsbc.deskflow.Admin;
import com.hsbc.deskflow.domain.Booking;
import com.hsbc.deskflow.domain.BookingStatus;
import com.hsbc.deskflow.domain.Desk;
import com.hsbc.deskflow.domain.Employee;
import com.hsbc.deskflow.dto.BookingResponse;
import com.hsbc.deskflow.dto.CreateBookingRequest;
import com.hsbc.deskflow.dto.DeskResponse;
import com.hsbc.deskflow.exception.BadRequestException;
import com.hsbc.deskflow.exception.ConflictException;
import com.hsbc.deskflow.exception.ForbiddenException;
import com.hsbc.deskflow.exception.NotFoundException;
import com.hsbc.deskflow.repository.BookingRepository;
import com.hsbc.deskflow.repository.DeskRepository;
import com.hsbc.deskflow.repository.EmployeeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class BookingService {

    private static final int MAX_DAYS_AHEAD = 7;

    private final BookingRepository bookingRepository;
    private final DeskRepository deskRepository;
    private final EmployeeRepository employeeRepository;

    public BookingService(
            BookingRepository bookingRepository,
            DeskRepository deskRepository,
            EmployeeRepository employeeRepository
    ) {
        this.bookingRepository = bookingRepository;
        this.deskRepository = deskRepository;
        this.employeeRepository = employeeRepository;
    }

    @Transactional
    public BookingResponse create(CreateBookingRequest request) {
        if (request == null) {
            throw new BadRequestException("Request body is required");
        }
        if (request.deskId() == null) {
            throw new BadRequestException("deskId is required");
        }
        if (request.date() == null) {
            throw new BadRequestException("date is required");
        }

        validateDateWindow(request.date());

        Desk desk = deskRepository.findById(request.deskId())
                .orElseThrow(() -> new NotFoundException("Desk not found: " + request.deskId()));
        if (!desk.isActive()) {
            throw new BadRequestException("Desk " + desk.getCode() + " is inactive and cannot be booked");
        }

        Employee employee = resolveEmployee(request);

        bookingRepository.findByDeskIdAndDateAndStatus(desk.getId(), request.date(), BookingStatus.BOOKED)
                .ifPresent(existing -> {
                    throw new ConflictException(
                            "Desk " + desk.getCode() + " is already booked on " + request.date());
                });

        bookingRepository.findByEmployeeIdAndDateAndStatus(employee.getId(), request.date(), BookingStatus.BOOKED)
                .ifPresent(existing -> {
                    throw new ConflictException(
                            "Employee " + employee.getName() + " already has a booking on " + request.date());
                });

        Booking booking = bookingRepository.save(new Booking(desk, employee, request.date()));
        return toResponse(booking);
    }

    @Transactional(readOnly = true)
    public List<BookingResponse> list(LocalDate date, Long employeeId) {
        if (date == null && employeeId == null) {
            throw new BadRequestException("Provide either date or employeeId query parameter");
        }
        if (employeeId != null) {
            if (!employeeRepository.existsById(employeeId)) {
                throw new NotFoundException("Employee not found: " + employeeId);
            }
            if (Admin.isAdmin(employeeId)) {
                return bookingRepository.findAllByOrderByDateAscIdAsc().stream()
                        .map(this::toResponse)
                        .toList();
            }
            // Employees only see active bookings; cancelled history is admin/audit only.
            return bookingRepository.findByEmployeeIdAndStatusOrderByDateAscIdAsc(
                            employeeId, BookingStatus.BOOKED).stream()
                    .map(this::toResponse)
                    .toList();
        }
        // Day view: only active bookings
        return bookingRepository.findByDateAndStatusOrderByIdAsc(date, BookingStatus.BOOKED).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public void cancel(Long id, Long employeeId) {
        if (employeeId == null) {
            throw new BadRequestException("employeeId is required to cancel a booking");
        }
        Employee actor = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new NotFoundException("Employee not found: " + employeeId));

        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Booking not found: " + id));

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new BadRequestException("Booking is already cancelled");
        }

        boolean owner = booking.getEmployee().getId().equals(employeeId);
        if (!owner && !Admin.isAdmin(employeeId)) {
            throw new ForbiddenException("You can only cancel your own bookings");
        }

        booking.cancel(actor);
        bookingRepository.save(booking);
    }

    @Transactional(readOnly = true)
    public List<DeskResponse> listAvailable(LocalDate date, Integer floor) {
        if (date == null) {
            throw new BadRequestException("date is required");
        }

        Set<Long> bookedDeskIds = new HashSet<>();
        for (Booking booking : bookingRepository.findByDateAndStatus(date, BookingStatus.BOOKED)) {
            bookedDeskIds.add(booking.getDesk().getId());
        }

        return deskRepository.findByActiveTrue().stream()
                .filter(desk -> !bookedDeskIds.contains(desk.getId()))
                .filter(desk -> floor == null || floor.equals(desk.getFloor()))
                .map(desk -> new DeskResponse(
                        desk.getId(),
                        desk.getCode(),
                        desk.getFloor(),
                        desk.isHasMonitor(),
                        desk.isActive()
                ))
                .toList();
    }

    private Employee resolveEmployee(CreateBookingRequest request) {
        if (request.employeeId() != null) {
            return employeeRepository.findById(request.employeeId())
                    .orElseThrow(() -> new NotFoundException("Employee not found: " + request.employeeId()));
        }

        String name = request.employeeName() == null ? "" : request.employeeName().trim();
        if (name.isEmpty()) {
            throw new BadRequestException("employeeName is blank");
        }

        return employeeRepository.findByNameIgnoreCase(name)
                .orElseGet(() -> employeeRepository.save(new Employee(name)));
    }

    private void validateDateWindow(LocalDate date) {
        LocalDate today = LocalDate.now();
        LocalDate max = today.plusDays(MAX_DAYS_AHEAD);
        if (date.isBefore(today) || date.isAfter(max)) {
            throw new BadRequestException(
                    "date must be between " + today + " and " + max + " (inclusive)");
        }
    }

    private BookingResponse toResponse(Booking booking) {
        Employee cancelledBy = booking.getCancelledBy();
        return new BookingResponse(
                booking.getId(),
                booking.getDesk().getId(),
                booking.getDesk().getCode(),
                booking.getEmployee().getId(),
                booking.getEmployee().getName(),
                booking.getDate(),
                booking.getStatus(),
                booking.getCreatedAt(),
                booking.getCancelledAt(),
                cancelledBy == null ? null : cancelledBy.getId(),
                cancelledBy == null ? null : cancelledBy.getName()
        );
    }
}
