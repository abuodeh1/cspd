package opex.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;

public class SettingTabController {

	private MainController mainController;

	@FXML private TextField omnidocsHost;
	@FXML private TextField omnidocsPort;
	@FXML private TextField cabinet;
	@FXML private TextField rootIndex;
	@FXML private TextField omniUser;
	@FXML private TextField omniUserPassword;
	@FXML private CheckBox deleteFolderIfExist;
	@FXML private TextField dbUrl;
	@FXML private TextField dbUser;
	@FXML private TextField dbPassword;
	@FXML private CheckBox transfer;
	@FXML private TextField transferDest;
	@FXML private Label omnidocsConnectionLabel;
	@FXML private Button testConnection;
	@FXML private Button saveBtn;
	@FXML private Button destBtn;

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
				dbUrl.setText(props.getProperty("db.url"));
				dbPassword.setText(props.getProperty("db.password"));
				dbUser.setText(props.getProperty("db.user"));
				transfer.setSelected(Boolean.valueOf(props.getProperty("omnidocs.transfer")));
				transferDest.setText(props.getProperty("omnidocs.transferDest"));
				
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
			props.setProperty("omnidocs.deleteFolderIfExist", String.valueOf(deleteFolderIfExist.isSelected()) );
			props.setProperty("db.url", dbUrl.getText()==null? "" : dbUrl.getText());
			props.setProperty("db.password", dbPassword.getText()==null? "" : dbPassword.getText());
			props.setProperty("db.user", dbUser.getText()==null? "" : dbUser.getText());
			props.setProperty("omnidocs.transfer", String.valueOf(transfer.isSelected()) );
			props.setProperty("omnidocs.transferDest", transferDest.getText()==null? "" : transferDest.getText());
			
			props.store(outputStream, "CSPD Application Properties");

			outputStream.close();
			
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

	public void injectMainController(MainController mainController) {
		this.mainController = mainController;
	}

	public Properties getApplicationProperties() {
		// TODO Auto-generated method stub
		return props;
	}

}
