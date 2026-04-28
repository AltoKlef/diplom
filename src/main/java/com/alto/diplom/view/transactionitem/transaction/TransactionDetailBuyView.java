package com.alto.diplom.view.transactionitem.transaction;

import com.alto.diplom.core.loyalty.LoyaltyService;
import com.alto.diplom.entity.TransactionParameters;
import com.alto.diplom.entity.items.Item;
import com.alto.diplom.entity.items.ItemGroup;
import com.alto.diplom.entity.transactions.Transaction;
import com.alto.diplom.entity.transactions.TransactionItem;
import com.alto.diplom.repository.TransactionRepository;
import com.alto.diplom.view.main.MainView;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import io.jmix.core.DataManager;
import io.jmix.core.FetchPlan;
import io.jmix.core.SaveContext;
import io.jmix.flowui.component.grid.DataGrid;
import io.jmix.flowui.model.CollectionLoader;
import io.jmix.flowui.model.CollectionPropertyContainer;
import io.jmix.flowui.model.InstanceContainer;
import io.jmix.flowui.view.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static com.helger.commons.ValueEnforcer.setEnabled;

@Route(value = "transactions/buy/:id", layout = MainView.class)
@ViewController("Transaction.detailBuy")
@ViewDescriptor("transaction-detail-buy-view.xml")
@EditedEntityContainer("transactionDc")
public class TransactionDetailBuyView extends StandardDetailView<Transaction> {

    @Autowired
    private DataManager dataManager;
    @Autowired
    private LoyaltyService loyaltyService;

    // --- КОНТЕЙНЕРЫ КОРЗИНЫ ---
    @ViewComponent
    private CollectionPropertyContainer<TransactionItem> transactionItemsDc;

    // --- КОНТЕЙНЕРЫ КАТАЛОГА (Добавь эти инъекции!) ---
    @ViewComponent
    private CollectionLoader<Item> itemsDl;
    @ViewComponent
    private DataGrid<Item> catalogItemsDataGrid;

    // --- UI ЭЛЕМЕНТЫ ---
    @ViewComponent
    private Checkbox spendPointsCheck;
    @ViewComponent
    private BigDecimalField pointsToSpendField;
    @ViewComponent
    private Span balanceInfo, earnedInfo, totalInfo;

    private TransactionParameters lastParams;

    // СЛУШАТЕЛЬ КАТАЛОГА: При выборе группы в дереве - загружаем товары
    @Subscribe(id = "itemGroupsDc", target = Target.DATA_CONTAINER)
    public void onItemGroupsDcItemChange(final InstanceContainer.ItemChangeEvent<ItemGroup> event) {
        ItemGroup selectedGroup = event.getItem();
        if (selectedGroup != null) {
            itemsDl.setParameter("group", selectedGroup);
            itemsDl.load();
        } else {
            // Если группа не выбрана (например, в дереве ткнули в пустоту)
            itemsDl.removeParameter("group");
            // Вместо ручной очистки лучше просто не загружать ничего
            itemsDl.load();
        }
    }

    private void applyScenarioToItems(TransactionParameters.CalculationScenario scenario) {
        scenario.getItemResults().forEach(res -> {
            TransactionItem item = res.getTransactionItem();
            item.setMarksEarned(res.getMarksEarned());
            item.setMarksSpent(res.getMarksSpent());
        });
    }

