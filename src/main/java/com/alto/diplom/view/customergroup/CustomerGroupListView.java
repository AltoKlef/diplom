package com.alto.diplom.view.customergroup;

import com.alto.diplom.entity.core.Customer;
import com.alto.diplom.entity.core.CustomerGroup;
import com.alto.diplom.repository.CustomerGroupRepository;
import com.alto.diplom.view.main.MainView;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.router.Route;
import io.jmix.core.DataManager;
import io.jmix.core.FetchPlan;
import io.jmix.core.LoadContext;
import io.jmix.core.SaveContext;
import io.jmix.core.repository.JmixDataRepositoryContext;
import io.jmix.flowui.DialogWindows;
import io.jmix.flowui.kit.component.button.JmixButton;
import io.jmix.flowui.model.CollectionContainer;
import io.jmix.flowui.model.CollectionLoader;
import io.jmix.flowui.model.InstanceContainer;
import io.jmix.flowui.view.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;

import java.util.*;

@Route(value = "customer-groups", layout = MainView.class)
@ViewController(id = "CustomerGroup.list")
@ViewDescriptor(path = "customer-group-list-view.xml")
@LookupComponent("customerGroupsDataGrid")
@DialogMode(width = "64em")
public class CustomerGroupListView extends StandardListView<CustomerGroup> {

    @Autowired
    private CustomerGroupRepository repository;

    @Autowired
    private DataManager dataManager;

    @ViewComponent
    private CollectionContainer<CustomerGroup> customerGroupsDc;

    @ViewComponent
    private InstanceContainer<CustomerGroup> customerGroupDc;

    @ViewComponent
    private CollectionLoader<Customer> membersDl;

    /**
     * Делегат загрузки участников.
     * Срабатывает каждый раз, когда мы вызываем membersDl.load()
     */
    @Install(to = "membersDl", target = Target.DATA_LOADER)
    protected List<Customer> membersDlLoadDelegate(final LoadContext<Customer> loadContext) {
        CustomerGroup selectedGroup = customerGroupDc.getItemOrNull();

        if (selectedGroup == null) {
            return Collections.emptyList();
        }

        // Запрос участников через Many-to-Many связь
        return dataManager.load(Customer.class)
                .query("select e from Customer e join e.customerGroups g where g = :group")
                .parameter("group", selectedGroup)
                .list();
    }

    /**
     * Обработчик смены выбранной группы в таблице.
     */
    @Subscribe(id = "customerGroupsDc", target = Target.DATA_CONTAINER)
    public void onCustomerGroupsDcItemChange(final InstanceContainer.ItemChangeEvent<CustomerGroup> event) {
        CustomerGroup entity = event.getItem();

        if (entity != null) {
            // Кладём выбранную группу в инстанс-контейнер, чтобы сработал лоадер участников
            customerGroupDc.setItem(entity);
            membersDl.load();
        } else {
            customerGroupDc.setItem(null);
        }
    }

    // === ДЕЛЕГАТЫ РЕПОЗИТОРИЯ ДЛЯ ГЛАВНОЙ ТАБЛИЦЫ ===

    @Install(to = "customerGroupsDl", target = Target.DATA_LOADER, subject = "loadFromRepositoryDelegate")
    private List<CustomerGroup> listLoadDelegate(Pageable pageable, JmixDataRepositoryContext context) {
        return repository.findAllSlice(pageable, context).getContent();
    }

    @Install(to = "pagination", subject = "totalCountByRepositoryDelegate")
    private Long paginationTotalCountByRepositoryDelegate(final JmixDataRepositoryContext context) {
        return repository.count(context);
    }

    @Install(to = "customerGroupsDataGrid.removeAction", subject = "delegate")
    private void customerGroupsDataGridRemoveDelegate(final Collection<CustomerGroup> collection) {
        repository.deleteAll(collection);
    }

    @Autowired
    private DialogWindows dialogWindows;

    @Subscribe("addMemberBtn")
    public void onAddMemberBtnClick(final ClickEvent<JmixButton> event) {
        CustomerGroup selectedGroup = customerGroupDc.getItemOrNull();
        if (selectedGroup == null) return;

        // Используем базовый диалог выбора
        dialogWindows.lookup(this, Customer.class)
                .withSelectHandler(customers -> {
                    addCustomersToGroup(customers, selectedGroup);
                })
                .build()
                .open();
    }

    private void addCustomersToGroup(Collection<Customer> customers, CustomerGroup group) {
        SaveContext saveContext = new SaveContext();

        for (Customer customer : customers) {
            // Подгружаем клиента со связями, чтобы не затереть существующие
            Customer fullCustomer = dataManager.load(Customer.class)
                    .id(customer.getId())
                    .fetchPlan(fp -> fp.add("customerGroups", FetchPlan.BASE))
                    .one();

            Set<CustomerGroup> groups = new HashSet<>(fullCustomer.getCustomerGroups() != null
                    ? fullCustomer.getCustomerGroups()
                    : Collections.emptySet());

            if (!groups.contains(group)) {
                groups.add(group);

                fullCustomer.setCustomerGroups(groups);

                saveContext.saving(fullCustomer);
            }
        }

        // Сохраняем всех пачкой (транзакционно)
        dataManager.save(saveContext);

        // Обновляем таблицу участников
        membersDl.load();
    }
}