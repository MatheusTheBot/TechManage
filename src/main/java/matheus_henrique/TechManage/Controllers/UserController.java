package matheus_henrique.TechManage.Controllers;

import gen.api.ApiApi;
import gen.model.ResponseModel;
import gen.model.UserModel;
import matheus_henrique.TechManage.Enums.EUserType;
import matheus_henrique.TechManage.Models.User;
import matheus_henrique.TechManage.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/users")
public class UserController implements ApiApi {
    @Autowired
    private UserService service;

    @PostMapping()
    @Override
    public ResponseEntity<ResponseModel> addUser(UserModel userModel) {

        if (service.emailAlreadyExists(userModel.getEmail()))
            return ResponseEntity
                    .badRequest()
                    .body(new ResponseModel()
                            .addErrorsItem("Email já existe no banco de dados")
                    );

        var newUser = User.builder()
                .fullName(userModel.getFullName())
                .email(userModel.getEmail())
                .phone(userModel.getPhone())
                .birthDate(userModel.getBirthDate())
                .userType(EUserType.valueOf(userModel.getUserType().getValue()))
                .build();

        try {
            var response = service.createUser(newUser);

            //improvável de cair aqui, deixando só por precaução
            if (response == null) {
                return ResponseEntity
                        .badRequest()
                        .body(new ResponseModel()
                                .addErrorsItem("Usuário não foi adicionado!!")
                        );
            } else
                return ResponseEntity
                        .ok(new ResponseModel()
                                .addDataItem(response)
                        );
        } catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body(new ResponseModel()
                            .addErrorsItem("Um Erro ocorreu ao processar a request" + e.getMessage())
                    );
        }
    }

    @DeleteMapping("/{id}")
    @Override
    public ResponseEntity<ResponseModel> deleteUser(Long id) {
        var wasDeleted = service.deleteUser(id);

        if (wasDeleted)
            return ResponseEntity
                    .ok(new ResponseModel()
                            .addDataItem("Usuário foi deletado!")
                    );
        else
            return ResponseEntity
                    .badRequest()
                    .body(new ResponseModel()
                            .addErrorsItem("Usuário não existe!")
                    );
    }

    @GetMapping("/{id}")
    @Override
    public ResponseEntity<ResponseModel> getUser(Long id) {
        var user = service.getUserById(id);

        if (user == null)
            return ResponseEntity
                    .badRequest()
                    .body(new ResponseModel()
                            .addErrorsItem("Usuário não foi encontrado!!")
                    );
        else
            return ResponseEntity
                    .ok(new ResponseModel()
                            .addDataItem(user)
                    );
    }

    @GetMapping()
    @Override
    public ResponseEntity<ResponseModel> getUsers() {
        List<User> users = service.getAllUser();

        if (users.isEmpty()) {
            return ResponseEntity
                    .noContent()
                    .build();
        }

        return ResponseEntity
                .ok(new ResponseModel(
                                List.of(),
                                Collections.singletonList(users)
                        )
                );
    }

    @PutMapping("/{id}")
    @Override
    public ResponseEntity<ResponseModel> updateUser(Long id, UserModel userModel) {
        User existingUser = service.getUserById(id);

        if (existingUser == null) {
            return ResponseEntity
                    .badRequest()
                    .body(new ResponseModel()
                            .addErrorsItem("User not found with ID: " + id)
                    );
        }

        if (!Objects.equals(userModel.getEmail(), existingUser.getEmail()))
            if (service.emailAlreadyExists(userModel.getEmail()))
                return ResponseEntity
                        .badRequest()
                        .body(new ResponseModel()
                                .addErrorsItem("Já existe um usuário com este email!")
                        );

        User userToUpdate = User
                .builder()
                .id(id)
                .fullName(userModel.getFullName())
                .email(userModel.getEmail())
                .phone(userModel.getPhone())
                .birthDate(userModel.getBirthDate())
                .userType(EUserType.valueOf(userModel.getUserType().getValue()))
                .build();

        try {
            var response = service.updateUser(userToUpdate);

            //novamente, improvável cair aqui, mas to deixando só por precaução
            if (response == null)
                return ResponseEntity
                        .badRequest()
                        .body(new ResponseModel()
                                .addErrorsItem("Usuário não foi ajustado!!")
                        );
            else return ResponseEntity
                        .ok(new ResponseModel()
                                .addDataItem(response)
                        );

        }
        catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body(new ResponseModel()
                            .addErrorsItem("Um Erro ocorreu ao processar a request" + e.getMessage())
                    );
        }
    }
}