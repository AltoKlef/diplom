package com.alto.diplom.security;

import com.alto.diplom.entity.core.User;
import io.jmix.securitydata.user.AbstractDatabaseUserRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;

@Primary
@Component("ls_UserRepository")
public class DatabaseUserRepository extends AbstractDatabaseUserRepository<User> {

    @Override
    protected Class<User> getUserClass() {
        return User.class;
    }

    // Это оставляем как было
    @Override
    protected void initSystemUser(final User systemUser) {
        final Collection<GrantedAuthority> authorities = getGrantedAuthoritiesBuilder()
                .addResourceRole(FullAccessRole.CODE)
                .build();
        systemUser.setAuthorities(authorities);
    }

    // --- ВОТ ЭТОТ МЕТОД ДЛЯ JMIX 2.x ---
    @Override
    public User loadUserByUsername(String username) throws UsernameNotFoundException {
        // 1. Загружаем пользователя стандартным способом (из базы + его роли из БД)
        User user = (User) super.loadUserByUsername(username);

        // 2. Если это не админ, добавляем ему Row-level роль программно
        if (!"admin".equals(username)) {
            Collection<GrantedAuthority> authorities = new ArrayList<>(user.getAuthorities());

            // Добавляем нашу роль через билдер
            authorities.addAll(getGrantedAuthoritiesBuilder()
                    .addRowLevelRole("company-isolation") // Тот самый код из @RowLevelRole
                    .build());

            // Устанавливаем обновленный список прав
            user.setAuthorities(authorities);
        }

        return user;
    }

    @Override
    protected void initAnonymousUser(final User anonymousUser) {
    }
}