package opex.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cspd.core.GeneralLog;
import etech.omni.OmniService;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class SyncTabController {

	private MainController mainController;

	@FXML
	private ComboBox<String> daysBefore;
	@FXML
	private Button syncButton;

	@FXML TableView<ChangedFolder> changesTable;
	@FXML private TableColumn<ChangedFolder, String> folder;
	@FXML private TableColumn<ChangedFolder, String> action;
	@FXML private TableColumn<ChangedFolder, String> noOfDocuments;
	@FXML private TableColumn<ChangedFolder, String> status;
	
	private List<ChangedFolder> changedFolders;

	@FXML
	public void initialize() {

		folder.setCellValueFactory(new PropertyValueFactory<>("folderName"));
		action.setCellValueFactory(new PropertyValueFactory<>("action"));
		noOfDocuments.setCellValueFactory(new PropertyValueFactory<>("documentName"));
		status.setCellValueFactory(new PropertyValueFactory<>("status"));
		folder.setStyle( "-fx-alignment: CENTER;");
		//noOfDocuments.setStyle( "-fx-alignment: CENTER;");
		action.setStyle( "-fx-alignment: CENTER;");
		status.setStyle( "-fx-alignment: CENTER;");

		
		ObservableList<String> options = FXCollections.observableArrayList("2 Days Before", "4 Days Before", "7 Days Before", "14 Days Before");

		daysBefore.getItems().addAll(options);

		daysBefore.valueProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue ov, String oldValue, String newValue) {
				
				int days = Integer.valueOf((newValue == null ? "0" : newValue.substring(0, newValue.indexOf(' ') + 1)).trim());
				
				ObservableList<ChangedFolder> data = FXCollections.observableArrayList();
				
				changedFolders = getChangesList(days);
						
				data.addAll(changedFolders);
				
				changesTable.setItems(data);
				changesTable.refresh();

			}
		});
	}
	
	
	public List<ChangedFolder> getChangesList(int days) {
		
		ChangedFolder changedFolder = null;
		final List<ChangedFolder> changedFolders = new ArrayList<>();
		try {
			
			Calendar calendar = Calendar.getInstance();
			calendar.add(Calendar.DAY_OF_MONTH, 1);
			String todayDate = new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime());
			calendar.add(Calendar.DAY_OF_MONTH, -(days));
			String fromDate = new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime());
			
			String sqlQuery = null;

			if(mainController.getOmniService().getCabinetUtility().getCabinetinfo().get(0).getDatabaseType().equalsIgnoreCase("oracle")){
				
				sqlQuery = "SELECT C.PARENTFOLDERINDEX, F.NAME, DOCUMENTINDEX, A.SUBSDIARYOBJECTNAME, ACTIVEOBJECTNAME, ACTIONID, A.OLDVALUE FROM PDBNEWAUDITTRAIL_TABLE A, PDBDOCUMENTCONTENT C, PDBFOLDER F " + 
							"WHERE ACTIVEOBJECTTYPE='D'  " + 
							"AND ACTIVEOBJECTID = DOCUMENTINDEX AND C.PARENTFOLDERINDEX = F.FOLDERINDEX  " +
							"AND ACTIONID IN (317, 321) " +
							"AND USERINDEX IN (SELECT USERINDEX FROM PDBGROUPMEMBER WHERE GROUPINDEX = (SELECT GROUPINDEX FROM PDBGROUP WHERE GROUPNAME LIKE 'Quality%')) " +
							"AND DATETIME BETWEEN TO_DATE(?, 'YYYY-MM-DD') AND TO_DATE(?, 'YYYY-MM-DD') AND SUBSDIARYOBJECTID <> -1";
			} else {
				
				sqlQuery = "SELECT C.PARENTFOLDERINDEX, F.NAME, DOCUMENTINDEX, A.SUBSDIARYOBJECTNAME, ACTIVEOBJECTNAME, ACTIONID, A.OLDVALUE FROM PDBNEWAUDITTRAIL_TABLE A, PDBDOCUMENTCONTENT C, PDBFOLDER F " + 
							"WHERE ACTIVEOBJECTTYPE='D'  " + 
							"AND ACTIVEOBJECTID = DOCUMENTINDEX AND C.PARENTFOLDERINDEX = F.FOLDERINDEX  " +
							"AND ACTIONID IN (317, 321) " +
							"AND USERINDEX IN (SELECT USERINDEX FROM PDBGROUPMEMBER WHERE GROUPINDEX = (SELECT GROUPINDEX FROM PDBGROUP WHERE GROUPNAME LIKE 'Quality%')) " +
							"AND DATETIME BETWEEN CONVERT(Date, ?, 111) AND CONVERT(Date, ?, 111) AND SUBSDIARYOBJECTID <> -1";
			}
			
			Connection connection = mainController.getOracleConnectionPoolService().get();
			
			PreparedStatement ps = connection.prepareStatement(sqlQuery);
			ps.setString(1, fromDate);
			ps.setString(2, todayDate);
			
			ResultSet rs = ps.executeQuery();
			
			/*Map<String, ChangedFolder> mappedChangedFolder = new HashMap<>();
			
			while (rs.next()) {			
				
				String folderID = rs.getString("PARENTFOLDERINDEX");
				String folderName = rs.getString("SUBSDIARYOBJECTNAME");
				String documentIndex = rs.getString("DOCUMENTINDEX");
				String documentName = rs.getString("ACTIVEOBJECTNAME");
				int action = rs.getInt("ACTIONID");
				String oldValue = rs.getString("OLDVALUE");
				
				switch(action) {
				
				case 317:
					changedFolder = mappedChangedFolder.get(oldValue.substring(oldValue.indexOf('=')+1).trim());
					break;
				case 321:
					changedFolder = mappedChangedFolder.get(folderID);
					break;
				
				}
				
				if(changedFolder == null) {
					changedFolder = new ChangedFolder();
					changedFolder.setFolderID(folderID);
					changedFolder.setFolderName(folderName);
					changedFolder.setDocumentName(documentName);
					changedFolder.setAction((action==317? "Delete" : "Add"));
					changedFolder.getDocumentActions().add( new DocumentAction(documentIndex, documentName, action) );
					mappedChangedFolder.put(folderID, changedFolder);
				}else {
					changedFolder.getDocumentActions().add( new DocumentAction(documentIndex,documentName, action) );
				}
				
			}

			mappedChangedFolder.entrySet().stream().forEach(mapped -> {
				ChangedFolder f = mapped.getValue();
				f.setNumberOfDocument(f.getDocumentActions().size());
				changedFolders.add(f);
			});*/
			
			while (rs.next()) {			
				
				String folderID = rs.getString("PARENTFOLDERINDEX");
				String folderName = rs.getString("SUBSDIARYOBJECTNAME");
				String documentIndex = rs.getString("DOCUMENTINDEX");
				String documentName = rs.getString("ACTIVEOBJECTNAME");
				int action = rs.getInt("ACTIONID");
				String oldValue = rs.getString("OLDVALUE");
				
				changedFolder = new ChangedFolder();
				changedFolder.setFolderID(folderID);
				changedFolder.setFolderName(folderName);
				changedFolder.setDocumentName(documentName);
				changedFolder.setAction((action==317? "Delete" : "Add"));
				changedFolder.getDocumentActions().add( new DocumentAction(documentIndex, documentName, action) );
				
				changedFolders.add(changedFolder);
			}

			//mainController.writeDBLog(new GeneralLog(processLogID, 3, "INFO", "DATADEFINITION FETCHED UP FROM DATABASE SUCCESSFULY"));
			
		} catch (Exception e) {

			//mainController.writeDBLog(new GeneralLog(processLogID, 3, "ERROR", "UNABLE TO FETCHED UP DATADEFINITION FROM DATABASE"));
			
			e.printStackTrace();

		} 
		
		return changedFolders;

	}

	@FXML
	public void handleSyncBeforeComboBox(ActionEvent event) {
		/*
		 * daysBefore.setOnAction(((Event)ev) -> {
		 * daysBefore.getSelectionModel().getSelectedItem().toString(); });
		 */
	}

	@FXML
	public void handleSyncButton(ActionEvent event) {

		mainController.getLoggerTextArea().clear();
		
		try {
			
			OmniService omniService = mainController.getOmniService();
			
			OpexModel opexModel = new OpexModel(mainController);
			
			Task<Void> task = new Task<Void>() {
				
				@Override
				protected Void call() throws Exception {
					
					syncButton.setDisable(true);
					
					changesTable.getItems().stream().forEach(changedFolder -> {
						try {
							mainController.writeLog("\nSync Process For ( " + changedFolder.getFolderName() + " )");
							
							opexModel.exportDocument(omniService, changedFolder.getFolderName(), changedFolder.getDocumentActions());
							
							mainController.writeLog("Finish Process For ( " + changedFolder.getFolderName() + " )" );
							
							changedFolder.setStatus("Finish Process." );
							
						}catch(Exception e) {
							
							changedFolder.setStatus("Finished with Errors");
							
							e.printStackTrace();
							
							
						}finally {
							
							changesTable.refresh();
						}

						
					});
					
					Thread.sleep(50);
					mainController.writeLog("\nSync Complete...");

					return null;
				}
			};
			
			Thread taskThread = new Thread(task);
			taskThread.start();
			
			task.setOnSucceeded(e -> {
				mainController.msgAlert("The upload task completed.");
				syncButton.setDisable(false);
			});

			task.setOnFailed(e -> syncButton.setDisable(false));

			task.setOnCancelled(e -> syncButton.setDisable(false));

			//opexModel.exportTaskWithSubfolder(omniService, "108", "D:\\temp1");
		    
		} catch (Exception e) {

			mainController.errorAlert("Error Communication ...", e);
			
			e.printStackTrace();
			
		}
		
	}

	public void injectMainController(MainController mainController) {
		this.mainController = mainController;
	}

	class DocumentAction{
		String documentIndex;
		String documentName;
		int action;

		public DocumentAction(String documentIndex, String documentName, int action) {
			this.documentIndex = documentIndex;
			this.documentName = documentName;
			this.action = action;
		}
		public String getDocumentIndex() {
			return documentIndex;
		}

		public void setDocumentIndex(String documentIndex) {
			this.documentIndex = documentIndex;
		}

		public String getDocumentName() {
			return documentName;
		}

		public void setDocumentName(String documentName) {
			this.documentName = documentName;
		}

		public int getAction() {
			return action;
		}

		public void setAction(int action) {
			this.action = action;
		}
		
	}
	
	public class ChangedFolder {

		private SimpleStringProperty folderID = new SimpleStringProperty();
		private SimpleStringProperty folderName = new SimpleStringProperty();
		private List<DocumentAction> documentActions = new ArrayList<>();
		private SimpleIntegerProperty numberOfDocument = new SimpleIntegerProperty();
		private SimpleStringProperty status = new SimpleStringProperty();
		private SimpleBooleanProperty withErrors = new SimpleBooleanProperty(false);
		private SimpleStringProperty action = new SimpleStringProperty();
		private SimpleStringProperty documentName = new SimpleStringProperty();
		
		public String getAction() {
			return action.get();
		}

		public void setAction(String action) {
			this.action.set(action);
		}

		public String getDocumentName() {
			return documentName.get();
		}

		public void setDocumentName(String documentName) {
			this.documentName.set(documentName);
		}

		public String getFolderID() {
			return folderID.get();
		}
		
		public String getFolderName() {
			return folderName.get();
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
		
		public void setFolderName(String folderName) {
			this.folderName.set(folderName);
		}

		public void setNumberOfDocument(int numberOfDocument) {
			this.numberOfDocument.set(numberOfDocument);
		}
		
		public void setStatus(String status) {
			this.status.set(status);
		}

		public List<DocumentAction> getDocumentActions() {
			return documentActions;
		}

		public void setDocumentIDs(List<DocumentAction> documentActions) {
			this.documentActions = documentActions;
		}

		public boolean isWithErrors() {
			return withErrors.get();
		}

		public void setWithErrors(boolean withErrors) {
			this.withErrors.set(withErrors);
		}
		
	}
}
