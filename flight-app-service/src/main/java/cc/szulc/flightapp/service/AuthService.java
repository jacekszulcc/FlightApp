package cc.szulc.flightapp.service;

import cc.szulc.flightapp.dto.AuthRequestDto;
import cc.szulc.flightapp.entity.User;
import cc.szulc.flightapp.exception.UserAlreadyExistsException;
import cc.szulc.flightapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void register(AuthRequestDto authRequest) {
        if (userRepository.findByUsername(authRequest.getUsername()).isPresent()) {
            throw new UserAlreadyExistsException("User with username '" + authRequest.getUsername() + "' already exists.");
        }

        User newUser = new User();
        newUser.setUsername(authRequest.getUsername());
        newUser.setPassword(passwordEncoder.encode(authRequest.getPassword()));

        userRepository.save(newUser);
    }
}