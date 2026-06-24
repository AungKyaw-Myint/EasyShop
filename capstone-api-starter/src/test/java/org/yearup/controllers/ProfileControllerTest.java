package org.yearup.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.yearup.models.Profile;
import org.yearup.models.User;
import org.yearup.service.ProfileService;
import org.yearup.service.UserService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static java.lang.reflect.Array.get;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProfileController.class)
class ProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProfileService profileService;

    @MockitoBean
    private UserService userService;

    // ---------------- GET PROFILE SUCCESS ----------------

    @Test
    void getProfile_success() throws Exception {

        User user = new User();
        user.setId(1);
        user.setUsername("admin");

        Profile profile = new Profile();
        profile.setFirstName("John");
        profile.setLastName("Doe");

        when(userService.getByUserName("admin")).thenReturn(user);
        when(profileService.getProfile(1)).thenReturn(profile);

        mockMvc.perform(get("/profile")
                        .principal(() -> "admin"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"));
    }

    // ---------------- GET PROFILE NOT FOUND ----------------

    @Test
    void getProfile_notFound() throws Exception {

        User user = new User();
        user.setId(1);
        user.setUsername("john");

        when(userService.getByUserName("john")).thenReturn(user);
        when(profileService.getProfile(1)).thenReturn(null);

        mockMvc.perform(get("/profile")
                        .principal(() -> "john"))
                .andExpect(status().isNotFound());
    }
}