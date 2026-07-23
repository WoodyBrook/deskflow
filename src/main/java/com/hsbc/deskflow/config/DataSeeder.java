package com.hsbc.deskflow.config;

import com.hsbc.deskflow.Admin;
import com.hsbc.deskflow.domain.Booking;
import com.hsbc.deskflow.domain.Desk;
import com.hsbc.deskflow.domain.Employee;
import com.hsbc.deskflow.repository.BookingRepository;
import com.hsbc.deskflow.repository.DeskRepository;
import com.hsbc.deskflow.repository.EmployeeRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {

    private final EmployeeRepository employeeRepository;
    private final DeskRepository deskRepository;
    private final BookingRepository bookingRepository;

    @PersistenceContext
    private EntityManager entityManager;

    public DataSeeder(
            EmployeeRepository employeeRepository,
            DeskRepository deskRepository,
            BookingRepository bookingRepository
    ) {
        this.employeeRepository = employeeRepository;
        this.deskRepository = deskRepository;
        this.bookingRepository = bookingRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (deskRepository.count() == 0) {
            seedCoreData();
        }
        ensureAdmin();
        backfillBookingStatus();
    }

    private void backfillBookingStatus() {
        entityManager.createNativeQuery(
                "UPDATE booking SET status = 'BOOKED' WHERE status IS NULL OR status = ''"
        ).executeUpdate();
    }

    private void seedCoreData() {
        // IDs: Joey=1, Ethan=2, Alfie=3, Julian=4, Justin=5
        Employee joey = employeeRepository.save(new Employee("Joey"));
        Employee ethan = employeeRepository.save(new Employee("Ethan"));
        Employee alfie = employeeRepository.save(new Employee("Alfie"));
        Employee julian = employeeRepository.save(new Employee("Julian"));
        Employee justin = employeeRepository.save(new Employee("Justin"));

        List<Desk> desks = deskRepository.saveAll(List.of(
                new Desk("HEL-2F-01", 2, true, true),
                new Desk("HEL-2F-02", 2, true, true),
                new Desk("HEL-2F-03", 2, false, true),
                new Desk("HEL-2F-04", 2, false, true),
                new Desk("HEL-3F-01", 3, true, true),
                new Desk("HEL-3F-02", 3, true, true),
                new Desk("HEL-3F-03", 3, false, true),
                new Desk("HEL-3F-04", 3, true, false),
                new Desk("HEL-3F-05", 3, false, true)
        ));

        LocalDate today = LocalDate.now();
        bookingRepository.save(new Booking(desks.get(0), joey, today));
        bookingRepository.save(new Booking(desks.get(4), ethan, today.plusDays(1)));
        bookingRepository.save(new Booking(desks.get(1), alfie, today.plusDays(2)));
        bookingRepository.save(new Booking(desks.get(5), julian, today.plusDays(3)));
        bookingRepository.save(new Booking(desks.get(2), justin, today.plusDays(4)));
    }

    private void ensureAdmin() {
        if (employeeRepository.existsById(Admin.ID)) {
            return;
        }
        entityManager.createNativeQuery("INSERT INTO employee (id, name) VALUES (?, ?)")
                .setParameter(1, Admin.ID)
                .setParameter(2, Admin.NAME)
                .executeUpdate();
        entityManager.createNativeQuery("ALTER TABLE employee AUTO_INCREMENT = 100")
                .executeUpdate();
    }
}
