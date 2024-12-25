package com.stock;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;



@SpringBootApplication
public class StockAnalysisAplication {

	public static void main(String[] args){

//		ClassLoader classLoader = StockAnalysisAplication.class.getClassLoader();
//
//		File file = new File(Objects.requireNonNull(classLoader.getResource("serviceAccountKey.json")).getFile());
//		FileInputStream serviceAccount = new FileInputStream(file.getAbsolutePath());
//
//		FirebaseOptions options = new FirebaseOptions.Builder()
//				.setCredentials(GoogleCredentials.fromStream(serviceAccount)).build();
//
//		FirebaseApp.initializeApp(options);

		SpringApplication.run(StockAnalysisAplication.class, args);
	}

}
