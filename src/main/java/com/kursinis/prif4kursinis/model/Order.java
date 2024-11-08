package com.kursinis.prif4kursinis.model;

import com.google.gson.annotations.Expose;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "customer_order")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Builder.Default
    private LocalDate orderDate = LocalDate.now();

    public Order(LocalDate dateCreated) {
        this.orderDate = dateCreated;
    }

    public enum OrderStatus {
        WAITING,
        PROCESSING,
        COMPLETED,
        CANCELED
    }

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @ManyToMany(fetch = FetchType.EAGER)
    private List<Product> products;

    @ManyToOne
    private Customer customer;

    @ManyToOne
    private Manager manager;

    @OneToOne
    private Cart cart;

    @Override
    public String toString() {
        return "ID: " + id + ", Order Date: " + orderDate;
    }
}
