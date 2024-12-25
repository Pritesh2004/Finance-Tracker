package com.stock.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Configuration
public class FirebaseConfiguration {

    @Value("${firebase.private_key}")
    private String privateKey;

    @Value("${firebase.client_email}")
    private String clientEmail;

    @Value("${firebase.project_id}")
    private String projectId;

    @Value("${firebase.auth_uri}")
    private String authUri;

    @Value("${firebase.token_uri}")
    private String tokenUri;

    @Value("${firebase.auth_provider_x509_cert_url}")
    private String authProviderX509CertUrl;

    @Value("${firebase.client_x509_cert_url}")
    private String clientX509CertUrl;

    @Value("${firebase.universe_domain}")
    private String universeDomain;

    @Bean
    public FirebaseApp firebaseApp() throws IOException {
        if (FirebaseApp.getApps().isEmpty()) {
            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(getCredentialsStream()))
                    .setDatabaseUrl("https://" + projectId + ".firebaseio.com") // Set the database URL if needed
                    .build();

            return FirebaseApp.initializeApp(options);
        } else {
            return FirebaseApp.getInstance();
        }
    }

    private InputStream getCredentialsStream() throws IOException {
        // Use the properties to create the JSON format expected by GoogleCredentials
        String json = String.format(
                "{ \"type\": \"service_account\", \"project_id\": \"%s\", \"private_key_id\": \"%s\", \"private_key\": \"%s\", \"client_email\": \"%s\", \"client_id\": \"%s\", \"auth_uri\": \"%s\", \"token_uri\": \"%s\", \"auth_provider_x509_cert_url\": \"%s\", \"client_x509_cert_url\": \"%s\", \"universe_domain\": \"%s\" }",
                projectId, "your-private-key-id", privateKey, clientEmail, "your-client-id",
                authUri, tokenUri, authProviderX509CertUrl, clientX509CertUrl, universeDomain
        );
        return new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8));
    }
}
