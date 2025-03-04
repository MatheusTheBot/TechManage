package matheus_henrique.TechManage.Controllers;

import gen.model.ResponseModel;
import gen.model.UserModel;
import matheus_henrique.TechManage.Enums.EUserType;
import matheus_henrique.TechManage.Infra.Repository.UserRepository;
import matheus_henrique.TechManage.Models.User;
import matheus_henrique.TechManage.Services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {
    private UserRepository repo;
    private UserService userService;
    private UserController userController;

    private User testUser;
    private UserModel testUserModel;

    @BeforeEach
    void setUp() {
        //Eu tentei multiplas alternativas para testar o Controller, mas pelo fato da service precisar de injeção de dependencia,
        // o mockito não consegue fazer o mock corretamente

        userController = new UserController();
        userService = Mockito.mock(UserService.class);

        ReflectionTestUtils.setField(userService, "repository", repo);
        ReflectionTestUtils.setField(userController, "service", userService);

        testUser = User.builder()
                .id(1L)
                .fullName("Test User")
                .email("test@example.com")
                .phone("+55 11 99999-9999")
                .birthDate(LocalDate.of(1990, 1, 1))
                .userType(EUserType.ADMIN)
                .build();

        testUserModel = new UserModel()
                .id(1L)
                .fullName("Test User")
                .email("test@example.com")
                .phone("+55 11 99999-9999")
                .birthDate(LocalDate.of(1990, 1, 1))
                .userType(UserModel.UserTypeEnum.ADMIN);
    }

    @Test
    void addUser_ValidUser_ReturnsOkResponse() {
        // Arrange
        when(userService.emailAlreadyExists(anyString())).thenReturn(false);
        when(userService.createUser(any(User.class))).thenReturn(testUser);

        // Act
        ResponseEntity<ResponseModel> response = userController.addUser(testUserModel);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getData());
        assertEquals(1, response.getBody().getData().size());
        verify(userService, times(1)).emailAlreadyExists("test@example.com");
        verify(userService, times(1)).createUser(testUser);
    }

    @Test
    void addUser_ExistingEmail_ReturnsBadRequest() {
        // Arrange
        when(userService.emailAlreadyExists(anyString())).thenReturn(true);

        // Act
        ResponseEntity<ResponseModel> response = userController.addUser(testUserModel);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getErrors());
        assertEquals(1, response.getBody().getErrors().size());
        assertEquals("Email já existe no banco de dados", response.getBody().getErrors().get(0));
        verify(userService, times(1)).emailAlreadyExists("test@example.com");
        verify(userService, never()).createUser(any(User.class));
    }

    @Test
    void deleteUser_ExistingUser_ReturnsOkResponse() {
        // Arrange
        when(userService.deleteUser(1L)).thenReturn(true);

        // Act
        ResponseEntity<ResponseModel> response = userController.deleteUser(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getData());
        assertEquals(1, response.getBody().getData().size());
        assertEquals("Usuário foi deletado!", response.getBody().getData().get(0));
        verify(userService, times(1)).deleteUser(1L);
    }

    @Test
    void deleteUser_NonExistingUser_ReturnsBadRequest() {
        // Arrange
        when(userService.deleteUser(99L)).thenReturn(false);

        // Act
        ResponseEntity<ResponseModel> response = userController.deleteUser(99L);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getErrors());
        assertEquals(1, response.getBody().getErrors().size());
        assertEquals("Usuário não existe!", response.getBody().getErrors().get(0));
        verify(userService, times(1)).deleteUser(99L);
    }

    @Test
    void getUser_ExistingId_ReturnsOkResponse() {
        // Arrange
        when(userService.getUserById(1L)).thenReturn(testUser);

        // Act
        ResponseEntity<ResponseModel> response = userController.getUser(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getData());
        assertEquals(1, response.getBody().getData().size());
        verify(userService, times(1)).getUserById(1L);
    }

    @Test
    void getUser_NonExistingId_ReturnsBadRequest() {
        // Arrange
        when(userService.getUserById(99L)).thenReturn(null);

        // Act
        ResponseEntity<ResponseModel> response = userController.getUser(99L);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getErrors());
        assertEquals(1, response.getBody().getErrors().size());
        assertEquals("Usuário não foi encontrado!!", response.getBody().getErrors().get(0));
        verify(userService, times(1)).getUserById(99L);
    }

    @Test
    void getUsers_UsersExist_ReturnsOkResponse() {
        // Arrange
        User secondUser = User.builder()
                .id(2L)
                .fullName("Second User")
                .email("second@example.com")
                .phone("+55 11 88888-8888")
                .birthDate(LocalDate.of(1995, 5, 5))
                .userType(EUserType.EDITOR)
                .build();

        List<User> userList = Arrays.asList(testUser, secondUser);
        when(userService.getAllUser()).thenReturn(userList);

        // Act
        ResponseEntity<ResponseModel> response = userController.getUsers();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getData());
        assertEquals(1, response.getBody().getData().size()); // The data contains a single list
        verify(userService, times(1)).getAllUser();
    }

    @Test
    void getUsers_NoUsers_ReturnsNoContent() {
        // Arrange
        when(userService.getAllUser()).thenReturn(Collections.emptyList());

        // Act
        ResponseEntity<ResponseModel> response = userController.getUsers();

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(userService, times(1)).getAllUser();
    }

    @Test
    void updateUser_ValidUpdate_ReturnsOkResponse() {
        // Arrange
        when(userService.getUserById(1L)).thenReturn(testUser);
        when(userService.emailAlreadyExists(anyString())).thenReturn(false);
        when(userService.updateUser(any(User.class))).thenReturn(testUser);

        UserModel updatedModel = new UserModel()
                .fullName("Updated Name")
                .email("test@example.com") // Same email
                .phone("+55 11 99999-9999")
                .birthDate(LocalDate.of(1990, 1, 1))
                .userType(UserModel.UserTypeEnum.EDITOR);

        // Act
        ResponseEntity<ResponseModel> response = userController.updateUser(1L, updatedModel);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getData());
        assertEquals(1, response.getBody().getData().size());
        verify(userService, times(1)).getUserById(1L);
        verify(userService, never()).emailAlreadyExists(anyString()); // Same email, no need to check
        verify(userService, times(1)).updateUser(any(User.class));
    }

    @Test
    void updateUser_ChangedEmailAlreadyExists_ReturnsBadRequest() {
        // Arrange
        when(userService.getUserById(1L)).thenReturn(testUser);
        when(userService.emailAlreadyExists("new@example.com")).thenReturn(true);

        UserModel updatedModel = new UserModel()
                .fullName("Updated Name")
                .email("new@example.com") // Different email
                .phone("+55 11 99999-9999")
                .birthDate(LocalDate.of(1990, 1, 1))
                .userType(UserModel.UserTypeEnum.EDITOR);

        // Act
        ResponseEntity<ResponseModel> response = userController.updateUser(1L, updatedModel);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getErrors());
        assertEquals(1, response.getBody().getErrors().size());
        assertEquals("Já existe um usuário com este email!", response.getBody().getErrors().get(0));
        verify(userService, times(1)).getUserById(1L);
        verify(userService, times(1)).emailAlreadyExists("new@example.com");
        verify(userService, never()).updateUser(any(User.class));
    }

    @Test
    void updateUser_NonExistingUser_ReturnsBadRequest() {
        // Arrange
        when(userService.getUserById(99L)).thenReturn(null);

        // Act
        ResponseEntity<ResponseModel> response = userController.updateUser(99L, testUserModel);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getErrors());
        assertEquals(1, response.getBody().getErrors().size());
        assertEquals("User not found with ID: 99", response.getBody().getErrors().get(0));
        verify(userService, times(1)).getUserById(99L);
        verify(userService, never()).emailAlreadyExists(anyString());
        verify(userService, never()).updateUser(any(User.class));
    }
}