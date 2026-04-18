package com.alto.diplom.entity.loyalty;

import com.alto.diplom.core.HasCompany;
import com.alto.diplom.entity.core.Company;
import com.alto.diplom.entity.core.Customer;
import io.jmix.core.DeletePolicy;
import io.jmix.core.entity.annotation.JmixGeneratedValue;
import io.jmix.core.entity.annotation.OnDeleteInverse;
import io.jmix.core.metamodel.annotation.JmixEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@JmixEntity
@Table(name = "CUSTOMER_BONUS_ACCOUNT", indexes = {
        @Index(name = "IDX_CUSTOMER_BONUS_ACCOUNT_CUSTOMER", columnList = ""),
        @Index(name = "IDX_CUSTOMER_BONUS_ACCOUNT_COMPANY", columnList = "COMPANY_ID"),
        @Index(name = "IDX_CUSTOMER_BONUS_ACCOUNT_LOYALTY_LEVEL", columnList = "LOYALTY_LEVEL_ID")
})
@Entity
public class CustomerBonusAccount implements HasCompany {
    @JmixGeneratedValue
    @Column(name = "ID", nullable = false)
    @Id
    private UUID id;

    @OnDeleteInverse(DeletePolicy.CASCADE)
    @JoinColumn(name = "CUSTOMER_ID", nullable = false)
    @NotNull
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    private Customer customer;

    @JoinColumn(name = "COMPANY_ID", nullable = false)
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Company company;

    @OnDeleteInverse(DeletePolicy.UNLINK)
    @JoinColumn(name = "LOYALTY_LEVEL_ID")
    @ManyToOne(fetch = FetchType.LAZY)
    private LoyaltyLevel loyaltyLevel;

    @Column(name = "MARK", precision = 19, scale = 2)
    private BigDecimal mark;

    @LastModifiedBy
    @Column(name = "LAST_MODIFIED_BY")
    private String lastModifiedBy;

    @LastModifiedDate
    @Column(name = "LAST_MODIFIED_DATE")
    private OffsetDateTime lastModifiedDate;

    public BigDecimal getMark() {
        return mark;
    }

    public void setMark(BigDecimal mark) {
        this.mark = mark;
    }

    public LoyaltyLevel getLoyaltyLevel() {
        return loyaltyLevel;
    }

    public void setLoyaltyLevel(LoyaltyLevel loyaltyLevel) {
        this.loyaltyLevel = loyaltyLevel;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
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

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

}