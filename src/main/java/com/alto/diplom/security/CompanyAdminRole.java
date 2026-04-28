package com.alto.diplom.security;

import com.alto.diplom.entity.config.LoyaltyProgramConfig;
import com.alto.diplom.entity.config.OnRegisterConfig;
import com.alto.diplom.entity.core.Company;
import com.alto.diplom.entity.core.Customer;
import com.alto.diplom.entity.core.CustomerGroup;
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

    @EntityPolicy(entityClass = Customer.class, actions = EntityPolicyAction.ALL)
    @EntityAttributePolicy(entityClass = Customer.class,
            attributes = {"phone", "firstName", "lastName", "isVerified", "isBlocked", "birthday"},
            action = EntityAttributePolicyAction.MODIFY)
    void customer();
    @EntityAttributePolicy(entityClass = CustomerBonusAccount.class, attributes = {"customer", "loyaltyLevel", "effectiveCash", "mark", "lastModifiedBy", "lastModifiedDate"}, action = EntityAttributePolicyAction.MODIFY)
    @EntityPolicy(entityClass = CustomerBonusAccount.class, actions = EntityPolicyAction.ALL)
    void customerBonusAccount();

    @EntityAttributePolicy(entityClass = Item.class, attributes = "*", action = EntityAttributePolicyAction.MODIFY)
    @EntityPolicy(entityClass = Item.class, actions = EntityPolicyAction.ALL)
    void item();

    @EntityAttributePolicy(entityClass = ItemGroup.class, attributes = {"deletedBy", "deletedDate", "parent", "children", "name"}, action = EntityAttributePolicyAction.MODIFY)
    @EntityPolicy(entityClass = ItemGroup.class, actions = EntityPolicyAction.ALL)
    void itemGroup();

    @EntityAttributePolicy(entityClass = LoyaltyLevel.class, attributes = "*", action = EntityAttributePolicyAction.MODIFY)
    @EntityPolicy(entityClass = LoyaltyLevel.class, actions = EntityPolicyAction.ALL)
    void loyaltyLevel();

    @EntityAttributePolicy(entityClass = LoyaltyProgramConfig.class, attributes = {"name", "priority", "isActive", "createdBy", "createdDate", "lastModifiedBy", "lastModifiedDate", "deletedBy", "deletedDate"}, action = EntityAttributePolicyAction.MODIFY)
    @EntityPolicy(entityClass = LoyaltyProgramConfig.class, actions = EntityPolicyAction.ALL)
    void loyaltyProgramConfig();

    @EntityAttributePolicy(entityClass = Transaction.class, attributes = {"customer", "externalNumber", "totalAmount", "marksEarned", "marksSpent", "externalTransactionTimestamp", "items", "initialTransaction", "isNeedToActivate", "timeActivate"}, action = EntityAttributePolicyAction.MODIFY)
    @EntityPolicy(entityClass = Transaction.class, actions = EntityPolicyAction.ALL)
    void transaction();

    @EntityAttributePolicy(entityClass = TransactionItem.class, attributes = "*", action = EntityAttributePolicyAction.MODIFY)
    @EntityPolicy(entityClass = TransactionItem.class, actions = EntityPolicyAction.ALL)
    void transactionItem();

    @EntityAttributePolicy(entityClass = User.class, attributes = {"username", "firstName", "lastName", "password"}, action = EntityAttributePolicyAction.MODIFY)
    @EntityAttributePolicy(entityClass = User.class, attributes = "company", action = EntityAttributePolicyAction.VIEW)
    @EntityPolicy(entityClass = User.class, actions = {EntityPolicyAction.UPDATE, EntityPolicyAction.READ})
    void user();

    @EntityAttributePolicy(entityClass = Company.class, attributes = "*", action = EntityAttributePolicyAction.VIEW)
    void company();

    @MenuPolicy(menuIds = {"Customer.list", "LoyaltyLevel.list", "CustomerBonusAccount.list", "LoyaltyProgramConfig.list", "ItemGroup.list", "Transaction_.list", "CustomerGroup.list"})
    @ViewPolicy(viewIds = {"Customer.list", "Customer.detail", "LoyaltyLevel.list", "CustomerBonusAccount.list", "LoyaltyProgramConfig.list", "ItemGroup.list", "ItemGroup.detail", "Item.detail", "LoyaltyLevel.detail", "LoyaltyProgramConfig.detail", "Transaction_.list", "CustomerGroup.list", "CustomerGroup.detail", "Transaction_.detail","Transaction.detailBuy", "TransactionItem.detail"})
    void screens();

    @EntityPolicy(entityClass = CustomerGroup.class, actions = EntityPolicyAction.ALL)
    void customerGroup();

    @EntityPolicy(entityClass = OnRegisterConfig.class, actions = EntityPolicyAction.ALL)
    void onRegisterConfig();
}