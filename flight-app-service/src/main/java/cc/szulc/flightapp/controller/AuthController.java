package cc.szulc.flightapp.controller;

import cc.szulc.flightapp.dto.AuthRequestDto;
import cc.szulc.flightapp.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public void registerUser(@RequestBody AuthRequestDto authRequest) {
        authService.register(authRequest);
    }
}