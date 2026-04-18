package com.alto.diplom.view.customerbonusaccount;

import com.alto.diplom.entity.loyalty.CustomerBonusAccount;
import com.alto.diplom.repository.CustomerBonusAccountRepository;
import com.alto.diplom.view.main.MainView;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.HasValidation;
import com.vaadin.flow.component.HasValueAndElement;
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

@Route(value = "customer-bonus-accounts", layout = MainView.class)
@ViewController(id = "CustomerBonusAccount.list")
@ViewDescriptor(path = "customer-bonus-account-list-view.xml")
@LookupComponent("customerBonusAccountsDataGrid")
@DialogMode(width = "64em")
public class CustomerBonusAccountListView extends StandardListView<CustomerBonusAccount> {

    @Autowired
    private CustomerBonusAccountRepository repository;

    @ViewComponent
    private DataContext dataContext;

    @ViewComponent
    private CollectionContainer<CustomerBonusAccount> customerBonusAccountsDc;

    @ViewComponent
    private InstanceContainer<CustomerBonusAccount> customerBonusAccountDc;

    @ViewComponent
    private InstanceLoader<CustomerBonusAccount> customerBonusAccountDl;

    @ViewComponent
    private VerticalLayout listLayout;

    @ViewComponent
    private DataGrid<CustomerBonusAccount> customerBonusAccountsDataGrid;

    @ViewComponent
    private FormLayout form;

    @ViewComponent
    private HorizontalLayout detailActions;

    @Autowired
    private AccessManager accessManager;

    @Autowired
    private EntityStates entityStates;

    @Autowired
    private UiViewProperties uiViewProperties;

    @Autowired
    private ViewValidation viewValidation;

    @Autowired
    private UiComponentProperties uiComponentProperties;

    private boolean modifiedAfterEdit;

    @Subscribe
    public void onInit(final InitEvent event) {
        customerBonusAccountsDataGrid.getActions().forEach(action -> {
            if (action instanceof SecuredBaseAction secured) {
                secured.addEnabledRule(() -> listLayout.isEnabled());
            }
        });
    }

    @Subscribe
    public void onReady(final ReadyEvent event) {
        setupModifiedTracking();
    }

    @Subscribe
    public void onBeforeShow(final BeforeShowEvent event) {
        updateControls(false);
    }

    @Subscribe
    private void onBeforeClose(final BeforeCloseEvent event) {
        preventUnsavedChanges(event);
    }

    @Subscribe("customerBonusAccountsDataGrid.createAction")
    public void onCustomerBonusAccountsDataGridCreateAction(final ActionPerformedEvent event) {
        prepareFormForValidation();

        dataContext.clear();
        CustomerBonusAccount entity = dataContext.create(CustomerBonusAccount.class);
        customerBonusAccountDc.setItem(entity);
        updateControls(true);
    }

    @Subscribe("customerBonusAccountsDataGrid.editAction")
    public void onCustomerBonusAccountsDataGridEditAction(final ActionPerformedEvent event) {
        updateControls(true);
    }

    @Subscribe("saveButton")
    public void onSaveButtonClick(final ClickEvent<JmixButton> event) {
        saveEditedEntity();
    }

    @Subscribe("cancelButton")
    public void onCancelButtonClick(final ClickEvent<JmixButton> event) {
        if (!hasUnsavedChanges()) {
            discardEditedEntity();
            return;
        }

        if (uiViewProperties.isUseSaveConfirmation()) {
            viewValidation.showSaveConfirmationDialog(this)
                    .onSave(this::saveEditedEntity)
                    .onDiscard(this::discardEditedEntity);
        } else {
            viewValidation.showUnsavedChangesDialog(this)
                    .onDiscard(this::discardEditedEntity);
        }
    }

