package opex.controller;

import java.io.File;

import etech.omni.OmniService;
import javafx.beans.property.SimpleStringProperty;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
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
	@FXML private Button uploadToOmnidocsButton;

	private TreeItem<OpexRowWrapper> root;
	
	private OmniService omniService;
		
	private FileChooser fileChooser;
	
	private Batch batch;
	
	private OpexModel opexModel;
	
	private File opexFile;

	@FXML private void initialize() {
		
		omniService = new OmniService("192.168.60.148", 3333, true);
		try {
			omniService.openCabinetSession("mabuodeh", "etech123", "jlgccab1", false, "S");
		} catch (Exception e) {
			mainController.errorAlert("OmniDocs Connection Error.", e);
			e.printStackTrace();
		}

		root = new TreeItem<>(new OpexRowWrapper());
		fileChooser = new FileChooser();
		batch = new Batch();
		opexModel = new OpexModel();
		
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
		opexModel.injectMainController(mainController);
	}
	
	@FXML
	private void handleUploadToOmnidocsButton(ActionEvent event){
		
		try {
			
			Task<Void> task = new Task<Void>() {
				
				@Override
				protected Void call() throws Exception {
					
					mainController.getLoggerTextArea().appendText("\nUpload Started...");
					uploadToOmnidocsButton.setDisable(true);
					opexModel.uploadDocumentsToOmnidocs(omniService, openXMLTextField.getText());
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
			//e.printStackTrace();
			mainController.errorAlert("Error Communication ...", e);
		}
	}
	
	@FXML
	private void handleOpexFileChooserButton(ActionEvent event){
		
		fileChooser.getExtensionFilters().addAll(new ExtensionFilter("Opex Scanner XML Files", "*.xml"));
		opexFile = fileChooser.showOpenDialog(null);
		
		if(opexFile != null) {
			try {
				
				batch = OpexModel.getResponseAsPOJO(Batch.class, opexFile);
			
				opexModel.setBatch(batch);
				openXMLTextField.setText(opexFile.getAbsolutePath());
			
				root.getChildren().clear();
				
				populateTreeTable();
				
			} catch (Exception e) {
				mainController.errorAlert("Unable to read the file ...", e);
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


