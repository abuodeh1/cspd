package opex.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

import cspd.core.GeneralLog;
import cspd.core.Log;
import cspd.core.ProcessDetailsLog;
import cspd.core.ProcessLog;
import etech.resource.pool.PoolFactory;
import etech.resource.pool.PoolService;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextArea;

public class MainController {

	@FXML private LoggerController loggerTabController;
	@FXML private OpexDirectoryTabController opexDirectoryTabController;
	@FXML private SettingTabController settingTabController;
	
	private PoolService<Connection> sqlConnectionPoolService;
	
	private Properties props;
	
	@FXML public void initialize() {
		
		loggerTabController.injectMainController(this);
		
		opexDirectoryTabController.injectMainController(this);
		
		settingTabController.injectMainController(this);

		String url = getApplicationProperties().getProperty("db.url");
		String user = getApplicationProperties().getProperty("db.user");
		String password = getApplicationProperties().getProperty("db.password");
				
		sqlConnectionPoolService = PoolFactory.newSingleConnection(url, user, password);
		
	}

	public TextArea getLoggerTextArea() {
		
		return loggerTabController.getLoggerTextArea();
		
	}
	
	public Properties getApplicationProperties() {
		
		return settingTabController.getApplicationProperties();
		
	}

	public PoolService<Connection> getSqlConnectionPoolService() {
		return sqlConnectionPoolService;
	}

	public void setSqlConnectionPoolService(PoolService<Connection> sqlConnectionPoolService) {
		this.sqlConnectionPoolService = sqlConnectionPoolService;
	}

	@FXML
	private void handleQuitMenuItem(ActionEvent event){
		
		Platform.exit();
		
		System.exit(0);
		
	}
	
	public void msgAlert(String headerText) {

		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Information");
		alert.setHeaderText(headerText);
		alert.showAndWait();

	}
	
	public void errorAlert(String headerText, Exception e) {

		if (e != null) {
			
		    Alert alert = new Alert(AlertType.ERROR);
		    alert.setTitle("Error");
		    alert.setHeaderText(headerText);
		    alert.setContentText(e.getLocalizedMessage());
		    alert.showAndWait();
		}

	}

	public void writeLog(String msg) {
		
		Task task = new Task<Void>() {

			@Override
			protected Void call() throws Exception {
				getLoggerTextArea().appendText("\n" + msg);
				return null;
			}
		};
		new Thread(task).start();
	}
	
	public int writeDBLog(Log log) {
		
		Connection connection = sqlConnectionPoolService.get();
		
		int logID = 0;
		
		if(log instanceof GeneralLog) {
			
			try {
					ProcessLog processLog = (ProcessLog) log;
					
					String sql = "INSERT INTO ProcessLog(BatchIdentifier, MachineName, StartTime, EndTime, NumberOfDocuments) "
							   + "VALUES ( ?, ?, ?, ?, ?)";
					
					PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
					preparedStatement.setString(1, processLog.getBatchIdentifier());
					preparedStatement.setString(2, processLog.getMachineName());
					preparedStatement.setTimestamp(3, processLog.getStartTime());
					preparedStatement.setTimestamp(4, processLog.getEndTime());
					preparedStatement.setInt(5, processLog.getNumberOfDocuments());
/*					preparedStatement.setBoolean(6, processLog.isUploadedToOmniDocs());
					preparedStatement.setInt(7, processLog.getTimeUploadedToOmniDocs());
					preparedStatement.setBoolean(8, processLog.isUploadedToDocuware());
					preparedStatement.setInt(9, processLog.getTimeUploadedToDocuware());
*/
					ResultSet rs = preparedStatement.executeQuery();
					if(rs.next()) logID = rs.getInt(1);
					
			}catch(SQLException sqle) {
				sqle.printStackTrace();
			}
		}else
		
		if(log instanceof ProcessLog) {
			Task task = new Task<Integer>() {

				@Override
				protected Integer call() throws Exception {
					
					int id = 0;
					try {
					ProcessLog processLog = (ProcessLog) log;
					String sql = "INSERT INTO ProcessLog( BatchIdentifier, MachineName, StartTime, EndTime, NumberOfDocuments) "
							   + "VALUES (?, ?, ?, ?, ?)";
					PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
					//preparedStatement.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
					preparedStatement.setString(1, processLog.getBatchIdentifier());
					preparedStatement.setString(2, processLog.getMachineName());
					preparedStatement.setTimestamp(3, processLog.getStartTime());
					preparedStatement.setTimestamp(4, processLog.getEndTime());
					preparedStatement.setInt(5, processLog.getNumberOfDocuments());
					/*preparedStatement.setBoolean(6, processLog.isUploadedToOmniDocs());
					preparedStatement.setInt(7, processLog.getTimeUploadedToOmniDocs());
					preparedStatement.setBoolean(8, processLog.isUploadedToDocuware());
					preparedStatement.setInt(9, processLog.getTimeUploadedToDocuware());*/

					ResultSet rs = preparedStatement.executeQuery();
					if(rs.next())
						id = rs.getInt(1);
					}catch(Exception e){e.printStackTrace();}
					return id;
				}
			};
			new Thread(task).start();
			try {
				logID = (Integer)task.get();
			} catch (InterruptedException | ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else
		
		if(log instanceof ProcessDetailsLog) {
			Task task = new Task<Integer>() {

				@Override
				protected Integer call() throws Exception {
					
					ProcessDetailsLog processDetailsLog = (ProcessDetailsLog) log;
					String sql = "INSERT INTO ProcessLogDetails(LogId, DocumentName, UploadedToOmniDocs)  "
							   + "VALUES (?, ?, ?)";
					PreparedStatement preparedStatement = connection.prepareStatement(sql);
					preparedStatement.setInt(1, processDetailsLog.getLogID());
					preparedStatement.setString(2, processDetailsLog.getDocumentName());
					preparedStatement.setBoolean(3, processDetailsLog.isUploadedToOmniDocs());
					
					preparedStatement.executeQuery();
					
					return processDetailsLog.getLogID();
				}
			};
			new Thread(task).start();
			try {
				logID = (Integer)task.get();
			} catch (InterruptedException | ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return logID;
	}

	@Override
	public void finalize() throws Exception {
		sqlConnectionPoolService.close();

	}
	
	
	
}


