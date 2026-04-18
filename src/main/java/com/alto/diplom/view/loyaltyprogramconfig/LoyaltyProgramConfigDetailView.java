package com.alto.diplom.view.loyaltyprogramconfig;

import com.alto.diplom.entity.config.LoyaltyProgramConfig;
import com.alto.diplom.repository.LoyaltyProgramConfigRepository;
import com.alto.diplom.view.main.MainView;
import com.vaadin.flow.router.Route;
import io.jmix.core.FetchPlan;
import io.jmix.core.SaveContext;
import io.jmix.flowui.view.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Route(value = "loyalty-program-configs/:id", layout = MainView.class)
@ViewController(id = "LoyaltyProgramConfig.detail")
@ViewDescriptor(path = "loyalty-program-config-detail-view.xml")
@EditedEntityContainer("loyaltyProgramConfigDc")
public class LoyaltyProgramConfigDetailView extends StandardDetailView<LoyaltyProgramConfig> {

    @Autowired
    private LoyaltyProgramConfigRepository repository;

    @Install(to = "loyaltyProgramConfigDl", target = Target.DATA_LOADER, subject = "loadFromRepositoryDelegate")
    private Optional<LoyaltyProgramConfig> loadDelegate(UUID id, FetchPlan fetchPlan) {
        return repository.findById(id, fetchPlan);
    }

    @Install(target = Target.DATA_CONTEXT)
    private Set<Object> saveDelegate(SaveContext saveContext) {
        return Set.of(repository.save(getEditedEntity()));
    }
}