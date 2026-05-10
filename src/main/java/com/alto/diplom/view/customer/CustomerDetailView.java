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

    @ViewComponent
    private Span groupsListSpan;


    @ViewComponent
    private InstanceContainer<Customer> customerDc; // Инжектируй свой контейнер

    @Subscribe
    public void onBeforeShow(final BeforeShowEvent event) {
        Customer customer = getEditedEntity();

        // Если это не новый клиент и группы не загружены — грузим ПРИНУДИТЕЛЬНО
        if (!entityStates.isNew(customer) && !entityStates.isLoaded(customer, "customerGroups")) {
            Customer reloadedCustomer = dataManager.load(Customer.class)
                    .id(customer.getId())
                    .fetchPlan(plan -> {
                        plan.addFetchPlan(FetchPlan.BASE);
                        plan.add("customerGroups", FetchPlan.BASE);
                        plan.add("company", FetchPlan.INSTANCE_NAME);
                    })
                    .one();

            // ВАЖНО: Устанавливаем перечитанный объект в контейнер экрана
            customerDc.setItem(reloadedCustomer);
            customer = reloadedCustomer;
        }

        // Твоя логика с бонусным счетом
        if (entityStates.isNew(customer)) {
            CustomerBonusAccount newAccount = dataContext.create(CustomerBonusAccount.class);
            newAccount.setCustomer(customer);
            newAccount.setMark(BigDecimal.ZERO);
            newAccount.setCompany(customer.getCompany());
            bonusAccountDc.setItem(newAccount);
        } else {
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

    @Subscribe
    public void onReady(final ReadyEvent event) {
        // Берем актуальный объект из контейнера
        Customer customer = customerDc.getItem();

        // Безопасная проверка: загружено ли поле и не пустое ли оно
        if (entityStates.isLoaded(customer, "customerGroups") && customer.getCustomerGroups() != null) {
            try {
                // Если групп нет, isEmpty() сработает нормально на загруженном IndirectSet
                if (!customer.getCustomerGroups().isEmpty()) {
                    String groups = customer.getCustomerGroups().stream()
                            .map(CustomerGroup::getName)
                            .collect(Collectors.joining(", "));
                    groupsListSpan.setText("Группы: " + groups);
                } else {
                    groupsListSpan.setText("Группы: не назначены");
                }
            } catch (Exception e) {
                // Подстраховка на случай странностей EclipseLink
                groupsListSpan.setText("Группы: ошибка загрузки");
            }
        } else {
            groupsListSpan.setText("Группы: не назначены");
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