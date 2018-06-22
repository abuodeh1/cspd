package etech.omni.utils;

import java.net.Socket;
import java.util.Date;
import java.util.List;

import etech.dms.exception.FolderException;
import etech.dms.util.FolderUtility;
import etech.dms.util.SearchCriteria;
import etech.omni.ngo.NGOConnectCabinet;
import etech.omni.transaction.DMSBroker;
import etech.resource.pool.PoolService;
import omnidocs.pojo.DataDefinition;
import omnidocs.pojo.Field;
import omnidocs.pojo.Folder;

public class OFolderUtility extends FolderUtility<Folder, DataDefinition, Field>{
	
	private NGOConnectCabinet ngoConnectCabinet;

	public OFolderUtility(PoolService<Socket> poolService) {
		super(poolService);
	}

	public OFolderUtility(PoolService<Socket> poolService, NGOConnectCabinet ngoConnectCabinet) {
		super(poolService);
		this.ngoConnectCabinet = ngoConnectCabinet;
	}
	
	public NGOConnectCabinet getNgoConnectCabinet() {
		return ngoConnectCabinet;
	}

	public void setNgoConnectCabinet(NGOConnectCabinet ngoConnectCabinet) {
		this.ngoConnectCabinet = ngoConnectCabinet;
	}

	@Override
	public Folder addFolder(String parentFolderID, Folder folder) throws FolderException {
		
		Socket socket = getPoolService().get();

		folder.setParentFolderIndex(parentFolderID);
		try {
			
			folder = DMSBroker.addFolder(socket, getNgoConnectCabinet(), folder).getFolder();
			
			getPoolService().close(socket);
			
		} catch (Exception e) {
			
			throw new FolderException(e);
			
		}

		return folder;
	}
	
	@Override
	public Folder setFolderProperties(Folder oFolder) throws FolderException {
		
		Socket socket = getPoolService().get();
		
		try {
			
			oFolder = DMSBroker.setFolderProperties(socket, getNgoConnectCabinet(), oFolder).getFolder();
			
			getPoolService().close(socket);
			
		} catch (Exception e) {
			
			throw new FolderException(e);
			
		}

		return oFolder;
	}

	@Override
	public Folder getFolder(String folderID) throws FolderException {

		Socket socket = getPoolService().get();
		
		Folder oFolder = new Folder();
		oFolder.setFolderIndex(folderID);
		
		try {
			
			oFolder = DMSBroker.getFolder(socket, getNgoConnectCabinet(), oFolder).getFolder();
			
			getPoolService().close(socket);
			
		} catch (Exception e) {
			
			throw new FolderException(e);
			
		}

		return oFolder;
	}

	@Override
	public List<Folder> findFolderByName(String parentFolderID, String folderName, boolean inSubfolder)
			throws FolderException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean removeFolder(String folderID) throws FolderException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Folder setFolderProperties(String folderID, DataDefinition dataDefinition) throws FolderException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Folder addFolder(String parentFolderID, String folderName) throws FolderException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Folder> getFolderList(String folderID, boolean withSubfolder) throws FolderException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Folder> findFolderByName(String folderName) throws FolderException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Folder> findFolderByName(String parentFolderID, String folderName) throws FolderException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Folder> findFolderByOwner(String ownerName) throws FolderException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Folder> findFolderByOwner(String parentFolderID, String ownerName) throws FolderException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Folder> findFolderByOwner(String parentFolderID, String ownerName, boolean inSubfolder)
			throws FolderException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Folder> findFolderByCreationDate(Date creationDate) throws FolderException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Folder> findFolderByCreationDate(String parentFolderID, Date creationDate) throws FolderException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Folder> findFolderByCreationDate(String parentFolderID, Date creationDate, boolean inSubfolder)
			throws FolderException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Folder> findFolderByDataDefinition(SearchCriteria searchCriteria) throws FolderException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Folder> findFolderByDataDefinition(String parentFolderID, SearchCriteria searchCriteria)
			throws FolderException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Folder> findFolderByDataDefinition(String parentFolderID, SearchCriteria searchCriteria,
			boolean inSubfolder) throws FolderException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Folder> getTrashContents() throws FolderException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Folder> getFolderAncestor(String folderID) throws FolderException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getFolderAncestorAsString(String folderID) throws FolderException {
		// TODO Auto-generated method stub
		return null;
	}


}
