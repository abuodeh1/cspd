package opex.controller;

import javafx.fxml.FXML;

import javafx.scene.control.Button;

import javafx.scene.control.TextField;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;

import javafx.event.ActionEvent;

import javafx.scene.control.CheckBox;

public class OmnidocsSettingsController {
	
	private MainController mainController;

	@FXML
	private TextField omnidocsHost;
	@FXML
	private TextField omnidocsPort;
	@FXML
	private TextField rootIndex;
	@FXML
	private TextField cabinet;
	@FXML
	private TextField omniUser;
	@FXML
	private TextField omniUserPassword;
	@FXML
	private CheckBox deleteFolderIfExist;
	@FXML
	private Button saveBtn;
	@FXML
	private TextField omniDBPassword;
	@FXML
	private TextField omniDBUser;
	@FXML
	private TextField omniDBUrl;
	@FXML
	private TextField dcPassport;
	@FXML
	private TextField dcVital;
	@FXML
	private TextField dcEmbassiess;
	@FXML
	private TextField dcCivil;

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
				
				omnidocsHost.setText(props.getProperty("omnidocs.host"));
				omnidocsPort.setText(props.getProperty("omnidocs.port"));
				cabinet.setText(props.getProperty("omnidocs.cabinet"));
				rootIndex.setText(props.getProperty("omnidocs.root"));
				omniUser.setText(props.getProperty("omnidocs.omniUser"));
				omniUserPassword.setText(props.getProperty("omnidocs.omniUserPassword"));
				deleteFolderIfExist.setSelected(Boolean.valueOf(props.getProperty("omnidocs.deleteFolderIfExist")));
				omniDBUrl.setText(props.getProperty("db.omniDBUrl"));
				omniDBPassword.setText(props.getProperty("db.omniDBPassword"));
				omniDBUser.setText(props.getProperty("db.omniDBUser"));
				dcPassport.setText(props.getProperty("db.dcPassport"));
				dcCivil.setText(props.getProperty("db.dcCivil"));
				dcVital.setText(props.getProperty("db.dcVital"));
				dcEmbassiess.setText(props.getProperty("db.dcEmbassiess"));
				
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@FXML
	public void handleSaveSettings(ActionEvent event) {
		try {

			FileInputStream in = new FileInputStream(fileProps);
			props.load(in);
			in.close();

			OutputStream outputStream = new FileOutputStream(fileProps);
			
			props.setProperty("omnidocs.host", omnidocsHost.getText()==null? "" : omnidocsHost.getText());
			props.setProperty("omnidocs.port", omnidocsPort.getText()==null? "" : omnidocsPort.getText());
			props.setProperty("omnidocs.cabinet", cabinet.getText()==null? "" : cabinet.getText());
			props.setProperty("omnidocs.root", rootIndex.getText()==null? "" : rootIndex.getText());
			props.setProperty("omnidocs.omniUser", omniUser.getText()==null? "" : omniUser.getText());
			props.setProperty("omnidocs.omniUserPassword", omniUserPassword.getText()==null? "" : omniUserPassword.getText());
			props.setProperty("omnidocs.deleteFolderIfExist", String.valueOf(deleteFolderIfExist.isSelected()) );
			props.setProperty("db.omniDBUrl", omniDBUrl.getText()==null? "" : omniDBUrl.getText());
			props.setProperty("db.omniDBPassword", omniDBPassword.getText()==null? "" : omniDBPassword.getText());
			props.setProperty("db.omniDBUser", omniDBUser.getText()==null? "" : omniDBUser.getText());
			props.setProperty("omnidocs.dcPassport", dcPassport.getText()==null? "" : dcPassport.getText());
			props.setProperty("omnidocs.dcCivil", dcCivil.getText()==null? "" : dcCivil.getText());
			props.setProperty("omnidocs.dcVital", dcVital.getText()==null? "" : dcVital.getText());
			props.setProperty("omnidocs.dcEmbassiess", dcEmbassiess.getText()==null? "" : dcEmbassiess.getText());
			
			props.store(outputStream, "Omnidocs Properties");

			outputStream.close();
			
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
