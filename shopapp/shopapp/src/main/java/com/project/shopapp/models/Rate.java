package com.project.shopapp.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "rates", uniqueConstraints = @UniqueConstraint(columnNames = {"product_id", "user_id"}))
@Getter
@Setter
@NoArgsConstructor
public class Rate extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @OneToOne
    @JoinColumn(name = "comment_id", nullable = false, unique = true)
    private Comment comment;
    @Column(nullable = false)
    private int rating;
}