package org.graniteds.tutorial.data.services;

import org.granite.tide.data.DataEnabled;
import org.granite.tide.data.model.Page;
import org.granite.tide.data.model.PageInfo;
import org.graniteds.tutorial.data.entities.Account;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Map;

// tag::service-impl[]
@Service
@Transactional
public class AccountServiceImpl implements AccountService {

    @PersistenceContext
    private EntityManager entityManager;

    public void save(Account user) {
        entityManager.merge(user);
    }

    public void remove(Account user) {
        entityManager.remove(user);
    }

    @Transactional(readOnly=true)
    public Page<Account> findByFilter(Map<String, String> filter, PageInfo pageInfo) {
        return new AccountSearch(entityManager).findByFilter(filter, pageInfo);
    }
}
// end::service-impl[]
