package com.alto.diplom.view.transaction.transaction;

import com.alto.diplom.core.loyalty.LoyaltyService;
import com.alto.diplom.entity.TransactionParameters;
import com.alto.diplom.entity.items.Item;
import com.alto.diplom.entity.transactions.Transaction;
import com.alto.diplom.entity.transactions.TransactionItem;
import com.alto.diplom.repository.TransactionRepository;
import com.alto.diplom.view.main.MainView;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.router.Route;
import io.jmix.core.DataManager;
import io.jmix.core.FetchPlan;
import io.jmix.core.Metadata;
import io.jmix.core.SaveContext;
import io.jmix.flowui.DialogWindows;
import io.jmix.flowui.kit.component.button.JmixButton;
import io.jmix.flowui.model.CollectionContainer;
import io.jmix.flowui.model.CollectionPropertyContainer;
import io.jmix.flowui.view.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Route(value = "transactions/:id", layout = MainView.class)
@ViewController(id = "Transaction_.detail")
@ViewDescriptor(path = "transaction-detail-view.xml")
@EditedEntityContainer("transactionDc")
public class TransactionDetailView extends StandardDetailView<Transaction> {
    @Autowired
    LoyaltyService loyaltyService;
    @Autowired
    private DialogWindows dialogWindows;
    @Autowired
    private Metadata metadata;
    @Autowired
    private TransactionRepository repository;
    @ViewComponent
    private CollectionPropertyContainer<TransactionItem> itemsDc;
    @Autowired
    private DataManager dataManager;
    @Install(to = "transactionDl", target = Target.DATA_LOADER, subject = "loadFromRepositoryDelegate")
    private Optional<Transaction> loadDelegate(UUID id, FetchPlan fetchPlan) {
        return repository.findById(id, fetchPlan);
    }

    @Install(target = Target.DATA_CONTEXT)
    private Set<Object> saveDelegate(SaveContext saveContext) {
        // saveContext уже содержит и Transaction, и все TransactionItem!
        // Просто отдаем его DataManager
        return dataManager.save(saveContext);
    }

    @Subscribe
    public void onInitEntity(final InitEntityEvent<Transaction> event) {
        // Генерируем UUID и устанавливаем его в новую сущность
        String randomUuid = UUID.randomUUID().toString();
        event.getEntity().setExternalNumber(randomUuid);
    }


}