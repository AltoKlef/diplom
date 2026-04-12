package com.alto.diplom.entity.transactions;

import com.alto.diplom.core.Company;
import com.alto.diplom.core.Customer;
import io.jmix.core.DeletePolicy;
import io.jmix.core.entity.annotation.JmixGeneratedValue;
import io.jmix.core.entity.annotation.OnDeleteInverse;
import io.jmix.core.metamodel.annotation.Composition;
import io.jmix.core.metamodel.annotation.JmixEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@JmixEntity
@Table(name = "TRANSACTION_", indexes = {
        @Index(name = "IDX_TRANSACTION__CUSTOMER", columnList = "CUSTOMER_ID"),
        @Index(name = "IDX_TRANSACTION__COMPANY", columnList = "COMPANY_ID"),
        @Index(name = "IDX_TRANSACTION__INITIAL_TRANSACTION", columnList = "INITIAL_TRANSACTION_ID")
})
@Entity(name = "Transaction_")
public class Transaction {
    @JmixGeneratedValue
    @Column(name = "ID", nullable = false)
    @Id
    private UUID id;

    @OnDeleteInverse(DeletePolicy.CASCADE)
    @JoinColumn(name = "CUSTOMER_ID", nullable = false)
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Customer customer;

    @Column(name = "EXTERNAL_NUMBER", nullable = false)
    @NotNull
    private String externalNumber;

    @OnDeleteInverse(DeletePolicy.CASCADE)
    @JoinColumn(name = "COMPANY_ID", nullable = false)
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Company company;

    @Column(name = "TOTAL_AMOUNT", precision = 19, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "MARKS_EARNED", precision = 19, scale = 2)
    private BigDecimal marksEarned;

    @Column(name = "MARKS_SPENT", precision = 19, scale = 2)
    private BigDecimal marksSpent;

    @Column(name = "EXTERNAL_TRANSACTION_TIMESTAMP")
    private OffsetDateTime externalTransactionTimestamp;

    @Composition
    @OneToMany(mappedBy = "transactionn")
    private List<TransactionItem> items;

    @JoinColumn(name = "INITIAL_TRANSACTION_ID")
    @OneToOne(fetch = FetchType.LAZY)
    private Transaction initialTransaction;

    @Column(name = "IS_NEED_TO_ACTIVATE")
    private Boolean isNeedToActivate;

    @Column(name = "TIME_ACTIVATE")
    private OffsetDateTime timeActivate;

    public List<TransactionItem> getItems() {
        return items;
    }

    public void setItems(List<TransactionItem> items) {
        this.items = items;
    }

    public void setExternalTransactionTimestamp(OffsetDateTime externalTransactionTimestamp) {
        this.externalTransactionTimestamp = externalTransactionTimestamp;
    }

    public OffsetDateTime getExternalTransactionTimestamp() {
        return externalTransactionTimestamp;
    }

    public OffsetDateTime getTimeActivate() {
        return timeActivate;
    }

    public void setTimeActivate(OffsetDateTime timeActivate) {
        this.timeActivate = timeActivate;
    }

    public Boolean getIsNeedToActivate() {
        return isNeedToActivate;
    }

    public void setIsNeedToActivate(Boolean isNeedToActivate) {
        this.isNeedToActivate = isNeedToActivate;
    }

    public Transaction getInitialTransaction() {
        return initialTransaction;
    }

    public void setInitialTransaction(Transaction initialTransaction) {
        this.initialTransaction = initialTransaction;
    }

    public BigDecimal getMarksSpent() {
        return marksSpent;
    }

    public void setMarksSpent(BigDecimal marksSpent) {
        this.marksSpent = marksSpent;
    }

    public BigDecimal getMarksEarned() {
        return marksEarned;
    }

    public void setMarksEarned(BigDecimal marksEarned) {
        this.marksEarned = marksEarned;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public String getExternalNumber() {
        return externalNumber;
    }

    public void setExternalNumber(String externalNumber) {
        this.externalNumber = externalNumber;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

}