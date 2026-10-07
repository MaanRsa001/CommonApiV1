package com.maan.eway.config;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Component;

@Component
public class ThreadMonitorService {
    private static final AtomicInteger callCounter = new AtomicInteger(0);
    private static final AtomicLong lastHighThreadLog = new AtomicLong(0);
    private static final int MONITORING_INTERVAL = 5; // Log details every 5th call
    private static final int HIGH_THREAD_THRESHOLD = 50;
    
    // NEW: Store monitoring history
    private final List<Map<String, Object>> monitoringHistory = Collections.synchronizedList(new ArrayList<>());
    
    public void monitorThreads(String phase, String serviceName) {
        try {
            ThreadMXBean threadBean = ManagementFactory.getThreadMXBean();
            int threadCount = threadBean.getThreadCount();
            int callCount = callCounter.incrementAndGet();
            
            // Light monitoring for most calls
            if (callCount % MONITORING_INTERVAL != 0 && threadCount <= HIGH_THREAD_THRESHOLD) {
                logLightMonitoring(phase, serviceName, threadCount);
                return;
            }
            
            // Full monitoring for high thread count or periodic check
            performFullMonitoring(phase, serviceName, threadBean, threadCount);
            
        } catch (Exception e) {
            System.err.printf("Error in thread monitoring: %s%n", e.getMessage());
        }
    }
    
    private void logLightMonitoring(String phase, String serviceName, int threadCount) {
        System.out.printf("[Light Monitor] %s - %s - Threads: %d%n", 
            phase, serviceName, threadCount);
    }
    
    private void performFullMonitoring(String phase, String serviceName, 
                                     ThreadMXBean threadBean, int threadCount) {
        int peakThreadCount = threadBean.getPeakThreadCount();
        int daemonCount = threadBean.getDaemonThreadCount();
        
        System.out.printf("=== THREAD MONITOR %s ===%n", phase);
        System.out.printf("Service: %s%n", serviceName);
        System.out.printf("Current Threads: %d, Peak: %d, Daemon: %d%n", 
            threadCount, peakThreadCount, daemonCount);
        System.out.printf("Executing in thread: %s (ID: %d)%n", 
            Thread.currentThread().getName(), Thread.currentThread().getId());
        
        // NEW: Store in history
        storeMonitoringSnapshot(phase, serviceName, threadCount, peakThreadCount, daemonCount);
        
        // Enumerate threads only if necessary (expensive operation)
        if (threadCount > HIGH_THREAD_THRESHOLD) {
            long now = System.currentTimeMillis();
            if (now - lastHighThreadLog.get() > 30000) { // Log at most every 30 seconds
                lastHighThreadLog.set(now);
                System.out.println("=== HIGH THREAD COUNT DETECTED ===");
                enumerateAndAnalyzeThreads(threadCount);
            }
        }
    }
    
    // NEW: Store monitoring data for API access
    private void storeMonitoringSnapshot(String phase, String serviceName, 
                                       int threadCount, int peakThreadCount, int daemonCount) {
        Map<String, Object> snapshot = new HashMap<>();
        snapshot.put("phase", phase);
        snapshot.put("serviceName", serviceName);
        snapshot.put("threadCount", threadCount);
        snapshot.put("peakThreadCount", peakThreadCount);
        snapshot.put("daemonCount", daemonCount);
        snapshot.put("timestamp", new Date());
        snapshot.put("threadName", Thread.currentThread().getName());
        
        monitoringHistory.add(snapshot);
        
        // Keep only last 100 entries to prevent memory leaks
        if (monitoringHistory.size() > 100) {
            monitoringHistory.remove(0);
        }
    }
    
