package ru.practicum.shareit.exception;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TestController {

    private final TestService testService;

    @GetMapping("/test/not-found")
    public void notFound() {
        throw new NotFoundException("Test not found");
    }

    @GetMapping("/test/not-available")
    public void notAvailable() {
        throw new NotAvailableException("Item not available");
    }

    @GetMapping("/test/duplicate")
    public void duplicate() {
        throw new DuplicatedDataException("Email already exists");
    }

    @GetMapping("/test/conditions")
    public void conditions() {
        throw new ConditionsNotMetException("Booking end must be after start");
    }

    @GetMapping("/test/unknown")
    public void unknown() {
        throw new RuntimeException("Unexpected error");
    }
}