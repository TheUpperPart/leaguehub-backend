package leaguehub.leaguehubbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "leaguehub.leaguehubbackend.repository")
public class LeaguehubBackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(LeaguehubBackendApplication.class, args);
    }
}
