package opex.controller;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import cspd.BatchDetails;
import cspd.core.GeneralLog;
import cspd.core.ProcessDetailsLog;
import cspd.core.ProcessLog;
import etech.dms.exception.DataDefinitionException;
import etech.dms.exception.DocumentException;
import etech.dms.exception.FolderException;
import etech.omni.OmniService;
import etech.omni.core.DataDefinition;
import etech.omni.core.Document;
import etech.omni.core.Folder;
import etech.omni.helper.NGOHelper;
import etech.omni.utils.OmniDocumentUtility;
import etech.omni.utils.OmniFolderUtility;
import opex.element.Batch;
import opex.element.Batch.Transaction;
import opex.element.Batch.Transaction.Group;
import opex.element.Batch.Transaction.Group.Page;
import opex.element.Batch.Transaction.Group.Page.Image;

public class OpexModel {

	private MainController mainController;
	
	int processLogID = -1;
	
	public OpexModel(MainController mainController) {
		this.mainController = mainController;
	}
	
	public void uploadFolder(OmniService omniService, String parentFolderID, File folder) throws Exception {
			
		Batch batch = readBatchOXI(folder);
		
		DataDefinition dataDefinition = prepareDataDefinition(omniService, 1, folder.getName());
		
		folderExistProcess(omniService, parentFolderID, folder);
		
		Folder addedFolder = addFolderProcess(omniService, parentFolderID, folder, dataDefinition);

		try {
			uploadDocumentsToOmnidocs(omniService, batch, addedFolder.getFolderIndex(), folder);
		} finally {
			moveUploadedFolder(folder);
		}
		
	}
	
	private void moveUploadedFolder(File folder) throws Exception {
		
		if( Boolean.valueOf((String)mainController.getApplicationProperties().get("omnidocs.transfer")) ){
			
			String dest = (String) mainController.getApplicationProperties().get("omnidocs.transferDest");

			try {
				File fileDest = new File(dest + System.getProperty("file.separator") + folder.getName());
				if(!fileDest.exists()) 
					fileDest.mkdirs();
				
				//Files.move(Paths.get(folder.getPath()), Paths.get(file.getPath()), StandardCopyOption.REPLACE_EXISTING);
				
				/*Files.walkFileTree(fileDest.toPath(), new SimpleFileVisitor<Path>() {
					
					public Path fromPath;
				    public Path toPath;
				    private StandardCopyOption copyOption = StandardCopyOption.REPLACE_EXISTING;
				    
					@Override
				    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
				        Path targetPath = toPath.resolve(fromPath.relativize(dir));
				        if(!Files.exists(targetPath)){
				            Files.createDirectory(targetPath);
				        }
				        return FileVisitResult.CONTINUE;
				    }

				    @Override
				    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
				        Files.copy(file, toPath.resolve(fromPath.relativize(file)), copyOption);
				        return FileVisitResult.CONTINUE;
				    }
				    				    
				});
				*/
				move(folder, fileDest);
				
				mainController.writeLog("Folder (" + folder.getName() + ") moved to the destination.");
				
				mainController.writeDBLog(new GeneralLog(processLogID, 4, "INFO", "FOLDER NAMED " + folder.getName() + " IS MOVED SUCCESSFULY"));
				
			} catch (Exception e) {
				
				mainController.writeLog("Unable to move the (" + folder.getName() + ") for the destination.");
				
				mainController.writeDBLog(new GeneralLog(processLogID, 4, "INFO", "UNABLE TO MOVE FOLDER NAMED " + folder.getName()));
				
				throw e;
			}
			
		}
		
	}

	private boolean move(File sourceFile, File destFile) {
	    if (sourceFile.isDirectory()) {
	        for (File file : sourceFile.listFiles()) {
	            move(file, new File(destFile.getPath() + System.getProperty("file.separator") + file.getName()));
	        }
	    } else {
	        try {
	            Files.move(Paths.get(sourceFile.getPath()), Paths.get(destFile.getPath()), StandardCopyOption.REPLACE_EXISTING);
	            return true;
	        } catch (IOException e) {
	            return false;
	        }
	    }
	    return false;
	}
	
	class MoveDirectory extends SimpleFileVisitor<Path> {
		
		public Path fromPath;
	    public Path toPath;
	    private StandardCopyOption copyOption = StandardCopyOption.REPLACE_EXISTING;