    @Subscribe
    public void onBeforeShow(final BeforeShowEvent event) {
        Transaction transaction = getEditedEntity();
        if (transaction.getExternalNumber() == null) {
            transaction.setExternalNumber(UUID.randomUUID().toString());
        }
    }

//    @Subscribe("addToCartBtn")
//    public void onAddToCartBtnClick(ClickEvent<Button> event) {
//        Item selectedItem = catalogItemsDataGrid.getSingleSelectedItem();
//        if (selectedItem == null) return;
//
//        // 1. Создаем позицию чека
//        TransactionItem newItem = dataManager.create(TransactionItem.class);
//        newItem.setItem(selectedItem);
//        newItem.setPrice(selectedItem.getPrice());
//        newItem.setQuantity(BigDecimal.ONE);
//
//        // Считаем сумму строки (важно!)
//        newItem.setTotalSum(newItem.getPrice().multiply(newItem.getQuantity()));
//
//        // Используем твой метод-сеттер для связи (тот самый transactionn)
//        newItem.setTransaction(getEditedEntity());
//
//        // 2. Добавляем в корзину
//        transactionItemsDc.getMutableItems().add(newItem);
//
//        // 3. Сразу обновляем общую сумму в главной сущности (ЛЕЧИМ NPE ТУТ)
//        updateTransactionTotal();
//    }
    @Subscribe("addToCartBtn")
    public void onAddToCartBtnClick(ClickEvent<Button> event) {
        Item selectedItem = catalogItemsDataGrid.getSingleSelectedItem();
        if (selectedItem == null) return;

        // 1. Ищем, есть ли уже этот товар в корзине
        Optional<TransactionItem> existingItem = transactionItemsDc.getItems().stream()
                .filter(ti -> ti.getItem().equals(selectedItem))
                .findFirst();

        if (existingItem.isPresent()) {
            // 2. Если товар найден — увеличиваем количество
            TransactionItem itemInCart = existingItem.get();
            BigDecimal newQuantity = itemInCart.getQuantity().add(BigDecimal.ONE);

            itemInCart.setQuantity(newQuantity);
            // Пересчитываем сумму строки (Цена * Новое кол-во)
            itemInCart.setTotalSum(itemInCart.getPrice().multiply(newQuantity));

            // Важно: уведомляем UI об изменении существующего объекта
            transactionItemsDc.replaceItem(itemInCart);
        } else {
            // 3. Если товара нет — создаем новую позицию (твой старый код)
            TransactionItem newItem = dataManager.create(TransactionItem.class);
            newItem.setItem(selectedItem);
            newItem.setPrice(selectedItem.getPrice());
            newItem.setQuantity(BigDecimal.ONE);
            newItem.setTotalSum(selectedItem.getPrice());
            newItem.setTransaction(getEditedEntity());

            transactionItemsDc.getMutableItems().add(newItem);
        }

        // Общий пересчет суммы транзакции и сброс параметров лояльности
        updateTransactionTotal();
    }

    private void updateTransactionTotal() {
        BigDecimal rawTotal = transactionItemsDc.getItems().stream()
                .map(TransactionItem::getTotalSum)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Устанавливаем сумму в Transaction, чтобы LoyaltyService ее видел
        getEditedEntity().setTotalAmount(rawTotal);

        totalInfo.setText("ИТОГО (без скидок): " + rawTotal);
        lastParams = null; // Требуем нового расчета лояльности
    }

    @Subscribe("calculateBtn")
    public void onCalculateBtnClick(ClickEvent<Button> event) {
        Transaction transaction = getEditedEntity();

        // Если сумма еще null (хотя мы ее сетнули выше, на всякий случай)
        if (transaction.getTotalAmount() == null) {
            updateTransactionTotal();
        }

        if (transaction.getCustomer() == null || transaction.getTotalAmount().compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }

        // Теперь вызываем твой сервис
        lastParams = loyaltyService.calculateFullParameters(transaction);

        // Выбираем сценарий
        TransactionParameters.CalculationScenario scenario = Boolean.TRUE.equals(spendPointsCheck.getValue())
                ? lastParams.getMaxSpendScenario()
                : lastParams.getZeroSpendScenario();

        // Обновляем красивые циферки на экране
        balanceInfo.setText("Баллы клиента: " + lastParams.getBalanceBefore());
        pointsToSpendField.setValue(scenario.getMarksToSpend());
        earnedInfo.setText("Будет начислено: " + scenario.getMarksToEarn());
        totalInfo.setText("К ОПЛАТЕ: " + scenario.getTotalAmount());

        // Прокидываем баллы в каждую строку (TransactionItem)
        applyScenarioToItems(scenario);
    }

    @Subscribe("buyBtn")
    public void onBuyBtnClick(ClickEvent<Button> event) {
        Transaction transaction = getEditedEntity();

        // 1. Проверка: не пустой ли чек?
        if (transactionItemsDc.getItems().isEmpty()) {
            // Уведомление (можно использовать Notifications)
            return;
        }

        // 2. Гарантируем, что расчет был сделан
        if (lastParams == null) {
            onCalculateBtnClick(null);
        }

        // 3. Выбираем финальный сценарий на основе галочки
        TransactionParameters.CalculationScenario finalScenario = Boolean.TRUE.equals(spendPointsCheck.getValue())
                ? lastParams.getMaxSpendScenario()
                : lastParams.getZeroSpendScenario();

        // 4. Записываем итоги в транзакцию
        transaction.setMarksSpent(finalScenario.getMarksToSpend());
        transaction.setMarksEarned(finalScenario.getMarksToEarn());
        transaction.setTotalAmount(finalScenario.getTotalAmount());
        transaction.setExternalTransactionTimestamp(OffsetDateTime.now());
        event.getSource().setEnabled(false);
        try {
            loyaltyService.executeTransaction(transaction);

            close(StandardOutcome.SAVE);

        } catch (Exception e) {
             event.getSource().setEnabled(true);

            throw e;
        }
    }
}