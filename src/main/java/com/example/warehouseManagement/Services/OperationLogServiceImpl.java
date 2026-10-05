package com.example.warehouseManagement.Services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.warehouseManagement.Domains.OperationLog;
import com.example.warehouseManagement.Repositories.OperationLogRepository;

@Service
public class OperationLogServiceImpl implements OperationLogService {

    private final OperationLogRepository operationLogRepository;

    public OperationLogServiceImpl(OperationLogRepository operationLogRepository) {
        this.operationLogRepository = operationLogRepository;
    }

    @Override
    public OperationLog record(String username, String action, String target, String details) {
        OperationLog log = OperationLog.builder()
                .username(username)
                .action(action)
                .target(target)
                .details(details)
                .build();

        return operationLogRepository.save(log);
    }

    @Override
    public List<OperationLog> findRecentLogs() {
        return operationLogRepository.findTop100ByOrderByCreatedAtDesc();
    }
}