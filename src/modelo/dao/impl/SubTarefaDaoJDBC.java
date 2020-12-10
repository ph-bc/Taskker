package modelo.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import db.DB;
import db.DbException;
import modelo.dao.SubTarefaDao;
import modelo.entidades.SubTarefa;
import modelo.entidades.Tarefa;

public class SubTarefaDaoJDBC implements SubTarefaDao {

	private Connection conn;

	SimpleDateFormat sdf = new SimpleDateFormat();

	public SubTarefaDaoJDBC(Connection conn) {
		this.conn = conn;
	}

	@Override
	public void insert(SubTarefa obj) {
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement(
					"INSERT INTO subtarefa (nome, descricao, dataCriacao, dataPrazo, status, codTarefa) "
							+ "VALUES (?, ?, ?, ?, ?, ?)",
					Statement.RETURN_GENERATED_KEYS);

			st.setString(1, obj.getNome());
			st.setString(2, obj.getDescricao());
			st.setDate(3, new java.sql.Date(obj.getDataCriacao().getTime()));
			st.setDate(4, new java.sql.Date(obj.getDataPrazo().getTime()));
			st.setString(5, obj.getStatus().name());
			st.setInt(6, obj.getTarefa().getCod());

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
	public void update(SubTarefa obj) {
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement(
					"UPDATE subtarefa SET nome = ?, descricao = ?, dataPrazo = ?, status = ? WHERE cod = ?");

			st.setString(1, obj.getNome());
			st.setString(2, obj.getDescricao());
			st.setDate(3, new java.sql.Date(obj.getDataPrazo().getTime()));
			st.setString(4, obj.getStatus().name());
			st.setInt(5, obj.getCod());

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
			st = conn.prepareStatement("DELETE FROM subtarefa WHERE cod = ?");

			st.setInt(1, id);

			st.executeUpdate();
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(st);
		}

	}

	@Override
	public SubTarefa findById(Integer id) {
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = conn.prepareStatement(
					"SELECT subtarefa.*, tarefa.nome as tarefaNome " + "FROM subtarefa INNER JOIN tarefa "
							+ "ON subtarefa.codTarefa = tarefa.cod " + "WHERE subtarefa.cod = ?");

			st.setInt(1, id);
			rs = st.executeQuery();
			if (rs.next()) {
				Tarefa tarefa = instanciarTarefa(rs);
				SubTarefa obj = instanciarSubTarefa(rs, tarefa);
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
	public List<SubTarefa> findAll() {
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = conn.prepareStatement("SELECT subtarefa.*, tarefa.nome as tarefaNome "
					+ "FROM subtarefa INNER JOIN tarefa " + "ON subtarefa.codTarefa = tarefa.cod " + "ORDER BY dataCriacao DESC");

			rs = st.executeQuery();

			List<SubTarefa> list = new ArrayList<>();
			Map<Integer, Tarefa> map = new HashMap<>();

			while (rs.next()) {

				Tarefa tar = map.get(rs.getInt("codTarefa"));

				if (tar == null) {
					tar = instanciarTarefa(rs);
					map.put(rs.getInt("codTarefa"), tar);
				}

				SubTarefa obj = instanciarSubTarefa(rs, tar);
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
	public List<SubTarefa> findByTarefa(Tarefa tarefa) {
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = conn.prepareStatement(
					"SELECT subtarefa.*, tarefa.nome as tarefaNome FROM subtarefa INNER JOIN tarefa "
					+ "ON subtarefa.codTarefa = tarefa.cod WHERE codTarefa = ? ORDER BY dataCriacao DESC");

			st.setInt(1, tarefa.getCod());

			rs = st.executeQuery();

			List<SubTarefa> list = new ArrayList<>();
			Map<Integer, Tarefa> map = new HashMap<>();

			while (rs.next()) {

				Tarefa tar = map.get(rs.getInt("codTarefa"));

				if (tar == null) {
					tar = instanciarTarefa(rs);
					map.put(rs.getInt("codTarefa"), tar);
				}

				SubTarefa obj = instanciarSubTarefa(rs, tar);
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

	private SubTarefa instanciarSubTarefa(ResultSet rs, Tarefa tarefa) throws SQLException {
		SubTarefa obj = new SubTarefa();
		obj.setCod(rs.getInt("cod"));
		obj.setNome(rs.getString("nome"));
		obj.setDescricao(rs.getString("descricao"));
		obj.setDataCriacao(new java.util.Date(rs.getTimestamp("dataCriacao").getTime()));
		obj.setDataPrazo(new java.util.Date(rs.getTimestamp("dataPrazo").getTime()));
		obj.setStatus(rs.getString("status"));
		obj.setTarefa(tarefa);
		return obj;
	}

	private Tarefa instanciarTarefa(ResultSet rs) throws SQLException {
		Tarefa tarefa = new Tarefa();
		tarefa.setCod(rs.getInt("codTarefa"));
		tarefa.setNome(rs.getString("tarefaNome"));
		tarefa.setDataCriacao(new java.util.Date(rs.getTimestamp("dataCriacao").getTime()));
		return tarefa;
	}
}
