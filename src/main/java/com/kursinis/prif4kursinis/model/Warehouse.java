package com.kursinis.prif4kursinis.model;

import com.google.gson.annotations.Expose;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
public class Warehouse implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String title;
    private String address;
    @OneToMany(mappedBy = "warehouse", cascade = CascadeType.ALL, orphanRemoval = true)
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<Product> inStockProducts = new ArrayList<>();  // Initialize here

    @ManyToMany(mappedBy = "worksAtWarehouse", cascade = CascadeType.ALL)
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<Manager> managers = new ArrayList<>();  // Initialize here

    public Warehouse(String title, String address) {
        this.title = title;
        this.address = address;
        this.inStockProducts = new ArrayList<>();
        this.managers = new ArrayList<>();
    }



    @Override
    public String toString() {
        return title;
    }
}
