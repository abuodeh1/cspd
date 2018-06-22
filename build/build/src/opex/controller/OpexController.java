package opex.controller;

import java.io.File;

import etech.omni.OmniService;
import javafx.beans.property.SimpleStringProperty;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import opex.element.Batch;

public class OpexController {
	
	private MainController mainController;
	
	@FXML private TreeTableColumn<OpexRowWrapper, String> transactionClm;
	@FXML private TreeTableColumn<OpexRowWrapper, String> groupClm;
	@FXML private TreeTableColumn<OpexRowWrapper, String> pagesClm;
	@FXML private TreeTableColumn<OpexRowWrapper, String> imagesClm;
	@FXML private TreeTableColumn<OpexRowWrapper, String> existenceClm;
	@FXML private TreeTableView<OpexRowWrapper> treeTableView; 
	@FXML private TextField openXMLTextField;

	private TreeItem<OpexRowWrapper> root;
	
	private OmniService omniService;
		
	private FileChooser fileChooser;
	
	private Batch batch;
		
	private OpexModel opexModel;

	@FXML private void initialize() {
		
		/*omniService = new OmniService("192.168.60.148", 3333, true);
		try {
			omniService.openCabinetSession("mabuodeh", "etech123", "jlgccab1", false, "S");
		} catch (Exception e) {
			errorAlert("OmniDocs Connection Error.", e);
			e.printStackTrace();
		}*/

		root = new TreeItem<>(new OpexRowWrapper());
		fileChooser = new FileChooser();
		batch = new Batch();
		
		opexModel = new OpexModel(mainController, batch);
		
		transactionClm.setCellValueFactory((TreeTableColumn.CellDataFeatures<OpexRowWrapper, String> param) -> param.getValue().getValue().getTransactionID() );
		groupClm.setCellValueFactory((TreeTableColumn.CellDataFeatures<OpexRowWrapper, String> param) -> param.getValue().getValue().getGroupID() );
		groupClm.setStyle( "-fx-alignment: CENTER;");
		pagesClm.setCellValueFactory((TreeTableColumn.CellDataFeatures<OpexRowWrapper, String> param) -> param.getValue().getValue().getNumberOfPages() );
		pagesClm.setStyle( "-fx-alignment: CENTER;");
		imagesClm.setCellValueFactory((TreeTableColumn.CellDataFeatures<OpexRowWrapper, String> param) -> param.getValue().getValue().getNumberOfImages() );
		imagesClm.setStyle( "-fx-alignment: CENTER;");
		existenceClm.setCellValueFactory((TreeTableColumn.CellDataFeatures<OpexRowWrapper, String> param) -> param.getValue().getValue().getExistence() );
		existenceClm.setStyle( "-fx-alignment: CENTER;");
				
		treeTableView.getColumns().setAll(transactionClm, groupClm, pagesClm, imagesClm, existenceClm);
		treeTableView.setRoot(root);
		treeTableView.setShowRoot(false);
	
	}
	
	public void injectMainController(MainController mainController) {
		this.mainController = mainController;
	}
	
	@FXML
	private void handleUploadToOmnidocsButton(ActionEvent event){
		
		try {
			
			mainController.getLoggerTextArea().appendText("\nUpload Started...");
			opexModel.uploadDocumentsToOmnidocs(omniService, openXMLTextField.getText());
			mainController.getLoggerTextArea().appendText("\nUpload Finished...");
			
			//opexModel.exportTaskWithSubfolder(omniService, "108", "D:\\temp1");
		    
		} catch (Exception e) {
			e.printStackTrace();
			errorAlert("Error Communication ...", e);
		}
	}
	
	@FXML
	private void handleOpexFileChooserButton(ActionEvent event){
		
		fileChooser.getExtensionFilters().addAll(new ExtensionFilter("Opex Scanner XML Files", "*.xml"));
		File file = fileChooser.showOpenDialog(null);
		
		if(file != null) {
			try {
				
				batch = OpexModel.getResponseAsPOJO(Batch.class, file);
			
				opexModel.setBatch(batch);
				openXMLTextField.setText(file.getAbsolutePath());
			
				root.getChildren().clear();
				
				populateTreeTable();
				
			} catch (Exception e) {
				errorAlert("Unable to read the file ...", e);
			}
			
		}
	}
	
