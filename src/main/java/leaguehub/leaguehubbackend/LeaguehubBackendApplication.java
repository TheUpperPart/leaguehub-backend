package leaguehub.leaguehubbackend;

import jakarta.annotation.PostConstruct;
import leaguehub.leaguehubbackend.util.UserUtil;
import lombok.AllArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@AllArgsConstructor
@SpringBootApplication
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
