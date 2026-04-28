package com.alto.diplom.entity.config;

import com.alto.diplom.core.HasCompany;
import com.alto.diplom.entity.core.Company;
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
@Table(name = "ON_REGISTER_CONFIG", indexes = {
        @Index(name = "IDX_ON_REGISTER_CONFIG_COMPANY", columnList = "COMPANY_ID")
})
@Entity
@Getter
@Setter
public class OnRegisterConfig implements HasCompany {
    @JmixGeneratedValue
    @Column(name = "ID", nullable = false)
    @Id
    private UUID id;

    @NotNull
    @OnDeleteInverse(DeletePolicy.CASCADE)
    @JoinColumn(name = "COMPANY_ID", nullable = false)
    @OneToOne(fetch = FetchType.LAZY, optional = false) // Одна настройка на компанию
    private Company company;

    @Column(name = "IS_ACTIVE", nullable = false)
    @NotNull
    private Boolean isActive = false;

    @InstanceName
    @Column(name = "MARK_INCREASE", precision = 19, scale = 2)
    private BigDecimal markIncrease; // Сумма приветственных баллов

    @Column(name = "DAYS_TO_DELAYED_ACTIVATION")
    private Integer daysToDelayedActivation; // Через сколько дней баллы станут активными

    // Геттеры и сеттеры...
}