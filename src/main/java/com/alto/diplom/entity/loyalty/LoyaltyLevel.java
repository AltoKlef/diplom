package com.alto.diplom.entity.loyalty;

import io.jmix.core.DeletePolicy;
import io.jmix.core.annotation.DeletedBy;
import io.jmix.core.annotation.DeletedDate;
import io.jmix.core.entity.annotation.JmixGeneratedValue;
import io.jmix.core.entity.annotation.OnDeleteInverse;
import io.jmix.core.metamodel.annotation.JmixEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@JmixEntity
@Table(name = "LOYALTY_LEVEL", indexes = {
        @Index(name = "IDX_LOYALTY_LEVEL_LOYALTY_PROGRAM_CONFIG", columnList = "LOYALTY_PROGRAM_CONFIG_ID")
})
@Entity
public class LoyaltyLevel {
    @JmixGeneratedValue
    @Column(name = "ID", nullable = false)
    @Id
    private UUID id;

    @PositiveOrZero
    @Column(name = "DISCOUNT", nullable = false, precision = 19, scale = 2)
    @NotNull
    private BigDecimal discount;

    @PositiveOrZero
    @Column(name = "CAHSBACK_RATE", nullable = false, precision = 19, scale = 2)
    @NotNull
    private BigDecimal cahsback_rate;

    @PositiveOrZero
    @Column(name = "MARKSPEND_RATE", nullable = false, precision = 19, scale = 2)
    @NotNull
    private BigDecimal markspend_rate;

    @Positive
    @Column(name = "NUMBER_", nullable = false)
    @NotNull
    private Short number;

    @PositiveOrZero
    @Column(name = "REQUIRED_SUM", nullable = false)
    @NotNull
    private Integer required_sum;

    @Column(name = "EFFECTIVE_CASH", precision = 19, scale = 2)
    private BigDecimal effectiveCash;

    @OnDeleteInverse(DeletePolicy.CASCADE)
    @JoinColumn(name = "LOYALTY_PROGRAM_CONFIG_ID", nullable = false)
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private LoyaltyProgramConfig loyaltyProgramConfig;

    @DeletedBy
    @Column(name = "DELETED_BY")
    private String deletedBy;

    @DeletedDate
    @Column(name = "DELETED_DATE")
    private OffsetDateTime deletedDate;

    @CreatedBy
    @Column(name = "CREATED_BY")
    private String createdBy;

    @CreatedDate
    @Column(name = "CREATED_DATE")
    private OffsetDateTime createdDate;

    @LastModifiedBy
    @Column(name = "LAST_MODIFIED_BY")
    private String lastModifiedBy;

    @LastModifiedDate
    @Column(name = "LAST_MODIFIED_DATE")
    private OffsetDateTime lastModifiedDate;

    public LoyaltyProgramConfig getLoyaltyProgramConfig() {
        return loyaltyProgramConfig;
    }

    public void setLoyaltyProgramConfig(LoyaltyProgramConfig loyaltyProgramConfig) {
        this.loyaltyProgramConfig = loyaltyProgramConfig;
    }

    public BigDecimal getEffectiveCash() {
        return effectiveCash;
    }

    public void setEffectiveCash(BigDecimal effectiveCash) {
        this.effectiveCash = effectiveCash;
    }

    public Integer getRequired_sum() {
        return required_sum;
    }

    public void setRequired_sum(Integer required_sum) {
        this.required_sum = required_sum;
    }

    public Short getNumber() {
        return number;
    }

    public void setNumber(Short number) {
        this.number = number;
    }

    public BigDecimal getMarkspend_rate() {
        return markspend_rate;
    }

    public void setMarkspend_rate(BigDecimal markspend_rate) {
        this.markspend_rate = markspend_rate;
    }

    public BigDecimal getCahsback_rate() {
        return cahsback_rate;
    }

    public void setCahsback_rate(BigDecimal cahsback_rate) {
        this.cahsback_rate = cahsback_rate;
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }

    public OffsetDateTime getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(OffsetDateTime lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public OffsetDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(OffsetDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public OffsetDateTime getDeletedDate() {
        return deletedDate;
    }

    public void setDeletedDate(OffsetDateTime deletedDate) {
        this.deletedDate = deletedDate;
    }

    public String getDeletedBy() {
        return deletedBy;
    }

    public void setDeletedBy(String deletedBy) {
        this.deletedBy = deletedBy;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

}