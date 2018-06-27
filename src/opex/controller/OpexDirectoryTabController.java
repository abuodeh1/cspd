package opex.controller;

import java.io.File;
import java.io.FileFilter;

import etech.omni.OmniService;
import etech.omni.core.DataDefinition;
import etech.omni.core.Folder;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.DirectoryChooser;
import opex.element.Batch;

public class OpexDirectoryTabController {
	
	private MainController mainController;
	
	@FXML private TextField openDirectoryTextField;
	private File batchDirectory = null;
	
	@FXML private TableView<OpexFolder> opexTable;
	@FXML private TableColumn<OpexFolder, String> folder;
	@FXML private TableColumn<OpexFolder, String> noOfDocuments;
	@FXML private TableColumn<OpexFolder, String> status;
	@FXML private Button uploadToOmnidocsButton;
	
	private OpexModel opexModel;
	
	private OmniService omniService;
	
	@FXML public void initialize() {
		folder.setCellValueFactory(new PropertyValueFactory<>("folderID"));
		noOfDocuments.setCellValueFactory(new PropertyValueFactory<>("numberOfDocument"));
		status.setCellValueFactory(new PropertyValueFactory<>("status"));
		folder.setStyle( "-fx-alignment: CENTER;");
		noOfDocuments.setStyle( "-fx-alignment: CENTER;");
		status.setStyle( "-fx-alignment: CENTER;");

	}
	
	private void populateOpexTable() {
		
		File files[] = batchDirectory.listFiles(new FileFilter() {
			
			public boolean accept(File file) {
			    if (file.isDirectory()) {
			      return true;
			    }
			    
			    return false;
			}
		});
				
		ObservableList<OpexFolder>  data = FXCollections.observableArrayList();
		
		for(int index = 0; index < files.length; index++) {

			File file = files[index];
	
			data.add( new OpexFolder(file.getName(), file.listFiles().length-1, "") );
		}

		opexTable.setItems(data);
	}
	
	@FXML
	private void handleUploadToOmnidocsButton(ActionEvent event){
		
		try {
			
			omniService = new OmniService("192.168.60.148", 3333, true);
			
			omniService.openCabinetSession("mabuodeh", "etech123", "jlgccab1", false, "S");
			
			String rootFolder = "108";
			
			opexModel = new OpexModel(mainController);
			
			Task<Void> task = new Task<Void>() {
				
				@Override
				protected Void call() throws Exception {
					
					mainController.getLoggerTextArea().appendText("\nUpload Started...");
					uploadToOmnidocsButton.setDisable(true);
					File[] files = batchDirectory.listFiles(new FileFilter() {
						
						@Override
						public boolean accept(File file) {
						    if (file.isDirectory()) {
						      return true;
						    }
						    
						    return false;
						}
					});
					
					for(int index = 0; index < files.length; index++) {

						try {
							opexModel.uploadFolder(omniService, rootFolder, files[index]);
						}catch(Exception e) {}

					}
					Thread.sleep(50);
					mainController.getLoggerTextArea().appendText("\nUpload Finished...");

					return null;
				}
			};
			
			Thread taskThread = new Thread(task);
			taskThread.start();
			
			task.setOnSucceeded(e -> {
				mainController.msgAlert("The upload task completed.");
				uploadToOmnidocsButton.setDisable(false);
			});

			task.setOnFailed(e -> uploadToOmnidocsButton.setDisable(false));

			task.setOnCancelled(e -> uploadToOmnidocsButton.setDisable(false));

			
			
			//opexModel.exportTaskWithSubfolder(omniService, "108", "D:\\temp1");
		    
		} catch (Exception e) {

			mainController.errorAlert("Error Communication ...", e);
			
			e.printStackTrace();
		}
	}
	
	@FXML
	public void handleOpexDirectoryChooserButton(ActionEvent event) {
		
		DirectoryChooser directoryChooser = new DirectoryChooser();
		directoryChooser.setTitle("Choose Opex Batch Directory");
		
		batchDirectory = directoryChooser.showDialog(null);
		openDirectoryTextField.setText(batchDirectory.getAbsolutePath());
		
		populateOpexTable();
	}

	public class OpexFolder {

		private SimpleStringProperty folderID;
		private SimpleIntegerProperty numberOfDocument;
		private SimpleStringProperty status;
		
		public OpexFolder(String folderID, int numberOfDocument, String status) {
			this.folderID = new SimpleStringProperty(folderID);
			this.numberOfDocument = new SimpleIntegerProperty(numberOfDocument);
			this.status = new SimpleStringProperty(status);
		}

		public String getFolderID() {
			return folderID.get();
		}

		public int getNumberOfDocument() {
			return numberOfDocument.get();
		}

		public String getStatus() {
			return status.get();
		}

		public void setFolderID(String folderID) {
			this.folderID.set(folderID);
		}

		public void setNumberOfDocument(int numberOfDocument) {
			this.numberOfDocument.set(numberOfDocument);
		}

		public void setStatus(String status) {
			this.status.set(status);
		}

	}

	public void injectMainController(MainController mainController) {
		this.mainController = mainController;
	}
	

}
