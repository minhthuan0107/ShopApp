package com.project.shopapp.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "social_accounts")
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Data
@Builder
public class SocialAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "provider",length = 20,nullable = false)
    private String 	provider;
    @Column(name = "provider_id",length = 50,nullable = false)
    private String 	providerId;
    @Column(name = "email",length = 100)
    private String 	email;
    @Column(name = "name",length = 100)
    private String name;
    @ManyToOne
    @JoinColumn(name ="user_id")
    private User user;


}
