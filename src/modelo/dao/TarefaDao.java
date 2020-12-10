package modelo.dao;

import java.util.List;

import modelo.entidades.Login;
import modelo.entidades.Tarefa;

public interface TarefaDao {
	void insert(Tarefa obj);
	void update(Tarefa obj);
	void deleteByCod(Integer id);
	Tarefa findById(Integer id);
	List<Tarefa> findAll();
	List<Tarefa> findByLogin(Login login);
}
