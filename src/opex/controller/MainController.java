package opex.controller;

import java.util.Properties;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextArea;

public class MainController {

	@FXML private LoggerController loggerTabController;
	@FXML private OpexDirectoryTabController opexDirectoryTabController;
	@FXML private SettingTabController settingTabController;
	
	private Properties props;
	
	@FXML public void initialize() {
		
		loggerTabController.injectMainController(this);
		
		opexDirectoryTabController.injectMainController(this);
		
		settingTabController.injectMainController(this);
		
	}
	
	public TextArea getLoggerTextArea() {
		
		return loggerTabController.getLoggerTextArea();
		
	}
	
	public Properties getApplicationProperties() {
		
		return settingTabController.getApplicationProperties();
		
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


