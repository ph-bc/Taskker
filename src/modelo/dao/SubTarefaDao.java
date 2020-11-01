package modelo.dao;

import java.util.List;

import modelo.entidades.SubTarefa;
import modelo.entidades.Tarefa;

public interface SubTarefaDao {
	void insert(SubTarefa obj);
	void update(SubTarefa obj);
	void deleteById(Integer id);
	SubTarefa findById(Integer id);
	List<SubTarefa> findAll();
	List<SubTarefa> findByTarefa(Tarefa tarefa);
}
