package org.yearup.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.yearup.models.Product;
import org.yearup.models.ShoppingCart;
import org.yearup.models.ShoppingCartItem;
import org.yearup.models.User;
import org.yearup.service.ShoppingCartService;
import org.yearup.service.UserService;

import java.security.Principal;

// convert this class to a REST controller
// only logged in users should have access to these actions
@RestController
@RequestMapping("cart")
@CrossOrigin
public class ShoppingCartController
{
    // a shopping cart controller depends on the service layer
    @Autowired
    private ShoppingCartService shoppingCartService;

    @Autowired
    private UserService userService;

    // each method in this controller requires a Principal object as a parameter
    @GetMapping("")
//    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @PreAuthorize("isAuthenticated()")
    public ShoppingCart getCart(Principal principal){
        int userId=getUserId(principal);

        // use the shoppingCartService to get all items in the cart and return the cart
        ShoppingCart cart=shoppingCartService.getByUserId(userId);
        if (cart == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        return cart;
    }

    // add a POST method to add a product to the cart - the url should be
    // https://localhost:8080/cart/products/15  (15 is the productId to be added)
    // return the updated cart with status 201 Created
    @PostMapping("products/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ShoppingCart> addShoppingCart(@PathVariable int id, Principal principal){
        int userId=getUserId(principal);
        ShoppingCart saved = shoppingCartService.addToCart(userId,id , 0);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // add a PUT method to update an existing product in the cart - the url should be
    // https://localhost:8080/cart/products/15  (15 is the productId to be updated)
    // the BODY should be a ShoppingCartItem - quantity is the only value that will be updated; return the cart (200 OK)
    @PutMapping("products/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ShoppingCart> updateShoppingCart(@PathVariable int id, Principal principal, @RequestBody ShoppingCartItem cartItem){
        int userId=getUserId(principal);
        ShoppingCart saved = shoppingCartService.addToCart(userId,id ,cartItem.getQuantity() );
        return ResponseEntity.status(HttpStatus.OK).body(saved);
    }

    // add a DELETE method to clear all products from the current users cart
    // https://localhost:8080/cart  - return the (now empty) cart so the front end can refresh it (200 OK)
    @DeleteMapping("")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> clearShoppingCart(Principal principal){
        int userId=getUserId(principal);
        shoppingCartService.clearCart(userId);
        return ResponseEntity.noContent().build();
    }

    private int getUserId(Principal principal){
        // get the currently logged in username
        String userName = principal.getName();
        // find database user by username
        User user = userService.getByUserName(userName);
        int userId = user.getId();
        return userId;
    }
}
