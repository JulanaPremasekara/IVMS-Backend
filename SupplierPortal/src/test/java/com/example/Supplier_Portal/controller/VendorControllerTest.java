package com.example.Supplier_Portal.controller;

import com.example.Supplier_Portal.model.Vendor;
import com.example.Supplier_Portal.service.VendorService;
import com.example.Supplier_Portal.repository.VendorRepository;
import com.example.Supplier_Portal.dto.VendorRequest;


import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(VendorController.class)
public class VendorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VendorService vendorService;

    @MockBean
    private VendorRepository vendorRepository;

    @Autowired
    private ObjectMapper objectMapper;

    // Register Vendor
    void testRegisterVendor() throws Exception {

        String requestJson = """
    {
        "name": "ABC Vendor",
        "email": "abc@gmail.com",
        "address": "Colombo",
        "phone": "0771234567",
        "companyName": "ABC Pvt Ltd",
        "contactPerson": "John Doe",
        "taxId": "TAX123",
        "sku": "SKU001",
        "startDate": "2024-01-01",
        "endDate": "2025-01-01",
        "paymentTerms": "30 days"
    }
    """;


        Map<String, Object> response = new HashMap<>();
        response.put("message", "Vendor Registered");

        Mockito.when(vendorService.registerVendor(Mockito.any(VendorRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/v1/vendors/register")
                        .contentType("application/json")
                        .content(requestJson))
                .andExpect(status().isCreated());
    }


    //Get All Vendors
    @Test
    void testGetAllVendors() throws Exception {

        Mockito.when(vendorService.getAllVendors())
                .thenReturn(new ArrayList<>());

        mockMvc.perform(get("/api/v1/vendors"))
                .andExpect(status().isOk());
    }

    //Vendor Stats
    @Test
    void testGetVendorStats() throws Exception {

        Mockito.when(vendorRepository.countByStatus("APPROVED")).thenReturn(5L);
        Mockito.when(vendorRepository.countByStatus("PENDING")).thenReturn(2L);
        Mockito.when(vendorRepository.countByStatus("BLACKLISTED")).thenReturn(1L);

        mockMvc.perform(get("/api/v1/vendors/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(5))
                .andExpect(jsonPath("$.pending").value(2))
                .andExpect(jsonPath("$.blacklisted").value(1));
    }

    //Approve Vendor
    @Test
    void testApproveVendor() throws Exception {

        mockMvc.perform(patch("/api/v1/vendors/1/approve"))
                .andExpect(status().isOk());

        Mockito.verify(vendorService).approveVendor(1L);
    }

    //Update Vendor
    void testUpdateVendor() throws Exception {

        String requestJson = """
    {
        "name": "ABC Vendor",
        "email": "abc@gmail.com",
        "address": "Colombo",
        "phone": "0771234567",
        "companyName": "ABC Pvt Ltd",
        "contactPerson": "John Doe",
        "taxId": "TAX123",
        "sku": "SKU001",
        "startDate": "2024-01-01",
        "endDate": "2025-01-01",
        "paymentTerms": "30 days"
    }
    """;


        mockMvc.perform(put("/api/v1/vendors/1")
                        .contentType("application/json")
                        .content(requestJson))
                .andExpect(status().isOk());

        Mockito.verify(vendorService)
                .updateVendor(Mockito.eq(1L), Mockito.any());
    }

    //Blacklist Vendor
    @Test
    void testBlacklistVendor() throws Exception {

        String requestJson = """
                {
                    "reason": "Bad performance"
                }
                """;

        mockMvc.perform(patch("/api/v1/vendors/1/blacklist")
                        .contentType("application/json")
                        .content(requestJson))
                .andExpect(status().isOk());

        Mockito.verify(vendorService)
                .blacklistVendor(1L, "Bad performance");
    }

    //Get Active Vendors
    @Test
    void testGetActiveVendors() throws Exception {

        Mockito.when(vendorService.getActiveVendors())
                .thenReturn(new ArrayList<>());

        mockMvc.perform(get("/api/v1/vendors/active"))
                .andExpect(status().isOk());
    }

    //Get Vendor By ID
    @Test
    void testGetVendorById() throws Exception {

        Mockito.when(vendorService.getVendorById(1L))
                .thenReturn(new Vendor());

        mockMvc.perform(get("/api/v1/vendors/1"))
                .andExpect(status().isOk());
    }

    //9. Search Vendors
    @Test
    void testSearchVendors() throws Exception {

        Mockito.when(vendorService.searchActiveVendors("abc"))
                .thenReturn(new ArrayList<>());

        mockMvc.perform(get("/api/v1/vendors/search")
                        .param("query", "abc"))
                .andExpect(status().isOk());
    }
}
