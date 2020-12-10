package gui;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;

import aplicacao.Main;
import db.DbIntegrityException;
import gui.listeners.AlteracaoDadosListener;
import gui.util.Alertas;
import gui.util.Utilidades;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;
import modelo.entidades.Login;
import modelo.entidades.SubTarefa;
import modelo.entidades.Tarefa;
import modelo.enums.Situacao;
import modelo.servicos.ServicoSubTarefa;
import modelo.servicos.ServicoTarefa;

public class MainController implements Initializable, AlteracaoDadosListener {

	private Login login = LoginController.login;

	private static Tarefa tarefa;

	private Date agora = new Date();
	
	private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

	@FXML
	private Button btLogout;
	
	@FXML
	private Label LabelPrincipal;
	
	@FXML
	private Label labelData;

	@FXML
	private Button btCriarTarefa;

	@FXML
	private Button btCriarSubTarefa;

	@FXML
	private Button btEditarTarefa;

	@FXML
	private Button btExcluirTarefa;

	@FXML
	private ComboBox<Tarefa> comboBoxTarefa;

	@FXML
	private TableView<SubTarefa> tableViewSubTarefa;

	@FXML
	private TableColumn<SubTarefa, String> tableColumnNome;

	@FXML
	private TableColumn<SubTarefa, Date> tableColumnCriacao;

	@FXML
	private TableColumn<SubTarefa, Date> tableColumnPrazo;

	@FXML
	private TableColumn<SubTarefa, Situacao> tableColumnStatus;

	@FXML
	private TableColumn<SubTarefa, SubTarefa> tableColumnEDITAR;

	@FXML
	private TableColumn<SubTarefa, SubTarefa> tableColumnREMOVER;

	private ObservableList<Tarefa> obsListTarefa;

	private ObservableList<SubTarefa> obsListSubTarefa;

	private ServicoSubTarefa servicoSubTarefa;

	private ServicoTarefa servicoTarefa;

	public void setServicoSubTarefa(ServicoSubTarefa servico) {
		this.servicoSubTarefa = servico;
	}

	public void setServicoTarefa(ServicoTarefa servicoTarefa) {
		this.servicoTarefa = servicoTarefa;
	}

	@FXML
	public void onComboBoxTarefaAction() {
		tarefa = comboBoxTarefa.getSelectionModel().getSelectedItem();
		updateTableView();
	}

	public void updateComboBox() {
		List<Tarefa> list = servicoTarefa.findByLogin(login);
		obsListTarefa = FXCollections.observableArrayList(list);
		comboBoxTarefa.setItems(obsListTarefa);
	}

	private void iniciarComboBoxTarefa() {
		Callback<ListView<Tarefa>, ListCell<Tarefa>> factory = lv -> new ListCell<Tarefa>() {
			@Override
			protected void updateItem(Tarefa item, boolean empty) {
				super.updateItem(item, empty);
				setText(empty ? "" : item.getNome());
			}
		};
		comboBoxTarefa.setCellFactory(factory);
		comboBoxTarefa.setButtonCell(factory.call(null));
	}

	@FXML
	public void onBtCriarTarefaAction(ActionEvent event) {
		Stage parentStage = Utilidades.currentStage(event);
		Tarefa obj = new Tarefa();
		criarFormularioTarefa(obj, "/gui/FormularioTarefa.fxml", "Nova Tarefa", parentStage);
	}

	@FXML
	public void onBtEditarTarefaAction(ActionEvent event) {
		Stage parentStage = Utilidades.currentStage(event);

		if (tarefa == null) {
			Alertas.mostrarAlerta("Nenhuma tarefa foi selecionada!", null, "Por favor, selecione uma tarefa primeiro!",
					AlertType.ERROR);
		} else {
			criarFormularioTarefa(tarefa, "/gui/FormularioTarefa.fxml", tarefa.getNome(), parentStage);
		}
	}

	private void criarFormularioTarefa(Tarefa obj, String absoluteName, String titulo, Stage parentStage) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			Pane pane = loader.load();

