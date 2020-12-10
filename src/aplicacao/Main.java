package aplicacao;

import java.io.IOException;

import gui.util.Alertas;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Main extends Application {

	public static Image icone = new Image("/images/icone.png");

	@Override
	public void start(Stage primaryStage) {
		try {
			AnchorPane loginPane = new FXMLLoader(getClass().getResource("/gui/Login.fxml")).load();

			primaryStage.setTitle("Taskker");
			primaryStage.getIcons().add(icone);
			primaryStage.initStyle(StageStyle.TRANSPARENT);
			primaryStage.setScene(new Scene(loginPane));
			primaryStage.show();

		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			Alertas.mostrarAlerta("ERRO FATAL!", "COD.: PROGRAM_NOT_SCHEDULED_#0001FB0404", e.getMessage(), AlertType.ERROR);
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}
