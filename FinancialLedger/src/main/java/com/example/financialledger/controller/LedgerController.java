package com.example.financialledger.controller;

import com.example.financialledger.dto.PaymentDetailsDTO;
import com.example.financialledger.dto.TransactionDTO;
import com.example.financialledger.model.FinancialTransaction;
import com.example.financialledger.service.ExportService;
import com.example.financialledger.service.FinancialService;
import com.example.financialledger.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ledger")
@CrossOrigin(origins = "http://localhost:3000")
public class LedgerController {

        @Autowired
        private FinancialService service;

        @GetMapping("/transactions")
        public ResponseEntity<List<TransactionDTO>> getTransactions() {
                List<TransactionDTO> dtos = service.getAllTransactions().stream()
                                .map(tx -> {
                                        String fiscal = tx.getFiscalPeriod() != null
                                                        ? tx.getFiscalPeriod().getStartDate().getMonth().name()
                                                                        .substring(0, 3) + " " +
                                                                        tx.getFiscalPeriod().getStartDate().getYear()
                                                        : "N/A";
                                        boolean locked = tx.getFiscalPeriod() != null
                                                        && tx.getFiscalPeriod().isLocked();
                                        String stock = tx.getFiscalPeriod() != null
                                                        ? tx.getFiscalPeriod().getStockStatus()
                                                        : "N/A";

                                        return new TransactionDTO(
                                                        tx.getId(),
                                                        tx.getOrderId(),
                                                        tx.getType().name(),
                                                        tx.getPartyName(),
                                                        tx.getAmount(),
                                                        tx.getTax(),
                                                        tx.getTotal(),
                                                        tx.getStatus().name(),
                                                        tx.getCreatedAt(),
                                                        tx.getCategory(),
                                                        fiscal,
                                                        locked,
                                                        stock);
                                })
                                .toList();
                return ResponseEntity.ok(dtos);
        }

        @GetMapping("/summary")
        public ResponseEntity<Map<String, BigDecimal>> getSummary() {
                Map<String, BigDecimal> summary = Map.of(
                                "revenue", service.getTotalRevenue(),
                                "expenses", service.getTotalExpenses(),
                                "outstanding", service.getOutstandingAmount());
                return ResponseEntity.ok(summary);
        }

        @Autowired
        private PaymentService paymentService;

        @GetMapping("/payments/{id}")
        public ResponseEntity<List<PaymentDetailsDTO>> getPaymentHistory(
                        @PathVariable String id) {

                return ResponseEntity.ok(paymentService.getPaymentDetails(id));
        }

        @GetMapping("/payments")
        public ResponseEntity<List<PaymentDetailsDTO>> getAllPayments() {
                return ResponseEntity.ok(paymentService.getAllPaymentDetails());
        }

        @PostMapping("/payments")
        public ResponseEntity<String> savePayment(
                        @RequestBody com.example.financialledger.model.PaymentHistory payment) {
                paymentService.savePayment(payment);
                return ResponseEntity.ok("Payment saved successfully");
        }

        @DeleteMapping("/transactions/{id}")
        public ResponseEntity<Void> deleteTransaction(@PathVariable String id) {
                service.deleteTransaction(id);
                return ResponseEntity.noContent().build();
        }

        @Autowired
        private ExportService exportService; // 1. Inject the service here

