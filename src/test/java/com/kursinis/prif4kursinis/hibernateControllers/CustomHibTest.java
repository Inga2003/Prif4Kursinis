package com.kursinis.prif4kursinis.hibernateControllers;
import com.kursinis.prif4kursinis.hibernateControllers.CustomHib;
import com.kursinis.prif4kursinis.model.*;
import jakarta.persistence.*;
import jakarta.persistence.criteria.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomHibTest {

    private CustomHib customHib;
    private EntityManager entityManager;
    private CriteriaBuilder criteriaBuilder;
    private EntityTransaction transaction;

    @BeforeEach
    void setUp() {
        entityManager = mock(EntityManager.class);
        transaction = mock(EntityTransaction.class);

        // Mock EntityManagerFactory to return our mocked EntityManager
        var entityManagerFactory = mock(EntityManagerFactory.class);
        when(entityManagerFactory.createEntityManager()).thenReturn(entityManager);

        // Instantiate CustomHib with mocked EntityManagerFactory
        customHib = new CustomHib(entityManagerFactory);

        // Mock transaction methods
        when(entityManager.getTransaction()).thenReturn(transaction);
        doNothing().when(transaction).begin();
        doNothing().when(transaction).commit();
        doNothing().when(transaction).rollback();
    }

    @Test
    void testDeleteComment() {

        Comment comment = mock(Comment.class);
        Product product = mock(Product.class); // Mock the Product object
        when(entityManager.find(Comment.class, 1)).thenReturn(comment);
        when(comment.getReplies()).thenReturn(null);  // Assuming no replies for simplicity
        when(comment.getProduct()).thenReturn(product);
        List<Comment> commentsList = mock(ArrayList.class);
        when(product.getComments()).thenReturn(commentsList); // Make sure getComments() returns a mock list
        customHib.deleteComment(1);
        verify(transaction).begin();
        verify(product, times(2)).getComments(); // Verify getComments is called twice
        verify(commentsList).remove(comment); // Verify remove is called on the mock comments list
        verify(entityManager).remove(comment); // Verify the comment is removed
        verify(transaction).commit(); // Verify transaction is committed
    }

    @Test
    void testDeleteComment_NoCommentFound() {
        // Simulate a case where no comment is found in the database
        when(entityManager.find(Comment.class, 1)).thenReturn(null);
        customHib.deleteComment(1);
        verify(transaction, never()).begin();
        verify(entityManager, never()).remove(any(Comment.class));
        verify(transaction, never()).commit();
    }

    @Test
    void testGetUserByCredentials() {
        String login = "testUser";
        String password = "testPass";

        Customer customer = new Customer(login, password, LocalDate.of(1990, 1, 1), "Test", "User", "123 Main St", "1111222233334444");

        // Mocking CriteriaBuilder and other required objects
        criteriaBuilder = mock(CriteriaBuilder.class);
        CriteriaQuery<Customer> userQuery = mock(CriteriaQuery.class); // Correct type for CriteriaQuery
        Root<Customer> userRoot = mock(Root.class); // Correct type for Root
        TypedQuery<Customer> typedUserQuery = mock(TypedQuery.class);

        // Mock the EntityManager to return these mocks
        when(entityManager.getCriteriaBuilder()).thenReturn(criteriaBuilder);
        when(criteriaBuilder.createQuery(Customer.class)).thenReturn(userQuery);
        when(userQuery.from(Customer.class)).thenReturn(userRoot); // Here is where we define what class we're querying
        when(entityManager.createQuery(userQuery)).thenReturn(typedUserQuery);

        // Mock the predicates
        Predicate loginPredicate = mock(Predicate.class);
        Predicate passwordPredicate = mock(Predicate.class);
        when(criteriaBuilder.like(userRoot.get("login"), login)).thenReturn(loginPredicate);
        when(criteriaBuilder.like(userRoot.get("password"), password)).thenReturn(passwordPredicate);
        when(criteriaBuilder.and(loginPredicate, passwordPredicate)).thenReturn(mock(Predicate.class));

        // Setup the query with the predicates
        when(userQuery.where(any(Predicate.class))).thenReturn(userQuery);
        when(typedUserQuery.getSingleResult()).thenReturn(customer);

        // Call the method under test
        Customer result = (Customer) customHib.getUserByCredentials(login, password);

        // Assertions
        assertNotNull(result);
        assertEquals(login, result.getLogin());
        assertEquals(password, result.getPassword());

        // Verify that close() was called on the entity manager
        verify(entityManager).close();
    }


    @Test
    void testGetProductByTitle() {
        String title = "Test Product";
        Product product = new Product();
        product.setTitle(title);

        CriteriaQuery<Product> productQuery = mock(CriteriaQuery.class);
        Root<Product> productRoot = mock(Root.class);
        TypedQuery<Product> typedProductQuery = mock(TypedQuery.class);

        when(criteriaBuilder.createQuery(Product.class)).thenReturn(productQuery);
        when(productQuery.from(Product.class)).thenReturn(productRoot);
        when(productQuery.select(productRoot)).thenReturn(productQuery);
        when(entityManager.createQuery(productQuery)).thenReturn(typedProductQuery);
        when(typedProductQuery.getSingleResult()).thenReturn(product);

        Product result = customHib.getProductByTitle(title);
        assertNotNull(result);
        assertEquals(title, result.getTitle());
        verify(entityManager).close();
    }

    @Test
    void testUpdateUserPassword() {
        int userId = 1;
        String newPassword = "newPassword";
        Customer customer = new Customer();
        customer.setId(userId);
        customer.setPassword("oldPassword");

        when(entityManager.find(Customer.class, userId)).thenReturn(customer);

        customHib.updateUserPassword(userId, newPassword);
        assertEquals(newPassword, customer.getPassword());
        verify(entityManager).merge(customer);
        verify(entityManager).close();
    }

    @Test
    void testDeleteProduct() {
        int productId = 1;
        Product product = new Product();
        product.setId(productId);

        when(entityManager.find(Product.class, productId)).thenReturn(product);

        customHib.deleteProduct(productId);

        verify(entityManager).remove(product);
        verify(entityManager).close();
    }

    @Test
    void testDeleteWarehouse() {
        int warehouseId = 1;
        Warehouse warehouse = new Warehouse();
        warehouse.setId(warehouseId);

        Product product1 = new Product();
        product1.setId(101);
        Product product2 = new Product();
        product2.setId(102);

        warehouse.getInStockProducts().add(product1);
        warehouse.getInStockProducts().add(product2);

        when(entityManager.find(Warehouse.class, warehouseId)).thenReturn(warehouse);

        customHib.deleteWarehouse(warehouseId);

        verify(entityManager).remove(warehouse);
        verify(entityManager).close();
    }

    @Test
    void testGetProductById() {
        int productId = 1;
        Product product = new Product();
        product.setId(productId);

        when(entityManager.find(Product.class, productId)).thenReturn(product);

        Product result = customHib.getProductById(Product.class, productId);
        assertNotNull(result);
        assertEquals(productId, result.getId());
        verify(entityManager).close();
    }

    @Test
    void testGetCartByUserName() {
        String userName = "user1";
        Cart cart = new Cart();
        Customer customer = new Customer();
        customer.setLogin(userName);
        cart.setUser(customer);

        CriteriaQuery<Cart> cartQuery = mock(CriteriaQuery.class);
        Root<Cart> cartRoot = mock(Root.class);
        TypedQuery<Cart> typedCartQuery = mock(TypedQuery.class);

        when(criteriaBuilder.createQuery(Cart.class)).thenReturn(cartQuery);
        when(cartQuery.from(Cart.class)).thenReturn(cartRoot);
        when(cartQuery.select(cartRoot)).thenReturn(cartQuery);
        when(entityManager.createQuery(cartQuery)).thenReturn(typedCartQuery);
        when(typedCartQuery.getSingleResult()).thenReturn(cart);

        Cart result = customHib.getCartByUserName(userName);
        assertNotNull(result);
        assertEquals(userName, result.getUser().getLogin());
        verify(entityManager).close();
    }


    @Test
    void testGetProductTitles() {
        List<String> titles = List.of("Product1", "Product2");

        Query query = mock(Query.class);
        when(entityManager.createQuery("SELECT p.title FROM Product p")).thenReturn(query);
        when(query.getResultList()).thenReturn(titles);

        List<String> result = customHib.getProductTitles();
        assertEquals(2, result.size());
        assertEquals(titles, result);
        verify(entityManager).close();
    }
}
