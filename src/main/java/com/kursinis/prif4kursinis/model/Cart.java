package com.kursinis.prif4kursinis.model;

import com.google.gson.annotations.Expose;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import java.time.LocalDate;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Builder.Default
    private LocalDate dateCreated = LocalDate.now();

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL)
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<Product> itemsInCart;

    @OneToOne
    private User user;

    @Getter
    @Setter
    @OneToOne(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    @LazyCollection(LazyCollectionOption.FALSE)
    private Order order;

    public Cart(LocalDate dateCreated) {
        this.dateCreated = dateCreated;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (Product product : itemsInCart) {
            stringBuilder.append(product.toString()).append(" ");
        }
        return stringBuilder.toString();
    }


}
