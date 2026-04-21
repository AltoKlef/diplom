package com.alto.diplom.entity.config;

import com.alto.diplom.core.HasCompany;
import com.alto.diplom.entity.core.Company;
import com.alto.diplom.entity.core.CustomerGroup;
import io.jmix.core.DeletePolicy;
import io.jmix.core.annotation.DeletedBy;
import io.jmix.core.annotation.DeletedDate;
import io.jmix.core.entity.annotation.JmixGeneratedValue;
import io.jmix.core.entity.annotation.OnDeleteInverse;
import io.jmix.core.metamodel.annotation.InstanceName;
import io.jmix.core.metamodel.annotation.JmixEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@JmixEntity
@Table(name = "LOYALTY_PROGRAM_CONFIG", indexes = {
        @Index(name = "IDX_LOYALTY_PROGRAM_CONFIG_COMPANY", columnList = "COMPANY_ID")
})
@Entity
@Getter
@Setter
public class LoyaltyProgramConfig implements HasCompany {
    @JmixGeneratedValue
    @Column(name = "ID", nullable = false)
    @Id
    private UUID id;

    @InstanceName
    @Column(name = "NAME")
    private String name;

    @Column(name = "PRIORITY", nullable = false)
    @NotNull
    private Integer priority;

    @Column(name = "IS_ACTIVE", nullable = false)
    @NotNull
    private Boolean isActive = false;

    @JoinTable(name = "LOYALTY_CONFIG_CUSTOMER_GROUP_LINK",
            joinColumns = @JoinColumn(name = "LOYALTY_PROGRAM_CONFIG_ID"),
            inverseJoinColumns = @JoinColumn(name = "CUSTOMER_GROUP_ID"))
    @ManyToMany
    private List<CustomerGroup> targetGroups;

    @OnDeleteInverse(DeletePolicy.CASCADE)
    @JoinColumn(name = "COMPANY_ID", nullable = false)
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Company company;

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

    @DeletedBy
    @Column(name = "DELETED_BY")
    private String deletedBy;

    @DeletedDate
    @Column(name = "DELETED_DATE")
    private OffsetDateTime deletedDate;

}