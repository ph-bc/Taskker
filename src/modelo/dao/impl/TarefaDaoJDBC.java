package modelo.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import db.DB;
import db.DbException;
import modelo.dao.TarefaDao;
import modelo.entidades.Login;
import modelo.entidades.Tarefa;

public class TarefaDaoJDBC implements TarefaDao {

	private Connection conn;

	public TarefaDaoJDBC(Connection conn) {
		this.conn = conn;
	}

	@Override
	public void insert(Tarefa obj) {
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement(
					"INSERT INTO tarefa (nome, dataCriacao, loginId) " + "VALUES (?, ?, ?)",
					Statement.RETURN_GENERATED_KEYS);

			st.setString(1, obj.getNome());
			st.setDate(2, new java.sql.Date(obj.getDataCriacao().getTime()));
			st.setInt(3, obj.getLogin().getId());

			int rowsAffected = st.executeUpdate();

			if (rowsAffected > 0) {
				ResultSet rs = st.getGeneratedKeys();
				if (rs.next()) {
					int cod = rs.getInt(1);
					obj.setCod(cod);
				}
				DB.closeResultSet(rs);
			} else {
				throw new DbException("Erro Inesperado! Nenhuma linha afetada!");
			}
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(st);
		}
	}

	@Override
	public void update(Tarefa obj) {
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement("UPDATE tarefa SET nome = ? WHERE cod = ?");

			st.setString(1, obj.getNome());
			st.setInt(2, obj.getCod());

			st.executeUpdate();
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(st);
		}
	}

	@Override
	public void deleteByCod(Integer id) {
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement("DELETE FROM tarefa WHERE cod = ?");

			st.setInt(1, id);

			st.executeUpdate();
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(st);
		}

	}

	@Override
	public Tarefa findById(Integer id) {
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = conn.prepareStatement("SELECT tarefa.*, login.usuario as logUser, login.senha as logSenha "
					+ "FROM tarefa INNER JOIN login ON tarefa.loginId = login.id WHERE tarefa.cod = ?");

			st.setInt(1, id);
			rs = st.executeQuery();
			if (rs.next()) {
				Login login = instanciarLogin(rs);
				Tarefa obj = instanciarTarefa(rs, login);
				return obj;
			}
			return null;
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs);
		}
	}

	@Override
	public List<Tarefa> findAll() {
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = conn.prepareStatement("SELECT tarefa.*, login.usuario as logUser, login.senha as logSenha FROM tarefa "
					+ "INNER JOIN login ON tarefa.loginId = login.id ORDER BY nome");

			rs = st.executeQuery();

			List<Tarefa> list = new ArrayList<>();
			Map<Integer, Login> map = new HashMap<>();

			while (rs.next()) {

				Login log = map.get(rs.getInt("loginId"));

				if (log == null) {
					log = instanciarLogin(rs);
					map.put(rs.getInt("loginId"), log);
				}

				Tarefa obj = instanciarTarefa(rs, log);
				list.add(obj);
			}
			return list;
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs);
		}
	}

	@Override
	public List<Tarefa> findByLogin(Login login) {
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = conn.prepareStatement(
					"SELECT tarefa.*, login.usuario as logUser, login.senha as logSenha FROM tarefa INNER JOIN login "
							+ "ON tarefa.loginId = login.id WHERE loginId = ? ORDER BY nome");

			st.setInt(1, login.getId());

			rs = st.executeQuery();

			List<Tarefa> list = new ArrayList<>();
			Map<Integer, Login> map = new HashMap<>();

			while (rs.next()) {

				Login log = map.get(rs.getInt("loginId"));

				if (log == null) {
					log = instanciarLogin(rs);
					map.put(rs.getInt("loginId"), log);
				}

				Tarefa obj = instanciarTarefa(rs, log);
				list.add(obj);
			}
			return list;
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs);
		}
	}

	private Tarefa instanciarTarefa(ResultSet rs, Login login) throws SQLException {
		Tarefa obj = new Tarefa();
		obj.setCod(rs.getInt("cod"));
		obj.setNome(rs.getString("nome"));
		obj.setDataCriacao(new java.util.Date(rs.getTimestamp("dataCriacao").getTime()));
		obj.setLogin(login);
		return obj;
	}

	private Login instanciarLogin(ResultSet rs) throws SQLException {
		Login login = new Login();
		login.setId(rs.getInt("loginId"));
		login.setUsuario(rs.getString("logUser"));
		login.setSenha(rs.getString("logSenha"));
		return login;
	}
}
