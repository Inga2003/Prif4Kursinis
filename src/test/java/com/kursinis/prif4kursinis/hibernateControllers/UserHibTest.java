package com.kursinis.prif4kursinis.hibernateControllers;

import com.kursinis.prif4kursinis.model.Customer;
import com.kursinis.prif4kursinis.model.Manager;
import com.kursinis.prif4kursinis.model.User;
import jakarta.persistence.*;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Collections;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserHibTest {

    private UserHib userHib;
    private EntityManagerFactory entityManagerFactory;
    private EntityManager entityManager;

    @BeforeEach
    void setUp() {
        entityManagerFactory = mock(EntityManagerFactory.class);
        entityManager = mock(EntityManager.class);
        when(entityManagerFactory.createEntityManager()).thenReturn(entityManager);
        userHib = new UserHib(entityManagerFactory);
    }

    @Test
    void createUserWithHashedPassword() {
        Customer customer = new Customer();
        customer.setLogin("customer1");
        customer.setPassword("password");

        EntityTransaction transaction = mock(EntityTransaction.class);
        when(entityManager.getTransaction()).thenReturn(transaction);

        userHib.createUserWithHashedPassword(customer);

        // Verify that the password was hashed
        assertNotEquals("password", customer.getPassword());
        assertTrue(customer.getPassword().length() > 0); // Check if the password is hashed

        verify(transaction).begin();
        verify(transaction).commit();
        verify(entityManager).persist(customer);
        verify(entityManager).close();
    }

    @Test
    void createUser() {
        Manager manager = new Manager();
        manager.setLogin("manager1");
        manager.setPassword("password");

        EntityTransaction transaction = mock(EntityTransaction.class);
        when(entityManager.getTransaction()).thenReturn(transaction);

        userHib.createUser(manager);

        verify(transaction).begin();
        verify(transaction).commit();
        verify(entityManager).persist(manager);
        verify(entityManager).close();
    }

    @Test
    void updateUser() {
        Manager manager = new Manager();
        manager.setId(1);
        manager.setLogin("manager1");
        manager.setPassword("newPassword");

        EntityTransaction transaction = mock(EntityTransaction.class);
        when(entityManager.getTransaction()).thenReturn(transaction);

        userHib.updateUser(manager);

        verify(transaction).begin();
        verify(transaction).commit();
        verify(entityManager).merge(manager);
        verify(entityManager).close();
    }

    @Test
    void deleteUser() {
        int userId = 1;

        User user = mock(User.class);
        when(entityManager.getReference(User.class, userId)).thenReturn(user);

        EntityTransaction transaction = mock(EntityTransaction.class);
        when(entityManager.getTransaction()).thenReturn(transaction);

        userHib.deleteUser(userId);

        verify(transaction).begin();
        verify(entityManager).remove(user);
        verify(transaction).commit();
        verify(entityManager).close();
    }

    @Test
    void getAllCustomers() {
        // Arrange
        Customer customer = new Customer();
        customer.setLogin("customer1");
        customer.setPassword("password");

        TypedQuery<Customer> typedQuery = mock(TypedQuery.class);
        CriteriaBuilder criteriaBuilder = mock(CriteriaBuilder.class);
        CriteriaQuery<Customer> criteriaQuery = mock(CriteriaQuery.class);
        Root<Customer> root = mock(Root.class);

        // Mocking the behavior of entityManager
        when(entityManager.getCriteriaBuilder()).thenReturn(criteriaBuilder);
        when(criteriaBuilder.createQuery(Customer.class)).thenReturn(criteriaQuery);
        when(criteriaQuery.from(Customer.class)).thenReturn(root);
        when(entityManager.createQuery(criteriaQuery)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(Collections.singletonList(customer));

        // Act
        List<Customer> customers = userHib.getAllCustomers();

        // Assert
        assertEquals(1, customers.size());
        assertEquals("customer1", customers.get(0).getLogin());
        verify(entityManager).close();
    }

    @Test
    void getAllManagers() {
        // Arrange
        Manager manager = new Manager();
        manager.setLogin("manager1");
        manager.setPassword("password");

        // Create the mock entity manager and entity manager factory
        EntityManager entityManager = mock(EntityManager.class);
        EntityManagerFactory entityManagerFactory = mock(EntityManagerFactory.class);

        TypedQuery<Manager> typedQuery = mock(TypedQuery.class);
        CriteriaBuilder criteriaBuilder = mock(CriteriaBuilder.class);
        CriteriaQuery<Manager> criteriaQuery = mock(CriteriaQuery.class);
        Root<Manager> root = mock(Root.class);

        // Mocking the behavior of entityManager
        when(entityManagerFactory.createEntityManager()).thenReturn(entityManager);
        when(entityManager.getCriteriaBuilder()).thenReturn(criteriaBuilder);
        when(criteriaBuilder.createQuery(Manager.class)).thenReturn(criteriaQuery);
        when(criteriaQuery.from(Manager.class)).thenReturn(root);
        when(entityManager.createQuery(criteriaQuery)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(Collections.singletonList(manager));

        // Create UserHib instance with the mock entity manager factory
        UserHib userHib = new UserHib(entityManagerFactory);

        // Act
        List<Manager> managers = userHib.getAllManagers();

        // Assert
        assertEquals(1, managers.size());
        assertEquals("manager1", managers.get(0).getLogin());
        verify(entityManager).close(); // Verify that close is called
    }

}
