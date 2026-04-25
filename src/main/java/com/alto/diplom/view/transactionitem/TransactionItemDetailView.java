package com.alto.diplom.view.transactionitem;

import com.alto.diplom.entity.items.Item;
import com.alto.diplom.entity.transactions.TransactionItem;
import com.alto.diplom.repository.TransactionItemRepository;
import com.alto.diplom.view.main.MainView;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.router.Route;
import io.jmix.core.FetchPlan;
import io.jmix.core.SaveContext;
import io.jmix.flowui.view.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
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

    @Subscribe("itemField") // Поле, где выбираем товар из каталога
    public void onItemFieldValueChange(final HasValue.ValueChangeEvent<Item> event) {
        Item selectedItem = event.getValue();
        TransactionItem entity = getEditedEntity();

        if (selectedItem != null) {
            // Копируем актуальную цену из каталога в строку чека
            entity.setPrice(selectedItem.getPrice());

            // Если количество уже введено, считаем сумму
            if (entity.getQuantity() == null) {
                entity.setQuantity(BigDecimal.ONE); // Ставим 1 по умолчанию
            }
            recalculateLine();
        }
    }

    private void recalculateLine() {
        TransactionItem entity = getEditedEntity();
        if (entity.getPrice() != null && entity.getQuantity() != null) {
            entity.setTotalSum(entity.getPrice().multiply(entity.getQuantity()));
        }
    }
}