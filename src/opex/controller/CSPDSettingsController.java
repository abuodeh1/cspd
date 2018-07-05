package opex.controller;

import javafx.fxml.FXML;

import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;

import javafx.event.ActionEvent;

public class CSPDSettingsController {
	
	private MainController mainController;

	@FXML
	private Button testConnection;
	@FXML
	private Button saveBtn;
	@FXML
	private TextField dbUrl;
	@FXML
	private TextField dbUser;
	@FXML
	private TextField dbPassword;
	@FXML private CheckBox transfer;
	@FXML private TextField transferDest;
	
	@FXML private TextField opexPassport;
	@FXML private TextField opexCivil;
	@FXML private TextField opexVital;
	@FXML private TextField opexEmbassies;
	
	private File fileProps;

	private Properties props;
	
	@FXML public void initialize() {

		fileProps = new File("application.properties");
		
		try {
			if (!fileProps.exists()) {

				fileProps.createNewFile();

			} else {
				
				FileInputStream in = new FileInputStream(fileProps);
				props = new Properties();
				props.load(in);
				in.close();
				
				dbUrl.setText(props.getProperty("db.url"));
				dbPassword.setText(props.getProperty("db.password"));
				dbUser.setText(props.getProperty("db.user"));
				
				opexPassport.setText(props.getProperty("opex.passport"));
				opexCivil.setText(props.getProperty("opex.civil"));
				opexVital.setText(props.getProperty("opex.vital"));
				opexEmbassies.setText(props.getProperty("opex.embassies"));
				
				transfer.setSelected(Boolean.valueOf(props.getProperty("omnidocs.transfer")));
				transferDest.setText(props.getProperty("omnidocs.transferDest"));
				
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@FXML
	public void handleDestinationChooser() {
		
		DirectoryChooser directoryChooser = new DirectoryChooser();
		directoryChooser.setTitle("Choose Opex Batch Directory");
		
		File folderDest = directoryChooser.showDialog(null);
		transferDest.setText(folderDest.getAbsolutePath());
	}


	@FXML
	public void handleSaveSettings(ActionEvent event) {
		try {

			FileInputStream in = new FileInputStream(fileProps);
			props.load(in);
			in.close();

			OutputStream outputStream = new FileOutputStream(fileProps);
			
			props.setProperty("db.url", dbUrl.getText()==null? "" : dbUrl.getText());
			props.setProperty("db.password", dbPassword.getText()==null? "" : dbPassword.getText());
			props.setProperty("db.user", dbUser.getText()==null? "" : dbUser.getText());
			
			props.setProperty("opex.passport", opexPassport.getText()==null? "" : opexPassport.getText());
			props.setProperty("opex.civil", opexCivil.getText()==null? "" : opexCivil.getText());
			props.setProperty("opex.vital", opexVital.getText()==null? "" : opexVital.getText());
			props.setProperty("opex.embassies", opexEmbassies.getText()==null? "" : opexEmbassies.getText());
			
			props.setProperty("omnidocs.transfer", String.valueOf(transfer.isSelected()) );
			props.setProperty("omnidocs.transferDest", transferDest.getText()==null? "" : transferDest.getText());
			
			props.store(outputStream, "CSPD Properties");

			outputStream.close();
			
			if(props.getProperty("db.url").contains("oracle")) {
				mainController.buildOmniConnection();	
			}else {
				
				mainController.buildCSPDConnection();
			}
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public Properties getApplicationProperties() {
		return props;
	}
	
	public void injectMainController(MainController mainController) {
		this.mainController = mainController;
	}

}
