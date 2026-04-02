package com.finance.dashboard.dto.response;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class DashboardSummaryResponse {

    private BigDecimal totalIncome;
    private BigDecimal totalExpense;
    private BigDecimal netBalance;
    private Map<String, BigDecimal> categoryTotals;
    private List<FinancialRecordResponse> recentActivity;

    public DashboardSummaryResponse(BigDecimal totalIncome, BigDecimal totalExpense,
                                     BigDecimal netBalance,
                                     Map<String, BigDecimal> categoryTotals,
                                     List<FinancialRecordResponse> recentActivity) {
        this.totalIncome = totalIncome;
        this.totalExpense = totalExpense;
        this.netBalance = netBalance;
        this.categoryTotals = categoryTotals;
        this.recentActivity = recentActivity;
    }

    public BigDecimal getTotalIncome() { return totalIncome; }
    public BigDecimal getTotalExpense() { return totalExpense; }
    public BigDecimal getNetBalance() { return netBalance; }
    public Map<String, BigDecimal> getCategoryTotals() { return categoryTotals; }
    public List<FinancialRecordResponse> getRecentActivity() { return recentActivity; }
}