package com.example.java_cine_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class JavaCineApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(JavaCineApiApplication.class, args);
		System.out.println("\n🎬 Java Cine API démarrée avec succès !");
		System.out.println("📍 Accès : http://localhost:8080");
	}

}
