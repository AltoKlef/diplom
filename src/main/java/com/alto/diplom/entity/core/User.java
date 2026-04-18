package com.alto.diplom.entity.core;

import com.alto.diplom.core.HasCompany;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import io.jmix.core.HasTimeZone;
import io.jmix.core.annotation.Secret;
import io.jmix.core.entity.annotation.JmixGeneratedValue;
import io.jmix.core.entity.annotation.SystemLevel;
import io.jmix.core.metamodel.annotation.DependsOnProperties;
import io.jmix.core.metamodel.annotation.InstanceName;
import io.jmix.core.metamodel.annotation.JmixEntity;
import io.jmix.security.authentication.JmixUserDetails;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

@JmixEntity
@Entity(name = "ls_User")
@Table(name = "LS_USER", indexes = {
        @Index(name = "IDX_LS_USER_ON_USERNAME", columnList = "USERNAME", unique = true)
})
@Getter
@Setter
public class User implements JmixUserDetails, HasTimeZone, HasCompany {

    @Id
    @Column(name = "ID")
    @JmixGeneratedValue
    private UUID id;

    @JoinColumn(name = "COMPANY_ID")
    @ManyToOne(fetch = FetchType.LAZY)
    private Company company;

    @Version
    @Column(name = "VERSION", nullable = false)
    private Integer version;

    @Column(name = "USERNAME", nullable = false)
    private String username;

    @Secret
    @SystemLevel
    @Column(name = "PASSWORD")
    private String password;

    @Column(name = "FIRST_NAME")
    private String firstName;

    @Column(name = "LAST_NAME")
    private String lastName;

    @Email
    @Column(name = "EMAIL")
    private String email;

    @Column(name = "ACTIVE")
    private Boolean active = true;

    @Column(name = "TIME_ZONE_ID")
    private String timeZoneId;

    @Transient
    private Collection<? extends GrantedAuthority> authorities;


    @Override
    public String getUsername() {
        return username;
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities != null ? authorities : Collections.emptyList();
    }

    @Override
    public void setAuthorities(final Collection<? extends GrantedAuthority> authorities) {
        this.authorities = authorities;
    }

    @Override
    public boolean isEnabled() {
        return Boolean.TRUE.equals(active);
    }

    @InstanceName
    @DependsOnProperties({"firstName", "lastName", "username"})
    public String getDisplayName() {
        return String.format("%s %s [%s]", (firstName != null ? firstName : ""),
                (lastName != null ? lastName : ""), username).trim();
    }

    @Override
    public String getTimeZoneId() {
        return timeZoneId;
    }

    @Override
    public boolean isAutoTimeZone() {
        return true;
    }

}