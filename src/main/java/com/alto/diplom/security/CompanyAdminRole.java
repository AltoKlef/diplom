package com.alto.diplom.security;

import com.alto.diplom.entity.config.LoyaltyProgramConfig;
import com.alto.diplom.entity.core.Company;
import com.alto.diplom.entity.core.Customer;
import com.alto.diplom.entity.core.User;
import com.alto.diplom.entity.items.Item;
import com.alto.diplom.entity.items.ItemGroup;
import com.alto.diplom.entity.loyalty.CustomerBonusAccount;
import com.alto.diplom.entity.loyalty.LoyaltyLevel;
import com.alto.diplom.entity.transactions.Transaction;
import com.alto.diplom.entity.transactions.TransactionItem;
import io.jmix.security.model.EntityAttributePolicyAction;
import io.jmix.security.model.EntityPolicyAction;
import io.jmix.security.role.annotation.EntityAttributePolicy;
import io.jmix.security.role.annotation.EntityPolicy;
import io.jmix.security.role.annotation.ResourceRole;
import io.jmix.securityflowui.role.annotation.MenuPolicy;
import io.jmix.securityflowui.role.annotation.ViewPolicy;

@ResourceRole(name = "CompanyAdmin", code = CompanyAdminRole.CODE)
public interface CompanyAdminRole extends UiMinimalRole {
    String CODE = "company-admin";

    @EntityAttributePolicy(entityClass = Customer.class, attributes = "*", action = EntityAttributePolicyAction.MODIFY)
    @EntityPolicy(entityClass = Customer.class, actions = EntityPolicyAction.ALL)
    void customer();

    @EntityAttributePolicy(entityClass = CustomerBonusAccount.class, attributes = "*", action = EntityAttributePolicyAction.MODIFY)
    @EntityPolicy(entityClass = CustomerBonusAccount.class, actions = EntityPolicyAction.ALL)
    void customerBonusAccount();

    @EntityAttributePolicy(entityClass = Item.class, attributes = "*", action = EntityAttributePolicyAction.MODIFY)
    @EntityPolicy(entityClass = Item.class, actions = EntityPolicyAction.ALL)
    void item();

    @EntityAttributePolicy(entityClass = ItemGroup.class, attributes = "*", action = EntityAttributePolicyAction.MODIFY)
    @EntityPolicy(entityClass = ItemGroup.class, actions = EntityPolicyAction.ALL)
    void itemGroup();

    @EntityAttributePolicy(entityClass = LoyaltyLevel.class, attributes = "*", action = EntityAttributePolicyAction.MODIFY)
    @EntityPolicy(entityClass = LoyaltyLevel.class, actions = EntityPolicyAction.ALL)
    void loyaltyLevel();

    @EntityAttributePolicy(entityClass = LoyaltyProgramConfig.class, attributes = "*", action = EntityAttributePolicyAction.MODIFY)
    @EntityPolicy(entityClass = LoyaltyProgramConfig.class, actions = EntityPolicyAction.ALL)
    void loyaltyProgramConfig();

    @EntityAttributePolicy(entityClass = Transaction.class, attributes = "*", action = EntityAttributePolicyAction.MODIFY)
    @EntityPolicy(entityClass = Transaction.class, actions = EntityPolicyAction.ALL)
    void transaction();

    @EntityAttributePolicy(entityClass = TransactionItem.class, attributes = "*", action = EntityAttributePolicyAction.MODIFY)
    @EntityPolicy(entityClass = TransactionItem.class, actions = EntityPolicyAction.ALL)
    void transactionItem();

    @EntityAttributePolicy(entityClass = User.class, attributes = {"username", "firstName", "lastName", "password"}, action = EntityAttributePolicyAction.MODIFY)
    @EntityAttributePolicy(entityClass = User.class, attributes = "company", action = EntityAttributePolicyAction.VIEW)
    @EntityPolicy(entityClass = User.class, actions = {EntityPolicyAction.UPDATE, EntityPolicyAction.READ})
    void user();

    @EntityPolicy(entityClass = Company.class, actions = EntityPolicyAction.READ)
    @EntityAttributePolicy(entityClass = Company.class, attributes = "*", action = EntityAttributePolicyAction.VIEW)
    void company();

    @MenuPolicy(menuIds = "Customer.list")
    @ViewPolicy(viewIds = {"Customer.list", "Customer.detail"})
    void screens();
}