	    MoveDirectory(Path fromPath, Path toPath){
	    	this.fromPath = fromPath;
	    	this.toPath = toPath;
	    }
	    
		@Override
	    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
	        Path targetPath = toPath.resolve(fromPath.relativize(dir));
	        if(!Files.exists(targetPath)){
	            Files.createDirectory(targetPath);
	        }
	        return FileVisitResult.CONTINUE;
	    }

	    @Override
	    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
	        Files.move(file, toPath.resolve(fromPath.relativize(file)), copyOption);
	        return FileVisitResult.CONTINUE;
	    }
	    				    
	}
	
	private void folderExistProcess(OmniService omniService, String parentFolderID, File folder) throws FolderException {
		
		List<Folder> folderRs = omniService.getFolderUtility().findFolderByName(parentFolderID, folder.getName(), false);
		
		boolean isFound = (folderRs.size() > 0);
		
		if(isFound) {
			
			mainController.writeDBLog(new GeneralLog(processLogID, 2, "INFO", "DOES FOLDER NAMED " + folder.getName() + " FOUND? " + isFound));
					
			if( Boolean.valueOf((String)mainController.getApplicationProperties().get("omnidocs.deleteFolderIfExist")) ){
				mainController.writeDBLog(new GeneralLog(processLogID, 2, "INFO", "DOES FOLDER NAMED " + folder.getName() + " FOUND? " + isFound));
				try {
						omniService.getFolderUtility().deleteFolder(folderRs.get(0).getFolderIndex());
						mainController.writeDBLog(new GeneralLog(processLogID, 2, "INFO", "FOLDER NAMED " + folder.getName() + " IS DELETED SUCCESSFULY"));
				
				}catch(FolderException fe) {
					
					mainController.writeLog("Unable delete the (" + folder.getName() + ") from omnidocs.");
					mainController.writeDBLog(new GeneralLog(processLogID, 2, "INFO", "UNABLE TO DELETE FOLDER NAMED " + folder.getName()));
					throw new FolderException("Unable delete the (" + folder.getName() + ") from omnidocs.");
				}
			}

		}

	}
	
	private Batch readBatchOXI(File folder) throws Exception {

		String folderPath = folder.getAbsolutePath();
		
		String batchOXI = folderPath + folderPath.substring(folderPath.lastIndexOf(System.getProperty("file.separator"))) + ".oxi";
		
		
		Batch batch = null;
		try {
			batch = NGOHelper.getResponseAsPOJO(Batch.class, new String(Files.readAllBytes(new File(batchOXI).toPath())));

		}catch(Exception fe) {
			mainController.writeLog("Unable to parse " + batchOXI);
			mainController.writeDBLog(new GeneralLog(processLogID, 1, "ERROR", "UNABLE TO PARSE OXI FILE " + batchOXI));
			throw new Exception(fe);
		}
		
		processLogID = 
				mainController.writeDBLog(new ProcessLog(batch.getBatchIdentifier(), batch.getBaseMachine(), batch.getStartTime(), 
						   batch.getEndInfo().getEndTime(), folder.listFiles().length, false, false, 0, 0));
		
		mainController.writeDBLog(new GeneralLog(processLogID, 1, "INFO", "OXI FILE PARSED SUCCESSFULY WITH " + batchOXI));
		
		
		return batch;
		
	}
	
	private Folder addFolderProcess(OmniService omniService, String parentFolderID, File folder, DataDefinition dataDefinition) throws FolderException {
		
		Folder addedFolder = null;
		
		try{
			
			Folder preparedFolder = new Folder();
			preparedFolder.setFolderName(folder.getName());					
			preparedFolder.setParentFolderIndex(parentFolderID);
			preparedFolder.setDataDefinition(dataDefinition);
			
			addedFolder = omniService.getFolderUtility().addFolder(parentFolderID, preparedFolder);
			mainController.writeDBLog(new GeneralLog(processLogID, 1, "INFO", "FOLDER NAMED IS ADDED SUCCESSFULY"));

		}catch(Exception fe) {
			mainController.writeLog("Unable to find " + folder.getName() + " in omnidocs");
			mainController.writeDBLog(new GeneralLog(processLogID, 1, "ERROR", "UNABLE TO ADD FOLDER NAMED " + folder.getName()));
			throw new FolderException(fe);
		}
	
		return addedFolder;
	}
	
	private void uploadDocumentsToOmnidocs(OmniService omniService, Batch batch, String folderIndex, File physicalFolder) throws DocumentException {
		
		boolean thereIsError = false;
		
		long startDate = System.currentTimeMillis();
		
		// read opex xml

		String scanFolderPath = physicalFolder.getAbsolutePath();//batch.getImageFilePath();

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
						document.setParentFolderIndex(folderIndex);							
						document.setDocumentName(image.getFilename());
						
						String imagePath = scanFolderPath + System.getProperty("file.separator") + image.getFilename();
						
						try {
							boolean additionFlag = true;
							if(!image.getSide().equalsIgnoreCase("FRONT") && image.getFilename().trim().length() == 0) {
								additionFlag = false;
							}
							
							if(additionFlag) {
								omniService.getDocumentUtility().addDocument(new File(imagePath), document);
								
								mainController.writeLog(imagePath + " uploaded successfuly.");
								
								mainController.writeDBLog(new ProcessDetailsLog(processLogID, document.getDocumentName(), true));
						
								mainController.writeDBLog(new GeneralLog(processLogID, 2, "INFO", "DOCUMENT ADDED SUCCESSFULLY WITH " + imagePath));
							}
						} catch (DocumentException e) {
							thereIsError = true;
							mainController.writeLog("Unable to upload " + imagePath);
							mainController.writeDBLog(new ProcessDetailsLog(processLogID, document.getDocumentName(), false));
							
							mainController.writeDBLog(new GeneralLog(processLogID, 2, "ERROR", "UNABLE TOT ADD DOCUMENT " + imagePath));
							e.printStackTrace();
						}
					}

				}
			}
		}
		
		mainController.writeLog("Time Completed with: " + TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - startDate)+".");
		
		if(thereIsError) {
			throw new DocumentException("Some Documents Failed.");
		}
	}
	
