package cc.szulc.flightapp.controller;

import cc.szulc.flightapp.entity.Role;
import cc.szulc.flightapp.entity.User;
import cc.szulc.flightapp.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AdminControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setup() {
        userRepository.deleteAll();
    }

    @AfterEach
    void cleanup() {
        SecurityContextHolder.clearContext();
        userRepository.deleteAll();
    }

    @Test
    void shouldReturnPaginatedUsers_WhenAdminRequests() throws Exception {
        createTestUser("admin", Role.ROLE_ADMIN);
        createTestUser("user1", Role.ROLE_USER);
        createTestUser("user2", Role.ROLE_USER);

        authenticateAs("admin");

        mockMvc.perform(get("/api/admin/users")
                        .param("page", "0")
                        .param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.totalElements").value(3))
                .andExpect(jsonPath("$.totalPages").value(2))
                .andExpect(jsonPath("$.content[0].username").isNotEmpty())
                .andExpect(jsonPath("$.content[0].role").isNotEmpty());
    }

    @Test
    void shouldReturnForbidden_WhenRegularUserRequests() throws Exception {
        createTestUser("user", Role.ROLE_USER);

        authenticateAs("user");

        mockMvc.perform(get("/api/admin/users"))
                .andExpect(status().isForbidden());
    }

    private void createTestUser(String username, Role role) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode("password"));
        user.setRole(role);
        userRepository.save(user);
    }

    private void authenticateAs(String username) {
        User user = userRepository.findByUsername(username).orElseThrow();
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }
}