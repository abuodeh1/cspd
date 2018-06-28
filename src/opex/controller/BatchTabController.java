package opex.controller;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;

public class BatchTabController {
	
	@FXML private TextField openDirectoryTextField;
	private File batchDirectory = null;
	
	@FXML
	private Button uploadToOmnidocsButton;

	@FXML private TextArea loggerTextArea;
	
	public TextArea getLoggerTextArea() {
		
		return loggerTextArea;
	}
	
	
	@FXML public void handleSaveLogButton(ActionEvent ae) {
		
		DirectoryChooser directoryChooser = new DirectoryChooser();
		File file = directoryChooser.showDialog(null);

		if(file != null) {
			
			try(FileWriter fileLog = new FileWriter(file+"\\log.txt", true)){
				fileLog.write(loggerTextArea.getText());
			} catch (IOException e) {
				
			}
			
		}
	}
	
	@FXML
	public void handleOpexDirectoryChooserButton(ActionEvent event) {
		
		DirectoryChooser directoryChooser = new DirectoryChooser();
		directoryChooser.setTitle("Choose Opex Batch Directory");
		
		batchDirectory = directoryChooser.showDialog(null);
		openDirectoryTextField.setText(batchDirectory.getAbsolutePath());

	}

	@FXML
	public void handleUploadToOmnidocsButton(ActionEvent event) {
	}
}
