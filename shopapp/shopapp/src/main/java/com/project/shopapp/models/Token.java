package com.project.shopapp.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "tokens")
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Data
@Builder
public class Token {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "token",length = 255,unique = true)
    private String token;
    @Column(name = "token_type",length = 50)
    private String tokenType;
    @Column(name = "expiration_date")
    private LocalDateTime expirationDate;
    @Column(name = "revoked")
    private boolean revoked;
    @ManyToOne
    @JoinColumn(name ="user_id")
    private User user;


}
