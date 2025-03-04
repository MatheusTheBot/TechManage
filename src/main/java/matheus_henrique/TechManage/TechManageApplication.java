package matheus_henrique.TechManage;

import matheus_henrique.TechManage.Enums.EUserType;
import matheus_henrique.TechManage.Infra.Repository.UserRepository;
import matheus_henrique.TechManage.Models.User;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.time.LocalDate;

@SpringBootApplication
public class TechManageApplication {
	public static void main(String[] args) {
		SpringApplication.run(TechManageApplication.class, args);
	}
}
