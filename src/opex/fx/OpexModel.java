package opex.fx;

import java.io.File;
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
import etech.dms.util.DocumentUtility;
import etech.omni.OmniService;
import etech.omni.core.DataDefinition;
import etech.omni.core.Document;
import etech.omni.core.Folder;
import etech.omni.helper.NGOHelper;
import etech.resource.pool.PoolFactory;
import etech.resource.pool.PoolService;
import opex.element.Batch;
import opex.element.Batch.Transaction;
import opex.element.Batch.Transaction.Group;
import opex.element.Batch.Transaction.Group.Page;
import opex.element.Batch.Transaction.Group.Page.Image;

public class OpexModel {

	Batch batch;
	
	public OpexModel(Batch batch) {
		this.batch = batch;		//String folderDestination = "D:\\temp1\\";
	}

	public void uploadDocumentsToOmnidocs(String filePath) throws Exception {

		String parentFolderID = "116";
		
		OmniService omniService = new OmniService("192.168.60.148", 3333, true);
		omniService.openCabinetSession("mabuodeh", "etech123", "jlgccab1", false, "S");

		// read opex xml

		Batch batch = NGOHelper.getResponseAsPOJO(Batch.class, new String(Files.readAllBytes(new File(filePath).toPath())));
		String scanFolderPath = batch.getImageFilePath();

		// fetch metadata from database from index per file

		long startDate = System.currentTimeMillis();

		Iterator<Transaction> transactions = batch.getTransaction().iterator();

		while (transactions.hasNext()) {

			Transaction transaction = transactions.next();

			Iterator<Group> groups = transaction.getGroup().iterator();

			while (groups.hasNext()) {

				Group group = groups.next();
				long fileID = group.getGroupID();

				// call database and get db row
				// select datadefinition according type field
				int dataDefinitionType = 1;

				String serialNumber = "018/01/00000" + String.valueOf(fileID);

				Folder folder = new Folder();
				folder.setFolderName(serialNumber);
				folder.setParentFolderIndex(parentFolderID);
				
				List<Folder> folderRs = omniService.getFolderUtility().findFolderByName(parentFolderID, folder.getFolderName());
				Folder searchFolderRs = folderRs.size() > 0 ? folderRs.get(0): null;

				if(searchFolderRs != null) {
					System.out.println("Folder ( " + serialNumber + " ) already added before in ( " + searchFolderRs.getCreationDateTime() + " ).");
					continue;
				}
				
				folder.setDataDefinition(prepareDataDefinition(omniService, dataDefinitionType, serialNumber));
				
				Folder addedFolder = omniService.getFolderUtility().addFolder(parentFolderID, folder);

				Iterator<Page> pages = group.getPage().iterator();
				
				while (pages.hasNext()) {

					Page page = pages.next();
					page.getImage().stream().forEach(image -> {
						Document document = new Document();
						document.setParentFolderIndex(addedFolder.getFolderIndex());
						document.setDocumentName(image.getFilename());

						try {
							omniService.getDocumentUtility().addDocument(new File(
									scanFolderPath + System.getProperty("file.separator") + image.getFilename()),
									document);
						} catch (DocumentException e) {
							e.printStackTrace();
						}
					});

				}
			}
		}

		System.out.println( "Time Completed with: " + TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - startDate));
		
