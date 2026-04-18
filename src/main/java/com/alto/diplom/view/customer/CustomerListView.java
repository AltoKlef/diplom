package com.alto.diplom.view.customer;

import com.alto.diplom.entity.core.Customer;
import com.alto.diplom.repository.CustomerRepository;
import com.alto.diplom.view.main.MainView;
import com.vaadin.flow.router.Route;
import io.jmix.core.repository.JmixDataRepositoryContext;
import io.jmix.flowui.view.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;

import java.util.Collection;
import java.util.List;

@Route(value = "customers", layout = MainView.class)
@ViewController(id = "Customer.list")
@ViewDescriptor(path = "customer-list-view.xml")
@LookupComponent("customersDataGrid")
@DialogMode(width = "64em")
public class CustomerListView extends StandardListView<Customer> {

    @Autowired
    private CustomerRepository repository;

    @Install(to = "customersDl", target = Target.DATA_LOADER, subject = "loadFromRepositoryDelegate")
    private List<Customer> loadDelegate(Pageable pageable, JmixDataRepositoryContext context) {
        return repository.findAllSlice(pageable, context).getContent();
    }

    @Install(to = "customersDataGrid.removeAction", subject = "delegate")
    private void customersDataGridRemoveDelegate(final Collection<Customer> collection) {
        repository.deleteAll(collection);
    }

    @Install(to = "pagination", subject = "totalCountByRepositoryDelegate")
    private Long paginationTotalCountByRepositoryDelegate(final JmixDataRepositoryContext context) {
        return repository.count(context);
    }
}