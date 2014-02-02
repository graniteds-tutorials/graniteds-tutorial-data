package org.graniteds.tutorial.data.services;

import java.util.Map;

import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.granite.tide.data.model.Page;
import org.granite.tide.data.model.PageInfo;
import org.graniteds.tutorial.data.entities.Account;

// tag::service-impl[]
@Stateless
@Local(AccountService.class)
public class AccountServiceBean implements AccountService {

    @PersistenceContext
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
