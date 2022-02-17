package ua.com.foxminded.university.dao;

import java.util.List;

public interface GenericDao<T> {
    List<T> getAll();
    T getById(int id);
    T insert(T item);
    void update(T item);
    void delete(int id);
}
