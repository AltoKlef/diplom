package com.alto.diplom.view.customergroup.customergroup;

import com.alto.diplom.entity.core.CustomerGroup;
import com.alto.diplom.repository.CustomerGroupRepository;
import com.alto.diplom.view.main.MainView;
import com.vaadin.flow.router.Route;
import io.jmix.core.FetchPlan;
import io.jmix.core.SaveContext;
import io.jmix.flowui.view.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Route(value = "customer-groups/:id", layout = MainView.class)
@ViewController(id = "CustomerGroup.detail")
@ViewDescriptor(path = "customer-group-detail-view.xml")
@EditedEntityContainer("customerGroupDc")
public class CustomerGroupDetailView extends StandardDetailView<CustomerGroup> {

    @Autowired
    private CustomerGroupRepository repository;

    @Install(to = "customerGroupDl", target = Target.DATA_LOADER, subject = "loadFromRepositoryDelegate")
    private Optional<CustomerGroup> loadDelegate(UUID id, FetchPlan fetchPlan) {
        return repository.findById(id, fetchPlan);
    }

    @Install(target = Target.DATA_CONTEXT)
    private Set<Object> saveDelegate(SaveContext saveContext) {
        return Set.of(repository.save(getEditedEntity()));
    }
}