    @Subscribe(id = "customerBonusAccountsDc", target = Target.DATA_CONTAINER)
    public void onCustomerBonusAccountsDcItemChange(final InstanceContainer.ItemChangeEvent<CustomerBonusAccount> event) {
        prepareFormForValidation();

        CustomerBonusAccount entity = event.getItem();
        dataContext.clear();
        if (entity != null) {
            customerBonusAccountDl.setEntityId(EntityValues.getId(entity));
            customerBonusAccountDl.load();
        } else {
            customerBonusAccountDl.setEntityId(null);
            customerBonusAccountDc.setItem(null);
        }
        updateControls(false);
    }

    private void prepareFormForValidation() {
        // all components shouldn't be readonly due to validation passing correctly
        UiComponentUtils.getComponents(form).forEach(component -> {
            if (component instanceof HasValueAndElement<?, ?> field) {
                field.setReadOnly(false);
            }
        });
    }

    private OperationResult saveEditedEntity() {
        CustomerBonusAccount item = customerBonusAccountDc.getItem();
        ValidationErrors validationErrors = validateView(item);

        if (!validationErrors.isEmpty()) {
            viewValidation.showValidationErrors(validationErrors);
            viewValidation.focusProblemComponent(validationErrors);
            return OperationResult.fail();
        }

        dataContext.save();
        customerBonusAccountsDc.replaceItem(item);
        updateControls(false);
        return OperationResult.success();
    }

    private void discardEditedEntity() {
        resetFormInvalidState();

        dataContext.clear();
        customerBonusAccountDc.setItem(null);
        customerBonusAccountDl.load();
        updateControls(false);
    }

    private void resetFormInvalidState() {
        UiComponentUtils.getComponents(form).forEach(component -> {
            if (component instanceof HasValidation hasValidation && hasValidation.isInvalid()) {
                component.getElement().setProperty(PROPERTY_INVALID, false);
                component.getElement().executeJs("this.invalid = $0", false);
            }
        });
    }

    private ValidationErrors validateView(CustomerBonusAccount entity) {
        ValidationErrors validationErrors = viewValidation.validateUiComponents(form);
        if (!validationErrors.isEmpty()) {
            return validationErrors;
        }
        validationErrors.addAll(viewValidation.validateBeanGroup(UiCrossFieldChecks.class, entity));
        return validationErrors;
    }

    private void updateControls(boolean editing) {
        UiComponentUtils.getComponents(form).forEach(component -> {
            if (component instanceof SupportsValueSource<?> valueSourceComponent
                    && valueSourceComponent.getValueSource() instanceof EntityValueSource<?, ?> entityValueSource
                    && component instanceof HasValueAndElement<?, ?> field) {
                field.setReadOnly(!editing || !isUpdatePermitted(entityValueSource));
            }
        });

        modifiedAfterEdit = false;
        detailActions.setVisible(editing);
        listLayout.setEnabled(!editing);
        customerBonusAccountsDataGrid.getActions().forEach(Action::refreshState);

        if (!uiComponentProperties.isImmediateRequiredValidationEnabled() && editing) {
            resetFormInvalidState();
        }
    }

    private boolean isUpdatePermitted(EntityValueSource<?, ?> valueSource) {
        UiEntityAttributeContext context = new UiEntityAttributeContext(valueSource.getMetaPropertyPath());
        accessManager.applyRegisteredConstraints(context);
        return context.canModify();
    }

