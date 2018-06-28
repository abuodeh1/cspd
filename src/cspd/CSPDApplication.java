package cspd;

import java.sql.Connection;

import etech.resource.pool.PoolService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class CSPDApplication extends Application {
	
	public static void main(String[] args) {

		launch(args);
		
	}
	
	@Override
	public void start(Stage primaryStage) {
		try {
			
			Parent batchPanel = FXMLLoader.load(getClass().getResource("../opex/fx/MainContainer.fxml"));
			
			Scene scene = new Scene(batchPanel);
			scene.getStylesheets().add(getClass().getResource("../opex/fx/application.css").toExternalForm());
			
			primaryStage.getIcons().add(new Image("/opex/fx/icon.jpg"));
			primaryStage.setTitle("CSPD Utility");
			primaryStage.setScene(scene);
			primaryStage.show();
			
		} catch(Exception e) {
			e.printStackTrace();
		}

	}
	
	

	@Override
	public void stop() throws Exception {
		// TODO Auto-generated method stub
		super.stop();
	}
	

}
