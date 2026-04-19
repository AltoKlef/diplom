package com.alto.diplom.view.itemgroup;

import com.alto.diplom.entity.items.Item;
import com.alto.diplom.entity.items.ItemGroup;
import com.alto.diplom.repository.ItemGroupRepository;
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
import io.jmix.flowui.component.grid.TreeDataGrid;
import io.jmix.flowui.component.validation.ValidationErrors;
import io.jmix.flowui.data.EntityValueSource;
import io.jmix.flowui.data.SupportsValueSource;
import io.jmix.flowui.kit.action.Action;
import io.jmix.flowui.kit.action.ActionPerformedEvent;
import io.jmix.flowui.kit.component.button.JmixButton;
import io.jmix.flowui.model.*;
import io.jmix.flowui.util.OperationResult;
import io.jmix.flowui.util.UnknownOperationResult;
import io.jmix.flowui.view.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;

import java.util.*;

import static io.jmix.flowui.component.delegate.AbstractFieldDelegate.PROPERTY_INVALID;

@Route(value = "item-groups", layout = MainView.class)
@ViewController(id = "ItemGroup.list")
@ViewDescriptor(path = "item-group-list-view.xml")
@LookupComponent("itemGroupsDataGrid")
@DialogMode(width = "64em")
public class ItemGroupListView extends StandardListView<ItemGroup> {

    @Autowired
    private ItemGroupRepository repository;

    @ViewComponent
    private DataContext dataContext;

    @ViewComponent
    private CollectionContainer<ItemGroup> itemGroupsDc;

    @ViewComponent
    private InstanceContainer<ItemGroup> itemGroupDc;

    @ViewComponent
    private InstanceLoader<ItemGroup> itemGroupDl;

    @ViewComponent
    private VerticalLayout listLayout;

    @ViewComponent
    private TreeDataGrid<ItemGroup> itemGroupsDataGrid;

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

    @ViewComponent
    private CollectionContainer<Item> itemsDc; // Должно быть CollectionContainer!

    @ViewComponent
    private CollectionLoader<Item> itemsDl;    // Должно быть CollectionLoader!


    private boolean modifiedAfterEdit;

    @Subscribe
    public void onInit(final InitEvent event) {
        itemGroupsDataGrid.getActions().forEach(action -> {
            if (action instanceof SecuredBaseAction secured) {
                secured.addEnabledRule(() -> listLayout.isEnabled());
            }
        });
    }

    @Subscribe
    public void onReady(final ReadyEvent event) {
        setupModifiedTracking();
    }


//    @Subscribe("saveButton")
//    public void onSaveButtonClick(final ClickEvent<JmixButton> event) {
//        saveEditedEntity();
//    }

//    @Subscribe("cancelButton")
//    public void onCancelButtonClick(final ClickEvent<JmixButton> event) {
//        if (!hasUnsavedChanges()) {
//            discardEditedEntity();
//            return;
//        }
//
//        if (uiViewProperties.isUseSaveConfirmation()) {
//            viewValidation.showSaveConfirmationDialog(this)
//                    .onSave(this::saveEditedEntity)
//                    .onDiscard(this::discardEditedEntity);
//        } else {
//            viewValidation.showUnsavedChangesDialog(this)
//                    .onDiscard(this::discardEditedEntity);
//        }
//    }
    @Subscribe(id = "itemGroupsDc", target = Target.DATA_CONTAINER)
    public void onItemGroupsDcItemChange(final InstanceContainer.ItemChangeEvent<ItemGroup> event) {
        ItemGroup selectedGroup = event.getItem();
        if (selectedGroup != null) {
            // Заряжаем лоадер товаров выбранной группой
            itemsDl.setParameter("group", selectedGroup);
            itemsDl.load();
        } else {
            // Если ничего не выбрано, обнуляем список товаров
            itemsDl.removeParameter("group");
            itemsDc.getMutableItems().clear();
        }
    }

    @Install(to = "itemsDataGrid.create", subject = "initializer")
    private void itemsDataGridCreateInitializer(Item item) {
        item.setItemGroup(itemGroupsDataGrid.getSingleSelectedItem());
        // Если у тебя есть компания у группы, можно и её подтянуть:
        // item.setCompany(itemGroupsDataGrid.getSingleSelected().getCompany());
    }

    private void prepareFormForValidation() {
        // all components shouldn't be readonly due to validation passing correctly
        UiComponentUtils.getComponents(form).forEach(component -> {
            if (component instanceof HasValueAndElement<?, ?> field) {
                field.setReadOnly(false);
            }
        });
    }



    private void resetFormInvalidState() {
        UiComponentUtils.getComponents(form).forEach(component -> {
            if (component instanceof HasValidation hasValidation && hasValidation.isInvalid()) {
                component.getElement().setProperty(PROPERTY_INVALID, false);
                component.getElement().executeJs("this.invalid = $0", false);
            }
        });
    }

    private ValidationErrors validateView(ItemGroup entity) {
        ValidationErrors validationErrors = viewValidation.validateUiComponents(form);
        if (!validationErrors.isEmpty()) {
            return validationErrors;
        }
        validationErrors.addAll(viewValidation.validateBeanGroup(UiCrossFieldChecks.class, entity));
        return validationErrors;
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


    private OperationResult navigateWithDiscard(BeforeLeaveEvent.ContinueNavigationAction navigationAction) {
        return navigate(navigationAction, StandardOutcome.DISCARD.getCloseAction());
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


    @Install(to = "itemGroupsDl", target = Target.DATA_LOADER, subject = "loadFromRepositoryDelegate")
    private List<ItemGroup> listLoadDelegate(Pageable pageable, JmixDataRepositoryContext context) {
        return repository.findAllSlice(pageable, context).getContent();
    }

    @Install(to = "pagination", subject = "totalCountByRepositoryDelegate")
    private Long paginationTotalCountByRepositoryDelegate(final JmixDataRepositoryContext context) {
        return repository.count(context);
    }

    @Install(to = "itemGroupsDataGrid.removeAction", subject = "delegate")
    private void itemGroupsDataGridRemoveDelegate(final Collection<ItemGroup> collection) {
        repository.deleteAll(collection);
    }

    @Install(to = "itemGroupDl", target = Target.DATA_LOADER, subject = "loadFromRepositoryDelegate")
    private Optional<ItemGroup> detailLoadDelegate(UUID id, FetchPlan fetchPlan) {
        return repository.findById(id, fetchPlan);
    }

    @Install(target = Target.DATA_CONTEXT)
    private Set<Object> saveDelegate(SaveContext saveContext) {
        return Set.of(repository.save(itemGroupDc.getItem()));
    }
}