	private void populateTreeTable() throws Exception{
		
		batch.getTransaction().stream().forEach(transaction -> {
			TreeItem<OpexRowWrapper> transactiontTreeItem = new TreeItem<>(new OpexRowWrapper("Agg. Total For (" + String.valueOf(transaction.getTransactionID()) + ")", String.valueOf(transaction.getGroup().size()), String.valueOf("0"), String.valueOf("0") ));
			transaction.getGroup().forEach(group -> {
				TreeItem<OpexRowWrapper> groupTreeItem = new TreeItem<>(new OpexRowWrapper(String.valueOf(transaction.getTransactionID()), String.valueOf(group.getGroupID()), String.valueOf(group.getPage().size()), String.valueOf("0")));
				
				Task<Void> task = new Task<Void>() {

					@Override
					protected Void call() throws Exception {
						try {
							groupTreeItem.getValue().setExistence( String.valueOf( opexModel.existance(omniService, String.valueOf(group.getGroupID()))? "Added Before": "New") );
						} catch (Exception e) {
							groupTreeItem.getValue().setExistence(e.getLocalizedMessage());
						}
						return null;
					}
				};

				Thread thread = new Thread(task);
				thread.start();
				
				group.getPage().forEach(page -> {
					groupTreeItem.getChildren().add( new TreeItem<>( new OpexRowWrapper(String.valueOf(transaction.getTransactionID()), String.valueOf(group.getGroupID()), String.valueOf("1"), String.valueOf(page.getImage().size()) ) ) );
					groupTreeItem.getValue().setNumberOfImages(String.valueOf( Integer.valueOf(groupTreeItem.getValue().getNumberOfImages().get()) + page.getImage().size()) );
					transactiontTreeItem.getValue().setNumberOfImages(String.valueOf( Integer.valueOf(transactiontTreeItem.getValue().getNumberOfImages().get()) + page.getImage().size()) );
				});
				
				transactiontTreeItem.getValue().setNumberOfPages(String.valueOf( Integer.valueOf(transactiontTreeItem.getValue().getNumberOfPages().get()) + group.getPage().size()) );
				try {
					thread.join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				transactiontTreeItem.getChildren().add(groupTreeItem);
			});			
			root.getChildren().add(transactiontTreeItem);
		});
				
	}
	
	private void errorAlert(String headerText, Exception e) {

		if (e != null) {
			
		    Alert alert = new Alert(AlertType.ERROR);
		    alert.setTitle("Error");
		    alert.setHeaderText(headerText);
		    alert.setContentText(e.getLocalizedMessage());
		    alert.showAndWait();
		}

	}

	
	class OpexRowWrapper{
		
		SimpleStringProperty transactionID;
		
		SimpleStringProperty groupID;
		
		SimpleStringProperty numberOfPages;
		
		SimpleStringProperty numberOfImages;

		SimpleStringProperty existence;
		
		public OpexRowWrapper() {}
		
		public OpexRowWrapper(String transactionID, String groupID, String numberOfPages, String numberOfImages) {
			this.transactionID = new SimpleStringProperty(transactionID);
			this.groupID = new SimpleStringProperty(groupID);
			this.numberOfPages = new SimpleStringProperty(numberOfPages);
			this.numberOfImages = new SimpleStringProperty(numberOfImages);
			this.existence = new SimpleStringProperty("");
		}
		
		public SimpleStringProperty getTransactionID() {
			return transactionID;
		}

		public void setTransactionID(String transactionID) {
			this.transactionID = new SimpleStringProperty(transactionID);
		}

		public SimpleStringProperty getGroupID() {
			return groupID;
		}

		public void setGroupID(String groupID) {
			this.groupID = new SimpleStringProperty(groupID);
		}

		public SimpleStringProperty getNumberOfPages() {
			return numberOfPages;
		}

		public void setNumberOfPages(String numberOfPages) {
			this.numberOfPages = new SimpleStringProperty(numberOfPages);
		}

		public SimpleStringProperty getNumberOfImages() {
			return numberOfImages;
		}

		public void setNumberOfImages(String numberOfImages) {
			this.numberOfImages = new SimpleStringProperty(numberOfImages);
		}
	
		public SimpleStringProperty getExistence() {
			return existence;
		}

		public void setExistence(String existence) {
			this.existence = new SimpleStringProperty(existence);
		}
	}
}


