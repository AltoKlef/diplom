package com.alto.diplom.entity.loyalty;

import com.alto.diplom.entity.config.LoyaltyProgramConfig;
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
import lombok.Getter;
import lombok.Setter;
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
@Getter
@Setter
public class LoyaltyLevel {
    @JmixGeneratedValue
    @Column(name = "ID", nullable = false)
    @Id
    private UUID id;

    @PositiveOrZero
    @Column(name = "DISCOUNT", nullable = false, precision = 19, scale = 2)
    @NotNull
    private BigDecimal discount = BigDecimal.ZERO;

    @PositiveOrZero
    @Column(name = "CAHSBACK_RATE", nullable = false, precision = 19, scale = 2)
    @NotNull
    private BigDecimal cashbackRate;

    @PositiveOrZero
    @Column(name = "MARKSPEND_RATE", nullable = false, precision = 19, scale = 2)
    @NotNull
    private BigDecimal markspendRate;

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

}