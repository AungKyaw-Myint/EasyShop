package org.yearup.service;

import org.springframework.stereotype.Service;
import org.yearup.models.Profile;
import org.yearup.repository.ProfileRepository;

@Service
public class ProfileService
{
    private final ProfileRepository profileRepository;

    public ProfileService(ProfileRepository profileRepository)
    {
        this.profileRepository = profileRepository;
    }

    public Profile create(Profile profile)
    {
        return profileRepository.save(profile);
    }

    public Profile getProfile(int userId){
        return profileRepository.findById(userId).orElse(null);
    }

    public Profile updateProfile(int userId, Profile profile){

        Profile existingProfile= profileRepository.findById(userId).orElse(null);

        if(existingProfile != null){
            profile.setUserId(existingProfile.getUserId());

            return profileRepository.save(profile);
        }

        return null;
    }
}
