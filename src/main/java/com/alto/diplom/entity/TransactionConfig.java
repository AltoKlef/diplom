package com.alto.diplom.entity;

import com.alto.diplom.core.HasCompany;
import com.alto.diplom.entity.core.Company;
import io.jmix.core.DeletePolicy;
import io.jmix.core.annotation.DeletedBy;
import io.jmix.core.annotation.DeletedDate;
import io.jmix.core.entity.annotation.JmixGeneratedValue;
import io.jmix.core.entity.annotation.OnDeleteInverse;
import io.jmix.core.metamodel.annotation.DependsOnProperties;
import io.jmix.core.metamodel.annotation.InstanceName;
import io.jmix.core.metamodel.annotation.JmixEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;

import java.time.OffsetDateTime;
import java.util.UUID;

@JmixEntity
@Table(name = "TRANSACTION_CONFIG", indexes = {
        @Index(name = "IDX_TRANSACTION_CONFIG_COMPANY", columnList = "COMPANY_ID")
})
@Entity
@Getter
@Setter
public class TransactionConfig implements HasCompany {
    @JmixGeneratedValue
    @Column(name = "ID", nullable = false)
    @Id
    private UUID id;

    @Column(name = "DAYS_TO_MARK_ACTIVATION", nullable = false)
    private Integer daysToMarkActivation;

    @Column(name = "MIN_SUM_TO_INCREASE")
    private Integer minSumToIncrease;

    @NotNull
    @Column(name = "MARK_INCREASE_MODE", nullable = false)
    private MarkIncreaseMode markIncreaseMode;

    @OnDeleteInverse(DeletePolicy.CASCADE)
    @JoinColumn(name = "COMPANY_ID", nullable = false)
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Company company;

    @InstanceName
    @DependsOnProperties({"daysToMarkActivation", "markIncreaseMode"})
    public String getInstanceName() {
        return String.format("Активация: %s дн., Режим: %s",
                daysToMarkActivation, markIncreaseMode.name());
    }

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


}