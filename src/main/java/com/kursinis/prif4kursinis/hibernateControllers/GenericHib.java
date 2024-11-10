package com.kursinis.prif4kursinis.hibernateControllers;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.CriteriaQuery;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GenericHib {

    private final EntityManagerFactory entityManagerFactory;
    private static final Logger LOGGER = Logger.getLogger(GenericHib.class.getName());

    public GenericHib(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    protected EntityManager getEntityManager() {
        return entityManagerFactory.createEntityManager();
    }

    protected void closeEntityManager(EntityManager em) {
        if (em != null && em.isOpen()) {
            em.close();
        }
    }

    private void logError(String message, Exception e) {
        LOGGER.log(Level.SEVERE, message, e);
    }

    public <T> void create(T entity) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(entity);
            em.getTransaction().commit();
        } catch (Exception e) {
            logError("Failed to create entity", e);
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
        } finally {
            closeEntityManager(em);
        }
    }

    public <T> void update(T entity) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(entity);
            em.getTransaction().commit();
        } catch (Exception e) {
            logError("Failed to update entity", e);
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
        } finally {
            closeEntityManager(em);
        }
    }

    public <T> void delete(Class<T> entityClass, int id) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            T entity = em.find(entityClass, id);
            if (entity != null) {
                em.remove(entity);
                em.getTransaction().commit();
            } else {
                LOGGER.log(Level.WARNING, "Entity with ID {0} not found for deletion", id);
                em.getTransaction().rollback();
            }
        } catch (Exception e) {
            logError("Failed to delete entity", e);
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
        } finally {
            closeEntityManager(em);
        }
    }

    public <T> T getEntityById(Class<T> entityClass, int id) {
        EntityManager em = getEntityManager();
        T result = null;
        try {
            result = em.find(entityClass, id);
        } catch (Exception e) {
            logError("Failed to retrieve entity by ID", e);
        } finally {
            closeEntityManager(em);
        }
        return result;
    }

    public <T> List<T> getAllRecords(Class<T> entityClass) {
        EntityManager em = getEntityManager();
        List<T> result = new ArrayList<>();
        try {
            CriteriaQuery<T> query = em.getCriteriaBuilder().createQuery(entityClass);
            query.select(query.from(entityClass));
            Query q = em.createQuery(query);
            result = q.getResultList();
        } catch (Exception e) {
            logError("Failed to retrieve all records", e);
        } finally {
            closeEntityManager(em);
        }
        return result;
    }
}
