package org.yearup.service;

import jakarta.transaction.Transactional;
import org.aspectj.weaver.ast.Or;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.yearup.exception.CartItemNotFoundException;
import org.yearup.exception.ProfileDataNotFoundException;
import org.yearup.models.Order;
import org.yearup.models.OrderDetails;
import org.yearup.models.Profile;
import org.yearup.models.ShoppingCart;
import org.yearup.repository.OrderDetailsRepository;
import org.yearup.repository.OrderRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

        List<OrderDetails> orderDetailsList= new ArrayList<>();

        cart.getItems().values()
                .forEach(cartItem -> {
                        OrderDetails orderDetails= new OrderDetails();
                        orderDetails.setOrderId(savedOrder.getOrderId());
                        orderDetails.setProductId(cartItem.getProductId());
                        orderDetails.setPrice(cartItem.getLineTotal());
                        orderDetails.setQuantity(cartItem.getQuantity());
                        orderDetails.setDiscount(cartItem.getDiscountPercent());

                        orderDetailsList.add(orderDetails);
                });

        orderDetailsRepository.saveAll(orderDetailsList);

        shoppingCartService.clearCart(userId);

        return savedOrder;
    }

    public boolean isProfileEmpty(Profile profile) {
        return profile == null ||
                isBlank(profile.getFirstName()) &&
                        isBlank(profile.getLastName()) &&
                        isBlank(profile.getPhone()) &&
                        isBlank(profile.getEmail()) &&
                        isBlank(profile.getAddress()) &&
                        isBlank(profile.getCity()) &&
                        isBlank(profile.getState()) &&
                        isBlank(profile.getZip());
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
