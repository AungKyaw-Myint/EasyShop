package org.yearup.service;

import jakarta.transaction.Transactional;
import org.aspectj.weaver.ast.Or;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.yearup.exception.CartItemNotFoundException;
import org.yearup.exception.InsufficientStockException;
import org.yearup.exception.ProfileDataNotFoundException;
import org.yearup.models.*;
import org.yearup.repository.OrderDetailsRepository;
import org.yearup.repository.OrderRepository;
import org.yearup.repository.ProductRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderDetailsRepository orderDetailsRepository;

    @Autowired
    private ShoppingCartService shoppingCartService;

    @Autowired
    private ProfileService profileService;

    @Autowired
    private ProductRepository productRepository;

    @Transactional
    public Order order(int userId){

        // shopping cart
        ShoppingCart cart= shoppingCartService.getByUserId(userId);
        if(cart.getItems().size()<1) throw new CartItemNotFoundException("Empty cart");
        // user profile
        Profile profile= profileService.getProfile(userId);
        if (isProfileEmpty(profile)) throw new ProfileDataNotFoundException("Update profile before order");

        Order order= new Order();
        order.setUserId(userId);
        order.setDate(LocalDateTime.now());
        order.setAddress(profile.getAddress());
        order.setCity(profile.getCity());
        order.setState(profile.getState());
        order.setZip(profile.getZip());
        order.setAmount(cart.getTotal());

        Order savedOrder=orderRepository.save(order);

        // Save data for the order detail
        List<OrderDetails> orderDetailsList= new ArrayList<>();

        // Product quantity variable to reduce each product after order
        Map<Integer, Integer> quantityMap = new HashMap<>();

        cart.getItems().values()
                .forEach(cartItem -> {
                        OrderDetails orderDetails= new OrderDetails();
                        orderDetails.setOrderId(savedOrder.getOrderId());
                        orderDetails.setProductId(cartItem.getProductId());
                        orderDetails.setPrice(cartItem.getLineTotal());
                        orderDetails.setQuantity(cartItem.getQuantity());
                        orderDetails.setDiscount(cartItem.getDiscountPercent());

                        quantityMap.put(cartItem.getProductId(), cartItem.getQuantity());

                        orderDetailsList.add(orderDetails);
                });

        orderDetailsRepository.saveAll(orderDetailsList);

        shoppingCartService.clearCart(userId);


        // Product Reduce after order process
        List<Product> products =
                productRepository.findAllById(quantityMap.keySet());

        for (Product product : products) {
            Integer qty = quantityMap.get(product.getProductId());
            if (product.getStock() < qty) {
                throw new InsufficientStockException(
                        "Not enough stock for product: " + product.getName()
                );
            }
            product.setStock(product.getStock() - qty);
        }

        productRepository.saveAll(products);

        return savedOrder;
    }

    public boolean isProfileEmpty(Profile profile) {
        return profile == null ||
                isBlank(profile.getFirstName()) &&
                        isBlank(profile.getLastName()) &&
                        isBlank(profile.getPhone()) &&
                        isBlank(profile.getEmail()) &&
                        isBlank(profile.getCity()) &&
                        isBlank(profile.getState()) &&
                        isBlank(profile.getZip());
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
