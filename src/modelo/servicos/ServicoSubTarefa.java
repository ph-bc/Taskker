package modelo.servicos;

import java.util.List;

import modelo.dao.GeradorDao;
import modelo.dao.SubTarefaDao;
import modelo.entidades.SubTarefa;
import modelo.entidades.Tarefa;

public class ServicoSubTarefa {

	private SubTarefaDao dao = GeradorDao.criarSubTarefaDao();

	public List<SubTarefa> findAll() {
		return dao.findAll();
	}
	
	public List<SubTarefa> findByTarefa(Tarefa tarefa) {
		return dao.findByTarefa(tarefa);
	}

	public void saveOrUpdate(SubTarefa obj) {
		if (obj.getCod() == null) {
			dao.insert(obj);
		} else {
			dao.update(obj);
		}
	}

	public void remove(SubTarefa obj) {
		dao.deleteByCod(obj.getCod());
	}
}
