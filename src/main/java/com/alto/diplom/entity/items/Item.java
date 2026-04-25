package com.alto.diplom.entity.items;

import io.jmix.core.annotation.DeletedBy;
import io.jmix.core.annotation.DeletedDate;
import io.jmix.core.entity.annotation.JmixGeneratedValue;
import io.jmix.core.metamodel.annotation.InstanceName;
import io.jmix.core.metamodel.annotation.JmixEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@JmixEntity
@Table(name = "ITEM", indexes = {
        @Index(name = "IDX_ITEM_ITEM_GROUP", columnList = "ITEM_GROUP_ID")
})
@Entity
@Getter
@Setter
public class Item {
    @JmixGeneratedValue
    @Column(name = "ID", nullable = false)
    @Id
    private UUID id;

    @InstanceName
    @Column(name = "NAME")
    private String name;

    @Column(name = "PRICE", nullable = false, precision = 19, scale = 2)
    @NotNull
    private BigDecimal price;

    @Column(name = "EXTERNAL_ID")
    private String externalId;

    @DeletedBy
    @Column(name = "DELETED_BY")
    private String deletedBy;

    @Column(name = "CAN_PAY_BY_MARK")
    private Boolean canPayByMark = true;

    @Column(name = "CAN_MARK_INCREASE")
    private Boolean canMarkIncrease = true;

    @JoinColumn(name = "ITEM_GROUP_ID")
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    private ItemGroup itemGroup;

    @DeletedDate
    @Column(name = "DELETED_DATE")
    private OffsetDateTime deletedDate;

}