package com.alto.diplom.view.transactionconfig;

import com.alto.diplom.entity.TransactionConfig;
import com.alto.diplom.repository.TransactionConfigRepository;
import com.alto.diplom.view.main.MainView;
import com.vaadin.flow.router.Route;
import io.jmix.core.FetchPlan;
import io.jmix.core.SaveContext;
import io.jmix.flowui.view.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Route(value = "transaction-configs/:id", layout = MainView.class)
@ViewController(id = "TransactionConfig.detail")
@ViewDescriptor(path = "transaction-config-detail-view.xml")
@EditedEntityContainer("transactionConfigDc")
public class TransactionConfigDetailView extends StandardDetailView<TransactionConfig> {

    @Autowired
    private TransactionConfigRepository repository;

    @Install(to = "transactionConfigDl", target = Target.DATA_LOADER, subject = "loadFromRepositoryDelegate")
    private Optional<TransactionConfig> loadDelegate(UUID id, FetchPlan fetchPlan) {
        return repository.findById(id, fetchPlan);
    }

    @Install(target = Target.DATA_CONTEXT)
    private Set<Object> saveDelegate(SaveContext saveContext) {
        return Set.of(repository.save(getEditedEntity()));
    }
}