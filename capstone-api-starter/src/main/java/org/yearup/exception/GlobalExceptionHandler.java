package org.yearup.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;
import org.yearup.models.CartItem;
import org.yearup.models.Product;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ProductNotFoundException.class)
    public Product handleProductNotFound(ProductNotFoundException ex) {

        throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(CartItemNotFoundException.class)
    public Product handleCartItemNotFound(CartItemNotFoundException ex) {

        throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ProfileDataNotFoundException.class)
    public Product handleProfileDataNotFound(ProfileDataNotFoundException ex) {

        throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }
}
