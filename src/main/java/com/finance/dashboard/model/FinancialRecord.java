package com.finance.dashboard.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "financial_records")
public class FinancialRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RecordType type;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false)
    private LocalDate date;

    private String notes;

    @Column(nullable = false)
    private boolean isDeleted = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() { createdAt = LocalDateTime.now(); }

    @PreUpdate
    protected void onUpdate() { updatedAt = LocalDateTime.now(); }

    // ── Getters and Setters ──────────────────────────

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public RecordType getType() { return type; }
    public void setType(RecordType type) { this.type = type; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public boolean isDeleted() { return isDeleted; }
    public void setDeleted(boolean isDeleted) { this.isDeleted = isDeleted; }

    public User getCreatedBy() { return createdBy; }
    public void setCreatedBy(User createdBy) { this.createdBy = createdBy; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    // ── Builder ──────────────────────────────────────

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private BigDecimal amount;
        private RecordType type;
        private String category;
        private LocalDate date;
        private String notes;
        private boolean isDeleted = false;
        private User createdBy;

        public Builder amount(BigDecimal amount) { this.amount = amount; return this; }
        public Builder type(RecordType type) { this.type = type; return this; }
        public Builder category(String category) { this.category = category; return this; }
        public Builder date(LocalDate date) { this.date = date; return this; }
        public Builder notes(String notes) { this.notes = notes; return this; }
        public Builder isDeleted(boolean isDeleted) { this.isDeleted = isDeleted; return this; }
        public Builder createdBy(User createdBy) { this.createdBy = createdBy; return this; }

        public FinancialRecord build() {
            FinancialRecord r = new FinancialRecord();
            r.amount = this.amount;
            r.type = this.type;
            r.category = this.category;
            r.date = this.date;
            r.notes = this.notes;
            r.isDeleted = this.isDeleted;
            r.createdBy = this.createdBy;
            return r;
        }
    }
}