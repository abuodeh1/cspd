package etech.dms.util;

import java.util.Date;
import java.util.List;

import etech.dms.exception.FolderException;

public interface FolderUtils<T, D, F> extends Utility {
	
	public T addFolder(String parentFolderID, T folder) throws FolderException;
	
	public T addFolder(String parentFolderID, String folderName) throws FolderException;
	
	public T setFolderProperties(T folder) throws FolderException;
	
	public T setFolderProperties(String folderID, D dataDefinition) throws FolderException;
	
	public T getFolder(String folderID) throws FolderException;
	
	public List<T> getFolderList(String folderID, boolean withSubfolder) throws FolderException;
	
	public List<T> findFolderByName(String folderName) throws FolderException;
	
	public List<T> findFolderByName(String parentFolderID, String folderName) throws FolderException;
	
	public List<T> findFolderByName(String parentFolderID, String folderName, boolean inSubfolder) throws FolderException;
	
	public List<T> findFolderByOwner(String ownerName) throws FolderException;
	
	public List<T> findFolderByOwner(String parentFolderID, String ownerName) throws FolderException;
	
	public List<T> findFolderByOwner(String parentFolderID, String ownerName, boolean inSubfolder) throws FolderException;
	
	public List<T> findFolderByCreationDate(Date creationDate) throws FolderException;
	
	public List<T> findFolderByCreationDate(String parentFolderID, Date creationDate) throws FolderException;
	
	public List<T> findFolderByCreationDate(String parentFolderID, Date creationDate, boolean inSubfolder) throws FolderException;
	
	public List<T> findFolderByDataDefinition(SearchCriteria searchCriteria) throws FolderException;
	
	public List<T> findFolderByDataDefinition(String parentFolderID, SearchCriteria searchCriteria) throws FolderException;
	
	public List<T> findFolderByDataDefinition(String parentFolderID, SearchCriteria searchCriteria, boolean inSubfolder) throws FolderException;
	
	public boolean removeFolder(String folderID) throws FolderException;
	
	public List<T> getTrashContents() throws FolderException;
	
	public List<T> getFolderAncestor(String folderID) throws FolderException;
	
	public String getFolderAncestorAsString(String folderID) throws FolderException;
}
