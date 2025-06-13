package com.project.shopapp.models;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.time.LocalDate;
import java.util.Date;

@Entity
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class User extends BaseEntity  {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "fullname",length = 100)
    private String 	fullname;
    @Column(name = "phone_number",length = 20)
    private String phoneNumber ;
    @Column(name = "address",length = 200)
    private String address;
    @Column(name = "password",length = 200)
    private String password;
    @Column(name = "is_active",insertable = false)
    private boolean isActive;
    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;
    @Column(name = "facebook_account_id")
    private String facebookAccountId;
    @Column(name = "google_account_id")
    private String googleAccountId;
    @ManyToOne
    @JoinColumn(name ="role_id")
    private Role role;

}
