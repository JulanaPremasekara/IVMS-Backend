//package org.example.ivms.repository;
//
//import org.example.ivms.entity.Product;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.stereotype.Repository;
//
//import java.util.List;
//
//@Repository
//public interface ProductRepository extends JpaRepository<Product, Long> {
//
//    List<Product> findBySkuContainingIgnoreCaseOrNameContainingIgnoreCase(
//            String sku, String name);
//}