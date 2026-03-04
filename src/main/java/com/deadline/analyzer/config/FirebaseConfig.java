package com.deadline.analyzer.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;

@Configuration
public class FirebaseConfig {

    private static final Logger logger = Logger.getLogger(FirebaseConfig.class.getName());

    @Value("${firebase.credentials.path:serviceAccountKey.json}")
    private String credentialsPath;

    @Bean
    public FirebaseApp firebaseApp() throws IOException {
        if (FirebaseApp.getApps().isEmpty()) {
            try {
                InputStream serviceAccountStream = getServiceAccountStream();

                FirebaseOptions options = new FirebaseOptions.Builder()
                        .setCredentials(GoogleCredentials.fromStream(serviceAccountStream))
                        .build();

                FirebaseApp.initializeApp(options);
                logger.info("Firebase initialized successfully");
            } catch (IOException e) {
                logger.severe("Failed to initialize Firebase: " + e.getMessage());
                throw new RuntimeException("Firebase initialization failed", e);
            }
        }
        return FirebaseApp.getInstance();
    }

    @Bean
    public Firestore firestore() throws IOException {
        return FirestoreClient.getFirestore(firebaseApp());
    }

    @Bean
    public FirebaseAuth firebaseAuth() throws IOException {
        return FirebaseAuth.getInstance(firebaseApp());
    }

    private InputStream getServiceAccountStream() throws IOException {
        // Try to load from classpath first (for packaged app)
        try {
            Resource resource = new ClassPathResource(credentialsPath);
            if (resource.exists()) {
                return resource.getInputStream();
            }
        } catch (Exception e) {
            logger.warning("Could not load from classpath: " + e.getMessage());
        }

        // Try to load from filesystem (for local development)
        try {
            Resource resource = new FileSystemResource(credentialsPath);
            if (resource.exists()) {
                return resource.getInputStream();
            }
        } catch (Exception e) {
            logger.warning("Could not load from filesystem: " + e.getMessage());
        }

        throw new IOException("Cannot find Firebase credentials at: " + credentialsPath);
    }
}