		omniService.complete();

	}

	private void exportTaskWithoutSubfolder(String folderID, String folderDestination) throws Exception {

		OmniService omniService = new OmniService("192.168.60.148", 3333, true);
		omniService.openCabinetSession("mabuodeh", "etech123", "jlgccab1", false, "S");

		long startDate = System.currentTimeMillis();

		List<Folder> folders = new ArrayList<>();
		folders.add( omniService.getFolderUtility().getFolder(folderID) );

		exportTaskFoldersWithDocuments(omniService, folders, folderDestination);
		
		System.out.println("exportTaskWithoutSubfolder Task Completed with: " + TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - startDate) + " s");
		
		omniService.complete();
	}
	
	private void exportTaskWithSubfolder(String folderID, String folderDestination) throws Exception {
		
		OmniService omniService = new OmniService("192.168.60.148", 3333, true);
		omniService.openCabinetSession("mabuodeh", "etech123", "jlgccab1", false, "S");

		long startDate = System.currentTimeMillis();

		List<Folder> folders = omniService.getFolderUtility().getFolderList(folderID, true);
		
		exportTaskFoldersWithDocuments(omniService, folders, folderDestination);
		
		System.out.println("exportTaskWithSubfolder Task Completed with: " + TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - startDate) + " s");
		
		omniService.complete();
	}	
	
	private void exportTaskFoldersWithDocuments(OmniService omniService, List<Folder> folders, String folderDestination) throws Exception {
	
		long startDate = System.currentTimeMillis();

		DocumentUtility<Document> documentUtility = omniService.getDocumentUtility();
		folders.stream().forEach(folder -> {
			try {
				
				List<Document> documents = documentUtility.getDocumentList(folder.getFolderIndex(), false);
				documents.stream().forEach(document -> {
					try {
						String nextDest = folderDestination + resolveInvlidFilenameChars(folder.getFolderName());
						File file = new File(nextDest);
						if(!file.exists()){
							file.mkdirs();
						}
						documentUtility.exportDocument(nextDest + "\\" + document.getDocumentName(),
								document.getISIndex().substring(0, document.getISIndex().indexOf('#')));
					} catch (DocumentException e) {
						e.printStackTrace();
					}
				});
				
			} catch (DocumentException e) {
				e.printStackTrace();
			}
		});
		
		System.out.println("exportTaskFoldersWithDocuments Task Completed with: " + TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - startDate)+ " s");

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

	private DataDefinition prepareDataDefinition(OmniService omniService, int dataDefinitionType, String fileID) {

		BatchDetails batchDetails = getDataDefinitionFromDB(fileID);

		DataDefinition dataDefinition = null;

		switch (dataDefinitionType) {

		case 1:
			dataDefinition = omniService.getDataDefinitionUtility().getDataDefinition("passport");
			dataDefinition.getFields().get("Holder Name").setIndexValue(batchDetails.getName());
			dataDefinition.getFields().get("Old Folder Number").setIndexValue(batchDetails.getFileNumber());
			dataDefinition.getFields().get("New Folder Number").setIndexValue(batchDetails.getSerialNumber());
			dataDefinition.getFields().get("Office Name").setIndexValue(batchDetails.getSerialNumber().substring(batchDetails.getSerialNumber().lastIndexOf('/') + 1));
			dataDefinition.getFields().get("Document Type").setIndexValue(batchDetails.getSerialNumber().substring(batchDetails.getSerialNumber().indexOf('/') + 1, batchDetails.getSerialNumber().lastIndexOf('/')));
			dataDefinition.getFields().get("Year").setIndexValue(batchDetails.getYear());

			break;

		case 2:
			dataDefinition = omniService.getDataDefinitionUtility().getDataDefinition("civil");
			dataDefinition.getFields().get("status").setIndexValue(batchDetails.getName());

			break;

		case 3:
			dataDefinition = omniService.getDataDefinitionUtility().getDataDefinition("vital");
			dataDefinition.getFields().get("barcode").setIndexValue(String.valueOf(fileID));

			break;

		case 4:
			dataDefinition = omniService.getDataDefinitionUtility().getDataDefinition("embassiess");
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
			
			NGOHelper.postPOJOToXML(Batch.class, "D:\\mywork-sts\\cspd\\src\\opex\\element\\00020150618 141054 102.xml", batch);
		}
		
	}

	public void setBatch(Batch batch) {
		this.batch = batch;
	}

	public static Batch getResponseAsPOJO(Class<Batch> class1, File file) throws Exception {
		return NGOHelper.getResponseAsPOJO(class1, new String(Files.readAllBytes(file.toPath())));
	}

	public boolean existance(String folderName) throws Exception {
		OmniService omniService = new OmniService("192.168.60.148", 3333, true);
		omniService.openCabinetSession("mabuodeh", "etech123", "jlgccab1", false, "S");

		List<Folder> folders = omniService.getFolderUtility().findFolderByName(folderName);
		
		omniService.complete();
		
		return folders.size() > 0? true: false;
		
	}

}
