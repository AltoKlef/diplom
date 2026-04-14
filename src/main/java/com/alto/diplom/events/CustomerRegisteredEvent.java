package com.alto.diplom.events;

import com.alto.diplom.entity.core.Customer;
import org.springframework.context.ApplicationEvent;

public class CustomerRegisteredEvent extends ApplicationEvent {
    private final Customer customer;

    public CustomerRegisteredEvent(Object source, Customer customer) {
        super(source);
        this.customer = customer;
    }

    public Customer getCustomer() {
        return customer;
    }
}