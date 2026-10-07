package com.maan.eway.integration.controller;

import com.maan.eway.config.ThreadMonitorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/threads")
public class ThreadManagementController {
    
    @Autowired
    private ThreadMonitorService threadMonitorService;
    
    /**
     * Get current system and thread status
     * GET: http://localhost:8080/api/threads/status
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getThreadStatus() {
        Map<String, Object> status = threadMonitorService.getSystemStatus();
        return ResponseEntity.ok(status);
    }
    
    /**
     * Trigger manual thread monitoring
     * POST: http://localhost:8080/api/threads/monitor
     */
    @PostMapping("/monitor")
    public ResponseEntity<Map<String, Object>> triggerMonitoring(
            @RequestParam(defaultValue = "MANUAL") String level,
            @RequestParam(defaultValue = "ManualTrigger") String service) {
        
        threadMonitorService.monitorThreads(level, service);
        
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Thread monitoring triggered");
        response.put("level", level);
        response.put("service", service);
        response.put("timestamp", new java.util.Date());
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Perform system cleanup
     * POST: http://localhost:8080/api/threads/cleanup
     */
    @PostMapping("/cleanup")
    public ResponseEntity<Map<String, Object>> performCleanup() {
        Map<String, Object> cleanupResult = threadMonitorService.performDetailedCleanup();
        
        Map<String, Object> response = new HashMap<>();
        response.put("operation", "cleanup");
        response.put("result", cleanupResult);
        response.put("timestamp", new java.util.Date());
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Force garbage collection
     * POST: http://localhost:8080/api/threads/gc
     */
    @PostMapping("/gc")
    public ResponseEntity<Map<String, Object>> forceGarbageCollection() {
        Map<String, Object> gcResult = threadMonitorService.forceGarbageCollection();
        
        Map<String, Object> response = new HashMap<>();
        response.put("operation", "garbage_collection");
        response.put("result", gcResult);
        response.put("timestamp", new java.util.Date());
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get monitoring history
     * GET: http://localhost:8080/api/threads/history
     */
    @GetMapping("/history")
    public ResponseEntity<Map<String, Object>> getMonitoringHistory() {
        List<Map<String, Object>> history = threadMonitorService.getMonitoringHistory();
        
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("historySize", history.size());
        response.put("history", history);
        response.put("timestamp", new java.util.Date());
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Reset monitoring counters
     * POST: http://localhost:8080/api/threads/reset
     */
    @PostMapping("/reset")
    public ResponseEntity<Map<String, Object>> resetMonitoring() {
        Map<String, Object> resetResult = threadMonitorService.resetMonitoring();
        
        Map<String, Object> response = new HashMap<>();
        response.put("operation", "reset_monitoring");
        response.put("result", resetResult);
        response.put("timestamp", new java.util.Date());
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Health check endpoint
     * GET: http://localhost:8080/api/threads/health
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> status = threadMonitorService.getSystemStatus();
        
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("application", "EWay Calculator Service");
        health.put("threads", status.get("liveThreads"));
        health.put("memoryUsage", status.get("memoryUsagePercent"));
        health.put("timestamp", new java.util.Date());
        
        // Health indicators based on your thresholds
        int threadCount = (Integer) status.get("liveThreads");
        String memoryUsageStr = (String) status.get("memoryUsagePercent");
        double memoryUsage = Double.parseDouble(memoryUsageStr.replace("%", ""));
        
        if (threadCount > 80 || memoryUsage > 90) {
            health.put("status", "WARNING");
            health.put("message", "High resource usage detected");
        } else if (threadCount > 100 || memoryUsage > 95) {
            health.put("status", "CRITICAL");
            health.put("message", "Critical resource usage - immediate attention required");
        } else {
            health.put("message", "System operating normally");
        }
        
        return ResponseEntity.ok(health);
    }
    
    /**
     * Get thread dump (triggers full monitoring)
     * GET: http://localhost:8080/api/threads/dump
     */
    @GetMapping("/dump")
    public ResponseEntity<Map<String, Object>> getThreadDump() {
        // Force full monitoring with thread enumeration
        threadMonitorService.monitorThreads("DUMP", "ThreadDumpEndpoint");
        
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Thread dump generated in server logs");
        response.put("timestamp", new java.util.Date());
        response.put("note", "Check your application console/logs for detailed thread information");
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Exception handler for the controller
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleException(Exception e) {
        Map<String, Object> error = new HashMap<>();
        error.put("status", "error");
        error.put("message", e.getMessage());
        error.put("timestamp", new java.util.Date());
        
        return ResponseEntity.badRequest().body(error);
    }
}