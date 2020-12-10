package gui;

import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.TimeUnit;

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
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import modelo.entidades.SubTarefa;
import modelo.entidades.Tarefa;
import modelo.enums.Situacao;
import modelo.exceptions.ValidationException;
import modelo.servicos.ServicoSubTarefa;

public class FormularioSubTarefaController implements Initializable {

	@FXML
	private TextField txtCod;

	@FXML
	private TextField txtNome;

	@FXML
	private TextArea txtDescricao;

	@FXML
	private TextField txtDataCriacao;

	@FXML
	private DatePicker dpPrazo;

	@FXML
	private TextField txtStatus;

	@FXML
	private Label labelErroNome;

	@FXML
	private Label labelErroPrazo;

	@FXML
	private Button btCancelar;

	@FXML
	private Button btConcluir;

	@FXML
	private Button btSalvar;

	private SubTarefa subTarefa;

	private Tarefa tarefa;

	private ServicoSubTarefa servicoSubTarefa;

	private List<AlteracaoDadosListener> alteracaoDadosListeners = new ArrayList<>();
	
	private Date agora = new Date();

	private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

	public void setSubTarefa(SubTarefa subTarefa) {
		this.subTarefa = subTarefa;
	}

	public void setTarefa(Tarefa tarefa) {
		this.tarefa = tarefa;
	}

	public void setServicoSubTarefa(ServicoSubTarefa servicoSubTarefa) {
		this.servicoSubTarefa = servicoSubTarefa;
	}

	@FXML
	public void onBtCancelarAction(ActionEvent event) {
		Utilidades.currentStage(event).close();
	}

	@FXML
	public void onBtConcluirAction(ActionEvent event) {
		Optional<ButtonType> result = Alertas.mostrarConfirmacao("Confirmar Conclusão", "A subtarefa foi finalizada?");

		if (result.get() == ButtonType.OK) {
			try {
				subTarefa.setStatus("CONCLUÍDA");
				servicoSubTarefa.saveOrUpdate(subTarefa);
				avisarAlteracaoDadosListener();
				Utilidades.currentStage(event).close();
			} catch (DbException e) {
				Alertas.mostrarAlerta("Erro ao concluir a subtarefa!", null, e.getMessage(), AlertType.ERROR);
			}
		}
	}

	@FXML
	public void onBtSalvarAction(ActionEvent event) {
		if (subTarefa == null) {
			throw new IllegalStateException("A classe é nula!");
		}

		if (servicoSubTarefa == null) {
			throw new IllegalStateException("O serviço é nulo!");
		}
		try {
			subTarefa = getDadosFormulario();
			servicoSubTarefa.saveOrUpdate(subTarefa);
			avisarAlteracaoDadosListener();
			Utilidades.currentStage(event).close();

		} catch (ValidationException e) {
			setMensagensErros(e.getErros());
		}

		catch (DbException e) {
			Alertas.mostrarAlerta("Erro ao salvar a subtarefa!", null, e.getMessage(), AlertType.ERROR);
		}
	}

	private SubTarefa getDadosFormulario() {
		SubTarefa obj = new SubTarefa();

		ValidationException exception = new ValidationException("Erro de Validação!");

		obj.setCod(null);

		if (txtNome.getText() == null || txtNome.getText().trim().equals("")) {
			exception.addErros("nome", "O campo não pode ser vazio");
		}
		obj.setNome(txtNome.getText());

		obj.setDescricao(txtDescricao.getText());

		try {
			obj.setDataCriacao(sdf.parse(txtDataCriacao.getText()));
		} catch (ParseException e) {
			e.printStackTrace();
		}

		if (dpPrazo.getValue() == null) {
			exception.addErros("dataPrazo", "O campo não pode ser vazio");
		} else {
			Instant instant = Instant.from(dpPrazo.getValue().atStartOfDay(ZoneId.systemDefault()));
			long diferenca = agora.getTime() - Date.from(instant).getTime();
			long atraso = TimeUnit.DAYS.convert(diferenca, TimeUnit.MILLISECONDS);
			if (atraso > 0) {
				exception.addErros("dataPrazo", "A data prazo tem que ser futura a data atual");
			}
			obj.setDataPrazo(Date.from(instant));
		}

		if (txtStatus.getText() == null || txtStatus.getText().trim().equals("")) {
			obj.setStatus("PENDENTE");
		} else {
			obj.setStatus(txtStatus.getText());
		}

		obj.setTarefa(tarefa);

		if (exception.getErros().size() > 0) {
			throw exception;
		}

		return obj;
	}

	public void updateCaixasFormulario() {
		if (subTarefa == null) {
			throw new IllegalStateException("A classe é nula!");
		}
		
		if (subTarefa.getCod() == null) {
			btConcluir.setDisable(true);
		}
		txtNome.setText(subTarefa.getNome());
		txtDescricao.setText(subTarefa.getDescricao());
		if (subTarefa.getDataCriacao() != null) {
			txtDataCriacao.setText(sdf.format(subTarefa.getDataCriacao()));
		} else {
			txtDataCriacao.setText(sdf.format(agora));
		}
		if (subTarefa.getDataPrazo() != null) {
			dpPrazo.setValue(LocalDate.ofInstant(subTarefa.getDataPrazo().toInstant(), ZoneId.systemDefault()));
		}
		if (subTarefa.getStatus() != null) {
			txtStatus.setText(subTarefa.getStatus().name());
		}
	}

	private void setMensagensErros(Map<String, String> erros) {
		Set<String> campos = erros.keySet();

		labelErroNome.setText((campos.contains("nome") ? erros.get("nome") : ""));
		labelErroPrazo.setText((campos.contains("dataPrazo") ? erros.get("dataPrazo") : ""));

	}

	public void desativaFormulario() {
		if (subTarefa != null) {
			if (subTarefa.getStatus() == Situacao.CONCLUÍDA) {
				txtNome.setDisable(true);
				txtDescricao.setDisable(true);
				txtDataCriacao.setDisable(true);
				dpPrazo.setDisable(true);
				txtStatus.setDisable(true);
				btConcluir.setDisable(true);
				btSalvar.setDisable(true);
			}
		}
	}

	public void sobrescreverDataChangeListener(AlteracaoDadosListener listener) {
		alteracaoDadosListeners.add(listener);
	}

	private void avisarAlteracaoDadosListener() {
		for (AlteracaoDadosListener listener : alteracaoDadosListeners) {
			listener.onDadosTableViewAlterado();
		}
	}

	private void iniciarNodes() {
		Restricoes.setTextFieldMaxLength(txtNome, 45);
		Restricoes.setTextAreaMaxLength(txtDescricao, 300);
		Utilidades.formatDatePicker(dpPrazo, "dd/MM/yyyy");

	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		iniciarNodes();
	}
}
