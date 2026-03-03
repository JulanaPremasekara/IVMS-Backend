package com.example.financialledger.service;

import com.example.financialledger.model.FinancialTransaction;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@Service
public class ExportService {

        public byte[] generateLedgerExcel(List<FinancialTransaction> transactions,
                        BigDecimal salesPaid, BigDecimal salesOutstanding,
                        BigDecimal purchasePaid, BigDecimal purchaseOutstanding) throws IOException {
                // Create the workbook
                try (Workbook workbook = new XSSFWorkbook();
                                ByteArrayOutputStream out = new ByteArrayOutputStream()) {

                        if (transactions == null || transactions.isEmpty()) {
                                throw new IOException("No data available to export");
                        }

                        Sheet sheet = workbook.createSheet("Financial Report");

                        // 1. Add Summary Section (Detailed Labels)
                        Row row0 = sheet.createRow(0);
                        row0.createCell(0).setCellValue("Financial Ledger Analysis - Summary");

                        Row row1 = sheet.createRow(1);
                        row1.createCell(0).setCellValue("Sales Invoices (Accounts Receivable)");
                        sheet.createRow(2).createCell(0).setCellValue("  - Outstanding: $" + salesOutstanding);
                        sheet.createRow(3).createCell(0).setCellValue("  - Paid: $" + salesPaid);
                        sheet.createRow(4).createCell(0).setCellValue("  - Total: $" + salesPaid.add(salesOutstanding));

                        Row row6 = sheet.createRow(6);
                        row6.createCell(0).setCellValue("Purchase Expenses (Accounts Payable)");
                        sheet.createRow(7).createCell(0).setCellValue("  - Outstanding: $" + purchaseOutstanding);
                        sheet.createRow(8).createCell(0).setCellValue("  - Paid: $" + purchasePaid);
                        sheet.createRow(9).createCell(0)
                                        .setCellValue("  - Total: $" + purchasePaid.add(purchaseOutstanding));

                        // 2. Add Table Headers (10 Columns)
                        Row headerRow = sheet.createRow(11);
                        String[] columns = { "Transaction ID", "Order ID", "Type", "Party", "Category", "Amount", "Tax",
                                        "Total",
                                        "Status", "Timestamp" };
                        for (int i = 0; i < columns.length; i++)
                                headerRow.createCell(i).setCellValue(columns[i]);

                        // 3. Populate Table Data
                        int rowIdx = 12;
                        for (FinancialTransaction t : transactions) {
                                Row r = sheet.createRow(rowIdx++);
                                r.createCell(0).setCellValue(t.getId());
                                r.createCell(1).setCellValue(t.getOrderId() != null ? t.getOrderId() : "");
                                r.createCell(2).setCellValue(t.getType() != null ? t.getType().toString() : "");
                                r.createCell(3).setCellValue(t.getPartyName() != null ? t.getPartyName() : "");
                                r.createCell(4).setCellValue(t.getCategory() != null ? t.getCategory() : "N/A");
                                r.createCell(5).setCellValue(t.getAmount() != null ? t.getAmount().doubleValue() : 0.0);
                                r.createCell(6).setCellValue(t.getTax() != null ? t.getTax().doubleValue() : 0.0);
                                r.createCell(7).setCellValue(t.getTotal() != null ? t.getTotal().doubleValue() : 0.0);
                                r.createCell(8).setCellValue(t.getStatus() != null ? t.getStatus().toString() : "");
                                r.createCell(9).setCellValue(
                                                t.getCreatedAt() != null ? t.getCreatedAt().toString() : "N/A");
                        }

                        for (int i = 0; i < columns.length; i++) {
                                sheet.autoSizeColumn(i);
                        }

                        workbook.write(out);
                        out.flush();
                        return out.toByteArray();
                }
        }

