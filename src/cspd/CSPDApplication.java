package cspd;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import opex.controller.MainController;

public class CSPDApplication extends Application {
	
	MainController mainController;
	
	public static void main(String[] args) {

		launch(args);
		
	}
	
	@FXML public void initialize() {
		
	}
	
	@Override
	public void start(Stage primaryStage) {
		try {
			
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../opex/fx/MainContainer.fxml"));
			
			Parent batchPanel = fxmlLoader.load();
			
			mainController = (MainController)fxmlLoader.getController();
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

}
