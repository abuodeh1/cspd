package cspd;

import java.sql.Connection;

import etech.resource.pool.PoolService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class CSPDApplication extends Application {

	private static PoolService<Connection> connectionPoolService;
	
	public static void main(String[] args) {
		
		try {
		//	connectionPoolService = new ConnectionPool("", "", "");
			
//			new CSPDApplication().task();
			launch(args);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void start(Stage primaryStage) {
		try {
			
			Parent batchPanel = FXMLLoader.load(getClass().getResource("../opex/fx/OpexView.fxml"));
			
			Scene scene = new Scene(batchPanel);
			scene.getStylesheets().add(getClass().getResource("../opex/fx/application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.show();
			
			
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	

}
