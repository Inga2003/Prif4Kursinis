package com.kursinis.prif4kursinis.hibernateControllers;

import com.kursinis.prif4kursinis.model.Warehouse;
import jakarta.persistence.*;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class WarehouseHibTest {
    private WarehouseHib warehouseHib;
    private EntityManagerFactory entityManagerFactory;
    private EntityManager entityManager;

    @BeforeEach
    void setUp() {
        entityManagerFactory = mock(EntityManagerFactory.class);
        entityManager = mock(EntityManager.class);
        when(entityManagerFactory.createEntityManager()).thenReturn(entityManager);
        warehouseHib = new WarehouseHib(entityManagerFactory);
    }

    @Test
    void createWarehouse() {
        Warehouse warehouse = new Warehouse();

        when(entityManager.getTransaction()).thenReturn(mock(EntityTransaction.class));
        warehouseHib.createWarehouse(warehouse);

        verify(entityManager).persist(warehouse);
        verify(entityManager).close();
    }

    @Test
    void updateWarehouse() {
        Warehouse warehouse = new Warehouse();

        when(entityManager.getTransaction()).thenReturn(mock(EntityTransaction.class));
        warehouseHib.updateWarehouse(warehouse);

        verify(entityManager).merge(warehouse);
        verify(entityManager).close();
    }

    @Test
    void deleteWarehouse() {
        int id = 1;
        Warehouse warehouse = new Warehouse();

        when(entityManager.getTransaction()).thenReturn(mock(EntityTransaction.class));
        when(entityManager.getReference(Warehouse.class, id)).thenReturn(warehouse);

        warehouseHib.deleteWarehouse(id);

        verify(entityManager).remove(warehouse);
        verify(entityManager).close();
    }

    @Test
    void getAllWarehouse() {
        CriteriaBuilder cb = mock(CriteriaBuilder.class);
        CriteriaQuery<Warehouse> cq = mock(CriteriaQuery.class);
        Root<Warehouse> root = mock(Root.class); // Mock the Root
        TypedQuery<Warehouse> typedQuery = mock(TypedQuery.class);

        // Setup mocks
        when(entityManager.getCriteriaBuilder()).thenReturn(cb);
        when(cb.createQuery(Warehouse.class)).thenReturn(cq);
        when(cq.from(Warehouse.class)).thenReturn(root); // Stub the root creation
        when(entityManager.createQuery(cq)).thenReturn(typedQuery);

        List<Warehouse> warehouseList = new ArrayList<>();
        when(typedQuery.getResultList()).thenReturn(warehouseList); // Fetching result list

        // Execute method under test
        List<Warehouse> result = warehouseHib.getAllWarehouse();

        // Assertions
        assertEquals(warehouseList, result);
        verify(entityManager).close(); // Ensure the entity manager is closed
    }
}
