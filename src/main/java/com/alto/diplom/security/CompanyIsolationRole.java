package com.alto.diplom.security;

import com.alto.diplom.core.HasCompany;
import com.alto.diplom.entity.OnRegisterConfig;
import com.alto.diplom.entity.config.LoyaltyProgramConfig;
import com.alto.diplom.entity.core.Customer;
import com.alto.diplom.entity.items.ItemGroup;
import com.alto.diplom.entity.loyalty.CustomerBonusAccount;
import com.alto.diplom.entity.transactions.Transaction;
import io.jmix.security.role.annotation.JpqlRowLevelPolicy;
import io.jmix.security.role.annotation.RowLevelRole;

@RowLevelRole(name = "CompanyIsolationRole", code = CompanyIsolationRole.CODE)
public interface CompanyIsolationRole {
    String CODE = "company-isolation-role";
    String COMPANY_FILTER = "{E}.company.id = :current_user_companyId";

//    @JpqlRowLevelPolicy(entityClass = OnRegisterConfig.class,
//            where = COMPANY_FILTER)
//    void onRegisterConfigPolicy();

    @JpqlRowLevelPolicy(entityClass = Customer.class,
            where = COMPANY_FILTER)
    void customerPolicy();

    @JpqlRowLevelPolicy(entityClass = Transaction.class,
            where = COMPANY_FILTER)
    void transactionPolicy();

    @JpqlRowLevelPolicy(entityClass = CustomerBonusAccount.class,
            where = COMPANY_FILTER)
    void CustomerBonusAccountPolicy();

    @JpqlRowLevelPolicy(entityClass = ItemGroup.class,
            where = COMPANY_FILTER)
    void ItemGroupAccountPolicy();

    @JpqlRowLevelPolicy(entityClass = LoyaltyProgramConfig.class,
            where = COMPANY_FILTER)
    void LoyaltyProgramConfigAccountPolicy();
}
