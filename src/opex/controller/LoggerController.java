package opex.controller;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.stage.DirectoryChooser;

public class LoggerController {
	
	private MainController mainController;
	
	@FXML private TextArea loggerTextArea;
	
	public void injectMainController(MainController mainController) {
		this.mainController = mainController;
	}
	
	public TextArea getLoggerTextArea() {
		
		return loggerTextArea;
	}
	
	
	@FXML public void handleSaveLogButton(ActionEvent ae) {
		
		DirectoryChooser directoryChooser = new DirectoryChooser();
		File file = directoryChooser.showDialog(null);

		if(file != null) {
			
			try(FileWriter fileLog = new FileWriter(file+"\\log.txt", true)){
				fileLog.write(mainController.getLoggerTextArea().getText());
				mainController.msgAlert("the file exported (log.txt).");
			} catch (IOException e) {
				mainController.errorAlert("Unable to write the file.", e);
			}
			
		}
	}
	
	@FXML public void handleReportStatusButton(ActionEvent ae){
		
		mainController.showReport(mainController.getOpexFolderReport());
		
	}
}


