package org.example.ivms.service;

import org.example.ivms.dto.AuditLogDTO;
import org.example.ivms.entity.AuditLog;
import org.example.ivms.repository.AuditLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuditLogService {

    @Autowired
    private AuditLogRepository auditLogRepository;

    // These should normally come from security context / thread-local / request
    // For demo purposes we're keeping them as fields → in real app use better approach
    private String performedByUserId;
    private String performedByUsername;

    public List<AuditLogDTO> getAllLogs() {
        return auditLogRepository.findAll()
                .stream()
                .map(this::toDTO)
                .sorted((a, b) -> b.getTimestamp().compareTo(a.getTimestamp())) // newest first
                .collect(Collectors.toList());
    }

    public void log(
            String module,
            String action,
            String entityType,
            String entityId,
            String status) {

        AuditLog log = new AuditLog();
        log.setModule(module);
        log.setAction(action);
        log.setEntityType(entityType);
        log.setEntityId(entityId);
        log.setStatus(status != null ? status : "Normal");

        log.setPerformedByUserId(performedByUserId != null ? performedByUserId : "SYSTEM");
        log.setPerformedByUsername(performedByUsername != null ? performedByUsername : "System");

        auditLogRepository.save(log);
    }

    private AuditLogDTO toDTO(AuditLog log) {
        AuditLogDTO dto = new AuditLogDTO();
        dto.setTimestamp(log.getTimestamp() != null ? log.getTimestamp().toString() : "");
        dto.setUserId(log.getPerformedByUserId());
        dto.setUser(log.getPerformedByUsername());
        dto.setModule(log.getModule());
        dto.setAction(log.getAction());

        String entity = log.getEntityType() != null ? log.getEntityType() : "";
        if (log.getEntityId() != null && !log.getEntityId().isBlank()) {
            entity += "-" + log.getEntityId();
        }
        dto.setEntity(entity);

        dto.setStatus(log.getStatus() != null ? log.getStatus() : "Normal");
        return dto;
    }

    // Helper method – in real app you would set this from SecurityContext / JWT / etc.
    public void setCurrentUser(String userId, String username) {
        this.performedByUserId = userId;
        this.performedByUsername = username;
    }
}