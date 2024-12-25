package com.stock.service;

import java.util.concurrent.ExecutionException;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.cloud.FirestoreClient;
import com.stock.entity.User;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
	
	 private static final String COLLECTION_NAME = "users";


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        try {
        	Firestore dbFirestore = FirestoreClient.getFirestore();
	        DocumentReference documentReference = dbFirestore.collection(COLLECTION_NAME).document(email);
	        ApiFuture<com.google.cloud.firestore.DocumentSnapshot> future = documentReference.get();
	        com.google.cloud.firestore.DocumentSnapshot document = future.get();
	        User user = null;
	        if (document.exists()) {
	            user = document.toObject(User.class);
	        }
	        return user;
        } catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
    }
}