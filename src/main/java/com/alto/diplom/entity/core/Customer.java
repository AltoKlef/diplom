package com.alto.diplom.entity.core;

import com.alto.diplom.core.HasCompany;
import io.jmix.core.annotation.DeletedBy;
import io.jmix.core.annotation.DeletedDate;
import io.jmix.core.entity.annotation.JmixGeneratedValue;
import io.jmix.core.metamodel.annotation.InstanceName;
import io.jmix.core.metamodel.annotation.JmixEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;

import java.time.OffsetDateTime;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@JmixEntity
@Table(name = "CUSTOMER", indexes = {
        @Index(name = "IDX_CUSTOMER_COMPANY_ID", columnList = "COMPANY_ID")
})
@Entity
@Getter
@Setter
public class Customer implements HasCompany {
    @JmixGeneratedValue
    @Column(name = "ID", nullable = false)
    @Id
    private UUID id;

    @Column(name = "PHONE", nullable = false, length = 20)
    @NotNull
    @Pattern(regexp = "^\\+?[78][-\\s]?\\d{3}[-\\s]?\\d{3}[-\\s]?\\d{2}[-\\s]?\\d{2}$",
            message = "Некорректный формат телефона")
    private String phone;

    @InstanceName
    @Column(name = "FIRST_NAME", nullable = false)
    @NotNull
    private String firstName;

    @Column(name = "LAST_NAME")
    private String lastName;

    @Column(name = "IS_VERIFIED")
    private Boolean isVerified = false;

    @Column(name = "IS_BLOCKED")
    private Boolean isBlocked = false;

    @Temporal(TemporalType.DATE)
    @Column(name = "BIRTHDAY")
    private Date birthday;

    @JoinColumn(name = "COMPANY_ID", nullable = false)
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Company company;

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

    @JoinTable(name = "CUSTOMER_CUSTOMER_GROUP_LINK",
            joinColumns = @JoinColumn(name = "CUSTOMER_ID"),
            inverseJoinColumns = @JoinColumn(name = "CUSTOMER_GROUP_ID"))
    @ManyToMany
    private Set<CustomerGroup> customerGroups;

}