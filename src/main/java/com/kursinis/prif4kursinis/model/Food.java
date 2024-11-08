package com.kursinis.prif4kursinis.model;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
public class Food extends Product {

    private LocalDate expirationDate;
    public Food(String title, String description, LocalDate expirationDate) {
        super(title, description);
        this.expirationDate = expirationDate;
    }

    public Food(String title, String description, String manufacturer, Warehouse warehouse, LocalDate expirationDate) {
        super(title, description, manufacturer, warehouse);
        this.expirationDate = expirationDate;
        setProductType(ProductType.FOOD);
    }
}
