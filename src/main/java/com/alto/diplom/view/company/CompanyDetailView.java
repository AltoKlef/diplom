package com.alto.diplom.view.company;

import com.alto.diplom.core.Company;
import com.alto.diplom.repository.CompanyRepository;
import com.alto.diplom.view.main.MainView;
import com.vaadin.flow.router.Route;
import io.jmix.core.FetchPlan;
import io.jmix.core.SaveContext;
import io.jmix.flowui.view.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;
import java.util.Set;

@Route(value = "companies/:id", layout = MainView.class)
@ViewController(id = "Company.detail")
@ViewDescriptor(path = "company-detail-view.xml")
@EditedEntityContainer("companyDc")
public class CompanyDetailView extends StandardDetailView<Company> {

    @Autowired
    private CompanyRepository repository;

    @Install(to = "companyDl", target = Target.DATA_LOADER, subject = "loadFromRepositoryDelegate")
    private Optional<Company> loadDelegate(Long id, FetchPlan fetchPlan) {
        return repository.findById(id, fetchPlan);
    }

    @Install(target = Target.DATA_CONTEXT)
    private Set<Object> saveDelegate(SaveContext saveContext) {
        return Set.of(repository.save(getEditedEntity()));
    }
}