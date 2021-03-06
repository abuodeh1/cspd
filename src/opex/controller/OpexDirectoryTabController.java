package opex.controller;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

import cspd.core.OpexFolderReport;
import etech.omni.OmniService;
import javafx.beans.property.SimpleBooleanProperty;
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

public class OpexDirectoryTabController {
	
	private MainController mainController;
	
	@FXML private TextField openDirectoryTextField;
	private File batchDirectory = null;
	
	@FXML private TableView<OpexFolder> opexTable;
	@FXML private TableColumn<OpexFolder, String> folder;
	@FXML private TableColumn<OpexFolder, String> noOfDocuments;
	@FXML private TableColumn<OpexFolder, String> status;
	@FXML private Button uploadToOmnidocsButton;
	
	private OpexFolderReport opexFolderReport = new OpexFolderReport();
	
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
	
			data.add( new OpexFolder(file.getName(), file.getAbsolutePath(), file.listFiles().length, "") );
		}

		opexTable.setItems(data);
	}
	
	@FXML
	private void handleUploadToOmnidocsButton(ActionEvent event){
		
		mainController.getLoggerTextArea().clear();
		
		try {
			
			String rootIndex = mainController.getOmnidocsProperties().getProperty("omnidocs.root");
			if( rootIndex == null || rootIndex.trim().length() == 0 ) {
				
				mainController.errorAlert("Cann't continue without specifying a folder root to upload", new Exception("Please check Omnidocs in setting tab."));
				
				return;
			}
			
			OmniService omniService = mainController.getOmniService();
			
			OpexModel opexModel = new OpexModel(mainController);
			
			List<OpexFolderReport> opexFolderReports = new ArrayList<>();

			Task<Void> task = new Task<Void>() {
				
				@Override
				protected Void call() throws Exception {
					
					uploadToOmnidocsButton.setDisable(true);
					opexTable.getItems().stream().forEach(opexFolder -> {
						
						OpexFolderReport opexFolderReport = new OpexFolderReport();
						
						File folder = new File(opexFolder.getFolderPath());
						
						opexFolderReport.setTotalDocuments(folder.listFiles().length);
						
						
						try {
							mainController.writeLog("\nStart Upload Process For ( " + opexFolder.getFolderID() + " )");
							
							opexModel.uploadFolder(opexFolderReport, omniService, folder);
							
							mainController.writeLog("Finish Process For ( " + opexFolder.getFolderID() + " )\n" );
							
							opexFolder.setStatus("Finish Process." );
							
						}catch(Exception e) {
							
							opexFolderReport.setFolderName(opexFolder.getFolderID());
							
							opexFolderReport.setFailedReason(e.getMessage());
							
							mainController.getOpexFolderReport().add(opexFolderReport);
							
							mainController.writeLog("Finish Process With Errors");
							
							opexFolder.setStatus("Finished with Errors");
							
							e.printStackTrace();
							
							
						}finally {
							
							opexTable.refresh();
						}

						
					});
					
					//Thread.sleep(50);
					
					//mainController.writeLog("\nUpload Complete...");

					omniService.complete();
					
					//mainController.setOpexFolderReport(opexFolderReports);
					
					return null;
				}
			};
			
			Thread taskThread = new Thread(task);
			taskThread.start();
			//taskThread.join();
			
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
		if(batchDirectory!= null) {
			openDirectoryTextField.setText(batchDirectory.getAbsolutePath());
			populateOpexTable();
		}
		
	}

	public class OpexFolder {

		private SimpleStringProperty folderID;
		private String folderPath;
		private SimpleIntegerProperty numberOfDocument;
		private SimpleStringProperty status;
		private SimpleBooleanProperty withErrors = new SimpleBooleanProperty(false);
		
		public OpexFolder(String folderID, String folderPath, int numberOfDocument, String status) {
			this.folderID = new SimpleStringProperty(folderID);
			this.folderPath = folderPath;
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

		public String getFolderPath() {
			return folderPath;
		}

		public void setFolderPath(String folderPath) {
			this.folderPath = folderPath;
		}

		public boolean isWithErrors() {
			return withErrors.get();
		}

		public void setWithErrors(boolean withErrors) {
			this.withErrors.set(withErrors);
		}
		
	}

	public void injectMainController(MainController mainController) {
		this.mainController = mainController;
	}

	public OpexFolderReport getOpexFolderReport() {
		return opexFolderReport;
	}

	public void setOpexFolderReport(OpexFolderReport opexFolderReport) {
		this.opexFolderReport = opexFolderReport;
	}
	

}
