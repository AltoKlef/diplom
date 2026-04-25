package com.alto.diplom.entity.transactions;

import com.alto.diplom.entity.items.Item;
import io.jmix.core.DeletePolicy;
import io.jmix.core.entity.annotation.JmixGeneratedValue;
import io.jmix.core.entity.annotation.OnDeleteInverse;
import io.jmix.core.metamodel.annotation.InstanceName;
import io.jmix.core.metamodel.annotation.JmixEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@JmixEntity
@Table(name = "TRANSACTION_ITEM", indexes = {
        @Index(name = "IDX_TRANSACTION_ITEM_TRANSACTIONN", columnList = "TRANSACTIONN_ID"),
        @Index(name = "IDX_TRANSACTION_ITEM_ITEM", columnList = "ITEM_ID")
})
@Entity
@Getter
@Setter
public class TransactionItem {
    @JmixGeneratedValue
    @Column(name = "ID", nullable = false)
    @Id
    private UUID id;

    @OnDeleteInverse(DeletePolicy.CASCADE)
    @JoinColumn(name = "TRANSACTIONN_ID", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Transaction transactionn;

    @InstanceName
    @OnDeleteInverse(DeletePolicy.UNLINK)
    @JoinColumn(name = "ITEM_ID")
    @ManyToOne(fetch = FetchType.LAZY)
    private Item item;

    @Column(name = "QUANTITY", nullable = false, precision = 19, scale = 2)
    @NotNull
    private BigDecimal quantity;

    @Column(name = "PRICE", nullable = false, precision = 19, scale = 2)
    @NotNull
    private BigDecimal price;

    @Column(name = "TOTAL_SUM", nullable = false, precision = 19, scale = 2)
    @NotNull
    private BigDecimal totalSum;

    @Column(name = "MARKS_EARNED", precision = 19, scale = 2)
    private BigDecimal marksEarned;

    @Column(name = "MARKS_SPENT", precision = 19, scale = 2)
    private BigDecimal marksSpent;

    @Column(name = "DISCOUNT", precision = 19, scale = 2)
    private BigDecimal discount;



}