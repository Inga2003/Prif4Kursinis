package com.kursinis.prif4kursinis.hibernateControllers;

import com.kursinis.prif4kursinis.model.*;
import jakarta.persistence.*;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

public class CustomHib extends GenericHib {

    public CustomHib(EntityManagerFactory entityManagerFactory) {
        super(entityManagerFactory);
    }

    private <T> T executeInTransaction(EntityTransactionAction<T> action) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            T result = action.execute(em);
            em.getTransaction().commit();
            return result;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Transaction failed: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    @FunctionalInterface
    private interface EntityTransactionAction<T> {
        T execute(EntityManager em);
    }

    public User getUserByCredentials(String login, String password) {
        return executeInTransaction(em -> {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<User> query = cb.createQuery(User.class);
            Root<User> root = query.from(User.class);
            query.select(root).where(cb.and(cb.equal(root.get("login"), login), cb.equal(root.get("password"), password)));
            return em.createQuery(query).getSingleResult();
        });
    }

    public Product getProductByTitle(String title) {
        return executeInTransaction(em -> {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Product> query = cb.createQuery(Product.class);
            Root<Product> root = query.from(Product.class);
            query.select(root).where(cb.equal(root.get("title"), title));
            return em.createQuery(query).getSingleResult();
        });
    }

    public void updateUserPassword(int userId, String newPassword) {
        executeInTransaction(em -> {
            User user = em.find(User.class, userId);
            if (user != null) {
                user.setPassword(newPassword);
                em.merge(user);
            }
            return null;
        });
    }

    public void deleteProduct(int productId) {
        executeInTransaction(em -> {
            Product product = em.find(Product.class, productId);
            if (product != null) {
                detachProductDependencies(em, product);
                em.remove(product);
            }
            return null;
        });
    }

    private void detachProductDependencies(EntityManager em, Product product) {
        if (product.getWarehouse() != null) {
            product.getWarehouse().getInStockProducts().remove(product);
            em.merge(product.getWarehouse());
        }
        if (product.getCart() != null) {
            product.getCart().getItemsInCart().remove(product);
            em.merge(product.getCart());
        }
        for (Order order : product.getOrders()) {
            order.getProducts().remove(product);
            em.merge(order);
        }
    }

    public void deleteWarehouse(int warehouseId) {
        executeInTransaction(em -> {
            Warehouse warehouse = em.find(Warehouse.class, warehouseId);
            if (warehouse != null) {
                warehouse.getInStockProducts().forEach(product -> deleteProduct(product.getId()));
                em.remove(warehouse);
            }
            return null;
        });
    }

    public <T> T getProductById(Class<T> entityClass, int id) {
        return executeInTransaction(em -> {
            if (Product.class.equals(entityClass)) {
                return em.createQuery("SELECT p FROM Product p JOIN FETCH p.cart WHERE p.id = :id", entityClass)
                        .setParameter("id", id)
                        .getSingleResult();
            } else {
                return em.find(entityClass, id);
            }
        });
    }

    public <T> T getProductByIdWeb(Class<T> entityClass, int id) {
        return executeInTransaction(em -> {
            if (Product.class.equals(entityClass)) {
                return em.createQuery("SELECT p FROM Product p WHERE p.id = :id", entityClass)
                        .setParameter("id", id)
                        .getSingleResult();
            } else {
                return em.find(entityClass, id);
            }
        });
    }

    public Cart getCartByUserName(String userName) {
        return executeInTransaction(em -> {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Cart> query = cb.createQuery(Cart.class);
            Root<Cart> root = query.from(Cart.class);
            Join<Cart, User> userJoin = root.join("user");
            query.select(root).where(cb.equal(userJoin.get("login"), userName));
            return em.createQuery(query).getSingleResult();
        });
    }

    public List<String> getProductTitles() {
        return executeInTransaction(em -> em.createQuery("SELECT p.title FROM Product p", String.class).getResultList());
    }

    public void deleteComment(int commentId) {
        executeInTransaction(em -> {
            Comment comment = em.find(Comment.class, commentId);
            if (comment != null) {
                recursivelyDeleteComment(em, comment);
            }
            return null;
        });
    }

    private void recursivelyDeleteComment(EntityManager em, Comment comment) {
        for (Comment reply : new ArrayList<>(comment.getReplies())) {
            recursivelyDeleteComment(em, reply);
        }
        if (comment.getProduct() != null) {
            comment.getProduct().getComments().remove(comment);
            em.merge(comment.getProduct());
        }
        em.remove(comment);
    }
}
