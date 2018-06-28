package opex.controller;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import cspd.BatchDetails;
import etech.dms.exception.DocumentException;
import etech.dms.exception.FolderException;
import etech.omni.OmniService;
import etech.omni.core.DataDefinition;
import etech.omni.core.Document;
import etech.omni.core.Folder;
import etech.omni.helper.NGOHelper;
import etech.omni.utils.OmniDocumentUtility;
import etech.omni.utils.OmniFolderUtility;
import etech.resource.pool.PoolFactory;
import etech.resource.pool.PoolService;
import javafx.concurrent.Task;
import opex.element.Batch;
import opex.element.Batch.Transaction;
import opex.element.Batch.Transaction.Group;
import opex.element.Batch.Transaction.Group.Page;
import opex.element.Batch.Transaction.Group.Page.Image;

public class OpexModel {

	private MainController mainController;
	
	public OpexModel(MainController mainController) {
		this.mainController = mainController;
	}
	
	public void uploadFolder(OmniService omniService, String parentFolderID, File folder) throws Exception {
		
		String folderPath = folder.getAbsolutePath();
		
		String batchOXI = folderPath + folderPath.substring(folderPath.lastIndexOf(System.getProperty("file.separator"))) + ".oxi";
		
		Batch batch = readBatchOXI(batchOXI);
		DataDefinition dataDefinition = prepareDataDefinition(omniService, 1, folder.getName());
		if(isFolderAdded(omniService, parentFolderID, folder.getName())) {
			writeLog("The folder (" + batchOXI + ") is already added");
		}
		Folder addedFolder = addFolder(omniService, parentFolderID, folder.getName(), dataDefinition);
		uploadDocumentsToOmnidocs(omniService, batch, dataDefinition, addedFolder);
		
	}

	private Batch readBatchOXI(String filePath) throws Exception {

		Batch batch = null;
		try {
			batch = NGOHelper.getResponseAsPOJO(Batch.class, new String(Files.readAllBytes(new File(filePath).toPath())));
		}catch(Exception fe) {
			writeLog("Unable to parse " + filePath);
			throw new Exception(fe);
		}
		
		return batch;
		
	}
	
	private boolean isFolderAdded(OmniService omniService, String parentFolderID, String fileName) throws FolderException {
		
		List<Folder> folderRs = null;
		
		try{
			folderRs = omniService.getFolderUtility().findFolderByName(parentFolderID, fileName, false);

		}catch(Exception fe) {
			writeLog("Unable to find " + fileName + " in omnidocs");
			throw new FolderException(fe);
		}
	
		return folderRs.size() > 0;
	}
	
	private Folder addFolder(OmniService omniService, String parentFolderID, String fileName, DataDefinition dataDefinition) throws FolderException {
		
		Folder folder = new Folder();
		folder.setFolderName(fileName);					
		folder.setParentFolderIndex(parentFolderID);
		folder.setDataDefinition(dataDefinition);
		try {
			folder = omniService.getFolderUtility().addFolder(parentFolderID, folder);
		}catch(Exception fe) {
			writeLog("Unable to add " + fileName + " to omnidocs");
			throw new FolderException(fe);
		}

		return folder;
	}

	private void uploadDocumentsToOmnidocs(OmniService omniService, Batch batch, DataDefinition dataDefinition, Folder folder) throws DocumentException {
		
		boolean thereIsError = false;
		
		long startDate = System.currentTimeMillis();
		
		// read opex xml

		String scanFolderPath = batch.getImageFilePath();

		// fetch metadata from database from index per file

		Iterator<Transaction> transactions = batch.getTransaction().iterator();

		while (transactions.hasNext()) {

			Transaction transaction = transactions.next();

			Iterator<Group> groups = transaction.getGroup().iterator();

			while (groups.hasNext()) {

				Group group = groups.next();

				Iterator<Page> pages = group.getPage().iterator();
				
				while (pages.hasNext()) {

					Page page = pages.next();
					
					Iterator<Image> images = page.getImage().iterator();
					
					while (images.hasNext()) {
					
						Image image = images.next();
						
						Document document = new Document();							
						document.setParentFolderIndex(folder.getFolderIndex());							
						document.setDocumentName(image.getFilename());
						
						String imagePath = scanFolderPath + System.getProperty("file.separator") + image.getFilename();
						
						try {
							omniService.getDocumentUtility().addDocument(new File(imagePath), document);
							writeLog(imagePath + " uploaded successfuly.");
						} catch (DocumentException e) {
							thereIsError = true;
							writeLog("Unable to upload " + imagePath);
							e.printStackTrace();
						}
					}

				}
			}
		}
		
		writeLog("Time Completed with: " + TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - startDate)+".");
		
