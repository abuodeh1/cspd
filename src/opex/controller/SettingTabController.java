package opex.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class SettingTabController {

	private MainController mainController;

	@FXML private TextField omnidocsHost;
	@FXML private TextField omnidocsPort;
	@FXML private TextField cabinet;
	@FXML private TextField rootIndex;
	@FXML private TextField omniUser;
	@FXML private TextField omniUserPassword;
	@FXML private TextField dbServer;
	@FXML private TextField dbSID;
	@FXML private TextField dbPort;
	@FXML private Label omnidocsConnectionLabel;
	@FXML private Button testConnection;
	@FXML private Button saveBtn;

	private File fileProps;

	private Properties props;
	
	@FXML private void initialize() {

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
				dbServer.setText(props.getProperty("db.host"));
				dbPort.setText(props.getProperty("db.port"));
				dbSID.setText(props.getProperty("db.sid"));
				
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@FXML
	public void handleSaveSettings() {

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
			props.setProperty("db.host", dbServer.getText()==null? "" : dbServer.getText());
			props.setProperty("db.port", dbPort.getText()==null? "" : dbPort.getText());
			props.setProperty("db.sid", dbSID.getText()==null? "" : dbSID.getText());

			props.store(outputStream, "CSPD Application Properties");

			outputStream.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void injectMainController(MainController mainController) {
		this.mainController = mainController;
	}

	public Properties getApplicationProperties() {
		// TODO Auto-generated method stub
		return props;
	}

}
