package com.alto.diplom.entity.core;

import com.alto.diplom.core.HasCompany;
import io.jmix.core.entity.annotation.JmixGeneratedValue;
import io.jmix.core.metamodel.annotation.InstanceName;
import io.jmix.core.metamodel.annotation.JmixEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@JmixEntity
@Table(name = "CUSTOMER_GROUP")
@Entity
@Getter
@Setter
public class CustomerGroup implements HasCompany {
    @JmixGeneratedValue
    @Column(name = "ID", nullable = false)
    @Id
    private UUID id;


    @InstanceName
    @Column(name = "NAME")
    private String name;

    @JoinColumn(name = "COMPANY_ID", nullable = false)
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Company company;


}