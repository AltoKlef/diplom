package com.alto.diplom.view.transaction;

import com.alto.diplom.entity.transactions.Transaction;
import com.alto.diplom.repository.TransactionRepository;
import com.alto.diplom.view.main.MainView;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.HasValidation;
import com.vaadin.flow.component.HasValueAndElement;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeLeaveEvent;
import com.vaadin.flow.router.Route;
import io.jmix.core.AccessManager;
import io.jmix.core.EntityStates;
import io.jmix.core.FetchPlan;
import io.jmix.core.SaveContext;
import io.jmix.core.entity.EntityValues;
import io.jmix.core.repository.JmixDataRepositoryContext;
import io.jmix.core.validation.group.UiCrossFieldChecks;
import io.jmix.flowui.UiComponentProperties;
import io.jmix.flowui.UiViewProperties;
import io.jmix.flowui.ViewNavigators;
import io.jmix.flowui.accesscontext.UiEntityAttributeContext;
import io.jmix.flowui.action.SecuredBaseAction;
import io.jmix.flowui.component.UiComponentUtils;
import io.jmix.flowui.component.grid.DataGrid;
import io.jmix.flowui.component.validation.ValidationErrors;
import io.jmix.flowui.data.EntityValueSource;
import io.jmix.flowui.data.SupportsValueSource;
import io.jmix.flowui.kit.action.Action;
import io.jmix.flowui.kit.action.ActionPerformedEvent;
import io.jmix.flowui.kit.component.button.JmixButton;
import io.jmix.flowui.model.CollectionContainer;
import io.jmix.flowui.model.DataContext;
import io.jmix.flowui.model.InstanceContainer;
import io.jmix.flowui.model.InstanceLoader;
import io.jmix.flowui.util.OperationResult;
import io.jmix.flowui.util.UnknownOperationResult;
import io.jmix.flowui.view.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;

import java.util.*;

import static io.jmix.flowui.component.delegate.AbstractFieldDelegate.PROPERTY_INVALID;

@Route(value = "transactions", layout = MainView.class)
@ViewController(id = "Transaction_.list")
@ViewDescriptor(path = "transaction-list-view.xml")
@LookupComponent("transactionsDataGrid")
@DialogMode(width = "64em")
public class TransactionListView extends StandardListView<Transaction> {

    @Autowired
    private TransactionRepository repository;

    @ViewComponent
    private DataContext dataContext;

    @ViewComponent
    private CollectionContainer<Transaction> transactionsDc;

    @ViewComponent
    private InstanceContainer<Transaction> transactionDc;

    @ViewComponent
    private InstanceLoader<Transaction> transactionDl;

    @ViewComponent
    private VerticalLayout listLayout;

    @ViewComponent
    private DataGrid<Transaction> transactionsDataGrid;


    private boolean modifiedAfterEdit;

    @Subscribe(id = "transactionsDc", target = Target.DATA_CONTAINER)
    public void onTransactionsDcItemChange(final InstanceContainer.ItemChangeEvent<Transaction> event) {
        Transaction entity = event.getItem();
        dataContext.clear();
        if (entity != null) {
            transactionDl.setEntityId(entity.getId());
            transactionDl.load();
        } else {
            transactionDl.setEntityId(null);
            transactionDc.setItem(null);
        }
    }

    @Autowired
    private ViewNavigators viewNavigators;

    @Subscribe("createPurchaseBtn")
    public void onCreatePurchaseBtnClick(final ClickEvent<Button> event) {
        viewNavigators.detailView(transactionsDataGrid)
                .withViewId("Transaction.detailBuy") // ID твоего нового экрана
                .newEntity() // Указываем, что создаем новый чек
                .withBackwardNavigation(true) // Чтобы можно было вернуться назад к списку
                .navigate();
    }

    // ДЕЛЕГАТЫ ДЛЯ РАБОТЫ С РЕПОЗИТОРИЕМ (Оставляем как есть)
    @Install(to = "transactionsDl", target = Target.DATA_LOADER, subject = "loadFromRepositoryDelegate")
    private List<Transaction> listLoadDelegate(Pageable pageable, JmixDataRepositoryContext context) {
        return repository.findAllSlice(pageable, context).getContent();
    }

    @Install(to = "transactionDl", target = Target.DATA_LOADER, subject = "loadFromRepositoryDelegate")
    private Optional<Transaction> detailLoadDelegate(UUID id, FetchPlan fetchPlan) {
        return repository.findById(id, fetchPlan);
    }

    @Install(to = "pagination", subject = "totalCountByRepositoryDelegate")
    private Long paginationTotalCountByRepositoryDelegate(final JmixDataRepositoryContext context) {
        return repository.count(context);
    }

    @Install(to = "transactionsDataGrid.removeAction", subject = "delegate")
    private void transactionsDataGridRemoveDelegate(final Collection<Transaction> collection) {
        repository.deleteAll(collection);
    }

    @Install(target = Target.DATA_CONTEXT)
    private Set<Object> saveDelegate(SaveContext saveContext) {
        return Set.of(repository.save(transactionDc.getItem()));
    }
    @Subscribe
    public void onInit(final InitEvent event) {
        transactionsDataGrid.getActions().forEach(action -> {
            if (action instanceof SecuredBaseAction secured) {
                secured.addEnabledRule(() -> listLayout.isEnabled());
            }
        });
    }

    @Subscribe
    public void onReady(final ReadyEvent event) {
        setupModifiedTracking();
    }






    private void setupModifiedTracking() {
        dataContext.addChangeListener(this::onChangeEvent);
        dataContext.addPostSaveListener(this::onPostSaveEvent);
    }

    private void onChangeEvent(DataContext.ChangeEvent changeEvent) {
        modifiedAfterEdit = true;
    }

    private void onPostSaveEvent(DataContext.PostSaveEvent postSaveEvent) {
        modifiedAfterEdit = false;
    }


    private OperationResult navigate(BeforeLeaveEvent.ContinueNavigationAction navigationAction,
                                     CloseAction closeAction) {
        navigationAction.proceed();

        AfterCloseEvent afterCloseEvent = new AfterCloseEvent(this, closeAction);
        fireEvent(afterCloseEvent);

        return OperationResult.success();
    }




}