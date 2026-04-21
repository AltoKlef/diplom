package com.alto.diplom.view.transactionitem;

import com.alto.diplom.entity.transactions.TransactionItem;
import com.alto.diplom.repository.TransactionItemRepository;
import com.alto.diplom.view.main.MainView;
import com.vaadin.flow.router.Route;
import io.jmix.core.FetchPlan;
import io.jmix.core.SaveContext;
import io.jmix.flowui.view.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Route(value = "transaction-items/:id", layout = MainView.class)
@ViewController(id = "TransactionItem.detail")
@ViewDescriptor(path = "transaction-item-detail-view.xml")
@EditedEntityContainer("transactionItemDc")
public class TransactionItemDetailView extends StandardDetailView<TransactionItem> {

    @Autowired
    private TransactionItemRepository repository;

    @Install(to = "transactionItemDl", target = Target.DATA_LOADER, subject = "loadFromRepositoryDelegate")
    private Optional<TransactionItem> loadDelegate(UUID id, FetchPlan fetchPlan) {
        return repository.findById(id, fetchPlan);
    }

    @Install(target = Target.DATA_CONTEXT)
    private Set<Object> saveDelegate(SaveContext saveContext) {
        return Set.of(repository.save(getEditedEntity()));
    }
}