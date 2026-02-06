package dao;

import java.util.List;

public interface DAO<T> {
    void create(T t) throws DaoException;
    T read(int id) throws DaoException;
    void update(T t) throws DaoException;
    void delete(int id) throws DaoException;
    List<T> findAll() throws DaoException;
}
