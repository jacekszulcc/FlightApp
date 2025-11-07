package cc.szulc.flightapp.controller;

import cc.szulc.flightapp.dto.ChangePasswordRequestDto;
import cc.szulc.flightapp.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final AuthService authService;

    @PostMapping("/change-password")
    public ResponseEntity<Void> changePassword(
            @Valid @RequestBody ChangePasswordRequestDto request,
            Authentication authentication
    ) {
        String username = authentication.getName();

        authService.changePassword(request, username);

        return ResponseEntity.ok().build();
    }
}