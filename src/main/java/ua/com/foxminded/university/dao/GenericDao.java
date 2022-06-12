package ua.com.foxminded.university.dao;

import java.util.List;

public interface GenericDao<T> {
    List<T> getAll();
    T getById(Long id);
    T insert(T item);
    void update(T item);
    void delete(Long id);
}
