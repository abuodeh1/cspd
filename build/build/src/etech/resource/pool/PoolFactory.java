package etech.resource.pool;

import java.net.Socket;
import java.sql.Connection;

public class PoolFactory {

	public static boolean SINGLE_RESOURCE;
	
	public static PoolService<Socket> newSingleSocket(String host, int port) {
		
		SINGLE_RESOURCE = true;
		
		try {
			
			return new SingleSocket(host, port);
			
		} catch (Exception e) {
		
			e.printStackTrace();
			
		}

		return null;
	}

	public static PoolService<Socket> newPoolSocket(String host, int port) {
		
		SINGLE_RESOURCE = false;
		
		return new SocketPool(host, port);
		
	}

	public static PoolService<Connection> newSingleConnection(String url, String username, String password){
		
		//SINGLE_RESOURCE = true;
		
		try {
			
			return new SingleConnection(url, username, password);
			
		} catch (Exception e) {
		
			e.printStackTrace();
			
		}

		return null;
	}

	public static PoolService<Connection> newPoolConnection(String url, String username, String password) {
		
		return new ConnectionPool(url, username, password);
		
	}
	
	public static boolean isSingleResource(PoolService poolService) {
		
		if(poolService instanceof SingleSocket || poolService instanceof SingleConnection) {
			
			return true;
			
		}
		
		return false;
	}
}
