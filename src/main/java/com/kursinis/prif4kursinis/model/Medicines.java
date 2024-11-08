package com.kursinis.prif4kursinis.model;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Medicines extends Product {

    private String instruction;
    public Medicines(String title, String description, String manufacturer, Warehouse warehouse, String instruction) {
        super(title, description, manufacturer, warehouse);
        this.instruction = instruction;
        setProductType(ProductType.MEDICINES);
    }
}