        public byte[] generateLedgerPdf(List<FinancialTransaction> transactions,
                        BigDecimal salesPaid, BigDecimal salesOutstanding,
                        BigDecimal purchasePaid, BigDecimal purchaseOutstanding) throws IOException {
                try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                        // Use Landscape for 10 columns
                        com.lowagie.text.Document document = new com.lowagie.text.Document(
                                        com.lowagie.text.PageSize.A4.rotate());
                        com.lowagie.text.pdf.PdfWriter.getInstance(document, out);

                        document.open();

                        // Fonts
                        com.lowagie.text.Font titleFont = com.lowagie.text.FontFactory
                                        .getFont(com.lowagie.text.FontFactory.HELVETICA_BOLD, 18,
                                                        java.awt.Color.BLUE.darker());
                        com.lowagie.text.Font headerFont = com.lowagie.text.FontFactory
                                        .getFont(com.lowagie.text.FontFactory.HELVETICA_BOLD, 10);
                        com.lowagie.text.Font normalFont = com.lowagie.text.FontFactory
                                        .getFont(com.lowagie.text.FontFactory.HELVETICA, 8);

                        // Title
                        com.lowagie.text.Paragraph title = new com.lowagie.text.Paragraph(
                                        "Financial Ledger Automation Report",
                                        titleFont);
                        title.setAlignment(com.lowagie.text.Element.ALIGN_CENTER);
                        document.add(title);
                        document.add(new com.lowagie.text.Paragraph(" ")); // Spacer

                        // Detailed Summary Section
                        document.add(new com.lowagie.text.Paragraph("1. Sales Invoices (Accounts Receivable)",
                                        headerFont));
                        document.add(new com.lowagie.text.Paragraph("   - Outstanding: $" + salesOutstanding,
                                        normalFont));
                        document.add(new com.lowagie.text.Paragraph("   - Paid: $" + salesPaid, normalFont));
                        document.add(new com.lowagie.text.Paragraph(
                                        "   - Total Sales: $" + salesPaid.add(salesOutstanding), normalFont));
                        document.add(new com.lowagie.text.Paragraph(" "));

                        document.add(new com.lowagie.text.Paragraph("2. Purchase Expenses (Accounts Payable)",
                                        headerFont));
                        document.add(new com.lowagie.text.Paragraph("   - Outstanding: $" + purchaseOutstanding,
                                        normalFont));
                        document.add(new com.lowagie.text.Paragraph("   - Paid: $" + purchasePaid, normalFont));
                        document.add(new com.lowagie.text.Paragraph(
                                        "   - Total Expenses: $" + purchasePaid.add(purchaseOutstanding),
                                        normalFont));
                        document.add(new com.lowagie.text.Paragraph(" "));

                        // Table with 10 columns
                        com.lowagie.text.pdf.PdfPTable table = new com.lowagie.text.pdf.PdfPTable(10);
                        table.setWidthPercentage(100);
                        table.setSpacingBefore(10f);

                        String[] headers = { "ID", "Order ID", "Type", "Party", "Category", "Base Amt", "Tax", "Total",
                                        "Status",
                                        "Timestamp" };
                        for (String h : headers) {
                                com.lowagie.text.pdf.PdfPCell cell = new com.lowagie.text.pdf.PdfPCell(
                                                new com.lowagie.text.Phrase(h, headerFont));
                                cell.setBackgroundColor(java.awt.Color.LIGHT_GRAY);
                                cell.setHorizontalAlignment(com.lowagie.text.Element.ALIGN_CENTER);
                                cell.setPadding(5);
                                table.addCell(cell);
                        }

                        for (FinancialTransaction t : transactions) {
                                table.addCell(new com.lowagie.text.Phrase(t.getId() != null ? t.getId() : "",
                                                normalFont));
                                table.addCell(new com.lowagie.text.Phrase(
                                                t.getOrderId() != null ? t.getOrderId() : "N/A", normalFont));
                                table.addCell(
                                                new com.lowagie.text.Phrase(
                                                                t.getType() != null ? t.getType().toString() : "",
                                                                normalFont));
                                table.addCell(
                                                new com.lowagie.text.Phrase(
                                                                t.getPartyName() != null ? t.getPartyName() : "N/A",
                                                                normalFont));
                                table.addCell(
                                                new com.lowagie.text.Phrase(
                                                                t.getCategory() != null ? t.getCategory() : "N/A",
                                                                normalFont));
                                table.addCell(new com.lowagie.text.Phrase(
                                                t.getAmount() != null ? "$" + t.getAmount().toString() : "$0.00",
                                                normalFont));
                                table.addCell(new com.lowagie.text.Phrase(
                                                t.getTax() != null ? "$" + t.getTax().toString() : "$0.00",
                                                normalFont));
                                table.addCell(new com.lowagie.text.Phrase(
                                                t.getTotal() != null ? "$" + t.getTotal().toString() : "$0.00",
                                                normalFont));
                                table.addCell(
                                                new com.lowagie.text.Phrase(
                                                                t.getStatus() != null ? t.getStatus().toString() : "",
                                                                normalFont));
                                table.addCell(new com.lowagie.text.Phrase(
                                                t.getCreatedAt() != null ? t.getCreatedAt().toString() : "N/A",
                                                normalFont));
                        }

                        document.add(table);
                        document.close();
                        return out.toByteArray();
                }
        }

        public byte[] generatePaymentExcel(List<com.example.financialledger.dto.PaymentDetailsDTO> payments,
                        BigDecimal totalOutstanding, BigDecimal totalPaid, long overdueCount)
                        throws IOException {
                try (Workbook workbook = new XSSFWorkbook();
                                ByteArrayOutputStream out = new ByteArrayOutputStream()) {

                        Sheet sheet = workbook.createSheet("Outstanding Debts");

                        // 1. Add Summary Section
                        Row titleRow = sheet.createRow(0);
                        titleRow.createCell(0).setCellValue("Summary - Accounts Payable");
                        sheet.createRow(1).createCell(0).setCellValue("Total Outstanding Debt: $" + totalOutstanding);
                        sheet.createRow(2).createCell(0).setCellValue("Total Paid amount: $" + totalPaid);
                        sheet.createRow(3).createCell(0).setCellValue("Overdue Payments: " + overdueCount);

                        // 2. Add Table Headers
                        Row headerRow = sheet.createRow(5);
                        String[] columns = { "Expense ID", "Vendor", "Order ID", "Original Amount", "Paid Amount",
                                        "Remaining",
                                        "Due Date", "Status" };
                        for (int i = 0; i < columns.length; i++)
                                headerRow.createCell(i).setCellValue(columns[i]);

                        int rowIdx = 6;
                        for (com.example.financialledger.dto.PaymentDetailsDTO p : payments) {
                                Row r = sheet.createRow(rowIdx++);
                                r.createCell(0).setCellValue(p.id() != null ? p.id() : "");
                                r.createCell(1).setCellValue(p.partyName() != null ? p.partyName() : "");
                                r.createCell(2).setCellValue(p.orderId() != null ? p.orderId() : "");
                                r.createCell(3).setCellValue(
                                                p.totalAmount() != null ? p.totalAmount().doubleValue() : 0.0);
                                r.createCell(4).setCellValue(
                                                p.amountPaid() != null ? p.amountPaid().doubleValue() : 0.0);
                                r.createCell(5).setCellValue(
                                                p.remainingBalance() != null ? p.remainingBalance().doubleValue()
                                                                : 0.0);
                                r.createCell(6).setCellValue(p.dueDate() != null ? p.dueDate().toString() : "N/A");
                                r.createCell(7).setCellValue(p.status() != null ? p.status() : "");
                        }

                        workbook.write(out);
                        return out.toByteArray();
                }
        }

        public byte[] generatePaymentPdf(List<com.example.financialledger.dto.PaymentDetailsDTO> payments,
                        BigDecimal totalOutstanding, BigDecimal totalPaid, long overdueCount)
                        throws IOException {
                try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                        com.lowagie.text.Document document = new com.lowagie.text.Document(
                                        com.lowagie.text.PageSize.A4);
                        com.lowagie.text.pdf.PdfWriter.getInstance(document, out);
                        document.open();

                        com.lowagie.text.Font titleFont = com.lowagie.text.FontFactory
                                        .getFont(com.lowagie.text.FontFactory.HELVETICA_BOLD, 18,
                                                        java.awt.Color.BLUE.darker());
                        com.lowagie.text.Font headerFont = com.lowagie.text.FontFactory
                                        .getFont(com.lowagie.text.FontFactory.HELVETICA_BOLD, 10);
                        com.lowagie.text.Font normalFont = com.lowagie.text.FontFactory
                                        .getFont(com.lowagie.text.FontFactory.HELVETICA, 8);

                        com.lowagie.text.Paragraph title = new com.lowagie.text.Paragraph("Outstanding Debts Report",
                                        titleFont);
                        title.setAlignment(com.lowagie.text.Element.ALIGN_CENTER);
                        document.add(title);
                        document.add(new com.lowagie.text.Paragraph(" "));

                        // Summary Section
                        document.add(new com.lowagie.text.Paragraph("Summary - Accounts Payable", headerFont));
                        document.add(new com.lowagie.text.Paragraph("Total Outstanding Debt: $" + totalOutstanding,
                                        normalFont));
                        document.add(new com.lowagie.text.Paragraph("Total Paid amount: $" + totalPaid, normalFont));
                        document.add(new com.lowagie.text.Paragraph("Overdue Payments: " + overdueCount, normalFont));
                        document.add(new com.lowagie.text.Paragraph(" "));

                        com.lowagie.text.pdf.PdfPTable table = new com.lowagie.text.pdf.PdfPTable(8);
                        table.setWidthPercentage(100);

                        String[] headers = { "ID", "Vendor", "Order", "Original", "Paid", "Balance", "Due Date",
                                        "Status" };
                        for (String h : headers) {
                                com.lowagie.text.pdf.PdfPCell cell = new com.lowagie.text.pdf.PdfPCell(
                                                new com.lowagie.text.Phrase(h, headerFont));
                                cell.setBackgroundColor(java.awt.Color.LIGHT_GRAY);
                                cell.setHorizontalAlignment(com.lowagie.text.Element.ALIGN_CENTER);
                                table.addCell(cell);
                        }

                        for (com.example.financialledger.dto.PaymentDetailsDTO p : payments) {
                                table.addCell(new com.lowagie.text.Phrase(p.id() != null ? p.id() : "", normalFont));
                                table.addCell(new com.lowagie.text.Phrase(p.partyName() != null ? p.partyName() : "N/A",
                                                normalFont));
                                table.addCell(new com.lowagie.text.Phrase(p.orderId() != null ? p.orderId() : "N/A",
                                                normalFont));
                                table.addCell(new com.lowagie.text.Phrase(
                                                p.totalAmount() != null ? "$" + p.totalAmount().toString() : "$0.00",
                                                normalFont));
                                table.addCell(new com.lowagie.text.Phrase(
                                                p.amountPaid() != null ? "$" + p.amountPaid().toString() : "$0.00",
                                                normalFont));
                                table.addCell(new com.lowagie.text.Phrase(
                                                p.remainingBalance() != null ? "$" + p.remainingBalance().toString()
                                                                : "$0.00",
                                                normalFont));
                                table.addCell(new com.lowagie.text.Phrase(
                                                p.dueDate() != null ? p.dueDate().toLocalDate().toString() : "N/A",
                                                normalFont));
                                table.addCell(new com.lowagie.text.Phrase(p.status() != null ? p.status() : "",
                                                normalFont));
                        }

                        document.add(table);
                        document.close();
                        return out.toByteArray();
                }
        }

        public byte[] generateReportExcel(List<FinancialTransaction> transactions,
                        BigDecimal income, BigDecimal expenses, BigDecimal profit) throws IOException {
                try (Workbook workbook = new XSSFWorkbook();
                                ByteArrayOutputStream out = new ByteArrayOutputStream()) {

                        Sheet sheet = workbook.createSheet("Financial General Report");

                        // 1. Summary
                        Row row0 = sheet.createRow(0);
                        row0.createCell(0).setCellValue("Financial Summary Report");
                        sheet.createRow(1).createCell(0).setCellValue("Total Income: $" + income);
                        sheet.createRow(2).createCell(0).setCellValue("Total Expenses: $" + expenses);
                        sheet.createRow(3).createCell(0).setCellValue("Net Profit: $" + profit);

                        // 2. Headers (8 Columns)
                        Row headerRow = sheet.createRow(5);
                        String[] columns = { "Transaction ID", "Order ID", "Party", "Type", "Category", "Amount",
                                        "Status", "Date" };
                        for (int i = 0; i < columns.length; i++)
                                headerRow.createCell(i).setCellValue(columns[i]);

                        int rowIdx = 6;
                        for (FinancialTransaction t : transactions) {
                                Row r = sheet.createRow(rowIdx++);
                                r.createCell(0).setCellValue(t.getId() != null ? t.getId() : "");
                                r.createCell(1).setCellValue(t.getOrderId() != null ? t.getOrderId() : "");
                                r.createCell(2).setCellValue(t.getPartyName() != null ? t.getPartyName() : "");
                                r.createCell(3).setCellValue(t.getType() != null ? t.getType().toString() : "");
                                r.createCell(4).setCellValue(t.getCategory() != null ? t.getCategory() : "General");
                                r.createCell(5).setCellValue(t.getTotal() != null ? t.getTotal().doubleValue() : 0.0);
                                r.createCell(6).setCellValue(t.getStatus() != null ? t.getStatus().toString() : "");
                                r.createCell(7).setCellValue(t.getCreatedAt() != null
                                                ? t.getCreatedAt().toString().split("T")[0]
                                                : "N/A");
                        }

                        for (int i = 0; i < columns.length; i++)
                                sheet.autoSizeColumn(i);
                        workbook.write(out);
                        return out.toByteArray();
                }
        }

        public byte[] generateReportPdf(List<FinancialTransaction> transactions,
                        BigDecimal income, BigDecimal expenses, BigDecimal profit) throws IOException {
                try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                        com.lowagie.text.Document document = new com.lowagie.text.Document(
                                        com.lowagie.text.PageSize.A4);
                        com.lowagie.text.pdf.PdfWriter.getInstance(document, out);
                        document.open();

                        com.lowagie.text.Font titleFont = com.lowagie.text.FontFactory.getFont(
                                        com.lowagie.text.FontFactory.HELVETICA_BOLD, 18, java.awt.Color.BLUE.darker());
                        com.lowagie.text.Font headerFont = com.lowagie.text.FontFactory.getFont(
                                        com.lowagie.text.FontFactory.HELVETICA_BOLD, 10);
                        com.lowagie.text.Font normalFont = com.lowagie.text.FontFactory
                                        .getFont(com.lowagie.text.FontFactory.HELVETICA, 8);

                        document.add(new com.lowagie.text.Paragraph("Financial Transactions Report", titleFont));
                        document.add(new com.lowagie.text.Paragraph(" "));
                        document.add(new com.lowagie.text.Paragraph("Summary Overview", headerFont));
                        document.add(new com.lowagie.text.Paragraph("Total Income: $" + income, normalFont));
                        document.add(new com.lowagie.text.Paragraph("Total Expenses: $" + expenses, normalFont));
                        document.add(new com.lowagie.text.Paragraph("Net Profit: $" + profit, normalFont));
                        document.add(new com.lowagie.text.Paragraph(" "));

                        com.lowagie.text.pdf.PdfPTable table = new com.lowagie.text.pdf.PdfPTable(8);
                        table.setWidthPercentage(100);
                        String[] headers = { "ID", "Order ID", "Party", "Type", "Category", "Amount", "Status",
                                        "Date" };
                        for (String h : headers) {
                                com.lowagie.text.pdf.PdfPCell cell = new com.lowagie.text.pdf.PdfPCell(
                                                new com.lowagie.text.Phrase(h, headerFont));
                                cell.setBackgroundColor(java.awt.Color.LIGHT_GRAY);
                                table.addCell(cell);
                        }

                        for (FinancialTransaction t : transactions) {
                                table.addCell(new com.lowagie.text.Phrase(t.getId() != null ? t.getId() : "",
                                                normalFont));
                                table.addCell(new com.lowagie.text.Phrase(
                                                t.getOrderId() != null ? t.getOrderId() : "N/A", normalFont));
                                table.addCell(new com.lowagie.text.Phrase(
                                                t.getPartyName() != null ? t.getPartyName() : "N/A", normalFont));
                                table.addCell(new com.lowagie.text.Phrase(
                                                t.getType() != null ? t.getType().toString() : "", normalFont));
                                table.addCell(new com.lowagie.text.Phrase(
                                                t.getCategory() != null ? t.getCategory() : "N/A", normalFont));
                                table.addCell(new com.lowagie.text.Phrase(
                                                t.getTotal() != null ? "$" + t.getTotal().toString() : "$0.00",
                                                normalFont));
                                table.addCell(new com.lowagie.text.Phrase(
                                                t.getStatus() != null ? t.getStatus().toString() : "", normalFont));
                                table.addCell(new com.lowagie.text.Phrase(t.getCreatedAt() != null
                                                ? t.getCreatedAt().toString().split("T")[0]
                                                : "N/A", normalFont));
                        }

                        document.add(table);
                        document.close();
                        return out.toByteArray();
                }
        }

        public byte[] generateEntriesExcel(List<FinancialTransaction> transactions,
                        long active, long voided, long locked, long stockLinked,
                        BigDecimal revenue, BigDecimal expenses) throws IOException {
                try (Workbook workbook = new XSSFWorkbook();
                                ByteArrayOutputStream out = new ByteArrayOutputStream()) {

                        Sheet sheet = workbook.createSheet("Financial Entries Audit");

                        // 1. Comprehensive Summary Section
                        Row row0 = sheet.createRow(0);
                        row0.createCell(0).setCellValue("Financial Records Management - Audit Summary");

                        sheet.createRow(1).createCell(0).setCellValue("Active Records: " + active);
                        sheet.createRow(2).createCell(0).setCellValue("Voided Records: " + voided);
                        sheet.createRow(3).createCell(0).setCellValue("Locked Periods: " + locked);
                        sheet.createRow(4).createCell(0).setCellValue("Linked to Stock: " + stockLinked);
                        sheet.createRow(5).createCell(0).setCellValue("Total Revenue (Active): $" + revenue);
                        sheet.createRow(6).createCell(0).setCellValue("Total Expenses (Active): $" + expenses);

                        // 2. Table Headers (7 Columns)
                        Row headerRow = sheet.createRow(8);
                        String[] columns = { "Transaction ID", "Order ID", "Type", "Amount", "Status", "Fiscal Period",
                                        "Stock Status" };
                        for (int i = 0; i < columns.length; i++)
                                headerRow.createCell(i).setCellValue(columns[i]);

                        // 3. Populate Table Data
                        int rowIdx = 9;
                        for (FinancialTransaction t : transactions) {
                                Row r = sheet.createRow(rowIdx++);
                                r.createCell(0).setCellValue(t.getId() != null ? t.getId() : "");
                                r.createCell(1).setCellValue(t.getOrderId() != null ? t.getOrderId() : "N/A");
                                r.createCell(2).setCellValue(t.getType() != null ? t.getType().toString() : "");
                                r.createCell(3).setCellValue(t.getTotal() != null ? t.getTotal().doubleValue() : 0.0);
                                r.createCell(4).setCellValue(t.getStatus() != null ? t.getStatus().toString() : "");

                                String fiscalId = "N/A";
                                String stockStatus = "N/A";
                                if (t.getFiscalPeriod() != null) {
                                        fiscalId = t.getFiscalPeriod().getId() != null ? t.getFiscalPeriod().getId()
                                                        : "N/A";
                                        stockStatus = t.getFiscalPeriod().getStockStatus() != null
                                                        ? t.getFiscalPeriod().getStockStatus()
                                                        : "N/A";
                                }
                                r.createCell(5).setCellValue(fiscalId);
                                r.createCell(6).setCellValue(stockStatus);
                        }

                        for (int i = 0; i < columns.length; i++)
                                sheet.autoSizeColumn(i);
                        workbook.write(out);
                        return out.toByteArray();
                }
        }

        public byte[] generateEntriesPdf(List<FinancialTransaction> transactions,
                        long active, long voided, long locked, long stockLinked,
                        BigDecimal revenue, BigDecimal expenses) throws IOException {
                try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                        com.lowagie.text.Document document = new com.lowagie.text.Document(
                                        com.lowagie.text.PageSize.A4);
                        com.lowagie.text.pdf.PdfWriter.getInstance(document, out);
                        document.open();

                        com.lowagie.text.Font titleFont = com.lowagie.text.FontFactory.getFont(
                                        com.lowagie.text.FontFactory.HELVETICA_BOLD, 18, java.awt.Color.BLUE.darker());
                        com.lowagie.text.Font headerFont = com.lowagie.text.FontFactory.getFont(
                                        com.lowagie.text.FontFactory.HELVETICA_BOLD, 10);
                        com.lowagie.text.Font normalFont = com.lowagie.text.FontFactory
                                        .getFont(com.lowagie.text.FontFactory.HELVETICA, 8);

                        document.add(new com.lowagie.text.Paragraph("Financial Entries Management Report", titleFont));
                        document.add(new com.lowagie.text.Paragraph(" "));

                        // Summary Section
                        document.add(new com.lowagie.text.Paragraph("Management Summary", headerFont));
                        document.add(new com.lowagie.text.Paragraph("Active Records: " + active, normalFont));
                        document.add(new com.lowagie.text.Paragraph("Voided Records: " + voided, normalFont));
                        document.add(new com.lowagie.text.Paragraph("Locked Periods: " + locked, normalFont));
                        document.add(new com.lowagie.text.Paragraph("Linked to Stock: " + stockLinked, normalFont));
                        document.add(new com.lowagie.text.Paragraph("Total Revenue (Active): $" + revenue, normalFont));
                        document.add(new com.lowagie.text.Paragraph("Total Expenses (Active): $" + expenses,
                                        normalFont));
                        document.add(new com.lowagie.text.Paragraph(" "));

                        com.lowagie.text.pdf.PdfPTable table = new com.lowagie.text.pdf.PdfPTable(7);
                        table.setWidthPercentage(100);
                        String[] headers = { "ID", "Order ID", "Type", "Amount", "Status", "Fiscal Period",
                                        "Stock Status" };
                        for (String h : headers) {
                                com.lowagie.text.pdf.PdfPCell cell = new com.lowagie.text.pdf.PdfPCell(
                                                new com.lowagie.text.Phrase(h, headerFont));
                                cell.setBackgroundColor(java.awt.Color.LIGHT_GRAY);
                                table.addCell(cell);
                        }

                        for (FinancialTransaction t : transactions) {
                                table.addCell(new com.lowagie.text.Phrase(t.getId() != null ? t.getId() : "",
                                                normalFont));
                                table.addCell(new com.lowagie.text.Phrase(
                                                t.getOrderId() != null ? t.getOrderId() : "N/A", normalFont));
                                table.addCell(new com.lowagie.text.Phrase(
                                                t.getType() != null ? t.getType().toString() : "", normalFont));
                                table.addCell(new com.lowagie.text.Phrase(
                                                t.getTotal() != null ? "$" + t.getTotal().toString() : "$0.00",
                                                normalFont));
                                table.addCell(new com.lowagie.text.Phrase(
                                                t.getStatus() != null ? t.getStatus().toString() : "", normalFont));

                                String fiscalId = "N/A";
                                String stockStatus = "N/A";
                                if (t.getFiscalPeriod() != null) {
                                        fiscalId = t.getFiscalPeriod().getId() != null ? t.getFiscalPeriod().getId()
                                                        : "N/A";
                                        stockStatus = t.getFiscalPeriod().getStockStatus() != null
                                                        ? t.getFiscalPeriod().getStockStatus()
                                                        : "N/A";
                                }
                                table.addCell(new com.lowagie.text.Phrase(fiscalId, normalFont));
                                table.addCell(new com.lowagie.text.Phrase(stockStatus, normalFont));
                        }

                        document.add(table);
                        document.close();
                        return out.toByteArray();
                }
        }
}
