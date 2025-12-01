package cc.szulc.flightapp.service;

import cc.szulc.flightapp.dto.AuthRequestDto;
import cc.szulc.flightapp.dto.AuthResponseDto;
import cc.szulc.flightapp.entity.RefreshToken;
import cc.szulc.flightapp.entity.User;
import cc.szulc.flightapp.exception.UserAlreadyExistsException;
import cc.szulc.flightapp.repository.RefreshTokenRepository;
import cc.szulc.flightapp.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.CacheManager;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private RefreshTokenRepository refreshTokenRepository; // NOWY MOCK

    @Mock
    private CacheManager cacheManager; // NOWY MOCK

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(authService, "refreshTokenDurationMs", 604800000L);
    }

    @Test
    void register_shouldSaveUser_whenUsernameIsUnique() {
        AuthRequestDto request = new AuthRequestDto();
        request.setUsername("newUser");
        request.setPassword("password123");

        when(userRepository.findByUsername("newUser")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password123")).thenReturn("hashedPassword");

        authService.register(request);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();

        assertThat(savedUser.getUsername()).isEqualTo("newUser");
        assertThat(savedUser.getPassword()).isEqualTo("hashedPassword");
    }

    @Test
    void register_shouldThrowException_whenUsernameIsTaken() {
        AuthRequestDto request = new AuthRequestDto();
        request.setUsername("existingUser");
        request.setPassword("password123");

        when(userRepository.findByUsername("existingUser")).thenReturn(Optional.of(new User()));

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(UserAlreadyExistsException.class)
                .hasMessage("User with username 'existingUser' already exists.");

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void login_shouldReturnTokens_whenCredentialsAreValid() {
        // Given
        AuthRequestDto request = new AuthRequestDto();
        request.setUsername("user");
        request.setPassword("password123");

        User user = new User();
        user.setId(1L);
        user.setUsername("user");

        String expectedAccessToken = "mockedAccessToken";
        String expectedRefreshToken = "mockedRefreshToken";

        RefreshToken refreshTokenEntity = new RefreshToken();
        refreshTokenEntity.setToken(expectedRefreshToken);

        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));
        when(jwtService.generateToken(user)).thenReturn(expectedAccessToken);
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenReturn(refreshTokenEntity);

        AuthResponseDto response = authService.login(request);

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(refreshTokenRepository).deleteByUser(user);
        verify(refreshTokenRepository).save(any(RefreshToken.class));

        assertThat(response).isNotNull();
        assertThat(response.getAccessToken()).isEqualTo(expectedAccessToken);
        assertThat(response.getRefreshToken()).isEqualTo(expectedRefreshToken);
    }

    @Test
    void login_shouldThrowException_whenCredentialsAreInvalid() {
        AuthRequestDto request = new AuthRequestDto();
        request.setUsername("user");
        request.setPassword("wrongPassword");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Invalid username or password"));

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessage("Invalid username or password");

        verify(userRepository, never()).findByUsername(anyString());
        verify(jwtService, never()).generateToken(any(User.class));
    }
}