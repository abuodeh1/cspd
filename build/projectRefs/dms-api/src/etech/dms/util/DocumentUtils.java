package etech.dms.util;

import java.io.File;
import java.util.List;

import etech.dms.exception.DocumentException;

public interface DocumentUtils<T> extends Utility {

	public T addDocument(File file, T document) throws DocumentException;
	
	public void exportDocument(String destination, String documentIndex) throws DocumentException;
	
	public T setDocumentProperties(T document) throws DocumentException;
	
	public List<T> getDocumentList(String parentFolderIndex, boolean withInSubfolder) throws DocumentException;
	
	public boolean removeDocument(String parentFolderIndex, String documentID) throws DocumentException;
}
