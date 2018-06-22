package etech.omni.utils;

import java.io.File;
import java.net.Socket;
import java.util.List;

import ISPack.ISUtil.JPISIsIndex;
import etech.dms.exception.DocumentException;
import etech.dms.util.DocumentUtility;
import etech.omni.ngo.NGOConnectCabinet;
import etech.omni.transaction.DMSBroker;
import etech.omni.transaction.ImageServer;
import etech.resource.pool.PoolService;
import omnidocs.pojo.Document;

public class ODocumentUtility extends DocumentUtility<Document> {

	private NGOConnectCabinet ngoConnectCabinet;
	
	public ODocumentUtility(PoolService<Socket> poolService, NGOConnectCabinet ngoConnectCabinet) {
		super(poolService);
		this.ngoConnectCabinet = ngoConnectCabinet;
	}

	@Override
	public Document addDocument(File file, Document document) throws DocumentException {
		
		Document Document = (Document) document;
		
		Socket socket = getPoolService().get();
		
		String absolutePath = file.getAbsolutePath();
		String documentExt = absolutePath.substring(absolutePath.indexOf('.')+1);
		
		if ((documentExt.equalsIgnoreCase("tif")) || (documentExt.equalsIgnoreCase("tiff")) || (documentExt.equalsIgnoreCase("bmp"))
				|| (documentExt.equalsIgnoreCase("jpeg")) || (documentExt.equalsIgnoreCase("jpg"))
				|| (documentExt.equalsIgnoreCase("gif"))) {
			Document.setDocumentType('I');
		} else {
			Document.setDocumentType('N');
		}

		Document.setDocumentSize((int) file.length());
		
		Document.setCreatedByAppName(documentExt);

		Document documentRs = null;
		
		try {
			
			JPISIsIndex jPISIsIndex = ImageServer.addDocument(socket, ngoConnectCabinet.getCabinet(), file, Document);
			
			documentRs = DMSBroker.addDocument(socket, ngoConnectCabinet, Document, jPISIsIndex).getDocument();
			
			getPoolService().close(socket);
			
		}catch(Exception e) {
			
			throw new DocumentException(e);
		}
		
		return documentRs;
		
		
	}

	@Override
	public Document setDocumentProperties(Document document) throws DocumentException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void exportDocument(String destination, String documentIndex) throws DocumentException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<Document> getDocumentList(String parentFolderIndex, boolean withInSubfolder) throws DocumentException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean removeDocument(String parentFolderIndex, String documentID) throws DocumentException {
		// TODO Auto-generated method stub
		return false;
	}



}
