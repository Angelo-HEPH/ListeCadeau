package be.couderiannello.dao;

import java.util.List;

public interface DAO<T> {

    int create(T obj);

    boolean delete(T obj);

    boolean update(T obj);

    T find(int id);

    List<T> findAll();
}
