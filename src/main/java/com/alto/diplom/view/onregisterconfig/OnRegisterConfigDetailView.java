package com.alto.diplom.view.onregisterconfig;

import com.alto.diplom.entity.config.OnRegisterConfig;
import com.alto.diplom.repository.OnRegisterConfigRepository;
import com.alto.diplom.view.main.MainView;
import com.vaadin.flow.router.Route;
import io.jmix.core.FetchPlan;
import io.jmix.core.SaveContext;
import io.jmix.flowui.view.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Route(value = "on-register-configs/:id", layout = MainView.class)
@ViewController(id = "OnRegisterConfig.detail")
@ViewDescriptor(path = "on-register-config-detail-view.xml")
@EditedEntityContainer("onRegisterConfigDc")
public class OnRegisterConfigDetailView extends StandardDetailView<OnRegisterConfig> {

    @Autowired
    private OnRegisterConfigRepository repository;

    @Install(to = "onRegisterConfigDl", target = Target.DATA_LOADER, subject = "loadFromRepositoryDelegate")
    private Optional<OnRegisterConfig> loadDelegate(UUID id, FetchPlan fetchPlan) {
        return repository.findById(id, fetchPlan);
    }

    @Install(target = Target.DATA_CONTEXT)
    private Set<Object> saveDelegate(SaveContext saveContext) {
        return Set.of(repository.save(getEditedEntity()));
    }
}