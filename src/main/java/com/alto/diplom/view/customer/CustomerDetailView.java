package com.alto.diplom.view.customer;

import com.alto.diplom.entity.core.Company;
import com.alto.diplom.entity.core.Customer;
import com.alto.diplom.entity.core.CustomerGroup;
import com.alto.diplom.entity.loyalty.CustomerBonusAccount;
import com.alto.diplom.repository.CustomerRepository;
import com.alto.diplom.view.main.MainView;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.router.Route;
import io.jmix.core.DataManager;
import io.jmix.core.EntityStates;
import io.jmix.core.FetchPlan;
import io.jmix.core.SaveContext;
import io.jmix.flowui.model.DataContext;
import io.jmix.flowui.model.InstanceContainer;
import io.jmix.flowui.model.InstanceLoader;
import io.jmix.flowui.view.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Route(value = "customers/:id", layout = MainView.class)
@ViewController(id = "Customer.detail")
@ViewDescriptor(path = "customer-detail-view.xml")
@EditedEntityContainer("customerDc")
public class CustomerDetailView extends StandardDetailView<Customer> {

    @ViewComponent
    private DataContext dataContext;

    @ViewComponent
    private InstanceContainer<CustomerBonusAccount> bonusAccountDc;

    @Autowired
    private DataManager dataManager;

    @Autowired
    private EntityStates entityStates;

    // Репозиторий тут только мешает нормальной работе DataContext, убираем его из делегатов
    @ViewComponent
    private Span groupsListSpan;

    @Subscribe
    public void onReady(final ReadyEvent event) {
        Customer customer = getEditedEntity();
        if (customer.getCustomerGroups() != null && !customer.getCustomerGroups().isEmpty()) {
            String groups = customer.getCustomerGroups().stream()
                    .map(CustomerGroup::getName)
                    .collect(Collectors.joining(", "));
            groupsListSpan.setText("Группы: " + groups);
        } else {
            groupsListSpan.setText("Группы: не назначены");
        }
    }
    @Subscribe
    public void onBeforeShow(final BeforeShowEvent event) {
        Customer customer = getEditedEntity();

        if (entityStates.isNew(customer)) {
            // Создаем новый счет
            CustomerBonusAccount newAccount = dataContext.create(CustomerBonusAccount.class);
            newAccount.setCustomer(customer);
            newAccount.setMark(BigDecimal.ZERO);


            newAccount.setCompany(customer.getCompany());

            bonusAccountDc.setItem(newAccount);
        } else {
            // Загружаем существующий счет через DataManager
            dataManager.load(CustomerBonusAccount.class)
                    .query("select e from CustomerBonusAccount e where e.customer = :cust")
                    .parameter("cust", customer)
                    .optional()
                    .ifPresent(account -> {
                        CustomerBonusAccount merged = dataContext.merge(account);
                        bonusAccountDc.setItem(merged);
                    });
        }
    }

    @Subscribe(id = "customerDc", target = Target.DATA_CONTAINER)
    public void onCustomerDcItemPropertyChange(final InstanceContainer.ItemPropertyChangeEvent<Customer> event) {
        // Если изменилось поле "company"
        if ("company".equals(event.getProperty())) {
            CustomerBonusAccount account = bonusAccountDc.getItemOrNull();
            if (account != null) {
                // Копируем выбранную компанию в бонусный аккаунт
                account.setCompany((Company) event.getValue());
            }
        }
    }
}