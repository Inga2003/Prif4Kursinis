package com.kursinis.prif4kursinis.hibernateControllers;

import com.kursinis.prif4kursinis.model.Customer;
import com.kursinis.prif4kursinis.model.Manager;
import com.kursinis.prif4kursinis.model.User;
import jakarta.persistence.*;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.mindrot.jbcrypt.BCrypt;

import java.util.ArrayList;
import java.util.List;

public class UserHib {

    private EntityManagerFactory entityManagerFactory;

    public UserHib(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    EntityManager getEntityManager() {
        return entityManagerFactory.createEntityManager();
    }

    //Persist atitiks INSERT
    public void createUserWithHashedPassword(User user) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            // Hash the password before setting it
            user.setPassword(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()));
            em.persist(user);
            em.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (em != null) em.close();
        }
    }

    public void createUser(User user) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.persist(user);
            em.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (em != null) em.close();
        }
    }

    //Merge atitiks UPDATE
    public void updateUser(User user) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.merge(user);
            em.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (em != null) em.close();
        }
    }

    //
    public void deleteUser(int id) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            User user = null;
            try {
                user = em.getReference(User.class, id);
                user.getId();
            } catch (Exception e) {
                System.out.println("No such user by given ID");
            }

            //Biski i ateiti, bet cia reikes nulinkint nuo susijusiu objektu, kad man leistu istrinti

            em.remove(user);
            em.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (em != null) em.close();
        }
    }

    public List<Customer> getAllCustomers() {
        EntityManager em = null;
        try {
            em = getEntityManager();
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Customer> query = cb.createQuery(Customer.class);
            Root<Customer> root = query.from(Customer.class);
            query.select(root);
            Query q = em.createQuery(query);
            return q.getResultList();
        } catch (Exception e) {
            e.printStackTrace(); // Consider logging instead
        } finally {
            if (em != null && em.isOpen()) em.close();
        }
        return new ArrayList<>();
    }

    public List<Manager> getAllManagers() {
        EntityManager em = null;
        try {
            em = getEntityManager();
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Manager> query = cb.createQuery(Manager.class);
            Root<Manager> root = query.from(Manager.class);
            query.select(root);
            Query q = em.createQuery(query);
            return q.getResultList();
        } catch (Exception e) {
            e.printStackTrace(); // Consider logging instead
        } finally {
            if (em != null && em.isOpen()) em.close();
        }
        return new ArrayList<>();
    }


    public User getUserByCredentials(String login, String password) {
        EntityManager em = getEntityManager();
        if (em == null) {
            throw new IllegalStateException("EntityManager is null");
        }

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<User> query = cb.createQuery(User.class);
        Root<User> root = query.from(User.class);
        query.select(root).where(cb.equal(root.get("login"), login));

        TypedQuery<User> q = em.createQuery(query);
        User user = null;

        try {
            user = q.getSingleResult();
            if (user != null && BCrypt.checkpw(password, user.getPassword())) {
                return user;
            }
        } catch (NoResultException e) {
            return null; // No user found
        } finally {
            em.close();
        }

        return null; // If user is not valid or found
    }


}
