package cc.szulc.flightapp.controller;

import cc.szulc.flightapp.dto.UserDto;
import cc.szulc.flightapp.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AuthService authService;

    @GetMapping("/test")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminOnlyTest() {
        return "Hello, Admin! This endpoint is only for you.";
    }

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public Page<UserDto> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return authService.getAllUsers(pageable);
    }

    @PatchMapping("/users/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> changeUserStatus(
            @PathVariable Long id,
            @RequestParam boolean enabled
    ) {
        authService.changeUserStatus(id, enabled);
        return ResponseEntity.noContent().build();
    }
}