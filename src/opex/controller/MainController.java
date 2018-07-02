package opex.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

import cspd.core.GeneralLog;
import cspd.core.Log;
import cspd.core.ProcessDetailsLog;
import cspd.core.ProcessLog;
import etech.omni.OmniService;
import etech.resource.pool.PoolFactory;
import etech.resource.pool.PoolService;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class MainController {

	@FXML private LoggerController loggerTabController;
	@FXML private OpexDirectoryTabController opexDirectoryTabController;
	@FXML private SettingTabController settingTabController;
	@FXML private SyncTabController syncTabController;
	
	private OmniService omniService;
	
	private PoolService<Connection> sqlConnectionPoolService;
	private PoolService<Connection> oracleConnectionPoolService;
	
	private Properties props;
	
	@FXML public void initialize() {
		
		loggerTabController.injectMainController(this);
		
		opexDirectoryTabController.injectMainController(this);
		
		settingTabController.injectMainController(this);
		
		syncTabController.injectMainController(this);
		
		String url = getApplicationProperties().getProperty("db.url");
		String user = getApplicationProperties().getProperty("db.user");
		String password = getApplicationProperties().getProperty("db.password");
		String driver = "net.sourceforge.jtds.jdbc.Driver";
		
		sqlConnectionPoolService = PoolFactory.newSingleConnection(driver, url, user, password);

	}

	public TextArea getLoggerTextArea() {
		
		return loggerTabController.getLoggerTextArea();
		
	}
	
	public Properties getApplicationProperties() {
		
		return settingTabController.getApplicationProperties();
		
	}

	
	public OmniService getOmniService() throws Exception{
		
		try {
			String host = getApplicationProperties().getProperty("omnidocs.host");
			String port = getApplicationProperties().getProperty("omnidocs.port");
			String cabinet = getApplicationProperties().getProperty("omnidocs.cabinet");
			String username = getApplicationProperties().getProperty("omnidocs.omniUser");
			String password = getApplicationProperties().getProperty("omnidocs.omniUserPassword");
			
			if( (host == null || host.trim().length() == 0) && 
					(port == null || port.trim().length() == 0) && 
							(cabinet == null || cabinet.trim().length() == 0) && 
								(username == null || username.trim().length() == 0) && 
									(password == null || password.trim().length() == 0)) {
				
				throw new Exception ("Omnidocs Settings Problem, Please check the settings.");
	
			}
			
			omniService = new OmniService(host, Integer.valueOf(port), true);
			
			omniService.openCabinetSession(username, password, cabinet, false, "S");
		
		} catch (Exception e) {
			
			e.printStackTrace();
			
			throw new Exception ("Omnidocs Communication Problem");

		}
		
		return omniService;
	}

	public void setOmniService(OmniService omniService) {
		this.omniService = omniService;
	}

	public PoolService<Connection> getSqlConnectionPoolService() throws Exception{
		
		String url = settingTabController.getApplicationProperties().getProperty("db.url");
		String user = settingTabController.getApplicationProperties().getProperty("db.user");
		String password = settingTabController.getApplicationProperties().getProperty("db.password");
		String driver = "net.sourceforge.jtds.jdbc.Driver";
		try {
			
			if( (url == null || url.trim().length() == 0) && 
					(user == null || user.trim().length() == 0) && 
							(password == null || password.trim().length() == 0) ) {
				
				throw new Exception ("Database Settings Problem, Please check the settings.");
				
			}
			
			sqlConnectionPoolService = PoolFactory.newSingleConnection(driver, url, user, password);

		}catch(Exception e) {
			
			e.printStackTrace();
			
			throw new Exception ("Unable to create a connection");

		}
		
		return sqlConnectionPoolService;
	}

	public void setSqlConnectionPoolService(PoolService<Connection> sqlConnectionPoolService) {
		this.sqlConnectionPoolService = sqlConnectionPoolService;
	}
	
	public PoolService<Connection> getOracleConnectionPoolService() throws Exception{
		
		String url = "jdbc:oracle:thin:@192.168.60.93:1521:etech11g";//settingTabController.getApplicationProperties().getProperty("db.url");
		String user = "jlgccab1";//settingTabController.getApplicationProperties().getProperty("db.user");
		String password = "jlgccab1";//settingTabController.getApplicationProperties().getProperty("db.password");
		String driver = "oracle.jdbc.driver.OracleDriver";
		try {
			
			if( (url == null || url.trim().length() == 0) && 
					(user == null || user.trim().length() == 0) && 
							(password == null || password.trim().length() == 0) ) {
				
				throw new Exception ("Database Settings Problem, Please check the settings.");
				
			}
			
			sqlConnectionPoolService = PoolFactory.newSingleConnection(driver, url, user, password);

		}catch(Exception e) {
			
			e.printStackTrace();
			
			throw new Exception ("Unable to create a connection");

		}
		
		return sqlConnectionPoolService;
	}

	public void setOracleConnectionPoolService(PoolService<Connection> oracleConnectionPoolService) {
		this.oracleConnectionPoolService = oracleConnectionPoolService;
	}

	@FXML
	private void handleQuitMenuItem(ActionEvent event){
		
		Platform.exit();
		
		System.exit(0);
		
	}
	
	@FXML
	private void handleAboutMenuItem(ActionEvent event){
		
		Image image = new Image(getClass().getResourceAsStream("../fx/icon.jpg"), 70, 70, true, true);
		Label label = new Label("Civil Status and Passport Department", new ImageView(image));
		
		HBox hBox = new HBox();
		hBox.setAlignment(Pos.CENTER);
		hBox.getChildren().add(label);
		
		Stage aboutStage = new Stage();
		
		Scene sence = new Scene(hBox);
		
		aboutStage.setScene(sence);
		aboutStage.setHeight(200);
		aboutStage.setWidth(400);
		
		aboutStage.initModality(Modality.APPLICATION_MODAL);
		aboutStage.setTitle("About Me");
		aboutStage.setIconified(false);
		
		aboutStage.showAndWait();
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
				try {
					getLoggerTextArea().appendText("\n" + msg);
				}catch(Exception e) {
					e.printStackTrace();
				}
				return null;
			}
		};
		new Thread(task).start();
	}
	
	public int writeDBLog(Log log) {
		
		Connection connection = sqlConnectionPoolService.get();
		
		int logID = 0;
		
		if(log instanceof GeneralLog) {
			
			Task task = new Task<Void>() {

				@Override
				protected Void call() throws Exception {
					
					try {
						GeneralLog generalLog = (GeneralLog) log;
						
						String sql = "INSERT INTO GeneralLog(LogID, LogPriority, LogSeverity, LogMessage) "
								   + "VALUES ( ?, ?, ?, ?)";
						
						PreparedStatement preparedStatement = connection.prepareStatement(sql);
						preparedStatement.setInt(1, generalLog.getLogID());
						preparedStatement.setInt(2, generalLog.getLogPriority());
						preparedStatement.setString(3, generalLog.getLogSeverity());
						preparedStatement.setString(4, generalLog.getLogMessage());
						
						preparedStatement.execute();
						
					}catch(Exception sqle) {
						sqle.printStackTrace();
					}
					
					return null;
					
				}
			};
			new Thread(task).start();
			
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
						preparedStatement.setString(1, processLog.getBatchIdentifier());
						preparedStatement.setString(2, processLog.getMachineName());
						preparedStatement.setTimestamp(3, processLog.getStartTime());
						preparedStatement.setTimestamp(4, processLog.getEndTime());
						preparedStatement.setInt(5, processLog.getNumberOfDocuments());
	
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
			Task task = new Task<Void>() {

				@Override
				protected Void call() throws Exception {
					try {
						ProcessDetailsLog processDetailsLog = (ProcessDetailsLog) log;
						String sql = "INSERT INTO ProcessLogDetails(LogId, DocumentName, UploadedToOmniDocs)  "
								   + "VALUES (?, ?, ?)";
						PreparedStatement preparedStatement = connection.prepareStatement(sql);
						preparedStatement.setInt(1, processDetailsLog.getLogID());
						preparedStatement.setString(2, processDetailsLog.getDocumentName());
						preparedStatement.setBoolean(3, processDetailsLog.isUploadedToOmniDocs());
						
						preparedStatement.execute();
						
					}catch(SQLException sqle) {
						sqle.printStackTrace();
					}
				
					return null;
				}
			};
			new Thread(task).start();
			
		}

		return logID;
	}

}


