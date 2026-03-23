package com.example.Supplier_Portal.repository;

import com.example.Supplier_Portal.model.Vendor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface VendorRepository extends JpaRepository<Vendor, Long> {

    // Used for Dashboard summary statistics
    long countByStatus(String status);

    // Used for the main Manage Vendors table
    List<Vendor> findAllByOrderByIdDesc();

    // Fetches vendors by status (Pending, Approved, or Blacklisted)
    List<Vendor> findByStatus(String status);

    // Specifically for the Analytics Sidebar and Directory Quick Select
    // Ensures the list is easy to navigate alphabetically
    List<Vendor> findByStatusOrderByCompanyNameAsc(String status);

    // Powers the Search Bar in the Analytics and Manage Vendors views
    // Filters by name while ensuring only 'APPROVED' vendors appear in analytics
    List<Vendor> findByCompanyNameContainingIgnoreCaseAndStatus(String name, String status);
}