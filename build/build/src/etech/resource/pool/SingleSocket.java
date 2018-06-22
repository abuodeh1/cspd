package etech.resource.pool;

import java.net.Socket;

public class SingleSocket implements PoolService<Socket> {

	private Socket socket;
	
	public SingleSocket(String host, int port) throws Exception {
		socket = new Socket(host, port);
	}

	@Override
	public void close() throws Exception {
		
		socket.close();
		
	}

	@Override
	public Socket get() {
		return socket;
	}

	@Override
	public <T> void close(T t) {
		
	}

	
}
