package com.alto.diplom.view.itemgroup.itemgroup;

import com.alto.diplom.entity.items.ItemGroup;
import com.alto.diplom.repository.ItemGroupRepository;
import com.alto.diplom.view.main.MainView;
import com.vaadin.flow.router.Route;
import io.jmix.core.repository.JmixDataRepositoryContext;
import io.jmix.flowui.view.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;

import java.util.Collection;
import java.util.List;

@Route(value = "item-groups-free", layout = MainView.class)
@ViewController(id = "ItemGroup.listFree")
@ViewDescriptor(path = "item-group-list-view-free.xml")
@LookupComponent("itemGroupsDataGrid")
@DialogMode(width = "64em")
public class ItemGroupListViewFree extends StandardListView<ItemGroup> {

    @Autowired
    private ItemGroupRepository repository;

    @Install(to = "itemGroupsDl", target = Target.DATA_LOADER, subject = "loadFromRepositoryDelegate")
    private List<ItemGroup> loadDelegate(Pageable pageable, JmixDataRepositoryContext context) {
        return repository.findAllSlice(pageable, context).getContent();
    }

    @Install(to = "itemGroupsDataGrid.removeAction", subject = "delegate")
    private void itemGroupsDataGridRemoveDelegate(final Collection<ItemGroup> collection) {
        repository.deleteAll(collection);
    }

    @Install(to = "pagination", subject = "totalCountByRepositoryDelegate")
    private Long paginationTotalCountByRepositoryDelegate(final JmixDataRepositoryContext context) {
        return repository.count(context);
    }
}