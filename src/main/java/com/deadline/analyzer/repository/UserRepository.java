package com.deadline.analyzer.repository;

import com.deadline.analyzer.model.User;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QuerySnapshot;
import com.deadline.analyzer.util.Constants;
import org.springframework.stereotype.Repository;
import java.util.concurrent.ExecutionException;

@Repository
public class UserRepository {

    private final Firestore firestore;

    public UserRepository(Firestore firestore) {
        this.firestore = firestore;
    }

    public User save(User user) throws ExecutionException, InterruptedException {
        firestore.collection(Constants.FirestoreCollections.USERS)
                .document(user.getUserId())
                .set(user)
                .get();
        return user;
    }

    public User findById(String userId) {
        try {
            return firestore.collection(Constants.FirestoreCollections.USERS)
                    .document(userId)
                    .get()
                    .get()
                    .toObject(User.class);
        } catch (Exception e) {
            return null;
        }
    }

    public User findByEmail(String email) {
        try {
            QuerySnapshot querySnapshot = firestore.collection(Constants.FirestoreCollections.USERS)
                    .whereEqualTo("email", email)
                    .get()
                    .get();

            if (!querySnapshot.isEmpty()) {
                return querySnapshot.getDocuments().get(0).toObject(User.class);
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    public User update(User user) throws ExecutionException, InterruptedException {
        firestore.collection(Constants.FirestoreCollections.USERS)
                .document(user.getUserId())
                .update(
                        "name", user.getName(),
                        "personalityType", user.getPersonalityType(),
                        "profileImageUrl", user.getProfileImageUrl()
                )
                .get();
        return user;
    }

    public boolean existsByEmail(String email) {
        try {
            QuerySnapshot querySnapshot = firestore.collection(Constants.FirestoreCollections.USERS)
                    .whereEqualTo("email", email)
                    .get()
                    .get();
            return !querySnapshot.isEmpty();
        } catch (Exception e) {
            return false;
        }
    }
}