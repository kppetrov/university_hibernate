package ua.com.foxminded.university.service;

import java.util.List;

public interface GenericService<T> {
    List<T> getAll();
    T getById(int id);
    T insert(T item);
    void update(T item);
    void delete(int id);
}
