package com.alto.diplom.view.loyaltylevel;

import com.alto.diplom.entity.loyalty.LoyaltyLevel;
import com.alto.diplom.repository.LoyaltyLevelRepository;
import com.alto.diplom.view.main.MainView;
import com.vaadin.flow.router.Route;
import io.jmix.core.FetchPlan;
import io.jmix.core.SaveContext;
import io.jmix.flowui.view.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Route(value = "loyalty-levels/:id", layout = MainView.class)
@ViewController(id = "LoyaltyLevel.detail")
@ViewDescriptor(path = "loyalty-level-detail-view.xml")
@EditedEntityContainer("loyaltyLevelDc")
public class LoyaltyLevelDetailView extends StandardDetailView<LoyaltyLevel> {

    @Autowired
    private LoyaltyLevelRepository repository;

    @Install(to = "loyaltyLevelDl", target = Target.DATA_LOADER, subject = "loadFromRepositoryDelegate")
    private Optional<LoyaltyLevel> loadDelegate(UUID id, FetchPlan fetchPlan) {
        return repository.findById(id, fetchPlan);
    }

    @Install(target = Target.DATA_CONTEXT)
    private Set<Object> saveDelegate(SaveContext saveContext) {
        return Set.of(repository.save(getEditedEntity()));
    }
}