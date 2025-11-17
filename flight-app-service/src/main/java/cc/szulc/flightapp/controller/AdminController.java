package cc.szulc.flightapp.controller;

import cc.szulc.flightapp.dto.UserDto;
import cc.szulc.flightapp.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
    public List<UserDto> getAllUsers() {
        return authService.getAllUsers();
    }
}