package com.alto.diplom.view.itemgroup;

import com.alto.diplom.entity.items.ItemGroup;
import com.alto.diplom.repository.ItemGroupRepository;
import com.alto.diplom.view.main.MainView;
import com.vaadin.flow.router.Route;
import io.jmix.core.FetchPlan;
import io.jmix.core.SaveContext;
import io.jmix.flowui.view.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Route(value = "item-groups/:id", layout = MainView.class)
@ViewController(id = "ItemGroup.detail")
@ViewDescriptor(path = "item-group-detail-view.xml")
@EditedEntityContainer("itemGroupDc")
public class ItemGroupDetailView extends StandardDetailView<ItemGroup> {

    @Autowired
    private ItemGroupRepository repository;

    @Install(to = "itemGroupDl", target = Target.DATA_LOADER, subject = "loadFromRepositoryDelegate")
    private Optional<ItemGroup> loadDelegate(UUID id, FetchPlan fetchPlan) {
        return repository.findById(id, fetchPlan);
    }

    @Install(target = Target.DATA_CONTEXT)
    private Set<Object> saveDelegate(SaveContext saveContext) {
        return Set.of(repository.save(getEditedEntity()));
    }
}