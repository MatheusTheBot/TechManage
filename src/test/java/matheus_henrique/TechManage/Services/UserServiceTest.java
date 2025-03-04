package matheus_henrique.TechManage.Services;

import matheus_henrique.TechManage.Enums.EUserType;
import matheus_henrique.TechManage.Infra.Repository.UserRepository;
import matheus_henrique.TechManage.Models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.SpyBean;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .fullName("Test User")
                .email("test@example.com")
                .phone("+55 11 99999-9999")
                .birthDate(LocalDate.of(1990, 1, 1))
                .userType(EUserType.ADMIN)
                .build();
    }

    @Test
    void getUserById_ExistingId_ReturnsUser() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // Act
        User result = userService.getUserById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(testUser.getId(), result.getId());
        assertEquals(testUser.getFullName(), result.getFullName());
        assertEquals(testUser.getEmail(), result.getEmail());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void getUserById_NonExistingId_ReturnsNull() {
        // Arrange
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        // Act
        User result = userService.getUserById(99L);

        // Assert
        assertNull(result);
        verify(userRepository, times(1)).findById(99L);
    }

    @Test
    void getAllUser_ReturnsAllUsers() {
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
        when(userRepository.findAll()).thenReturn(userList);

        // Act
        List<User> result = userService.getAllUser();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(testUser.getId(), result.get(0).getId());
        assertEquals(secondUser.getId(), result.get(1).getId());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void updateUser_ExistingUser_UpdatesAndReturnsUser() {
        // Arrange
        User updatedUser = User.builder()
                .id(1L)
                .fullName("Updated Name")
                .email("updated@example.com")
                .phone("+55 11 77777-7777")
                .birthDate(LocalDate.of(1990, 1, 1))
                .userType(EUserType.EDITOR)
                .build();

        when(userRepository.existsById(1L)).thenReturn(true);
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        // Act
        User result = userService.updateUser(updatedUser);

        // Assert
        assertNotNull(result);
        assertEquals("Updated Name", result.getFullName());
        assertEquals("updated@example.com", result.getEmail());
        assertEquals(EUserType.EDITOR, result.getUserType());
        verify(userRepository, times(1)).existsById(1L);
        verify(userRepository, times(1)).save(updatedUser);
    }

    @Test
    void updateUser_NonExistingUser_ReturnsNull() {
        // Arrange
        User nonExistingUser = User.builder()
                .id(99L)
                .fullName("Non Existing")
                .email("nonexisting@example.com")
                .phone("+55 11 66666-6666")
                .birthDate(LocalDate.of(1990, 1, 1))
                .userType(EUserType.VIEWER)
                .build();

        when(userRepository.existsById(99L)).thenReturn(false);

        // Act
        User result = userService.updateUser(nonExistingUser);

        // Assert
        assertNull(result);
        verify(userRepository, times(1)).existsById(99L);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void createUser_SavesAndReturnsUser() {
        // Arrange
        User newUser = User.builder()
                .fullName("New User")
                .email("new@example.com")
                .phone("+55 11 55555-5555")
                .birthDate(LocalDate.of(1995, 5, 5))
                .userType(EUserType.VIEWER)
                .build();

        User savedUser = User.builder()
                .id(3L)
                .fullName("New User")
                .email("new@example.com")
                .phone("+55 11 55555-5555")
                .birthDate(LocalDate.of(1995, 5, 5))
                .userType(EUserType.VIEWER)
                .build();

        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // Act
        User result = userService.createUser(newUser);

        // Assert
        assertNotNull(result);
        assertEquals(3L, result.getId());
        assertEquals("New User", result.getFullName());
        assertEquals("new@example.com", result.getEmail());
        verify(userRepository, times(1)).save(newUser);
    }

    @Test
    void deleteUser_ExistingId_DeletesUserAndReturnsTrue() {
        // Arrange
        when(userRepository.existsById(1L)).thenReturn(true, false);
        doNothing().when(userRepository).deleteById(1L);

        // Act
        boolean result = userService.deleteUser(1L);

        // Assert
        assertTrue(result);
        verify(userRepository, times(1)).deleteById(1L);
        verify(userRepository, times(2)).existsById(1L);
    }

    @Test
    void deleteUser_NonExistingId_ReturnsFalse() {
        // Arrange
        when(userRepository.existsById(99L)).thenReturn(false);

        // Act
        boolean result = userService.deleteUser(99L);

        // Assert
        assertFalse(result);
        verify(userRepository, times(1)).existsById(99L);
        verify(userRepository, never()).deleteById(anyLong());
    }

    @Test
    void emailAlreadyExists_ExistingEmail_ReturnsTrue() {
        // Arrange
        when(userRepository.findByEmail("test@example.com")).thenReturn(testUser);

        // Act
        boolean result = userService.emailAlreadyExists("test@example.com");

        // Assert
        assertTrue(result);
        verify(userRepository, times(1)).findByEmail("test@example.com");
    }

    @Test
    void emailAlreadyExists_NonExistingEmail_ReturnsFalse() {
        // Arrange
        when(userRepository.findByEmail("nonexisting@example.com")).thenReturn(null);

        // Act
        boolean result = userService.emailAlreadyExists("nonexisting@example.com");

        // Assert
        assertFalse(result);
        verify(userRepository, times(1)).findByEmail("nonexisting@example.com");
    }
}