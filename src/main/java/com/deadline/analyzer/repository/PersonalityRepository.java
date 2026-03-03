package com.deadline.analyzer.repository;

import com.deadline.analyzer.model.PersonalityProfile;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QuerySnapshot;
import com.deadline.analyzer.util.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

@Repository
public class PersonalityRepository {

    @Autowired
    private Firestore firestore;

    public PersonalityProfile save(PersonalityProfile profile) throws ExecutionException, InterruptedException {
        String profileId = UUID.randomUUID().toString();
        profile.setProfileId(profileId);

        firestore.collection(Constants.FirestoreCollections.PERSONALITY_PROFILES)
                .document(profileId)
                .set(profile)
                .get();
        return profile;
    }

    public PersonalityProfile findLatestByUserId(String userId) {
        try {
            QuerySnapshot querySnapshot = firestore.collection(Constants.FirestoreCollections.PERSONALITY_PROFILES)
                    .whereEqualTo("userId", userId)
                    .orderBy("classificationDate")
                    .get()
                    .get();

            if (!querySnapshot.isEmpty()) {
                return querySnapshot.getDocuments()
                        .get(querySnapshot.size() - 1)
                        .toObject(PersonalityProfile.class);
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }
}