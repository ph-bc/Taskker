package modelo.servicos;

import java.util.List;

import modelo.dao.GeradorDao;
import modelo.dao.TarefaDao;
import modelo.entidades.Login;
import modelo.entidades.SubTarefa;
import modelo.entidades.Tarefa;

public class ServicoTarefa {
	
	private TarefaDao dao = GeradorDao.criarTarefaDao();
	
	public List<Tarefa> findAll() {
		return dao.findAll();
	}
	
	public List<Tarefa> findByLogin(Login login) {
		return dao.findByLogin(login);
	}

	public void saveOrUpdate(Tarefa obj) {
		if (obj.getCod() == null) {
			dao.insert(obj);
		} else {
			dao.update(obj);
		}
	}

	public void remove(SubTarefa obj) {
		dao.deleteById(obj.getCod());
	}
}