		if(thereIsError) {
			throw new DocumentException("Some Documents Failed.");
		}
	}
	
	private void writeLog(String msg) {
		
		Task task = new Task<Void>() {

			@Override
			protected Void call() throws Exception {
				mainController.getLoggerTextArea().appendText("\n" + msg);
				return null;
			}
		};
		new Thread(task).start();
	}

	public void exportTaskWithoutSubfolder(OmniService omniService, String folderID, String folderDestination) throws Exception {

		long startDate = System.currentTimeMillis();

		List<Folder> folders = new ArrayList<>();
		folders.add( omniService.getFolderUtility().getFolder(folderID) );

		exportTaskFoldersWithDocuments(omniService, folders, folderDestination);
		
		System.out.println("exportTaskWithoutSubfolder Task Completed with: " + TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - startDate) + " s");

	}
	
	public void exportTaskWithSubfolder(OmniService omniService, String folderID, String folderDestination) throws Exception {
		
		long startDate = System.currentTimeMillis();

		List<Folder> folders = omniService.getFolderUtility().getFolderList(folderID, true);
		
		exportTaskFoldersWithDocuments(omniService, folders, folderDestination);
		
		System.out.println("exportTaskWithSubfolder Task Completed with: " + TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - startDate) + " s");

	}	
	
	public void exportTaskFoldersWithDocuments(OmniService omniService, List<Folder> folders, String folderDestination) throws Exception {
	
		try(FileWriter fileLog = new FileWriter(new File(folderDestination).getParent()+"\\export-log.txt", true)) {
			long startDate = System.currentTimeMillis();
	
			OmniFolderUtility folderUtility = omniService.getFolderUtility();
			
			OmniDocumentUtility documentUtility = omniService.getDocumentUtility();
	
			folders.stream().forEach(folder -> {
				try {
					
					List<Document> documents = documentUtility.getDocumentList(folder.getFolderIndex(), false);
					documents.stream().forEach(document -> {
						String nextDest = "";
						String uploadDocumentPath = "";
						try {
							nextDest = folderDestination + System.getProperty("file.separator") + folderUtility.getFolderAncestorAsString(folder.getFolderIndex());
							File file = new File(nextDest);
							
							if(!file.exists()){
								file.mkdirs();
								fileLog.write("\n" + nextDest + " Created.");
							}
							
							uploadDocumentPath = nextDest + System.getProperty("file.separator") + document.getDocumentName();
							documentUtility.exportDocument(uploadDocumentPath, document.getISIndex().substring(0, document.getISIndex().indexOf('#')));
							
							fileLog.write("\nThe document " + uploadDocumentPath + " exported successfully." );
							mainController.getLoggerTextArea().appendText("\nThe document " + uploadDocumentPath + " exported successfully.");
						} catch (DocumentException e) {
							try {
								fileLog.write("\nUnable to upload a document called " + nextDest + System.getProperty("file.separator") + uploadDocumentPath );
								mainController.getLoggerTextArea().appendText("\nUnable to upload a document called " + nextDest + System.getProperty("file.separator") + uploadDocumentPath);
							} catch (IOException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
							e.printStackTrace();
						} catch (FolderException e) {
							try {
								fileLog.write("\nUnable to create a folder called " + nextDest );
								mainController.getLoggerTextArea().appendText("\nUnable to create a folder called " + nextDest);
							} catch (IOException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
					});
					
				} catch (DocumentException e) {
					try {
						fileLog.write("Unable to get a document list" );
						mainController.getLoggerTextArea().appendText("Unable to get a document list");
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					e.printStackTrace();
				}
			});
			
			System.out.println("exportTaskFoldersWithDocuments Task Completed with: " + TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - startDate)+ " s");
			mainController.getLoggerTextArea().appendText("exportTaskFoldersWithDocuments Task Completed with: " + TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - startDate)+ " s");
		}
	}
	
	private String resolveInvlidFilenameChars(String filename) {
		String resolvedFilename = filename.replace('\\', '_');
		resolvedFilename = resolvedFilename.replace('/', '_');
		resolvedFilename = resolvedFilename.replace(':', '_');
		resolvedFilename = resolvedFilename.replace('*', '_');
		resolvedFilename = resolvedFilename.replace('?', '_');
		resolvedFilename = resolvedFilename.replace('"', '_');
		resolvedFilename = resolvedFilename.replace('>', '_');
		resolvedFilename = resolvedFilename.replace('<', '_');
		resolvedFilename = resolvedFilename.replace('|', '_');
		
		return resolvedFilename;
	}

	public DataDefinition prepareDataDefinition(OmniService omniService, int dataDefinitionType, String fileID) {

		BatchDetails batchDetails = getDataDefinitionFromDB(fileID);

		DataDefinition dataDefinition = null;

		switch (dataDefinitionType) {

		case 1:
			dataDefinition = omniService.getDataDefinitionUtility().findDataDefinitionByName("passport");
			dataDefinition.getFields().get("Holder Name").setIndexValue(batchDetails.getName());
			dataDefinition.getFields().get("Old Folder Number").setIndexValue(batchDetails.getFileNumber());
			dataDefinition.getFields().get("New Folder Number").setIndexValue(batchDetails.getSerialNumber());
			dataDefinition.getFields().get("Office Name").setIndexValue(batchDetails.getSerialNumber().substring(batchDetails.getSerialNumber().lastIndexOf('/') + 1));
			dataDefinition.getFields().get("Document Type").setIndexValue(batchDetails.getSerialNumber().substring(batchDetails.getSerialNumber().indexOf('/') + 1, batchDetails.getSerialNumber().lastIndexOf('/')));
			dataDefinition.getFields().get("Year").setIndexValue(batchDetails.getYear());

			break;

		case 2:
			dataDefinition = omniService.getDataDefinitionUtility().findDataDefinitionByName("civil");
			dataDefinition.getFields().get("status").setIndexValue(batchDetails.getName());

			break;

		case 3:
			dataDefinition = omniService.getDataDefinitionUtility().findDataDefinitionByName("vital");
			dataDefinition.getFields().get("barcode").setIndexValue(String.valueOf(fileID));

			break;

		case 4:
			dataDefinition = omniService.getDataDefinitionUtility().findDataDefinitionByName("embassiess");
			dataDefinition.getFields().get("barcode").setIndexValue(String.valueOf(fileID));

			break;

		default:

		}

		return dataDefinition;

	}

	private BatchDetails getDataDefinitionFromDB(String recordPrimaryKey) {

		BatchDetails batchDetails = new BatchDetails();
		
		String url = "jdbc:jtds:sqlserver://192.168.60.158:1433;DatabaseName=cspd";
		  
		PoolService<Connection> connectionPoolService = PoolFactory.newSingleConnection(url, "sa", "P@ssw0rd");

		Connection connection = connectionPoolService.get();
		try {
			PreparedStatement ps = connection.prepareStatement("SELECT * FROM BatchDetails WHERE SerialNumber = ?");
			ps.setString(1, recordPrimaryKey);

			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				batchDetails.setBatchId(rs.getInt("BatchId"));
				batchDetails.setCreateDate(rs.getTimestamp("CreateDate"));
				batchDetails.setCreatedBy(rs.getString("CreatedBy"));
				batchDetails.setFileNumber(rs.getString("FileNumber"));
				batchDetails.setFileStatus(rs.getInt("FileStatus"));
				batchDetails.setId(rs.getInt("Id"));
				batchDetails.setIndexFileNumber(rs.getString("IndexFileNumber"));
				batchDetails.setIndexName(rs.getString("IndexName"));
				batchDetails.setName(rs.getString("Name"));
				batchDetails.setSerialNumber(rs.getString("SerialNumber"));
				batchDetails.setYear(rs.getString("Year"));
			}

		} catch (SQLException e) {

			e.printStackTrace();

		} finally {
			try {
				connectionPoolService.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		System.out.println(batchDetails.toString());
		
		return batchDetails;
	}

	private void refillBatch() throws Exception {
		
		int imageTempNumber = 1;
		
		Batch batch = new Batch();
		batch.setImageFilePath("D:\\temp\\");
		Transaction transaction = null;
		for(int t = 1; t <= 1; t++) {
			transaction = new Transaction();
			transaction.setTransactionID(t);
			
			Group group= null;
			for(int g = 1; g <= 1; g++) {
				group = new Group();
				group.setGroupID(61);

				Page page = null;
				for(int p = 1; p <= 500; p++) {
					page = new Page();
					
					Image image = null;
					for(int i = 1; i <= 2; i++) {
						image = new Image();
						image.setFilename("temp" + imageTempNumber++ + ".tiff");
						
						page.getImage().add(image);
					}
					
					group.getPage().add(page);
				}
				
				transaction.getGroup().add(group);
			}

			batch.getTransaction().add(transaction);
			
			//NGOHelper.postPOJOToXML(Batch.class, "D:\\mywork-sts\\cspd\\src\\opex\\element\\00020150618 141054 102.xml", batch);
		}
		
	}

	private static Batch getResponseAsPOJO(Class<Batch> class1, File file) throws Exception {
		return NGOHelper.getResponseAsPOJO(class1, new String(Files.readAllBytes(file.toPath())));
	}

	public boolean existance(OmniService omniService, String folderName) throws Exception {
		
		List<Folder> folders = omniService.getFolderUtility().findFolderByName(folderName);
		
		return folders.size() > 0? true: false;
		
	}

	public void injectMainController(MainController mainController) {
		this.mainController = mainController;
	}

}
