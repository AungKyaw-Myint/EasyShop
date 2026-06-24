package org.yearup.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.yearup.models.Profile;
import org.yearup.models.ShoppingCart;
import org.yearup.models.ShoppingCartItem;
import org.yearup.models.User;
import org.yearup.service.ProfileService;
import org.yearup.service.UserService;

import java.security.Principal;

@RestController
@RequestMapping("profile")
@CrossOrigin
public class ProfileController {

    @Autowired
    private ProfileService profileService;

    @Autowired
    private UserService userService;

    @GetMapping("")
    @PreAuthorize("isAuthenticated()")
    public Profile getProfile(Principal principal){
        int userId=getUserId(principal);

        // use the shoppingCartService to get all items in the cart and return the cart
        Profile profile=profileService.getProfile(userId);
        if (profile == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        return profile;
    }

    @PutMapping("")
    @PreAuthorize("isAuthenticated()")
    public Profile updateProfile(Principal principal, @RequestBody Profile profile){
        int userId=getUserId(principal);

        // use the shoppingCartService to get all items in the cart and return the cart
        Profile updatedProfile=profileService.updateProfile(userId, profile);
        if (profile == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        return profile;
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
