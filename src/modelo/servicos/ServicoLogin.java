package modelo.servicos;

import java.util.List;

import modelo.dao.GeradorDao;
import modelo.dao.LoginDao;
import modelo.entidades.Login;

public class ServicoLogin {
	
	private LoginDao dao = GeradorDao.criarLoginDao();
	
	public Login findUser(String usuario, String senha) {
		return dao.findUser(usuario, senha);
	}

	public List<Login> findAll() {
		return dao.findAll();
	}

	public void salvar(Login obj) {
		dao.insert(obj);
	}

	public void remove(Login obj) {
		dao.deleteById(obj.getId());
	}
}
