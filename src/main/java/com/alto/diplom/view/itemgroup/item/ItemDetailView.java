package com.alto.diplom.view.itemgroup.item;

import com.alto.diplom.entity.items.Item;
import com.alto.diplom.entity.transactions.Transaction;
import com.alto.diplom.repository.ItemRepository;
import com.alto.diplom.view.main.MainView;
import com.vaadin.flow.router.Route;
import io.jmix.core.FetchPlan;
import io.jmix.core.SaveContext;
import io.jmix.flowui.view.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Route(value = "items/:id", layout = MainView.class)
@ViewController(id = "Item.detail")
@ViewDescriptor(path = "item-detail-view.xml")
@EditedEntityContainer("itemDc")
public class ItemDetailView extends StandardDetailView<Item> {

    @Autowired
    private ItemRepository repository;

    @Install(to = "itemDl", target = Target.DATA_LOADER, subject = "loadFromRepositoryDelegate")
    private Optional<Item> loadDelegate(UUID id, FetchPlan fetchPlan) {
        return repository.findById(id, fetchPlan);
    }

    @Install(target = Target.DATA_CONTEXT)
    private Set<Object> saveDelegate(SaveContext saveContext) {
        return Set.of(repository.save(getEditedEntity()));
    }

    @Subscribe
    public void onBeforeShow(final BeforeShowEvent event) {
        Item item = getEditedEntity();
        if (item.getExternalId() == null) {
            item.setExternalId(UUID.randomUUID().toString());
        }
    }
}