        @GetMapping("/export/excel")
        public ResponseEntity<byte[]> exportToExcel() {
                try {
                        List<FinancialTransaction> data = service.getAllTransactions();

                        // Calculate detailed metrics for the Ledger Automation Page
                        BigDecimal salesPaid = data.stream()
                                        .filter(t -> t
                                                        .getType() == com.example.financialledger.model.FinancialTransaction.TransactionType.Sale)
                                        .filter(t -> t.getStatus() == com.example.financialledger.model.FinancialTransaction.Status.Paid)
                                        .map(t -> t.getTotal() != null ? t.getTotal() : BigDecimal.ZERO)
                                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                        BigDecimal salesOutstanding = data.stream()
                                        .filter(t -> t
                                                        .getType() == com.example.financialledger.model.FinancialTransaction.TransactionType.Sale)
                                        .filter(t -> t.getStatus() != com.example.financialledger.model.FinancialTransaction.Status.Paid)
                                        .map(t -> t.getTotal() != null ? t.getTotal() : BigDecimal.ZERO)
                                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                        BigDecimal purchasePaid = data.stream()
                                        .filter(t -> t
                                                        .getType() == com.example.financialledger.model.FinancialTransaction.TransactionType.Purchase)
                                        .filter(t -> t.getStatus() == com.example.financialledger.model.FinancialTransaction.Status.Paid)
                                        .map(t -> t.getTotal() != null ? t.getTotal() : BigDecimal.ZERO)
                                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                        BigDecimal purchaseOutstanding = data.stream()
                                        .filter(t -> t
                                                        .getType() == com.example.financialledger.model.FinancialTransaction.TransactionType.Purchase)
                                        .filter(t -> t.getStatus() != com.example.financialledger.model.FinancialTransaction.Status.Paid)
                                        .map(t -> t.getTotal() != null ? t.getTotal() : BigDecimal.ZERO)
                                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                        byte[] excelContent = exportService.generateLedgerExcel(data, salesPaid, salesOutstanding,
                                        purchasePaid,
                                        purchaseOutstanding);

                        return ResponseEntity.ok()
                                        .header(HttpHeaders.CONTENT_DISPOSITION,
                                                        "attachment; filename=\"FinancialReport.xlsx\"")
                                        .contentType(MediaType.APPLICATION_OCTET_STREAM)
                                        .body(excelContent);

                } catch (Exception e) {
                        e.printStackTrace(); // THIS IS CRITICAL: This will print the error to your console
                        return ResponseEntity.internalServerError().build();
                }
        }

        @GetMapping("/export/pdf")
        public ResponseEntity<byte[]> exportToPdf() {
                try {
                        List<FinancialTransaction> data = service.getAllTransactions();

                        // Calculate detailed metrics for the Ledger Automation Page
                        BigDecimal salesPaid = data.stream()
                                        .filter(t -> t
                                                        .getType() == com.example.financialledger.model.FinancialTransaction.TransactionType.Sale)
                                        .filter(t -> t.getStatus() == com.example.financialledger.model.FinancialTransaction.Status.Paid)
                                        .map(t -> t.getTotal() != null ? t.getTotal() : BigDecimal.ZERO)
                                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                        BigDecimal salesOutstanding = data.stream()
                                        .filter(t -> t
                                                        .getType() == com.example.financialledger.model.FinancialTransaction.TransactionType.Sale)
                                        .filter(t -> t.getStatus() != com.example.financialledger.model.FinancialTransaction.Status.Paid)
                                        .map(t -> t.getTotal() != null ? t.getTotal() : BigDecimal.ZERO)
                                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                        BigDecimal purchasePaid = data.stream()
                                        .filter(t -> t
                                                        .getType() == com.example.financialledger.model.FinancialTransaction.TransactionType.Purchase)
                                        .filter(t -> t.getStatus() == com.example.financialledger.model.FinancialTransaction.Status.Paid)
                                        .map(t -> t.getTotal() != null ? t.getTotal() : BigDecimal.ZERO)
                                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                        BigDecimal purchaseOutstanding = data.stream()
                                        .filter(t -> t
                                                        .getType() == com.example.financialledger.model.FinancialTransaction.TransactionType.Purchase)
                                        .filter(t -> t.getStatus() != com.example.financialledger.model.FinancialTransaction.Status.Paid)
                                        .map(t -> t.getTotal() != null ? t.getTotal() : BigDecimal.ZERO)
                                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                        byte[] pdfContent = exportService.generateLedgerPdf(data, salesPaid, salesOutstanding,
                                        purchasePaid,
                                        purchaseOutstanding);

                        return ResponseEntity.ok()
                                        .header(HttpHeaders.CONTENT_DISPOSITION,
                                                        "attachment; filename=\"FinancialReport.pdf\"")
                                        .contentType(MediaType.APPLICATION_PDF)
                                        .body(pdfContent);

                } catch (Exception e) {
                        e.printStackTrace();
                        return ResponseEntity.internalServerError().build();
                }
        }

