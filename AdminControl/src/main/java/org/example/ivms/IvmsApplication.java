package org.example.ivms;

import org.example.ivms.entity.AuditLog;
import org.example.ivms.entity.User;
import org.example.ivms.repository.AuditLogRepository;
import org.example.ivms.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

@EnableDiscoveryClient
@SpringBootApplication
public class IvmsApplication {

    public static void main(String[] args) {
        SpringApplication.run(IvmsApplication.class, args);
    }

    // Define PasswordEncoder as a Spring Bean
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);  // Recommended strength: 10–12
    }

    // CommandLineRunner for seeding data
    @Bean
    CommandLineRunner runner(UserRepository userRepo,
                             AuditLogRepository auditRepo,
                             PasswordEncoder passwordEncoder) {   // ← Inject here!
        return args -> {
            // Only seed if the database is completely empty
            if (userRepo.count() == 0) {
                System.out.println("Seeding initial users...");

                // From schema example
                createUser(userRepo, passwordEncoder, "USR-000", "System Administrator",
                        "admin@ivms.local", "admin", "Super Admin");

                createUser(userRepo, passwordEncoder, "USR-001", "John Anderson",
                        "john.anderson@company.com", "warehouse", "Warehouse Manager");
                createUser(userRepo, passwordEncoder, "USR-002", "Sarah Chen",
                        "sarah.chen@company.com", "procurement", "Procurement Manager");
                createUser(userRepo, passwordEncoder, "USR-003", "Michael Rodriguez",
                        "michael.r@company.com", "warehouse", "Sales Staff");
                createUser(userRepo, passwordEncoder, "USR-004", "Emma Wilson",
                        "emma.wilson@company.com", "admin", "Finance Officer", "Inactive");
                createUser(userRepo, passwordEncoder, "USR-005", "Admin User",
                        "admin@company.com", "admin", "System Administrator");

                System.out.println("Seeded 6 initial users.");
            } else {
                System.out.println("Users already exist — skipping initial seed.");
            }

            // Seed audit logs only if empty
            if (auditRepo.count() == 0) {
                System.out.println("Seeding sample audit logs...");
                auditRepo.save(createLog("2026-02-22T14:35:22", "USR-1021", "John Doe", "Inventory", "Update", "SKU-2048",
                        "{\"stock_qty\": 500...}", "{\"stock_qty\": 450...}", "Info", "Normal"));
                auditRepo.save(createLog("2026-02-22T13:22:10", "USR-1035", "Sarah Chen", "Procurement", "Delete", "PO-8821",
                        "{\"po_number\": \"PO-8821\"...}", "null", "Warning", "Flagged"));
                auditRepo.save(createLog("2026-02-22T11:15:48", "USR-9999", "Unknown User", "Auth", "Login Attempt", "admin@company.com",
                        "null", "{\"status\": \"failed\"...}", "Critical", "Flagged"));
                auditRepo.save(createLog("2026-02-22T10:42:33", "USR-1023", "Michael Torres", "Finance", "Create", "INV-5024",
                        "null", "{\"invoice_number\": \"INV-5024\"...}", "Info", "Normal"));
                auditRepo.save(createLog("2026-02-22T09:18:55", "USR-0001", "Admin User", "Inventory", "Stock Adjustment", "SKU-3041",
                        "{\"stock_qty\": 1200}", "{\"stock_qty\": 1100...}", "Warning", "Normal"));
                auditRepo.save(createLog("2026-02-21T16:45:12", "USR-1007", "Emily Johnson", "Inventory", "Update", "SKU-1825",
                        "{\"location\": \"A-12\"...}", "{\"location\": \"B-05\"...}", "Info", "Normal"));
                System.out.println("Seeded sample audit logs.");
            }
        };
    }

    // Helper method – now takes PasswordEncoder as parameter
    private void createUser(UserRepository repo, PasswordEncoder encoder,
                            String userId, String name, String email,
                            String role, String designation) {
        createUser(repo, encoder, userId, name, email, role, designation, "Active");
    }

    private void createUser(UserRepository repo, PasswordEncoder encoder,
                            String userId, String name, String email,
                            String role, String designation, String status) {
        User u = new User();
        u.setUserId(userId);
        u.setName(name);
        u.setEmail(email);
        u.setPassword(encoder.encode("default123"));  // Change this in production!
        u.setRole(role);
        u.setDesignation(designation);
        u.setStatus(status);
        u.setLastLogin(LocalDateTime.now().minusDays(1));
        // forcePasswordChange defaults to true from entity
        repo.save(u);
    }

    // Helper to create audit log
    private AuditLog createLog(String ts, String uid, String uname, String module, String action,
                               String entity, String oldV, String newV, String sev, String st) {
        AuditLog log = new AuditLog();
        log.setTimestamp(LocalDateTime.parse(ts));
        log.setPerformedByUserId(uid);
        log.setPerformedByUsername(uname);
        log.setModule(module);
        log.setAction(action);
        log.setEntityType(entity.split("-")[0]);
        log.setEntityId(entity.contains("-") ? entity.split("-")[1] : null);
        log.setStatus(st);
        return log;
    }
}