			FormularioTarefaController controller = loader.getController();
			controller.setTarefa(obj);
			controller.setLogin(login);
			controller.setServicoTarefa(new ServicoTarefa());
			controller.sobrescreverAlteracaoDadosListener(this);
			controller.updateCaixasFormulario();

			Stage stageDialogo = new Stage();
			stageDialogo.setTitle(titulo);
			stageDialogo.getIcons().add(Main.icone);
			stageDialogo.setScene(new Scene(pane));
			stageDialogo.setResizable(false);
			stageDialogo.initOwner(parentStage);
			stageDialogo.initModality(Modality.WINDOW_MODAL);
			stageDialogo.showAndWait();
		} catch (IOException e) {
			e.printStackTrace();
			Alertas.mostrarAlerta("IO Exception", "Erro carregando janela", e.getMessage(), AlertType.ERROR);
		}
	}

	@FXML
	public void onBtCriarSubTarefaAction(ActionEvent event) {
		Stage parentStage = Utilidades.currentStage(event);
		SubTarefa obj = new SubTarefa();

		if (tarefa == null) {
			Alertas.mostrarAlerta("Nenhuma tarefa foi selecionada!", null, "Por favor, selecione uma tarefa primeiro!",
					AlertType.ERROR);
		} else {
			criarFormularioSubTarefa(obj, "/gui/FormularioSubTarefa.fxml", "Nova Subtarefa", parentStage);
		}
	}

	private void criarFormularioSubTarefa(SubTarefa obj, String absoluteName, String titulo, Stage parentStage) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			Pane pane = loader.load();

			FormularioSubTarefaController controller = loader.getController();
			controller.setSubTarefa(obj);
			controller.setTarefa(tarefa);
			controller.setServicoSubTarefa(new ServicoSubTarefa());
			controller.sobrescreverDataChangeListener(this);
			controller.updateCaixasFormulario();
			controller.desativaFormulario();

			Stage stageDialogo = new Stage();
			stageDialogo.setTitle(titulo);
			stageDialogo.getIcons().add(Main.icone);
			stageDialogo.setScene(new Scene(pane));
			stageDialogo.setResizable(false);
			stageDialogo.initOwner(parentStage);
			stageDialogo.initModality(Modality.WINDOW_MODAL);
			stageDialogo.showAndWait();
		} catch (IOException e) {
			e.printStackTrace();
			Alertas.mostrarAlerta("IO Exception", "Erro carregando janela", e.getMessage(), AlertType.ERROR);
		}
	}

	@FXML
	private void onBtExcluirAction() {
		if (tarefa == null) {
			Alertas.mostrarAlerta("Nenhuma tarefa foi selecionada!", null, "Por favor, selecione uma tarefa primeiro!",
					AlertType.ERROR);
		} else {
			Optional<ButtonType> result = Alertas.mostrarConfirmacao("Confirmar Exclusão",
					"Tem certeza que deseja deletar essa tarefa?");

			if (result.get() == ButtonType.OK) {
				try {
					for (SubTarefa obj : servicoSubTarefa.findByTarefa(tarefa)) {
						servicoSubTarefa.remove(obj);
					}
					servicoTarefa.remove(tarefa);
					updateComboBox();
					iniciarComboBoxTarefa();
				} catch (DbIntegrityException e) {
					Alertas.mostrarAlerta("Erro removendo objeto!", null, e.getMessage(), AlertType.ERROR);
				}
			}
		}
	}

	@FXML
	private void onBtLogoutAction(ActionEvent event) {
		Optional<ButtonType> result = Alertas.mostrarConfirmacao("Logout", "Deseja mesmo sair?");

		if (result.get() == ButtonType.OK) {
			Utilidades.currentStage(event).hide();

			try {
				FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/Login.fxml"));
				AnchorPane loginPane = loader.load();

				Stage loginStage = new Stage();
				loginStage.initStyle(StageStyle.TRANSPARENT);
				loginStage.setScene(new Scene(loginPane));
				loginStage.show();

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void iniciarNodes() {
		tableColumnNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
		tableColumnCriacao.setCellValueFactory(new PropertyValueFactory<>("dataCriacao"));
		tableColumnPrazo.setCellValueFactory(new PropertyValueFactory<>("dataPrazo"));
		tableColumnStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
		Utilidades.formatTableColumnDate(tableColumnPrazo, "dd/MM/yyyy");
		Utilidades.formatTableColumnDate(tableColumnCriacao, "dd/MM/yyyy");
		iniciarComboBoxTarefa();
	}

	public void updateTableView() {
		try {
			if (tarefa == null) {
				tableViewSubTarefa.setItems(null);
			} else {
				List<SubTarefa> list = servicoSubTarefa.findByTarefa(tarefa);
				obsListSubTarefa = FXCollections.observableArrayList(list);
				tableViewSubTarefa.setItems(obsListSubTarefa);
				iniciarDetalhesButtons();
				iniciarRemoverButtons();
			}
		} catch (RuntimeException e) {
		}
	}

	public void updateStatus() {
		List<SubTarefa> list = servicoSubTarefa.findAll();
		for (SubTarefa obj : list) {
			long diferenca = agora.getTime() - obj.getDataPrazo().getTime();
			long atraso = TimeUnit.DAYS.convert(diferenca, TimeUnit.MILLISECONDS);
			if (obj.getStatus() != Situacao.CONCLUÍDA) {
				if (atraso > 0) {
					obj.setStatus("ATRASADA");
				} else if (atraso == 0) {
					obj.setStatus("PENDENTE");
				}
				servicoSubTarefa.saveOrUpdate(obj);
			}
		}
	}

	private void iniciarDetalhesButtons() {
		tableColumnEDITAR.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnEDITAR.setCellFactory(param -> new TableCell<SubTarefa, SubTarefa>() {
			private final Button button = new Button("Detalhes");

			@Override
			protected void updateItem(SubTarefa obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setStyle("-fx-background-color: teal;");
				button.setTextFill(Color.WHITE);
				button.setCursor(Cursor.HAND);
				button.setOnAction(event -> criarFormularioSubTarefa(obj, "/gui/FormularioSubTarefa.fxml",
						obj.getNome(), Utilidades.currentStage(event)));
			}
		});
	}

	private void iniciarRemoverButtons() {
		tableColumnREMOVER.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnREMOVER.setCellFactory(param -> new TableCell<SubTarefa, SubTarefa>() {
			private final Button button = new Button("Remover");

			@Override
			protected void updateItem(SubTarefa obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setStyle("-fx-background-color: teal;");
				button.setTextFill(Color.WHITE);
				button.setCursor(Cursor.HAND);
				button.setOnAction(event -> removerSubTarefa(obj));
			}
		});
	}

	private void removerSubTarefa(SubTarefa obj) {
		Optional<ButtonType> result = Alertas.mostrarConfirmacao("Confirmar Exclusão",
				"Tem certeza que deseja deletar essa subtarefa?");

		if (result.get() == ButtonType.OK) {
			if (servicoSubTarefa == null) {
				throw new IllegalStateException("O serviço é nulo!");
			}
			try {
				servicoSubTarefa.remove(obj);
				updateTableView();
			} catch (DbIntegrityException e) {
				Alertas.mostrarAlerta("Erro removendo objeto!", null, e.getMessage(), AlertType.ERROR);
			}
		}
	}

	@Override
	public void onDadosComboBoxAlterado() {
		updateComboBox();
	}

	@Override
	public void onDadosTableViewAlterado() {
		updateStatus();
		updateTableView();
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		setServicoSubTarefa(new ServicoSubTarefa());
		setServicoTarefa(new ServicoTarefa());

		LabelPrincipal.setText("Olá, " + login.getUsuario() + "!");
		labelData.setText(sdf.format(agora));
		updateStatus();
		updateComboBox();
		iniciarNodes();
	}
}