package com.alto.diplom.view.loyaltyprogramconfig;

import com.alto.diplom.entity.config.LoyaltyProgramConfig;
import com.alto.diplom.entity.loyalty.LoyaltyLevel;
import com.alto.diplom.repository.LoyaltyProgramConfigRepository;
import com.alto.diplom.view.main.MainView;
import com.vaadin.flow.router.Route;
import io.jmix.core.repository.JmixDataRepositoryContext;
import io.jmix.flowui.component.grid.DataGrid;
import io.jmix.flowui.model.CollectionContainer;
import io.jmix.flowui.model.InstanceContainer;
import io.jmix.flowui.view.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;

import java.util.Collection;
import java.util.List;

@Route(value = "loyalty-program-configs", layout = MainView.class)
@ViewController(id = "LoyaltyProgramConfig.list")
@ViewDescriptor(path = "loyalty-program-config-list-view.xml")
@LookupComponent("loyaltyProgramConfigsDataGrid")
@DialogMode(width = "64em")
public class LoyaltyProgramConfigListView extends StandardListView<LoyaltyProgramConfig> {

    @Autowired
    private LoyaltyProgramConfigRepository repository;

    @ViewComponent
    private InstanceContainer<LoyaltyProgramConfig> loyaltyProgramConfigDc;

    @ViewComponent
    private CollectionContainer<LoyaltyLevel> levelsDc; // Нужно для расчета номера и доступа к данным

    @ViewComponent
    private DataGrid<LoyaltyProgramConfig> loyaltyProgramConfigsDataGrid; // Для получения выбранного элемента

    @Install(to = "loyaltyProgramConfigsDataGrid.remove", subject = "delegate")
    private void loyaltyProgramConfigsDataGridRemoveDelegate(final Collection<LoyaltyProgramConfig> collection) {
        repository.deleteAll(collection);
    }

    @Subscribe(id = "loyaltyProgramConfigsDc", target = Target.DATA_CONTAINER)
    public void onLoyaltyProgramConfigsDcItemChange(final InstanceContainer.ItemChangeEvent<LoyaltyProgramConfig> event) {
        // Устанавливаем выбранный элемент в "одиночный" контейнер.
        // Это заставляет вложенный levelsDc обновить список уровней.
        loyaltyProgramConfigDc.setItem(event.getItem());
    }

    @Install(to = "levelsDataGrid.create", subject = "initializer")
    private void levelsDataGridCreateInitializer(final LoyaltyLevel loyaltyLevel) {
        // Берем программу, выбранную в левой таблице
        LoyaltyProgramConfig selectedConfig = loyaltyProgramConfigsDataGrid.getSingleSelectedItem();

        if (selectedConfig != null) {
            // Привязываем новый уровень к этой программе
            loyaltyLevel.setLoyaltyProgramConfig(selectedConfig);

            // Автоматически ставим следующий номер (например, 1, 2, 3...)
            int nextNumber = levelsDc.getItems().size() + 1;
            loyaltyLevel.setNumber((short) nextNumber);

            // Если в LoyaltyLevel есть поле company, проставляем его из родителя
            // loyaltyLevel.setCompany(selectedConfig.getCompany());
        }
    }
}