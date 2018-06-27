package opex.controller;

import java.io.File;
import java.io.FileFilter;

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.DirectoryChooser;
import javafx.util.Callback;

public class OpexDirectoryTabController {
	
	private MainController mainController;
	
	@FXML private TextField openDirectoryTextField;
	private File batchDirectory = null;
	
	@FXML private TableView<OpexFolder> opexTable;
	@FXML private TableColumn<OpexFolder, String> folder;
	@FXML private TableColumn<OpexFolder, String> noOfDocuments;
	@FXML private TableColumn<OpexFolder, String> inOmnidocs;
	@FXML private TableColumn<OpexFolder, String> toDocuware;
	@FXML private TableColumn<OpexFolder, String> status;
	@FXML private TableColumn<OpexFolder, String> inQueue;
	
	@FXML public void initialize() {
		folder.setCellValueFactory(new PropertyValueFactory<>("folderID"));
		//folder.setCellValueFactory( (CellDataFeatures<OpexFolder, String> param) -> param.getValue().folderID );
		noOfDocuments.setCellValueFactory(new PropertyValueFactory<>("numberOfDocument"));
		inOmnidocs.setCellValueFactory(new PropertyValueFactory<>("inOmnidocs"));
		toDocuware.setCellValueFactory(new PropertyValueFactory<>("toDocuware"));
		status.setCellValueFactory(new PropertyValueFactory<>("status"));
		inQueue.setCellValueFactory(new PropertyValueFactory<>("DUMMY"));

		Callback<TableColumn<OpexFolder, String>, TableCell<OpexFolder, String>> inQueueCellFactory = 
				new Callback<TableColumn<OpexFolder, String>, TableCell<OpexFolder, String>>() {
					@Override
					public TableCell<OpexFolder, String> call(final TableColumn<OpexFolder, String> param) {
						final TableCell<OpexFolder, String> cell = new TableCell<OpexFolder, String>() {

							final ToggleButton btn = new ToggleButton("InQueue");

							// anonymous constructor:
				            {
				                btn.selectedProperty().addListener((obs, wasSelected, isNowSelected) -> {
				                    if (isNowSelected) {
				                    	getTableView().getItems().get(getIndex()).setInQueue(true);
				                    } else {
				                    	if(getTableView().getItems().get(getIndex()).isInProgress()) {
				                    		mainController.errorAlert("The folder in process..", new Exception("Unable to dequeue the folder."));
				                    		btn.setSelected(true);
				                    	}else {
				                    		getTableView().getItems().get(getIndex()).setInQueue(false);
				                    	}
				                    }
				                });

				                btn.textProperty().bind(Bindings.when(btn.selectedProperty()).then("DeQueue").otherwise("InQueue"));
				                setAlignment(Pos.CENTER);
				            }
				            
							@Override
							public void updateItem(String item, boolean empty) {
								super.updateItem(item, empty);
								if (empty) {
									setGraphic(null);
									setText(null);
								} else {
									btn.setOnAction(event -> {
										OpexFolder opexFolder = getTableView().getItems().get(getIndex());
										System.out.println(opexFolder.getFolderID() + "\t" + opexFolder.getNumberOfDocument()  + "\t" + opexFolder.isInQueue());
									});
	
									setGraphic(btn);
									setText(null);
								}
							}
						};
						return cell;
					}
				};

		inQueue.setCellFactory(inQueueCellFactory);

		noOfDocuments.setStyle( "-fx-alignment: CENTER;");
		inOmnidocs.setStyle( "-fx-alignment: CENTER;");
		toDocuware.setStyle( "-fx-alignment: CENTER;");
		status.setStyle( "-fx-alignment: CENTER;");
		inQueue.setStyle( "-fx-alignment: CENTER;");
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

			// From database
			String isInOmnidocs = "";
			
			// From database
			String isToDocuware = "";
			
			data.add( new OpexFolder(file.getName(), file.listFiles().length-1, isInOmnidocs, isToDocuware, "", false) );
		}

		opexTable.setItems(data);
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
		private SimpleStringProperty inOmnidocs;
		private SimpleStringProperty toDocuware;
		private SimpleStringProperty status;
		private SimpleBooleanProperty inProgress;
		private boolean inQueue = false;
		
		public OpexFolder(String folderID, int numberOfDocument, String inOmnidocs, String toDocuware, String status, boolean inProgress) {
			this.folderID = new SimpleStringProperty(folderID);
			this.numberOfDocument = new SimpleIntegerProperty(numberOfDocument);
			this.inOmnidocs = new SimpleStringProperty(inOmnidocs);
			this.toDocuware = new SimpleStringProperty(toDocuware);
			this.status = new SimpleStringProperty(status);
			this.inProgress = new SimpleBooleanProperty(inProgress);
		}

		public String getFolderID() {
			return folderID.get();
		}

		public int getNumberOfDocument() {
			return numberOfDocument.get();
		}

		public String getInOmnidocs() {
			return inOmnidocs.get();
		}

		public String getToDocuware() {
			return toDocuware.get();
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

		public void setInOmnidocs(String inOmnidocs) {
			this.inOmnidocs.set(inOmnidocs);
		}

		public void setToDocuware(String toDocuware) {
			this.toDocuware.set(toDocuware);
		}

		public void setStatus(String status) {
			this.status.set(status);
		}

		public boolean isInProgress() {
			return inProgress.get();
		}

		public void setInProgress(boolean inProgress) {
			this.inProgress.set(inProgress);
		}

		public boolean isInQueue() {
			return inQueue;
		}

		public void setInQueue(boolean inQueue) {
			this.inQueue = inQueue;
		}	

	}

	public void injectMainController(MainController mainController) {
		this.mainController = mainController;
	}
	

}
