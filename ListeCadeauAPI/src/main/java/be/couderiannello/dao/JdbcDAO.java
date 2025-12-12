package be.couderiannello.dao;

import java.sql.Connection;

public abstract class JdbcDAO<T> implements DAO<T> {

    protected Connection connect;

    public JdbcDAO(Connection conn) {
        this.connect = conn;
    }
}
