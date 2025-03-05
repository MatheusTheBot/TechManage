package matheus_henrique.TechManage.Models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import matheus_henrique.TechManage.Enums.EUserType;

import java.time.LocalDate;

@Entity
@Table(name = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Full name is required")
    private String fullName;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    @Column(unique = true)
    private String email;

    @NotBlank(message = "Phone should not be blank")
    private String phone;

    @NotNull(message = "BirthDate should not be null")
    private LocalDate birthDate;

    @NotNull(message = "User type is required")
    @Enumerated(EnumType.STRING)
    private EUserType userType;
}