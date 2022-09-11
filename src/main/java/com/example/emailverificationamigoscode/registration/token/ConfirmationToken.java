package com.example.emailverificationamigoscode.registration.token;

import com.example.emailverificationamigoscode.appuser.AppUser;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@Getter
@Setter
@Table(name = "confirmation_token",schema = "public")
public class ConfirmationToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @SequenceGenerator(
//            name = "confirmation_token_sequence",
//            sequenceName = "confirmation_token_sequence",
//            allocationSize = 1
//    )
//    @GeneratedValue(
//            strategy = GenerationType.SEQUENCE,
//            generator = "confirmation_token_sequence"
//    )
    private Long id;
    @Column(nullable = false)
    private String token;
    @Column(nullable = false)
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
    private LocalDateTime confirmedAt;
    @ManyToOne
    @JoinColumn(name = "app_user_id",
    nullable = false)
    private AppUser appUser;

    public ConfirmationToken(String token, LocalDateTime createdAt,
                             LocalDateTime expiresAt,
                             AppUser appUser) {
        this.token = token;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
        this.appUser = appUser;
    }
}
