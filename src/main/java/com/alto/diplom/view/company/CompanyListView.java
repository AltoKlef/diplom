package com.alto.diplom.view.company;

import com.alto.diplom.entity.core.Company;
import com.alto.diplom.repository.CompanyRepository;
import com.alto.diplom.view.main.MainView;
import com.vaadin.flow.router.Route;
import io.jmix.core.repository.JmixDataRepositoryContext;
import io.jmix.flowui.view.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;

import java.util.Collection;
import java.util.List;

@Route(value = "companies", layout = MainView.class)
@ViewController(id = "Company.list")
@ViewDescriptor(path = "company-list-view.xml")
@LookupComponent("companiesDataGrid")
@DialogMode(width = "64em")
public class CompanyListView extends StandardListView<Company> {

    @Autowired
    private CompanyRepository repository;

    @Install(to = "companiesDl", target = Target.DATA_LOADER, subject = "loadFromRepositoryDelegate")
    private List<Company> loadDelegate(Pageable pageable, JmixDataRepositoryContext context) {
        return repository.findAllSlice(pageable, context).getContent();
    }

    @Install(to = "companiesDataGrid.removeAction", subject = "delegate")
    private void companiesDataGridRemoveDelegate(final Collection<Company> collection) {
        repository.deleteAll(collection);
    }

    @Install(to = "pagination", subject = "totalCountByRepositoryDelegate")
    private Long paginationTotalCountByRepositoryDelegate(final JmixDataRepositoryContext context) {
        return repository.count(context);
    }
}