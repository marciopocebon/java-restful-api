package com.mario.java.restful.api.hibernate.jpa.repository.impl;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.hibernate.ObjectNotFoundException;
import org.hibernate.criterion.Restrictions;

import com.mario.java.restful.api.hibernate.jpa.repository.AbstractRepository;
import com.mario.java.restful.api.hibernate.jpa.repository.util.HibernateSessionManagerSingleton;

public class AbstractRepositoryHibernateImpl<T, ID extends Serializable> implements AbstractRepository<T, ID> {

	private String domainName;
	private Class<T> domainClass;
	private HibernateSessionManagerSingleton sessionManager;

	public AbstractRepositoryHibernateImpl(String domainName, Class<T> domainClass) {
		this(HibernateSessionManagerSingleton.getInstance(), domainName, domainClass);
	}

	public AbstractRepositoryHibernateImpl(HibernateSessionManagerSingleton sessionManager, String domainName, Class<T> domainClass){
		this.sessionManager = sessionManager;
		this.domainName = domainName;
		this.domainClass = domainClass;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<T> findAll() {
		this.sessionManager.openSession();
		List<T> entities = this.sessionManager.getSession().createQuery("from "+this.domainName).list();
		this.sessionManager.closeSession();
		return entities;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<T> findAll(Map<String, Object> criterias){
		List<T> entities = null;

		this.sessionManager.openSession();
		entities = this.sessionManager.getSession().createCriteria(this.domainClass).add(Restrictions.allEq(criterias)).list();
		this.sessionManager.closeSession();

		return entities;
	}

	@Override
	@SuppressWarnings("unchecked")
	public T find(ID id){
		this.sessionManager.openSession();
		T entity = (T) this.sessionManager.getSession().get(this.domainClass, id);
		this.sessionManager.closeSession();
		return entity;
	}

	@Override
	public void persist(T entity) {
		this.sessionManager.openSessionWithTransaction();
		this.sessionManager.getSession().save(entity);
		this.sessionManager.closeSessionWithTransaction();
	}

	@Override
	public void update(T entity) {
		this.sessionManager.openSessionWithTransaction();
		this.sessionManager.getSession().update(entity);
		this.sessionManager.closeSessionWithTransaction();
	}

	@Override
	public void delete(T entity) {
		this.sessionManager.openSessionWithTransaction();
		this.sessionManager.getSession().delete(entity);
		this.sessionManager.closeSessionWithTransaction();
	}

	@Override
	public void deleteAll() {
		List<T> entities = this.findAll();
		this.deleteAll(entities);
	}

	@Override
	public void deleteAll(Map<String, Object> criterias){
		List<T> entities = this.findAll(criterias);
		this.deleteAll(entities);
	}

	@Override
	public void deleteAll(List<T> entities){
		if(entities != null){
			this.deleteAllPrivate(entities);
		} else {
			throw (new ObjectNotFoundException(null, this.domainName));
		}
	}

	private void deleteAllPrivate(List<T> entities){
		this.sessionManager.openSessionWithTransaction();
		for (T entity : entities) {
			this.sessionManager.getSession().delete(entity);
		}
		this.sessionManager.closeSessionWithTransaction();
	}
}