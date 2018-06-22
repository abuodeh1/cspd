package etech.omni;

import java.net.Socket;

import etech.dms.AbstractDmsService;
import etech.dms.util.CabinetUtility;
import etech.omni.ngo.NGOConnectCabinet;
import etech.omni.transaction.DMSBroker;
import etech.omni.utils.ODataDefinitionUtility;
import etech.omni.utils.ODocumentUtility;
import etech.omni.utils.OFolderUtility;
import etech.resource.pool.PoolFactory;
import etech.resource.pool.PoolService;
import omnidocs.pojo.DataDefinition;
import omnidocs.pojo.Field;
import omnidocs.pojo.Folder;


public class OmniService extends AbstractDmsService<Folder, DataDefinition, Field> {
	
	private NGOConnectCabinet ngoConnectCabinet = null;
	
	private PoolService<Socket> poolService;
	
	public OmniService(String host, int port, boolean singleSocket) {
		
		poolService = singleSocket? PoolFactory.newSingleSocket(host, port) : PoolFactory.newPoolSocket(host, port);
		
	}
	
	public void openCabinetSession(String username, String password, String cabinetName, boolean userExist,	String userType) throws Exception {

		Socket socket = poolService.get();
		
		if (ngoConnectCabinet != null) {

			DMSBroker.disconnectCabinet(socket, ngoConnectCabinet);

			ngoConnectCabinet = null;

		}

		ngoConnectCabinet = DMSBroker.connectToCabinet(socket, username, password, cabinetName, userExist, userType);

		poolService.close(socket);

	}

	@Override
	public OFolderUtility getFolderUtility() {
		return new OFolderUtility(poolService, ngoConnectCabinet);
	}

	@Override
	public ODocumentUtility getDocumentUtility() {
		return new ODocumentUtility(poolService, ngoConnectCabinet);
	}

	@Override
	public ODataDefinitionUtility getDataDefinitionUtility() {
		return new ODataDefinitionUtility(poolService, ngoConnectCabinet);
	}

	@Override
	public CabinetUtility getCabinetUtility() {
		// TODO Auto-generated method stub
		return null;
	}

}
