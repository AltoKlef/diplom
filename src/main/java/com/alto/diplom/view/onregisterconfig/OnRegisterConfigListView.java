package com.alto.diplom.view.onregisterconfig;

import com.alto.diplom.entity.config.OnRegisterConfig;
import com.alto.diplom.repository.OnRegisterConfigRepository;
import com.alto.diplom.view.main.MainView;
import com.vaadin.flow.router.Route;
import io.jmix.core.repository.JmixDataRepositoryContext;
import io.jmix.flowui.view.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;

import java.util.Collection;
import java.util.List;

@Route(value = "on-register-configs", layout = MainView.class)
@ViewController(id = "OnRegisterConfig.list")
@ViewDescriptor(path = "on-register-config-list-view.xml")
@LookupComponent("onRegisterConfigsDataGrid")
@DialogMode(width = "64em")
public class OnRegisterConfigListView extends StandardListView<OnRegisterConfig> {

    @Autowired
    private OnRegisterConfigRepository repository;

    @Install(to = "onRegisterConfigsDl", target = Target.DATA_LOADER, subject = "loadFromRepositoryDelegate")
    private List<OnRegisterConfig> loadDelegate(Pageable pageable, JmixDataRepositoryContext context) {
        return repository.findAllSlice(pageable, context).getContent();
    }

    @Install(to = "onRegisterConfigsDataGrid.removeAction", subject = "delegate")
    private void onRegisterConfigsDataGridRemoveDelegate(final Collection<OnRegisterConfig> collection) {
        repository.deleteAll(collection);
    }

    @Install(to = "pagination", subject = "totalCountByRepositoryDelegate")
    private Long paginationTotalCountByRepositoryDelegate(final JmixDataRepositoryContext context) {
        return repository.count(context);
    }
}