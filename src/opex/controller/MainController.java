package opex.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

public class MainController {

	@FXML private OpexController opexTabController;
	@FXML private LoggerController loggerTabController;
	
	@FXML public void initialize() {
		
		System.out.println("Started km f dnfdk");
		
		opexTabController.injectMainController(this);
		
		loggerTabController.injectMainController(this);
		
		
	}
	
	public TextArea getLoggerTextArea() {
		
		return loggerTabController.getLoggerTextArea();
		
	}
	
	@FXML
	private void handleQuitMenuItem(ActionEvent event){
		
		Platform.exit();
		
		System.exit(0);
		
	}
	
	
}