        @GetMapping("/export/report/excel")
        public ResponseEntity<byte[]> exportReportToExcel() {
                try {
                        List<FinancialTransaction> data = service.getAllTransactions();
                        BigDecimal income = service.getTotalRevenue();
                        BigDecimal expenses = service.getTotalExpenses();
                        BigDecimal profit = income.subtract(expenses);

                        byte[] excelContent = exportService.generateReportExcel(data, income, expenses, profit);

                        return ResponseEntity.ok()
                                        .header(HttpHeaders.CONTENT_DISPOSITION,
                                                        "attachment; filename=\"Financial_Report.xlsx\"")
                                        .contentType(MediaType.APPLICATION_OCTET_STREAM)
                                        .body(excelContent);
                } catch (Exception e) {
                        e.printStackTrace();
                        return ResponseEntity.internalServerError().build();
                }
        }

        @GetMapping("/export/report/pdf")
        public ResponseEntity<byte[]> exportReportToPdf() {
                try {
                        List<FinancialTransaction> data = service.getAllTransactions();
                        BigDecimal income = service.getTotalRevenue();
                        BigDecimal expenses = service.getTotalExpenses();
                        BigDecimal profit = income.subtract(expenses);

                        byte[] pdfContent = exportService.generateReportPdf(data, income, expenses, profit);

                        return ResponseEntity.ok()
                                        .header(HttpHeaders.CONTENT_DISPOSITION,
                                                        "attachment; filename=\"Financial_Report.pdf\"")
                                        .contentType(MediaType.APPLICATION_PDF)
                                        .body(pdfContent);
                } catch (Exception e) {
                        e.printStackTrace();
                        return ResponseEntity.internalServerError().build();
                }
        }

        @GetMapping("/export/payments/excel")
        public ResponseEntity<byte[]> exportPaymentsToExcel() {
                try {
                        List<com.example.financialledger.dto.PaymentDetailsDTO> data = paymentService
                                        .getAllPaymentDetails();

                        BigDecimal totalOutstanding = data.stream()
                                        .map(com.example.financialledger.dto.PaymentDetailsDTO::remainingBalance)
                                        .filter(java.util.Objects::nonNull)
                                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                        BigDecimal totalPaid = data.stream()
                                        .map(com.example.financialledger.dto.PaymentDetailsDTO::amountPaid)
                                        .filter(java.util.Objects::nonNull)
                                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                        long overdueCount = data.stream()
                                        .filter(p -> "Overdue".equalsIgnoreCase(p.status()))
                                        .count();

                        byte[] excelContent = exportService.generatePaymentExcel(data, totalOutstanding, totalPaid,
                                        overdueCount);

                        return ResponseEntity.ok()
                                        .header(HttpHeaders.CONTENT_DISPOSITION,
                                                        "attachment; filename=\"VendorPaymentsReport.xlsx\"")
                                        .contentType(MediaType.APPLICATION_OCTET_STREAM)
                                        .body(excelContent);
                } catch (Exception e) {
                        e.printStackTrace();
                        return ResponseEntity.internalServerError().build();
                }
        }

        @GetMapping("/export/payments/pdf")
        public ResponseEntity<byte[]> exportPaymentsToPdf() {
                try {
                        List<com.example.financialledger.dto.PaymentDetailsDTO> data = paymentService
                                        .getAllPaymentDetails();

                        BigDecimal totalOutstanding = data.stream()
                                        .map(com.example.financialledger.dto.PaymentDetailsDTO::remainingBalance)
                                        .filter(java.util.Objects::nonNull)
                                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                        BigDecimal totalPaid = data.stream()
                                        .map(com.example.financialledger.dto.PaymentDetailsDTO::amountPaid)
                                        .filter(java.util.Objects::nonNull)
                                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                        long overdueCount = data.stream()
                                        .filter(p -> "Overdue".equalsIgnoreCase(p.status()))
                                        .count();

                        byte[] pdfContent = exportService.generatePaymentPdf(data, totalOutstanding, totalPaid,
                                        overdueCount);

                        return ResponseEntity.ok()
                                        .header(HttpHeaders.CONTENT_DISPOSITION,
                                                        "attachment; filename=\"VendorPaymentsReport.pdf\"")
                                        .contentType(MediaType.APPLICATION_PDF)
                                        .body(pdfContent);
                } catch (Exception e) {
                        e.printStackTrace();
                        return ResponseEntity.internalServerError().build();
                }

        }

