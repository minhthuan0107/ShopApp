package com.project.shopapp.models;

import jakarta.persistence.*;
import lombok.*;
@Entity
@Table(name = "favorites")
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class Favorite extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;


    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
}
