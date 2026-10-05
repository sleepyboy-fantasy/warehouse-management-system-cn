package com.example.warehouseManagement.Controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.warehouseManagement.Services.OperationLogService;

@Controller
@RequestMapping("/admin/operation-logs")
public class OperationLogController {

    private final OperationLogService operationLogService;

    public OperationLogController(OperationLogService operationLogService) {
        this.operationLogService = operationLogService;
    }

    @GetMapping
    public String operationLogs(Model model) {
        model.addAttribute("title", "操作日志");
        model.addAttribute("operationLogs", operationLogService.findRecentLogs());
        return "admin/operationLogs";
    }
}