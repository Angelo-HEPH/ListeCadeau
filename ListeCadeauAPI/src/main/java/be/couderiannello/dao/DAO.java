package be.couderiannello.dao;

import java.util.List;

public interface DAO<T> {
	
	public abstract int create(T obj);
	
	public abstract boolean delete(T obj);
	
	public abstract boolean update(T obj);
	
	public abstract T find(int id);
	
	public abstract List<T> findAll();
}

