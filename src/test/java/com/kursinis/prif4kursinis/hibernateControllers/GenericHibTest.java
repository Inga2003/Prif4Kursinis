package com.kursinis.prif4kursinis.hibernateControllers;

import jakarta.persistence.*;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GenericHibTest {

    @Mock
    private EntityManagerFactory entityManagerFactory;

    @Mock
    private EntityManager em;

    @InjectMocks
    private GenericHib genericHib;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(entityManagerFactory.createEntityManager()).thenReturn(em);
    }

    @Test
    void testCreate() {
        Object entity = new Object();
        when(em.getTransaction()).thenReturn(mock(EntityTransaction.class));

        genericHib.create(entity);

        verify(em).persist(entity);
        verify(em).close();
    }

    @Test
    void testUpdate() {
        Object entity = new Object();
        when(em.getTransaction()).thenReturn(mock(EntityTransaction.class));

        genericHib.update(entity);

        verify(em).merge(entity);
        verify(em).close();
    }

    @Test
    void testDelete() {
        when(em.getTransaction()).thenReturn(mock(EntityTransaction.class));
        Object mockEntity = new Object();
        when(em.find(Object.class, 1)).thenReturn(mockEntity);

        genericHib.delete(Object.class, 1);

        verify(em).remove(mockEntity);
        verify(em).close();
    }

    @Test
    void testGetEntityById() {
        Object mockEntity = new Object();
        when(em.getTransaction()).thenReturn(mock(EntityTransaction.class));
        when(em.find(Object.class, 1)).thenReturn(mockEntity);

        Object result = genericHib.getEntityById(Object.class, 1);

        assertEquals(mockEntity, result);
    }

    @Test
    void testGetAllRecords() {
        List<Object> mockList = new ArrayList<>();
        TypedQuery<Object> mockQuery = mock(TypedQuery.class); // TypedQuery<Object> instead of Query
        CriteriaQuery<Object> mockCriteriaQuery = mock(CriteriaQuery.class);
        CriteriaBuilder mockCriteriaBuilder = mock(CriteriaBuilder.class);
        Root<Object> mockRoot = mock(Root.class); // Mock the Root to be returned by from()

        when(em.getCriteriaBuilder()).thenReturn(mockCriteriaBuilder);
        when(mockCriteriaBuilder.createQuery(Object.class)).thenReturn(mockCriteriaQuery);
        when(mockCriteriaQuery.from(Object.class)).thenReturn(mockRoot); // Setup for the from() call
        when(em.createQuery(mockCriteriaQuery)).thenReturn(mockQuery);
        when(mockQuery.getResultList()).thenReturn(mockList);

        List<Object> result = genericHib.getAllRecords(Object.class);
        assertEquals(mockList, result);
        verify(em).close();
    }
}
