package com.alto.diplom.listeners.initializers;

import com.alto.diplom.core.HasCompany;
import com.alto.diplom.entity.core.User;
import io.jmix.core.security.CurrentAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import io.jmix.core.EntityInitializer;


@Component
public class CompanyEntityInitializer implements EntityInitializer {

    @Autowired
    private CurrentAuthentication currentAuthentication;

    @Override
    public void initEntity(Object entity) {
        // Проверяем, реализует ли сущность наш интерфейс
        if (entity instanceof HasCompany hasCompany) {
            // Проверяем, залогинен ли кто-то (чтобы не упасть при системных задачах)
            if (currentAuthentication.isSet()) {
                User user = (User) currentAuthentication.getUser();

                // Если у пользователя есть компания, а у сущности еще нет — сетаем
                if (user.getCompany() != null && hasCompany.getCompany() == null) {
                    hasCompany.setCompany(user.getCompany());
                }
            }
        }
    }
}