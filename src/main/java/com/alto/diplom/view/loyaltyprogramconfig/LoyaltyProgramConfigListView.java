package com.alto.diplom.view.loyaltyprogramconfig;

import com.alto.diplom.entity.config.LoyaltyProgramConfig;
import com.alto.diplom.repository.LoyaltyProgramConfigRepository;
import com.alto.diplom.view.main.MainView;
import com.vaadin.flow.router.Route;
import io.jmix.core.repository.JmixDataRepositoryContext;
import io.jmix.flowui.view.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;

import java.util.Collection;
import java.util.List;

@Route(value = "loyalty-program-configs", layout = MainView.class)
@ViewController(id = "LoyaltyProgramConfig.list")
@ViewDescriptor(path = "loyalty-program-config-list-view.xml")
@LookupComponent("loyaltyProgramConfigsDataGrid")
@DialogMode(width = "64em")
public class LoyaltyProgramConfigListView extends StandardListView<LoyaltyProgramConfig> {

    @Autowired
    private LoyaltyProgramConfigRepository repository;

    @Install(to = "loyaltyProgramConfigsDl", target = Target.DATA_LOADER, subject = "loadFromRepositoryDelegate")
    private List<LoyaltyProgramConfig> loadDelegate(Pageable pageable, JmixDataRepositoryContext context) {
        return repository.findAllSlice(pageable, context).getContent();
    }

    @Install(to = "loyaltyProgramConfigsDataGrid.removeAction", subject = "delegate")
    private void loyaltyProgramConfigsDataGridRemoveDelegate(final Collection<LoyaltyProgramConfig> collection) {
        repository.deleteAll(collection);
    }

    @Install(to = "pagination", subject = "totalCountByRepositoryDelegate")
    private Long paginationTotalCountByRepositoryDelegate(final JmixDataRepositoryContext context) {
        return repository.count(context);
    }
}