package opex.controller;

import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import cspd.core.BatchDetails;
import cspd.core.DocumentReport;
import cspd.core.GeneralLog;
import cspd.core.OpexFolderReport;
import cspd.core.ProcessDetailsLog;
import cspd.core.ProcessLog;
import etech.dms.exception.DataDefinitionException;
import etech.dms.exception.DocumentException;
import etech.dms.exception.FolderException;
import etech.omni.OmniService;
import etech.omni.core.DataDefinition;
import etech.omni.core.Document;
import etech.omni.core.Field;
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

	public void uploadFolder(OpexFolderReport opexFolderReport, OmniService omniService, File opexFolder) throws Exception {

		BatchDetails batchDetails = getDataDefinitionFromDB(opexFolder.getName().replaceAll("_", "/"));

		//Batch batch = readBatchOXI(opexFolder);

		processLogID = mainController.writeDBLog(new ProcessLog(opexFolder.getName(), opexFolder.listFiles().length, "Machine 1", "", ""));

		//updateNumberOfPagesAndImages(opexFolder, batch);

		int dfType = Integer.valueOf(batchDetails.getFileType());//Integer.valueOf(opexFolder.getName().substring(opexFolder.getName().indexOf("+"), opexFolder.getName().lastIndexOf("+")));

		DataDefinition dataDefinition = prepareDataDefinition(omniService, dfType, batchDetails);

		String parentFolderID = null;
		
		switch(dfType) {
		case 1:
			parentFolderID = mainController.getOmnidocsProperties().getProperty("opex.passport");
			break;
		case 2:
			parentFolderID = mainController.getOmnidocsProperties().getProperty("opex.civil");
			break;
		case 3:
			parentFolderID = mainController.getOmnidocsProperties().getProperty("opex.vital");
			break;
		case 4:
			parentFolderID = mainController.getOmnidocsProperties().getProperty("opex.embassies");
			break;
		default:
			throw new Exception("Unable to specify data definition type");
		}
		
		folderExistProcess(omniService, parentFolderID, opexFolder);

		Folder addedFolder = addFolderProcess(omniService, parentFolderID, opexFolder, dataDefinition);

		// try {

		uploadDocumentsToOmnidocs(opexFolderReport, omniService, null /* batch */, addedFolder.getFolderIndex(),
				opexFolder);

		// } finally {

		// moveUploadedFile(new File(opexFolder.getAbsolutePath() +
		// System.getProperty("file.separator") + opexFolder.getName() + ".oxi"));

		opexFolder.delete();

		// }
	}

	private void updateNumberOfPagesAndImages(File opexFolder, Batch batch) throws Exception {

		try {

			int noOfPages = 0;

			int noOfImages = 0;

			Iterator<Transaction> transactions = batch.getTransaction().iterator();

			while (transactions.hasNext()) {

				Transaction transaction = transactions.next();

				Iterator<Group> groups = transaction.getGroup().iterator();

				while (groups.hasNext()) {

					Group group = groups.next();

					noOfPages += group.getPage().size();

					Iterator<Page> pages = group.getPage().iterator();

					while (pages.hasNext()) {

						Page page = pages.next();

						noOfImages += page.getImage().size();

					}
				}
			}

			Connection connection = mainController.getSqlConnectionPoolService().get();

			String baseIdentifier = opexFolder.getName().contains("%")
					? opexFolder.getName().substring(0, opexFolder.getName().indexOf("%"))
					: opexFolder.getName();

			PreparedStatement ps = connection.prepareStatement(
					"UPDATE BatchDetails SET NumberOfPages = ?, NumberOfImages  = ? WHERE SerialNumber = ?");
			ps.setInt(1, noOfPages);
			ps.setInt(2, noOfImages);
			ps.setString(3, baseIdentifier);

			ps.execute();

			mainController.writeLog("Number of pages and images updated.");

			mainController.writeDBLog( new GeneralLog(processLogID, 4, "INFO", "NUMBER OF PAGES AND IMAGES UPDATED") );

		} catch (Exception e) {

			e.printStackTrace();

			mainController.writeLog("Unable to update number of pages and images");

			mainController.writeDBLog(new GeneralLog(processLogID, 4, "INFO", "UNABLE TO UPDATE NUMBER OF PAGES AND IMAGES"));

			throw e;
		}
	}

	private void moveUploadedFile(File file) throws Exception {

		if (Boolean.valueOf((String) mainController.getOmnidocsProperties().get("omnidocs.transfer"))) {

			String dest = (String) mainController.getOmnidocsProperties().get("omnidocs.transferDest");

			try {
				
				File fileDest = new File(dest + System.getProperty("file.separator") + file.getParentFile().getName()
						+ System.getProperty("file.separator") + file.getName());

				if(!fileDest.exists()) {
					fileDest.mkdirs();
				}
				
				Files.move(file.toPath(), fileDest.toPath(), StandardCopyOption.REPLACE_EXISTING);

				mainController.writeLog("Folder (" + file.getName() + ") moved to the destination.");

				mainController.writeDBLog(
						new GeneralLog(processLogID, 4, "INFO", "FILE NAMED " + file.getName() + " MOVED SUCCESSFULY"));

			} catch (Exception e) {

				e.printStackTrace();

				mainController.writeLog("Unable to move the (" + file.getName() + ") for the destination.");

				mainController.writeDBLog(
						new GeneralLog(processLogID, 4, "INFO", "UNABLE TO MOVE FILE NAMED " + file.getName()));

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
				Files.move(Paths.get(sourceFile.getPath()), Paths.get(destFile.getPath()),
						StandardCopyOption.REPLACE_EXISTING);
				return true;
			} catch (IOException e) {
				return false;
			}
		}
		return false;
	}

	private void folderExistProcess(OmniService omniService, String parentFolderID, File folder)
			throws FolderException, Exception {

		List<Folder> folderRs = omniService.getFolderUtility().findFolderByName(parentFolderID, folder.getName(),
				false);

		boolean isFound = (folderRs.size() > 0);

		if (isFound) {

			mainController.writeDBLog(
					new GeneralLog(processLogID, 2, "INFO", "FOLDER NAMED " + folder.getName() + " FOUND? " + isFound));

			if (mainController.getOmnidocsProperties().get("omnidocs.deleteFolderIfExist").toString()
					.equalsIgnoreCase("true")) {
				mainController.writeDBLog(new GeneralLog(processLogID, 2, "INFO",
						"FOLDER NAMED " + folder.getName() + " FOUND? " + isFound));
				try {
					omniService.getFolderUtility().deleteFolder(folderRs.get(0).getFolderIndex());

					// updateDeleteFolderFlag(folder.getName());

					mainController.writeDBLog(new GeneralLog(processLogID, 2, "INFO",
							"FOLDER NAMED " + folder.getName() + " DELETED SUCCESSFULY"));

				} catch (FolderException fe) {

					mainController.writeLog("Unable delete the (" + folder.getName() + ") from omnidocs.");
					mainController.writeDBLog(new GeneralLog(processLogID, 2, "INFO",
							"UNABLE TO DELETE FOLDER NAMED " + folder.getName()));
					throw new FolderException("Unable delete the (" + folder.getName() + ") from omnidocs.");
				}
			} else {

				mainController.writeDBLog(new GeneralLog(processLogID, 2, "INFO",
						"UNABLE TO CONTINUE WITH FOLDER " + folder.getName() + " THAT ALREADY EXISTS IN OMNIDOCS"));

				throw new FolderException("Unable to continue with " + folder.getName() + " that exists in omnidocs.");

			}

		}

	}

	private int updateDeleteFolderFlag(String folderName) {

		try {

			Connection connection = mainController.getSqlConnectionPoolService().get();

			PreparedStatement ps = connection.prepareStatement(
					"UPDATE ProcessLog SET UploadedToDocuWare = 0 WHERE BatchIdentifier = ? and LogTimestamp = (SELECT MAX(LogTimestamp) FROM ProcessLog WHERE BatchIdentifier = ?)");
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

		Batch batch = null;

		File[] files = folder.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.toLowerCase().endsWith(".oii_edit");
			}
		});

		try {

			if (files.length == 0) {

				throw new Exception();

			}

			batch = NGOHelper.getResponseAsPOJO(Batch.class, new String(Files.readAllBytes(files[0].toPath())));
			
			moveUploadedFile(files[0]);


		} catch (Exception fe) {

			mainController.writeLog("Unable to parse xml or not found");

			mainController
					.writeDBLog(new GeneralLog(processLogID, 1, "ERROR", "UNABLE TO PARSE XML FILE OR NOT FOUND"));

			throw new Exception("Unable to parse xml or not found");
		}

		/*
		 * processLogID = mainController.writeDBLog(new
		 * ProcessLog(batch.getBatchIdentifier(), batch.getBaseMachine(),
		 * batch.getStartTime(), batch.getEndInfo().getEndTime(),
		 * folder.listFiles().length));
		 */

		mainController.writeDBLog(new GeneralLog(processLogID, 1, "INFO", "XML FILE PARSED SUCCESSFULY"));

		return batch;

	}

	private Folder addFolderProcess(OmniService omniService, String parentFolderID, File folder,
			DataDefinition dataDefinition) throws FolderException, Exception {

		Folder addedFolder = null;

		try {

			Folder preparedFolder = new Folder();
			preparedFolder.setFolderName(folder.getName());
			preparedFolder.setParentFolderIndex(parentFolderID);
			preparedFolder.setDataDefinition(dataDefinition);

			addedFolder = omniService.getFolderUtility().addFolder(parentFolderID, preparedFolder);
			mainController.writeDBLog(new GeneralLog(processLogID, 1, "INFO", "FOLDER NAMED ADDED SUCCESSFULY"));

		} catch (Exception fe) {
			mainController.writeLog("Unable to add " + folder.getName() + " to omnidocs");
			mainController.writeDBLog(
					new GeneralLog(processLogID, 1, "ERROR", "UNABLE TO ADD FOLDER NAMED " + folder.getName()));
			throw new FolderException(fe);
		}

		return addedFolder;
	}

	private void uploadDocumentsToOmnidocs(OpexFolderReport opexFolderReport, OmniService omniService, Batch batch,
			String folderIndex, File physicalFolder) throws DocumentException, Exception {

		boolean thereIsError = false;

		long startDate = System.currentTimeMillis();

		String scanFolderPath = physicalFolder.getAbsolutePath();

		File[] files = physicalFolder.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.toLowerCase().endsWith(".pdf");
			}
		});

		for (int index = 0; index < files.length; index++) {

			File file = files[index];

			String filePath = scanFolderPath + System.getProperty("file.separator") + file.getName();
			
			String documentName = file.getName().substring(0, file.getName().lastIndexOf('.'));

			try {
				
				omniService.getDocumentUtility().addDocument(new File(filePath), folderIndex, documentName);

				mainController.writeLog(filePath + " uploaded successfuly.");

				mainController.writeDBLog(new ProcessDetailsLog(processLogID, documentName, true));

				mainController.writeDBLog(new GeneralLog(processLogID, 2, "INFO",
						"DOCUMENT ADDED SUCCESSFULLY WITH " + physicalFolder.getName() + "/" + file.getName()));

				moveUploadedFile(new File(filePath));

			} catch (DocumentException e) {

				thereIsError = true;

				mainController.writeLog("Unable to upload " + filePath);

				mainController.writeDBLog(new ProcessDetailsLog(processLogID, documentName, false));

				mainController.writeDBLog(new GeneralLog(processLogID, 2, "ERROR", "UNABLE TO ADD DOCUMENT " + filePath));

				opexFolderReport.setDocumentLevel(true);

				opexFolderReport.getFailedDocuments().add(new DocumentReport(documentName, "Unable to add document " + file.getName()));

				e.printStackTrace();

			} catch (Exception e) {

				thereIsError = true;

				e.printStackTrace();
			}

		}

		/*
		 * Iterator<Transaction> transactions = batch.getTransaction().iterator();
		 * 
		 * while (transactions.hasNext()) {
		 * 
		 * Transaction transaction = transactions.next();
		 * 
		 * Iterator<Group> groups = transaction.getGroup().iterator();
		 * 
		 * while (groups.hasNext()) {
		 * 
		 * Group group = groups.next();
		 * 
		 * Iterator<Page> pages = group.getPage().iterator();
		 * 
		 * while (pages.hasNext()) {
		 * 
		 * Page page = pages.next();
		 * 
		 * Iterator<Image> images = page.getImage().iterator();
		 * 
		 * while (images.hasNext()) {
		 * 
		 * Image image = images.next();
		 * 
		 * String imagePath = scanFolderPath + System.getProperty("file.separator") +
		 * image.getFilename(); Document document = new Document();
		 * document.setParentFolderIndex(folderIndex);
		 * 
		 * try { boolean additionFlag = true;
		 * if(!image.getSide().equalsIgnoreCase("FRONT") &&
		 * image.getFilename().trim().length() == 0) { additionFlag = false; }
		 * 
		 * if(additionFlag) {
		 * 
		 * document.setDocumentName(image.getFilename().substring(0,
		 * image.getFilename().lastIndexOf('.')));
		 * 
		 * omniService.getDocumentUtility().addDocument(new File(imagePath), document);
		 * 
		 * mainController.writeLog(imagePath + " uploaded successfuly.");
		 * 
		 * mainController.writeDBLog(new ProcessDetailsLog(processLogID,
		 * document.getDocumentName(), true));
		 * 
		 * mainController.writeDBLog(new GeneralLog(processLogID, 2, "INFO",
		 * "DOCUMENT ADDED SUCCESSFULLY WITH " + physicalFolder.getName() + "/" +
		 * image.getFilename()));
		 * 
		 * moveUploadedFile(new File(imagePath));
		 * 
		 * } } catch (DocumentException e) {
		 * 
		 * thereIsError = true;
		 * 
		 * mainController.writeLog("Unable to upload " + imagePath);
		 * 
		 * mainController.writeDBLog(new ProcessDetailsLog(processLogID,
		 * document.getDocumentName(), false));
		 * 
		 * mainController.writeDBLog(new GeneralLog(processLogID, 2, "ERROR",
		 * "UNABLE TO ADD DOCUMENT " + imagePath));
		 * 
		 * opexFolderReport.setDocumentLevel(true);
		 * 
		 * opexFolderReport.getFailedDocuments().add(new
		 * DocumentReport(document.getDocumentName(), "Unable to add document " +
		 * image.getFilename()));
		 * 
		 * e.printStackTrace();
		 * 
		 * } catch (Exception e) {
		 * 
		 * thereIsError = true; e.printStackTrace(); } }
		 * 
		 * } } }
		 */
		long completeTime = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - startDate);
		mainController.writeLog("Time Completed with: " + completeTime + ".");

		updateProcessLogInfo(physicalFolder.getName(), completeTime);

		if (thereIsError) {
			throw new DocumentException("Some Documents Failed.");
		}
	}

	private void updateProcessLogInfo(String folderName, long seconds) {
		try {

			Connection connection = mainController.getSqlConnectionPoolService().get();

			PreparedStatement ps = connection.prepareStatement(
					"UPDATE ProcessLog SET UploadedToOmniDocsTime = ?, UploadedToOmniDocs = 'true' WHERE BatchIdentifier = ? and LogTimestamp = (SELECT MAX(LogTimestamp) FROM ProcessLog WHERE BatchIdentifier = ?)");
			ps.setInt(1, (int) seconds);
			ps.setString(2, folderName);
			ps.setString(3, folderName);

			ps.execute();

		} catch (Exception e) {

			e.printStackTrace();

		}
	}

	public void exportTaskWithoutSubfolder(OmniService omniService, String folderID, String folderDestination)
			throws Exception {

		long startDate = System.currentTimeMillis();

		List<Folder> folders = new ArrayList<>();
		folders.add(omniService.getFolderUtility().getFolder(folderID));

		exportTaskFoldersWithDocuments(omniService, folders, folderDestination);

		System.out.println("exportTaskWithoutSubfolder Task Completed with: "
				+ TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - startDate) + " s");

	}

	public void exportTaskWithSubfolder(OmniService omniService, String folderID, String folderDestination)
			throws Exception {

		long startDate = System.currentTimeMillis();

		List<Folder> folders = omniService.getFolderUtility().getFolderList(folderID, true);

		exportTaskFoldersWithDocuments(omniService, folders, folderDestination);

		System.out.println("exportTaskWithSubfolder Task Completed with: "
				+ TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - startDate) + " s");

	}

	public void exportTaskFoldersWithDocuments(OmniService omniService, List<Folder> folders, String folderDestination)
			throws Exception {

		try (FileWriter fileLog = new FileWriter(new File(folderDestination).getParent() + "\\export-log.txt", true)) {
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
							nextDest = folderDestination + System.getProperty("file.separator")
									+ folderUtility.getFolderAncestorAsString(folder.getFolderIndex());
							File file = new File(nextDest);

							if (!file.exists()) {
								file.mkdirs();
								fileLog.write("\n" + nextDest + " Created.");
							}

							uploadDocumentPath = nextDest + System.getProperty("file.separator") + document.getDocumentName();
							
							documentUtility.exportDocument(uploadDocumentPath, document);

							fileLog.write("\nThe document " + uploadDocumentPath + " exported successfully.");
							
							mainController.writeLog("\nThe document " + uploadDocumentPath + " exported successfully.");
							
						} catch (DocumentException e) {
							try {
								fileLog.write("\nUnable to upload a document called " + nextDest
										+ System.getProperty("file.separator") + uploadDocumentPath);
								mainController.writeLog("\nUnable to upload a document called " + nextDest
										+ System.getProperty("file.separator") + uploadDocumentPath);
							} catch (IOException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
							e.printStackTrace();
						} catch (FolderException e) {
							try {
								fileLog.write("\nUnable to create a folder called " + nextDest);
								mainController.writeLog("\nUnable to create a folder called " + nextDest);
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
						fileLog.write("Unable to get a document list");
						mainController.writeLog("Unable to get a document list");
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					e.printStackTrace();
				}
			});

			System.out.println("exportTaskFoldersWithDocuments Task Completed with: "
					+ TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - startDate) + " s");
			mainController.writeLog("exportTaskFoldersWithDocuments Task Completed with: "
					+ TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - startDate) + " s");
		}
	}

	public void syncFolder(OmniService omniService, String folderName) throws DocumentException {

		boolean withErrors = false;

		processLogID = getProcessLogIdFromDB(folderName);

		OmniDocumentUtility omniDocumentUtility = omniService.getDocumentUtility();
		OmniFolderUtility omniFolderUtility = omniService.getFolderUtility();

		if (Boolean.valueOf((String) mainController.getOmnidocsProperties().get("omnidocs.transfer"))) {

			String dest = (String) mainController.getOmnidocsProperties().get("omnidocs.transferDest")
					+ System.getProperty("file.separator") + folderName;

			File destFile = new File(dest);

			if (destFile.exists()) {

				SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
				String currentDateTime = simpleDateFormat.format(System.currentTimeMillis());

				boolean isRenamed = destFile.renameTo(new File(dest + " - " + currentDateTime));

				if (isRenamed) {

					mainController.writeLog("Destination folder " + destFile.getName() + " renamed to "
							+ destFile.getName() + " - " + currentDateTime);

					mainController.writeDBLog(new GeneralLog(processLogID, 1, "ERROR", "DESTINATION FOLDER "
							+ destFile.getName() + " RENAMED TO " + destFile.getName() + " - " + currentDateTime));

				} else {

					throw new DocumentException("Unable to rename destination folder " + destFile.getName());
				}

			}

			boolean isCreated = destFile.mkdir();

			if (isCreated) {

				mainController.writeLog("Destination folder " + dest + " created");

				mainController.writeDBLog(new GeneralLog(processLogID, 1, "ERROR",
						"DESTINATION FOLDER " + dest + " CREATED SUCCESSFULY"));

			} else {

				throw new DocumentException("Unable to create destination folder " + dest);
			}

			String folderIndex = null;
			try {
				folderIndex = omniFolderUtility
						.findFolderByName((String) mainController.getOmnidocsProperties().get("omnidocs.root"),
								folderName, false)
						.get(0).getFolderIndex();
			} catch (FolderException e1) {
				e1.printStackTrace();
			}
			List<Document> documents = omniDocumentUtility.getDocumentList(folderIndex, false);
			Iterator<Document> docIterator = documents.iterator();

			while (docIterator.hasNext()) {

				Document document = docIterator.next();

				try {

					String docName = document.getDocumentName() + "." + document.getCreatedByAppName();

					String imagePath = dest + System.getProperty("file.separator") + docName;

					omniDocumentUtility.exportDocument(imagePath, document);

					// updateProcessDetailsLog(folderName, docName, "ADD");

					// mainController.writeLog("Document (" + docName + ") Synced Successfully");

					mainController.writeDBLog(
							new GeneralLog(processLogID, 1, "INFO", "DOCUMENT " + docName + " SYNCED SUCCESSFULY"));

				} catch (DocumentException e) {

					withErrors = true;

					// mainController.writeLog("Unable to sync a document (" +
					// document.getDocumentName() + ")");

					mainController.writeDBLog(new GeneralLog(processLogID, 1, "ERROR",
							"UNALBLE TO SYNC DOCUMENT " + document.getDocumentName()));

					e.printStackTrace();

				} catch (Exception e) {

					withErrors = true;

					mainController.writeLog(e.getMessage());

					mainController.writeDBLog(new GeneralLog(processLogID, 1, "ERROR", e.getMessage().toUpperCase()));

					e.printStackTrace();
				}

			}

			updateUploadedToDocuWare(folderName);
			updateFolderMetaData(omniService, folderName);

			if (withErrors) {

				throw new DocumentException("Unable sync some documents");

			} else {

				mainController.writeLog("Sync documents finished");
			}

		}
	}

	private void updateFolderMetaData(OmniService omniService, String folderName) {
		try {

			String definitionName = null;

			switch (Integer.valueOf(folderName.substring(folderName.indexOf("+"), folderName.lastIndexOf("+")))) {

			case 1:
				definitionName = (String) mainController.getOmnidocsProperties().get("opex.passport");
				break;
			case 2:
				definitionName = (String) mainController.getOmnidocsProperties().get("opex.civil");
				break;
			case 3:
				definitionName = (String) mainController.getOmnidocsProperties().get("opex.vital");
				break;
			case 4:
				definitionName = (String) mainController.getOmnidocsProperties().get("opex.embassies");
				break;

			}

			String root = (String) mainController.getOmnidocsProperties().get("omnidocs.root");

			List<Folder> folders = omniService.getFolderUtility().findFolderByName(root, folderName);

			Map<String, Field> fields = omniService.getFolderUtility().getFolder(folders.get(0).getFolderIndex())
					.getDataDefinition().getFields();

			String firstName = fields.get("Holder First Name").getIndexValue();
			String secondName = fields.get("Holder Second Name").getIndexValue();
			String thirdName = fields.get("Holder Third Name").getIndexValue();

			Connection connection = mainController.getSqlConnectionPoolService().get();

			PreparedStatement ps = connection.prepareStatement(
					"UPDATE BatchDetails SET FirstName = ?, SecondName = ?, FamilyName = ? WHERE SerialNumber = ?");
			ps.setString(1, firstName);
			ps.setString(2, secondName);
			ps.setString(3, thirdName);
			ps.setString(4, folderName.substring(folderName.indexOf("%") + 1));

			ps.execute();

		} catch (Exception e) {

			e.printStackTrace();

		}
	}

	private void updateUploadedToDocuWare(String folderName) {
		try {

			Connection connection = mainController.getSqlConnectionPoolService().get();

			PreparedStatement ps = connection.prepareStatement(
					"UPDATE ProcessLog SET UploadedToDocuWare = 2 WHERE LogId = (SELECT LogId FROM ProcessLog WHERE LogTimestamp = (SELECT MAX(LogTimestamp) FROM ProcessLog WHERE BatchIdentifier = ?))");
			ps.setString(1, folderName);

			ps.execute();

		} catch (Exception e) {

			e.printStackTrace();

		}

	}

	private void updateProcessDetailsLog(String folderName, String docName, String action) {
		try {

			Connection connection = mainController.getSqlConnectionPoolService().get();

			PreparedStatement ps = connection.prepareStatement(
					"UPDATE ProcessLogDetails SET UploadedToDocuWare = 0, Action = ? WHERE LogId = (SELECT LogId FROM ProcessLog WHERE LogTimestamp = (SELECT MAX(LogTimestamp) FROM ProcessLog WHERE BatchIdentifier = ?)) AND DocumentName = ?");
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

	public DataDefinition prepareDataDefinition(OmniService omniService, int dataDefinitionType, BatchDetails batchDetails)
			throws Exception {

		DataDefinition dataDefinition = null;
		String dataDefinitionName = null;

		try {
			switch (dataDefinitionType) {

			case 1:
				dataDefinitionName = (String) mainController.getOmnidocsProperties().get("omnidocs.dcPassport");

				dataDefinition = omniService.getDataDefinitionUtility().findDataDefinitionByName(dataDefinitionName);
				
				dataDefinition.getFields().get("Office Code").setIndexValue(batchDetails.getOfficeCode());
				dataDefinition.getFields().get("Office Name").setIndexValue(batchDetails.getOfficeName());
				
				dataDefinition.getFields().get("Prefix").setIndexValue(batchDetails.getPrefix());
			
				dataDefinition.getFields().get("Old Serial").setIndexValue(batchDetails.getSerialOldNumber());
				dataDefinition.getFields().get("Serial").setIndexValue(batchDetails.getSerialNumber());
				
				dataDefinition.getFields().get("First Name").setIndexValue(batchDetails.getFirstName());
				dataDefinition.getFields().get("Second Name").setIndexValue(batchDetails.getSecondName());
				dataDefinition.getFields().get("Third Name").setIndexValue(batchDetails.getThirdName());
				dataDefinition.getFields().get("Family Name").setIndexValue(batchDetails.getFamilyName());
				
				dataDefinition.getFields().get("Year").setIndexValue(batchDetails.getYear());
				dataDefinition.getFields().get("Part").setIndexValue(batchDetails.getPart());
				
				break;

			case 2:
				dataDefinitionName = (String) mainController.getOmnidocsProperties().get("omnidocs.dcCivil");

				dataDefinition = omniService.getDataDefinitionUtility().findDataDefinitionByName(dataDefinitionName);

				dataDefinition.getFields().get("Office Code").setIndexValue(batchDetails.getOfficeCode());				
				dataDefinition.getFields().get("Prefix").setIndexValue(batchDetails.getPrefix());
				dataDefinition.getFields().get("Year").setIndexValue(batchDetails.getYear());
				
				dataDefinition.getFields().get("First Name").setIndexValue(batchDetails.getFirstName());
				dataDefinition.getFields().get("Second Name").setIndexValue(batchDetails.getSecondName());
				dataDefinition.getFields().get("Third Name").setIndexValue(batchDetails.getThirdName());
				dataDefinition.getFields().get("Family Name").setIndexValue(batchDetails.getFamilyName());
				
				break;

			case 3:
				dataDefinitionName = (String) mainController.getOmnidocsProperties().get("omnidocs.dcVital");

				dataDefinition = omniService.getDataDefinitionUtility().findDataDefinitionByName(dataDefinitionName);
				
				dataDefinition.getFields().get("Office Code").setIndexValue(batchDetails.getOfficeCode());				
				dataDefinition.getFields().get("Prefix").setIndexValue(batchDetails.getPrefix());
				dataDefinition.getFields().get("Year").setIndexValue(batchDetails.getYear());
				
				dataDefinition.getFields().get("Folder Class Code").setIndexValue(batchDetails.getFolderClassCode());				
				dataDefinition.getFields().get("Folder Class Text").setIndexValue(batchDetails.getFolderClassText());
				
				break;

			case 4:
				dataDefinitionName = (String) mainController.getOmnidocsProperties().get("omnidocs.dcEmbassiess");

				dataDefinition = omniService.getDataDefinitionUtility().findDataDefinitionByName(dataDefinitionName);
				
				dataDefinition.getFields().get("Office Code").setIndexValue(batchDetails.getOfficeCode());				
				dataDefinition.getFields().get("Prefix").setIndexValue(batchDetails.getPrefix());
				dataDefinition.getFields().get("Year").setIndexValue(batchDetails.getYear());
				dataDefinition.getFields().get("File Type").setIndexValue(batchDetails.getFileType());

				break;

			case 5:
				dataDefinitionName = (String) mainController.getOmnidocsProperties().get("omnidocs.dcVitalNonJordandian");

				dataDefinition = omniService.getDataDefinitionUtility().findDataDefinitionByName(dataDefinitionName);
				
				dataDefinition.getFields().get("Office Code").setIndexValue(batchDetails.getOfficeCode());				
				dataDefinition.getFields().get("Prefix").setIndexValue(batchDetails.getPrefix());
				dataDefinition.getFields().get("Year").setIndexValue(batchDetails.getYear());
				
				dataDefinition.getFields().get("Folder Class Code").setIndexValue(batchDetails.getFolderClassCode());				
				dataDefinition.getFields().get("Folder Class Text").setIndexValue(batchDetails.getFolderClassText());
				
			default:

				throw new Exception();
			}

			mainController.writeLog("Datadefinition Type " + dataDefinitionType + " prepared successfully");

			mainController.writeDBLog(new GeneralLog(processLogID, 3, "INFO",
					"DATADEFINITION TYPE " + dataDefinitionType + " PREPARED SUCCESSFULY"));

		} catch (DataDefinitionException dfe) {

			mainController.writeLog("Unable to prepare datadefinition type" + dataDefinitionType);

			mainController.writeDBLog(new GeneralLog(processLogID, 3, "ERROR",
					"UNABLE TO PREPARE DATADEFINITION TYPE " + dataDefinitionType));

			dfe.printStackTrace();
		}

		return dataDefinition;

	}

	private String encodeText(String text) {

		return new String(text.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);

	}

	private BatchDetails getDataDefinitionFromDB(String recordPrimaryKey) throws Exception {

		BatchDetails batchDetails = new BatchDetails();

		try {

			Connection connection = mainController.getSqlConnectionPoolService().get();

			PreparedStatement ps = connection.prepareStatement(
													"SELECT b.OfficeCode, " + 
													"	oe.OfficeName, " + 
													"	b.FileType, " + 
													"	dbo.GetFileOldSerial(bd.FileNumber, b.FileType) AS OldSerial, " + 
													"	dbo.GetFilePrefix(bd.FileNumber, b.FileType) AS Prefix, " + 
													"	bd.Year, " + 
													"	dbo.GetNewSerial(bd.SerialNumber) AS SerialNumber, " + 
													"	bd.Part, " + 
													"	bd.FirstName, " + 
													"	bd.SecondName, " + 
													"	bd.ThirdName, " + 
													"	bd.FamilyName, " + 
													"	bd.FileNumber, " + 
													"	dbo.GetFolderClassCode(bd.SerialNumber) AS FolderClassCode, " + 
													"	dbo.GetFolderClassText(bd.SerialNumber) AS FolderClassText  " + 
													"FROM Batches b  " + 
													"		INNER JOIN BatchDetails bd  " + 
													"		ON b.Id=bd.BatchId  " + 
													"			INNER JOIN OldOffices oe  " + 
													"			ON b.OldOfficeCode = oe.OfficeCode  " + 
													"WHERE SerialNumber = ?  " + 
													"AND   Part = ?");
			
			ps.setString(1, // recordPrimaryKey.substring(0, recordPrimaryKey.indexOf("%")));
					recordPrimaryKey.contains("%") ? recordPrimaryKey.substring(0, recordPrimaryKey.indexOf("%"))
							: recordPrimaryKey);
			ps.setString(2,
					recordPrimaryKey.contains("%") ? recordPrimaryKey.substring(recordPrimaryKey.indexOf("%") + 1)
							: "1");

			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				batchDetails.setOfficeCode(rs.getString("OfficeCode"));
				batchDetails.setOfficeName(rs.getString("OfficeName"));
				batchDetails.setFileType(rs.getString("FileType"));
				batchDetails.setSerialOldNumber(rs.getString("OldSerial"));
				batchDetails.setPrefix(rs.getString("Prefix"));
				batchDetails.setYear(rs.getString("Year"));
				batchDetails.setSerialNumber(rs.getString("SerialNumber"));
				batchDetails.setPart("Part");
				batchDetails.setFirstName(rs.getString("Firstname"));
				batchDetails.setSecondName(rs.getString("SecondName"));
				batchDetails.setThirdName(rs.getString("ThirdName"));
				batchDetails.setFamilyName(rs.getString("FamilyName"));
				batchDetails.setFileNumber("FileNumber");
				batchDetails.setFolderClassCode(rs.getString("FolderClassCode"));
				batchDetails.setFolderClassText(rs.getString("FolderClassText"));
				
				mainController.writeLog("Metadata fetched up from database successfuly");

				mainController.writeDBLog(
						new GeneralLog(processLogID, 3, "INFO", "METADATA FETCHED UP FROM DATABASE SUCCESSFULY"));

			} else {

				throw new Exception();
			}

		} catch (Exception e) {

			mainController.writeLog("Unable to fetch up data definition from database successfuly");

			mainController.writeDBLog(
					new GeneralLog(processLogID, 3, "ERROR", "UNABLE TO FETCH UP THE METADATA FROM DATABASE"));

			e.printStackTrace();

			throw new Exception("Unable to fetch up the metadata from database successfuly");
		}

		System.out.println(batchDetails.toString());

		return batchDetails;
	}

	private int getProcessLogIdFromDB(String folderName) {

		try {

			Connection connection = mainController.getSqlConnectionPoolService().get();

			PreparedStatement ps = connection.prepareStatement(
					"SELECT LogId FROM ProcessLog WHERE BatchIdentifier = ? and LogTimestamp = (SELECT MAX(LogTimestamp) FROM ProcessLog WHERE BatchIdentifier = ?)");
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
		for (int t = 1; t <= 1; t++) {
			transaction = new Transaction();
			transaction.setTransactionID(t);

			Group group = null;
			for (int g = 1; g <= 1; g++) {
				group = new Group();
				group.setGroupID(61);

				Page page = null;
				for (int p = 1; p <= 500; p++) {
					page = new Page();

					Image image = null;
					for (int i = 1; i <= 2; i++) {
						image = new Image();
						image.setFilename("temp" + imageTempNumber++ + ".tiff");

						page.getImage().add(image);
					}

					group.getPage().add(page);
				}

				transaction.getGroup().add(group);
			}

			batch.getTransaction().add(transaction);

			// NGOHelper.postPOJOToXML(Batch.class,
			// "D:\\mywork-sts\\cspd\\src\\opex\\element\\00020150618 141054 102.xml",
			// batch);
		}

	}

	private static Batch getResponseAsPOJO(Class<Batch> class1, File file) throws Exception {
		return NGOHelper.getResponseAsPOJO(class1, new String(Files.readAllBytes(file.toPath())));
	}

	public boolean existance(OmniService omniService, String folderName) throws Exception {

		List<Folder> folders = omniService.getFolderUtility().findFolderByName(folderName);

		return folders.size() > 0 ? true : false;

	}

	public void injectMainController(MainController mainController) {
		this.mainController = mainController;
	}

}
