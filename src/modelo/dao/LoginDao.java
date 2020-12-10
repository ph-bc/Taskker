package modelo.dao;

import java.util.List;

import modelo.entidades.Login;

public interface LoginDao {
	void insert(Login obj);
	void update(Login obj);
	void deleteById(Integer id);
	Login findUser(String usuario, String senha);
	List<Login> findAll();
}
