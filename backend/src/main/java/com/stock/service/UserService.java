package com.stock.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.cloud.FirestoreClient;
import com.stock.entity.User;


@Service
public class UserService {

	 private static final String COLLECTION_NAME = "users";
	 
	 private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();


	 public String saveUser(User user) throws ExecutionException, InterruptedException {
	        Firestore dbFirestore = FirestoreClient.getFirestore();
	        DocumentReference documentReference = dbFirestore.collection(COLLECTION_NAME).document(user.getEmail());

	        // Check if the user already exists
	        ApiFuture<com.google.cloud.firestore.DocumentSnapshot> future = documentReference.get();
	        com.google.cloud.firestore.DocumentSnapshot document = future.get();

	        if (document.exists()) {
	            return "Username already exists!";
	        } else {

                user.setPassword(encoder.encode(user.getPassword()));
	            ApiFuture<WriteResult> collectionsApiFuture = documentReference.set(user);
	            return collectionsApiFuture.get().getUpdateTime().toString();
	        }
	    }

	    public User getUser(String email) throws ExecutionException, InterruptedException {
	        Firestore dbFirestore = FirestoreClient.getFirestore();
	        DocumentReference documentReference = dbFirestore.collection(COLLECTION_NAME).document(email);
	        ApiFuture<com.google.cloud.firestore.DocumentSnapshot> future = documentReference.get();
	        com.google.cloud.firestore.DocumentSnapshot document = future.get();
	        User user = null;
	        if (document.exists()) {
	            user = document.toObject(User.class);
	        }
	        return user;
	    }

//	    public String addPrediction(String email, Prediction prediction) throws ExecutionException, InterruptedException {
//	        Firestore dbFirestore = FirestoreClient.getFirestore();
//	        DocumentReference documentReference = dbFirestore.collection(COLLECTION_NAME).document(email);
//	        ApiFuture<com.google.cloud.firestore.DocumentSnapshot> future = documentReference.get();
//	        com.google.cloud.firestore.DocumentSnapshot document = future.get();
//	        if (document.exists()) {
//	            User user = document.toObject(User.class);
//	            if (user != null) {
//	                List<Prediction> predictions = user.getPredictions();
//	                predictions.add(prediction);
//	                user.setPredictions(predictions);
//	                documentReference.set(user);
//	            }
//	        }
//	        return "Prediction added successfully";
//	    }
}
