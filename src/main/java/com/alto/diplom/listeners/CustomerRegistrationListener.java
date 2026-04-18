package com.alto.diplom.listeners;

//import com.alto.diplom.app.TransactionService;
//import com.alto.diplom.entity.OnRegisterConfig;
//import com.alto.diplom.entity.core.Company;
//import com.alto.diplom.entity.core.Customer;
//import com.alto.diplom.entity.loyalty.CustomerBonusAccount;
//import com.alto.diplom.events.CustomerRegisteredEvent;
//import com.alto.diplom.repository.LoyaltyLevelRepository;
//import com.alto.diplom.repository.OnRegisterConfigRepository;
//import io.jmix.core.DataManager;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//import org.springframework.transaction.event.TransactionPhase;
//import org.springframework.transaction.event.TransactionalEventListener;
//import java.math.BigDecimal;
//import java.util.Optional;
//
//@Component
//public class CustomerRegistrationListener {
//
//    @Autowired
//    private DataManager dataManager;
//    @Autowired
//    private LoyaltyLevelRepository levelRepository;
//    @Autowired
//    private OnRegisterConfigRepository onRegisterConfigRepository;
//    @Autowired
//    private TransactionService transactionService;
//
//    // Используем BEFORE_COMMIT, чтобы создание счета вошло в ту же транзакцию, что и клиент
//    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
//    public void handleCustomerRegistration(CustomerRegisteredEvent event) {
//        Customer customer = event.getCustomer();
//        Company company = customer.getCompany();
//
//        // 1. Создаем основной счет
//        CustomerBonusAccount account = dataManager.create(CustomerBonusAccount.class);
//        account.setCustomer(customer);
//        account.setCompany(company);
//
//        // 2. Ищем настройки приветственного бонуса
//        Optional<OnRegisterConfig> configOpt = onRegisterConfigRepository.findByCompanyAndIsActiveTrue(company);
//
//        BigDecimal initialMarks = BigDecimal.ZERO;
//        Integer delayDays = 0;
//
//        if (configOpt.isPresent()) {
//            OnRegisterConfig config = configOpt.get();
//            initialMarks = config.getMarkIncrease() != null ? config.getMarkIncrease() : BigDecimal.ZERO;
//            delayDays = config.getDaysToDelayedActivation() != null ? config.getDaysToDelayedActivation() : 0;
//        }
//
//        // 3. Логика активации
//        if (initialMarks.compareTo(BigDecimal.ZERO) > 0) {
//            if (delayDays > 0) {
//                // Если есть задержка, на счет пока НЕ ПИШЕМ (или пишем в спец.поле "ожидающие баллы")
//                // Но обязательно создаем транзакцию-запись
//                transactionService.createWelcomeTransaction(customer, initialMarks, delayDays);
//                account.setMark(BigDecimal.ZERO);
//            } else {
//                // Если задержки нет, начисляем сразу
//                account.setMark(initialMarks);
//                transactionService.createWelcomeTransaction(customer, initialMarks, 0);
//            }
//        } else {
//            account.setMark(BigDecimal.ZERO);
//        }
//
//        // 4. Устанавливаем уровень
//        levelRepository.findDefaultLevel(company)
//                .ifPresent(account::setLoyaltyLevel);
//
//        dataManager.save(account);
//    }
//
//
//}