package org.graniteds.tutorial.data.services;

import org.granite.tide.data.model.Page;
import org.granite.tide.data.model.PageInfo;
import org.graniteds.tutorial.data.entities.Account;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Map;

// tag::service-impl[]
@Stateless
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
