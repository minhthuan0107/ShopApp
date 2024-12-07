package com.project.shopapp.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "products")
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class Product extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "name", nullable = false,length = 350)
    private String name;
    private Float price;
    @Column(name = "url_image",length = 350)
    private String urlImage;
    @Column(name = "description")
    private String description;
    @ManyToOne
    @JoinColumn(name ="category_id")
    private Category category;


}
