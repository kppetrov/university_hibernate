package ua.com.foxminded.university.dao.jpa;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ua.com.foxminded.university.dao.GenericDao;
import ua.com.foxminded.university.exception.DaoException;

public abstract class GenericDaoImpl<T> implements GenericDao<T> {
    private static final Logger LOGGER = LoggerFactory.getLogger(GenericDaoImpl.class);
    
    @PersistenceContext
    private EntityManager entityManager;
    private Class<T> entityClass;

    protected void setEntityClass(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    protected EntityManager getEntityManager() {
        return entityManager;
    }

    @Override
    public List<T> getAll() {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Getting all {}", entityClass);
        }
        try {
            return entityManager.createQuery("from " + entityClass.getName(), entityClass).getResultList();
        } catch (PersistenceException e) {
            String msg = String.format("Cannot get all %s", entityClass);
            throw new DaoException(msg, e);
        }
    }

    @Override
    public T getById(int id) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Getting {} by id. id={}", entityClass, id);
        }
        try {
            return entityManager.createQuery("from " + entityClass.getName() + " where id = :id", entityClass)
                .setParameter("id", id)
                .getSingleResult();
        } catch (NoResultException e) {
            String msg = String.format("The %s with id=%d does not exist", entityClass, id);
            throw new DaoException(msg, e);
        } catch (PersistenceException e) {
            String msg = String.format("Cannot get %s by id. id=%d", entityClass, id);
            throw new DaoException(msg, e);
        }
    }

    @Override
    public T insert(T item) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Creating {}. {}", entityClass, item);
        }
        try {
            entityManager.persist(item);
            return item;
        } catch (PersistenceException e) {
            String msg = String.format("Cannot create %s.", item);
            throw new DaoException(msg, e);
        }
    }

    @Override
    public void update(T item) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Updating {}. {}", entityClass, item);
        }
        try {
            entityManager.merge(item);
        } catch (PersistenceException e) {
            String msg = String.format("Cannot update %s", item);
            throw new DaoException(msg, e);
        }
    }

    @Override
    public void delete(int id) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Removung {}. id={}", entityClass, id);
        }
        try {
            entityManager.createQuery("delete from " + entityClass.getName() + " where id = :id")
                .setParameter("id", id)
                .executeUpdate();
        } catch (PersistenceException e) {
            String msg = String.format("Cannot remove %s by id. id=%d", entityClass, id);
            throw new DaoException(msg, e);
        }
    }
}
