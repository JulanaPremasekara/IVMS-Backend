package com.example.Supplier_Portal.service;

import com.example.Supplier_Portal.dto.VendorRequest;
import com.example.Supplier_Portal.model.Vendor;
import com.example.Supplier_Portal.repository.VendorRepository;
import com.lowagie.text.pdf.PdfContentByte;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfWriter;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.awt.Color;

@Service
@RequiredArgsConstructor
public class VendorServiceImpl implements VendorService {

    private final VendorRepository vendorRepository;

    @Override
    @Transactional
    public Map<String, Object> registerVendor(VendorRequest request) {
        // The Controller @Valid check ensures 'request' is already validated here
        Vendor vendor = Vendor.builder()
                .companyName(request.getCompanyName())
                .taxId(request.getTaxId())
                .address(request.getAddress())
                .contactPerson(request.getContactPerson())
                .email(request.getEmail())
                .phone(request.getPhone())
                .paymentTerms(request.getPaymentTerms())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .sku(request.getSku())
                .unitPrice(request.getUnitPrice()) // Mapping the validated unit price
                .status("PENDING")
                //INITIALIZING ANALYTICS BASELINE
                .performanceScore(0.0)
                .avgLeadTime(0.0)
                .delayProbability(0)
                .reliabilityRating(0.0)
                .totalOrders(0)
                .build();

        Vendor savedVendor = vendorRepository.save(vendor);

        Map<String, Object> response = new HashMap<>();
        response.put("id", savedVendor.getId());
        response.put("message", "Vendor registered successfully. Awaiting approval.");
        response.put("status", 201);

        return response;
    }

    @Override
    @Transactional
    public void approveVendor(Long id) {
        Vendor vendor = vendorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vendor not found"));

        vendor.setStatus("APPROVED");
        vendor.setBlacklistReason(null);

        if (vendor.getTotalOrders() == null) {
            vendor.setTotalOrders(0);
            vendor.setPerformanceScore(0.0);
            vendor.setAvgLeadTime(0.0);
            vendor.setDelayProbability(0);
            vendor.setReliabilityRating(0.0);
        }

        vendorRepository.save(vendor);
    }

    @Override
    public List<Vendor> getAllVendors() {
        return vendorRepository.findAllByOrderByIdDesc();
    }

    @Override
    @Transactional
    public void blacklistVendor(Long id, String reason) {
        Vendor vendor = vendorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vendor not found with id: " + id));

        vendor.setStatus("BLACKLISTED");
        vendor.setBlacklistReason(reason);
        vendorRepository.save(vendor);
    }

    @Override
    @Transactional
    public void updateVendor(Long id, VendorRequest request) {
        Vendor vendor = vendorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vendor not found with id: " + id));

        // Update fields with validated data from request
        vendor.setCompanyName(request.getCompanyName());
        vendor.setTaxId(request.getTaxId());
        vendor.setAddress(request.getAddress());
        vendor.setContactPerson(request.getContactPerson());
        vendor.setEmail(request.getEmail());
        vendor.setPhone(request.getPhone());
        vendor.setPaymentTerms(request.getPaymentTerms());
        vendor.setStartDate(request.getStartDate());
        vendor.setEndDate(request.getEndDate());

        if (request.getSku() != null) vendor.setSku(request.getSku());
        if (request.getUnitPrice() != null) vendor.setUnitPrice(request.getUnitPrice());

        vendorRepository.save(vendor);
    }

    @Override
    public List<Vendor> getActiveVendors() {
        return vendorRepository.findByStatusOrderByCompanyNameAsc("APPROVED");
    }

    @Override
    public Vendor getVendorById(Long id) {
        return vendorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vendor not found with id: " + id));
    }

    @Override
    public List<Vendor> searchActiveVendors(String query) {
        return vendorRepository.findByCompanyNameContainingIgnoreCaseAndStatus(query, "APPROVED");
    }

    //export report
    @Override
    public void generateVendorPdf(Long id, HttpServletResponse response) throws IOException {
        Vendor vendor = vendorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vendor not found"));

        Document document = new Document(PageSize.A4,36, 36, 36, 36);

        PdfWriter writer = PdfWriter.getInstance(document, response.getOutputStream());
        PdfWriter.getInstance(document, response.getOutputStream());

        document.open();

        //frame
        PdfContentByte canvas = writer.getDirectContent();
        Rectangle rect = new Rectangle(document.getPageSize());
        rect.setBorder(Rectangle.BOX);
        rect.setBorderWidth(2);
        rect.setBorderColor(Color.LIGHT_GRAY);
        rect.setUseVariableBorders(true);
        //adjust rectangle
        rect.setLeft(20);
        rect.setBottom(20);
        rect.setRight(document.getPageSize().getRight() - 20);
        rect.setTop(document.getPageSize().getTop() - 20);
        canvas.rectangle(rect);

        // Fonts & Colors
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, Color.DARK_GRAY);
        Font subTitleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Color.GRAY);
        Font bodyFont = FontFactory.getFont(FontFactory.HELVETICA, 10);
        //Font boldBody = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);

        //Title
        Paragraph title = new Paragraph("VENDOR ANALYTICS REPORT", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(20);
        document.add(title);

        //Vendor Overview Section
        document.add(new Paragraph("VENDOR OVERVIEW", subTitleFont));
        document.add(new Paragraph("----------------------------------------------------------------------------------------------------------------------------------"));

        document.add(new Paragraph("Company Name: " + vendor.getCompanyName(), bodyFont));
        document.add(new Paragraph("Vendor ID: VEN-" + vendor.getId(), bodyFont));
        document.add(new Paragraph("Contact Person: " + vendor.getContactPerson(), bodyFont));
        document.add(new Paragraph("Email: " + vendor.getEmail(), bodyFont));
        document.add(new Paragraph("Payment Terms: " + (vendor.getPaymentTerms() != null ? vendor.getPaymentTerms() : "N/A"), bodyFont));
        document.add(new Paragraph("Status: " + vendor.getStatus(), bodyFont));

        //AI Performance Section
        Paragraph perfHeader = new Paragraph("\nAI-GENERATED PERFORMANCE SUMMARY", subTitleFont);
        perfHeader.setSpacingBefore(10);
        document.add(perfHeader);
        document.add(new Paragraph("----------------------------------------------------------------------------------------------------------------------------------"));

        //list
        document.add(new Paragraph("Reliability Rating: " + String.format("%.1f", vendor.getReliabilityRating()) + " / 5.0", bodyFont));
        document.add(new Paragraph("Vendor Score (AI): " + vendor.getPerformanceScore() + "%", bodyFont));
        document.add(new Paragraph("Average Lead Time: " + String.format("%.1f", vendor.getAvgLeadTime()) + " Days", bodyFont));
        document.add(new Paragraph("Delay Probability: " + vendor.getDelayProbability() + "%", bodyFont));
        document.add(new Paragraph("Total Orders Processed: " + vendor.getTotalOrders(), bodyFont));

        //Transaction History
        document.add(new Paragraph("\n\nTransactional History", subTitleFont));
        document.add(new Paragraph("----------------------------------------------------------------------------------------------------------------------------------"));

        //Footer
        Paragraph footer = new Paragraph("\n\nReport generated by Cognitive IVMS on " + java.time.LocalDate.now(),
                FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 8));
        footer.setAlignment(Element.ALIGN_BOTTOM);
        document.add(footer);

        document.close();
    }
}