package com.alto.diplom.view.transactionconfig;

import com.alto.diplom.entity.TransactionConfig;
import com.alto.diplom.repository.TransactionConfigRepository;
import com.alto.diplom.view.main.MainView;
import com.vaadin.flow.router.Route;
import io.jmix.core.repository.JmixDataRepositoryContext;
import io.jmix.flowui.view.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;

import java.util.Collection;
import java.util.List;

@Route(value = "transaction-configs", layout = MainView.class)
@ViewController(id = "TransactionConfig.list")
@ViewDescriptor(path = "transaction-config-list-view.xml")
@LookupComponent("transactionConfigsDataGrid")
@DialogMode(width = "64em")
public class TransactionConfigListView extends StandardListView<TransactionConfig> {

    @Autowired
    private TransactionConfigRepository repository;

    @Install(to = "transactionConfigsDl", target = Target.DATA_LOADER, subject = "loadFromRepositoryDelegate")
    private List<TransactionConfig> loadDelegate(Pageable pageable, JmixDataRepositoryContext context) {
        return repository.findAllSlice(pageable, context).getContent();
    }

    @Install(to = "transactionConfigsDataGrid.removeAction", subject = "delegate")
    private void transactionConfigsDataGridRemoveDelegate(final Collection<TransactionConfig> collection) {
        repository.deleteAll(collection);
    }

    @Install(to = "pagination", subject = "totalCountByRepositoryDelegate")
    private Long paginationTotalCountByRepositoryDelegate(final JmixDataRepositoryContext context) {
        return repository.count(context);
    }
}