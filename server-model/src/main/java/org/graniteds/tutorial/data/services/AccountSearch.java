package org.graniteds.tutorial.data.services;

import org.granite.tide.data.model.Page;
import org.granite.tide.data.model.PageInfo;
import org.graniteds.tutorial.data.entities.Account;

import javax.persistence.*;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AccountSearch {

    private EntityManager entityManager;

    public AccountSearch(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    // tag::findByFilter[]
    public Page<Account> findByFilter(Map<String, String> filter, PageInfo pageInfo) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        String searchText = filter.get("searchText"); // <1>
        if (searchText != null)
            searchText = searchText.trim();
        if (searchText != null && searchText.length() == 0)
            searchText = null;
        if (searchText != null)
            searchText = "%" + searchText + "%";

        CriteriaQuery<Long> cqc = cb.createQuery(Long.class);
        Root<Account> accountRoot = cqc.from(Account.class);
        cqc.select(cb.count(accountRoot));
        ParameterExpression<String> searchTextParam = null;
        Predicate searchTextPredicate = null;
        if (searchText != null) {
            searchTextParam = cb.parameter(String.class);
            searchTextPredicate = cb.or(
                cb.like(accountRoot.get("name").as(String.class), searchTextParam),
                cb.like(accountRoot.get("email").as(String.class), searchTextParam)
            );
            cqc.where(searchTextPredicate);
        }

        TypedQuery<Long> qc = entityManager.createQuery(cqc);
        if (searchText != null)
            qc.setParameter(searchTextParam, searchText);
        long resultCount = qc.getSingleResult();

        CriteriaQuery<Account> cq = cb.createQuery(Account.class);
        accountRoot = cq.from(Account.class);
        if (searchText != null)
            cq.where(searchTextPredicate);

        if (pageInfo != null && pageInfo.getSortInfo() != null && pageInfo.getSortInfo().getOrder() != null) { // <2>
            List<Order> orderBy = new ArrayList<Order>();
            for (int idx = 0; idx < pageInfo.getSortInfo().getOrder().length; idx++) {
                Path<String> sortPath = accountRoot.get(pageInfo.getSortInfo().getOrder()[idx]);
                orderBy.add(pageInfo.getSortInfo().getDesc()[idx] ? cb.desc(sortPath) : cb.asc(sortPath));
            }
            cq.orderBy(orderBy);
        }
        TypedQuery<Account> q = entityManager.createQuery(cq);
        if (searchText != null)
            q.setParameter(searchTextParam, searchText);
        List<Account> resultList = q
            .setFirstResult(pageInfo.getFirstResult())
            .setMaxResults(pageInfo.getMaxResults())
            .getResultList();

        return new Page<Account>(pageInfo.getFirstResult(), pageInfo.getMaxResults(), (int)resultCount, new ArrayList<Account>(resultList));
    }
    // end::findByFilter[]

}
