package ua.com.foxminded.university.dao.jpa;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import ua.com.foxminded.university.dao.GenericDao;

public abstract class GenericDaoImpl<T> implements GenericDao<T> {

    @PersistenceContext
    private EntityManager entityManager;
    private Class<T> entityClass;

    protected void setEntityClass(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    @Override
    public List<T> getAll() {
        return entityManager.createQuery("from " + entityClass.getName(), entityClass).getResultList();
    }

    @Override
    public T getById(int id) {
        return entityManager.createQuery("from " + entityClass.getName() + " where id = :id", entityClass)
                .setParameter("id", id)
                .getSingleResult();
    }

    @Override
    public T insert(T item) {
        entityManager.persist(item);
        return item;
    }

    @Override
    public void update(T item) {
        entityManager.merge(item);
    }

    @Override
    public void delete(int id) {
        entityManager.createQuery("delete from " + entityClass.getName() + " where id = :id")
        .setParameter("id", id)
        .executeUpdate();
    }
}
