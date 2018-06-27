package opex.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.Alert.AlertType;

public class MainController {

	@FXML private OpexController opexTabController;
	@FXML private LoggerController loggerTabController;
	@FXML private OpexDirectoryTabController opexDirectoryTabController;
	
	@FXML public void initialize() {
		
		opexTabController.injectMainController(this);
		
		loggerTabController.injectMainController(this);
		
		opexDirectoryTabController.injectMainController(this);
		
	}
	
	public TextArea getLoggerTextArea() {
		
		return loggerTabController.getLoggerTextArea();
		
	}
	
	@FXML
	private void handleQuitMenuItem(ActionEvent event){
		
		Platform.exit();
		
		System.exit(0);
		
	}
	
	public void msgAlert(String headerText) {

		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Information");
		alert.setHeaderText(headerText);
		alert.showAndWait();

	}
	
	public void errorAlert(String headerText, Exception e) {

		if (e != null) {
			
		    Alert alert = new Alert(AlertType.ERROR);
		    alert.setTitle("Error");
		    alert.setHeaderText(headerText);
		    alert.setContentText(e.getLocalizedMessage());
		    alert.showAndWait();
		}

	}
	
}


