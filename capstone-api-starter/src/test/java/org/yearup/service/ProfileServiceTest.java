package org.yearup.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.yearup.models.Profile;
import org.yearup.repository.ProfileRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProfileServiceTest {

    @Mock
    private ProfileRepository profileRepository;

    @InjectMocks
    private ProfileService profileService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void create_shouldReturnSavedProfile() {
        Profile profile = new Profile();
        profile.setUserId(1);

        when(profileRepository.save(profile)).thenReturn(profile);

        Profile result = profileService.create(profile);

        assertNotNull(result);
        assertEquals(1, result.getUserId());
        verify(profileRepository, times(1)).save(profile);
    }

    @Test
    void getProfile_shouldReturnProfile_whenExists() {
        Profile profile = new Profile();
        profile.setUserId(1);

        when(profileRepository.findById(1)).thenReturn(Optional.of(profile));

        Profile result = profileService.getProfile(1);

        assertNotNull(result);
        assertEquals(1, result.getUserId());
        verify(profileRepository, times(1)).findById(1);
    }

    @Test
    void getProfile_shouldReturnNull_whenNotFound() {
        when(profileRepository.findById(1)).thenReturn(Optional.empty());

        Profile result = profileService.getProfile(1);

        assertNull(result);
        verify(profileRepository, times(1)).findById(1);
    }

    @Test
    void updateProfile_shouldReturnNull_whenProfileNotFound() {
        Profile profile = new Profile();
        profile.setUserId(1);

        when(profileRepository.findById(1)).thenReturn(Optional.empty());

        Profile result = profileService.updateProfile(1, profile);

        assertNull(result);
        verify(profileRepository).findById(1);
        verify(profileRepository, never()).save(any());
    }
}