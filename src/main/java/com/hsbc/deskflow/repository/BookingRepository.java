package com.hsbc.deskflow.repository;

import com.hsbc.deskflow.domain.Booking;
import com.hsbc.deskflow.domain.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByDateAndStatusOrderByIdAsc(LocalDate date, BookingStatus status);

    List<Booking> findByEmployeeIdOrderByDateAscIdAsc(Long employeeId);

    List<Booking> findByEmployeeIdAndStatusOrderByDateAscIdAsc(Long employeeId, BookingStatus status);

    List<Booking> findAllByOrderByDateAscIdAsc();

    Optional<Booking> findByDeskIdAndDateAndStatus(Long deskId, LocalDate date, BookingStatus status);

    Optional<Booking> findByEmployeeIdAndDateAndStatus(Long employeeId, LocalDate date, BookingStatus status);

    List<Booking> findByDateAndStatus(LocalDate date, BookingStatus status);
}
