package matheus_henrique.TechManage.Infra;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import matheus_henrique.TechManage.Infra.Repository.UserRepository;
import matheus_henrique.TechManage.Models.User;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.InputStream;
import java.net.URI;
import java.util.Arrays;
import java.util.List;

@Configuration
@EnableJpaRepositories(basePackages = "matheus_henrique.TechManage.Infra.Repository")
@EntityScan("matheus_henrique.TechManage.Models")
public class DatabaseConfig {
    private static final String MOCKAROO = "https://api.mockaroo.com/api/093d7dc0?count=300&key=86426470";
    @Bean
    public CommandLineRunner dataInitializer(UserRepository repository) {
        return args -> {
            System.out.println("\u001B[32m" + "******** SETTANDO DADOS DE MOCK ********" + "\u001B[0m");

            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<User[]> response = restTemplate.getForEntity(URI.create(MOCKAROO), User[].class);

            //if the request fail, we use a fallback file
            if(response.getStatusCode().is2xxSuccessful() && response.hasBody()){
                System.out.println("\u001B[32m" + "******** USANDO DADOS DA API DE MOCK ********" + "\u001B[0m");

                repository.saveAll(
                        Arrays.stream(response.getBody()).toList()
                );
            }
            else {
                System.out.println("\u001B[32m" + "******** USANDO DADOS FALLBACK DO MOCK ********" + "\u001B[0m");

                try(InputStream in = Thread.currentThread()
                        .getContextClassLoader()
                        .getResourceAsStream("UsersFallback.json"))
                {
                    ObjectMapper mapper = new ObjectMapper();
                    var userList = mapper.readValue(in,
                            new TypeReference<List<User>>() {});

                    repository.saveAll(userList);
                }
                catch(Exception e){
                    throw new RuntimeException("Erro ao converter arquivo fallback e inserir dados no banco: ", e);
                }
            }

            if(repository.count() == 0)
                System.out.println("Sample data was not initialized by unknown reasons");
            else
                System.out.println("Sample data initialized in H2 database!");
        };
    }
}