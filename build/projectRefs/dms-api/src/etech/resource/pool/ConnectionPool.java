package etech.resource.pool;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionPool extends ObjectPool<Connection> implements PoolService<Connection> {

	private String url, username, password;
	
	public ConnectionPool(String url, String username, String password) {
		this.url = url;
		this.username = username;
		this.password = password;
		
		try {
			Class.forName("net.sourceforge.jtds.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void close() throws Exception {
		throw new Exception("close() not applicable for pool resource.");
	}

	@Override
	public Connection get() {
		return checkOut();
	}

	@Override
	protected Connection create() {

		try {
			return DriverManager.getConnection(url, username, password);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return null;
	}

	@Override
	public boolean validate(Connection o) {
		try {
			return o.isClosed();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public void expire(Connection o) {

		try {
			o.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public <T> void close(T t) {
		checkIn((Connection)t);

	}

}
