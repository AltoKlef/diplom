package com.alto.diplom.view.transaction.transaction;

import com.alto.diplom.entity.transactions.Transaction;
import com.alto.diplom.repository.TransactionRepository;
import com.alto.diplom.view.main.MainView;
import com.vaadin.flow.router.Route;
import io.jmix.core.FetchPlan;
import io.jmix.core.SaveContext;
import io.jmix.flowui.view.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Route(value = "transactions/:id", layout = MainView.class)
@ViewController(id = "Transaction_.detail")
@ViewDescriptor(path = "transaction-detail-view.xml")
@EditedEntityContainer("transactionDc")
public class TransactionDetailView extends StandardDetailView<Transaction> {

    @Autowired
    private TransactionRepository repository;

    @Install(to = "transactionDl", target = Target.DATA_LOADER, subject = "loadFromRepositoryDelegate")
    private Optional<Transaction> loadDelegate(UUID id, FetchPlan fetchPlan) {
        return repository.findById(id, fetchPlan);
    }

    @Install(target = Target.DATA_CONTEXT)
    private Set<Object> saveDelegate(SaveContext saveContext) {
        // Transaction has the following @Composition attributes: items.
        // Make sure they have CascadeType.ALL in @OneToMany annotation.
        return Set.of(repository.save(getEditedEntity()));
    }

    @Subscribe
    public void onInitEntity(final InitEntityEvent<Transaction> event) {
        // Генерируем UUID и устанавливаем его в новую сущность
        String randomUuid = UUID.randomUUID().toString();
        event.getEntity().setExternalNumber(randomUuid);
    }
}