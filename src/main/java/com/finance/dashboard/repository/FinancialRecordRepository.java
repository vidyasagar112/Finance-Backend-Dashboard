package com.finance.dashboard.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.finance.dashboard.model.FinancialRecord;
import com.finance.dashboard.model.RecordType;



@Repository
public interface FinancialRecordRepository extends JpaRepository<FinancialRecord, Long> {

    // Get all records that are not soft deleted
    List<FinancialRecord> findByIsDeletedFalse();

    // Filter by type (INCOME or EXPENSE)
    List<FinancialRecord> findByTypeAndIsDeletedFalse(RecordType type);

    // Filter by category
    List<FinancialRecord> findByCategoryAndIsDeletedFalse(String category);

    // Filter by date range
    List<FinancialRecord> findByDateBetweenAndIsDeletedFalse(LocalDate from, LocalDate to);

    // Filter by type and date range
    List<FinancialRecord> findByTypeAndDateBetweenAndIsDeletedFalse(
        RecordType type, LocalDate from, LocalDate to
    );

    // Get recent N records (for dashboard)
    List<FinancialRecord> findTop5ByIsDeletedFalseOrderByCreatedAtDesc();

    // Sum of all income
    @Query("SELECT COALESCE(SUM(f.amount), 0) FROM FinancialRecord f WHERE f.type = 'INCOME' AND f.isDeleted = false")
    BigDecimal sumTotalIncome();

    // Sum of all expenses
    @Query("SELECT COALESCE(SUM(f.amount), 0) FROM FinancialRecord f WHERE f.type = 'EXPENSE' AND f.isDeleted = false")
    BigDecimal sumTotalExpense();

    // Category wise totals
    @Query("SELECT f.category, SUM(f.amount) FROM FinancialRecord f WHERE f.isDeleted = false GROUP BY f.category")
    List<Object[]> sumByCategory();

    // Monthly trends
    @Query("SELECT MONTH(f.date), YEAR(f.date), f.type, SUM(f.amount) " +
           "FROM FinancialRecord f WHERE f.isDeleted = false " +
           "GROUP BY YEAR(f.date), MONTH(f.date), f.type " +
           "ORDER BY YEAR(f.date), MONTH(f.date)")
    List<Object[]> monthlyTrends();
}