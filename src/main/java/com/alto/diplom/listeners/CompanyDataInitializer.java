package com.alto.diplom.listeners;

import com.alto.diplom.core.HasCompany;
import com.alto.diplom.entity.core.User;
import io.jmix.core.security.CurrentAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
//import org.springframework.stereotype.Component;
//import io.jmix.core.event.EntityInitializingEvent;
//@Component
//public class CompanyDataInitializer {
//
//    @Autowired
//    private CurrentAuthentication currentAuthentication;
//
//    @EventListener
//    public void onInitEntity(EntityInitializingEvent<?> event) {
//        Object entity = event.getEntity();
//
//        // Если сущность умеет хранить компанию (твой интерфейс HasCompany)
//        if (entity instanceof HasCompany) {
//            // Берем юзера, которого JwtFilter положил в SecurityContext
//            Object principal = currentAuthentication.getUser();
//
//            if (principal instanceof User user) {
//                if (user.getCompany() != null) {
//                    // Подставляем компанию автоматически
//                    ((HasCompany) entity).setCompany(user.getCompany());
//                }
//            }
//        }
//    }
//}