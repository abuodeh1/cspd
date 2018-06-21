package opex.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

public class LoggerController {
	
	private MainController mainController;
	
	@FXML private TextArea loggerTextArea;
	
	public void injectMainController(MainController mainController) {
		this.mainController = mainController;
	}
	
	public TextArea getLoggerTextArea() {
		
		return loggerTextArea;
	}
	
}


