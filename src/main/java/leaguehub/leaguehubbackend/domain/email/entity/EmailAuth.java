package leaguehub.leaguehubbackend.domain.email.entity;

import jakarta.persistence.*;
import leaguehub.leaguehubbackend.entity.BaseTimeEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EmailAuth extends BaseTimeEntity {

    @Id
    @Column(name = "email_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String email;

    private String authToken;

    private LocalDateTime emailExpireDate;
    @Builder
    public EmailAuth(String email, String authToken) {
        this.email = email;
        this.authToken = authToken;
        this.emailExpireDate = LocalDateTime.now().plusMinutes(10);
    }

    public void changeExpireDate(LocalDateTime localDateTime) {
        this.emailExpireDate = emailExpireDate;
    }
}
