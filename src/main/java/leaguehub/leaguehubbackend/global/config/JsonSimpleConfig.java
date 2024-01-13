package leaguehub.leaguehubbackend.global.config;

import org.json.simple.parser.JSONParser;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JsonSimpleConfig {

    @Bean
    public JSONParser jsonParser(){
        return new JSONParser();
    }
}
