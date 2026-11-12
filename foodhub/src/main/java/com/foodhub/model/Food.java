package com.foodhub.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Food {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String restaurant;
    private String name;
    private Double price;
    private String imageUrl; 
}
