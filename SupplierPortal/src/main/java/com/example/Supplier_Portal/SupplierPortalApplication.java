package com.example.Supplier_Portal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableDiscoveryClient
@SpringBootApplication
@EntityScan("com.example.Supplier_Portal.model")
@EnableJpaRepositories("com.example.Supplier_Portal.repository")
public class SupplierPortalApplication {

	public static void main(String[] args) {
		SpringApplication.run(SupplierPortalApplication.class, args);
	}

}