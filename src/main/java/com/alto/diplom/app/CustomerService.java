package com.alto.diplom.app;

import com.alto.diplom.entity.core.Company;
import com.alto.diplom.entity.core.Customer;
import com.alto.diplom.repository.CustomerRepository;
import com.alto.diplom.repository.LoyaltyProgramConfigRepository;
import io.jmix.core.DataManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomerService {

    @Autowired
    private DataManager dataManager;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private LoyaltyProgramConfigRepository configRepository;

    /**
     * Основной метод регистрации
     */
    @Transactional
    public Customer registerNewCustomer(String phone, String firstName, Company company) {
        // 1. Проверяем, не зарегистрирован ли уже такой клиент
        customerRepository.findByPhoneAndCompany(phone, company).ifPresent(c -> {
            throw new IllegalArgumentException("Клиент с таким номером уже есть в этой компании");
        });

        // 2. Создаем и сохраняем Customer
        Customer customer = dataManager.create(Customer.class);
        customer.setPhone(phone);
        customer.setFirst_name(firstName);
        customer.setCompany(company);
        customer.setIs_verified(false);
        customer.setIs_blocked(false);

        // Используем save(), чтобы получить сохраненный объект с ID
        Customer savedCustomer = dataManager.save(customer);

        return savedCustomer;
    }


}