package etech.omni.transaction;

import java.io.File;
import java.net.Socket;

import ISPack.CPISDocumentTxn;
import ISPack.ISUtil.JPDBRecoverDocData;
import ISPack.ISUtil.JPISException;
import ISPack.ISUtil.JPISIsIndex;
import omnidocs.pojo.Cabinet;
import omnidocs.pojo.Document;

public class ImageServer {
	
	public static JPISIsIndex addDocument(Socket socket, Cabinet cabinet, File file, Document document) throws Exception {
		
		JPISIsIndex jPISIsIndex = new JPISIsIndex();
		
		JPDBRecoverDocData jPDBRecoverDocData = new JPDBRecoverDocData();
		jPDBRecoverDocData.m_cDocumentType = document.getDocumentType();
		jPDBRecoverDocData.m_nDocumentSize = document.getDocumentSize();
		jPDBRecoverDocData.m_sVolumeId = cabinet.getImageVolumeIndex();
		
		try {
			
			CPISDocumentTxn.AddDocument_MT(
					null, 
					socket.getInetAddress().getHostAddress(), 
					(short) socket.getPort(), 
					cabinet.getCabinetName(),
					Short.valueOf(cabinet.getImageVolumeIndex()), 
					file.getAbsolutePath(), 
					jPDBRecoverDocData, 
					"",
					jPISIsIndex);
			
		} catch (NumberFormatException | JPISException e) {
			
			throw new Exception(e.getMessage());
			
		}
		
		return jPISIsIndex;
		
	}

}
