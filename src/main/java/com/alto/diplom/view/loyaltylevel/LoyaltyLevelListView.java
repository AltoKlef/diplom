package com.alto.diplom.view.loyaltylevel;

import com.alto.diplom.entity.loyalty.LoyaltyLevel;
import com.alto.diplom.repository.LoyaltyLevelRepository;
import com.alto.diplom.view.main.MainView;
import com.vaadin.flow.router.Route;
import io.jmix.core.repository.JmixDataRepositoryContext;
import io.jmix.flowui.view.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;

import java.util.Collection;
import java.util.List;

@Route(value = "loyalty-levels", layout = MainView.class)
@ViewController(id = "LoyaltyLevel.list")
@ViewDescriptor(path = "loyalty-level-list-view.xml")
@LookupComponent("loyaltyLevelsDataGrid")
@DialogMode(width = "64em")
public class LoyaltyLevelListView extends StandardListView<LoyaltyLevel> {

    @Autowired
    private LoyaltyLevelRepository repository;

    @Install(to = "loyaltyLevelsDl", target = Target.DATA_LOADER, subject = "loadFromRepositoryDelegate")
    private List<LoyaltyLevel> loadDelegate(Pageable pageable, JmixDataRepositoryContext context) {
        return repository.findAllSlice(pageable, context).getContent();
    }

    @Install(to = "loyaltyLevelsDataGrid.removeAction", subject = "delegate")
    private void loyaltyLevelsDataGridRemoveDelegate(final Collection<LoyaltyLevel> collection) {
        repository.deleteAll(collection);
    }

    @Install(to = "pagination", subject = "totalCountByRepositoryDelegate")
    private Long paginationTotalCountByRepositoryDelegate(final JmixDataRepositoryContext context) {
        return repository.count(context);
    }
}