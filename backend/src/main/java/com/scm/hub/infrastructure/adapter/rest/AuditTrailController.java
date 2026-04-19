package com.scm.hub.infrastructure.adapter.rest;

import com.scm.hub.application.service.SyncLogService;
import com.scm.hub.domain.model.SyncLog;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST Controller for audit trail and synchronization log retrieval.
 * Exposes endpoints to monitor the synchronization status of data between on-premise and cloud.
 */
@RestController
@RequestMapping("/api/audit-trail")
@RequiredArgsConstructor
public class AuditTrailController {

    private final SyncLogService syncLogService;

    /**
     * Retrieves all synchronization audit logs.
     * 
     * @return A list of synchronization logs.
     */
    @GetMapping
    public ResponseEntity<List<SyncLog>> getAuditLogs() {
        return ResponseEntity.ok(syncLogService.getAuditLogs());
    }
}
