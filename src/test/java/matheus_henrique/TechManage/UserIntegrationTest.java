package matheus_henrique.TechManage;

import com.fasterxml.jackson.databind.ObjectMapper;
import gen.model.UserModel;
import matheus_henrique.TechManage.Enums.EUserType;
import matheus_henrique.TechManage.Infra.Repository.UserRepository;
import matheus_henrique.TechManage.Models.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class UserIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .fullName("Integration Test User")
                .email("integration@example.com")
                .phone("+55 11 99999-9999")
                .birthDate(LocalDate.of(1990, 1, 1))
                .userType(EUserType.ADMIN)
                .build();

        testUser = userRepository.save(testUser);
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    void getAllUsers_ReturnsUsersList() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data[0]", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$.data[0][0].id", is(testUser.getId().intValue())))
                .andExpect(jsonPath("$.data[0][0].fullName", is("Integration Test User")))
                .andExpect(jsonPath("$.data[0][0].email", is("integration@example.com")));
    }

    @Test
    void getUserById_ExistingId_ReturnsUser() throws Exception {
        mockMvc.perform(get("/api/users/{id}", testUser.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data[0].id", is(testUser.getId().intValue())))
                .andExpect(jsonPath("$.data[0].fullName", is("Integration Test User")))
                .andExpect(jsonPath("$.data[0].email", is("integration@example.com")))
                .andExpect(jsonPath("$.data[0].userType", is("ADMIN")));
    }

    @Test
    void getUserById_NonExistingId_ReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/api/users/999"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errors[0]", is("Usuário não foi encontrado!!")));
    }

    @Test
    void createUser_ValidUser_ReturnsCreatedUser() throws Exception {
        UserModel newUser = new UserModel()
                .fullName("New Integration User")
                .email("new_integration@example.com")
                .phone("+55 11 88888-8888")
                .birthDate(LocalDate.of(1995, 5, 5))
                .userType(UserModel.UserTypeEnum.EDITOR);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUser)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data[0].fullName", is("New Integration User")))
                .andExpect(jsonPath("$.data[0].email", is("new_integration@example.com")))
                .andExpect(jsonPath("$.data[0].userType", is("EDITOR")));

        // Verify the user was actually saved to the database
        User savedUser = userRepository.findByEmail("new_integration@example.com");
        assertEquals("New Integration User", savedUser.getFullName());
        assertEquals(EUserType.EDITOR, savedUser.getUserType());
    }

    @Test
    void createUser_DuplicateEmail_ReturnsBadRequest() throws Exception {
        UserModel duplicateUser = new UserModel()
                .fullName("Duplicate Email User")
                .email("integration@example.com") // Same as testUser
                .phone("+55 11 77777-7777")
                .birthDate(LocalDate.of(1985, 10, 10))
                .userType(UserModel.UserTypeEnum.VIEWER);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(duplicateUser)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errors[0]", is("Email já existe no banco de dados")));
    }

    @Test
    void updateUser_ValidUpdate_ReturnsUpdatedUser() throws Exception {
        UserModel updatedUser = new UserModel()
                .fullName("Updated Integration User")
                .email("updated_integration@example.com")
                .phone("+55 11 66666-6666")
                .birthDate(LocalDate.of(1992, 2, 2))
                .userType(UserModel.UserTypeEnum.VIEWER);

        mockMvc.perform(put("/api/users/{id}", testUser.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedUser)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data[0].id", is(testUser.getId().intValue())))
                .andExpect(jsonPath("$.data[0].fullName", is("Updated Integration User")))
                .andExpect(jsonPath("$.data[0].email", is("updated_integration@example.com")))
                .andExpect(jsonPath("$.data[0].userType", is("VIEWER")));

        // Verify the user was actually updated in the database
        User updatedUserInDb = userRepository.findById(testUser.getId()).orElse(null);
        assertEquals("Updated Integration User", updatedUserInDb.getFullName());
        assertEquals("updated_integration@example.com", updatedUserInDb.getEmail());
        assertEquals(EUserType.VIEWER, updatedUserInDb.getUserType());
    }

    @Test
    void deleteUser_ExistingId_DeletesUserAndReturnsSuccess() throws Exception {
        MvcResult result = mockMvc.perform(delete("/api/users/{id}", testUser.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data[0]", is("Usuário foi deletado!")))
                .andReturn();

        // Verify the user was actually deleted from the database
        assertFalse(userRepository.existsById(testUser.getId()));
    }

    @Test
    void deleteUser_NonExistingId_ReturnsBadRequest() throws Exception {
        mockMvc.perform(delete("/api/users/999"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errors[0]", is("Usuário não existe!")));
    }
}