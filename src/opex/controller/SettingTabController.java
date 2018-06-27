package opex.controller;

import javafx.fxml.FXML;

import javafx.scene.control.Button;

import javafx.scene.control.TextField;

import javafx.scene.control.Label;

public class SettingTabController {
	
	private MainController mainController;
	
	@FXML
	private TextField omnidocsHost;
	@FXML
	private TextField omnidocsPort;
	@FXML
	private TextField dbServer;
	@FXML
	private TextField dbSID;
	@FXML
	private TextField dbPort;
	@FXML
	private Label omnidocsConnectionLabel;
	@FXML
	private Button testConnection;
	@FXML
	private Button saveBtn;

	
	@FXML public void initialize() {}


	public void injectMainController(MainController mainController) {
		this.mainController = mainController;
	}
	
	
}
