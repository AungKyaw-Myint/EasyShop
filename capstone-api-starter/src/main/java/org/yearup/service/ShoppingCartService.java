package org.yearup.service;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.yearup.exception.CartItemNotFoundException;
import org.yearup.exception.InsufficientStockException;
import org.yearup.exception.ProductNotFoundException;
import org.yearup.models.*;
import org.yearup.repository.ProductRepository;
import org.yearup.repository.ShoppingCartRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ShoppingCartService
{
    // a shopping cart is built from cart rows plus a product lookup for each row
    private final ShoppingCartRepository shoppingCartRepository;
    private final ProductService productService;
    private final ProductRepository productRepository;

    public ShoppingCartService(ShoppingCartRepository shoppingCartRepository, ProductService productService, ProductRepository productRepository)
    {
        this.shoppingCartRepository = shoppingCartRepository;
        this.productService = productService;
        this.productRepository = productRepository;
    }

    public ShoppingCart getByUserId(int userId)
    {
        // load the user's cart rows, look up each product, and build the ShoppingCart
        List<CartItem> cartItems=shoppingCartRepository.findByUserId(userId);

        List<Integer> productIds = cartItems.stream()
                .map(CartItem::getProductId)
                .toList();

        Map<Integer, Product> productList= getProductListById(productIds);

        ShoppingCart shoppingCart= new ShoppingCart();
        for(CartItem item : cartItems){
            int productId= item.getProductId();
            if(!shoppingCart.contains(productId)){
                ShoppingCartItem cartItem= new ShoppingCartItem();
                cartItem.setProduct(productList.get(productId));
                cartItem.setQuantity(item.getQuantity());
                cartItem.setDiscountPercent(0);

                shoppingCart.add(cartItem);
            }
        }

        return shoppingCart;
    }

    // add additional methods here
    private Map<Integer, Product> getProductListById(List<Integer> products){

        Map<Integer, Product> result = productRepository.findAllById(products)
                .stream()
                .collect(Collectors.toMap(
                        Product::getProductId,
                        Function.identity()
                ));

        return result;
    }

    // Add to Cart and Update
    @Transactional
    public ShoppingCart addToCart(int userId, int id, int upateQuantity){

        // Find by product ID, throw error if not found
        Product product= productRepository.findById(id).orElseThrow(() -> new ProductNotFoundException("Product Not Found"));
        // Quantity check
        if(upateQuantity >= product.getStock()) throw new InsufficientStockException("Quantity are not avaliable!");

        if(product != null){
            // Find cart item list according to user id
            List<CartItem> cartItemList= shoppingCartRepository.findByUserId(userId);

            // Update item quantity, adding new item function.
            // Can't update quantity not in the cart.
            cartItemList.stream()
                    .filter(item -> item.getProductId() == id)
                    .findFirst()
                    .ifPresentOrElse(
                            item -> {
                                int newQuantity=upateQuantity == 0 ? item.getQuantity() + 1 : upateQuantity;
                                if(newQuantity > product.getStock()) throw new InsufficientStockException("Quantity are not avaliable!");
                                item.setQuantity(newQuantity);
                                shoppingCartRepository.save(item);
                            },
                            () -> {
                                if(upateQuantity > 1) throw new CartItemNotFoundException("Cart item not found");
                                CartItem cartItem = new CartItem();
                                cartItem.setUserId(userId);
                                cartItem.setProductId(id);
                                cartItem.setQuantity(1);
                                shoppingCartRepository.save(cartItem);
                            }
                    );
        }
        return getByUserId(userId);
    }

    @Transactional
    public void clearCart(int userId){

        shoppingCartRepository.deleteByUserId(userId);
    }
}
