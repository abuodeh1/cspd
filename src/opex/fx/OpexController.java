package opex.fx;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import opex.element.Batch;

public class OpexController implements Initializable {
	
	@FXML private TreeTableColumn<OpexRowWrapper, String> transactionClm;
	@FXML private TreeTableColumn<OpexRowWrapper, String> groupClm;
	@FXML private TreeTableColumn<OpexRowWrapper, String> pagesClm;
	@FXML private TreeTableColumn<OpexRowWrapper, String> imagesClm;
	@FXML private TreeTableColumn<OpexRowWrapper, String> existenceClm;
	@FXML private TreeTableView<OpexRowWrapper> treeTableView; 
	@FXML private TextField openXMLTextField;

	private TreeItem<OpexRowWrapper> root;
		
	private FileChooser fileChooser;
	
	private Batch batch;
	
	private OpexModel opexModel;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		root = new TreeItem<>(new OpexRowWrapper());
		fileChooser = new FileChooser();
		batch = new Batch();
		
		opexModel = new OpexModel(batch);
		
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
	
	@FXML
	private void handleOpenMenuItem(ActionEvent event){
//		fileChooser.getExtensionFilters().addAll(new ExtensionFilter("Opex Scanner XML Files", "*.xml"));
//		File file = fileChooser.showOpenDialog(null);
	}
	
	@FXML
	private void handleQuitMenuItem(ActionEvent event){
		Platform.exit();
		System.exit(0);
	}
	
	@FXML
	private void handleUploadToOmnidocsButton(ActionEvent event){
		
		try {
			
			opexModel.uploadDocumentsToOmnidocs(openXMLTextField.getText());
		    
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
				Thread thread = new Thread(new Runnable() {
					
					@Override
					public void run() {
						try {
							groupTreeItem.getValue().setExistence( String.valueOf( opexModel.existance(String.valueOf(group.getGroupID()))? "Added Before": "New") );
						} catch (Exception e) {
							groupTreeItem.getValue().setExistence(e.getLocalizedMessage());
						}
					}
				});
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
					// TODO Auto-generated catch block
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


