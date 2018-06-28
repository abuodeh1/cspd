package cspd;
	
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class Main extends Application {
	@Override
	public void start(Stage primaryStage) {
		try {
			
			Parent batchPanel = FXMLLoader.load(getClass().getResource("BatchPanel.fxml"));
			
			Scene scene = new Scene(batchPanel);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.show();
			
			
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		//launch(args);
		
		/*Properties props = new Properties();
		props.setProperty("omnidocs.host", "192.168.60.148");
		props.setProperty("omnidocs.port", "3333");
		props.setProperty("db.host", "192.168.60.93");
		props.setProperty("db.port", "1526");
		props.setProperty("db.sid", "etech11g");*/
		
		
		OutputStream outputStream = null;
		try {

			FileInputStream in = new FileInputStream("application.properties");
			Properties props = new Properties();
			props.load(in);
			in.close();
			
			File file = new File("application.properties");
			outputStream = new FileOutputStream(file);
//			props.store(outputStream, "CSPD Application Properties");
//			System.out.println("db.port " + props.get("db.port"));
//			
			props.setProperty("db.port", "4444");
			props.store(outputStream, null);
			System.out.println("db.port " + props.get("db.port"));
			
			outputStream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
}
