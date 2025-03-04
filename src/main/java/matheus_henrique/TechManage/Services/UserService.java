package matheus_henrique.TechManage.Services;

import lombok.AllArgsConstructor;
import matheus_henrique.TechManage.Models.User;
import matheus_henrique.TechManage.Infra.Repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserService {
    private UserRepository repository;

    public User getUserById(long id) {
        Optional<User> userOptional = repository.findById(id);
        return userOptional.orElse(null);
    }
    public List<User> getAllUser() {
        return repository.findAll();
    }
    public User updateUser(User user) {
        if (repository.existsById(user.getId())) {
            return repository.save(user);
        }
        return null;
    }
    public User createUser(User newUser) {
        return repository.save(newUser);
    }
    public boolean deleteUser(long id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return !repository.existsById(id);
        }
        return false;
    }

    // Para validações
    public boolean emailAlreadyExists(String email){
        return repository.findByEmail(email) == null ? false : true;
    }
}