package org.example.ivms.controller;

import org.example.ivms.dto.AuditLogDTO;
import org.example.ivms.service.AuditLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")   // ← CHANGE IN PRODUCTION!
public class AuditController {

    @Autowired
    private AuditLogService auditLogService;

    @GetMapping("/audit/logs")
    public List<AuditLogDTO> getAuditLogs() {
        return auditLogService.getAllLogs();
    }
}