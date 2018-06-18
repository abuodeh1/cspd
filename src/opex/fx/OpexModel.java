package opex.fx;

import java.io.File;
import java.io.StringReader;
import java.util.Iterator;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import etech.dms.exception.DocumentException;
import etech.dms.exception.FolderException;
import etech.omni.OmniService;
import omnidocs.pojo.DataDefinition;
import omnidocs.pojo.Document;
import omnidocs.pojo.Folder;
import opex.element.Batch;
import opex.element.Batch.Transaction;
import opex.element.Batch.Transaction.Group;
import opex.element.Batch.Transaction.Group.Page;

public class OpexModel {

	OmniService omniService; 
	Batch batch;
	
	public OpexModel(Batch batch) {
		
		/*omniService = new OmniService("192.168.60.148", 3333, true);
		try {
			omniService.openCabinetSession("mabuodeh", "supervisor351", "jlgccab1", false, "S");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
	}
	
	public DataDefinition prepareDataDefinition(OmniService omniService, int dataDefinitionType, long fileID) {
		
		DataDefinition oDataDefinition = null;
		
		switch (dataDefinitionType) {

			case 1:
				oDataDefinition = omniService.getDataDefinitionUtility().getDataDefinition("myDF");
				oDataDefinition.getFields().get("barcode").setIndexValue(String.valueOf(fileID));
	
				break;
	
			case 2:
				oDataDefinition = omniService.getDataDefinitionUtility().getDataDefinition("myDF1");
				oDataDefinition.getFields().get("barcode").setIndexValue(String.valueOf(fileID));
				
				break;
	
			case 3:
				oDataDefinition = omniService.getDataDefinitionUtility().getDataDefinition("myDF2");
				oDataDefinition.getFields().get("barcode").setIndexValue(String.valueOf(fileID));
	
				break;
	
			case 4:
				oDataDefinition = omniService.getDataDefinitionUtility().getDataDefinition("myDF3");
				oDataDefinition.getFields().get("barcode").setIndexValue(String.valueOf(fileID));
	
				break;
	
			default:

		}
		
		return oDataDefinition;

	}
	
	public void getDataDefinitionFromDB(String recordPrimaryKey) {
		
		
	}
	
	public void setBatch(Batch batch) {
		this.batch = batch;
	}
	
	public static <T> T getResponseAsPOJO(Class<T> classType, String xmlResponse) {

		T ngoResponse = null;

		try {
			
			StringReader stringReader = new StringReader(xmlResponse);

			JAXBContext jaxbContext = JAXBContext.newInstance(classType);

			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			
			ngoResponse = (T) jaxbUnmarshaller.unmarshal(stringReader);
			
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		
		return ngoResponse;
	}

	public void uploadDocumentsToOmnidocs() throws Exception {

		String parentFolderId = "61507";
		
		String scanFolderPath = batch.getImageFilePath();

		// fetch metadata from database from index per file

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
				
				Folder folder = new Folder();
				folder.setFolderName(String.valueOf(fileID));
				folder.setParentFolderIndex(parentFolderId);
				folder.setDataDefinition(prepareDataDefinition(omniService, dataDefinitionType, fileID));
				
				Folder addedFolder = omniService.getFolderUtility().addFolder(parentFolderId, folder);
				
				Iterator<Page> pages = group.getPage().iterator();
				
				while (pages.hasNext()) {
					
					Page page = pages.next();
					page.getImage().stream().forEach(image -> {
						Document document = new Document();
						document.setParentFolderIndex(addedFolder.getParentFolderIndex());
						document.setDocumentName(image.getFilename());
													
						try {
							omniService.getDocumentUtility().addDocument(new File(scanFolderPath + System.getProperty("file.separator") + image.getFilename()), document);
						} catch (DocumentException e) {
							e.printStackTrace();
						}
					});
					
				}
			}
		}
		
	}




}
