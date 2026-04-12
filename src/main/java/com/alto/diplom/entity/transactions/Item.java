package com.alto.diplom.entity.transactions;

import com.alto.diplom.entity.ItemGroup;
import io.jmix.core.annotation.DeletedBy;
import io.jmix.core.annotation.DeletedDate;
import io.jmix.core.entity.annotation.JmixGeneratedValue;
import io.jmix.core.metamodel.annotation.InstanceName;
import io.jmix.core.metamodel.annotation.JmixEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@JmixEntity
@Table(name = "ITEM", indexes = {
        @Index(name = "IDX_ITEM_ITEM_GROUP", columnList = "ITEM_GROUP_ID")
})
@Entity
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
    private Boolean canPayByMark;

    @Column(name = "CAN_MARK_INCREASE")
    private Boolean canMarkIncrease;

    @JoinColumn(name = "ITEM_GROUP_ID")
    @ManyToOne(fetch = FetchType.LAZY)
    private ItemGroup itemGroup;

    @DeletedDate
    @Column(name = "DELETED_DATE")
    private OffsetDateTime deletedDate;

    public ItemGroup getItemGroup() {
        return itemGroup;
    }

    public void setItemGroup(ItemGroup itemGroup) {
        this.itemGroup = itemGroup;
    }

    public Boolean getCanMarkIncrease() {
        return canMarkIncrease;
    }

    public void setCanMarkIncrease(Boolean canMarkIncrease) {
        this.canMarkIncrease = canMarkIncrease;
    }

    public Boolean getCanPayByMark() {
        return canPayByMark;
    }

    public void setCanPayByMark(Boolean canPayByMark) {
        this.canPayByMark = canPayByMark;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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