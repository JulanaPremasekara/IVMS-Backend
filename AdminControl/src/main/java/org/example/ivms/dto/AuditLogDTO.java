package org.example.ivms.dto;

import lombok.Data;

@Data
public class AuditLogDTO {
    private String timestamp;
    private String userId;
    private String user;
    private String module;
    private String action;
    private String entity;
    private String status;
}