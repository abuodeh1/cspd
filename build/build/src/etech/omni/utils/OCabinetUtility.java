package etech.omni.utils;

import java.net.Socket;
import java.util.List;

import etech.dms.util.AbstractUtility;
import etech.omni.ngo.NGOConnectCabinet;
import etech.resource.pool.PoolService;
import omnidocs.pojo.Cabinet;
import omnidocs.pojo.Folder;

public class OCabinetUtility extends AbstractUtility {

	private NGOConnectCabinet ngoConnectCabinet;
	
	public OCabinetUtility(PoolService<Socket> poolService, NGOConnectCabinet ngoConnectCabinet) {
		super(poolService);
		this.ngoConnectCabinet = ngoConnectCabinet;
	}

	public Cabinet getCabinet() {

		return ngoConnectCabinet.getCabinet();

	}

	public List<Folder> getCabinetRootFolders() {

		return ngoConnectCabinet.getFolders();

	}

	public String getCabinetUserDBId() {

		return ngoConnectCabinet.getUserDBId();

	}

	public String getCabinetLeftLoginAttempts() {

		return ngoConnectCabinet.getLeftLoginAttempts();

	}
}