    private boolean hasUnsavedChanges() {
        for (Object modified : dataContext.getModified()) {
            if (!entityStates.isNew(modified)) {
                return true;
            }
        }

        return modifiedAfterEdit;
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

    private void preventUnsavedChanges(BeforeCloseEvent event) {
        CloseAction closeAction = event.getCloseAction();

        if (closeAction instanceof ChangeTrackerCloseAction trackerCloseAction
                && trackerCloseAction.isCheckForUnsavedChanges()
                && hasUnsavedChanges()) {
            UnknownOperationResult result = new UnknownOperationResult();

            if (closeAction instanceof NavigateCloseAction navigateCloseAction) {
                BeforeLeaveEvent beforeLeaveEvent = navigateCloseAction.getBeforeLeaveEvent();
                BeforeLeaveEvent.ContinueNavigationAction navigationAction = beforeLeaveEvent.postpone();

                if (uiViewProperties.isUseSaveConfirmation()) {
                    viewValidation.showSaveConfirmationDialog(this)
                            .onSave(() -> result.resume(navigateWithSave(navigationAction)))
                            .onDiscard(() -> result.resume(navigateWithDiscard(navigationAction)))
                            .onCancel(() -> {
                                result.otherwise(() -> cancelNavigation(navigationAction));
                                result.fail();
                            });
                } else {
                    viewValidation.showUnsavedChangesDialog(this)
                            .onDiscard(() -> result.resume(navigateWithDiscard(navigationAction)))
                            .onCancel(() -> {
                                result.otherwise(() -> cancelNavigation(navigationAction));
                                result.fail();
                            });
                }
            } else {
                if (uiViewProperties.isUseSaveConfirmation()) {
                    viewValidation.showSaveConfirmationDialog(this)
                            .onSave(() -> result.resume(closeWithSave()))
                            .onDiscard(() -> result.resume(closeWithDiscard()))
                            .onCancel(result::fail);
                } else {
                    viewValidation.showUnsavedChangesDialog(this)
                            .onDiscard(() -> result.resume(closeWithDiscard()))
                            .onCancel(result::fail);
                }
            }

            event.preventClose(result);
        }
    }

    private OperationResult navigateWithDiscard(BeforeLeaveEvent.ContinueNavigationAction navigationAction) {
        return navigate(navigationAction, StandardOutcome.DISCARD.getCloseAction());
    }

    private OperationResult navigateWithSave(BeforeLeaveEvent.ContinueNavigationAction navigationAction) {
        return saveEditedEntity()
                .compose(() -> navigate(navigationAction, StandardOutcome.SAVE.getCloseAction()));
    }

    private void cancelNavigation(BeforeLeaveEvent.ContinueNavigationAction navigationAction) {
        // Because of using React Router, we need to call
        // 'BeforeLeaveEvent.ContinueNavigationAction.cancel'
        // explicitly, otherwise navigation process hangs
        navigationAction.cancel();
    }

    private OperationResult navigate(BeforeLeaveEvent.ContinueNavigationAction navigationAction,
                                     CloseAction closeAction) {
        navigationAction.proceed();

        AfterCloseEvent afterCloseEvent = new AfterCloseEvent(this, closeAction);
        fireEvent(afterCloseEvent);

        return OperationResult.success();
    }

    private OperationResult closeWithSave() {
        return saveEditedEntity()
                .compose(() -> close(StandardOutcome.SAVE));
    }

    @Install(to = "customerBonusAccountsDl", target = Target.DATA_LOADER, subject = "loadFromRepositoryDelegate")
    private List<CustomerBonusAccount> listLoadDelegate(Pageable pageable, JmixDataRepositoryContext context) {
        return repository.findAllSlice(pageable, context).getContent();
    }

    @Install(to = "pagination", subject = "totalCountByRepositoryDelegate")
    private Long paginationTotalCountByRepositoryDelegate(final JmixDataRepositoryContext context) {
        return repository.count(context);
    }

    @Install(to = "customerBonusAccountsDataGrid.removeAction", subject = "delegate")
    private void customerBonusAccountsDataGridRemoveDelegate(final Collection<CustomerBonusAccount> collection) {
        repository.deleteAll(collection);
    }

    @Install(to = "customerBonusAccountDl", target = Target.DATA_LOADER, subject = "loadFromRepositoryDelegate")
    private Optional<CustomerBonusAccount> detailLoadDelegate(UUID id, FetchPlan fetchPlan) {
        return repository.findById(id, fetchPlan);
    }

    @Install(target = Target.DATA_CONTEXT)
    private Set<Object> saveDelegate(SaveContext saveContext) {
        return Set.of(repository.save(customerBonusAccountDc.getItem()));
    }
}