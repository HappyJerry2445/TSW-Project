package com.cardhaven.cardhaven.model.dao;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

public interface GenericDAO<T, ID> {
    void save(T t) throws SQLException;

    boolean delete(ID id) throws SQLException;

    T getById(ID id) throws SQLException;

    Collection<T> getAll(String order) throws SQLException;

    List<String> getAllowedOrderColumns();

}
