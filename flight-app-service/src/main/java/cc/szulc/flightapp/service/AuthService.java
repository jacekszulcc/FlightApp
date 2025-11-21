package cc.szulc.flightapp.service;

import cc.szulc.flightapp.dto.AuthRequestDto;
import cc.szulc.flightapp.dto.AuthResponseDto;
import cc.szulc.flightapp.dto.ChangePasswordRequestDto;
import cc.szulc.flightapp.dto.UserDto;
import cc.szulc.flightapp.entity.Role;
import cc.szulc.flightapp.entity.User;
import cc.szulc.flightapp.exception.UserAlreadyExistsException;
import cc.szulc.flightapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict; // Import
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public void register(AuthRequestDto authRequest) {
        if (userRepository.findByUsername(authRequest.getUsername()).isPresent()) {
            throw new UserAlreadyExistsException("User with username '" + authRequest.getUsername() + "' already exists.");
        }
        User newUser = new User();
        newUser.setUsername(authRequest.getUsername());
        newUser.setPassword(passwordEncoder.encode(authRequest.getPassword()));
        newUser.setRole(Role.ROLE_USER);
        userRepository.save(newUser);
    }

    public AuthResponseDto login(AuthRequestDto authRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
        );
        User user = userRepository.findByUsername(authRequest.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Invalid username or password"));
        return new AuthResponseDto(jwtService.generateToken(user));
    }

    @CacheEvict(value = "users", key = "#username")
    public void changePassword(ChangePasswordRequestDto request, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("Authenticated user not found in database"));

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid old password");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    public Page<UserDto> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(this::mapToUserDto);
    }

    private UserDto mapToUserDto(User user) {
        return new UserDto(
                user.getId(),
                user.getUsername(),
                user.getRole()
        );
    }
}