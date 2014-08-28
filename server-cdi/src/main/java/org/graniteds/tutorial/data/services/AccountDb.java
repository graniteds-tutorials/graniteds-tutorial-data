package org.graniteds.tutorial.data.services;

import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public class AccountDb {

	@Produces @PersistenceContext
	private EntityManager entityManager;
}
