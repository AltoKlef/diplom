package com.alto.diplom.entity.transactions;

import com.alto.diplom.entity.items.Item;
import io.jmix.core.DeletePolicy;
import io.jmix.core.entity.annotation.JmixGeneratedValue;
import io.jmix.core.entity.annotation.OnDeleteInverse;
import io.jmix.core.metamodel.annotation.JmixEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

@JmixEntity
@Table(name = "TRANSACTION_ITEM", indexes = {
        @Index(name = "IDX_TRANSACTION_ITEM_TRANSACTIONN", columnList = "TRANSACTIONN_ID"),
        @Index(name = "IDX_TRANSACTION_ITEM_ITEM", columnList = "ITEM_ID")
})
@Entity
public class TransactionItem {
    @JmixGeneratedValue
    @Column(name = "ID", nullable = false)
    @Id
    private UUID id;

    @OnDeleteInverse(DeletePolicy.CASCADE)
    @JoinColumn(name = "TRANSACTIONN_ID", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Transaction transactionn;

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

    public BigDecimal getDiscount() {
        return discount;
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
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

    public BigDecimal getTotalSum() {
        return totalSum;
    }

    public void setTotalSum(BigDecimal totalSum) {
        this.totalSum = totalSum;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public Transaction getTransactionn() {
        return transactionn;
    }

    public void setTransactionn(Transaction transactionn) {
        this.transactionn = transactionn;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

}