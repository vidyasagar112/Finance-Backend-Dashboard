package com.finance.dashboard.service;

import com.finance.dashboard.dto.response.DashboardSummaryResponse;
import com.finance.dashboard.dto.response.FinancialRecordResponse;
import com.finance.dashboard.repository.FinancialRecordRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    private final FinancialRecordRepository recordRepository;
    private final FinancialRecordService recordService;

    public DashboardService(FinancialRecordRepository recordRepository,
                             FinancialRecordService recordService) {
        this.recordRepository = recordRepository;
        this.recordService = recordService;
    }

    public DashboardSummaryResponse getSummary() {
        BigDecimal totalIncome = recordRepository.sumTotalIncome();
        BigDecimal totalExpense = recordRepository.sumTotalExpense();
        BigDecimal netBalance = totalIncome.subtract(totalExpense);

        Map<String, BigDecimal> categoryTotals = getCategoryTotals();

        List<FinancialRecordResponse> recent = recordRepository
                .findTop5ByIsDeletedFalseOrderByCreatedAtDesc()
                .stream()
                .map(recordService::mapToResponse)
                .collect(Collectors.toList());

        return new DashboardSummaryResponse(
                totalIncome, totalExpense, netBalance, categoryTotals, recent);
    }

    public Map<String, BigDecimal> getCategoryTotals() {
        Map<String, BigDecimal> result = new HashMap<>();
        recordRepository.sumByCategory().forEach(row -> {
            result.put((String) row[0], (BigDecimal) row[1]);
        });
        return result;
    }

    public List<Map<String, Object>> getMonthlyTrends() {
        return recordRepository.monthlyTrends().stream().map(row -> {
            Map<String, Object> map = new HashMap<>();
            map.put("month", row[0]);
            map.put("year", row[1]);
            map.put("type", row[2]);
            map.put("total", row[3]);
            return map;
        }).collect(Collectors.toList());
    }

    public List<FinancialRecordResponse> getRecentActivity() {
        return recordRepository
                .findTop5ByIsDeletedFalseOrderByCreatedAtDesc()
                .stream()
                .map(recordService::mapToResponse)
                .collect(Collectors.toList());
    }
}