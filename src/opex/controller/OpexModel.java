package opex.controller;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
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
	
	public void uploadFolder(OmniService omniService, String parentFolderID, File opexFolder) throws Exception {
			
		Batch batch = readBatchOXI(opexFolder);
		
		DataDefinition dataDefinition = prepareDataDefinition(omniService, 1, opexFolder.getName());
		
		folderExistProcess(omniService, parentFolderID, opexFolder);
		
		Folder addedFolder = addFolderProcess(omniService, parentFolderID, opexFolder, dataDefinition);

		try {
			
			uploadDocumentsToOmnidocs(omniService, batch, addedFolder.getFolderIndex(), opexFolder);
			
		} finally {
		
			moveUploadedFile(new File(opexFolder.getAbsolutePath() + System.getProperty("file.separator") + opexFolder.getName() + ".oxi"));
				
			opexFolder.delete();
			
		}
	}
	
	private void moveUploadedFile(File file) throws Exception {
		
		if( Boolean.valueOf((String)mainController.getApplicationProperties().get("omnidocs.transfer")) ){
			
			String dest = (String) mainController.getApplicationProperties().get("omnidocs.transferDest");

			try {
				File fileDest = new File(dest + System.getProperty("file.separator") + file.getParentFile().getName() + System.getProperty("file.separator") + file.getName());
				if(!fileDest.exists()) 
					fileDest.mkdirs();
				
				Files.move(file.toPath(), fileDest.toPath(), StandardCopyOption.REPLACE_EXISTING);
				
				//move(folder, fileDest);
				
				mainController.writeLog("Folder (" + file.getName() + ") moved to the destination.");
				
				mainController.writeDBLog(new GeneralLog(processLogID, 4, "INFO", "FILE NAMED " + file.getName() + " MOVED SUCCESSFULY"));
			} catch (Exception e) {
				
				e.printStackTrace();
				
				mainController.writeLog("Unable to move the (" + file.getName() + ") for the destination.");
				
				mainController.writeDBLog(new GeneralLog(processLogID, 4, "INFO", "UNABLE TO MOVE FILE NAMED " + file.getName()));
				
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
	
	private void folderExistProcess(OmniService omniService, String parentFolderID, File folder) throws FolderException, Exception {
		
		List<Folder> folderRs = omniService.getFolderUtility().findFolderByName(parentFolderID, folder.getName(), false);
		
		boolean isFound = (folderRs.size() > 0);
		
		if(isFound) {
			
			mainController.writeDBLog(new GeneralLog(processLogID, 2, "INFO", "FOLDER NAMED " + folder.getName() + " FOUND? " + isFound));
					
			if( mainController.getApplicationProperties().get("omnidocs.deleteFolderIfExist").toString().equalsIgnoreCase("true") ){
				mainController.writeDBLog(new GeneralLog(processLogID, 2, "INFO", "FOLDER NAMED " + folder.getName() + " FOUND? " + isFound));
				try {
						omniService.getFolderUtility().deleteFolder(folderRs.get(0).getFolderIndex());
						
						//updateDeleteFolderFlag(folder.getName());
						
						mainController.writeDBLog(new GeneralLog(processLogID, 2, "INFO", "FOLDER NAMED " + folder.getName() + " DELETED SUCCESSFULY"));
				
				}catch(FolderException fe) {
					
					mainController.writeLog("Unable delete the (" + folder.getName() + ") from omnidocs.");
					mainController.writeDBLog(new GeneralLog(processLogID, 2, "INFO", "UNABLE TO DELETE FOLDER NAMED " + folder.getName()));
					throw new FolderException("Unable delete the (" + folder.getName() + ") from omnidocs.");
				}
			}else {
				
				mainController.writeDBLog(new GeneralLog(processLogID, 2, "INFO", "UNABLE TO CONTINUE WITH FOLDER " + folder.getName() + " THAT ALREADY EXISTS IN OMNIDOCS"));
				
				throw new FolderException("Unable to continue with " + folder.getName() + " that exists in omnidocs.");
				
			}

		}

	}
	
	private int updateDeleteFolderFlag(String folderName) {

		try {
			
			Connection connection = mainController.getSqlConnectionPoolService().get();
			
			PreparedStatement ps = connection.prepareStatement("UPDATE ProcessLog SET UploadedToDocuWare = 0 WHERE BatchIdentifier = ? and LogTimestamp = (SELECT MAX(LogTimestamp) FROM ProcessLog WHERE BatchIdentifier = ?)");
			ps.setString(1, folderName);
			ps.setString(2, folderName);

			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				return rs.getInt("LogId");
			}
			
		} catch (Exception e) {

			e.printStackTrace();

		} 

		return -1;
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
	
	private Folder addFolderProcess(OmniService omniService, String parentFolderID, File folder, DataDefinition dataDefinition) throws FolderException, Exception {
		
		Folder addedFolder = null;
		
		try{
			
			Folder preparedFolder = new Folder();
			preparedFolder.setFolderName(folder.getName());					
			preparedFolder.setParentFolderIndex(parentFolderID);
			preparedFolder.setDataDefinition(dataDefinition);
			
			addedFolder = omniService.getFolderUtility().addFolder(parentFolderID, preparedFolder);
			mainController.writeDBLog(new GeneralLog(processLogID, 1, "INFO", "FOLDER NAMED ADDED SUCCESSFULY"));

		}catch(Exception fe) {
			mainController.writeLog("Unable to find " + folder.getName() + " in omnidocs");
			mainController.writeDBLog(new GeneralLog(processLogID, 1, "ERROR", "UNABLE TO ADD FOLDER NAMED " + folder.getName()));
			throw new FolderException(fe);
		}
	
		return addedFolder;
	}
	
	private void uploadDocumentsToOmnidocs(OmniService omniService, Batch batch, String folderIndex, File physicalFolder) throws DocumentException, Exception {
		
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
						
						String imagePath = scanFolderPath + System.getProperty("file.separator") + image.getFilename();
						Document document = new Document();
						document.setParentFolderIndex(folderIndex);							
						
						try {
							boolean additionFlag = true;
							if(!image.getSide().equalsIgnoreCase("FRONT") && image.getFilename().trim().length() == 0) {
								additionFlag = false;
							}
							
							if(additionFlag) {
															
								document.setDocumentName(image.getFilename().substring(0, image.getFilename().lastIndexOf('.')));
								
								omniService.getDocumentUtility().addDocument(new File(imagePath), document);
								
								mainController.writeLog(imagePath + " uploaded successfuly.");
								
								mainController.writeDBLog(new ProcessDetailsLog(processLogID, document.getDocumentName(), true));
						
								mainController.writeDBLog(new GeneralLog(processLogID, 2, "INFO", "DOCUMENT ADDED SUCCESSFULLY WITH " + imagePath));
								
								moveUploadedFile(new File(imagePath));

							}
						} catch (DocumentException e) {
							
							thereIsError = true;
							
							mainController.writeLog("Unable to upload " + imagePath);
							
							mainController.writeDBLog(new ProcessDetailsLog(processLogID, document.getDocumentName(), false));
							
							mainController.writeDBLog(new GeneralLog(processLogID, 2, "ERROR", "UNABLE TO ADD DOCUMENT " + imagePath));
							
							e.printStackTrace();
							
						} catch (Exception e) {
							
							thereIsError = true;
							e.printStackTrace();
						} 
					}

				}
			}
		}
		
		long completeTime = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - startDate);
		mainController.writeLog("Time Completed with: " + completeTime + ".");
		
		updateProcessLogInfo(physicalFolder.getName(), completeTime);
		
		if(thereIsError) {
			throw new DocumentException("Some Documents Failed.");
		}
	}
	
	private void updateProcessLogInfo(String folderName, long seconds) {
		try {
			
			Connection connection = mainController.getSqlConnectionPoolService().get();
			
			PreparedStatement ps = connection.prepareStatement("UPDATE ProcessLog SET UploadedToOmniDocsTime = ?, UploadedToOmniDocs = 'true' WHERE BatchIdentifier = ? and LogTimestamp = (SELECT MAX(LogTimestamp) FROM ProcessLog WHERE BatchIdentifier = ?)");
			ps.setInt(1, (int)seconds);
			ps.setString(2, folderName);
			ps.setString(3, folderName);
			
			ps.execute();

		} catch (Exception e) {

			e.printStackTrace();

		} 
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
							documentUtility.exportDocumentByImageIndex(uploadDocumentPath, document.getISIndex().substring(0, document.getISIndex().indexOf('#')));
							
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
	
	public void exportDocument(OmniService omniService, String folderName) throws DocumentException {
		
		boolean withErrors = false;
		
		processLogID = getProcessLogIdFromDB(folderName);
		
		OmniDocumentUtility omniDocumentUtility = omniService.getDocumentUtility();
		OmniFolderUtility omniFolderUtility = omniService.getFolderUtility();
		
		if( Boolean.valueOf((String)mainController.getApplicationProperties().get("omnidocs.transfer")) ){
			
			String dest = (String) mainController.getApplicationProperties().get("omnidocs.transferDest") + System.getProperty("file.separator") + folderName;

				/*if(!new File(dest).exists()) {
					
					mainController.writeLog("Destination folder " + dest + " does't exist to sync");
					
					mainController.writeDBLog(new GeneralLog(processLogID, 1, "ERROR", "DESTINATION FOLDER " + dest + " DOEN'T EXIST TO SYNC" ));
					
					throw new DocumentException("Destination folder " + dest + "  does't exist to sync");
				}*/
				
				File destFile = new File(dest);
				
				if(destFile.exists()) {
					
					SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
					String currentDateTime = simpleDateFormat.format(System.currentTimeMillis());
					
					boolean isRenamed = destFile.renameTo(new File(dest + " - " + currentDateTime));
					
					if(isRenamed) {
						
						mainController.writeLog("Destination folder " + destFile.getName() + " renamed to " + destFile.getName() + " - " + currentDateTime);
					
						mainController.writeDBLog(new GeneralLog(processLogID, 1, "ERROR", "DESTINATION FOLDER " + destFile.getName() + " RENAMED TO "  + destFile.getName() + " - " + currentDateTime));
					
					}else{
					
						throw new DocumentException("Unable to rename destination folder " + destFile.getName());
					}
					
				} 
					
				boolean isCreated = destFile.mkdir();
				
				if(isCreated) {
					
					mainController.writeLog("Destination folder " + dest + " created");
					
					mainController.writeDBLog(new GeneralLog(processLogID, 1, "ERROR", "DESTINATION FOLDER " + dest + " CREATED SUCCESSFULY"));
					
				}else {
					
					throw new DocumentException("Unable to create destination folder " + dest);
				}
				
				
				String folderIndex = null;
				try {
					folderIndex = omniFolderUtility.findFolderByName((String) mainController.getApplicationProperties().get("omnidocs.root"), folderName, false).get(0).getFolderIndex();
				} catch (FolderException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				List<Document> documents = omniDocumentUtility.getDocumentList(folderIndex, false);
				Iterator<Document> docIterator = documents.iterator();
				
				while(docIterator.hasNext()) {
											
					Document document = docIterator.next();
					
					try {

						String docName = document.getDocumentName()+"."+document.getCreatedByAppName();

						String imagePath = dest + System.getProperty("file.separator") + docName ;
						String imageIndex = document.getISIndex().substring(0, document.getISIndex().indexOf('#'));
						omniDocumentUtility.exportDocumentByImageIndex(imagePath, imageIndex);
						
						//updateProcessDetailsLog(folderName, docName, "ADD");
						
						//mainController.writeLog("Document (" + docName + ") Synced Successfully");
						
						mainController.writeDBLog(new GeneralLog(processLogID, 1, "INFO", "DOCUMENT " +  docName + " SYNCED SUCCESSFULY"));

						
					} catch (DocumentException e) {
						
						withErrors = true;
						
						//mainController.writeLog("Unable to sync a document (" +  document.getDocumentName() + ")");
						
						mainController.writeDBLog(new GeneralLog(processLogID, 1, "ERROR", "UNALBLE TO SYNC DOCUMENT " +  document.getDocumentName() ));
						
						e.printStackTrace();
						
					} catch (Exception e) {
						
						withErrors = true;
						
						mainController.writeLog(e.getMessage());
						
						mainController.writeDBLog(new GeneralLog(processLogID, 1, "ERROR", e.getMessage().toUpperCase() ));
						
						e.printStackTrace();
					}
					
				}
				
				
				if(withErrors) {
					
					throw new DocumentException("Unable sync some documents");
					
				}else {
				
					mainController.writeLog("Sync documents finished");
				}
				
			
		}
	}
	
	private void updateProcessDetailsLog(String folderName, String docName, String action) {
		try {
			
			Connection connection = mainController.getSqlConnectionPoolService().get();
			
			PreparedStatement ps = connection.prepareStatement("UPDATE ProcessLogDetails SET UploadedToDocuWare = 0, Action = ? WHERE LogId = (SELECT LogId FROM ProcessLog WHERE LogTimestamp = (SELECT MAX(LogTimestamp) FROM ProcessLog WHERE BatchIdentifier = ?)) AND DocumentName = ?");
			ps.setString(1, action);
			ps.setString(2, folderName);
			ps.setString(3, docName);
			
			ps.execute();

		} catch (Exception e) {

			e.printStackTrace();

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

	public DataDefinition prepareDataDefinition(OmniService omniService, int dataDefinitionType, String fileID) throws Exception {

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

	private BatchDetails getDataDefinitionFromDB(String recordPrimaryKey) throws Exception {

		BatchDetails batchDetails = new BatchDetails();

		try {
			
			Connection connection = mainController.getSqlConnectionPoolService().get();
			
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
			
		} catch (Exception e) {

			mainController.writeDBLog(new GeneralLog(processLogID, 3, "ERROR", "UNABLE TO FETCHED UP DATADEFINITION FROM DATABASE"));
			
			e.printStackTrace();

		} 
		
		System.out.println(batchDetails.toString());
		
		return batchDetails;
	}

	private int getProcessLogIdFromDB(String folderName) {

		try {
			
			Connection connection = mainController.getSqlConnectionPoolService().get();
			
			PreparedStatement ps = connection.prepareStatement("SELECT LogId FROM ProcessLog WHERE BatchIdentifier = ? and LogTimestamp = (SELECT MAX(LogTimestamp) FROM ProcessLog WHERE BatchIdentifier = ?)");
			ps.setString(1, folderName);
			ps.setString(2, folderName);

			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				return rs.getInt("LogId");
			}
			
		} catch (Exception e) {

			e.printStackTrace();

		} 

		return -1;
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
