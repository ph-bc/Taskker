package gui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import aplicacao.Main;
import gui.util.Restricoes;
import gui.util.Utilidades;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;
import modelo.entidades.Login;
import modelo.servicos.ServicoLogin;

public class LoginController implements Initializable {

	public static Login login;

	@FXML
	private TextField txtUsuario;

	@FXML
	private PasswordField txtSenha;

	@FXML
	private Label labelErro;

	@FXML
	private Button btCadastro;

	@FXML
	private Button btSair;

	@FXML
	private Button btEntrar;

	private ServicoLogin servicoLogin = new ServicoLogin();

	@FXML
	public void onBtCadastroAction(ActionEvent event) {
		try {
			Stage parentStage = Utilidades.currentStage(event);

			FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/FormularioUsuario.fxml"));
			Pane pane = loader.load();

			FormularioUsuarioController controller = loader.getController();
			controller.setServicoLogin(new ServicoLogin());

			Stage stageDialogo = new Stage();
			stageDialogo.setTitle("Cadastro de usuário");
			stageDialogo.getIcons().add(Main.icone);
			stageDialogo.setScene(new Scene(pane));
			stageDialogo.setResizable(false);
			stageDialogo.initOwner(parentStage);
			stageDialogo.initModality(Modality.WINDOW_MODAL);
			stageDialogo.showAndWait();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@FXML
	public void onBtSairAction() {
		System.exit(0);
	}

	@FXML
	public void onBtEntrarAction(ActionEvent event) {
		String usuario = txtUsuario.getText();
		String senha = txtSenha.getText();
		
		login = servicoLogin.findUser(usuario, senha);
		
		if (txtUsuario.getText() == null || txtUsuario.getText().trim().equals("")
				|| txtSenha.getText() == null
				|| txtSenha.getText().trim().equals("")) {
			labelErro.setText("Os campos não podem ser vazios.");
		} else if (login == null) {
			labelErro.setTextAlignment(TextAlignment.CENTER);
			labelErro.setText("Usuário não cadastrado ou dados digitados incorretamente.");
		} else {
			Utilidades.currentStage(event).close();
			main();
		}

	}

	private void main() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/Main.fxml"));
			AnchorPane mainPane = loader.load();

			MainController controller = loader.getController();
			controller.updateComboBox();

			Stage mainStage = new Stage();
			mainStage.setTitle("Taskker");
			mainStage.getIcons().add(Main.icone);
			mainStage.setResizable(false);
			mainStage.setScene(new Scene(mainPane));
			mainStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		Restricoes.setTextFieldMaxLength(txtUsuario, 12);
		Restricoes.setTextFieldMaxLength(txtSenha, 8);
	}

}
