package com.example.warehouseManagement.Services;

import java.util.List;

import com.example.warehouseManagement.Domains.OperationLog;

public interface OperationLogService {

    OperationLog record(String username, String action, String target, String details);

    List<OperationLog> findRecentLogs();
}