    private void enumerateAndAnalyzeThreads(int estimatedThreadCount) {
        try {
            ThreadGroup rootGroup = getRootThreadGroup();
            Thread[] threads = new Thread[estimatedThreadCount * 2]; // Overallocate
            int actualCount = rootGroup.enumerate(threads, true);
            
            analyzeThreadDetails(threads, actualCount);
            
        } catch (Exception e) {
            System.out.printf("Error enumerating threads: %s%n", e.getMessage());
        }
    }
    
    private ThreadGroup getRootThreadGroup() {
        ThreadGroup rootGroup = Thread.currentThread().getThreadGroup();
        while (rootGroup.getParent() != null) {
            rootGroup = rootGroup.getParent();
        }
        return rootGroup;
    }
    
    private void analyzeThreadDetails(Thread[] threads, int actualCount) {
        Map<String, Integer> threadGroups = new HashMap<>();
        Map<Thread.State, Integer> threadStates = new HashMap<>();
        List<ThreadInfo> problematicThreads = new ArrayList<>();
        
        for (int i = 0; i < actualCount && threads[i] != null; i++) {
            Thread thread = threads[i];
            updateThreadCounts(thread, threadGroups, threadStates);
            
            // Identify problematic threads
            if (isProblematicThread(thread)) {
                problematicThreads.add(new ThreadInfo(thread));
            }
        }
        
        logThreadAnalysis(threadGroups, threadStates, problematicThreads);
    }
    
    private void updateThreadCounts(Thread thread, Map<String, Integer> threadGroups, 
                                   Map<Thread.State, Integer> threadStates) {
        // Count by thread group
        String groupName = thread.getThreadGroup().getName();
        threadGroups.put(groupName, threadGroups.getOrDefault(groupName, 0) + 1);
        
        // Count by state
        Thread.State state = thread.getState();
        threadStates.put(state, threadStates.getOrDefault(state, 0) + 1);
    }
    
    private boolean isProblematicThread(Thread thread) {
        String name = thread.getName().toLowerCase();
        return (name.contains("calculator") && thread.getState() == Thread.State.RUNNABLE) ||
               thread.getState() == Thread.State.BLOCKED ||
               (name.contains("pool") && thread.getState() == Thread.State.WAITING);
    }
    
    private void logThreadAnalysis(Map<String, Integer> threadGroups, 
                                 Map<Thread.State, Integer> threadStates,
                                 List<ThreadInfo> problematicThreads) {
        System.out.println("Thread Groups: " + threadGroups);
        System.out.println("Thread States: " + threadStates);
        
        if (!problematicThreads.isEmpty()) {
            System.out.println("=== PROBLEMATIC THREADS ===");
            for (ThreadInfo info : problematicThreads) {
                System.out.printf("  - %s (ID: %d, State: %s)%n",
                    info.thread.getName(), info.thread.getId(), info.thread.getState());
                logStackTrace(info.thread);
            }
        }
    }
    
    private void logStackTrace(Thread thread) {
        try {
            StackTraceElement[] stackTrace = thread.getStackTrace();
            if (stackTrace.length > 0) {
                System.out.println("    Stack Trace (top 3):");
                for (int i = 0; i < Math.min(3, stackTrace.length); i++) {
                    System.out.println("      at " + stackTrace[i]);
                }
            }
        } catch (Exception e) {
            System.out.println("    Could not get stack trace: " + e.getMessage());
        }
    }
    
    public void performCleanup() {
        try {
            // Suggest GC if memory usage is high
            long usedMemory = ManagementFactory.getMemoryMXBean()
                .getHeapMemoryUsage().getUsed();
            long maxMemory = ManagementFactory.getMemoryMXBean()
                .getHeapMemoryUsage().getMax();
            
            if (usedMemory > maxMemory * 0.75) {
                System.out.println("High memory usage detected, suggesting GC...");
                System.gc();
            }
            
        } catch (Exception e) {
            System.out.println("Cleanup warning: " + e.getMessage());
        }
    }
    
    // NEW: API Methods for Controller
    
