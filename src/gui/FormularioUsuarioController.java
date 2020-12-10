package gui;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import db.DbException;
import gui.util.Alertas;
import gui.util.Restricoes;
import gui.util.Utilidades;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import modelo.entidades.Login;
import modelo.servicos.ServicoLogin;

public class FormularioUsuarioController implements Initializable {

	@FXML
	private TextField txtUsuario;

	@FXML
	private PasswordField txtSenha;

	@FXML
	private PasswordField txtSenha2;

	@FXML
	private Label labelErro;

	@FXML
	private Button btCancelar;

	@FXML
	private Button btSalvar;

	private ServicoLogin servicoLogin;

	public void setServicoLogin(ServicoLogin servicoLogin) {
		this.servicoLogin = servicoLogin;
	}

	@FXML
	public void onBtCancelarAction(ActionEvent event) {
		Utilidades.currentStage(event).close();
	}

	@FXML
	public void onBtSalvarAction(ActionEvent event) {
		try {

			if (txtUsuario.getText() == null || txtUsuario.getText().trim().equals("")
					|| txtSenha.getText() == null
					|| txtSenha.getText().trim().equals("") || txtSenha2.getText() == null
					|| txtSenha2.getText().trim().equals("")) {
				labelErro.setText("Os campos não podem ser vazios.");
			} else if (!txtSenha.getText().equals(txtSenha2.getText())) {
				labelErro.setText("As senhas não coincidem.");
			} else {
				Login obj = new Login();
				obj.setId(null);
				obj.setUsuario(txtUsuario.getText());
				obj.setSenha(txtSenha.getText());
				servicoLogin.salvar(obj);
				Optional<ButtonType> result = Alertas.mostrarConfirmacaoGenerica("Sucesso!",
						"Usuário cadastrado com sucesso.", AlertType.INFORMATION);
				if (result.get() == ButtonType.OK) {
					Utilidades.currentStage(event).close();
					;
				}
			}
		} catch (DbException e) {
			Alertas.mostrarAlerta("Erro ao salvar a tarefa!", null, e.getMessage(), AlertType.ERROR);
		}
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		Restricoes.setTextFieldMaxLength(txtUsuario, 12);
		Restricoes.setTextFieldMaxLength(txtSenha, 8);
		Restricoes.setTextFieldMaxLength(txtSenha2, 8);
	}

}
