package etech.resource.pool;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SingleConnection implements PoolService<Connection> {

	private Connection connection;

	public SingleConnection(String url, String username, String password) {
		
		try {
			Class.forName("net.sourceforge.jtds.jdbc.Driver");
			connection = DriverManager.getConnection(url, username, password);
		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void close() throws Exception {
		connection.close();
	}

	@Override
	public Connection get() {
		return connection;
	}

	@Override
	public <T> void close(T t) {
		// TODO Auto-generated method stub

	}

}
