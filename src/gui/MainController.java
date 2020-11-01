package gui;

import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import gui.util.Utilidades;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;
import modelo.entidades.SubTarefa;
import modelo.entidades.Tarefa;
import modelo.enums.Situacao;
import modelo.servicos.ServicoSubTarefa;
import modelo.servicos.ServicoTarefa;

public class MainController implements Initializable {

	@FXML
	private Button btLogout;
	
	@FXML
	private Button btSobre;
	
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
	private TableColumn<SubTarefa, Integer> tableColumnCod;
	
	@FXML
	private TableColumn<SubTarefa, String> tableColumnNome;
	
	@FXML
	private TableColumn<SubTarefa, Date> tableColumnPrazo;
	
	@FXML
	private TableColumn<SubTarefa, Situacao> tableColumnStatus;
	
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
		Tarefa tarefa = comboBoxTarefa.getSelectionModel().getSelectedItem();
		updateTableView(tarefa);
	}
	
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		setServicoSubTarefa(new ServicoSubTarefa());
		setServicoTarefa(new ServicoTarefa());
		
		updateComboBox();
		iniciarComboBoxTarefa();
		iniciarNodes();
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
	
	private void iniciarNodes() {
		tableColumnCod.setCellValueFactory(new PropertyValueFactory<>("cod"));
		tableColumnNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
		tableColumnPrazo.setCellValueFactory(new PropertyValueFactory<>("dataPrazo"));
		tableColumnStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
		Utilidades.formatTableColumnDate(tableColumnPrazo, "dd/MM/yyyy");
	}
	
	public void updateTableView(Tarefa tarefa) {
		List<SubTarefa> list = servicoSubTarefa.findByTarefa(tarefa);
		obsListSubTarefa = FXCollections.observableArrayList(list);
		tableViewSubTarefa.setItems(obsListSubTarefa);
	}
	
	public void updateComboBox() {
		List<Tarefa> list = servicoTarefa.findAll();
		obsListTarefa = FXCollections.observableArrayList(list);
		comboBoxTarefa.setItems(obsListTarefa);
	}
}