package modelo.dao;

import db.DB;
import modelo.dao.impl.LoginDaoJDBC;
import modelo.dao.impl.SubTarefaDaoJDBC;
import modelo.dao.impl.TarefaDaoJDBC;

public class GeradorDao {
	
	public static LoginDao criarLoginDao() {
		return new LoginDaoJDBC(DB.getConnection());
	}
	
	public static TarefaDao criarTarefaDao() {
		return new TarefaDaoJDBC(DB.getConnection());
	}
	
	public static SubTarefaDao criarSubTarefaDao() {
		return new SubTarefaDaoJDBC(DB.getConnection());
	}
}