    /**
     * Get comprehensive system status for API responses
     */
    public Map<String, Object> getSystemStatus() {
        Map<String, Object> status = new HashMap<>();
        try {
            ThreadMXBean threadBean = ManagementFactory.getThreadMXBean();
            Runtime runtime = Runtime.getRuntime();
            
            // Thread information
            status.put("liveThreads", threadBean.getThreadCount());
            status.put("daemonThreads", threadBean.getDaemonThreadCount());
            status.put("peakThreads", threadBean.getPeakThreadCount());
            status.put("totalStartedThreads", threadBean.getTotalStartedThreadCount());
            
            // Memory information
            long usedMemory = runtime.totalMemory() - runtime.freeMemory();
            long totalMemory = runtime.totalMemory();
            double memoryUsagePercent = totalMemory > 0 ? (double) usedMemory / totalMemory * 100 : 0;
            
            status.put("freeMemory", runtime.freeMemory());
            status.put("totalMemory", totalMemory);
            status.put("maxMemory", runtime.maxMemory());
            status.put("usedMemory", usedMemory);
            status.put("memoryUsagePercent", String.format("%.2f", memoryUsagePercent));
            
            // System information
            status.put("availableProcessors", runtime.availableProcessors());
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss"); 
            status.put("timestamp", sdf.format(new Date()));
            status.put("uptime", ManagementFactory.getRuntimeMXBean().getUptime());
            status.put("monitoringCalls", callCounter.get());
            
        } catch (Exception e) {
            status.put("error", e.getMessage());
        }
        return status;
    }
    
    /**
     * Get monitoring history
     */
    public List<Map<String, Object>> getMonitoringHistory() {
        return new ArrayList<>(monitoringHistory);
    }
    
    /**
     * Force garbage collection with detailed results
     */
    public Map<String, Object> forceGarbageCollection() {
        Map<String, Object> result = new HashMap<>();
        try {
            Runtime runtime = Runtime.getRuntime();
            long memoryBefore = runtime.totalMemory() - runtime.freeMemory();
            
            System.gc();
            Thread.sleep(200); // Give GC a moment to work
            
            long memoryAfter = runtime.totalMemory() - runtime.freeMemory();
            long memoryFreed = memoryBefore - memoryAfter;
            
            result.put("status", "success");
            result.put("memoryBefore", memoryBefore);
            result.put("memoryAfter", memoryAfter);
            result.put("memoryFreed", memoryFreed > 0 ? memoryFreed : 0);
            result.put("message", "Garbage collection completed");
            result.put("timestamp", new Date());
            
        } catch (Exception e) {
            result.put("status", "error");
            result.put("message", e.getMessage());
        }
        return result;
    }
    
    /**
     * Perform comprehensive cleanup
     */
    public Map<String, Object> performDetailedCleanup() {
        Map<String, Object> result = new HashMap<>();
        try {
            // Perform the existing cleanup
            performCleanup();
            
            // Additional cleanup operations
            Runtime runtime = Runtime.getRuntime();
            result.put("status", "success");
            result.put("freeMemory", runtime.freeMemory());
            result.put("totalMemory", runtime.totalMemory());
            result.put("message", "Cleanup operations completed");
            result.put("timestamp", new Date());
            
        } catch (Exception e) {
            result.put("status", "error");
            result.put("message", e.getMessage());
        }
        return result;
    }
    
    /**
     * Reset monitoring counters
     */
    public Map<String, Object> resetMonitoring() {
        callCounter.set(0);
        monitoringHistory.clear();
        
        Map<String, Object> result = new HashMap<>();
        result.put("status", "success");
        result.put("message", "Monitoring counters reset");
        result.put("timestamp", new Date());
        return result;
    }
    
    // Helper class to store thread information
    private static class ThreadInfo {
        Thread thread;
        long timestamp;
        
        ThreadInfo(Thread thread) {
            this.thread = thread;
            this.timestamp = System.currentTimeMillis();
        }
    }
}