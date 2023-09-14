package leaguehub.leaguehubbackend;

import jakarta.annotation.PostConstruct;
import leaguehub.leaguehubbackend.util.UserUtil;
import lombok.AllArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@AllArgsConstructor
@SpringBootApplication
@EnableJpaRepositories(basePackages = "leaguehub.leaguehubbackend.repository")
@EnableMongoRepositories(basePackages = "leaguehub.leaguehubbackend.mongo_repository")
public class LeaguehubBackendApplication {

    private final UserUtil userUtil;

    public static void main(String[] args) {
        SpringApplication.run(LeaguehubBackendApplication.class, args);
    }

    @PostConstruct
    protected void init() {
        userUtil.addDefaultUsers();
    }
}