/*	private void writeDBLog(String msg) {
		
		Connection connection = mainController.getSqlConnectionPoolService().get();
		
		Task task = new Task<Void>() {

			@Override
			protected Void call() throws Exception {
				PreparedStatement preparedStatement = connection.prepareStatement("");
				preparedStatement.executeQuery();
				return null;
			}
		};
		new Thread(task).start();
	}
*/
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

		BatchDetails batchDetails = getDataDefinitionFromDB("018/01/0000061");//fileID);

		DataDefinition dataDefinition = null;

		try {
			switch (dataDefinitionType) {
	
			case 1:
				dataDefinition = omniService.getDataDefinitionUtility().findDataDefinitionByName("passport");
				dataDefinition.getFields().get("Holder Name").setIndexValue(batchDetails.getName());
				dataDefinition.getFields().get("Old Folder Number").setIndexValue(batchDetails.getFileNumber());
				dataDefinition.getFields().get("New Folder Number").setIndexValue(batchDetails.getSerialNumber());
				//dataDefinition.getFields().get("Office Name").setIndexValue(batchDetails.getSerialNumber().substring(batchDetails.getSerialNumber().lastIndexOf('_') + 1));
				//dataDefinition.getFields().get("Document Type").setIndexValue(batchDetails.getSerialNumber().substring(batchDetails.getSerialNumber().indexOf('_') + 1, batchDetails.getSerialNumber().lastIndexOf('_')));
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
			
			mainController.writeDBLog(new GeneralLog(processLogID, 3, "INFO", "DATADEFINITION TYPE " + dataDefinitionType + " PREPARED SUCCESSFULY"));
			
		}catch(DataDefinitionException dfe) {
			
			mainController.writeDBLog(new GeneralLog(processLogID, 3, "ERROR", "UNABLE TO PREPARE DATADEFINITION TYPE " + dataDefinitionType));
			
			dfe.printStackTrace();
		}
		

		return dataDefinition;

	}

	private BatchDetails getDataDefinitionFromDB(String recordPrimaryKey) {

		BatchDetails batchDetails = new BatchDetails();

		Connection connection = mainController.getSqlConnectionPoolService().get();
		
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

			mainController.writeDBLog(new GeneralLog(processLogID, 3, "INFO", "DATADEFINITION FETCHED UP FROM DATABASE SUCCESSFULY"));
			
		} catch (SQLException e) {

			mainController.writeDBLog(new GeneralLog(processLogID, 3, "ERROR", "UNABLE TO FETCHED UP DATADEFINITION FROM DATABASE"));
			e.printStackTrace();

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
