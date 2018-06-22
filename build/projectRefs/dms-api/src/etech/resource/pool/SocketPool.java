package etech.resource.pool;
import java.io.IOException;
import java.net.Socket;

public class SocketPool extends ObjectPool<Socket> implements PoolService<Socket> {

	private String host;
	private int port;

	public SocketPool(String host, int port) {
		super();

		this.host = host;
		this.port = port;
	}

	@Override
	protected Socket create() {
		
		Socket socket = null;
		
		try {
			socket = new Socket(host, port);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return socket;
	}

	@Override
	public boolean validate(Socket socket) {
		
		return socket.isClosed();
	}

	@Override
	public void expire(Socket socket) {
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public Socket get() {
		return checkOut();
	}

	@Override
	public void close() throws Exception {
		throw new Exception("close() not applicable for pool resource.");
	}

	@Override
	public <T> void close(T t) {
		checkIn((Socket)t);		
	}

}