        @GetMapping("/export/entries/excel")
        public ResponseEntity<byte[]> exportEntriesToExcel() {
                try {
                        List<FinancialTransaction> data = service.getAllTransactions();

                        long active = data.stream().filter(t -> t
                                        .getStatus() != com.example.financialledger.model.FinancialTransaction.Status.Voided)
                                        .count();
                        long voided = data.stream().filter(t -> t
                                        .getStatus() == com.example.financialledger.model.FinancialTransaction.Status.Voided)
                                        .count();
                        long locked = data.stream()
                                        .filter(t -> t.getFiscalPeriod() != null && t.getFiscalPeriod().isLocked())
                                        .count();
                        long stockLinked = data.stream()
                                        .filter(t -> t.getFiscalPeriod() != null
                                                        && t.getFiscalPeriod().getStockStatus() != null
                                                        && !t.getFiscalPeriod().getStockStatus().isEmpty())
                                        .count();

                        BigDecimal revenue = data.stream()
                                        .filter(t -> t.getType() == com.example.financialledger.model.FinancialTransaction.TransactionType.Sale
                                                        && t.getStatus() == com.example.financialledger.model.FinancialTransaction.Status.Paid)
                                        .map(t -> t.getTotal() != null ? t.getTotal() : BigDecimal.ZERO)
                                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                        BigDecimal expenses = data.stream()
                                        .filter(t -> t.getType() == com.example.financialledger.model.FinancialTransaction.TransactionType.Purchase
                                                        && t.getStatus() != com.example.financialledger.model.FinancialTransaction.Status.Voided)
                                        .map(t -> t.getTotal() != null ? t.getTotal() : BigDecimal.ZERO)
                                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                        byte[] excelContent = exportService.generateEntriesExcel(data, active, voided, locked,
                                        stockLinked, revenue, expenses);

                        return ResponseEntity.ok()
                                        .header(HttpHeaders.CONTENT_DISPOSITION,
                                                        "attachment; filename=\"Financial_Entries_Audit.xlsx\"")
                                        .contentType(MediaType.APPLICATION_OCTET_STREAM)
                                        .body(excelContent);
                } catch (Exception e) {
                        e.printStackTrace();
                        return ResponseEntity.internalServerError().build();
                }
        }

        @GetMapping("/export/entries/pdf")
        public ResponseEntity<byte[]> exportEntriesToPdf() {
                try {
                        List<FinancialTransaction> data = service.getAllTransactions();

                        long active = data.stream().filter(t -> t
                                        .getStatus() != com.example.financialledger.model.FinancialTransaction.Status.Voided)
                                        .count();
                        long voided = data.stream().filter(t -> t
                                        .getStatus() == com.example.financialledger.model.FinancialTransaction.Status.Voided)
                                        .count();
                        long locked = data.stream()
                                        .filter(t -> t.getFiscalPeriod() != null && t.getFiscalPeriod().isLocked())
                                        .count();
                        long stockLinked = data.stream()
                                        .filter(t -> t.getFiscalPeriod() != null
                                                        && t.getFiscalPeriod().getStockStatus() != null
                                                        && !t.getFiscalPeriod().getStockStatus().isEmpty())
                                        .count();

                        BigDecimal revenue = data.stream()
                                        .filter(t -> t.getType() == com.example.financialledger.model.FinancialTransaction.TransactionType.Sale
                                                        && t.getStatus() == com.example.financialledger.model.FinancialTransaction.Status.Paid)
                                        .map(t -> t.getTotal() != null ? t.getTotal() : BigDecimal.ZERO)
                                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                        BigDecimal expenses = data.stream()
                                        .filter(t -> t.getType() == com.example.financialledger.model.FinancialTransaction.TransactionType.Purchase
                                                        && t.getStatus() != com.example.financialledger.model.FinancialTransaction.Status.Voided)
                                        .map(t -> t.getTotal() != null ? t.getTotal() : BigDecimal.ZERO)
                                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                        byte[] pdfContent = exportService.generateEntriesPdf(data, active, voided, locked, stockLinked,
                                        revenue, expenses);

                        return ResponseEntity.ok()
                                        .header(HttpHeaders.CONTENT_DISPOSITION,
                                                        "attachment; filename=\"Financial_Entries_Audit.pdf\"")
                                        .contentType(MediaType.APPLICATION_PDF)
                                        .body(pdfContent);
                } catch (Exception e) {
                        e.printStackTrace();
                        return ResponseEntity.internalServerError().build();
                }
        }
}
