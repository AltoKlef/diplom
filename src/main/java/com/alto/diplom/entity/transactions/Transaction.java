package com.alto.diplom.entity.transactions;

import com.alto.diplom.core.HasCompany;
import com.alto.diplom.entity.MarkStrategy;
import com.alto.diplom.entity.core.Company;
import com.alto.diplom.entity.core.Customer;
import io.jmix.core.DeletePolicy;
import io.jmix.core.entity.annotation.JmixGeneratedValue;
import io.jmix.core.entity.annotation.OnDelete;
import io.jmix.core.entity.annotation.OnDeleteInverse;
import io.jmix.core.metamodel.annotation.Composition;
import io.jmix.core.metamodel.annotation.InstanceName;
import io.jmix.core.metamodel.annotation.JmixEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@JmixEntity
@Table(name = "TRANSACTION_", indexes = {
        @Index(name = "IDX_TRANSACTION__CUSTOMER", columnList = "CUSTOMER_ID"),
        @Index(name = "IDX_TRANSACTION__COMPANY", columnList = "COMPANY_ID"),
        @Index(name = "IDX_TRANSACTION__INITIAL_TRANSACTION", columnList = "INITIAL_TRANSACTION_ID")
})
@Entity(name = "Transaction_")
@Getter
@Setter
public class Transaction implements HasCompany {
    @JmixGeneratedValue
    @Column(name = "ID", nullable = false)
    @Id
    private UUID id;

    @NotNull
    @Column(name = "MARK_STRATEGY", nullable = false)
    private MarkStrategy markStrategy;

    @OnDeleteInverse(DeletePolicy.CASCADE)
    @JoinColumn(name = "CUSTOMER_ID", nullable = false)
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Customer customer;

    @InstanceName
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
    @OneToMany(mappedBy = "transactionn", cascade = CascadeType.ALL, orphanRemoval = true) // ДОБАВЬ ЭТО
    private List<TransactionItem> items = new ArrayList<>();

    @JoinColumn(name = "INITIAL_TRANSACTION_ID")
    @OnDelete(DeletePolicy.CASCADE)
    @OneToOne(fetch = FetchType.LAZY)
    private Transaction initialTransaction;

    @Column(name = "IS_NEED_TO_ACTIVATE")
    private Boolean isNeedToActivate;

    @Column(name = "TIME_ACTIVATE")
    private OffsetDateTime timeActivate;

}