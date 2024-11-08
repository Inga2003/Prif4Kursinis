package com.kursinis.prif4kursinis.model;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Clothes extends Product {

    private String color;
    public Clothes(String title, String description, String manufacturer, Warehouse warehouse, String color) {
        super(title, description, manufacturer, warehouse);
        this.color = color;
        setProductType(ProductType.CLOTHES);
    }
}
