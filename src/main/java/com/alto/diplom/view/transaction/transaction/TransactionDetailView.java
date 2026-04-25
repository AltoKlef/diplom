package com.alto.diplom.view.transaction.transaction;

import com.alto.diplom.core.services.LoyaltyService;
import com.alto.diplom.entity.items.Item;
import com.alto.diplom.entity.transactions.Transaction;
import com.alto.diplom.entity.transactions.TransactionItem;
import com.alto.diplom.repository.TransactionRepository;
import com.alto.diplom.view.itemgroup.ItemGroupListView;
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

    // В TransactionDetailView
    @Subscribe(id = "itemsDc", target = Target.DATA_CONTAINER)
    public void onItemsDcCollectionChange(final CollectionContainer.CollectionChangeEvent<TransactionItem> event) {
        // Считаем общую сумму чека (Total Amount)
        BigDecimal total = getEditedEntity().getItems().stream()
                .map(TransactionItem::getTotalSum)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        getEditedEntity().setTotalAmount(total);

        // Вызываем твой расчет баллов
        loyaltyService.calculateAccrual(getEditedEntity());
    }


    @Subscribe("addFromCatalogBtn")
    public void onAddFromCatalogBtnClick(final ClickEvent<JmixButton> event) {
        // Не сохраняем в переменную с типом <ItemGroupListView>,
        // так как Jmix вернет View<?> при поиске по String ID
        dialogWindows.lookup(this, Item.class)
                .withViewId("ItemGroup.list")
                .withSelectHandler(items -> {
                    for (Item catalogItem : items) {
                        addTransactionItem(catalogItem);
                    }
                })
                .build()
                .open();
    }
    private void addTransactionItem(Item catalogItem) {
        // 1. Создаем новую "строку чека" через metadata
        TransactionItem newItem = metadata.create(TransactionItem.class);

        // 2. Связываем её с выбранным товаром из каталога
        newItem.setItem(catalogItem);

        // 3. Копируем цену и ставим количество по умолчанию
        newItem.setPrice(catalogItem.getPrice());
        newItem.setQuantity(BigDecimal.ONE);
        newItem.setTotalSum(catalogItem.getPrice()); // Цена * 1

        // 4. Привязываем к текущей транзакции
        newItem.setTransactionn(getEditedEntity());

        // 5. Добавляем в контейнер (таблица сразу её увидит)
        itemsDc.getMutableItems().add(newItem);

        // 6. Вызываем пересчет (твой метод с LoyaltyService)
        loyaltyService.calculateAccrual(newItem.getTransactionn());
    }
}