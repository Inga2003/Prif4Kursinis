package com.kursinis.prif4kursinis.webControllers.controllers;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.kursinis.prif4kursinis.hibernateControllers.CustomHib;
import com.kursinis.prif4kursinis.model.*;
import com.kursinis.prif4kursinis.webControllers.serializers.CartGsonSerializer;
import com.kursinis.prif4kursinis.webControllers.serializers.LocalDateGsonSerializer;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import jakarta.persistence.Persistence;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

@Controller
public class CartWeb {
    EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("coursework-shop");
    CustomHib customHib = new CustomHib(entityManagerFactory);

    @RequestMapping(value = "/getCartById/{id}", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public String getCartById(@PathVariable(name = "id") int id){
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(LocalDate.class, new LocalDateGsonSerializer())
                .registerTypeAdapter(Cart.class, new CartGsonSerializer());

        Cart cart = customHib.getEntityById(Cart.class, id);
        Gson gson = builder.create();
        return cart != null ? gson.toJson(cart) : "Cart not found";
    }

    @RequestMapping(value = "/addProductToCart/{productTitle}/{cartId}", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public String addProductToCart(@PathVariable(name = "productTitle") String productTitle, @PathVariable(name = "cartId") int cartId) {
        Cart cart = customHib.getEntityById(Cart.class, cartId);
        Product product = customHib.getProductByTitle(productTitle);

        if (cart != null && product != null) {
            Cart existingCartWithProduct = product.getCart();
            if (existingCartWithProduct != null && existingCartWithProduct.getId() != cartId) {
                existingCartWithProduct.getItemsInCart().remove(product);
                product.setCart(null);
                customHib.update(existingCartWithProduct);
                customHib.update(product);
            }
            cart.getItemsInCart().add(product);
            product.setCart(cart);
            cart.getItemsInCart().add(product);
            customHib.update(product);
            return "Product added to cart successfully";
        } else {
            return "Unable to add product to cart. Cart or product not found.";
        }
    }

    @DeleteMapping(value = "/deleteProductFromCart/{productTitle}/{cartId}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public String deleteProductFromCart(@PathVariable(name = "productTitle") String productTitle, @PathVariable(name = "cartId") int cartId) {
        Cart cart = customHib.getEntityById(Cart.class, cartId);
        Product product = customHib.getProductByTitle(productTitle);

        if (cart != null && product != null) {
            if (product.getCart() != null && product.getCart().getId() == cartId) {
                cart.getItemsInCart().remove(product);
                product.setCart(null);
                customHib.update(cart);
                customHib.update(product);
                return "Product removed from cart successfully";
            } else {
                return "The product is not in the specified cart.";
            }
        } else {
            return "Unable to delete product from cart. Cart or product not found.";
        }
    }

    @RequestMapping(value = "/getCartByUserName/{userName}", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public String getCartByUserName(@PathVariable(name = "userName") String userName) {
        Cart cart = customHib.getCartByUserName(userName);
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(LocalDate.class, new LocalDateGsonSerializer())
                .registerTypeAdapter(Cart.class, new CartGsonSerializer());

        Gson gson = builder.create();
        return cart != null ? gson.toJson(cart) : "Cart not found";
    }

    @RequestMapping(value = "/getAllItemsInCart/{cartId}", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public String getAllItemsInCart(@PathVariable(name = "cartId") int cartId) {
        Cart cart = customHib.getEntityById(Cart.class, cartId);

        if (cart != null) {
            List<String> items = cart.getItemsInCart().stream()
                    .map(Product::toString) // Assuming Product has a meaningful toString method
                    .collect(Collectors.toList());

            Gson gson = new Gson();
            return gson.toJson(items);
        } else {
            return "Cart not found";
        }
    }
}
