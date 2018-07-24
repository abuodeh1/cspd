package opex.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

import cspd.core.GeneralLog;
import cspd.core.Log;
import cspd.core.OpexFolderReport;
import cspd.core.ProcessDetailsLog;
import cspd.core.ProcessLog;
import etech.omni.OmniService;
import etech.resource.pool.PoolFactory;
import etech.resource.pool.PoolService;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.print.PrinterJob;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class MainController {

	@FXML private LoggerController loggerTabController;
	@FXML private OpexDirectoryTabController opexDirectoryTabController;
	@FXML private SyncTabController syncTabController;
	
	private OmnidocsSettingsController omnidocsSettingsController;
	private CSPDSettingsController cSPDSettingsController;
	
	private Parent omnidocsSettingsPanel;
	private Parent cspdSettingsPanel;
	
	private OmniService omniService;
	
	private PoolService<Connection> cspdConnectionPoolService;
	private PoolService<Connection> omniConnectionPoolService;
	
	private List<OpexFolderReport> opexFolderReport = new ArrayList<>();
	
	@FXML public void initialize() {
		
		loggerTabController.injectMainController(this);
		
		opexDirectoryTabController.injectMainController(this);
		
		FXMLLoader omnidocsSettingsLoader = new FXMLLoader(getClass().getResource("/opex/fx/OmnidocsSettings.fxml"));
		FXMLLoader cspdSettingsLoader = new FXMLLoader(getClass().getResource("/opex/fx/CSPDSettings.fxml"));
		
		try {
			omnidocsSettingsPanel = omnidocsSettingsLoader.load();
			omnidocsSettingsController = (OmnidocsSettingsController)omnidocsSettingsLoader.getController();
			
			cspdSettingsPanel = cspdSettingsLoader.load();
			cSPDSettingsController = (CSPDSettingsController)cspdSettingsLoader.getController();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		omnidocsSettingsController.injectMainController(this);
		
		cSPDSettingsController.injectMainController(this);
		
		syncTabController.injectMainController(this);
		
		String url = getCSPDProperties().getProperty("db.url");
		String user = getCSPDProperties().getProperty("db.user");
		String password = getCSPDProperties().getProperty("db.password");
		String driver = "net.sourceforge.jtds.jdbc.Driver";
		
		try {
			
			cspdConnectionPoolService = PoolFactory.newSingleConnection(driver, url, user, password);
			
		} catch (Exception e) {
			
			errorAlert("Please revice connection settings", new Exception("The required conncetion not prepared properly"));
		}

	}
	
	public void buildCSPDConnection() {
		
		String url = getCSPDProperties().getProperty("db.url");
		String user = getCSPDProperties().getProperty("db.user");
		String password = getCSPDProperties().getProperty("db.password");
		String driver = url.contains("oracle")? "oracle.jdbc.driver.OracleDriver":"net.sourceforge.jtds.jdbc.Driver";
		
		try {
			
			if( (url == null || url.trim().length() == 0) && 
					(user == null || user.trim().length() == 0) && 
							(password == null || password.trim().length() == 0) ) {
				
				throw new Exception ("Database Settings Problem, Please check the settings.");
				
			}
			
			cspdConnectionPoolService = PoolFactory.newSingleConnection(driver, url, user, password);

		}catch(Exception e) {
			
			e.printStackTrace();
			
			errorAlert("Please revice connection settings", new Exception("The required conncetion not prepared properly"));

		}
		
	}
	
	public void buildOmniConnection() {
		
		String url = getOmnidocsProperties().getProperty("db.omniDBUrl");
		String user = getOmnidocsProperties().getProperty("db.omniDBUser");
		String password = getOmnidocsProperties().getProperty("db.omniDBPassword");
		String driver = url.contains("oracle")? "oracle.jdbc.driver.OracleDriver":"net.sourceforge.jtds.jdbc.Driver";
		
		try {
			
			if( (url == null || url.trim().length() == 0) && 
					(user == null || user.trim().length() == 0) && 
							(password == null || password.trim().length() == 0) ) {
				
				throw new Exception ("Database Settings Problem, Please check the settings.");
				
			}
			
			omniConnectionPoolService = PoolFactory.newSingleConnection(driver, url, user, password);

		}catch(Exception e) {
			
			e.printStackTrace();
			
			errorAlert("Please revice connection settings", new Exception("The required conncetion not prepared properly"));

		}

	}

	public TextArea getLoggerTextArea() {
		
		return loggerTabController.getLoggerTextArea();
		
	}
	
	public Properties getOmnidocsProperties() {
		
		return omnidocsSettingsController.getApplicationProperties();
		
	}

	public Properties getCSPDProperties() {
		
		return cSPDSettingsController.getApplicationProperties();
		
	}

	
	public OmniService getOmniService() throws Exception{
		
		try {
			String host = getOmnidocsProperties().getProperty("omnidocs.host");
			String port = getOmnidocsProperties().getProperty("omnidocs.port");
			String cabinet = getOmnidocsProperties().getProperty("omnidocs.cabinet");
			String username = getOmnidocsProperties().getProperty("omnidocs.omniUser");
			String password = getOmnidocsProperties().getProperty("omnidocs.omniUserPassword");
			
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
			
			throw new Exception (e.getMessage());

		}
		
		return omniService;
	}

	public void setOmniService(OmniService omniService) {
		this.omniService = omniService;
	}

	public PoolService<Connection> getSqlConnectionPoolService() throws Exception{
		
		String url = getCSPDProperties().getProperty("db.url");
		String user = getCSPDProperties().getProperty("db.user");
		String password = getCSPDProperties().getProperty("db.password");
		String driver = "net.sourceforge.jtds.jdbc.Driver";
		try {
			
			if( (url == null || url.trim().length() == 0) && 
					(user == null || user.trim().length() == 0) && 
							(password == null || password.trim().length() == 0) ) {
				
				throw new Exception ("Database Settings Problem, Please check the settings.");
				
			}
			
			cspdConnectionPoolService = PoolFactory.newSingleConnection(driver, url, user, password);

		}catch(Exception e) {
			
			e.printStackTrace();
			
			throw new Exception ("Unable to create a connection");

		}
		
		return cspdConnectionPoolService;
	}

	public void setSqlConnectionPoolService(PoolService<Connection> sqlConnectionPoolService) {
		this.cspdConnectionPoolService = sqlConnectionPoolService;
	}
	
	public PoolService<Connection> getOracleConnectionPoolService() throws Exception{
		
//		String url = "jdbc:oracle:thin:@192.168.60.93:1521:etech11g";//settingTabController.getApplicationProperties().getProperty("db.url");
//		String user = "jlgccab1";//settingTabController.getApplicationProperties().getProperty("db.user");
//		String password = "jlgccab1";//settingTabController.getApplicationProperties().getProperty("db.password");
		
		String url = getOmnidocsProperties().getProperty("db.omniDBUrl");
		String user = getOmnidocsProperties().getProperty("db.omniDBUser");
		String password = getOmnidocsProperties().getProperty("db.omniDBPassword");
		String driver = url.contains("oracle")? "oracle.jdbc.driver.OracleDriver":"net.sourceforge.jtds.jdbc.Driver";
		
		try {
			
			if( (url == null || url.trim().length() == 0) && 
					(user == null || user.trim().length() == 0) && 
							(password == null || password.trim().length() == 0) ) {
				
				throw new Exception ("Database Settings Problem, Please check the settings.");
				
			}
			
			omniConnectionPoolService = PoolFactory.newSingleConnection(driver, url, user, password);

		}catch(Exception e) {
			
			e.printStackTrace();
			
			throw new Exception ("Unable to create a connection");

		}
		
		return omniConnectionPoolService;
	}

	public void setOracleConnectionPoolService(PoolService<Connection> oracleConnectionPoolService) {
		this.omniConnectionPoolService = oracleConnectionPoolService;
	}

	@FXML
	private void handleQuitMenuItem(ActionEvent event){
		
		Platform.exit();
		
		System.exit(0);
		
	}
	
	@FXML
	private void handleAboutMenuItem(ActionEvent event){
		
		Image image = new Image(getClass().getResourceAsStream("/opex/fx/icon.jpg"), 70, 70, true, true);
		Label label = new Label("Civil Status and Passport Department", new ImageView(image));
		
		HBox hBox = new HBox();
		hBox.setAlignment(Pos.CENTER);
		hBox.getChildren().add(label);
		
		Stage aboutStage = new Stage();
		
		Scene scene = new Scene(hBox);
				
		aboutStage.setScene(scene);
		aboutStage.setHeight(200);
		aboutStage.setWidth(400);
		
		aboutStage.initModality(Modality.APPLICATION_MODAL);
		aboutStage.setTitle("About Me");
		aboutStage.setIconified(false);
		
		aboutStage.showAndWait();
	}
	
	
	public void showReport(List<OpexFolderReport> processReport) {

		try {
			final File report = File.createTempFile("report-", ".html");

			final FileWriter fileWriter = new FileWriter(report);
			
			reportTemplateStart = reportTemplateStart.replaceAll(":title", "Process Report").replaceAll(":date", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
			fileWriter.append(reportTemplateStart);
			
			processReport.stream().forEach(process -> {
				try {
					
					fileWriter.append("<tr class=\\\"test-result-step-row test-result-step-row-altone\\\">");
					fileWriter.append("<td class=\\\"test-result-step-command-cell\\\">" + process.getFolderName() + "</td>");
					fileWriter.append("<td class=\"test-result-step-description-cell\">" + process.getFailedReason() + " "  + (process.isDocumentLevel()?process.getFailedDocuments().size() + "/" + process.getTotalDocuments():"")+ "</td>");
					fileWriter.append("</tr>");

					process.getFailedDocuments().stream().forEach(doc -> {
						try {
							fileWriter.append("<tr class=\\\"test-result-step-row test-result-comment-row\\\">");
							fileWriter.append("<td class=\"test-result-describe-cell\" colspan=\"3\">");
							fileWriter.append(doc.getFailedReason() + "</td>");
							fileWriter.append("</tr>");
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					});
					
					
				} catch (IOException e) {
					
					e.printStackTrace();
				}
				
			});
			
			fileWriter.append(reportTemplateClose);
			
			fileWriter.close();
			
			
			Browser browser = new Browser(report.getName());
			
			VBox vBox = new VBox();
			//vBox.getChildren().add(browser);
			
			Button btn = new Button("Print");
			btn.setOnAction((ActionEvent e) -> {
			    PrinterJob job = PrinterJob.createPrinterJob();
			    if (job != null && job.showPrintDialog(btn.getScene().getWindow())) {
			    	browser.getWebEngine().print(job);
			        job.endJob();
			    }
			});
			
			Button btnSave = new Button("Save");
			btnSave.setOnAction((ActionEvent e) -> {
				FileOutputStream fileOutputStream = null;
			    try {
			    	
					Files.copy(new File(report.getAbsolutePath()).toPath(), new File("D:/temp1/" + report.getName()).toPath());
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}finally {
					try {
						fileOutputStream.close();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			});
			
			vBox.getChildren().add(browser);
			
			HBox hBox = new HBox();
			hBox.getChildren().add(btn);
			hBox.getChildren().add(btnSave);
			hBox.setAlignment(Pos.CENTER);
			
			vBox.getChildren().add(hBox);
			
			Scene scene = new Scene(vBox);
			
			Stage reportStage = new Stage();
			
			reportStage.setScene(scene);

			reportStage.initModality(Modality.APPLICATION_MODAL);
			reportStage.setTitle("Report Status");
			reportStage.setIconified(false);
			reportStage.setResizable(false);
			
			reportStage.showAndWait();
			
			
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		

		
		
		
	}
	
	@FXML
	private void handleOmnidocsSettingsMenuItem(ActionEvent event){
		
		Stage omnidocsSettingsStage = new Stage();
		
		Scene scene = new Scene(omnidocsSettingsPanel);
		
		omnidocsSettingsStage.setScene(scene);

		omnidocsSettingsStage.initModality(Modality.APPLICATION_MODAL);
		
		omnidocsSettingsStage.setTitle("Omnidocs Settings");
		
		omnidocsSettingsStage.setIconified(false);
		
		omnidocsSettingsStage.show();

	}
	
	@FXML
	private void handleCSPDSettingsMenuItem(ActionEvent event){
		
		Stage cspdSettingsStage = new Stage();
		
		Scene scene = new Scene(cspdSettingsPanel);
		
		cspdSettingsStage.setScene(scene);
		
		cspdSettingsStage.initModality(Modality.APPLICATION_MODAL);
		cspdSettingsStage.setTitle("Omnidocs Settings");
		cspdSettingsStage.setIconified(false);
		
		cspdSettingsStage.showAndWait();

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
		    alert.setContentText(e.getMessage());
		    alert.showAndWait();
		}

	}

	public void writeLog(String msg) {
		getLoggerTextArea().appendText("\n" + msg);
		/*Task task = new Task<Void>() {

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
		new Thread(task).start();*/
	}
	
	public int writeDBLog(Log log) {
		
		Connection connection = cspdConnectionPoolService.get();
		
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
			
		}else if(log instanceof ProcessLog) {
			
			Task task = new Task<Integer>() {

				@Override
				protected Integer call() throws Exception {
					
					int id = 0;
					try {
						ProcessLog processLog = (ProcessLog) log;
						String sql = "INSERT INTO ProcessLog( BatchIdentifier, NumberOfDocuments, MachineName, StartTime, EndTime) "
								   + "VALUES (?, ?, ?, ?, ?)";
						PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
						preparedStatement.setString(1, processLog.getBatchIdentifier());
						preparedStatement.setInt(2, processLog.getNumberOfDocuments());
						preparedStatement.setString(3, processLog.getMachineName());
						preparedStatement.setTimestamp(4, processLog.getStartTime());
						preparedStatement.setTimestamp(5, processLog.getEndTime());
	
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
		}else if(log instanceof ProcessDetailsLog) {
			
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


	public List<OpexFolderReport> getOpexFolderReport() {
		return opexFolderReport;
	}

	public void setOpexFolderReport(List<OpexFolderReport> opexFolderReport) {
		this.opexFolderReport = opexFolderReport;
	}


	class Browser extends Region {

		final WebView webView = new WebView();
		final WebEngine webEngine = webView.getEngine();

		public Browser() {}
		
		public Browser(String url) {
			
			getStyleClass().add("browser");

			URL urlHello = null;
			try {

				String path = "file:///" + System.getProperty("java.io.tmpdir")  + url;
				
				urlHello = URI.create(path.replace("\\", "/")).toURL();
				
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} // getClass().getResource(url);
			
			//urlHello = getClass().getResource(url);
			
			webEngine.load(urlHello.toExternalForm());
			
			getChildren().add(webView);

		}

		public WebEngine getWebEngine() {
			return webEngine;
		}

		private Node createSpacer() {
			Region spacer = new Region();
			HBox.setHgrow(spacer, Priority.ALWAYS);
			return spacer;
		}

		@Override
		protected void layoutChildren() {
			double w = getWidth();
			double h = getHeight();
			layoutInArea(webView, 0, 0, w, h, 0, HPos.CENTER, VPos.CENTER);
		}

		@Override
		protected double computePrefWidth(double height) {
			return 750;
		}

		@Override
		protected double computePrefHeight(double width) {
			return 500;
		}
	}
	
	String reportTemplateStart = "<html>\r\n" + 
			"    <head>\r\n" + 
			"        <title>\r\n" + 
			"            :title\r\n" + 
			"        </title>\r\n" + 
			"        <style type=\"text/css\">\r\n" + 
			"            .test-result-table {\r\n" + 
			"                border: 1px solid black;\r\n" + 
			"                width: 100%;\r\n" + 
			"            }\r\n" + 
			"            .test-result-table-header-cell {\r\n" + 
			"\r\n" + 
			"                border-bottom: 1px solid black;\r\n" + 
			"                background-color: silver;\r\n" + 
			"            }\r\n" + 
			"            .test-result-step-command-cell {\r\n" + 
			"\r\n" + 
			"                border-bottom: 1px solid gray;\r\n" + 
			"            }\r\n" + 
			"            .test-result-step-description-cell {\r\n" + 
			"\r\n" + 
			"                border-bottom: 1px solid gray;\r\n" + 
			"            }\r\n" + 
			"            .test-result-describe-cell {\r\n" + 
			"                background-color: tan;\r\n" + 
			"                font-style: italic;\r\n" + 
			"            }\r\n" + 
			"        </style>\r\n" + 
			"    </head>\r\n" + 
			"    <body dir=\"ltr\" >\r\n" + 
			"        <h2 style=\"position: relative; left:40%;\">:title</h2>\r\n" + 
			"\r\n" + 
			"        <table>\r\n" + 
			"            <tr>\r\n" + 
			"                <td>\r\n" + 
			"                    <h3>Date:</h3>\r\n" + 
			"                </td>\r\n" + 
			"                <td>\r\n" + 
			"                    <h3>:date</h3>\r\n" + 
			"                </td>\r\n" + 
			"            </tr>\r\n" + 
			"        </table>\r\n" + 
			"\r\n" + 
			"       <table class=\"test-result-table\" cellspacing=\"0\">\r\n" + 
			"            <thead>\r\n" + 
			"                <tr>\r\n" + 
			"                    <td class=\"test-result-table-header-cell\">\r\n" + 
			"                        Folder\r\n" + 
			"                    </td>\r\n" + 
			"                    <td class=\"test-result-table-header-cell\">\r\n" + 
			"                        Error Description\r\n" + 
			"                    </td>\r\n" + 
			"                </tr>\r\n" + 
			"            </thead>\r\n" + 
			"            <tbody>"; 
			
	
	String reportTemplateClose = "	            </tbody>\r\n" + 
									"	        </table>\r\n" + 
									"	   </body>\r\n" + 
									"	</html>";
}


