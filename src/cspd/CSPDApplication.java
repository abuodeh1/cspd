package cspd;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class CSPDApplication extends Application {

	public static Scene PRIMARY_SCENE;
	
	public static void main(String[] args) {

		launch(args);
		
	}
	
	@Override
	public void start(Stage primaryStage) {
		try {
			
			Parent batchPanel = FXMLLoader.load(getClass().getResource("../opex/fx/OpexView.fxml"));
			
			Scene scene = new Scene(batchPanel);
			scene.getStylesheets().add(getClass().getResource("../opex/fx/application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.show();
			
			PRIMARY_SCENE = scene;
			
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
