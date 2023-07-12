package leaguehub.leaguehubbackend;

import jakarta.annotation.PostConstruct;
import leaguehub.leaguehubbackend.util.UserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LeaguehubBackendApplication {
    private final UserUtil userUtil;

    @Autowired
    public LeaguehubBackendApplication(UserUtil userUtil) {
        this.userUtil = userUtil;
    }

    public static void main(String[] args) {
        SpringApplication.run(LeaguehubBackendApplication.class, args);
    }

    @PostConstruct
    protected void init() {
        userUtil.addDefaultUsers();
    }
}
