package modelo.dao;

import java.util.List;

import modelo.entidades.Login;

public interface LoginDao {
	void insert(Login obj);
	void update(Login obj);
	void deleteById(Integer id);
	Login findById(Integer id);
	List<Login> findAll();
}
