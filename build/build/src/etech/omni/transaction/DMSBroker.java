package etech.omni.transaction;

import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.Map;

import com.newgen.dmsapiSV.DMSCallBroker;
import com.newgen.dmsapiSV.DMSInputXml;

import ISPack.ISUtil.JPISIsIndex;
import etech.omni.helper.NGOHelper;
import etech.omni.ngo.NGOAddDocument;
import etech.omni.ngo.NGOAddFolder;
import etech.omni.ngo.NGOChangeFolderProperty;
import etech.omni.ngo.NGOConnectCabinet;
import etech.omni.ngo.NGOGetDataDefProperty;
import etech.omni.ngo.NGOGetFolderProperty;
import etech.omni.ngo.NGOGetIDFromName;
import etech.omni.ngo.NGOResponse;
import omnidocs.pojo.DataDefinition;
import omnidocs.pojo.Document;
import omnidocs.pojo.Field;
import omnidocs.pojo.Folder;

public class DMSBroker {
	
	public static NGOConnectCabinet connectToCabinet(Socket socket, String username, String password, String cabinetName, boolean userExist, String userType) throws Exception {

		String xmlResponse = DMSCallBroker.execute( new DMSInputXml().getConnectCabinetXml(cabinetName, username, password, "", (userExist? "Y": "N"), "0",	userType), socket, 1 );
		
		NGOConnectCabinet ngoConnectCabinet = NGOHelper.getNGOResponseAsPOJO(NGOConnectCabinet.class, xmlResponse);

		if (ngoConnectCabinet.getStatus() < 0 ) {
		
			throw new Exception( getErrorMessage(ngoConnectCabinet) );
		}

		return ngoConnectCabinet;
	}
	
	public static void disconnectCabinet(Socket socket, NGOConnectCabinet ngoConnectCabinet) throws Exception {
		
		String xmlResponse = DMSCallBroker.execute( new DMSInputXml().getDisconnectCabinetXml(ngoConnectCabinet.getCabinet().getCabinetName(), ngoConnectCabinet.getCabinet().getUserDBId()), socket, 1 );
		
		NGOConnectCabinet ngoConnectCabinetRs = NGOHelper.getNGOResponseAsPOJO(NGOConnectCabinet.class, xmlResponse);

		if (ngoConnectCabinetRs.getStatus() < 0 ) {
		
			throw new Exception(getErrorMessage(ngoConnectCabinetRs) );
		}
	}
	
	public static NGOAddDocument addDocument(Socket socket, NGOConnectCabinet ngoConnectCabinet, Document document, JPISIsIndex jPISIsIndex) throws Exception {
	
		String dataDefinitionInputXml = getNGOGetDataDefPropertyInputMsg(document.getDataDefinition());	
		
		String in = new DMSInputXml().getAddDocumentXml(ngoConnectCabinet.getCabinet().getCabinetName(),
				ngoConnectCabinet.getUserDBId(), 
				"0", 
				document.getParentFolderIndex(), 
				String.valueOf(document.getNoOfPages()),
				"I", 
				document.getDocumentName(), 
				document.getCreationDateTime(), 
				document.getExpiryDateTime(),
				document.getVersionFlag(),
				String.valueOf(document.getDocumentType()), 
				String.valueOf(document.getDocumentSize()), 
				document.getCreatedByApp(),
				document.getCreatedByAppName(),
				(jPISIsIndex.m_nDocIndex + "#" + jPISIsIndex.m_sVolumeId),
				document.getTextISIndex(), 
				document.getODMADocumentIndex(),
				document.getComment(),
				document.getAuthor(), //check
				document.getOwnerIndex(),
				document.getEnableLog(),
				document.getFTSFlag(),
				dataDefinitionInputXml,
				null);
		
		String xmlResponse = DMSCallBroker.execute( 

				in,
				socket, 1
				
				);
		
		NGOAddDocument ngoAddDocument = NGOHelper.getNGOResponseAsPOJO(NGOAddDocument.class, xmlResponse);

		if (ngoAddDocument.getStatus() < 0 ) {
		
			throw new Exception( getErrorMessage(ngoAddDocument) );
		}

		return ngoAddDocument;
		
	}
	
	public static NGOGetIDFromName getGetIDFromNameXml(Socket socket, NGOConnectCabinet ngoConnectCabinet, char type, String name) throws Exception {
		
		String xmlResponse = DMSCallBroker.execute( 
				
				new DMSInputXml().getGetIDFromNameXml(ngoConnectCabinet.getCabinet().getCabinetName(), 
													  ngoConnectCabinet.getUserDBId(), 
													  String.valueOf(type), 
													  name, 
													  ""),
				socket, 1
				);
		
		NGOGetIDFromName ngoGetIDFromName = NGOHelper.getNGOResponseAsPOJO(NGOGetIDFromName.class, xmlResponse);

		if (ngoGetIDFromName.getStatus() < 0 ) {
		
			throw new Exception(getErrorMessage(ngoGetIDFromName));
		}

		return ngoGetIDFromName;
	}

