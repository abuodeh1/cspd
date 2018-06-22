package etech.omni.utils;

import java.net.Socket;

import etech.dms.util.DataDefinitionUtility;
import etech.omni.ngo.NGOConnectCabinet;
import etech.omni.transaction.DMSBroker;
import etech.resource.pool.PoolService;
import omnidocs.pojo.DataDefinition;

public class ODataDefinitionUtility extends DataDefinitionUtility<DataDefinition> {

	private NGOConnectCabinet ngoConnectCabinet;

	public ODataDefinitionUtility(PoolService<?> poolService, NGOConnectCabinet ngoConnectCabinet) {
		super(poolService);
		this.ngoConnectCabinet = ngoConnectCabinet;
	}

	public DataDefinition getDataDefinition(String dataDefinitionName) {

		Socket socket = getPoolService().get();
		
		DataDefinition dataDefinition = null;

		try {
			String dataDefinitionIndex = DMSBroker.getGetIDFromNameXml(
														socket, 
														ngoConnectCabinet, 
														'X', 
														dataDefinitionName)
												  .getObjectIndex();

			dataDefinition = DMSBroker.getGetDataDefPropertyXml(
											socket,
											ngoConnectCabinet.getCabinet().getCabinetName(), 
											ngoConnectCabinet.getUserDBId(),
											dataDefinitionIndex)
									  .getDataDefinition();
			
			getPoolService().close(socket);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return dataDefinition;
	}

}
