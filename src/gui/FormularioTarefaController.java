package gui;

import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import db.DbException;
import gui.listeners.AlteracaoDadosListener;
import gui.util.Alertas;
import gui.util.Restricoes;
import gui.util.Utilidades;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import modelo.entidades.Login;
import modelo.entidades.Tarefa;
import modelo.exceptions.ValidationException;
import modelo.servicos.ServicoTarefa;

public class FormularioTarefaController implements Initializable {

	@FXML
	private TextField txtCod;

	@FXML
	private TextField txtNome;
	
	@FXML
	private TextField txtDataCriacao;
	
	@FXML
	private Label labelErroNome;
	
	@FXML
	private Button btCancelar;
	
	@FXML
	private Button btSalvar;
	
	private Login login;
	
	private Tarefa tarefa;

	private ServicoTarefa servicoTarefa;
	
	private List<AlteracaoDadosListener> alteracaoDadosListener = new ArrayList<>();
	
	private Date agora = new Date();
	
	private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
	
	public void setLogin(Login login) {
		this.login = login;
	}

	public void setTarefa(Tarefa tarefa) {
		this.tarefa = tarefa;
	}

	public void setServicoTarefa(ServicoTarefa servicoTarefa) {
		this.servicoTarefa = servicoTarefa;
	}

	@FXML
	public void onBtCancelarAction(ActionEvent event) {
		Utilidades.currentStage(event).close();
	}
	
	@FXML
	public void onBtSalvarAction(ActionEvent event) {
		if (tarefa == null) {
			throw new IllegalStateException("A classe é nula!");
		}

		if (servicoTarefa == null) {
			throw new IllegalStateException("O serviço é nulo!");
		}
		try {
			tarefa = getDadosFormulario();
			servicoTarefa.saveOrUpdate(tarefa);
			avisarAlteracaoDados();
			Utilidades.currentStage(event).close();

		} catch (ValidationException e) {
			setMensagensErros(e.getErros());
		}

		catch (DbException e) {
			Alertas.mostrarAlerta("Erro ao salvar a tarefa!", null, e.getMessage(), AlertType.ERROR);
		}
	}
	
	private Tarefa getDadosFormulario() {
		Tarefa obj = new Tarefa();

		ValidationException exception = new ValidationException("Erro de Validação!");

		obj.setCod(null);

		if (txtNome.getText() == null || txtNome.getText().trim().equals("")) {
			exception.addErros("nome", "O campo não pode ser vazio.");
		}
		obj.setNome(txtNome.getText());

		try {
			obj.setDataCriacao(sdf.parse(txtDataCriacao.getText()));
		} catch (ParseException e) {
			e.printStackTrace();
		}

		obj.setLogin(login);

		if (exception.getErros().size() > 0) {
			throw exception;
		}

		return obj;
	}
	
	public void updateCaixasFormulario() {
		if (tarefa == null) {
			throw new IllegalStateException("A classe é nula!");
		}

		txtNome.setText(tarefa.getNome());
		
		if (tarefa.getDataCriacao() != null) {
			txtDataCriacao.setText(sdf.format(tarefa.getDataCriacao()));
		} else {
			txtDataCriacao.setText(sdf.format(agora));
		}
	}
	
	private void setMensagensErros(Map<String, String> erros) {
		Set<String> campos = erros.keySet();

		labelErroNome.setText((campos.contains("nome") ? erros.get("nome") : ""));
	}
	
	public void sobrescreverAlteracaoDadosListener(AlteracaoDadosListener listener) {
		alteracaoDadosListener.add(listener);
	}

	private void avisarAlteracaoDados() {
		for (AlteracaoDadosListener listener : alteracaoDadosListener) {
			listener.onDadosComboBoxAlterado();
		}
	}
	
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		Restricoes.setTextFieldMaxLength(txtNome, 45);
	}
}
