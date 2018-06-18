package opex.fx;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.ResourceBundle;

import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
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
	private void handleOpenButton(ActionEvent event){
		fileChooser.getExtensionFilters().addAll(new ExtensionFilter("Opex Scanner XML Files", "*.xml"));
		File file = fileChooser.showOpenDialog(null);
	}
	
	@FXML
	private void handleUploadToOmnidocsButton(ActionEvent event){
		
		try {
			opexModel.uploadDocumentsToOmnidocs();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@FXML
	private void handleOpexFileChooserButton(ActionEvent event){
		fileChooser.getExtensionFilters().addAll(new ExtensionFilter("Opex Scanner XML Files", "*.xml"));
		File file = fileChooser.showOpenDialog(null);
		
		if(file != null) {
			try {
				batch = OpexModel.getResponseAsPOJO(Batch.class, new String(Files.readAllBytes(file.toPath())));
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			opexModel.setBatch(batch);
			openXMLTextField.setText(file.getAbsolutePath());
			
			populateTreeTable();
		}
	}
	
	private void populateTreeTable() {
		
		batch.getTransaction().stream().forEach(transaction -> {
			TreeItem<OpexRowWrapper> transactiontTreeItem = new TreeItem<>(new OpexRowWrapper("Agg. Total For (" + String.valueOf(transaction.getTransactionID()) + ")", String.valueOf(transaction.getGroup().size()), String.valueOf("0"), String.valueOf("0") ));
			transaction.getGroup().forEach(group -> {
				TreeItem<OpexRowWrapper> groupTreeItem = new TreeItem<>(new OpexRowWrapper(String.valueOf(transaction.getTransactionID()), String.valueOf(group.getGroupID()), String.valueOf(group.getPage().size()), String.valueOf("0")));
				group.getPage().forEach(page -> {
					groupTreeItem.getChildren().add( new TreeItem<>( new OpexRowWrapper(String.valueOf(transaction.getTransactionID()), String.valueOf(group.getGroupID()), String.valueOf("1"), String.valueOf(page.getImage().size()) ) ) );
					groupTreeItem.getValue().setNumberOfImages(new SimpleStringProperty( String.valueOf( Integer.valueOf(groupTreeItem.getValue().getNumberOfImages().get()) + page.getImage().size()) ));
					transactiontTreeItem.getValue().setNumberOfImages(new SimpleStringProperty( String.valueOf( Integer.valueOf(transactiontTreeItem.getValue().getNumberOfImages().get()) + page.getImage().size()) ));
				});
				transactiontTreeItem.getValue().setNumberOfPages(new SimpleStringProperty( String.valueOf( Integer.valueOf(transactiontTreeItem.getValue().getNumberOfPages().get()) + group.getPage().size()) ));
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

		public void setTransactionID(SimpleStringProperty transactionID) {
			this.transactionID = transactionID;
		}

		public SimpleStringProperty getGroupID() {
			return groupID;
		}

		public void setGroupID(SimpleStringProperty groupID) {
			this.groupID = groupID;
		}

		public SimpleStringProperty getNumberOfPages() {
			return numberOfPages;
		}

		public void setNumberOfPages(SimpleStringProperty numberOfPages) {
			this.numberOfPages = numberOfPages;
		}

		public SimpleStringProperty getNumberOfImages() {
			return numberOfImages;
		}

		public void setNumberOfImages(SimpleStringProperty numberOfImages) {
			this.numberOfImages = numberOfImages;
		}
	
		public SimpleStringProperty getExistence() {
			return existence;
		}

		public void setExistence(SimpleStringProperty existence) {
			this.existence = existence;
		}
	}
}


