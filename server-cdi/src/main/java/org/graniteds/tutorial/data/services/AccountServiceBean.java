package org.graniteds.tutorial.data.services;

import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.granite.tide.data.model.Page;
import org.granite.tide.data.model.PageInfo;
import org.graniteds.tutorial.data.entities.Account;

// tag::service-impl[]
@Stateless
public class AccountServiceBean implements AccountService {

    @Inject
    private EntityManager entityManager;

    public void save(Account account) {
        entityManager.merge(account); // <1>
    }

    public void remove(Account account) {
        account = entityManager.find(Account.class, account.getId());
        entityManager.remove(account);
    }

    public Page<Account> findByFilter(Map<String, String> filter, PageInfo pageInfo) { // <2>
        return new AccountSearch(entityManager).findByFilter(filter, pageInfo);
    }
}
// end::service-impl[]