	public static NGOAddFolder addFolder(Socket socket, NGOConnectCabinet ngoConnectCabinet, Folder folder) throws Exception {

		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
		LocalDateTime now = LocalDateTime.now();

		String imageVolIndex = String.valueOf(ngoConnectCabinet.getCabinet().getImageVolumeIndex());
		
		String xmlResponse = DMSCallBroker.execute(

				new DMSInputXml().getAddFolderXml(ngoConnectCabinet.getCabinet().getCabinetName(),
												  ngoConnectCabinet.getUserDBId(), 
												  folder.getParentFolderIndex(), 
												  folder.getFolderName(), 
												  now.format(dtf),
												  folder.getAccessType() == null? "S" : folder.getAccessType(), 
												  imageVolIndex, 
												  folder.getFolderType(), 
												  null, 
												  null,
												  folder.getVersionFlag(), 
												  null, 
												  folder.getOwnerIndex(), 
												  null,
												  null,
												  null,//folder.getDataDefinition()==null? null: folder.getDataDefinition().getDataDefinitionName(),//dataDefinitionInputXml, 
												  null, 
												  null,
												  folder.getExpiryDateTime(),
												  null,
												  null,
												  null),

				socket, 1);

		NGOAddFolder ngoAddFolder = NGOHelper.getNGOResponseAsPOJO(NGOAddFolder.class, xmlResponse);

		if (ngoAddFolder.getStatus() < 0) {

			throw new Exception(getErrorMessage(ngoAddFolder));
		}
		
		if(folder.getDataDefinition()!=null) {	
			
			try {
				
				Folder addedFolder = ngoAddFolder.getFolder();
				addedFolder.setDataDefinition(folder.getDataDefinition());
				
				setFolderProperties(socket, ngoConnectCabinet, addedFolder);
				
			}catch(Exception e){
				
				//delete folder
				throw e;
			}
			
		}

		return ngoAddFolder;

	}
	
	public static NGOChangeFolderProperty setFolderProperties(Socket socket, NGOConnectCabinet ngoConnectCabinet, Folder folder) throws Exception {
		
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
		LocalDateTime now = LocalDateTime.now();

		String dfInputMsg = getNGOGetDataDefPropertyInputMsg(folder.getDataDefinition());
		
		String inputMsg = new DMSInputXml().getChangeFolderPropertyXml(
												ngoConnectCabinet.getCabinet().getCabinetName(),
												ngoConnectCabinet.getUserDBId(), 
												now.format(dtf), 
												folder.getAccessType(), 
												folder.getFolderIndex(),
												folder.getFolderName(), 
												folder.getImageVolumeIndex(), 
												folder.getFolderLock(), 
												folder.getLockByUser(),
												null, 
												folder.getVersionFlag(), 
												folder.getComment(), 
												folder.getVersionFlag(),
												dfInputMsg , 
												folder.getOwner(), 
												null, 
												null);
		
		
		String xmlResponse = DMSCallBroker.execute(inputMsg, socket, 1);

		NGOChangeFolderProperty ngoChangeFolderProperty = NGOHelper.getNGOResponseAsPOJO(NGOChangeFolderProperty.class, xmlResponse);

		if (ngoChangeFolderProperty.getStatus() < 0) {

			throw new Exception(getErrorMessage(ngoChangeFolderProperty));
		}

		return ngoChangeFolderProperty;

	}

	public static NGOGetFolderProperty getFolder(Socket socket, NGOConnectCabinet ngoConnectCabinet, Folder folder) throws Exception {

		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
		LocalDateTime now = LocalDateTime.now();
		
		String inputMsg = new DMSInputXml().getGetFolderPropertyXml(
											ngoConnectCabinet.getCabinet().getCabinetName(),
											ngoConnectCabinet.getUserDBId(), 
											now.format(dtf), 
											folder.getFolderIndex(), 
											"Y");

		String xmlResponse = DMSCallBroker.execute(inputMsg, socket, 1);
		
		NGOGetFolderProperty ngoGetFolderProperty = NGOHelper.getNGOResponseAsPOJO(NGOGetFolderProperty.class, xmlResponse);

		if (ngoGetFolderProperty.getStatus() < 0 ) {
		
			throw new Exception(getErrorMessage(ngoGetFolderProperty));
		}

		
		return ngoGetFolderProperty;

	}
	
	public static NGOGetDataDefProperty getGetDataDefPropertyXml(Socket socket, String cabinetName, String userDBID, String dataDefinetionIndex) throws Exception {
		
		String xmlResponse = DMSCallBroker.execute( new DMSInputXml().getGetDataDefPropertyXml(cabinetName, userDBID, dataDefinetionIndex), socket, 1);
		
		NGOGetDataDefProperty ngoGetDataDefProperty = NGOHelper.getNGOResponseAsPOJO(NGOGetDataDefProperty.class, xmlResponse);
		
		if (ngoGetDataDefProperty.getStatus() < 0 ) {
			
			throw new Exception(getErrorMessage(ngoGetDataDefProperty));
		}
		
		return ngoGetDataDefProperty;
	}
	
	private static String getErrorMessage(NGOResponse ngoResponse) {
		
		return ngoResponse.getStatus() + " >> " + ngoResponse.getError();
	}
	
	private static String getNGOGetDataDefPropertyInputMsg(DataDefinition dataDefinition) {
		
		if(dataDefinition == null) {
			
			return null;
			
		}

		String[] fieldNameArray = new String[dataDefinition.getFields().size()];
		
		Map<String, Field> fieldList = dataDefinition.getFields();
		Iterator<String> fieldIter = fieldList.keySet().iterator();
		
		int i = 0;
		while(fieldIter.hasNext()){
			String fieldKey = fieldIter.next();
			fieldNameArray[i++] = fieldList.get(fieldKey).getIndexId() + "#" +  fieldList.get(fieldKey).getIndexType() + "#" +  fieldList.get(fieldKey).getIndexType();
		
		}
		
		return new DMSInputXml().getdataClassAndFieldsXml(dataDefinition.getDataDefName(), dataDefinition.getDataDefIndex(), fieldNameArray);
	}

	

}
