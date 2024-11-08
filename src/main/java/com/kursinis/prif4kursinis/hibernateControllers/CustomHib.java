package com.kursinis.prif4kursinis.hibernateControllers;

import com.kursinis.prif4kursinis.model.*;
import jakarta.persistence.*;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import org.mindrot.jbcrypt.BCrypt;



public class CustomHib extends GenericHib {
    public CustomHib(EntityManagerFactory entityManagerFactory) {
        super(entityManagerFactory);
    }
    private EntityManager entityManager;
    public User getUserByCredentials(String login, String password) {
        EntityManager em = getEntityManager();
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<User> query = cb.createQuery(User.class);
            Root<User> root = query.from(User.class);
            query.select(root).where(cb.and(cb.like(root.get("login"), login), cb.like(root.get("password"), password)));
            Query q;

            q = em.createQuery(query);
            return (User) q.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            if (em != null) em.close();
        }
    }

    public Product getProductByTitle(String title) {
        EntityManager em = getEntityManager();
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Product> query = cb.createQuery(Product.class);
            Root<Product> root = query.from(Product.class);
            query.select(root).where(cb.equal(root.get("title"), title));

            Query q = em.createQuery(query);
            return (Product) q.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            if (em != null) em.close();
        }
    }

    public void updateUserPassword(int userId, String newPassword) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            User user = em.find(User.class, userId);

            if (user != null) {
                user.setPassword(newPassword);
                em.merge(user);
            }

            em.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (em != null) em.close();
        }
    }

    public void deleteProduct(int productId){
        EntityManager em = getEntityManager();
        try{
            em.getTransaction().begin();
            var product = em.find(Product.class, productId);
            var warehouse = product.getWarehouse();
            if(warehouse != null){
                warehouse.getInStockProducts().remove(product);
                em.merge(warehouse);
            }

            if (product.getCart() != null) {
                var cart = product.getCart();
                cart.getItemsInCart().remove(product);
                em.merge(cart);
            }

            for (Order order : product.getOrders()) {
                order.getProducts().remove(product);
                em.merge(order);
            }

            em.remove(product);
            em.getTransaction().commit();
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public void deleteWarehouse(int warehouseId) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            var warehouse = em.find(Warehouse.class, warehouseId);
            List<Product> products = new ArrayList<>(warehouse.getInStockProducts());

            for (Product product : products) {
                warehouse.getInStockProducts().remove(product);
                deleteProduct(product.getId()); // Call deleteProduct to delete each product
            }
            em.remove(warehouse);
            em.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (em != null) em.close();
        }
    }

    public <T> T getProductById(Class<T> entityClass, int id) {
        EntityManager em = getEntityManager();
        T result = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            // Use JOIN FETCH to eagerly fetch associated entities
            if (entityClass.equals(Product.class)) {
                // Adjust the query to fetch related entities like cart, warehouse, etc.
                result = em.createQuery("SELECT p FROM Product p JOIN FETCH p.cart WHERE p.id = :id", entityClass)
                        .setParameter("id", id)
                        .getSingleResult();
            } else {
                result = em.find(entityClass, id);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


    public <T> T getProductByIdWeb(Class<T> entityClass, int id) {
        T result = null;
        try {
            EntityManager em = getEntityManager();
            em.getTransaction().begin();

            if (entityClass.equals(Product.class)) {
                // Fetch the Product without including the Cart relationship
                result = em.createQuery("SELECT p FROM Product p WHERE p.id = :id", entityClass)
                        .setParameter("id", id)
                        .getSingleResult();
            } else {
                // If it's not a Product, perform a default find
                result = em.find(entityClass, id);
            }

            em.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public Cart getCartByUserName(String userName) {
        EntityManager em = getEntityManager();
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Cart> query = cb.createQuery(Cart.class);
            Root<Cart> root = query.from(Cart.class);

            // Assuming there's a 'user' field in the Cart entity that refers to the User entity
            Join<Cart, User> userJoin = root.join("user");

            query.select(root).where(cb.equal(userJoin.get("login"), userName));

            Query q = em.createQuery(query);
            return (Cart) q.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            if (em != null) em.close();
        }
    }


    public List<String> getProductTitles() {
        EntityManager em = getEntityManager();
        List<String> result = new ArrayList<>();
        try {
            em = getEntityManager();
            em.getTransaction().begin();

            Query query = em.createQuery("SELECT p.title FROM Product p");
            result = query.getResultList();

            em.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (em != null) em.close();
        }
        return result;
    }

    public void deleteComment(int commentId) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            var comment = em.find(Comment.class, commentId);

            if (comment != null && comment.getReplies() != null) {
                for (Comment reply : new ArrayList<>(comment.getReplies())) {
                    deleteComment(reply.getId());
                }
            }

            var product = comment != null ? comment.getProduct() : null;
            if (product != null && product.getComments() != null) {  // Null check on getComments()
                product.getComments().remove(comment);
                em.merge(product);
            }

            if (comment != null) {
                em.remove(comment);
            }

            em.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (em != null) em.close();
        }
    }

    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

}
